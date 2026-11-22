package br.unisul.mercadao.model;

import br.unisul.mercadao.i18n.I18n;

public enum StatusProduto {

    PENDENTE("status.pendente"),
    PARCIAL("status.parcial"),
    COMPRADO("status.comprado");

    private final String chaveDescricao;

    StatusProduto(String chaveDescricao) {
        this.chaveDescricao = chaveDescricao;
    }

    public String getDescricao() {
        return I18n.t(chaveDescricao);
    }
}
