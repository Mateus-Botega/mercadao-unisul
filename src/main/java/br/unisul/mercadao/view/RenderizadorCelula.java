package br.unisul.mercadao.view;

import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.StatusProduto;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.awt.Font;

/** Desenha as células da tabela de produtos: alinha números à direita e destaca a coluna de status. */
final class RenderizadorCelula extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private final int tamanhoIconeStatus;

    RenderizadorCelula(int tamanhoIconeStatus) {
        this.tamanhoIconeStatus = tamanhoIconeStatus;
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionada,
                                                   boolean comFoco, int linha, int coluna) {
        super.getTableCellRendererComponent(tabela, valor, selecionada, comFoco, linha, coluna);
        setHorizontalAlignment(ehNumerica(coluna) ? SwingConstants.RIGHT : SwingConstants.LEFT);
        setIcon(null);
        setFont(tabela.getFont());
        if (!selecionada) {
            setForeground(tabela.getForeground());
        }
        if (coluna == ProdutoTableModel.COLUNA_STATUS) {
            destacarStatus(tabela, linha, selecionada);
        }
        return this;
    }

    private void destacarStatus(JTable tabela, int linha, boolean selecionada) {
        ProdutoTableModel modelo = (ProdutoTableModel) tabela.getModel();
        int linhaDoModelo = tabela.convertRowIndexToModel(linha);
        Produto produto = modelo.getProduto(linhaDoModelo);
        StatusProduto status = produto.getStatus();
        setIcon(Icones.status(status, tamanhoIconeStatus));
        setFont(tabela.getFont().deriveFont(Font.BOLD));
        if (!selecionada) {
            setForeground(Cores.doStatus(status));
        }
    }

    private static boolean ehNumerica(int coluna) {
        return coluna == ProdutoTableModel.COLUNA_CODIGO
                || (coluna >= ProdutoTableModel.COLUNA_DESEJADA && coluna <= ProdutoTableModel.COLUNA_PENDENTE)
                || (coluna >= ProdutoTableModel.COLUNA_PRECO && coluna <= ProdutoTableModel.COLUNA_GASTO);
    }
}
