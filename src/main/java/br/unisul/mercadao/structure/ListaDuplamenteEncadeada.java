package br.unisul.mercadao.structure;

public class ListaDuplamenteEncadeada<T> {

    private static class No<T> {
        private final T valor;
        private No<T> anterior;
        private No<T> proximo;

        private No(T valor) {
            this.valor = valor;
        }
    }

    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public void adicionarNoFim(T valor) {
        No<T> novoNo = new No<>(valor);
        if (fim == null) {
            inicio = novoNo;
        } else {
            novoNo.anterior = fim;
            fim.proximo = novoNo;
        }
        fim = novoNo;
        tamanho++;
    }

    public T removerPrimeiro(Filtro<T> filtro) {
        No<T> encontrado = localizar(filtro);
        if (encontrado == null) {
            return null;
        }
        desligar(encontrado);
        return encontrado.valor;
    }

    public T buscarPrimeiro(Filtro<T> filtro) {
        No<T> encontrado = localizar(filtro);
        return encontrado == null ? null : encontrado.valor;
    }

    public ListaDuplamenteEncadeada<T> filtrar(Filtro<T> filtro) {
        ListaDuplamenteEncadeada<T> resultado = new ListaDuplamenteEncadeada<>();
        No<T> atual = inicio;
        while (atual != null) {
            if (filtro.aceita(atual.valor)) {
                resultado.adicionarNoFim(atual.valor);
            }
            atual = atual.proximo;
        }
        return resultado;
    }

    public ListaDuplamenteEncadeada<T> copiar() {
        ListaDuplamenteEncadeada<T> copia = new ListaDuplamenteEncadeada<>();
        No<T> atual = inicio;
        while (atual != null) {
            copia.adicionarNoFim(atual.valor);
            atual = atual.proximo;
        }
        return copia;
    }

    public static <T> ListaDuplamenteEncadeada<T> comUmElemento(T valor) {
        ListaDuplamenteEncadeada<T> lista = new ListaDuplamenteEncadeada<>();
        lista.adicionarNoFim(valor);
        return lista;
    }

    public void paraCada(Acao<T> acao) {
        No<T> atual = inicio;
        while (atual != null) {
            acao.executar(atual.valor);
            atual = atual.proximo;
        }
    }

    public T obter(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException(indice);
        }
        if (indice < tamanho / 2) {
            return percorrerDoInicio(indice).valor;
        }
        return percorrerDoFim(indice).valor;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    private No<T> localizar(Filtro<T> filtro) {
        No<T> atual = inicio;
        while (atual != null && !filtro.aceita(atual.valor)) {
            atual = atual.proximo;
        }
        return atual;
    }

    private No<T> percorrerDoInicio(int indice) {
        No<T> atual = inicio;
        for (int posicao = 0; posicao < indice; posicao++) {
            atual = atual.proximo;
        }
        return atual;
    }

    private No<T> percorrerDoFim(int indice) {
        No<T> atual = fim;
        for (int posicao = tamanho - 1; posicao > indice; posicao--) {
            atual = atual.anterior;
        }
        return atual;
    }

    private void desligar(No<T> no) {
        if (no.anterior == null) {
            inicio = no.proximo;
        } else {
            no.anterior.proximo = no.proximo;
        }
        if (no.proximo == null) {
            fim = no.anterior;
        } else {
            no.proximo.anterior = no.anterior;
        }
        no.anterior = null;
        no.proximo = null;
        tamanho--;
    }
}
