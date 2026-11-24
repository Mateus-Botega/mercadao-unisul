package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;

public enum OpcaoMenu {
    CADASTRAR("menu.cadastrar", "dica.cadastrar", Icones.CADASTRAR),
    LISTAR_CADASTRO("menu.listar_cadastro", "dica.listar_cadastro", Icones.LISTAR),
    LISTAR_ALFABETICA("menu.listar_alfabetica", "dica.listar_alfabetica", Icones.ALFABETICA),
    CONSULTAR("menu.consultar", "dica.consultar", Icones.CONSULTAR),
    EDITAR("menu.editar", "dica.editar", Icones.EDITAR),
    REMOVER("menu.remover", "dica.remover", Icones.REMOVER),
    COMPRA_PARCIAL("menu.compra_parcial", "dica.compra_parcial", Icones.COMPRA_PARCIAL),
    COMPRA_TOTAL("menu.compra_total", "dica.compra_total", Icones.COMPRA_TOTAL),
    ADQUIRIDOS("menu.adquiridos", "dica.adquiridos", Icones.ADQUIRIDOS),
    PENDENTES("menu.pendentes", "dica.pendentes", Icones.PENDENTES),
    RESUMO("menu.resumo", "dica.resumo", Icones.RESUMO),
    RESTAURAR_PRODUTO("menu.restaurar_produto", "dica.restaurar_produto", Icones.REMOVER),
    DESFAZER_COMPRA("menu.desfazer_compra", "dica.desfazer_compra", Icones.AVISO),
    EXEMPLOS("menu.exemplos", "dica.exemplos", Icones.EXEMPLOS),
    SAIR("botao.sair", null, Icones.APP);

    private final String chaveTitulo;
    private final String chaveDica;
    private final String nomeIcone;

    OpcaoMenu(String chaveTitulo, String chaveDica, String nomeIcone) {
        this.chaveTitulo = chaveTitulo;
        this.chaveDica = chaveDica;
        this.nomeIcone = nomeIcone;
    }

    String getTitulo() {
        return I18n.t(chaveTitulo);
    }

    String getDica() {
        return I18n.t(chaveDica);
    }

    String getNomeIcone() {
        return nomeIcone;
    }
}
