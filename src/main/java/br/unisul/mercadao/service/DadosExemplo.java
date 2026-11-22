package br.unisul.mercadao.service;

import br.unisul.mercadao.i18n.I18n;

import java.math.BigDecimal;

public final class DadosExemplo {

    private DadosExemplo() {
    }

    public static int carregar(ListaComprasService listaCompras) {
        int inseridos = 0;
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.arroz", "secao.mercearia", "unidade.pct", "2", "27.90");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.feijao", "secao.mercearia", "unidade.kg", "3", "8.50");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.macarrao", "secao.mercearia", "unidade.pct", "4", "4.99");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.leite", "secao.laticinios", "unidade.l", "12", "5.49");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.queijo", "secao.laticinios", "unidade.kg", "0.5", "42.00");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.iogurte", "secao.laticinios", "unidade.un", "6", "3.80");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.banana", "secao.hortifruti", "unidade.kg", "1.5", "6.99");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.tomate", "secao.hortifruti", "unidade.kg", "2", "7.50");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.alface", "secao.hortifruti", "unidade.un", "2", "3.50");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.frango", "secao.acougue", "unidade.kg", "2.5", "17.90");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.carne", "secao.acougue", "unidade.kg", "1", "34.90");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.detergente", "secao.limpeza", "unidade.un", "5", "2.79");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.papel", "secao.higiene", "unidade.pct", "2", "19.90");
        inseridos += cadastrarSeAusente(listaCompras, "exemplo.cafe", "secao.mercearia", "unidade.pct", "2", "16.50");
        return inseridos;
    }

    private static int cadastrarSeAusente(ListaComprasService listaCompras, String chaveNome, String chaveSecao,
                                          String chaveUnidade, String quantidade, String preco) {
        String nome = I18n.t(chaveNome);
        if (listaCompras.buscarPorNome(nome) != null) {
            return 0;
        }
        String secao = I18n.t(chaveSecao);
        String unidade = I18n.t(chaveUnidade);
        BigDecimal quantidadeDesejada = new BigDecimal(quantidade);
        BigDecimal precoEstimado = new BigDecimal(preco);
        listaCompras.cadastrar(nome, secao, unidade, quantidadeDesejada, precoEstimado);
        return 1;
    }
}
