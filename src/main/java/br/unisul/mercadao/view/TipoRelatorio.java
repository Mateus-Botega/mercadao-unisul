package br.unisul.mercadao.view;

public enum TipoRelatorio {
    CADASTRO("menu.listar_cadastro", Icones.LISTAR, "aviso.lista_vazia", "relatorio.totais_geral"),
    ALFABETICA("menu.listar_alfabetica", Icones.ALFABETICA, "aviso.lista_vazia", "relatorio.totais_geral"),
    ADQUIRIDOS("menu.adquiridos", Icones.ADQUIRIDOS, "aviso.sem_adquiridos", "relatorio.totais_adquiridos"),
    PENDENTES("menu.pendentes", Icones.PENDENTES, "aviso.sem_pendentes", "relatorio.totais_pendentes"),
    CONSULTA("consulta.resultado", Icones.CONSULTAR, "aviso.nada_encontrado", "relatorio.totais_geral");

    private final String chaveTitulo;
    private final String nomeIcone;
    private final String chaveSemItens;
    private final String chaveRodape;

    TipoRelatorio(String chaveTitulo, String nomeIcone, String chaveSemItens, String chaveRodape) {
        this.chaveTitulo = chaveTitulo;
        this.nomeIcone = nomeIcone;
        this.chaveSemItens = chaveSemItens;
        this.chaveRodape = chaveRodape;
    }

    String getChaveTitulo() {
        return chaveTitulo;
    }

    String getNomeIcone() {
        return nomeIcone;
    }

    String getChaveSemItens() {
        return chaveSemItens;
    }

    String getChaveRodape() {
        return chaveRodape;
    }
}
