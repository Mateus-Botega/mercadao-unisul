package br.unisul.mercadao.service;

import br.unisul.mercadao.exception.RegraNegocioException;
import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.ResumoFinanceiro;
import br.unisul.mercadao.model.StatusProduto;
import br.unisul.mercadao.structure.ArvoreBinariaBusca;
import br.unisul.mercadao.structure.ListaDuplamenteEncadeada;
import br.unisul.mercadao.util.Formatador;
import br.unisul.mercadao.util.Texto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ListaComprasService {

    private static final int TAMANHO_MAXIMO_NOME = 60;
    private static final int TAMANHO_MAXIMO_SECAO = 30;
    private static final int TAMANHO_MAXIMO_UNIDADE = 10;
    private static final int ESCALA_PERCENTUAL = 1;

    private record DadosValidados(String nome, String secao, String unidade,
                                  BigDecimal quantidadeDesejada, BigDecimal precoEstimado) {
    }

    private static class Acumulador {
        private int pendentes;
        private int parciais;
        private int comprados;
        private BigDecimal estimadoTotal = BigDecimal.ZERO.setScale(Produto.ESCALA_VALOR);
        private BigDecimal gasto = BigDecimal.ZERO.setScale(Produto.ESCALA_VALOR);
        private BigDecimal estimadoComprado = BigDecimal.ZERO.setScale(Produto.ESCALA_VALOR);
        private BigDecimal estimadoRestante = BigDecimal.ZERO.setScale(Produto.ESCALA_VALOR);

        private void somar(Produto produto) {
            contarStatus(produto.getStatus());
            estimadoTotal = estimadoTotal.add(produto.getValorEstimadoTotal());
            gasto = gasto.add(produto.getValorGasto());
            estimadoComprado = estimadoComprado.add(produto.getValorEstimadoComprado());
            estimadoRestante = estimadoRestante.add(produto.getValorEstimadoPendente());
        }

        private void contarStatus(StatusProduto status) {
            switch (status) {
                case PENDENTE -> pendentes++;
                case PARCIAL -> parciais++;
                case COMPRADO -> comprados++;
            }
        }
    }

    private final ListaDuplamenteEncadeada<Produto> produtos = new ListaDuplamenteEncadeada<>();
    private final ArvoreBinariaBusca<String, Produto> indicePorNome = new ArvoreBinariaBusca<>();
    private int proximoId = 1;

    public Produto cadastrar(String nome, String secao, String unidade,
                             BigDecimal quantidadeDesejada, BigDecimal precoEstimado) {
        DadosValidados dados = validar(nome, secao, unidade, quantidadeDesejada, precoEstimado);
        String chaveNome = Texto.normalizar(dados.nome());
        exigirNomeDisponivel(chaveNome, dados.nome());
        int id = proximoId;
        proximoId++;
        Produto produto = new Produto(id, dados.nome(), dados.secao(), dados.unidade(),
                dados.quantidadeDesejada(), dados.precoEstimado());
        produtos.adicionarNoFim(produto);
        indicePorNome.inserir(chaveNome, produto);
        return produto;
    }

    public Produto editar(int id, String nome, String secao, String unidade,
                          BigDecimal quantidadeDesejada, BigDecimal precoEstimado) {
        Produto produto = exigirPorId(id);
        DadosValidados dados = validar(nome, secao, unidade, quantidadeDesejada, precoEstimado);
        exigirQuantidadeNaoMenorQueComprada(produto, dados.quantidadeDesejada());
        atualizarIndicePorNome(produto, dados.nome());
        produto.setNome(dados.nome());
        produto.setSecao(dados.secao());
        produto.setUnidade(dados.unidade());
        produto.setQuantidadeDesejada(dados.quantidadeDesejada());
        produto.setPrecoEstimadoUnitario(dados.precoEstimado());
        return produto;
    }

    public void remover(int id) {
        Produto produto = exigirPorId(id);
        if (produto.possuiCompra()) {
            throw new RegraNegocioException(I18n.t("erro.remover_com_compra", produto.getNome()));
        }
        String chaveNome = Texto.normalizar(produto.getNome());
        produtos.removerPrimeiro(candidato -> candidato.getId() == id);
        indicePorNome.remover(chaveNome);
    }

    public Produto comprar(int id, BigDecimal quantidade, BigDecimal precoPago) {
        Produto produto = exigirPorId(id);
        if (produto.getStatus() == StatusProduto.COMPRADO) {
            throw new RegraNegocioException(I18n.t("erro.ja_comprado", produto.getNome()));
        }
        BigDecimal quantidadeValida = validarQuantidadeComprada(quantidade, produto);
        BigDecimal precoValido = validarPreco("campo.preco_pago", precoPago);
        produto.registrarCompra(quantidadeValida, precoValido);
        return produto;
    }

    public Produto comprarTotal(int id, BigDecimal precoPago) {
        Produto produto = exigirPorId(id);
        return comprar(id, produto.getQuantidadePendente(), precoPago);
    }

    public Produto buscarPorId(int id) {
        return produtos.buscarPrimeiro(candidato -> candidato.getId() == id);
    }

    public Produto buscarPorNome(String nome) {
        String chaveNome = Texto.normalizar(nome);
        return indicePorNome.buscar(chaveNome);
    }

    public ListaDuplamenteEncadeada<Produto> listarPorId(int id) {
        Produto produto = buscarPorId(id);
        return emLista(produto);
    }

    public ListaDuplamenteEncadeada<Produto> listarPorNome(String nome) {
        Produto produto = buscarPorNome(nome);
        return emLista(produto);
    }

    public ListaDuplamenteEncadeada<Produto> buscarPorTrecho(String trecho) {
        String alvo = Texto.normalizar(trecho);
        if (alvo.isEmpty()) {
            throw new RegraNegocioException(I18n.t("erro.trecho_vazio"));
        }
        return produtos.filtrar(candidato -> Texto.normalizar(candidato.getNome()).contains(alvo));
    }

    public ListaDuplamenteEncadeada<Produto> listarPorSecao(String secao) {
        String alvo = Texto.normalizar(secao);
        if (alvo.isEmpty()) {
            throw new RegraNegocioException(I18n.t("erro.secao_vazia"));
        }
        return produtos.filtrar(candidato -> Texto.normalizar(candidato.getSecao()).contains(alvo));
    }

    public ListaDuplamenteEncadeada<Produto> listarEmOrdemCadastro() {
        return produtos.copiar();
    }

    public ListaDuplamenteEncadeada<Produto> listarEmOrdemAlfabetica() {
        ListaDuplamenteEncadeada<Produto> resultado = new ListaDuplamenteEncadeada<>();
        indicePorNome.emOrdem(resultado::adicionarNoFim);
        return resultado;
    }

    public ListaDuplamenteEncadeada<Produto> listarComprados() {
        return produtos.filtrar(Produto::possuiCompra);
    }

    public ListaDuplamenteEncadeada<Produto> listarPendentes() {
        return produtos.filtrar(Produto::faltaComprar);
    }

    public int tamanho() {
        return produtos.tamanho();
    }

    public ResumoFinanceiro calcularResumo() {
        return calcularResumo(produtos);
    }

    public ResumoFinanceiro calcularResumo(ListaDuplamenteEncadeada<Produto> itens) {
        Acumulador acumulador = new Acumulador();
        itens.paraCada(acumulador::somar);
        int total = itens.tamanho();
        BigDecimal percentual = calcularPercentual(acumulador.comprados, total);
        return new ResumoFinanceiro(total, acumulador.pendentes, acumulador.parciais, acumulador.comprados,
                acumulador.estimadoTotal, acumulador.gasto, acumulador.estimadoComprado,
                acumulador.estimadoRestante, percentual);
    }

    private ListaDuplamenteEncadeada<Produto> emLista(Produto produto) {
        if (produto == null) {
            return new ListaDuplamenteEncadeada<>();
        }
        return ListaDuplamenteEncadeada.comUmElemento(produto);
    }

    private BigDecimal calcularPercentual(int comprados, int total) {
        if (total == 0) {
            return BigDecimal.ZERO.setScale(ESCALA_PERCENTUAL);
        }
        return BigDecimal.valueOf(comprados * 100L)
                .divide(BigDecimal.valueOf(total), ESCALA_PERCENTUAL, RoundingMode.HALF_UP);
    }

    private Produto exigirPorId(int id) {
        Produto produto = buscarPorId(id);
        if (produto == null) {
            throw new RegraNegocioException(I18n.t("erro.produto_inexistente", String.valueOf(id)));
        }
        return produto;
    }

    private void exigirNomeDisponivel(String chaveNome, String nome) {
        if (indicePorNome.contem(chaveNome)) {
            throw new RegraNegocioException(I18n.t("erro.nome_duplicado", nome));
        }
    }

    private void exigirQuantidadeNaoMenorQueComprada(Produto produto, BigDecimal novaQuantidadeDesejada) {
        BigDecimal comprada = produto.getQuantidadeComprada();
        if (novaQuantidadeDesejada.compareTo(comprada) < 0) {
            String desejadaFormatada = Formatador.quantidade(novaQuantidadeDesejada);
            String compradaFormatada = Formatador.quantidade(comprada);
            throw new RegraNegocioException(I18n.t("erro.qtd_desejada_menor_comprada",
                    desejadaFormatada, compradaFormatada));
        }
    }

    private void atualizarIndicePorNome(Produto produto, String novoNome) {
        String chaveAntiga = Texto.normalizar(produto.getNome());
        String chaveNova = Texto.normalizar(novoNome);
        if (chaveAntiga.equals(chaveNova)) {
            return;
        }
        exigirNomeDisponivel(chaveNova, novoNome);
        indicePorNome.remover(chaveAntiga);
        indicePorNome.inserir(chaveNova, produto);
    }

    private DadosValidados validar(String nome, String secao, String unidade,
                                   BigDecimal quantidadeDesejada, BigDecimal precoEstimado) {
        String nomeValido = validarNome(nome);
        String secaoValida = validarTexto("campo.secao", secao, TAMANHO_MAXIMO_SECAO);
        String unidadeValida = validarTexto("campo.unidade", unidade, TAMANHO_MAXIMO_UNIDADE);
        BigDecimal quantidadeValida = validarQuantidade(quantidadeDesejada,
                "erro.qtd_desejada_positiva", "erro.qtd_desejada_casas");
        BigDecimal precoValido = validarPreco("campo.preco_estimado", precoEstimado);
        return new DadosValidados(nomeValido, secaoValida, unidadeValida, quantidadeValida, precoValido);
    }

    private String validarNome(String nome) {
        String limpo = Texto.colapsarEspacos(nome);
        if (limpo.isEmpty()) {
            throw new RegraNegocioException(I18n.t("erro.nome_obrigatorio"));
        }
        if (limpo.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraNegocioException(I18n.t("erro.nome_tamanho", String.valueOf(TAMANHO_MAXIMO_NOME)));
        }
        return limpo;
    }

    private String validarTexto(String chaveCampo, String valor, int tamanhoMaximo) {
        String campo = I18n.t(chaveCampo);
        String limpo = Texto.colapsarEspacos(valor);
        if (limpo.isEmpty()) {
            throw new RegraNegocioException(I18n.t("erro.campo_obrigatorio", campo));
        }
        if (limpo.length() > tamanhoMaximo) {
            throw new RegraNegocioException(I18n.t("erro.campo_tamanho", campo, String.valueOf(tamanhoMaximo)));
        }
        return limpo;
    }

    private BigDecimal validarQuantidade(BigDecimal quantidade, String chaveErroPositiva, String chaveErroCasas) {
        if (quantidade == null || quantidade.signum() <= 0) {
            throw new RegraNegocioException(I18n.t(chaveErroPositiva));
        }
        if (quantidade.stripTrailingZeros().scale() > Produto.ESCALA_QUANTIDADE) {
            throw new RegraNegocioException(I18n.t(chaveErroCasas));
        }
        return quantidade.setScale(Produto.ESCALA_QUANTIDADE, RoundingMode.HALF_UP);
    }

    private BigDecimal validarQuantidadeComprada(BigDecimal quantidade, Produto produto) {
        BigDecimal quantidadeValida = validarQuantidade(quantidade,
                "erro.qtd_comprada_positiva", "erro.qtd_comprada_casas");
        BigDecimal pendente = produto.getQuantidadePendente();
        if (quantidadeValida.compareTo(pendente) > 0) {
            String pendenteFormatada = Formatador.quantidade(pendente);
            throw new RegraNegocioException(I18n.t("erro.qtd_excede", pendenteFormatada));
        }
        return quantidadeValida;
    }

    private BigDecimal validarPreco(String chaveCampo, BigDecimal preco) {
        String campo = I18n.t(chaveCampo);
        if (preco == null || preco.signum() < 0) {
            throw new RegraNegocioException(I18n.t("erro.preco_negativo", campo));
        }
        if (preco.stripTrailingZeros().scale() > Produto.ESCALA_VALOR) {
            throw new RegraNegocioException(I18n.t("erro.preco_casas", campo));
        }
        return preco.setScale(Produto.ESCALA_VALOR, RoundingMode.HALF_UP);
    }
}
