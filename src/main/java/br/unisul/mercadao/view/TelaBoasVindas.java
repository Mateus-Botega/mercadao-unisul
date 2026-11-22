package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;

public final class TelaBoasVindas {

    private static final int TAMANHO_ICONE = 48;
    private static final float TAMANHO_FONTE_NOME = 24f;

    private TelaBoasVindas() {
    }

    public static void exibir() {
        Icon iconeDoApp = Icones.carregar(Icones.APP, TAMANHO_ICONE);
        JLabel logo = new JLabel(iconeDoApp, SwingConstants.CENTER);
        JLabel nome = criarRotuloDoNome();
        JLabel boasVindas = new JLabel(I18n.t("app.boas_vindas"), SwingConstants.CENTER);
        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.add(logo, BorderLayout.NORTH);
        painel.add(nome, BorderLayout.CENTER);
        painel.add(boasVindas, BorderLayout.SOUTH);
        String titulo = I18n.t("app.titulo");
        String textoBotao = I18n.t("botao.comecar");
        Dialogos.abrirComBotao(titulo, painel, textoBotao, null);
    }

    private static JLabel criarRotuloDoNome() {
        JLabel nome = new JLabel(I18n.t("app.nome"), SwingConstants.CENTER);
        nome.setFont(nome.getFont().deriveFont(Font.BOLD, TAMANHO_FONTE_NOME));
        return nome;
    }
}
