package br.unisul.mercadao.view;

import br.unisul.mercadao.model.StatusProduto;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

final class Icones {

    static final int TAMANHO_BOTAO = 24;
    static final int TAMANHO_DIALOGO = 48;

    static final String APP = "app";
    static final String CADASTRAR = "cadastrar";
    static final String LISTAR = "listar";
    static final String ALFABETICA = "alfabetica";
    static final String CONSULTAR = "consultar";
    static final String CODIGO = "codigo";
    static final String SECAO = "secao";
    static final String EDITAR = "editar";
    static final String REMOVER = "remover";
    static final String COMPRA_PARCIAL = "compra_parcial";
    static final String COMPRA_TOTAL = "compra_total";
    static final String ADQUIRIDOS = "adquiridos";
    static final String PENDENTES = "pendentes";
    static final String RESUMO = "resumo";
    static final String EXEMPLOS = "exemplos";
    static final String SUCESSO = "sucesso";
    static final String ERRO = "erro";
    static final String AVISO = "aviso";
    static final String PERGUNTA = "pergunta";
    static final String STATUS_PARCIAL = "status_parcial";

    private static final String PASTA = "/icones/";
    private static final String EXTENSAO = ".png";
    private static final int TAMANHO_ARQUIVO = 48;

    private Icones() {
    }

    static Icon botao(String nome) {
        return carregar(nome, TAMANHO_BOTAO);
    }

    static Icon dialogo(String nome) {
        return carregar(nome, TAMANHO_DIALOGO);
    }

    static Icon carregar(String nome, int tamanho) {
        Image imagem = imagem(nome, tamanho);
        return new ImageIcon(imagem);
    }

    static Icon status(StatusProduto status, int tamanho) {
        String nome = nomeDoStatus(status);
        return carregar(nome, tamanho);
    }

    static Image imagem(String nome, int tamanho) {
        URL arquivo = Icones.class.getResource(PASTA + nome + EXTENSAO);
        if (arquivo == null) {
            return new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
        }
        Image original = new ImageIcon(arquivo).getImage();
        if (tamanho == TAMANHO_ARQUIVO) {
            return original;
        }
        return original.getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH);
    }

    private static String nomeDoStatus(StatusProduto status) {
        return switch (status) {
            case PENDENTE -> PENDENTES;
            case PARCIAL -> STATUS_PARCIAL;
            case COMPRADO -> ADQUIRIDOS;
        };
    }
}
