package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.ResumoFinanceiro;
import br.unisul.mercadao.util.Formatador;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.math.BigDecimal;

public final class ResumoView extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final float TAMANHO_FONTE_DIFERENCA = 16f;
    private static final Dimension TAMANHO_DA_BARRA = new Dimension(380, 26);

    private ResumoView(ResumoFinanceiro resumo) {
        super(new BorderLayout(0, 10));
        JPanel valores = criarValores(resumo);
        JPanel base = criarBase(resumo);
        add(valores, BorderLayout.CENTER);
        add(base, BorderLayout.SOUTH);
    }

    public static void exibir(ResumoFinanceiro resumo) {
        ResumoView painel = new ResumoView(resumo);
        String titulo = OpcaoMenu.RESUMO.getTitulo();
        String textoBotao = I18n.t("botao.fechar");
        Icon icone = Icones.dialogo(Icones.RESUMO);
        Dialogos.abrirComBotao(titulo, painel, textoBotao, icone);
    }

    private static JPanel criarValores(ResumoFinanceiro resumo) {
        String itens = String.valueOf(resumo.getTotalItens());
        String situacao = I18n.t("resumo.situacao_valor", String.valueOf(resumo.getPendentes()),
                String.valueOf(resumo.getParciais()), String.valueOf(resumo.getComprados()));
        String estimadoTotal = Formatador.moeda(resumo.getValorEstimadoTotal());
        String gasto = Formatador.moeda(resumo.getValorGasto());
        String estimadoComprado = Formatador.moeda(resumo.getValorEstimadoComprado());
        String restante = Formatador.moeda(resumo.getValorEstimadoRestante());
        String projecao = Formatador.moeda(resumo.getProjecaoFinal());
        JPanel valores = new JPanel(new GridBagLayout());
        adicionarValor(valores, 0, "resumo.itens", itens);
        adicionarValor(valores, 1, "resumo.situacao", situacao);
        adicionarValor(valores, 2, "resumo.estimado_total", estimadoTotal);
        adicionarValor(valores, 3, "resumo.gasto", gasto);
        adicionarValor(valores, 4, "resumo.estimado_comprado", estimadoComprado);
        adicionarDiferenca(valores, 5, resumo.getDiferenca());
        adicionarValor(valores, 6, "resumo.restante", restante);
        adicionarValor(valores, 7, "resumo.projecao", projecao);
        return valores;
    }

    private static void adicionarValor(JPanel valores, int linha, String chaveRotulo, String texto) {
        String rotulo = I18n.t(chaveRotulo);
        JLabel valor = Componentes.destaque(texto);
        Componentes.adicionarLinha(valores, linha, rotulo, valor);
    }

    private static void adicionarDiferenca(JPanel valores, int linha, BigDecimal diferenca) {
        String chaveRotulo = "resumo.diferenca";
        Color cor = null;
        if (diferenca.signum() > 0) {
            chaveRotulo = "resumo.economia";
            cor = Cores.POSITIVA;
        } else if (diferenca.signum() < 0) {
            chaveRotulo = "resumo.excesso";
            cor = Cores.NEGATIVA;
        }
        String diferencaFormatada = Formatador.moeda(diferenca.abs());
        JLabel valor = Componentes.destaque(diferencaFormatada);
        valor.setFont(valor.getFont().deriveFont(Font.BOLD, TAMANHO_FONTE_DIFERENCA));
        if (cor != null) {
            valor.setForeground(cor);
        }
        String rotulo = I18n.t(chaveRotulo);
        Componentes.adicionarLinha(valores, linha, rotulo, valor);
    }

    private static JPanel criarBase(ResumoFinanceiro resumo) {
        JProgressBar barra = Componentes.barraDeProgresso(resumo.getPercentualConcluido(), "resumo.progresso");
        barra.setPreferredSize(TAMANHO_DA_BARRA);
        JPanel base = new JPanel(new BorderLayout(0, 4));
        base.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 4));
        JLabel titulo = new JLabel(I18n.t("resumo.itens_concluidos"));
        base.add(titulo, BorderLayout.NORTH);
        base.add(barra, BorderLayout.CENTER);
        return base;
    }
}
