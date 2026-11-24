package br.unisul.mercadao.controller;

import br.unisul.mercadao.exception.RegraNegocioException;
import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.CompraRegistrada;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.ResumoFinanceiro;
import br.unisul.mercadao.service.DadosExemplo;
import br.unisul.mercadao.service.ListaComprasService;
import br.unisul.mercadao.util.Conversor;
import br.unisul.mercadao.util.Formatador;
import br.unisul.mercadao.view.ConsultaView;
import br.unisul.mercadao.view.Dialogos;
import br.unisul.mercadao.view.FormularioCompraView;
import br.unisul.mercadao.view.FormularioProdutoView;
import br.unisul.mercadao.view.MenuPrincipalView;
import br.unisul.mercadao.view.OpcaoConsulta;
import br.unisul.mercadao.view.OpcaoMenu;
import br.unisul.mercadao.view.RelatorioView;
import br.unisul.mercadao.view.ResumoView;
import br.unisul.mercadao.view.SeletorProdutoView;
import br.unisul.mercadao.view.TelaBoasVindas;
import br.unisul.mercadao.view.TextosStatus;
import br.unisul.mercadao.view.TipoRelatorio;

import java.math.BigDecimal;
import java.util.List;

public class MenuController {

    private final ListaComprasService listaCompras;

    public MenuController(ListaComprasService listaCompras) {
        this.listaCompras = listaCompras;
    }

    public void executar() {
        Dialogos.configurar();
        TelaBoasVindas.exibir();
        boolean encerrar = false;
        while (!encerrar) {
            encerrar = executarCicloDoMenu();
        }
    }

    private boolean executarCicloDoMenu() {
        ResumoFinanceiro resumo = listaCompras.calcularResumo();
        OpcaoMenu opcao = MenuPrincipalView.escolherOpcao(resumo);
        if (opcao == OpcaoMenu.SAIR) {
            String pergunta = I18n.t("confirmacao.sair");
            return Dialogos.confirmar(pergunta);
        }
        executarComTratamentoDeErro(opcao);
        return false;
    }

    private void executarComTratamentoDeErro(OpcaoMenu opcao) {
        try {
            executarOpcao(opcao);
        } catch (RegraNegocioException excecao) {
            Dialogos.erro(excecao.getMessage());
        }
    }

    private void executarOpcao(OpcaoMenu opcao) {
        switch (opcao) {
            case CADASTRAR -> cadastrarProduto();
            case LISTAR_CADASTRO -> listarEmOrdemCadastro();
            case LISTAR_ALFABETICA -> listarEmOrdemAlfabetica();
            case CONSULTAR -> consultarProduto();
            case EDITAR -> editarProduto();
            case REMOVER -> removerProduto();
            case COMPRA_PARCIAL -> registrarCompra(false);
            case COMPRA_TOTAL -> registrarCompra(true);
            case ADQUIRIDOS -> listarAdquiridos();
            case PENDENTES -> listarPendentes();
            case RESUMO -> exibirResumo();
            case RESTAURAR_PRODUTO -> restaurarProdutoRemovido();
            case DESFAZER_COMPRA -> desfazerUltimaCompra();
            case EXEMPLOS -> carregarExemplos();
        }
    }

    private void listarEmOrdemCadastro() {
        List<Produto> produtos = listaCompras.listarEmOrdemCadastro();
        exibirRelatorio(TipoRelatorio.CADASTRO, produtos);
    }

    private void listarEmOrdemAlfabetica() {
        List<Produto> produtos = listaCompras.listarEmOrdemAlfabetica();
        exibirRelatorio(TipoRelatorio.ALFABETICA, produtos);
    }

    private void listarAdquiridos() {
        List<Produto> produtos = listaCompras.listarComprados();
        exibirRelatorio(TipoRelatorio.ADQUIRIDOS, produtos);
    }

    private void listarPendentes() {
        List<Produto> produtos = listaCompras.listarPendentes();
        exibirRelatorio(TipoRelatorio.PENDENTES, produtos);
    }

    private void exibirResumo() {
        ResumoFinanceiro resumo = listaCompras.calcularResumo();
        ResumoView.exibir(resumo);
    }

    private void exibirRelatorio(TipoRelatorio tipo, List<Produto> produtos) {
        ResumoFinanceiro resumo = listaCompras.calcularResumo(produtos);
        RelatorioView.exibir(tipo, produtos, resumo);
    }

    private void restaurarProdutoRemovido() {
        Produto ultimo = listaCompras.espiarUltimoRemovido();
        String pergunta = I18n.t("confirmacao.restaurar_produto", ultimo.getNome());
        if (!Dialogos.confirmar(pergunta)) {
            return;
        }
        Produto restaurado = listaCompras.restaurarUltimoRemovido();
        String mensagem = I18n.t("sucesso.restaurado", restaurado.getNome(), String.valueOf(restaurado.getId()));
        Dialogos.sucesso(mensagem);
    }

    private void desfazerUltimaCompra() {
        CompraRegistrada ultima = listaCompras.espiarUltimaCompra();
        String quantidadeUltima = Formatador.quantidade(ultima.quantidade());
        String pergunta = I18n.t("confirmacao.desfazer_compra", ultima.produto().getNome(), quantidadeUltima);
        if (!Dialogos.confirmar(pergunta)) {
            return;
        }
        CompraRegistrada desfeita = listaCompras.desfazerUltimaCompra();
        String quantidade = Formatador.quantidade(desfeita.quantidade());
        String mensagem = I18n.t("sucesso.compra_desfeita", desfeita.produto().getNome(), quantidade);
        Dialogos.sucesso(mensagem);
    }

    private void carregarExemplos() {
        int inseridos = DadosExemplo.carregar(listaCompras);
        if (inseridos > 0) {
            String mensagem = I18n.t("sucesso.exemplos", String.valueOf(inseridos));
            Dialogos.sucesso(mensagem);
        } else {
            String mensagem = I18n.t("aviso.exemplos_existentes");
            Dialogos.aviso(mensagem);
        }
    }

    private void cadastrarProduto() {
        List<Produto> existentes = listaCompras.listarEmOrdemCadastro();
        FormularioProdutoView formulario = FormularioProdutoView.paraCadastro(existentes);
        boolean concluido = false;
        while (!concluido && formulario.exibir()) {
            concluido = cadastrarComDadosDoFormulario(formulario);
        }
    }

    private void editarProduto() {
        List<Produto> existentes = listaCompras.listarEmOrdemCadastro();
        Produto escolhido = escolherProduto(OpcaoMenu.EDITAR, "selecao.editar", "aviso.lista_vazia", existentes);
        if (escolhido == null) {
            return;
        }
        FormularioProdutoView formulario = FormularioProdutoView.paraEdicao(existentes, escolhido);
        boolean concluido = false;
        while (!concluido && formulario.exibir()) {
            concluido = editarComDadosDoFormulario(escolhido, formulario);
        }
    }

    private void removerProduto() {
        List<Produto> existentes = listaCompras.listarEmOrdemCadastro();
        Produto escolhido = escolherProduto(OpcaoMenu.REMOVER, "selecao.remover", "aviso.lista_vazia", existentes);
        if (escolhido == null) {
            return;
        }
        String pergunta = I18n.t("confirmacao.remover", escolhido.getNome());
        if (!Dialogos.confirmar(pergunta)) {
            return;
        }
        listaCompras.remover(escolhido.getId());
        String mensagem = I18n.t("sucesso.removido", escolhido.getNome());
        Dialogos.sucesso(mensagem);
    }

    private void registrarCompra(boolean compraTotal) {
        OpcaoMenu tipoDeCompra = compraTotal ? OpcaoMenu.COMPRA_TOTAL : OpcaoMenu.COMPRA_PARCIAL;
        List<Produto> pendentes = listaCompras.listarPendentes();
        Produto escolhido = escolherProduto(tipoDeCompra, "selecao.comprar", "aviso.sem_pendentes", pendentes);
        if (escolhido == null) {
            return;
        }
        FormularioCompraView formulario = FormularioCompraView.paraCompra(escolhido, compraTotal);
        boolean concluido = false;
        while (!concluido && formulario.exibir()) {
            concluido = concluirCompra(escolhido, formulario, compraTotal);
        }
    }

    private Produto escolherProduto(OpcaoMenu finalidade, String chaveInstrucao, String chaveSemItens,
                                    List<Produto> opcoes) {
        String instrucao = I18n.t(chaveInstrucao);
        String semItens = I18n.t(chaveSemItens);
        return SeletorProdutoView.escolher(finalidade, instrucao, semItens, opcoes);
    }

    private void consultarProduto() {
        OpcaoConsulta forma = ConsultaView.escolherForma();
        if (forma == OpcaoConsulta.VOLTAR) {
            return;
        }
        String texto = ConsultaView.pedirTexto(forma);
        if (texto != null) {
            exibirResultadoDaConsulta(forma, texto);
        }
    }

    private void exibirResultadoDaConsulta(OpcaoConsulta forma, String texto) {
        switch (forma) {
            case CODIGO -> consultarPorCodigo(texto);
            case NOME -> consultarPorNome(texto);
            case TRECHO -> consultarPorTrecho(texto);
            case SECAO -> consultarPorSecao(texto);
        }
    }

    private void consultarPorCodigo(String texto) {
        int codigo = lerCodigo(texto);
        List<Produto> encontrados = listaCompras.listarPorId(codigo);
        exibirRelatorio(TipoRelatorio.CONSULTA, encontrados);
    }

    private void consultarPorNome(String texto) {
        List<Produto> encontrados = listaCompras.listarPorNome(texto);
        exibirRelatorio(TipoRelatorio.CONSULTA, encontrados);
    }

    private void consultarPorTrecho(String texto) {
        List<Produto> encontrados = listaCompras.buscarPorTrecho(texto);
        exibirRelatorio(TipoRelatorio.CONSULTA, encontrados);
    }

    private void consultarPorSecao(String texto) {
        List<Produto> encontrados = listaCompras.listarPorSecao(texto);
        exibirRelatorio(TipoRelatorio.CONSULTA, encontrados);
    }

    private boolean cadastrarComDadosDoFormulario(FormularioProdutoView formulario) {
        try {
            String nome = formulario.getNome();
            String secao = formulario.getSecao();
            String unidade = formulario.getUnidade();
            BigDecimal quantidade = lerNumero(formulario.getQuantidade(), "campo.quantidade_desejada");
            BigDecimal preco = lerNumero(formulario.getPreco(), "campo.preco_estimado");
            Produto salvo = listaCompras.cadastrar(nome, secao, unidade, quantidade, preco);
            String mensagem = I18n.t("sucesso.cadastrado", salvo.getNome(), String.valueOf(salvo.getId()));
            Dialogos.sucesso(mensagem);
            return true;
        } catch (RegraNegocioException excecao) {
            return exibirErro(excecao);
        }
    }

    private boolean editarComDadosDoFormulario(Produto produto, FormularioProdutoView formulario) {
        try {
            String nome = formulario.getNome();
            String secao = formulario.getSecao();
            String unidade = formulario.getUnidade();
            BigDecimal quantidade = lerNumero(formulario.getQuantidade(), "campo.quantidade_desejada");
            BigDecimal preco = lerNumero(formulario.getPreco(), "campo.preco_estimado");
            Produto salvo = listaCompras.editar(produto.getId(), nome, secao, unidade, quantidade, preco);
            String mensagem = I18n.t("sucesso.editado", salvo.getNome());
            Dialogos.sucesso(mensagem);
            return true;
        } catch (RegraNegocioException excecao) {
            return exibirErro(excecao);
        }
    }

    private boolean concluirCompra(Produto produto, FormularioCompraView formulario, boolean compraTotal) {
        try {
            BigDecimal precoPago = lerNumero(formulario.getPreco(), "campo.preco_pago");
            Produto atualizado;
            if (compraTotal) {
                atualizado = listaCompras.comprarTotal(produto.getId(), precoPago);
            } else {
                BigDecimal quantidade = lerNumero(formulario.getQuantidade(), "campo.quantidade_comprada");
                atualizado = listaCompras.comprar(produto.getId(), quantidade, precoPago);
            }
            String situacao = TextosStatus.descricao(atualizado.getStatus());
            String mensagem = I18n.t("sucesso.compra", atualizado.getNome(), situacao);
            Dialogos.sucesso(mensagem);
            return true;
        } catch (RegraNegocioException excecao) {
            return exibirErro(excecao);
        }
    }

    private boolean exibirErro(RegraNegocioException excecao) {
        Dialogos.erro(excecao.getMessage());
        return false;
    }

    private int lerCodigo(String texto) {
        Integer codigo = Conversor.paraInteiro(texto);
        if (codigo == null) {
            throw new RegraNegocioException(I18n.t("erro.codigo_invalido"));
        }
        return codigo;
    }

    private BigDecimal lerNumero(String texto, String chaveCampo) {
        BigDecimal valor = Conversor.paraDecimal(texto);
        if (valor == null) {
            String campo = I18n.t(chaveCampo);
            throw new RegraNegocioException(I18n.t("erro.valor_invalido", campo));
        }
        return valor;
    }
}
