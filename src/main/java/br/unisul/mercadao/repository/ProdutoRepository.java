package br.unisul.mercadao.repository;

import br.unisul.mercadao.model.Produto;
import br.unisul.mercadao.util.Texto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Guarda os produtos em memória (lista na ordem de cadastro) e responde consultas simples.
 * Não conhece regras de negócio nem textos de tela.
 */
public class ProdutoRepository {

    private final List<Produto> produtos = new ArrayList<>();
    private int proximoId = 1;

    public int gerarId() {
        int id = proximoId;
        proximoId++;
        return id;
    }

    public void adicionar(Produto produto) {
        produtos.add(produto);
    }

    public void inserirPorCodigo(Produto produto) {
        int posicao = 0;
        while (posicao < produtos.size() && produtos.get(posicao).getId() < produto.getId()) {
            posicao++;
        }
        produtos.add(posicao, produto);
    }

    public void remover(Produto produto) {
        produtos.remove(produto);
    }

    public Produto buscarPorId(int id) {
        for (Produto produto : produtos) {
            if (produto.getId() == id) {
                return produto;
            }
        }
        return null;
    }

    public Produto buscarPorNome(String nome) {
        String chave = Texto.normalizar(nome);
        for (Produto produto : produtos) {
            if (Texto.normalizar(produto.getNome()).equals(chave)) {
                return produto;
            }
        }
        return null;
    }

    public boolean existeNome(String nome, int idIgnorado) {
        Produto encontrado = buscarPorNome(nome);
        return encontrado != null && encontrado.getId() != idIgnorado;
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    public List<Produto> listarOrdenadosPorNome() {
        List<Produto> resultado = new ArrayList<>(produtos);
        resultado.sort((a, b) -> Texto.normalizar(a.getNome()).compareTo(Texto.normalizar(b.getNome())));
        return resultado;
    }

    public List<Produto> filtrar(Predicate<Produto> criterio) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto produto : produtos) {
            if (criterio.test(produto)) {
                resultado.add(produto);
            }
        }
        return resultado;
    }

    public int tamanho() {
        return produtos.size();
    }
}
