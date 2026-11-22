package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class Dialogos {

    private static final String CHAVE_TEXTO_OK = "OptionPane.okButtonText";
    private static final int TAMANHO_ICONE_JANELA = 64;

    private Dialogos() {
    }

    public static void configurar() {
        UIManager.put(CHAVE_TEXTO_OK, I18n.t("botao.ok"));
        UIManager.put("OptionPane.cancelButtonText", I18n.t("botao.cancelar"));
        UIManager.put("OptionPane.yesButtonText", I18n.t("botao.sim"));
        UIManager.put("OptionPane.noButtonText", I18n.t("botao.nao"));
        Image iconeDaJanela = Icones.imagem(Icones.APP, TAMANHO_ICONE_JANELA);
        JFrame janela = new JFrame();
        janela.setIconImage(iconeDaJanela);
        JOptionPane.setRootFrame(janela);
    }

    public static void sucesso(String mensagem) {
        mostrarMensagem("titulo.sucesso", mensagem, Icones.SUCESSO);
    }

    public static void erro(String mensagem) {
        mostrarMensagem("titulo.erro", mensagem, Icones.ERRO);
    }

    public static void aviso(String mensagem) {
        mostrarMensagem("titulo.aviso", mensagem, Icones.AVISO);
    }

    public static boolean confirmar(String mensagem) {
        String titulo = I18n.t("titulo.confirmacao");
        Icon icone = Icones.dialogo(Icones.PERGUNTA);
        int opcao = JOptionPane.showConfirmDialog(null, mensagem, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, icone);
        return opcao == JOptionPane.YES_OPTION;
    }

    static Object abrir(String titulo, JComponent conteudo, int tipoOpcao, Icon icone, Component foco) {
        JOptionPane painel = new JOptionPane(conteudo, JOptionPane.PLAIN_MESSAGE, tipoOpcao, icone);
        JDialog dialogo = painel.createDialog(null, titulo);
        dialogo.setResizable(true);
        if (foco != null) {
            dialogo.addWindowListener(focarAoAbrir(foco));
        }
        dialogo.setVisible(true);
        dialogo.dispose();
        return painel.getValue();
    }

    static Object abrirComBotao(String titulo, JComponent conteudo, String textoBotao, Icon icone) {
        UIManager.put(CHAVE_TEXTO_OK, textoBotao);
        try {
            return abrir(titulo, conteudo, JOptionPane.DEFAULT_OPTION, icone, null);
        } finally {
            UIManager.put(CHAVE_TEXTO_OK, I18n.t("botao.ok"));
        }
    }

    static boolean foiConfirmado(Object valor) {
        return valor instanceof Integer opcao && opcao == JOptionPane.OK_OPTION;
    }

    static void fechar(Component dentro, Object valor) {
        Component painel = SwingUtilities.getAncestorOfClass(JOptionPane.class, dentro);
        if (painel instanceof JOptionPane opcoes) {
            opcoes.setValue(valor);
        }
    }

    private static void mostrarMensagem(String chaveTitulo, String mensagem, String nomeIcone) {
        String titulo = I18n.t(chaveTitulo);
        Icon icone = Icones.dialogo(nomeIcone);
        JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.PLAIN_MESSAGE, icone);
    }

    private static WindowAdapter focarAoAbrir(Component foco) {
        return new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent evento) {
                foco.requestFocusInWindow();
            }
        };
    }
}
