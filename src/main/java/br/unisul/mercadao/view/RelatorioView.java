package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.ResumoFinanceiro;
import br.unisul.mercadao.util.Formatador;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.List;

public final class RelatorioView extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int ALTURA_DA_LINHA = 26;
    private static final Dimension TAMANHO_DA_TABELA = new Dimension(980, 320);
    private static final int TAMANHO_ICONE_STATUS = 16;

    private RelatorioView(List<Produto> produtos, TipoRelatorio tipo, ResumoFinanceiro resumo) {
        super(new BorderLayout(0, 8));
        JScrollPane rolagem = criarRolagem(produtos);
        JLabel rodape = criarRodape(tipo, resumo);
        add(rolagem, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    public static void exibir(TipoRelatorio tipo, List<Produto> produtos, ResumoFinanceiro resumo) {
        if (produtos.isEmpty()) {
            String mensagem = I18n.t(tipo.getChaveSemItens());
            Dialogos.aviso(mensagem);
            return;
        }
        RelatorioView relatorio = new RelatorioView(produtos, tipo, resumo);
        String titulo = I18n.t(tipo.getChaveTitulo());
        String textoBotao = I18n.t("botao.fechar");
        Icon icone = Icones.dialogo(tipo.getNomeIcone());
        Dialogos.abrirComBotao(titulo, relatorio, textoBotao, icone);
    }

    private static JScrollPane criarRolagem(List<Produto> produtos) {
        ProdutoTableModel modelo = new ProdutoTableModel(produtos);
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(ALTURA_DA_LINHA);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setDefaultRenderer(String.class, new RenderizadorCelula(TAMANHO_ICONE_STATUS));
        for (int coluna = 0; coluna < modelo.getColumnCount(); coluna++) {
            int largura = larguraDaColuna(coluna);
            TableColumn colunaDaTabela = tabela.getColumnModel().getColumn(coluna);
            colunaDaTabela.setPreferredWidth(largura);
        }
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setPreferredSize(TAMANHO_DA_TABELA);
        return rolagem;
    }

    private static JLabel criarRodape(TipoRelatorio tipo, ResumoFinanceiro resumo) {
        String texto = textoDoRodape(tipo, resumo);
        JLabel rodape = new JLabel(texto);
        rodape.setFont(rodape.getFont().deriveFont(Font.BOLD));
        rodape.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return rodape;
    }

    private static String textoDoRodape(TipoRelatorio tipo, ResumoFinanceiro resumo) {
        BigDecimal estimado = switch (tipo) {
            case ADQUIRIDOS -> resumo.getValorEstimadoComprado();
            case PENDENTES -> resumo.getValorEstimadoRestante();
            default -> resumo.getValorEstimadoTotal();
        };
        String itens = String.valueOf(resumo.getTotalItens());
        String estimadoFormatado = Formatador.moeda(estimado);
        String gastoFormatado = Formatador.moeda(resumo.getValorGasto());
        return I18n.t(tipo.getChaveRodape(), itens, estimadoFormatado, gastoFormatado);
    }

    private static int larguraDaColuna(int coluna) {
        return switch (coluna) {
            case ProdutoTableModel.COLUNA_CODIGO, ProdutoTableModel.COLUNA_UNIDADE -> 55;
            case ProdutoTableModel.COLUNA_NOME -> 190;
            case ProdutoTableModel.COLUNA_DESEJADA, ProdutoTableModel.COLUNA_COMPRADA,
                 ProdutoTableModel.COLUNA_PENDENTE -> 75;
            case ProdutoTableModel.COLUNA_PRECO -> 90;
            case ProdutoTableModel.COLUNA_ESTIMADO, ProdutoTableModel.COLUNA_GASTO -> 95;
            default -> 100;
        };
    }
}
