package br.unisul.mercadao.util;

import br.unisul.mercadao.i18n.I18n;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Formatador {

    private static final int ESCALA_MOEDA = 2;
    private static final int ESCALA_PORCENTAGEM = 1;
    private static final int DIGITOS_POR_GRUPO = 3;

    private Formatador() {
    }

    public static String moeda(BigDecimal valor) {
        BigDecimal arredondado = valor.setScale(ESCALA_MOEDA, RoundingMode.HALF_UP);
        String texto = arredondado.abs().toPlainString();
        int fimParteInteira = texto.length() - ESCALA_MOEDA - 1;
        String parteInteira = texto.substring(0, fimParteInteira);
        String centavos = texto.substring(fimParteInteira + 1);
        StringBuilder resultado = new StringBuilder();
        if (arredondado.signum() < 0) {
            resultado.append('-');
        }
        String inteiraAgrupada = agruparMilhares(parteInteira);
        resultado.append(I18n.t("formato.moeda.simbolo")).append(' ')
                .append(inteiraAgrupada)
                .append(I18n.t("formato.decimal")).append(centavos);
        return resultado.toString();
    }

    public static String quantidade(BigDecimal quantidade) {
        if (quantidade.signum() == 0) {
            return I18n.t("formato.zero");
        }
        String texto = quantidade.stripTrailingZeros().toPlainString();
        return comSeparadorDecimal(texto);
    }

    public static String preco(BigDecimal valor) {
        String texto = valor.setScale(ESCALA_MOEDA, RoundingMode.HALF_UP).toPlainString();
        return comSeparadorDecimal(texto);
    }

    public static String porcentagem(BigDecimal percentual) {
        String texto = percentual.setScale(ESCALA_PORCENTAGEM, RoundingMode.HALF_UP).toPlainString();
        String comVirgula = comSeparadorDecimal(texto);
        return I18n.t("formato.porcentagem", comVirgula);
    }

    private static String agruparMilhares(String parteInteira) {
        String separador = I18n.t("formato.milhar");
        StringBuilder invertido = new StringBuilder();
        for (int posicao = parteInteira.length() - 1; posicao >= 0; posicao--) {
            int digitosJaEscritos = parteInteira.length() - 1 - posicao;
            if (digitosJaEscritos > 0 && digitosJaEscritos % DIGITOS_POR_GRUPO == 0) {
                invertido.append(separador);
            }
            invertido.append(parteInteira.charAt(posicao));
        }
        return invertido.reverse().toString();
    }

    private static String comSeparadorDecimal(String numero) {
        return numero.replace(".", I18n.t("formato.decimal"));
    }
}
