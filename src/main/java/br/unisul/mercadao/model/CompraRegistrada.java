package br.unisul.mercadao.model;

import java.math.BigDecimal;

public record CompraRegistrada(Produto produto, BigDecimal quantidade, BigDecimal precoPago) {
}
