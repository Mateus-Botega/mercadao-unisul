package br.unisul.mercadao.repository;

import br.unisul.mercadao.model.CompraRegistrada;
import br.unisul.mercadao.structure.Pilha;

/** Pilha das compras feitas: a mais recente fica no topo e é a primeira a ser desfeita. */
public class HistoricoCompras {

    private final Pilha<CompraRegistrada> compras = new Pilha<>();

    public void registrar(CompraRegistrada compra) {
        compras.empilhar(compra);
    }

    public CompraRegistrada ultima() {
        return compras.espiar();
    }

    public CompraRegistrada retirarUltima() {
        return compras.desempilhar();
    }
}
