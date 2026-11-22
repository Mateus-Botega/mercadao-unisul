package br.unisul.mercadao;

import br.unisul.mercadao.exception.RegraNegocioException;
import br.unisul.mercadao.i18n.I18n;
import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.model.ResumoFinanceiro;
import br.unisul.mercadao.model.StatusProduto;
import br.unisul.mercadao.service.DadosExemplo;
import br.unisul.mercadao.service.ListaComprasService;
import br.unisul.mercadao.structure.ArvoreBinariaBusca;
import br.unisul.mercadao.structure.ListaDuplamenteEncadeada;
import br.unisul.mercadao.util.Conversor;
import br.unisul.mercadao.util.Formatador;

import java.math.BigDecimal;

public class TesteSistema {

    private interface Bloco {
        void executar();
    }

    private static int totalDeVerificacoes = 0;
    private static int totalDeFalhas = 0;

    public static void main(String[] args) {
        testarLista();
        testarArvore();
        testarCadastro();
        testarCompras();
        testarValores();
        testarEdicao();
        testarRemocao();
        testarBuscas();
        testarConsultasUnicas();
        testarObter();
        testarI18n();
        testarConversor();

        System.out.println();
        System.out.println("Total de verificações: " + totalDeVerificacoes + " | Falhas: " + totalDeFalhas);
        if (totalDeFalhas > 0) {
            System.exit(1);
        }
        System.out.println("TODOS OS TESTES PASSARAM.");
    }

    private static void verificar(String descricao, boolean condicao) {
        totalDeVerificacoes++;
        if (condicao) {
            System.out.println("OK      - " + descricao);
        } else {
            totalDeFalhas++;
            System.out.println("FALHOU  - " + descricao);
        }
    }

    private static void verificarRejeita(String descricao, Bloco bloco) {
        boolean lancouExcecao = false;
        try {
            bloco.executar();
        } catch (RegraNegocioException excecao) {
            lancouExcecao = true;
        }
        verificar(descricao, lancouExcecao);
    }

    private static BigDecimal decimal(String valor) {
        return new BigDecimal(valor);
    }

    private static boolean igual(BigDecimal valor, String esperado) {
        return valor.compareTo(new BigDecimal(esperado)) == 0;
    }

    private static Produto cadastrarProduto(ListaComprasService listaCompras, String nome, String secao,
                                            String quantidade, String preco) {
        return listaCompras.cadastrar(nome, secao, "un", decimal(quantidade), decimal(preco));
    }

    private static void testarLista() {
        System.out.println("== Lista duplamente encadeada ==");
        ListaDuplamenteEncadeada<Integer> lista = new ListaDuplamenteEncadeada<>();
        verificar("lista nova está vazia", lista.estaVazia() && lista.tamanho() == 0);
        for (int numero = 1; numero <= 5; numero++) {
            lista.adicionarNoFim(numero);
        }
        verificar("tamanho 5 após inserir 5", lista.tamanho() == 5);
        verificar("ordem de inserção preservada", textoDaLista(lista).equals("1,2,3,4,5,"));
        verificar("buscarPrimeiro encontra 3", lista.buscarPrimeiro(numero -> numero == 3) != null);
        verificar("buscarPrimeiro não encontra 9", lista.buscarPrimeiro(numero -> numero == 9) == null);
        verificar("filtrar pares gera 2,4", textoDaLista(lista.filtrar(numero -> numero % 2 == 0)).equals("2,4,"));
        verificar("remover inexistente retorna null", lista.removerPrimeiro(numero -> numero == 9) == null);
        verificar("remover do início", lista.removerPrimeiro(numero -> numero == 1) != null
                && textoDaLista(lista).equals("2,3,4,5,"));
        verificar("remover do meio", lista.removerPrimeiro(numero -> numero == 3) != null
                && textoDaLista(lista).equals("2,4,5,"));
        verificar("remover do fim", lista.removerPrimeiro(numero -> numero == 5) != null
                && textoDaLista(lista).equals("2,4,"));
        lista.adicionarNoFim(7);
        verificar("inserir após remover o fim mantém encadeamento", textoDaLista(lista).equals("2,4,7,"));
        lista.removerPrimeiro(numero -> numero == 2);
        lista.removerPrimeiro(numero -> numero == 4);
        verificar("remover até restar um", lista.tamanho() == 1 && textoDaLista(lista).equals("7,"));
        verificar("remover o único elemento", lista.removerPrimeiro(numero -> numero == 7) != null && lista.estaVazia());
        lista.adicionarNoFim(8);
        verificar("lista reutilizável após esvaziar", lista.tamanho() == 1 && textoDaLista(lista).equals("8,"));
    }

    private static String textoDaLista(ListaDuplamenteEncadeada<Integer> lista) {
        StringBuilder texto = new StringBuilder();
        lista.paraCada(numero -> texto.append(numero).append(','));
        return texto.toString();
    }

    private static void testarArvore() {
        System.out.println("== Árvore binária de busca ==");
        ArvoreBinariaBusca<String, String> arvore = new ArvoreBinariaBusca<>();
        verificar("árvore nova vazia", arvore.tamanho() == 0 && arvore.buscar("m") == null);
        verificar("inserir raiz m", arvore.inserir("m", "M"));
        arvore.inserir("f", "F");
        arvore.inserir("t", "T");
        arvore.inserir("c", "C");
        arvore.inserir("h", "H");
        arvore.inserir("p", "P");
        arvore.inserir("w", "W");
        arvore.inserir("a", "A");
        verificar("tamanho 8", arvore.tamanho() == 8);
        verificar("duplicado rejeitado", !arvore.inserir("m", "outro") && arvore.tamanho() == 8);
        verificar("buscar existente", "H".equals(arvore.buscar("h")));
        verificar("buscar inexistente", arvore.buscar("z") == null && !arvore.contem("z"));
        verificar("em ordem alfabético", textoDaArvore(arvore).equals("a,c,f,h,m,p,t,w,"));

        verificar("remover folha (a)", arvore.remover("a") && textoDaArvore(arvore).equals("c,f,h,m,p,t,w,"));
        verificar("remover nó com 1 filho (f)", arvore.remover("f") && textoDaArvore(arvore).equals("c,h,m,p,t,w,"));
        verificar("remover nó com 2 filhos (t)", arvore.remover("t") && textoDaArvore(arvore).equals("c,h,m,p,w,"));
        verificar("remover raiz (m)", arvore.remover("m") && textoDaArvore(arvore).equals("c,h,p,w,"));
        verificar("remover inexistente retorna false", !arvore.remover("zz"));
        verificar("tamanho final 4", arvore.tamanho() == 4);
        verificar("buscas continuam corretas após remoções",
                "P".equals(arvore.buscar("p")) && arvore.buscar("m") == null);
        arvore.remover("c");
        arvore.remover("h");
        arvore.remover("p");
        arvore.remover("w");
        verificar("esvaziar a árvore", arvore.tamanho() == 0 && textoDaArvore(arvore).isEmpty());
    }

    private static String textoDaArvore(ArvoreBinariaBusca<String, String> arvore) {
        StringBuilder texto = new StringBuilder();
        arvore.emOrdem(valor -> texto.append(valor.toLowerCase()).append(','));
        return texto.toString();
    }

    private static void testarCadastro() {
        System.out.println("== Cadastro e validações ==");
        ListaComprasService listaCompras = new ListaComprasService();
        Produto arroz = cadastrarProduto(listaCompras, "Arroz", "Mercearia", "2", "5.50");
        verificar("primeiro código é 1", arroz.getId() == 1);
        verificar("novo produto é PENDENTE", arroz.getStatus() == StatusProduto.PENDENTE);
        verificarRejeita("nome duplicado normalizado ( ÁRROZ )",
                () -> cadastrarProduto(listaCompras, "  ÁRROZ ", "Mercearia", "1", "1"));
        verificarRejeita("nome vazio", () -> cadastrarProduto(listaCompras, "   ", "Mercearia", "1", "1"));
        verificarRejeita("seção vazia", () -> cadastrarProduto(listaCompras, "Feijão", " ", "1", "1"));
        verificarRejeita("quantidade zero", () -> cadastrarProduto(listaCompras, "Feijão", "Mercearia", "0", "1"));
        verificarRejeita("quantidade negativa", () -> cadastrarProduto(listaCompras, "Feijão", "Mercearia", "-1", "1"));
        verificarRejeita("quantidade com 4 casas",
                () -> cadastrarProduto(listaCompras, "Feijão", "Mercearia", "1.2345", "1"));
        verificarRejeita("preço negativo", () -> cadastrarProduto(listaCompras, "Feijão", "Mercearia", "1", "-0.01"));
        verificarRejeita("preço com 3 casas", () -> cadastrarProduto(listaCompras, "Feijão", "Mercearia", "1", "1.234"));
        verificarRejeita("unidade longa demais",
                () -> listaCompras.cadastrar("Feijão", "Mercearia", "unidadegigante", decimal("1"), decimal("1")));
        StringBuilder nomeLongo = new StringBuilder();
        for (int repeticao = 0; repeticao < 61; repeticao++) {
            nomeLongo.append('x');
        }
        verificarRejeita("nome com mais de 60 caracteres",
                () -> cadastrarProduto(listaCompras, nomeLongo.toString(), "Mercearia", "1", "1"));
        verificar("preço zero é aceito", cadastrarProduto(listaCompras, "Brinde", "Mercearia", "1", "0").getId() == 2);
        verificar("rejeições não cadastraram nada (2 itens)", listaCompras.tamanho() == 2);
    }

    private static void testarCompras() {
        System.out.println("== Compras parciais e totais ==");
        ListaComprasService listaCompras = new ListaComprasService();
        Produto leite = cadastrarProduto(listaCompras, "Leite", "Laticínios", "10", "5.00");
        verificar("inicia PENDENTE", leite.getStatus() == StatusProduto.PENDENTE);
        listaCompras.comprar(leite.getId(), decimal("3"), decimal("4.90"));
        verificar("após 1ª parcial: PARCIAL", leite.getStatus() == StatusProduto.PARCIAL);
        listaCompras.comprar(leite.getId(), decimal("2.5"), decimal("5.10"));
        verificar("após 2ª parcial: continua PARCIAL, acumula 5,5", leite.getStatus() == StatusProduto.PARCIAL
                && igual(leite.getQuantidadeComprada(), "5.5"));
        verificar("pendente = 4,5", igual(leite.getQuantidadePendente(), "4.5"));
        verificarRejeita("comprar acima do pendente",
                () -> listaCompras.comprar(leite.getId(), decimal("4.6"), decimal("5")));
        verificarRejeita("comprar quantidade zero",
                () -> listaCompras.comprar(leite.getId(), decimal("0"), decimal("5")));
        verificarRejeita("comprar com preço negativo",
                () -> listaCompras.comprar(leite.getId(), decimal("1"), decimal("-1")));
        verificarRejeita("comprar produto inexistente", () -> listaCompras.comprar(99, decimal("1"), decimal("1")));
        verificar("rejeições não alteraram a compra", igual(leite.getQuantidadeComprada(), "5.5"));
        listaCompras.comprarTotal(leite.getId(), decimal("5.00"));
        verificar("após compra total: COMPRADO", leite.getStatus() == StatusProduto.COMPRADO);
        verificar("pendente zerou", leite.getQuantidadePendente().signum() == 0);
        verificarRejeita("comprar item COMPRADO (parcial)",
                () -> listaCompras.comprar(leite.getId(), decimal("1"), decimal("1")));
        verificarRejeita("comprar item COMPRADO (total)", () -> listaCompras.comprarTotal(leite.getId(), decimal("1")));

        Produto pao = cadastrarProduto(listaCompras, "Pão", "Padaria", "4", "1.00");
        listaCompras.comprarTotal(pao.getId(), decimal("1.25"));
        verificar("compra total direta de PENDENTE -> COMPRADO", pao.getStatus() == StatusProduto.COMPRADO
                && igual(pao.getValorGasto(), "5.00"));
    }

    private static void testarValores() {
        System.out.println("== Valores e resumo ==");
        ListaComprasService listaCompras = new ListaComprasService();
        ResumoFinanceiro resumoVazio = listaCompras.calcularResumo();
        verificar("resumo vazio zerado", resumoVazio.getTotalItens() == 0
                && igual(resumoVazio.getValorGasto(), "0") && igual(resumoVazio.getPercentualConcluido(), "0"));

        Produto arroz = cadastrarProduto(listaCompras, "Arroz", "Mercearia", "2", "10.00");
        Produto leite = cadastrarProduto(listaCompras, "Leite", "Laticínios", "10", "5.00");
        Produto pao = cadastrarProduto(listaCompras, "Pão", "Padaria", "4", "1.50");
        listaCompras.comprarTotal(arroz.getId(), decimal("9.00"));
        listaCompras.comprar(leite.getId(), decimal("4"), decimal("5.50"));

        ResumoFinanceiro resumo = listaCompras.calcularResumo();
        verificar("total de itens 3", resumo.getTotalItens() == 3);
        verificar("1 pendente, 1 parcial, 1 comprado",
                resumo.getPendentes() == 1 && resumo.getParciais() == 1 && resumo.getComprados() == 1);
        verificar("estimado total = 76,00", igual(resumo.getValorEstimadoTotal(), "76.00"));
        verificar("gasto = 40,00", igual(resumo.getValorGasto(), "40.00"));
        verificar("estimado do comprado = 40,00", igual(resumo.getValorEstimadoComprado(), "40.00"));
        verificar("diferença = 0,00", igual(resumo.getDiferenca(), "0.00"));
        verificar("estimado restante = 36,00 (30 + 6)", igual(resumo.getValorEstimadoRestante(), "36.00"));
        verificar("projeção final = 76,00", igual(resumo.getProjecaoFinal(), "76.00"));
        verificar("percentual concluído = 33,3", igual(resumo.getPercentualConcluido(), "33.3"));

        listaCompras.comprarTotal(pao.getId(), decimal("1.00"));
        ResumoFinanceiro resumoAtualizado = listaCompras.calcularResumo();
        verificar("economia de 2,00 no item novo", igual(resumoAtualizado.getDiferenca(), "2.00"));
        verificar("gasto atualizado = 44,00 (recalculado)", igual(resumoAtualizado.getValorGasto(), "44.00"));
        verificar("estimado restante = 30,00", igual(resumoAtualizado.getValorEstimadoRestante(), "30.00"));
        verificar("projeção = 74,00", igual(resumoAtualizado.getProjecaoFinal(), "74.00"));
        verificar("resumo de lista filtrada considera só os itens dela",
                listaCompras.calcularResumo(listaCompras.listarPendentes()).getTotalItens() == 1);
        verificar("arredondamento HALF_UP do valor gasto (0,335 x 1,00 = 0,34)", arredondaValorGasto());
    }

    private static boolean arredondaValorGasto() {
        ListaComprasService listaCompras = new ListaComprasService();
        Produto granel = listaCompras.cadastrar("Granel", "Mercearia", "kg", decimal("1"), decimal("1"));
        listaCompras.comprar(granel.getId(), decimal("0.335"), decimal("1.00"));
        return igual(granel.getValorGasto(), "0.34");
    }

    private static void testarEdicao() {
        System.out.println("== Edição ==");
        ListaComprasService listaCompras = new ListaComprasService();
        Produto arroz = cadastrarProduto(listaCompras, "Arroz", "Mercearia", "5", "4.00");
        cadastrarProduto(listaCompras, "Feijão", "Mercearia", "2", "7.00");
        listaCompras.comprar(arroz.getId(), decimal("3"), decimal("4.00"));

        listaCompras.editar(arroz.getId(), "Arroz Integral", "Grãos", "kg", decimal("6"), decimal("4.50"));
        verificar("nome antigo não é mais encontrado", listaCompras.buscarPorNome("Arroz") == null);
        verificar("nome novo é encontrado (mesmo objeto)", listaCompras.buscarPorNome("arroz integral") == arroz);
        verificar("demais campos atualizados", arroz.getSecao().equals("Grãos") && arroz.getUnidade().equals("kg")
                && igual(arroz.getQuantidadeDesejada(), "6") && igual(arroz.getPrecoEstimadoUnitario(), "4.50"));
        verificar("compra anterior preservada",
                igual(arroz.getQuantidadeComprada(), "3") && igual(arroz.getValorGasto(), "12.00"));
        verificarRejeita("desejada abaixo da comprada", () -> listaCompras.editar(arroz.getId(), "Arroz Integral",
                "Grãos", "kg", decimal("2.5"), decimal("4")));
        verificarRejeita("renomear para nome já existente", () -> listaCompras.editar(arroz.getId(), "FEIJAO",
                "Grãos", "kg", decimal("6"), decimal("4")));
        verificar("rejeições não alteraram o produto",
                arroz.getNome().equals("Arroz Integral") && igual(arroz.getQuantidadeDesejada(), "6"));
        listaCompras.editar(arroz.getId(), "ARROZ INTEGRAL", "Grãos", "kg", decimal("6"), decimal("4.50"));
        verificar("alterar só maiúsculas do próprio nome é permitido", arroz.getNome().equals("ARROZ INTEGRAL")
                && listaCompras.buscarPorNome("arroz integral") == arroz);
        verificarRejeita("editar código inexistente",
                () -> listaCompras.editar(99, "X", "Y", "un", decimal("1"), decimal("1")));
    }

    private static void testarRemocao() {
        System.out.println("== Remoção ==");
        ListaComprasService listaCompras = new ListaComprasService();
        Produto arroz = cadastrarProduto(listaCompras, "Arroz", "Mercearia", "2", "5");
        Produto feijao = cadastrarProduto(listaCompras, "Feijão", "Mercearia", "2", "6");
        listaCompras.comprar(arroz.getId(), decimal("1"), decimal("5"));
        verificarRejeita("remover produto com compra", () -> listaCompras.remover(arroz.getId()));
        verificar("produto com compra continua na lista e no índice", listaCompras.buscarPorId(arroz.getId()) != null
                && listaCompras.buscarPorNome("arroz") == arroz);
        listaCompras.remover(feijao.getId());
        verificar("remoção sem compra tira da lista",
                listaCompras.buscarPorId(feijao.getId()) == null && listaCompras.tamanho() == 1);
        verificar("remoção sem compra tira do índice", listaCompras.buscarPorNome("Feijão") == null);
        verificar("ordem alfabética sem o removido", listaCompras.listarEmOrdemAlfabetica().tamanho() == 1);
        verificarRejeita("remover inexistente", () -> listaCompras.remover(feijao.getId()));
        Produto feijaoNovo = cadastrarProduto(listaCompras, "Feijão", "Mercearia", "1", "6");
        verificar("id não é reutilizado (3)", feijaoNovo.getId() == 3);
        verificar("nome removido pode ser cadastrado de novo", listaCompras.buscarPorNome("feijao") == feijaoNovo);
    }

    private static void testarBuscas() {
        System.out.println("== Buscas e listagens ==");
        ListaComprasService listaCompras = new ListaComprasService();
        cadastrarProduto(listaCompras, "Banana Prata", "Hortifrúti", "1", "5");
        cadastrarProduto(listaCompras, "Maçã", "Hortifrúti", "1", "6");
        cadastrarProduto(listaCompras, "Abacate", "Hortifrúti", "1", "7");
        Produto leite = cadastrarProduto(listaCompras, "Leite Integral", "Laticínios", "1", "4");

        verificar("busca por nome exato ignora acento/caixa", listaCompras.buscarPorNome(" MACA ") != null);
        verificar("busca por trecho ignora acento/caixa", listaCompras.buscarPorTrecho("MAC").tamanho() == 1);
        verificar("busca por trecho 'a' encontra todos com 'a'", listaCompras.buscarPorTrecho("a").tamanho() == 4);
        verificar("busca por trecho sem resultado", listaCompras.buscarPorTrecho("zzz").estaVazia());
        verificarRejeita("trecho vazio rejeitado", () -> listaCompras.buscarPorTrecho("   "));
        verificar("seção ignora acento/caixa", listaCompras.listarPorSecao("HORTIFRUTI").tamanho() == 3);
        verificar("seção de laticínios", listaCompras.listarPorSecao("laticinios").tamanho() == 1
                && listaCompras.listarPorSecao("laticinios").buscarPrimeiro(produto -> true) == leite);
        verificar("seção inexistente", listaCompras.listarPorSecao("padaria").estaVazia());

        StringBuilder codigosEmOrdemDeCadastro = new StringBuilder();
        listaCompras.listarEmOrdemCadastro().paraCada(produto -> codigosEmOrdemDeCadastro.append(produto.getId()));
        verificar("ordem de cadastro 1234", codigosEmOrdemDeCadastro.toString().equals("1234"));
        StringBuilder codigosEmOrdemAlfabetica = new StringBuilder();
        listaCompras.listarEmOrdemAlfabetica().paraCada(produto -> codigosEmOrdemAlfabetica.append(produto.getId()));
        verificar("ordem alfabética (Abacate, Banana, Leite, Maçã) = 3142",
                codigosEmOrdemAlfabetica.toString().equals("3142"));

        listaCompras.comprar(leite.getId(), decimal("0.5"), decimal("4"));
        verificar("adquiridos inclui parcial", listaCompras.listarComprados().tamanho() == 1);
        verificar("pendentes inclui parcial e pendentes", listaCompras.listarPendentes().tamanho() == 4);
        listaCompras.comprarTotal(leite.getId(), decimal("4"));
        verificar("pendentes exclui comprados", listaCompras.listarPendentes().tamanho() == 3);
        verificar("dados de exemplo inserem e não duplicam",
                DadosExemplo.carregar(listaCompras) > 0 && DadosExemplo.carregar(listaCompras) == 0);
    }

    private static void testarConsultasUnicas() {
        System.out.println("== Consultas por código/nome, cópia e critérios do produto ==");
        ListaComprasService listaCompras = new ListaComprasService();
        Produto arroz = cadastrarProduto(listaCompras, "Arroz", "Mercearia", "2", "10");
        Produto feijao = cadastrarProduto(listaCompras, "Feijão", "Mercearia", "1", "8");

        verificar("listarPorId encontrado devolve 1 item",
                listaCompras.listarPorId(arroz.getId()).tamanho() == 1
                        && listaCompras.listarPorId(arroz.getId()).buscarPrimeiro(produto -> true) == arroz);
        verificar("listarPorId inexistente devolve lista vazia", listaCompras.listarPorId(99).estaVazia());
        verificar("listarPorNome ignora acento e caixa", listaCompras.listarPorNome(" FEIJAO ").tamanho() == 1);
        verificar("listarPorNome inexistente devolve lista vazia", listaCompras.listarPorNome("zzz").estaVazia());

        ListaDuplamenteEncadeada<Produto> copia = listaCompras.listarEmOrdemCadastro();
        verificar("copia mantém tamanho e ordem", copia.tamanho() == 2 && copia.obter(0) == arroz
                && copia.obter(1) == feijao);
        copia.adicionarNoFim(arroz);
        verificar("alterar a cópia não altera a lista original", listaCompras.tamanho() == 2
                && listaCompras.listarEmOrdemCadastro().tamanho() == 2);
        verificar("comUmElemento cria lista de 1 item",
                ListaDuplamenteEncadeada.comUmElemento(7).tamanho() == 1);

        verificar("produto novo: sem compra e falta comprar", !arroz.possuiCompra() && arroz.faltaComprar());
        listaCompras.comprar(arroz.getId(), decimal("1"), decimal("10"));
        verificar("produto parcial: com compra e ainda falta", arroz.possuiCompra() && arroz.faltaComprar());
        listaCompras.comprarTotal(arroz.getId(), decimal("10"));
        verificar("produto completo: com compra e nada falta", arroz.possuiCompra() && !arroz.faltaComprar());
        verificar("calcularValor arredonda em 2 casas HALF_UP",
                igual(Produto.calcularValor(decimal("1.5"), decimal("0.33")), "0.50")
                        && Produto.calcularValor(decimal("1.5"), decimal("0.33")).scale() == 2);
    }

    private static void testarObter() {
        System.out.println("== Lista: obter(int) ==");
        ListaDuplamenteEncadeada<Integer> lista = new ListaDuplamenteEncadeada<>();
        for (int indice = 0; indice < 7; indice++) {
            lista.adicionarNoFim(indice * 10);
        }
        boolean todosCorretos = true;
        for (int indice = 0; indice < 7; indice++) {
            todosCorretos = todosCorretos && lista.obter(indice) == indice * 10;
        }
        verificar("obter devolve cada posição (início e fim da lista)", todosCorretos);
        boolean rejeitou = false;
        try {
            lista.obter(7);
        } catch (IndexOutOfBoundsException excecao) {
            rejeitou = true;
        }
        verificar("obter fora do limite lança IndexOutOfBoundsException", rejeitou);
        lista.removerPrimeiro(numero -> numero == 30);
        verificar("obter após remoção no meio", lista.obter(3) == 40 && lista.obter(5) == 60);
    }

    private static void testarI18n() {
        System.out.println("== I18n e formatação ==");
        verificar("chave existente é carregada",
                I18n.existe("app.titulo") && !I18n.t("app.titulo").startsWith("!"));
        verificar("acentos UTF-8 preservados", I18n.t("secao.hortifruti").equals("Hortifrúti")
                && I18n.t("status.comprado").equals("Comprado"));
        verificar("chave ausente devolve !chave!", I18n.t("chave.que.nao.existe").equals("!chave.que.nao.existe!")
                && !I18n.existe("chave.que.nao.existe"));
        verificar("um parâmetro {0}",
                I18n.t("erro.nome_duplicado", "Arroz").equals("Já existe um produto com o nome \"Arroz\"."));
        verificar("dois parâmetros {0} {1}", I18n.t("erro.campo_tamanho", "Seção", "30")
                .equals("O campo \"Seção\" deve ter no máximo 30 caracteres."));
        verificar("três parâmetros {0} {1} {2}",
                I18n.t("selecao.item", "1", "Arroz", "Pendente").equals("#1 - Arroz - Pendente"));
        verificar("parâmetro ausente vira vazio", I18n.t("selecao.item", "1").equals("#1 -  - "));
        verificar("descrição do status vem do bundle",
                StatusProduto.PARCIAL.getDescricao().equals(I18n.t("status.parcial")));

        ListaComprasService listaCompras = new ListaComprasService();
        cadastrarProduto(listaCompras, "Arroz", "Mercearia", "1", "1");
        String mensagem = "";
        try {
            cadastrarProduto(listaCompras, " ARROZ ", "Mercearia", "1", "1");
        } catch (RegraNegocioException excecao) {
            mensagem = excecao.getMessage();
        }
        verificar("exceção de regra usa a mensagem do bundle",
                mensagem.equals(I18n.t("erro.nome_duplicado", "ARROZ")));

        verificar("moeda com milhar e vírgula", Formatador.moeda(decimal("1234.5")).equals("R$ 1.234,50"));
        verificar("moeda negativa", Formatador.moeda(decimal("-3")).equals("-R$ 3,00"));
        verificar("moeda com milhões", Formatador.moeda(decimal("1234567.89")).equals("R$ 1.234.567,89"));
        verificar("quantidade sem zeros à direita", Formatador.quantidade(decimal("1.500")).equals("1,5")
                && Formatador.quantidade(decimal("0.000")).equals("0"));
        verificar("porcentagem", Formatador.porcentagem(decimal("37.5")).equals("37,5%"));
        verificar("preço para campo de digitação", Formatador.preco(decimal("5.4")).equals("5,40"));
    }

    private static void testarConversor() {
        System.out.println("== Conversor ==");
        verificar("decimal com vírgula", igual(Conversor.paraDecimal("2,5"), "2.5"));
        verificar("decimal com ponto e espaços", igual(Conversor.paraDecimal(" 1.25 "), "1.25"));
        verificar("decimal inválido devolve null", Conversor.paraDecimal("1,2,3") == null
                && Conversor.paraDecimal("abc") == null && Conversor.paraDecimal("") == null
                && Conversor.paraDecimal(".") == null && Conversor.paraDecimal(null) == null);
        verificar("inteiro válido", Conversor.paraInteiro(" 42 ") == 42);
        verificar("inteiro inválido devolve null", Conversor.paraInteiro("4x") == null
                && Conversor.paraInteiro("-1") == null && Conversor.paraInteiro("1234567890") == null
                && Conversor.paraInteiro("") == null);
    }
}
