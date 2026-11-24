package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.StatusProduto;
import br.unisul.mercadao.util.Formatador;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.math.BigDecimal;

public final class FormularioCompraView extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int LARGURA_CAMPO = 10;
    private static final int TAMANHO_ICONE_SITUACAO = 16;
    private static final float TAMANHO_FONTE_TOTAL = 16f;

    private final JTextField campoQuantidade = new JTextField(LARGURA_CAMPO);
    private final JTextField campoPreco = new JTextField(LARGURA_CAMPO);
    private final JLabel rotuloTotal = new JLabel();
    private final OpcaoMenu tipoDeCompra;

    private FormularioCompraView(Produto produto, boolean compraTotal) {
        super(new BorderLayout(0, 10));
        tipoDeCompra = compraTotal ? OpcaoMenu.COMPRA_TOTAL : OpcaoMenu.COMPRA_PARCIAL;
        JPanel cartao = criarCartaoDoProduto(produto);
        JPanel campos = criarCampos(compraTotal);
        add(cartao, BorderLayout.NORTH);
        add(campos, BorderLayout.CENTER);
        String quantidadePendente = Formatador.quantidade(produto.getQuantidadePendente());
        String precoEstimado = Formatador.preco(produto.getPrecoEstimadoUnitario());
        campoQuantidade.setText(quantidadePendente);
        campoPreco.setText(precoEstimado);
        Componentes.aoDigitar(campoQuantidade, this::atualizarTotal);
        Componentes.aoDigitar(campoPreco, this::atualizarTotal);
        atualizarTotal();
    }

    public static FormularioCompraView paraCompra(Produto produto, boolean compraTotal) {
        return new FormularioCompraView(produto, compraTotal);
    }

    public boolean exibir() {
        String titulo = tipoDeCompra.getTitulo();
        Icon icone = Icones.dialogo(tipoDeCompra.getNomeIcone());
        Component foco = campoComFocoInicial();
        Object valor = Dialogos.abrir(titulo, this, JOptionPane.OK_CANCEL_OPTION, icone, foco);
        return Dialogos.foiConfirmado(valor);
    }

    public String getQuantidade() {
        return campoQuantidade.getText();
    }

    public String getPreco() {
        return campoPreco.getText();
    }

    private JPanel criarCartaoDoProduto(Produto produto) {
        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBorder(BorderFactory.createTitledBorder(produto.getNome()));
        JLabel secao = Componentes.destaque(produto.getSecao());
        JLabel desejada = destaqueDeQuantidade(produto.getQuantidadeDesejada(), produto);
        JLabel comprada = destaqueDeQuantidade(produto.getQuantidadeComprada(), produto);
        JLabel pendente = destaqueDeQuantidade(produto.getQuantidadePendente(), produto);
        String precoFormatado = Formatador.moeda(produto.getPrecoEstimadoUnitario());
        JLabel precoEstimado = Componentes.destaque(precoFormatado);
        JLabel situacao = criarRotuloDeSituacao(produto);
        Componentes.adicionarLinha(cartao, 0, I18n.t("compra.secao"), secao);
        Componentes.adicionarLinha(cartao, 1, I18n.t("compra.desejada"), desejada);
        Componentes.adicionarLinha(cartao, 2, I18n.t("compra.comprada"), comprada);
        Componentes.adicionarLinha(cartao, 3, I18n.t("compra.pendente"), pendente);
        Componentes.adicionarLinha(cartao, 4, I18n.t("compra.preco_estimado"), precoEstimado);
        Componentes.adicionarLinha(cartao, 5, I18n.t("compra.situacao"), situacao);
        return cartao;
    }

    private JPanel criarCampos(boolean compraTotal) {
        JPanel campos = new JPanel(new GridBagLayout());
        campoQuantidade.setEnabled(!compraTotal);
        String chaveDicaQuantidade = compraTotal ? "dica.compra_qtd_travada" : "dica.compra_qtd";
        campoQuantidade.setToolTipText(I18n.t(chaveDicaQuantidade));
        campoPreco.setToolTipText(I18n.t("dica.compra_preco"));
        rotuloTotal.setFont(rotuloTotal.getFont().deriveFont(Font.BOLD, TAMANHO_FONTE_TOTAL));
        Componentes.adicionarLinha(campos, 0, I18n.t("compra.quantidade"), campoQuantidade);
        Componentes.adicionarLinha(campos, 1, I18n.t("compra.preco_pago"), campoPreco);
        Componentes.adicionarLinha(campos, 2, I18n.t("compra.total"), rotuloTotal);
        return campos;
    }

    private static JLabel destaqueDeQuantidade(BigDecimal quantidade, Produto produto) {
        String quantidadeFormatada = Formatador.quantidade(quantidade);
        String texto = I18n.t("compra.qtd_unidade", quantidadeFormatada, produto.getUnidade());
        return Componentes.destaque(texto);
    }

    private static JLabel criarRotuloDeSituacao(Produto produto) {
        StatusProduto status = produto.getStatus();
        JLabel situacao = Componentes.destaque(TextosStatus.descricao(status));
        situacao.setIcon(Icones.status(status, TAMANHO_ICONE_SITUACAO));
        situacao.setForeground(Cores.doStatus(status));
        return situacao;
    }

    private void atualizarTotal() {
        String total = Componentes.totalDigitado(campoQuantidade, campoPreco);
        rotuloTotal.setText(total);
    }

    private Component campoComFocoInicial() {
        return campoQuantidade.isEnabled() ? campoQuantidade : campoPreco;
    }
}
