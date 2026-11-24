package br.unisul.mercadao.service;

import br.unisul.mercadao.exception.RegraNegocioException;
import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.CompraRegistrada;
import br.unisul.mercadao.model.DadosProduto;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.ResumoFinanceiro;
import br.unisul.mercadao.model.StatusProduto;
import br.unisul.mercadao.repository.HistoricoCompras;
import br.unisul.mercadao.repository.ProdutoRepository;
import br.unisul.mercadao.repository.ProdutosRemovidos;
import br.unisul.mercadao.util.Texto;
import br.unisul.mercadao.validation.ProdutoValidador;

import java.math.BigDecimal;
import java.util.List;

/** Casos de uso da lista de compras: cadastrar, editar, remover, comprar, desfazer e consultar. */
public class ListaComprasService {

    private static final int SEM_PRODUTO = -1;

    private final ProdutoRepository produtos;
    private final HistoricoCompras historicoCompras;
    private final ProdutosRemovidos produtosRemovidos;
    private final ProdutoValidador validador;
    private final ResumoService resumoService;

    public ListaComprasService(ProdutoRepository produtos, HistoricoCompras historicoCompras,
                               ProdutosRemovidos produtosRemovidos, ProdutoValidador validador,
                               ResumoService resumoService) {
        this.produtos = produtos;
        this.historicoCompras = historicoCompras;
        this.produtosRemovidos = produtosRemovidos;
        this.validador = validador;
        this.resumoService = resumoService;
    }

    public Produto cadastrar(String nome, String secao, String unidade,
                             BigDecimal quantidadeDesejada, BigDecimal precoEstimado) {
        DadosProduto dados = validador.validar(nome, secao, unidade, quantidadeDesejada, precoEstimado);
        exigirNomeDisponivel(dados.nome(), SEM_PRODUTO);
        Produto produto = new Produto(produtos.gerarId(), dados.nome(), dados.secao(), dados.unidade(),
                dados.quantidadeDesejada(), dados.precoEstimado());
        produtos.adicionar(produto);
        return produto;
    }

    public Produto editar(int id, String nome, String secao, String unidade,
                          BigDecimal quantidadeDesejada, BigDecimal precoEstimado) {
        Produto produto = exigirPorId(id);
        DadosProduto dados = validador.validar(nome, secao, unidade, quantidadeDesejada, precoEstimado);
        validador.exigirQuantidadeNaoMenorQueComprada(produto, dados.quantidadeDesejada());
        exigirNomeDisponivel(dados.nome(), id);
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
        produtos.remover(produto);
        produtosRemovidos.guardar(produto);
    }

    public Produto espiarUltimoRemovido() {
        Produto ultimo = produtosRemovidos.ultimo();
        if (ultimo == null) {
            throw new RegraNegocioException(I18n.t("erro.sem_remocao_para_desfazer"));
        }
        return ultimo;
    }

    public Produto restaurarUltimoRemovido() {
        Produto produto = espiarUltimoRemovido();
        exigirNomeDisponivel(produto.getNome(), SEM_PRODUTO);
        produtosRemovidos.retirarUltimo();
        produtos.inserirPorCodigo(produto);
        return produto;
    }

    public Produto comprar(int id, BigDecimal quantidade, BigDecimal precoPago) {
        Produto produto = exigirPorId(id);
        if (produto.getStatus() == StatusProduto.COMPRADO) {
            throw new RegraNegocioException(I18n.t("erro.ja_comprado", produto.getNome()));
        }
        BigDecimal quantidadeValida = validador.validarQuantidadeComprada(quantidade, produto);
        BigDecimal precoValido = validador.validarPreco("campo.preco_pago", precoPago);
        produto.registrarCompra(quantidadeValida, precoValido);
        historicoCompras.registrar(new CompraRegistrada(produto, quantidadeValida, precoValido));
        return produto;
    }

    public Produto comprarTotal(int id, BigDecimal precoPago) {
        Produto produto = exigirPorId(id);
        return comprar(id, produto.getQuantidadePendente(), precoPago);
    }

    public CompraRegistrada espiarUltimaCompra() {
        CompraRegistrada ultima = historicoCompras.ultima();
        if (ultima == null) {
            throw new RegraNegocioException(I18n.t("erro.sem_compra_para_desfazer"));
        }
        return ultima;
    }

    public CompraRegistrada desfazerUltimaCompra() {
        CompraRegistrada ultima = espiarUltimaCompra();
        historicoCompras.retirarUltima();
        ultima.produto().desfazerCompra(ultima.quantidade(), ultima.precoPago());
        return ultima;
    }

    public Produto buscarPorId(int id) {
        return produtos.buscarPorId(id);
    }

    public Produto buscarPorNome(String nome) {
        return produtos.buscarPorNome(nome);
    }

    public List<Produto> listarPorId(int id) {
        return emLista(buscarPorId(id));
    }

    public List<Produto> listarPorNome(String nome) {
        return emLista(buscarPorNome(nome));
    }

    public List<Produto> buscarPorTrecho(String trecho) {
        String alvo = Texto.normalizar(trecho);
        if (alvo.isEmpty()) {
            throw new RegraNegocioException(I18n.t("erro.trecho_vazio"));
        }
        return produtos.filtrar(produto -> produto.nomeContem(alvo));
    }

    public List<Produto> listarPorSecao(String secao) {
        String alvo = Texto.normalizar(secao);
        if (alvo.isEmpty()) {
            throw new RegraNegocioException(I18n.t("erro.secao_vazia"));
        }
        return produtos.filtrar(produto -> produto.secaoContem(alvo));
    }

    public List<Produto> listarEmOrdemCadastro() {
        return produtos.listarTodos();
    }

    public List<Produto> listarEmOrdemAlfabetica() {
        return produtos.listarOrdenadosPorNome();
    }

    public List<Produto> listarComprados() {
        return produtos.filtrar(Produto::possuiCompra);
    }

    public List<Produto> listarPendentes() {
        return produtos.filtrar(Produto::faltaComprar);
    }

    public int tamanho() {
        return produtos.tamanho();
    }

    public ResumoFinanceiro calcularResumo() {
        return resumoService.calcular(produtos.listarTodos());
    }

    public ResumoFinanceiro calcularResumo(List<Produto> itens) {
        return resumoService.calcular(itens);
    }

    private List<Produto> emLista(Produto produto) {
        return produto == null ? List.of() : List.of(produto);
    }

    private Produto exigirPorId(int id) {
        Produto produto = produtos.buscarPorId(id);
        if (produto == null) {
            throw new RegraNegocioException(I18n.t("erro.produto_inexistente", String.valueOf(id)));
        }
        return produto;
    }

    private void exigirNomeDisponivel(String nome, int idIgnorado) {
        if (produtos.existeNome(nome, idIgnorado)) {
            throw new RegraNegocioException(I18n.t("erro.nome_duplicado", nome));
        }
    }
}
