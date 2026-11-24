package br.unisul.mercadao;

import br.unisul.mercadao.controller.MenuController;
import br.unisul.mercadao.service.ListaComprasService;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        aplicarVisualDoSistema();
        ListaComprasService listaCompras = AppConfig.criarServico();
        MenuController controller = new MenuController(listaCompras);
        controller.executar();
        System.exit(0);
    }

    private static void aplicarVisualDoSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
