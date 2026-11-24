package br.unisul.mercadao.validation;

import br.unisul.mercadao.exception.RegraNegocioException;
import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.DadosProduto;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.util.Formatador;
import br.unisul.mercadao.util.Texto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Valida e normaliza os dados digitados. Não altera nenhum produto. */
public class ProdutoValidador {

    private static final int TAMANHO_MAXIMO_NOME = 60;
    private static final int TAMANHO_MAXIMO_SECAO = 30;
    private static final int TAMANHO_MAXIMO_UNIDADE = 10;

    public DadosProduto validar(String nome, String secao, String unidade,
                                BigDecimal quantidadeDesejada, BigDecimal precoEstimado) {
        String nomeValido = validarNome(nome);
        String secaoValida = validarTexto("campo.secao", secao, TAMANHO_MAXIMO_SECAO);
        String unidadeValida = validarTexto("campo.unidade", unidade, TAMANHO_MAXIMO_UNIDADE);
        BigDecimal quantidadeValida = validarQuantidade(quantidadeDesejada,
                "erro.qtd_desejada_positiva", "erro.qtd_desejada_casas");
        BigDecimal precoValido = validarPreco("campo.preco_estimado", precoEstimado);
        return new DadosProduto(nomeValido, secaoValida, unidadeValida, quantidadeValida, precoValido);
    }

    public BigDecimal validarQuantidadeComprada(BigDecimal quantidade, Produto produto) {
        BigDecimal quantidadeValida = validarQuantidade(quantidade,
                "erro.qtd_comprada_positiva", "erro.qtd_comprada_casas");
        BigDecimal pendente = produto.getQuantidadePendente();
        if (quantidadeValida.compareTo(pendente) > 0) {
            String pendenteFormatada = Formatador.quantidade(pendente);
            throw new RegraNegocioException(I18n.t("erro.qtd_excede", pendenteFormatada));
        }
        return quantidadeValida;
    }

    public void exigirQuantidadeNaoMenorQueComprada(Produto produto, BigDecimal novaQuantidadeDesejada) {
        BigDecimal comprada = produto.getQuantidadeComprada();
        if (novaQuantidadeDesejada.compareTo(comprada) < 0) {
            String desejadaFormatada = Formatador.quantidade(novaQuantidadeDesejada);
            String compradaFormatada = Formatador.quantidade(comprada);
            throw new RegraNegocioException(I18n.t("erro.qtd_desejada_menor_comprada",
                    desejadaFormatada, compradaFormatada));
        }
    }

    public BigDecimal validarPreco(String chaveCampo, BigDecimal preco) {
        String campo = I18n.t(chaveCampo);
        if (preco == null || preco.signum() < 0) {
            throw new RegraNegocioException(I18n.t("erro.preco_negativo", campo));
        }
        if (preco.stripTrailingZeros().scale() > Produto.ESCALA_VALOR) {
            throw new RegraNegocioException(I18n.t("erro.preco_casas", campo));
        }
        return preco.setScale(Produto.ESCALA_VALOR, RoundingMode.HALF_UP);
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
}
