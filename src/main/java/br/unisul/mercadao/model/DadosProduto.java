package br.unisul.mercadao.model;

import java.math.BigDecimal;

public record DadosProduto(String nome, String secao, String unidade,
                           BigDecimal quantidadeDesejada, BigDecimal precoEstimado) {
}
