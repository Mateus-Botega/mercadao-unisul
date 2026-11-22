package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.ResumoFinanceiro;
import br.unisul.mercadao.util.Formatador;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public final class MenuPrincipalView {

    private static final int COLUNAS_DA_GRADE = 3;
    private static final int ESPACO_ENTRE_BOTOES = 8;

    private MenuPrincipalView() {
    }

    public static OpcaoMenu escolherOpcao(ResumoFinanceiro resumo) {
        JPanel topo = criarTopo(resumo);
        JPanel grade = criarGrade();
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.add(topo, BorderLayout.NORTH);
        painel.add(grade, BorderLayout.CENTER);
        String titulo = I18n.t("app.titulo");
        String textoBotaoSair = OpcaoMenu.SAIR.getTitulo();
        Icon icone = Icones.dialogo(Icones.APP);
        Object valor = Dialogos.abrirComBotao(titulo, painel, textoBotaoSair, icone);
        return valor instanceof OpcaoMenu opcao ? opcao : OpcaoMenu.SAIR;
    }

    private static JPanel criarTopo(ResumoFinanceiro resumo) {
        String itens = String.valueOf(resumo.getTotalItens());
        String gasto = Formatador.moeda(resumo.getValorGasto());
        String restante = Formatador.moeda(resumo.getValorEstimadoRestante());
        JLabel miniResumo = new JLabel(I18n.t("menu.mini_resumo", itens, gasto, restante));
        miniResumo.setFont(miniResumo.getFont().deriveFont(Font.BOLD));
        JProgressBar barra = Componentes.barraDeProgresso(resumo.getPercentualConcluido(), "menu.progresso");
        barra.setToolTipText(I18n.t("dica.progresso"));
        JPanel topo = new JPanel(new BorderLayout(0, 6));
        topo.add(miniResumo, BorderLayout.NORTH);
        topo.add(barra, BorderLayout.CENTER);
        return topo;
    }

    private static JPanel criarGrade() {
        JPanel grade = new JPanel(new GridLayout(0, COLUNAS_DA_GRADE, ESPACO_ENTRE_BOTOES, ESPACO_ENTRE_BOTOES));
        adicionarBotao(grade, OpcaoMenu.CADASTRAR);
        adicionarBotao(grade, OpcaoMenu.LISTAR_CADASTRO);
        adicionarBotao(grade, OpcaoMenu.LISTAR_ALFABETICA);
        adicionarBotao(grade, OpcaoMenu.CONSULTAR);
        adicionarBotao(grade, OpcaoMenu.EDITAR);
        adicionarBotao(grade, OpcaoMenu.REMOVER);
        adicionarBotao(grade, OpcaoMenu.COMPRA_PARCIAL);
        adicionarBotao(grade, OpcaoMenu.COMPRA_TOTAL);
        adicionarBotao(grade, OpcaoMenu.ADQUIRIDOS);
        adicionarBotao(grade, OpcaoMenu.PENDENTES);
        adicionarBotao(grade, OpcaoMenu.RESUMO);
        adicionarBotao(grade, OpcaoMenu.EXEMPLOS);
        return grade;
    }

    private static void adicionarBotao(JPanel grade, OpcaoMenu opcao) {
        JButton botao = Componentes.botaoDeMenu(opcao.getTitulo(), opcao.getDica(), opcao.getNomeIcone(), opcao);
        grade.add(botao);
    }
}
