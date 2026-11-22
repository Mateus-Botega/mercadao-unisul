package br.unisul.mercadao.structure;

public class ArvoreBinariaBusca<K extends Comparable<K>, V> {

    private static class No<K, V> {
        private K chave;
        private V valor;
        private No<K, V> esquerda;
        private No<K, V> direita;

        private No(K chave, V valor) {
            this.chave = chave;
            this.valor = valor;
        }
    }

    private No<K, V> raiz;
    private int tamanho;

    public boolean inserir(K chave, V valor) {
        if (contem(chave)) {
            return false;
        }
        raiz = inserir(raiz, chave, valor);
        tamanho++;
        return true;
    }

    public V buscar(K chave) {
        No<K, V> atual = raiz;
        while (atual != null) {
            int comparacao = chave.compareTo(atual.chave);
            if (comparacao == 0) {
                return atual.valor;
            }
            atual = comparacao < 0 ? atual.esquerda : atual.direita;
        }
        return null;
    }

    public boolean contem(K chave) {
        return buscar(chave) != null;
    }

    public boolean remover(K chave) {
        if (!contem(chave)) {
            return false;
        }
        raiz = remover(raiz, chave);
        tamanho--;
        return true;
    }

    public void emOrdem(Acao<V> acao) {
        emOrdem(raiz, acao);
    }

    public int tamanho() {
        return tamanho;
    }

    private No<K, V> inserir(No<K, V> no, K chave, V valor) {
        if (no == null) {
            return new No<>(chave, valor);
        }
        if (chave.compareTo(no.chave) < 0) {
            no.esquerda = inserir(no.esquerda, chave, valor);
        } else {
            no.direita = inserir(no.direita, chave, valor);
        }
        return no;
    }

    private No<K, V> remover(No<K, V> no, K chave) {
        int comparacao = chave.compareTo(no.chave);
        if (comparacao < 0) {
            no.esquerda = remover(no.esquerda, chave);
            return no;
        }
        if (comparacao > 0) {
            no.direita = remover(no.direita, chave);
            return no;
        }
        return removerNo(no);
    }

    private No<K, V> removerNo(No<K, V> no) {
        if (no.esquerda == null) {
            return no.direita;
        }
        if (no.direita == null) {
            return no.esquerda;
        }
        No<K, V> sucessor = menorNo(no.direita);
        no.chave = sucessor.chave;
        no.valor = sucessor.valor;
        no.direita = remover(no.direita, sucessor.chave);
        return no;
    }

    private No<K, V> menorNo(No<K, V> no) {
        No<K, V> atual = no;
        while (atual.esquerda != null) {
            atual = atual.esquerda;
        }
        return atual;
    }

    private void emOrdem(No<K, V> no, Acao<V> acao) {
        if (no == null) {
            return;
        }
        emOrdem(no.esquerda, acao);
        acao.executar(no.valor);
        emOrdem(no.direita, acao);
    }
}
