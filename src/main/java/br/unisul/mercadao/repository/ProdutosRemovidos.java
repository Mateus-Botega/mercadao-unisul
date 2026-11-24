package br.unisul.mercadao.repository;

import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.structure.Pilha;

/** Pilha dos produtos removidos: o último removido fica no topo e é o primeiro a ser restaurado. */
public class ProdutosRemovidos {

    private final Pilha<Produto> removidos = new Pilha<>();

    public void guardar(Produto produto) {
        removidos.empilhar(produto);
    }

    public Produto ultimo() {
        return removidos.espiar();
    }

    public Produto retirarUltimo() {
        return removidos.desempilhar();
    }
}
