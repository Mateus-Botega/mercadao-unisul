package br.unisul.mercadao.structure;

@FunctionalInterface
public interface Filtro<T> {

    boolean aceita(T elemento);
}
