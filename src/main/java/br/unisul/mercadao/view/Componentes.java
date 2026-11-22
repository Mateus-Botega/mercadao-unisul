package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.util.Conversor;
import br.unisul.mercadao.util.Formatador;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.math.BigDecimal;

final class Componentes {

    private static final int LARGURA_BOTAO_MENU = 235;
    private static final int ALTURA_BOTAO_MENU = 46;

    private Componentes() {
    }

    static JButton botaoDeMenu(String texto, String dica, String nomeIcone, Object valor) {
        Icon icone = Icones.botao(nomeIcone);
        JButton botao = new JButton(texto, icone);
        botao.setToolTipText(dica);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setIconTextGap(10);
        botao.setMargin(new Insets(6, 10, 6, 10));
        botao.setPreferredSize(new Dimension(LARGURA_BOTAO_MENU, ALTURA_BOTAO_MENU));
        botao.addActionListener(evento -> Dialogos.fechar(botao, valor));
        return botao;
    }

    static JProgressBar barraDeProgresso(BigDecimal percentual, String chaveTexto) {
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue(percentual.intValue());
        barra.setStringPainted(true);
        String porcentagem = Formatador.porcentagem(percentual);
        barra.setString(I18n.t(chaveTexto, porcentagem));
        return barra;
    }

    static String totalDigitado(JTextComponent campoQuantidade, JTextComponent campoPreco) {
        BigDecimal quantidade = Conversor.paraDecimal(campoQuantidade.getText());
        BigDecimal preco = Conversor.paraDecimal(campoPreco.getText());
        if (quantidade == null || preco == null) {
            return Formatador.moeda(BigDecimal.ZERO);
        }
        BigDecimal total = Produto.calcularValor(quantidade, preco);
        return Formatador.moeda(total);
    }

    static void aoDigitar(JTextComponent campo, Runnable acao) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                acao.run();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                acao.run();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                acao.run();
            }
        });
    }

    static void adicionarLinha(JPanel painel, int linha, String rotulo, JComponent campo) {
        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.gridx = 0;
        restricoes.gridy = linha;
        restricoes.anchor = GridBagConstraints.WEST;
        restricoes.insets = new Insets(4, 4, 4, 10);
        painel.add(new JLabel(rotulo), restricoes);
        restricoes.gridx = 1;
        restricoes.weightx = 1.0;
        restricoes.fill = GridBagConstraints.HORIZONTAL;
        restricoes.insets = new Insets(4, 0, 4, 4);
        painel.add(campo, restricoes);
    }

    static JLabel destaque(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(rotulo.getFont().deriveFont(Font.BOLD));
        return rotulo;
    }
}
