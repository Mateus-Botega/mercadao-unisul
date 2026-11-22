# Plano de Implementação - Mercadão Unisul (Lista de Compras)

## 0. Decisões tomadas

| Tema | Decisão |
|---|---|
| Linguagem / build | Java 24 (Corretto 24, já configurado no `.idea` e `pom.xml`), Maven, sem dependências externas |
| Interface | `JOptionPane` (javax.swing). **Proibido `Scanner`/console para interação**. Relatórios longos em `JTextArea` monoespaçada dentro de `JScrollPane` mostrada via `JOptionPane` |
| Estruturas | **Implementadas à mão**, sem `java.util` (nem `ArrayList`, `HashMap`, `Iterator`, `Predicate`, `Locale`, `Scanner`...) |
| Vetores/matrizes | **Proibidos no código da equipe.** Única exceção: `String[] args` do `main`. Não usar `split`, `toCharArray`, varargs (`String.format`, `printf`), `showOptionDialog` com array de opções, `Enum.values()` |
| Dinheiro / quantidade | `java.math.BigDecimal` (dinheiro escala 2, quantidade escala 3, `RoundingMode.HALF_UP`). Nunca `double` |
| Totais | **Nunca armazenados em cache.** Sempre recalculados percorrendo a lista ⇒ consistência garantida |
| Escopo extra | Editar e remover produto; consulta por seção (localizar no mercado); dados de exemplo para demonstração |

## 1. Checklist de regras da documentação (rastreabilidade)

| ID | Regra (Documentacao.md) | Onde é atendida |
|---|---|---|
| R1 | Cadastrar produtos | Menu 1 → `ListaCompras.cadastrar` |
| R2 | Consultar e localizar itens | Menu 2, 3, 4 (código, nome exato, trecho, seção) |
| R3 | Registrar compras totais | Menu 8 → `ListaCompras.comprarTotal` |
| R4 | Registrar compras parciais | Menu 7 → `ListaCompras.comprar` (várias compras parciais acumulam) |
| R5 | Info de produtos adquiridos | Menu 9 (comprados e parciais, com qtd e valor gasto) |
| R6 | Info de produtos pendentes | Menu 10 (pendentes e parciais, com qtd e valor estimado restante) |
| R7 | Valores estimados e gastos | Menu 11 resumo + colunas nos relatórios |
| R8 | Estruturas adequadas, sem vetor/matriz | `estrutura/` - lista duplamente encadeada + árvore binária de busca próprias |
| R9 | Descrição técnica e justificativa | `docs/DescricaoTecnica.md` |
| R10 | Atualização de produtos | Menu 5 editar, Menu 6 remover |
| R11 | Única lista de compras na execução | `ListaCompras` instanciada uma única vez no `Main` |
| R12 | Somente memória, sem arquivo/BD | Nenhum `java.io`/`java.nio`/JDBC no código |
| R13 | Dados e cálculos consistentes | Validações (seção 4), BigDecimal, totais recalculados, lista e árvore sempre sincronizadas |
| R14 | Interface simples que demonstre funções | JOptionPane com menu numerado |
| R15 | Decisões adicionais explicadas | Seção "Decisões adicionais" da DescricaoTecnica.md |
| R16 | Apresentação/demonstração | Roteiro de demonstração na DescricaoTecnica.md + menu 12 dados de exemplo |

## 2. Estrutura de pacotes

```
src/main/java/br/unisul/mercadao/
├── Main.java                         // cria ListaCompras única e abre o menu
├── estrutura/
│   ├── ListaDuplamenteEncadeada.java // genérica <T>, nó interno privado
│   ├── ArvoreBinariaBusca.java       // genérica <K extends Comparable<K>, V>
│   ├── Filtro.java                   // interface funcional própria: boolean aceita(T)
│   └── Acao.java                     // interface funcional própria: void executar(T)
├── modelo/
│   ├── Produto.java
│   └── StatusProduto.java            // PENDENTE, PARCIAL, COMPRADO (+ descrição)
├── servico/
│   ├── ListaCompras.java             // regras de negócio
│   ├── ResumoFinanceiro.java         // objeto imutável com os totais
│   └── RegraNegocioException.java    // extends RuntimeException
├── ui/
│   ├── Dialogos.java                 // helpers JOptionPane (ler/validar/mostrar)
│   └── MenuPrincipal.java            // laço do menu e telas
└── util/
    ├── Formatador.java               // moeda "R$ 1.234,56", quantidade "1,5"
    └── Texto.java                    // normalizar nome (trim, espaços, minúsculo, sem acento)
src/test/java/br/unisul/mercadao/
└── TesteSistema.java                 // testes sem framework, main com asserts próprios
docs/DescricaoTecnica.md
```

## 3. Estruturas de dados

### 3.1 `ListaDuplamenteEncadeada<T>` (fonte principal da lista de compras)
- Campos: `inicio`, `fim`, `tamanho`. Nó privado com `valor`, `anterior`, `proximo`.
- Operações: `adicionarNoFim(T)` O(1); `removerPrimeiro(Filtro<T>)` O(n) retorna o removido ou null; `buscarPrimeiro(Filtro<T>)` O(n); `filtrar(Filtro<T>)` → nova lista O(n); `paraCada(Acao<T>)` O(n) em ordem de cadastro; `tamanho()`, `estaVazia()`.
- Justificativa: tamanho dinâmico (sem vetor), inserção O(1) no fim preserva ordem de cadastro, remoção no meio sem deslocar elementos (encadeamento duplo facilita religar anterior/próximo).

### 3.2 `ArvoreBinariaBusca<K, V>` (índice por nome)
- Chave = nome normalizado (`Texto.normalizar`), valor = referência ao **mesmo** objeto `Produto` da lista.
- Operações: `inserir(K,V)` (lança exceção ou retorna false se chave existe), `buscar(K)`, `remover(K)` (3 casos: folha, 1 filho, 2 filhos via sucessor), `emOrdem(Acao<V>)`, `tamanho()`, `contem(K)`.
- Implementação **iterativa** ou recursiva - ambas ok; recursão é aceitável para o volume do trabalho.
- Justificativa: busca por nome O(log n) no caso médio, impede nomes duplicados, listagem alfabética de graça pelo percurso em ordem.

### 3.3 Sincronização
- Toda operação que altera a lista altera a árvore na mesma operação (cadastrar insere nas duas; remover remove das duas; renomear remove a chave antiga e insere a nova). Validar **antes** de alterar qualquer estrutura para nunca ficar meio-atualizado.
- Busca por código (`id`) usa a lista (O(n)) - aceitável e explicado no documento.

## 4. Modelo e regras de negócio

### 4.1 `Produto`
Campos: `id` (int, sequencial a partir de 1, nunca reutilizado), `nome` (exibição, com trim e espaços colapsados), `secao` (ex.: Hortifrúti, Laticínios - "localizar" o item no mercado), `unidade` (texto livre curto: un, kg, L, pct), `quantidadeDesejada`, `precoEstimadoUnitario`, `quantidadeComprada` (inicia 0), `valorGasto` (inicia 0,00).

Derivados (métodos, sem campo):
- `getQuantidadePendente()` = desejada − comprada
- `getValorEstimadoTotal()` = desejada × preçoEstimado
- `getValorEstimadoPendente()` = pendente × preçoEstimado
- `getStatus()`: comprada = 0 → PENDENTE; 0 < comprada < desejada → PARCIAL; comprada = desejada → COMPRADO

Setters de quantidade comprada/gasto **não públicos**; só `registrarCompra(qtd, precoUnitarioPago)` altera, somando `qtd` e `qtd × preço` (arredondado escala 2).

### 4.2 Validações (lançam `RegraNegocioException` com mensagem amigável em português)
- Nome obrigatório, não vazio após trim, máx. 60 caracteres, **único** (comparação normalizada: sem acento, minúsculo, espaços colapsados - "Arroz" == " arroz " == "ÁRROZ").
- Seção obrigatória (máx. 30). Unidade obrigatória (máx. 10).
- Quantidade desejada > 0, máx. 3 casas decimais.
- Preço estimado ≥ 0 (zero permitido para brinde/não sei), máx. 2 casas.
- Compra parcial: qtd > 0 e ≤ pendente; preço pago ≥ 0; produto não pode estar COMPRADO.
- Compra total: compra exatamente o pendente; preço pago unitário sugerido = estimado (usuário pode alterar).
- Editar: nome (unicidade, atualiza árvore), seção, unidade, preço estimado, quantidade desejada - **nova quantidade desejada ≥ quantidade já comprada**.
- Remover: só se `quantidadeComprada == 0` (preserva o valor gasto e a consistência). Mensagem explica o motivo. Confirmação antes de remover.

### 4.3 `ListaCompras` (serviço, uma instância)
`cadastrar(...)`, `buscarPorId(int)`, `buscarPorNome(String)`, `buscarPorTrecho(String)` (lista filtrada, contém, normalizado), `listarPorSecao(String)`, `listarEmOrdemCadastro()`, `listarEmOrdemAlfabetica()`, `listarComprados()` (status COMPRADO ou PARCIAL - tudo que tem algo adquirido), `listarPendentes()` (PENDENTE ou PARCIAL - tudo que falta algo), `editar...`, `remover(int id)`, `comprar(int id, BigDecimal qtd, BigDecimal precoPago)`, `comprarTotal(int id, BigDecimal precoPago)`, `calcularResumo()`, `carregarExemplos()` (ignora nomes já existentes, retorna quantos inseriu).

### 4.4 `ResumoFinanceiro`
Total de itens; qtd pendentes/parciais/comprados; **valor estimado total da lista**; **valor gasto**; **valor estimado do que já foi comprado** (comprada × estimado); **diferença economia/excesso** (estimado comprado − gasto); **valor estimado restante** (soma dos pendentes); **projeção final** (gasto + estimado restante); percentual concluído por itens.

## 5. Interface (JOptionPane)

Menu atual: ver seção 10.3 e 11.

- `Main`: `UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())` em try/catch; cria `ListaCompras`; `new MenuPrincipal(lista).executar()`.
- Menu via `showInputDialog` com texto do menu (StringBuilder) e campo para digitar o número. Título "Mercadão Unisul - Lista de Compras". Cabeçalho mostra mini-resumo (itens, gasto, restante).

```
 1 - Cadastrar produto
 2 - Listar produtos (ordem de cadastro)
 3 - Listar produtos (ordem alfabética)
 4 - Consultar / localizar produto
 5 - Editar produto
 6 - Remover produto
 7 - Registrar compra parcial
 8 - Registrar compra total
 9 - Produtos adquiridos
10 - Produtos pendentes
11 - Resumo financeiro
12 - Carregar dados de exemplo
 0 - Sair
```
- Submenu consulta: 1 por código, 2 por nome exato, 3 por parte do nome, 4 por seção.
- Seleção de produto (editar/remover/comprar): mostra lista curta "código - nome (status)" e pede o código.
- `Dialogos`: `lerTexto(titulo, msg, obrigatorio)`, `lerDecimal(titulo, msg, permiteZero, valorPadrao)` (aceita "1,5" e "1.5"), `lerInteiro`, `confirmar` (`showConfirmDialog` SIM/NÃO), `info`, `erro`, `mostrarRelatorio(titulo, texto)` (JTextArea monoespaçada, não editável, JScrollPane ~ 700x400).
- **Cancelar/fechar (null) em qualquer diálogo = volta ao menu sem alterar nada** (nunca `NullPointerException`). Entrada inválida = mensagem de erro e pergunta de novo. Exceções de regra → `Dialogos.erro(e.getMessage())`.
- Sair pede confirmação e avisa que os dados serão perdidos (só memória).
- Relatórios em tabela alinhada (padding manual com StringBuilder, sem `String.format`): Código | Nome | Seção | Desejada | Comprada | Pendente | Unid | Preço est. | Est. total | Gasto | Status, com linha de totais no fim. Lista vazia → mensagem "Nenhum produto ...".

## 6. Utilitários
- `Formatador.moeda(BigDecimal)` → "R$ 1.234,56" (manual, negativo com "-"). `Formatador.quantidade(BigDecimal)` → remove zeros à direita, vírgula decimal ("2", "1,5"). `Formatador.porcentagem`.
- `Texto.normalizar(String)` → `java.text.Normalizer` NFD, remover marcas diacríticas, minúsculo, trim, espaços múltiplos → um. `Texto.ajustar(String, largura)` corta/preenche para tabelas.

## 7. Testes (`src/test/java/br/unisul/mercadao/TesteSistema.java`, sem JUnit)
`main` executa casos e imprime `OK`/`FALHOU` + total; `System.exit(1)` se algo falhar. Casos mínimos:
1. Lista: inserir, remover início/meio/fim/único, buscar, filtrar, tamanho.
2. Árvore: inserir, duplicado rejeitado, buscar, em ordem alfabética, remover folha / 1 filho / 2 filhos / raiz.
3. Cadastro: nome duplicado normalizado rejeitado ("Arroz" vs " ÁRROZ "); validações de quantidade/preço.
4. Compra parcial 2× + total → status PENDENTE→PARCIAL→COMPRADO; comprar acima do pendente rejeitado; comprar item COMPRADO rejeitado.
5. Valores: estimado, gasto, diferença, restante, projeção conferidos com BigDecimal (`compareTo`).
6. Editar: renomear atualiza busca por nome (antigo some, novo acha); reduzir desejada abaixo da comprada rejeitado.
7. Remover: com compra rejeitado; sem compra remove da lista **e** da árvore; ids não reutilizados.
8. Busca por trecho e por seção, ignorando acento/maiúscula.

## 8. Documentação técnica (`docs/DescricaoTecnica.md`)
Visão geral; como executar (IntelliJ e linha de comando); arquitetura em camadas; **cada estrutura: o que é, por que foi escolhida, por que não vetor/matriz, complexidade das operações (tabela Big-O)**; diagrama ASCII da lista + árvore apontando para os mesmos `Produto`; regras de negócio e como garantem consistência; decisões adicionais (BigDecimal, totais recalculados, remoção bloqueada após compra, nome normalizado, dados de exemplo, JOptionPane); roteiro de demonstração passo a passo para a apresentação; mapeamento regra → funcionalidade (tabela da seção 1).

## 9. Critérios de aceite (validação do chefe de obra)
- [ ] `javac` (JDK 24) compila main e test sem warnings de erro.
- [ ] `TesteSistema` 100% OK.
- [ ] `grep` no `src/main`: nenhum `java.util`, `Scanner`, `System.in`, `java.io`, `java.nio`, `[]` (exceto `String[] args`), `split(`, `toCharArray`, `String.format`, `printf`, `values()`, `double`/`float` em valores.
- [ ] Revisão manual de cada regra R1-R16.
- [ ] Teste de fluxo da UI (cancelar em cada diálogo não quebra).

## 10. Revisão 2 - pontuação, interface interativa e i18n

### 10.1 Pontuação
- Nenhum travessão (U+2014) nem meia-risca (U+2013) em nenhum arquivo do projeto (código, properties, docs). Usar apenas "-" simples.

### 10.2 i18n (padrão oficial Java)
- Arquivo único: `src/main/resources/i18n/mensagens_pt_BR.properties` (somente ASCII; acentos escritos como `\uXXXX`; sem comentários). Só pt-BR.
- Classe `br.unisul.mercadao.i18n.I18n`: carrega com `ResourceBundle.getBundle("i18n.mensagens", Locale.of("pt", "BR"))`.
  - **Única classe autorizada a importar `java.util`** (`ResourceBundle`, `Locale`, `MissingResourceException`). Exceção documentada: não é estrutura de dados nem vetor; as estruturas da lista continuam 100% próprias.
  - API sem varargs/arrays: `t(chave)`, `t(chave, a)`, `t(chave, a, b)`, `t(chave, a, b, c)`. Substituição própria de `{0}`, `{1}`, `{2}` (não usar `MessageFormat`, que exige varargs). Chave ausente devolve `!chave!` (fica visível no teste).
- **Todo texto visível ao usuário vem do bundle**: menus, títulos, rótulos, botões, dicas (tooltips), mensagens de erro de `RegraNegocioException`, descrições de `StatusProduto`, cabeçalhos de colunas, resumo, dados de exemplo (nomes/seções/unidades), textos padrão do `JOptionPane` ("OK", "Cancelar", "Sim", "Não" via `UIManager.put("OptionPane.okButtonText", ...)` etc.).
- Chaves agrupadas por prefixo: `app.*`, `menu.*`, `consulta.*`, `form.*`, `compra.*`, `relatorio.*`, `coluna.*`, `resumo.*`, `status.*`, `erro.*`, `sucesso.*`, `confirmacao.*`, `exemplo.*`, `unidade.*`, `botao.*`.
- Formatação de moeda/quantidade (símbolo "R$", separador decimal) também via chaves (`formato.moeda.simbolo`, `formato.decimal`, `formato.milhar`).

### 10.3 Interface interativa com ícones (sempre `JOptionPane` como contêiner)
- `ui/Icones.java`: ícones desenhados por código (`javax.swing.Icon`, `Graphics2D` com antialias, `Path2D` - sem `Polygon`/arrays, sem arquivos de imagem). Quadrado arredondado colorido + símbolo branco. Tamanhos 24 (botões) e 48 (diálogos). Um ícone por ação: app (carrinho), cadastrar (+), listar, ordem alfabética (A-Z), consultar (lupa), editar (lápis), remover (lixeira/X), compra parcial, compra total, adquiridos (check), pendentes (relógio), resumo ($), exemplos (caixa), sair, sucesso, erro, aviso, pergunta.
- Ícone da janela: `JOptionPane.setRootFrame(frame invisível com setIconImage)` para todos os diálogos herdarem o ícone do app.
- **Tela de boas-vindas** ao iniciar: ícone grande, nome do app, breve instrução.
- **Menu principal**: painel com grade de botões (ícone + texto + tooltip) dentro de um `JOptionPane`; cada botão chama `pane.setValue(...)` e fecha o diálogo. Cabeçalho com mini-resumo (itens, gasto, restante) e `JProgressBar` de progresso da compra. Botão de rodapé do `JOptionPane` = "Sair" (rótulo trocado via `UIManager` só durante o menu, ou outra técnica sem arrays). Fechar a janela = pedir confirmação de saída.
- **Submenu de consulta** no mesmo estilo (por código, nome exato, parte do nome, seção).
- **Formulário único de cadastro/edição** (`showConfirmDialog` com painel `GridBagLayout`): nome, seção (`JComboBox` editável com seções existentes + sugestões do bundle), unidade (`JComboBox` editável com sugestões), quantidade, preço estimado, e rótulo **"Valor estimado total" atualizado ao digitar** (`DocumentListener`). Erro de validação: mostra erro e **reabre o formulário com os valores preenchidos**.
- **Seleção de produto**: `JList` (modelo `DefaultListModel` preenchido percorrendo a nossa lista) com renderer mostrando ícone de status + "#código - nome - status", campo de filtro por texto acima da lista, duplo clique ou OK confirma. Lista vazia: aviso amigável.
- **Formulário de compra**: cartão com dados do produto (desejada, comprada, pendente, preço estimado), campos quantidade e preço pago (padrão = estimado), rótulo **"Total desta compra" ao vivo**. Compra total: quantidade fixa (desabilitada) = pendente. Após compra, mensagem de sucesso com novo status.
- **Relatórios**: `JTable` com `AbstractTableModel` próprio que lê a nossa `ListaDuplamenteEncadeada` (adicionar `obter(int indice)` O(n) na lista, documentado), renderer colorindo a coluna Status, valores alinhados à direita, rodapé com totais, dentro de `JScrollPane` no `JOptionPane`.
- **Resumo financeiro**: painel com rótulos em destaque (estimado, gasto, economia/excesso em verde/vermelho, restante, projeção) e `JProgressBar` de itens concluídos.
- Mensagens: sucesso (ícone check verde), erro (ícone vermelho), confirmação (ícone pergunta). Cancelar/fechar em qualquer tela continua voltando ao menu sem erro.
- Continua valendo: nenhum array explícito no nosso código (exceto `String[] args`), nenhum varargs, nenhum `java.util` fora de `I18n`.

### 10.4 Critérios de aceite adicionais
- [ ] Nenhum `-`/`-` no projeto.
- [ ] Nenhum literal de texto visível ao usuário em `src/main/java` (só chaves do bundle).
- [ ] Toda chave usada existe no properties e toda chave do properties é usada.
- [ ] `java.util` só em `I18n.java`.
- [ ] Testes continuam 100% OK.

## 11. Revisão 3 - ícones em arquivo, código limpo e camadas padrão (MVC)

### 11.1 Ícones como arquivos de imagem
- Ícones deixam de ser desenhados por código. Ficam em `src/main/resources/icones/*.png` (48x48, fundo transparente), um arquivo por ícone, nomes em minúsculo com `_` (ex.: `cadastrar.png`, `compra_parcial.png`, `app.png`).
- Gerar os PNGs uma única vez a partir do desenho atual (gerador temporário fora do projeto, apagado depois), para manter o visual aprovado.
- `view/Icones.java` só carrega: `new ImageIcon(Icones.class.getResource("/icones/" + nome + ".png"))`, versão 24px via `Image.SCALE_SMOOTH`. Arquivo ausente não derruba o app (ícone vazio).

### 11.2 Código limpo para avaliação do docente
- **Remover todos os comentários** (`//`, `/* */` e Javadoc) de `src/main` e `src/test`. O código deve se explicar pelos nomes; a explicação técnica (complexidade, justificativas) fica em `docs/DescricaoTecnica.md`.
- Nomes descritivos e completos (sem abreviações como `p`, `sb`, `qtd`, `tf`); métodos curtos (alvo até ~25 linhas), uma responsabilidade por método/classe; sem código morto, sem métodos/parametros não usados, sem lógica duplicada; complexidade baixa (evitar aninhamentos profundos, preferir retornos antecipados).
- Lambdas simples são permitidas.

### 11.3 Camadas e pacotes (padrão MVC + service, nomes de pacote em inglês minúsculo, classes em português)
```
br.unisul.mercadao
├── Main                                  (só inicia: configura L&F/i18n e chama o controller)
├── model           Produto, StatusProduto, ResumoFinanceiro
├── structure       ListaDuplamenteEncadeada, ArvoreBinariaBusca, Filtro, Acao
├── service         ListaComprasService (regras de negócio), DadosExemplo (produtos de exemplo)
├── exception       RegraNegocioException
├── controller      MenuController (fluxo: recebe a escolha da view, chama o service, abre a próxima view, trata exceções)
├── view            TelaBoasVindas, MenuPrincipalView, FormularioProdutoView, FormularioCompraView,
│                   SeletorProdutoView, RelatorioView, ResumoView, ProdutoTableModel, Dialogos, Icones
├── i18n            I18n
└── util            Formatador, Texto
src/main/resources/i18n/mensagens_pt_BR.properties
src/main/resources/icones/*.png
src/test/java/br/unisul/mercadao/TesteSistema.java
```
- Regras de dependência: `view` não chama `service` (só recebe dados/devolve entrada ao controller); `controller` orquestra; `service` não conhece Swing; `model` não conhece service/view; `structure` é genérica (não conhece `Produto`).
- Continua valendo tudo das seções 0-10 (sem arrays exceto `String[] args`, sem varargs, BigDecimal, `java.util` só em `I18n`, todo texto no bundle, nenhum travessão).
