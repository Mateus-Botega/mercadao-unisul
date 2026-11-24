package br.unisul.mercadao.view;

import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.StatusProduto;
import br.unisul.mercadao.util.Texto;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public final class SeletorProdutoView extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int LINHAS_VISIVEIS = 10;
    private static final int LARGURA_FILTRO = 20;
    private static final int TAMANHO_ICONE_STATUS = 20;
    private static final Dimension TAMANHO_DA_LISTA = new Dimension(460, 250);

    private final DefaultListModel<Produto> modelo = new DefaultListModel<>();
    private final JList<Produto> lista = new JList<>(modelo);
    private final JTextField filtro = new JTextField(LARGURA_FILTRO);
    private final transient List<Produto> fonte;
    private final OpcaoMenu finalidade;

    private SeletorProdutoView(OpcaoMenu finalidade, String instrucao, List<Produto> fonte) {
        super(new BorderLayout(0, 8));
        this.finalidade = finalidade;
        this.fonte = fonte;
        configurarLista();
        configurarFiltro();
        add(criarTopo(instrucao), BorderLayout.NORTH);
        add(criarRolagem(), BorderLayout.CENTER);
        preencherModelo();
    }

    public static Produto escolher(OpcaoMenu finalidade, String instrucao, String semItens,
                                   List<Produto> fonte) {
        if (fonte.isEmpty()) {
            Dialogos.aviso(semItens);
            return null;
        }
        SeletorProdutoView seletor = new SeletorProdutoView(finalidade, instrucao, fonte);
        return seletor.exibir();
    }

    private Produto exibir() {
        while (true) {
            String titulo = finalidade.getTitulo();
            Icon icone = Icones.dialogo(finalidade.getNomeIcone());
            Object valor = Dialogos.abrir(titulo, this, JOptionPane.OK_CANCEL_OPTION, icone, filtro);
            if (!Dialogos.foiConfirmado(valor)) {
                return null;
            }
            Produto escolhido = lista.getSelectedValue();
            if (escolhido != null) {
                return escolhido;
            }
            Dialogos.aviso(I18n.t("selecao.nenhum"));
        }
    }

    private void configurarLista() {
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setCellRenderer(new RenderizadorProduto());
        lista.setVisibleRowCount(LINHAS_VISIVEIS);
        lista.setToolTipText(I18n.t("dica.lista_produtos"));
        lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (evento.getClickCount() == 2) {
                    confirmarSelecao(lista);
                }
            }
        });
    }

    private void configurarFiltro() {
        filtro.setToolTipText(I18n.t("dica.filtro"));
        Componentes.aoDigitar(filtro, this::preencherModelo);
        filtro.addActionListener(evento -> confirmarSelecao(filtro));
    }

    private void confirmarSelecao(Component origem) {
        if (lista.getSelectedValue() != null) {
            Dialogos.fechar(origem, Integer.valueOf(JOptionPane.OK_OPTION));
        }
    }

    private JPanel criarTopo(String instrucao) {
        JPanel topo = new JPanel(new BorderLayout(6, 4));
        topo.add(new JLabel(instrucao), BorderLayout.NORTH);
        topo.add(new JLabel(I18n.t("selecao.filtro")), BorderLayout.WEST);
        topo.add(filtro, BorderLayout.CENTER);
        return topo;
    }

    private JScrollPane criarRolagem() {
        JScrollPane rolagem = new JScrollPane(lista);
        rolagem.setPreferredSize(TAMANHO_DA_LISTA);
        return rolagem;
    }

    private void preencherModelo() {
        String alvo = Texto.normalizar(filtro.getText());
        modelo.clear();
        for (Produto produto : fonte) {
            if (combinaComFiltro(produto, alvo)) {
                modelo.addElement(produto);
            }
        }
        if (!modelo.isEmpty()) {
            lista.setSelectedIndex(0);
        }
    }

    private static boolean combinaComFiltro(Produto produto, String alvo) {
        return alvo.isEmpty()
                || Texto.normalizar(produto.getNome()).contains(alvo)
                || Texto.normalizar(produto.getSecao()).contains(alvo)
                || String.valueOf(produto.getId()).equals(alvo);
    }

    private static final class RenderizadorProduto extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                      boolean selecionado, boolean comFoco) {
            super.getListCellRendererComponent(lista, valor, indice, selecionado, comFoco);
            if (valor instanceof Produto produto) {
                StatusProduto status = produto.getStatus();
                String texto = I18n.t("selecao.item", String.valueOf(produto.getId()), produto.getNome(),
                        status.getDescricao());
                setText(texto);
                setIcon(Icones.status(status, TAMANHO_ICONE_STATUS));
            }
            return this;
        }
    }
}
