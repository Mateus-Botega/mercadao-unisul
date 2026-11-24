package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.StatusProduto;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

/** Desenha cada produto na lista de seleção: código, nome, status e ícone do status. */
final class RenderizadorProduto extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private final int tamanhoIcone;

    RenderizadorProduto(int tamanhoIcone) {
        this.tamanhoIcone = tamanhoIcone;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                  boolean selecionado, boolean comFoco) {
        super.getListCellRendererComponent(lista, valor, indice, selecionado, comFoco);
        if (valor instanceof Produto produto) {
            StatusProduto status = produto.getStatus();
            String texto = I18n.t("selecao.item", String.valueOf(produto.getId()), produto.getNome(),
                    TextosStatus.descricao(status));
            setText(texto);
            setIcon(Icones.status(status, tamanhoIcone));
        }
        return this;
    }
}
