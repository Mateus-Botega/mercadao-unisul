package br.unisul.mercadao.model;

import java.math.BigDecimal;

public class ResumoFinanceiro {

    private final int totalItens;
    private final int pendentes;
    private final int parciais;
    private final int comprados;
    private final BigDecimal valorEstimadoTotal;
    private final BigDecimal valorGasto;
    private final BigDecimal valorEstimadoComprado;
    private final BigDecimal valorEstimadoRestante;
    private final BigDecimal percentualConcluido;

    public ResumoFinanceiro(int totalItens, int pendentes, int parciais, int comprados,
                            BigDecimal valorEstimadoTotal, BigDecimal valorGasto,
                            BigDecimal valorEstimadoComprado, BigDecimal valorEstimadoRestante,
                            BigDecimal percentualConcluido) {
        this.totalItens = totalItens;
        this.pendentes = pendentes;
        this.parciais = parciais;
        this.comprados = comprados;
        this.valorEstimadoTotal = valorEstimadoTotal;
        this.valorGasto = valorGasto;
        this.valorEstimadoComprado = valorEstimadoComprado;
        this.valorEstimadoRestante = valorEstimadoRestante;
        this.percentualConcluido = percentualConcluido;
    }

    public int getTotalItens() {
        return totalItens;
    }

    public int getPendentes() {
        return pendentes;
    }

    public int getParciais() {
        return parciais;
    }

    public int getComprados() {
        return comprados;
    }

    public BigDecimal getValorEstimadoTotal() {
        return valorEstimadoTotal;
    }

    public BigDecimal getValorGasto() {
        return valorGasto;
    }

    public BigDecimal getValorEstimadoComprado() {
        return valorEstimadoComprado;
    }

    public BigDecimal getDiferenca() {
        return valorEstimadoComprado.subtract(valorGasto);
    }

    public BigDecimal getValorEstimadoRestante() {
        return valorEstimadoRestante;
    }

    public BigDecimal getProjecaoFinal() {
        return valorGasto.add(valorEstimadoRestante);
    }

    public BigDecimal getPercentualConcluido() {
        return percentualConcluido;
    }
}
