package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.StatusProduto;

/** Texto exibido para cada status de produto. */
public final class TextosStatus {

    private TextosStatus() {
    }

    public static String descricao(StatusProduto status) {
        return switch (status) {
            case PENDENTE -> I18n.t("status.pendente");
            case PARCIAL -> I18n.t("status.parcial");
            case COMPRADO -> I18n.t("status.comprado");
        };
    }
}
