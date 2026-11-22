package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public final class ConsultaView {

    private static final int ESPACO_ENTRE_BOTOES = 8;
    private static final int LARGURA_CAMPO_TEXTO = 24;

    private ConsultaView() {
    }

    public static OpcaoConsulta escolherForma() {
        JLabel instrucao = new JLabel(I18n.t("consulta.instrucao"));
        JPanel grade = criarGrade();
        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.add(instrucao, BorderLayout.NORTH);
        painel.add(grade, BorderLayout.CENTER);
        painel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        String titulo = OpcaoMenu.CONSULTAR.getTitulo();
        String textoBotaoVoltar = OpcaoConsulta.VOLTAR.getTitulo();
        Icon icone = Icones.dialogo(Icones.CONSULTAR);
        Object valor = Dialogos.abrirComBotao(titulo, painel, textoBotaoVoltar, icone);
        return valor instanceof OpcaoConsulta forma ? forma : OpcaoConsulta.VOLTAR;
    }

    public static String pedirTexto(OpcaoConsulta forma) {
        JTextField campo = new JTextField(LARGURA_CAMPO_TEXTO);
        campo.addActionListener(evento -> confirmar(campo));
        JLabel pergunta = new JLabel(forma.getPergunta());
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.add(pergunta, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        String titulo = OpcaoMenu.CONSULTAR.getTitulo();
        Icon icone = Icones.dialogo(forma.getNomeIcone());
        Object valor = Dialogos.abrir(titulo, painel, JOptionPane.OK_CANCEL_OPTION, icone, campo);
        return Dialogos.foiConfirmado(valor) ? campo.getText() : null;
    }

    private static void confirmar(JTextField campo) {
        Dialogos.fechar(campo, Integer.valueOf(JOptionPane.OK_OPTION));
    }

    private static JPanel criarGrade() {
        JPanel grade = new JPanel(new GridLayout(2, 2, ESPACO_ENTRE_BOTOES, ESPACO_ENTRE_BOTOES));
        adicionarBotao(grade, OpcaoConsulta.CODIGO);
        adicionarBotao(grade, OpcaoConsulta.NOME);
        adicionarBotao(grade, OpcaoConsulta.TRECHO);
        adicionarBotao(grade, OpcaoConsulta.SECAO);
        return grade;
    }

    private static void adicionarBotao(JPanel grade, OpcaoConsulta forma) {
        JButton botao = Componentes.botaoDeMenu(forma.getTitulo(), forma.getDica(), forma.getNomeIcone(), forma);
        grade.add(botao);
    }
}
