package br.unisul.mercadao.structure;

/**
 * Pilha (LIFO): o ultimo elemento empilhado e o primeiro a ser desempilhado.
 * Implementada com nos encadeados, sem vetor.
*/
public class Pilha<T> {

    private static class No<T> {
        private final T valor;
        private final No<T> abaixo;

        private No(T valor, No<T> abaixo) {
            this.valor = valor;
            this.abaixo = abaixo;
        }
    }

    private No<T> topo;
    private int tamanho;

    public void empilhar(T valor) {
        topo = new No<>(valor, topo);
        tamanho++;
    }

    public T desempilhar() {
        if (topo == null) {
            return null;
        }
        T valor = topo.valor;
        topo = topo.abaixo;
        tamanho--;
        return valor;
    }

    public T espiar() {
        return topo == null ? null : topo.valor;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }
}
