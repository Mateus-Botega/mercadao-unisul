package br.unisul.mercadao.service;

import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.ResumoFinanceiro;
import br.unisul.mercadao.model.StatusProduto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Calcula os totais e o percentual concluído de um conjunto de produtos. */
public class ResumoService {

    private static final int ESCALA_PERCENTUAL = 1;

    public ResumoFinanceiro calcular(List<Produto> itens) {
        int pendentes = 0;
        int parciais = 0;
        int comprados = 0;
        BigDecimal estimadoTotal = zeroDeValor();
        BigDecimal gasto = zeroDeValor();
        BigDecimal estimadoComprado = zeroDeValor();
        BigDecimal estimadoRestante = zeroDeValor();
        for (Produto produto : itens) {
            StatusProduto status = produto.getStatus();
            if (status == StatusProduto.PENDENTE) {
                pendentes++;
            } else if (status == StatusProduto.PARCIAL) {
                parciais++;
            } else {
                comprados++;
            }
            estimadoTotal = estimadoTotal.add(produto.getValorEstimadoTotal());
            gasto = gasto.add(produto.getValorGasto());
            estimadoComprado = estimadoComprado.add(produto.getValorEstimadoComprado());
            estimadoRestante = estimadoRestante.add(produto.getValorEstimadoPendente());
        }
        int total = itens.size();
        BigDecimal percentual = calcularPercentual(comprados, total);
        return new ResumoFinanceiro(total, pendentes, parciais, comprados,
                estimadoTotal, gasto, estimadoComprado, estimadoRestante, percentual);
    }

    private BigDecimal zeroDeValor() {
        return BigDecimal.ZERO.setScale(Produto.ESCALA_VALOR);
    }

    private BigDecimal calcularPercentual(int comprados, int total) {
        if (total == 0) {
            return BigDecimal.ZERO.setScale(ESCALA_PERCENTUAL);
        }
        return BigDecimal.valueOf(comprados * 100L)
                .divide(BigDecimal.valueOf(total), ESCALA_PERCENTUAL, RoundingMode.HALF_UP);
    }
}
