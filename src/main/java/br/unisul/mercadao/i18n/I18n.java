package br.unisul.mercadao.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class I18n {

    private static final String NOME_DO_PACOTE = "i18n.mensagens";
    private static final int TAMANHO_MARCADOR = 3;
    private static final char SEM_MARCADOR = '\0';
    private static final ResourceBundle PACOTE = carregarPacote();

    private I18n() {
    }

    public static boolean existe(String chave) {
        return PACOTE != null && chave != null && PACOTE.containsKey(chave);
    }

    public static String t(String chave) {
        if (!existe(chave)) {
            return "!" + chave + "!";
        }
        return PACOTE.getString(chave);
    }

    public static String t(String chave, String primeiro) {
        return substituir(t(chave), primeiro, null, null);
    }

    public static String t(String chave, String primeiro, String segundo) {
        return substituir(t(chave), primeiro, segundo, null);
    }

    public static String t(String chave, String primeiro, String segundo, String terceiro) {
        return substituir(t(chave), primeiro, segundo, terceiro);
    }

    private static ResourceBundle carregarPacote() {
        try {
            return ResourceBundle.getBundle(NOME_DO_PACOTE, Locale.of("pt", "BR"));
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private static String substituir(String modelo, String primeiro, String segundo, String terceiro) {
        StringBuilder resultado = new StringBuilder();
        int posicao = 0;
        while (posicao < modelo.length()) {
            char indice = indiceDoMarcador(modelo, posicao);
            if (indice == SEM_MARCADOR) {
                resultado.append(modelo.charAt(posicao));
                posicao++;
            } else {
                resultado.append(argumento(indice, primeiro, segundo, terceiro));
                posicao += TAMANHO_MARCADOR;
            }
        }
        return resultado.toString();
    }

    private static char indiceDoMarcador(String modelo, int posicao) {
        boolean formatoDeMarcador = modelo.charAt(posicao) == '{'
                && posicao + 2 < modelo.length()
                && modelo.charAt(posicao + 2) == '}';
        if (!formatoDeMarcador) {
            return SEM_MARCADOR;
        }
        char indice = modelo.charAt(posicao + 1);
        return indice >= '0' && indice <= '2' ? indice : SEM_MARCADOR;
    }

    private static String argumento(char indice, String primeiro, String segundo, String terceiro) {
        String valor = switch (indice) {
            case '0' -> primeiro;
            case '1' -> segundo;
            default -> terceiro;
        };
        return valor == null ? "" : valor;
    }
}
