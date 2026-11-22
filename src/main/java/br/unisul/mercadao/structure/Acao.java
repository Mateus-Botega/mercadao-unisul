package br.unisul.mercadao.structure;

@FunctionalInterface
public interface Acao<T> {

    void executar(T elemento);
}
