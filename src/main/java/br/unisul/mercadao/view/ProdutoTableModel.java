package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.util.Formatador;

import javax.swing.table.AbstractTableModel;
import java.util.List;

final class ProdutoTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    static final int COLUNA_CODIGO = 0;
    static final int COLUNA_NOME = 1;
    static final int COLUNA_SECAO = 2;
    static final int COLUNA_DESEJADA = 3;
    static final int COLUNA_COMPRADA = 4;
    static final int COLUNA_PENDENTE = 5;
    static final int COLUNA_UNIDADE = 6;
    static final int COLUNA_PRECO = 7;
    static final int COLUNA_ESTIMADO = 8;
    static final int COLUNA_GASTO = 9;
    static final int COLUNA_STATUS = 10;
    private static final int TOTAL_COLUNAS = 11;

    private final transient List<Produto> produtos;

    ProdutoTableModel(List<Produto> produtos) {
        this.produtos = produtos;
    }

    Produto getProduto(int linha) {
        return produtos.get(linha);
    }

    @Override
    public int getRowCount() {
        return produtos.size();
    }

    @Override
    public int getColumnCount() {
        return TOTAL_COLUNAS;
    }

    @Override
    public Class<?> getColumnClass(int coluna) {
        return String.class;
    }

    @Override
    public String getColumnName(int coluna) {
        String chave = switch (coluna) {
            case COLUNA_CODIGO -> "coluna.codigo";
            case COLUNA_NOME -> "coluna.nome";
            case COLUNA_SECAO -> "coluna.secao";
            case COLUNA_DESEJADA -> "coluna.desejada";
            case COLUNA_COMPRADA -> "coluna.comprada";
            case COLUNA_PENDENTE -> "coluna.pendente";
            case COLUNA_UNIDADE -> "coluna.unidade";
            case COLUNA_PRECO -> "coluna.preco_estimado";
            case COLUNA_ESTIMADO -> "coluna.estimado_total";
            case COLUNA_GASTO -> "coluna.gasto";
            default -> "coluna.status";
        };
        return I18n.t(chave);
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Produto produto = produtos.get(linha);
        return switch (coluna) {
            case COLUNA_CODIGO -> String.valueOf(produto.getId());
            case COLUNA_NOME -> produto.getNome();
            case COLUNA_SECAO -> produto.getSecao();
            case COLUNA_DESEJADA -> Formatador.quantidade(produto.getQuantidadeDesejada());
            case COLUNA_COMPRADA -> Formatador.quantidade(produto.getQuantidadeComprada());
            case COLUNA_PENDENTE -> Formatador.quantidade(produto.getQuantidadePendente());
            case COLUNA_UNIDADE -> produto.getUnidade();
            case COLUNA_PRECO -> Formatador.moeda(produto.getPrecoEstimadoUnitario());
            case COLUNA_ESTIMADO -> Formatador.moeda(produto.getValorEstimadoTotal());
            case COLUNA_GASTO -> Formatador.moeda(produto.getValorGasto());
            default -> produto.getStatus().getDescricao();
        };
    }
}
