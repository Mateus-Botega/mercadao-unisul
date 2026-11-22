package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;

public enum OpcaoConsulta {
    CODIGO("consulta.codigo", "dica.consulta_codigo", "consulta.pedir_codigo", Icones.CODIGO),
    NOME("consulta.nome", "dica.consulta_nome", "consulta.pedir_nome", Icones.CONSULTAR),
    TRECHO("consulta.trecho", "dica.consulta_trecho", "consulta.pedir_trecho", Icones.ALFABETICA),
    SECAO("consulta.secao", "dica.consulta_secao", "consulta.pedir_secao", Icones.SECAO),
    VOLTAR("botao.voltar", null, null, Icones.CONSULTAR);

    private final String chaveTitulo;
    private final String chaveDica;
    private final String chavePergunta;
    private final String nomeIcone;

    OpcaoConsulta(String chaveTitulo, String chaveDica, String chavePergunta, String nomeIcone) {
        this.chaveTitulo = chaveTitulo;
        this.chaveDica = chaveDica;
        this.chavePergunta = chavePergunta;
        this.nomeIcone = nomeIcone;
    }

    String getTitulo() {
        return I18n.t(chaveTitulo);
    }

    String getDica() {
        return I18n.t(chaveDica);
    }

    String getPergunta() {
        return I18n.t(chavePergunta);
    }

    String getNomeIcone() {
        return nomeIcone;
    }
}
