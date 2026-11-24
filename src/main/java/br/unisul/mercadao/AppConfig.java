package br.unisul.mercadao;

import br.unisul.mercadao.repository.HistoricoCompras;
import br.unisul.mercadao.repository.ProdutoRepository;
import br.unisul.mercadao.repository.ProdutosRemovidos;
import br.unisul.mercadao.service.ListaComprasService;
import br.unisul.mercadao.service.ResumoService;
import br.unisul.mercadao.validation.ProdutoValidador;

/** Único lugar que monta as camadas e injeta as dependências. */
public final class AppConfig {

    private AppConfig() {
    }

    public static ListaComprasService criarServico() {
        return new ListaComprasService(new ProdutoRepository(), new HistoricoCompras(),
                new ProdutosRemovidos(), new ProdutoValidador(), new ResumoService());
    }
}
