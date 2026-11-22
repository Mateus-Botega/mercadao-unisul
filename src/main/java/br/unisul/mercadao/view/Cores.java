package br.unisul.mercadao.view;

import br.unisul.mercadao.model.StatusProduto;

import java.awt.Color;

final class Cores {

    static final Color POSITIVA = new Color(27, 120, 45);
    static final Color NEGATIVA = new Color(190, 30, 30);

    private static final Color LARANJA = new Color(239, 108, 0);
    private static final Color AZUL = new Color(25, 118, 210);
    private static final Color VERDE = new Color(46, 125, 50);

    private Cores() {
    }

    static Color doStatus(StatusProduto status) {
        return switch (status) {
            case PENDENTE -> LARANJA;
            case PARCIAL -> AZUL;
            case COMPRADO -> VERDE;
        };
    }
}
