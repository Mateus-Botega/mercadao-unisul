package br.unisul.mercadao.util;

import java.text.Normalizer;

public final class Texto {

    private Texto() {
    }

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String decomposto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        StringBuilder semAcentos = new StringBuilder();
        for (int posicao = 0; posicao < decomposto.length(); posicao++) {
            char caractere = decomposto.charAt(posicao);
            if (Character.getType(caractere) != Character.NON_SPACING_MARK) {
                semAcentos.append(Character.toLowerCase(caractere));
            }
        }
        return colapsarEspacos(semAcentos.toString());
    }

    public static String colapsarEspacos(String texto) {
        if (texto == null) {
            return "";
        }
        StringBuilder resultado = new StringBuilder();
        boolean ultimoFoiEspaco = true;
        for (int posicao = 0; posicao < texto.length(); posicao++) {
            char caractere = texto.charAt(posicao);
            if (!Character.isWhitespace(caractere)) {
                resultado.append(caractere);
                ultimoFoiEspaco = false;
            } else if (!ultimoFoiEspaco) {
                resultado.append(' ');
                ultimoFoiEspaco = true;
            }
        }
        removerEspacoFinal(resultado);
        return resultado.toString();
    }

    private static void removerEspacoFinal(StringBuilder texto) {
        int tamanho = texto.length();
        if (tamanho > 0 && texto.charAt(tamanho - 1) == ' ') {
            texto.setLength(tamanho - 1);
        }
    }
}
