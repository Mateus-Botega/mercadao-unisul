package br.unisul.mercadao.util;

import java.math.BigDecimal;

public final class Conversor {

    private static final int MAXIMO_DIGITOS_INTEIRO = 9;

    private Conversor() {
    }

    public static BigDecimal paraDecimal(String entrada) {
        String texto = entrada == null ? "" : entrada.trim().replace(',', '.');
        if (!contemApenasNumeroDecimal(texto)) {
            return null;
        }
        return new BigDecimal(texto);
    }

    public static Integer paraInteiro(String entrada) {
        String texto = entrada == null ? "" : entrada.trim();
        if (texto.isEmpty() || texto.length() > MAXIMO_DIGITOS_INTEIRO || !contemApenasDigitos(texto)) {
            return null;
        }
        return Integer.valueOf(texto);
    }

    private static boolean contemApenasNumeroDecimal(String texto) {
        int separadores = 0;
        int digitos = 0;
        for (int posicao = 0; posicao < texto.length(); posicao++) {
            char caractere = texto.charAt(posicao);
            if (caractere == '.') {
                separadores++;
            } else if (ehDigito(caractere)) {
                digitos++;
            } else {
                return false;
            }
        }
        return separadores <= 1 && digitos > 0;
    }

    private static boolean contemApenasDigitos(String texto) {
        for (int posicao = 0; posicao < texto.length(); posicao++) {
            if (!ehDigito(texto.charAt(posicao))) {
                return false;
            }
        }
        return true;
    }

    private static boolean ehDigito(char caractere) {
        return caractere >= '0' && caractere <= '9';
    }
}
