package br.unisul.mercadao.model;

import br.unisul.mercadao.util.Texto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Produto {

    public static final int ESCALA_QUANTIDADE = 3;
    public static final int ESCALA_VALOR = 2;

    private final int id;
    private String nome;
    private String secao;
    private String unidade;
    private BigDecimal quantidadeDesejada;
    private BigDecimal precoEstimadoUnitario;
    private BigDecimal quantidadeComprada = BigDecimal.ZERO.setScale(ESCALA_QUANTIDADE);
    private BigDecimal valorGasto = BigDecimal.ZERO.setScale(ESCALA_VALOR);

    public Produto(int id, String nome, String secao, String unidade,
                   BigDecimal quantidadeDesejada, BigDecimal precoEstimadoUnitario) {
        this.id = id;
        this.nome = nome;
        this.secao = secao;
        this.unidade = unidade;
        this.quantidadeDesejada = quantidadeDesejada;
        this.precoEstimadoUnitario = precoEstimadoUnitario;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSecao() {
        return secao;
    }

    public String getUnidade() {
        return unidade;
    }

    public BigDecimal getQuantidadeDesejada() {
        return quantidadeDesejada;
    }

    public BigDecimal getPrecoEstimadoUnitario() {
        return precoEstimadoUnitario;
    }

    public BigDecimal getQuantidadeComprada() {
        return quantidadeComprada;
    }

    public BigDecimal getValorGasto() {
        return valorGasto;
    }

    public BigDecimal getQuantidadePendente() {
        return quantidadeDesejada.subtract(quantidadeComprada);
    }

    public BigDecimal getValorEstimadoTotal() {
        return valorEstimado(quantidadeDesejada);
    }

    public BigDecimal getValorEstimadoComprado() {
        return valorEstimado(quantidadeComprada);
    }

    public BigDecimal getValorEstimadoPendente() {
        return valorEstimado(getQuantidadePendente());
    }

    public boolean possuiCompra() {
        return quantidadeComprada.signum() > 0;
    }

    public boolean faltaComprar() {
        return getStatus() != StatusProduto.COMPRADO;
    }

    public StatusProduto getStatus() {
        if (quantidadeComprada.signum() == 0) {
            return StatusProduto.PENDENTE;
        }
        if (quantidadeComprada.compareTo(quantidadeDesejada) < 0) {
            return StatusProduto.PARCIAL;
        }
        return StatusProduto.COMPRADO;
    }

    public void registrarCompra(BigDecimal quantidade, BigDecimal precoUnitarioPago) {
        quantidadeComprada = quantidadeComprada.add(quantidade);
        valorGasto = valorGasto.add(calcularValor(quantidade, precoUnitarioPago));
    }

    public void desfazerCompra(BigDecimal quantidade, BigDecimal precoUnitarioPago) {
        quantidadeComprada = quantidadeComprada.subtract(quantidade);
        valorGasto = valorGasto.subtract(calcularValor(quantidade, precoUnitarioPago));
    }

    public boolean nomeContem(String textoNormalizado) {
        return Texto.normalizar(nome).contains(textoNormalizado);
    }

    public boolean secaoContem(String textoNormalizado) {
        return Texto.normalizar(secao).contains(textoNormalizado);
    }

    public boolean combinaCom(String textoNormalizado) {
        return textoNormalizado.isEmpty()
                || nomeContem(textoNormalizado)
                || secaoContem(textoNormalizado)
                || String.valueOf(id).equals(textoNormalizado);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSecao(String secao) {
        this.secao = secao;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public void setQuantidadeDesejada(BigDecimal quantidadeDesejada) {
        this.quantidadeDesejada = quantidadeDesejada;
    }

    public void setPrecoEstimadoUnitario(BigDecimal precoEstimadoUnitario) {
        this.precoEstimadoUnitario = precoEstimadoUnitario;
    }

    private BigDecimal valorEstimado(BigDecimal quantidade) {
        return calcularValor(quantidade, precoEstimadoUnitario);
    }

    public static BigDecimal calcularValor(BigDecimal quantidade, BigDecimal precoUnitario) {
        return quantidade.multiply(precoUnitario).setScale(ESCALA_VALOR, RoundingMode.HALF_UP);
    }
}
