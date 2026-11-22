package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.structure.ListaDuplamenteEncadeada;
import br.unisul.mercadao.util.Formatador;
import br.unisul.mercadao.util.Texto;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.math.BigDecimal;

public final class FormularioProdutoView extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int LARGURA_CAMPO_NOME = 26;
    private static final int LARGURA_CAMPO_NUMERO = 10;
    private static final float TAMANHO_FONTE_TOTAL = 16f;

    private final JTextField campoNome = new JTextField(LARGURA_CAMPO_NOME);
    private final JComboBox<String> campoSecao;
    private final JComboBox<String> campoUnidade;
    private final JTextField campoQuantidade = new JTextField(LARGURA_CAMPO_NUMERO);
    private final JTextField campoPreco = new JTextField(LARGURA_CAMPO_NUMERO);
    private final JLabel rotuloTotal = new JLabel();
    private final String chaveTitulo;
    private final String nomeIcone;

    private FormularioProdutoView(ListaDuplamenteEncadeada<Produto> produtosExistentes,
                                  String chaveTitulo, String nomeIcone) {
        super(new GridBagLayout());
        this.chaveTitulo = chaveTitulo;
        this.nomeIcone = nomeIcone;
        campoSecao = criarCombo(criarModeloDeSecoes(produtosExistentes), "dica.form_secao");
        campoUnidade = criarCombo(criarModeloDeUnidades(), "dica.form_unidade");
        montarLayout();
        configurarDicas();
        Componentes.aoDigitar(campoQuantidade, this::atualizarTotal);
        Componentes.aoDigitar(campoPreco, this::atualizarTotal);
    }

    public static FormularioProdutoView paraCadastro(ListaDuplamenteEncadeada<Produto> produtosExistentes) {
        FormularioProdutoView formulario = new FormularioProdutoView(produtosExistentes,
                "form.titulo_cadastrar", Icones.CADASTRAR);
        String unidadePadrao = I18n.t("unidade.un");
        String quantidadePadrao = Formatador.quantidade(BigDecimal.ONE);
        String precoPadrao = Formatador.preco(BigDecimal.ZERO);
        formulario.preencher("", "", unidadePadrao, quantidadePadrao, precoPadrao);
        return formulario;
    }

    public static FormularioProdutoView paraEdicao(ListaDuplamenteEncadeada<Produto> produtosExistentes,
                                                   Produto produto) {
        FormularioProdutoView formulario = new FormularioProdutoView(produtosExistentes,
                "form.titulo_editar", Icones.EDITAR);
        String quantidade = Formatador.quantidade(produto.getQuantidadeDesejada());
        String preco = Formatador.preco(produto.getPrecoEstimadoUnitario());
        formulario.preencher(produto.getNome(), produto.getSecao(), produto.getUnidade(), quantidade, preco);
        return formulario;
    }

    public boolean exibir() {
        String titulo = I18n.t(chaveTitulo);
        Icon icone = Icones.dialogo(nomeIcone);
        Object valor = Dialogos.abrir(titulo, this, JOptionPane.OK_CANCEL_OPTION, icone, campoNome);
        return Dialogos.foiConfirmado(valor);
    }

    public String getNome() {
        return campoNome.getText();
    }

    public String getSecao() {
        return textoDoCombo(campoSecao);
    }

    public String getUnidade() {
        return textoDoCombo(campoUnidade);
    }

    public String getQuantidade() {
        return campoQuantidade.getText();
    }

    public String getPreco() {
        return campoPreco.getText();
    }

    private void preencher(String nome, String secao, String unidade, String quantidade, String preco) {
        campoNome.setText(nome);
        campoSecao.setSelectedItem(secao);
        campoUnidade.setSelectedItem(unidade);
        campoQuantidade.setText(quantidade);
        campoPreco.setText(preco);
        atualizarTotal();
    }

    private void montarLayout() {
        rotuloTotal.setFont(rotuloTotal.getFont().deriveFont(Font.BOLD, TAMANHO_FONTE_TOTAL));
        Componentes.adicionarLinha(this, 0, I18n.t("form.nome"), campoNome);
        Componentes.adicionarLinha(this, 1, I18n.t("form.secao"), campoSecao);
        Componentes.adicionarLinha(this, 2, I18n.t("form.unidade"), campoUnidade);
        Componentes.adicionarLinha(this, 3, I18n.t("form.quantidade"), campoQuantidade);
        Componentes.adicionarLinha(this, 4, I18n.t("form.preco"), campoPreco);
        Componentes.adicionarLinha(this, 5, I18n.t("form.total_estimado"), rotuloTotal);
    }

    private void configurarDicas() {
        campoNome.setToolTipText(I18n.t("dica.form_nome"));
        campoQuantidade.setToolTipText(I18n.t("dica.form_quantidade"));
        campoPreco.setToolTipText(I18n.t("dica.form_preco"));
    }

    private void atualizarTotal() {
        String total = Componentes.totalDigitado(campoQuantidade, campoPreco);
        rotuloTotal.setText(total);
    }

    private static JComboBox<String> criarCombo(DefaultComboBoxModel<String> modelo, String chaveDica) {
        JComboBox<String> combo = new JComboBox<>(modelo);
        combo.setEditable(true);
        combo.setToolTipText(I18n.t(chaveDica));
        return combo;
    }

    private static DefaultComboBoxModel<String> criarModeloDeSecoes(ListaDuplamenteEncadeada<Produto> produtos) {
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        produtos.paraCada(produto -> adicionarSeNovo(modelo, produto.getSecao()));
        adicionarChave(modelo, "secao.mercearia");
        adicionarChave(modelo, "secao.hortifruti");
        adicionarChave(modelo, "secao.laticinios");
        adicionarChave(modelo, "secao.acougue");
        adicionarChave(modelo, "secao.padaria");
        adicionarChave(modelo, "secao.bebidas");
        adicionarChave(modelo, "secao.limpeza");
        adicionarChave(modelo, "secao.higiene");
        return modelo;
    }

    private static DefaultComboBoxModel<String> criarModeloDeUnidades() {
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        adicionarChave(modelo, "unidade.un");
        adicionarChave(modelo, "unidade.kg");
        adicionarChave(modelo, "unidade.g");
        adicionarChave(modelo, "unidade.l");
        adicionarChave(modelo, "unidade.pct");
        adicionarChave(modelo, "unidade.cx");
        return modelo;
    }

    private static void adicionarChave(DefaultComboBoxModel<String> modelo, String chave) {
        String texto = I18n.t(chave);
        adicionarSeNovo(modelo, texto);
    }

    private static void adicionarSeNovo(DefaultComboBoxModel<String> modelo, String texto) {
        String chave = Texto.normalizar(texto);
        for (int posicao = 0; posicao < modelo.getSize(); posicao++) {
            if (Texto.normalizar(modelo.getElementAt(posicao)).equals(chave)) {
                return;
            }
        }
        modelo.addElement(texto);
    }

    private static String textoDoCombo(JComboBox<String> combo) {
        return ((JTextComponent) combo.getEditor().getEditorComponent()).getText();
    }
}
