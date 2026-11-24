# Descrição Técnica - Mercadão Unisul (Lista de Compras de Supermercado)

## 1. Visão geral

O sistema gerencia **uma única lista de compras** durante a execução. Permite cadastrar, consultar, localizar, editar e remover produtos, registrar compras **parciais** e **totais** e apresentar produtos adquiridos, pendentes e os valores **estimados** e **gastos**.

- Linguagem: Java 24, Maven, **sem dependências externas**.
- Dados **somente em memória** (nenhum arquivo, banco de dados ou serialização).
- Interface: Swing com **todas as telas dentro de `JOptionPane`** (menu em grade de botões com ícones, formulários, listas e tabelas), ícones carregados de arquivos PNG (`src/main/resources/icones`), sem leitura de console.
- Textos: todo texto visível vem do arquivo `i18n/mensagens_pt_BR.properties`, acessado pela classe `I18n`. O arquivo é somente ASCII: caracteres especiais usam sequências Unicode (`ã` para "ã"), o que garante a mesma leitura em qualquer sistema e codificação.
- As estruturas de dados foram **implementadas pela equipe** (a pilha) e a lista de produtos usa `java.util.List`. Não há vetores nem matrizes no código do sistema (a única exceção é o `String[] args` exigido pela assinatura do `main`). Do `java.util` usamos `List`/`ArrayList`, `Predicate` e, na `I18n`, o suporte a `ResourceBundle` (ver seção 4.5).
- Dinheiro e quantidades usam `BigDecimal` (nunca `double`/`float`).

## 2. Como executar

**IntelliJ IDEA:** abrir a pasta do projeto, usar o JDK 24 (Corretto 24) e executar a classe `br.unisul.mercadao.Main`.

**Linha de comando (sem Maven):**

```bash
mkdir out
javac -encoding UTF-8 -d out $(find src/main/java -name '*.java')
cp -r src/main/resources/* out/      # arquivo de mensagens (i18n)
java -cp out br.unisul.mercadao.Main
```

**Com Maven:** `mvn package` e depois `java -jar target/mercadao-1.0-SNAPSHOT.jar` (o `pom.xml` define a classe principal no manifesto).

**Testes (sem JUnit):**

```bash
javac -Xlint:all -encoding UTF-8 -d out-test $(find src/main/java src/test/java -name '*.java')
cp -r src/main/resources/* out-test/
java -cp out-test br.unisul.mercadao.TesteSistema
```

## 3. Arquitetura em camadas (MVC + service)

```
br.unisul.mercadao
|-- Main                  inicia: aplica o look and feel e chama o controller
|-- AppConfig             único lugar que cria as camadas e injeta as dependências
|-- controller/           MenuController            -> fluxo: recebe a escolha da view, chama o service,
|                                                      abre a próxima view e trata RegraNegocioException
|-- view/                 telas Swing (tudo em JOptionPane); sem regra de negócio
|                         TelaBoasVindas, MenuPrincipalView, ConsultaView, FormularioProdutoView,
|                         FormularioCompraView, SeletorProdutoView, RelatorioView, ResumoView,
|                         ProdutoTableModel, RenderizadorCelula, RenderizadorProduto, TipoRelatorio,
|                         TextosStatus, Dialogos, Componentes, Icones, Cores, OpcaoMenu, OpcaoConsulta
|-- service/              ListaComprasService       -> casos de uso: cadastrar, editar, remover, restaurar,
|                                                      comprar, desfazer compra, consultar (só orquestra)
|                         ResumoService             -> cálculo dos totais e do percentual
|                         DadosExemplo              -> produtos de exemplo para a demonstração
|-- validation/           ProdutoValidador          -> valida e normaliza os dados digitados
|-- repository/           ProdutoRepository         -> guarda a List de produtos e responde consultas simples
|                         HistoricoCompras          -> pilha das compras (desfazer)
|                         ProdutosRemovidos         -> pilha dos produtos removidos (restaurar)
|-- model/                Produto, StatusProduto, ResumoFinanceiro, CompraRegistrada, DadosProduto
|                                                    -> dados e regras do próprio produto (sem texto de tela)
|-- exception/            RegraNegocioException     -> erro de regra com mensagem amigável
|-- structure/            Pilha                     -> estrutura própria e genérica (não conhece Produto)
|-- util/                 Texto, Formatador, Conversor
|                                                    -> normalização de nomes, formatação e conversão de números digitados
`-- i18n/                 I18n                      -> textos do arquivo mensagens_pt_BR.properties

src/main/resources/i18n/mensagens_pt_BR.properties
src/main/resources/icones/*.png
src/test/java/br/unisul/mercadao/TesteSistema.java
```

**Regras de dependência:**

| Camada | Responsabilidade | Pode usar | Não pode ter |
|---|---|---|---|
| `view` | desenhar telas e devolver o que foi digitado | `model`, `util`, `i18n`, Swing | `service`, `repository`, regra de negócio |
| `controller` | fluxo entre telas e service; converte texto digitado | `view`, `service`, `model`, `util`, `exception`, `i18n` | Swing, regra de negócio |
| `service` | casos de uso (cadastrar, editar, remover, comprar, desfazer) | `repository`, `validation`, `model`, `exception`, `util`, `i18n` | Swing, classes internas, armazenamento próprio, cálculos auxiliares (vão para classes próprias) |
| `validation` | validar e normalizar dados | `model`, `exception`, `util`, `i18n` | alterar produtos |
| `repository` | guardar e consultar dados em memória | `model`, `structure`, `util` | regras de negócio, mensagens |
| `model` | dados e regras do próprio objeto | `util` | `service`, `view`, `controller`, `i18n`, Swing |
| `structure` | estruturas genéricas (pilha) | somente `java.lang` | qualquer outra camada |

**Regras para manter a estrutura (valem para todo código novo):**
1. Nenhuma classe, `record` ou `enum` aninhado em `service`, `controller`, `validation` e `repository`; cada tipo vai no seu arquivo e na sua camada (dados em `model`).
2. Em `view`, renderizadores e enums também ficam em arquivo próprio.
3. Camadas não criam suas próprias dependências: recebem pelo construtor, e só `AppConfig` faz os `new` das camadas.
4. `service` não guarda dados nem calcula resumos: guarda no `repository`, valida no `validation` e delega cálculos a classes de service próprias.
5. Texto de tela (`I18n` para rótulos, ícones, cores) fica em `view`; `model` não depende de `i18n`.

O fluxo de uma ação é: a `view` mostra a tela e devolve ao `controller` o que o usuário escolheu ou digitou (por exemplo uma `OpcaoMenu` ou os textos dos campos); o `controller` converte os textos (`Conversor`), chama o `service`, e abre a próxima `view` com o resultado; se o `service` lançar `RegraNegocioException`, o `controller` mostra a mensagem em `Dialogos.erro` e reabre o formulário com o que foi digitado.

**Ícones:** ficam em `src/main/resources/icones/*.png` (48 x 48, fundo transparente, nomes em minúsculo com `_`, por exemplo `cadastrar.png`, `compra_parcial.png`, `status_parcial.png`). A classe `view.Icones` apenas os carrega com `new ImageIcon(Icones.class.getResource("/icones/" + nome + ".png"))`; os tamanhos 16, 20, 24 (botões) são obtidos com `Image.SCALE_SMOOTH`. Se um arquivo não existir, o ícone fica vazio (transparente) e o sistema continua funcionando. Os PNGs foram gerados uma única vez a partir do desenho original feito em código, para manter o visual aprovado.

## 4. Estruturas de dados escolhidas

### 4.1 Lista (`java.util.List`, implementação `ArrayList`)

**O que é:** sequência de elementos acessível por posição. O código usa a interface `List<Produto>` e só instancia `ArrayList` nos pontos de criação; a classe é da biblioteca padrão e não há vetores (`[]`) no código da equipe.

**Por que foi escolhida:**
- A lista de compras cresce e diminui durante a execução; `List` tem **tamanho dinâmico**.
- Cadastrar um produto é `add` no fim, preservando a **ordem de cadastro**.
- Remover um produto é `remove(produto)`; percorrer, filtrar e somar são `for` simples, fáceis de explicar e depurar.
- Os relatórios recebem a mesma `List<Produto>`; o modelo da tabela usa `get(linha)` direto.

### 4.1.1 Pilha (`Pilha<T>`) - histórico de compras

**O que é:** estrutura LIFO (último a entrar, primeiro a sair), implementada pela equipe com nós encadeados (`empilhar`, `desempilhar`, `espiar`, `estaVazia`, `tamanho`), sem vetor.

**Onde é usada:** cada compra registrada (parcial ou total) é empilhada em `ListaComprasService` (com confirmação antes de desfazer) como `CompraRegistrada(produto, quantidade, precoPago)`. A opção de menu **Desfazer última compra** desempilha a compra mais recente e a reverte no produto (`Produto.desfazerCompra`). Uma segunda pilha guarda os produtos removidos: a opção **Restaurar produto removido** devolve o mais recente à lista (na posição do seu código), recusando se o nome já foi reutilizado. Uma pilha é a estrutura natural porque a compra mais recente é sempre a primeira a ser desfeita, como devolver o último item colocado no carrinho.

### 4.2 Busca e ordenação por nome (sobre a própria `List`)

Não há índice separado: o nome é uma propriedade do `Produto` e a lista é a única fonte de dados.
- **Nome único e busca por nome:** percorre a lista comparando o nome normalizado (sem acento, minúsculo, espaços colapsados), então "Arroz", " arroz " e "ÁRROZ" são o mesmo produto.
- **Ordem alfabética:** copia a lista e aplica `List.sort` com o nome normalizado como critério; a lista de cadastro não é alterada.
- **Por que sem estrutura extra:** para o tamanho de uma lista de compras o custo O(n) é imperceptível, e não existe segunda estrutura para manter sincronizada.

### 4.3 Estruturas usadas

A **lista** guarda os produtos e suporta percursos, filtros e ordenação; a **pilha** guarda o histórico de compras e de remoções para desfazer.

### 4.4 Diagrama

```
   List<Produto> (ArrayList)        (ordem de cadastro)
   [ Produto #1 | Produto #2 | Produto #3 | ... ]
        |
        +--> filtros (seção, trecho, adquiridos, pendentes): percorrem e devolvem nova List
        +--> ordem alfabética: copia e ordena pelo nome normalizado

   Pilha<CompraRegistrada>          Pilha<Produto>
   topo: última compra              topo: último removido
        (desfazer compra)                (restaurar produto)
```

Pilhas e lista referenciam os **mesmos** objetos `Produto`; não há cópias que possam divergir.

### 4.5 Bibliotecas padrão permitidas

Estão liberados `java.util.List`/`ArrayList` (e `java.util.function.Predicate`). Continuam proibidos vetores e matrizes (`[]`, `toCharArray`, `split`, varargs) no código da equipe, com a única exceção do `String[] args` do `main`.

## 5. Complexidade (Big-O)

n = número de produtos.

| Operação | Estrutura usada | Complexidade |
|---|---|---|
| Cadastrar produto | lista (verifica nome e insere no fim) | O(n) |
| Inserir no fim da lista | lista | O(1) |
| Buscar por código | lista | O(n) |
| Buscar por nome exato | lista (percorre) | O(n) |
| Buscar por trecho do nome | lista (filtro) | O(n) |
| Listar por seção | lista (filtro) | O(n) |
| Listar em ordem de cadastro | lista | O(n) |
| Listar em ordem alfabética | lista (cópia + `sort`) | O(n log n) |
| Listar adquiridos / pendentes | lista (filtro) | O(n) |
| Editar (sem trocar nome) | lista (localiza por código) | O(n) |
| Editar (trocando nome) | lista (confere nome único) | O(n) |
| Remover produto | lista (+ empilha em removidos) | O(n) |
| Registrar compra parcial/total | lista (localiza por código) | O(n) |
| Resumo financeiro | lista (percorre tudo) | O(n) |
| Obter o i-ésimo elemento (`get(int)`) | lista | O(1) |
| Desfazer última compra | pilha (desempilha) + produto | O(1) |
| Restaurar produto removido | pilha + inserção por código na lista | O(n) |
| Tamanho / lista vazia | contador | O(1) |

### 5.1 Complexidade das operações das estruturas

**`Pilha<T>`** (cada nó guarda o valor e o nó abaixo; a pilha guarda `topo` e `tamanho`):

| Operação | Complexidade | Observação |
|---|---|---|
| `empilhar` | O(1) | novo nó vira o topo |
| `desempilhar` | O(1) | devolve o topo (ou `null` se vazia) |
| `espiar` | O(1) | lê o topo sem remover |
| `tamanho`, `estaVazia` | O(1) | contador |

**`java.util.ArrayList`:** `add` no fim O(1) amortizado; `get` O(1); `remove(objeto)` O(n); percursos e filtros O(n).

**`ListaComprasService`:** cadastrar, buscar por nome ou código, filtros, comprar, editar, remover e resumo são O(n); listar em ordem alfabética O(n log n). As operações validam todos os dados antes de alterar qualquer estrutura. `Texto.normalizar` custa O(L), com L = tamanho do texto.

A busca por código usa a lista (O(n)) porque o código é usado apenas na seleção de produtos; para listas de compras o custo é desprezível. Um índice por código seria uma melhoria possível, mas duplicaria a manutenção de estruturas sem ganho perceptível.

## 6. Regras de negócio e consistência

### 6.1 Modelo `Produto`

Campos: `id` (sequencial a partir de 1, **nunca reutilizado**), `nome`, `secao`, `unidade`, `quantidadeDesejada`, `precoEstimadoUnitario`, `quantidadeComprada` (inicia 0) e `valorGasto` (inicia 0,00).

Valores **derivados** (calculados, nunca guardados): quantidade pendente = desejada − comprada; valor estimado total; valor estimado pendente; valor estimado do comprado; status:

| Condição | Status |
|---|---|
| comprada = 0 | PENDENTE |
| 0 < comprada < desejada | PARCIAL |
| comprada = desejada | COMPRADO |

`quantidadeComprada` e `valorGasto` só mudam por `registrarCompra`, que soma a quantidade e `quantidade × preço pago` (arredondado em 2 casas, HALF_UP). Essa conta existe em um único lugar, `Produto.calcularValor`, reutilizado pelos valores estimados e pelo "total ao vivo" digitado nos formulários. `possuiCompra()` e `faltaComprar()` concentram os critérios usados pelas listagens de adquiridos e pendentes e pela regra de remoção.

### 6.2 Validações (mensagens amigáveis em português)

- Nome obrigatório, máx. 60 caracteres, **único** (comparação normalizada).
- Seção obrigatória (máx. 30) e unidade obrigatória (máx. 10).
- Quantidade desejada > 0 com no máximo 3 casas; preço estimado ≥ 0 com no máximo 2 casas.
- Compra parcial: quantidade > 0 e ≤ pendente; preço pago ≥ 0; produto não pode estar COMPRADO.
- Compra total: compra exatamente o pendente; o preço sugerido é o estimado, mas o usuário pode alterar.
- Edição: a nova quantidade desejada não pode ser menor que a já comprada; nome continua único.
- Remoção: só se nada foi comprado (preserva o valor gasto), com confirmação.

### 6.3 Como a consistência é garantida

1. **Validar antes de alterar:** toda operação valida todos os dados antes de mexer em qualquer estrutura, então nunca fica "meio atualizada".
2. **Fonte única de dados:** só a lista guarda os produtos; nome único é conferido contra a própria lista (ignorando o produto sendo editado).
3. **Objetos compartilhados:** lista e pilhas guardam o mesmo `Produto`; não há cópias que possam divergir.
4. **Totais nunca em cache:** `calcularResumo()` percorre a lista a cada chamada; é impossível um total ficar desatualizado.
5. **`BigDecimal`:** sem erros de ponto flutuante; escala 2 para dinheiro, 3 para quantidade, `RoundingMode.HALF_UP`.
6. **Encapsulamento:** quantidade comprada e valor gasto não possuem setters públicos.

### 6.4 Resumo financeiro

Total de itens (e quantos pendentes, parciais e comprados); **valor estimado total**; **valor gasto**; **estimado do que já foi comprado**; **diferença** (estimado do comprado − gasto; positivo = economia, negativo = excesso); **estimado restante**; **projeção final** (gasto + estimado restante); **percentual concluído** (itens totalmente comprados ÷ total).

## 7. Decisões adicionais

| Decisão | Justificativa |
|---|---|
| `BigDecimal` para dinheiro e quantidade | `double` não representa 0,1 exatamente; erros acumulariam nos totais |
| Totais recalculados sempre | elimina inconsistência entre itens e totais |
| Remoção bloqueada após compra | remover apagaria valor gasto e distorceria o resumo |
| Nome normalizado como chave | "Arroz" e " ÁRROZ " são o mesmo produto; evita duplicatas |
| Campo "seção" | permite **localizar** o item no supermercado (consulta por seção) |
| Código sequencial nunca reutilizado | evita confundir produtos removidos com novos |
| Editar e remover produto | cobre o requisito de "atualização" dos produtos |
| Dados de exemplo (botão "Dados de exemplo") | facilita a demonstração e os testes manuais |
| `JOptionPane` como contêiner de todas as telas | atende "interface simples" com visual rico (grade de botões, formulários, tabelas); cancelar volta ao menu sem alterar nada |
| Ícones em arquivos PNG (`resources/icones`) | o código de desenho saiu das classes; `view.Icones` só carrega, escala com `SCALE_SMOOTH` e não derruba o app se faltar um arquivo |
| Arquitetura MVC + service (`controller`, `view`, `service`, `model`, `structure`) | a view não conhece o service e o service não conhece Swing; cada camada tem uma responsabilidade |
| Sem comentários no código | nomes descritivos e métodos curtos explicam o código; as explicações técnicas (complexidade, justificativas) ficam neste documento |
| Todos os textos em `mensagens_pt_BR.properties` | internacionalização padrão do Java; facilita revisar e corrigir textos num só lugar |
| Árvore recursiva e não balanceada | código mais simples de explicar; limitação documentada |

### 7.1 Interface (JOptionPane com Swing)

- **Contêiner único:** todo diálogo é um `JOptionPane` (criado em `view.Dialogos`; os componentes que vão dentro dele são montados por `view.Componentes`). O conteúdo (painéis, listas, tabelas) é passado como componente de mensagem; botões de menu fecham o painel com `pane.setValue(...)`. Os rótulos de rodapé ("Sair", "Voltar", "Fechar", "Começar") são trocados por `UIManager` só durante a criação de cada diálogo, sem usar vetor de opções.
- **Ícone da janela:** `JOptionPane.setRootFrame(frame invisível)` com o ícone do aplicativo; todos os diálogos herdam. Look and feel do sistema operacional.
- **Ícones:** `Icones` carrega os PNGs de `resources/icones` (quadrado arredondado colorido com símbolo branco) e os usa nos tamanhos 16, 20, 24 (botões), 48 (diálogos e boas-vindas). Há ícones de sucesso (check verde), erro (X vermelho), aviso, pergunta e de situação do produto (relógio, meio círculo, check).
- **Boas-vindas** (nome do aplicativo, ícone grande e instrução) seguida do **menu principal**: grade 3 x 4 de botões com ícone, texto e dica (tooltip), mini-resumo (itens, gasto, falta estimada) e `JProgressBar` com o percentual de itens comprados. O botão de rodapé é "Sair"; fechar a janela também pede confirmação de saída.
- **Consulta:** submenu no mesmo estilo (`ConsultaView`: código, nome exato, parte do nome, seção).
- **Formulário único** de cadastro e edição (`FormularioProdutoView`): nome, seção e unidade (`JComboBox` editáveis com sugestões), quantidade, preço e "Valor estimado total" atualizado a cada tecla (`DocumentListener`). Em caso de erro de validação a mensagem é exibida e o formulário **reabre com os valores digitados**.
- **Seleção de produto** (`SeletorProdutoView`): `JList` alimentada percorrendo a nossa lista, com filtro por texto, ícone de situação e "#código - nome - situação"; OK, Enter ou duplo clique confirmam.
- **Compra** (`FormularioCompraView`): cartão com os dados do produto, campos quantidade e preço pago (padrão: o estimado) e "Total desta compra" ao vivo. Na compra total a quantidade fica travada no que falta.
- **Relatórios:** `JTable` (`RelatorioView`) com `ProdutoTableModel` (estende `AbstractTableModel` e lê a `List<Produto>` via `get(int)`), coluna Status colorida com ícone, números à direita e rodapé de totais.
- **Resumo financeiro** (`ResumoView`): valores em destaque, economia em verde ou excesso em vermelho e barra de progresso.
- **Cancelar ou fechar** qualquer tela volta ao menu sem alterar nada (nunca há `NullPointerException`).

### 7.2 Internacionalização (i18n)

- Arquivo `src/main/resources/i18n/mensagens_pt_BR.properties` (somente ASCII, com acentos escritos como `\uXXXX`, sem comentários), somente pt-BR. Chaves agrupadas por prefixo (`app.*`, `menu.*`, `dica.*`, `consulta.*`, `form.*`, `selecao.*`, `compra.*`, `coluna.*`, `relatorio.*`, `resumo.*`, `status.*`, `erro.*`, `sucesso.*`, `aviso.*`, `confirmacao.*`, `exemplo.*`, `secao.*`, `unidade.*`, `formato.*`, `botao.*`, `titulo.*`, `campo.*`).
- `I18n.t(chave...)` devolve o texto; chave inexistente devolve `!chave!` (fica visível na tela e nos testes).
- Vêm do arquivo: textos da interface, mensagens de `RegraNegocioException`, descrições de `StatusProduto`, cabeçalhos de coluna, nomes dos dados de exemplo, textos padrão do `JOptionPane` (OK, Cancelar, Sim, Não) e símbolos de formatação (`R$`, separador decimal e de milhar).
- Como o arquivo vai junto no `.jar`/`out`, ao compilar manualmente é preciso copiar `src/main/resources` para a pasta de saída (ver seção 2).

## 8. Roteiro de demonstração (apresentação)

1. Abrir o sistema. Mostrar a **tela de boas-vindas** e clicar em "Começar". No menu, mostrar o mini-resumo (itens: 0), a barra de progresso e passar o mouse sobre alguns botões para exibir as dicas.
2. **Dados de exemplo** - carregar os 14 produtos. Clicar de novo: avisa que nenhum produto novo foi inserido (nomes já existem).
3. **Listar (ordem de cadastro)** e **Listar (ordem alfabética)** - tabelas com status colorido e totais no rodapé. Comentar a lista e a ordenação por nome.
4. **Cadastrar produto** - preencher "Sabão em pó" (Limpeza, un, 2, 12,50) e observar o "Valor estimado total" mudando ao digitar. Cadastrar de novo " SABÃO EM PÓ ": o erro de nome duplicado aparece e o formulário **reabre com os valores preenchidos**.
5. **Consultar produto** - por código; por nome exato (sem acento, ex.: "cafe torrado"); por parte do nome ("leite"); por seção ("hortifruti").
6. **Editar produto** - escolher "Tomate" na lista (usar o filtro e o duplo clique), aumentar a quantidade para 3 e renomear para "Tomate italiano". Consultar o nome antigo (não encontra) e o novo (encontra).
7. **Compra parcial** - "Leite integral", 5 de 12 a R$ 5,20 (ver o "Total desta compra" ao vivo). Status vira Parcial. Nova compra parcial de 4. Tentar comprar 10 (excede o pendente) e mostrar a mensagem de erro com o formulário reaberto.
8. **Compra total** - "Arroz 5kg": a quantidade vem travada; alterar o preço para 26,90. Status vira Comprado. O produto deixa de aparecer na seleção de compras.
9. **Produtos adquiridos** (comprados e parciais, com quantidade e valor gasto) e **Produtos pendentes** (pendentes e parciais).
10. **Resumo financeiro** - estimado total, gasto, economia (verde) ou excesso (vermelho), restante, projeção e barra de progresso. Conferir um número manualmente.
11. **Remover produto** - tentar remover um já comprado (rejeitado, com explicação) e remover um sem compra (confirmação; some da lista ).
12. Mostrar o **cancelamento**: abrir um cadastro e clicar em Cancelar (volta ao menu, nada muda). Clicar em **Sair** e mostrar o aviso de que os dados serão perdidos.
13. (Opcional) Executar `TesteSistema` com todas as verificações em OK.

## 9. Mapeamento regra → funcionalidade

| ID | Regra (Documentacao.md) | Onde é atendida |
|---|---|---|
| R1 | Cadastrar produtos | Botão "Cadastrar produto" → `ListaComprasService.cadastrar` |
| R2 | Consultar e localizar itens | Botões de listagem e "Consultar produto" (código, nome exato, trecho, seção) |
| R3 | Registrar compras totais | Botão "Compra total" → `ListaComprasService.comprarTotal` |
| R4 | Registrar compras parciais | Botão "Compra parcial" → `ListaComprasService.comprar` (compras parciais acumulam) |
| R5 | Informação de produtos adquiridos | Botão "Produtos adquiridos" (comprados e parciais, com quantidade e valor gasto) |
| R6 | Informação de produtos pendentes | Botão "Produtos pendentes" (pendentes e parciais, com quantidade e valor estimado restante) |
| R7 | Valores estimados e gastos | Botão "Resumo financeiro" e colunas dos relatórios |
| R8 | Estruturas adequadas, sem vetor/matriz | `java.util.List`, pilha (histórico de compras / desfazer), sem vetor/matrizs |
| R9 | Descrição técnica e justificativa | este documento |
| R10 | Atualização de produtos | Botões "Editar produto" e "Remover produto" |
| R11 | Única lista de compras na execução | `ListaComprasService` instanciada uma única vez no `Main` |
| R12 | Somente memória, sem arquivo/BD | nenhum uso de classes de arquivo ou banco no código |
| R13 | Dados e cálculos consistentes | validações, `BigDecimal`, totais recalculados, lista como fonte única |
| R14 | Interface simples que demonstre as funções | menu em grade de botões com ícones e dicas, todas as telas em `JOptionPane` |
| R15 | Decisões adicionais explicadas | seção 7 |
| R16 | Apresentação e demonstração | seção 8 e botão "Dados de exemplo" |

## 10. Testes

`TesteSistema` (sem framework) cobre: operações da pilha (empilhar, desempilhar, LIFO, vazia) e desfazer compra; cadastro e validações; compras parciais e total com transição de status; valores do resumo; edição (renomear atualiza o índice, limite da quantidade); remoção (bloqueio após compra, sincronização, ids não reutilizados); buscas por trecho e seção ignorando acento e maiúsculas; `obter(int)` da lista; o `Conversor` (decimais com vírgula ou ponto, entradas inválidas); o resumo de uma lista filtrada; e a internacionalização (chaves carregadas com acentos, substituição de `{0}`, `{1}` e `{2}`, chave ausente devolvendo `!chave!`, mensagens de exceção vindas do arquivo) e a formatação de moeda, quantidade e porcentagem. Imprime `OK`/`FALHOU` por verificação e encerra com código 1 se houver falha.
