# Mercadão Unisul - Sistema de Lista de Compras de Supermercado

Aplicação desktop (interface gráfica **Swing**) em **Java 24 + Maven** para gerenciar uma lista de compras de supermercado. Projeto acadêmico de Estruturas de Dados (Unisul): os dados ficam **apenas em memória** e as estruturas (lista duplamente encadeada e árvore binária de busca) são implementadas manualmente, sem usar `java.util.List`/`Map` como estrutura principal.

## Funcionalidades

- Cadastrar, editar e remover produtos
- Listar por ordem de cadastro e em ordem alfabética
- Consultar por código, nome, trecho do nome ou seção
- Registrar compra parcial ou total
- Listar produtos adquiridos e pendentes
- Resumo financeiro (valor estimado × valor gasto)
- Carregar dados de exemplo

## Estrutura do projeto

```
MercadaoUnisul/
├── pom.xml                         # Configuração Maven (Java 24, UTF-8)
├── docs/                           # Enunciado, descrição técnica e plano de implementação
└── src/
    ├── main/
    │   ├── java/br/unisul/mercadao/
    │   │   ├── Main.java           # Ponto de entrada (classe principal)
    │   │   ├── controller/         # Controle do menu
    │   │   ├── model/              # Produto, StatusProduto, ResumoFinanceiro
    │   │   ├── service/            # Regras de negócio e dados de exemplo
    │   │   ├── structure/          # Pilha (desfazer compra) e árvore binária de busca
    │   │   ├── view/               # Telas Swing
    │   │   ├── util/ i18n/ exception/
    │   └── resources/              # Ícones (icones/) e textos (i18n/mensagens_pt_BR.properties)
    └── test/java/br/unisul/mercadao/
        └── TesteSistema.java       # Testes (classe com main, sem JUnit)
```

---

## 1. Pré-requisitos

| Item | Versão | Observação |
|------|--------|------------|
| **JDK** | **24 ou superior** | Obrigatório: o `pom.xml` usa `maven.compiler.source/target = 24`. Precisa ser JDK (não JRE). |
| **Maven** | 3.9+ | Opcional nas IDEs (ambas trazem Maven embutido), necessário só para rodar pela linha de comando. |
| **IntelliJ IDEA** | 2024.3+ (Community ou Ultimate) | Versões antigas não suportam Java 24. |
| **Eclipse** | 2025-03 ou superior (Eclipse IDE for Java Developers) | Versões antigas não suportam Java 24. Já inclui o m2e (Maven). |

### Instalar o JDK 24

1. Baixe em <https://jdk.java.net/24/> (OpenJDK) ou <https://adoptium.net/> (Temurin 24) ou <https://www.oracle.com/java/technologies/downloads/>.
2. Instale/extraia (ex.: `C:\Program Files\Java\jdk-24`).
3. Confirme no terminal:
   ```bash
   java -version
   javac -version
   ```
   Ambos devem mostrar versão `24` ou maior.

> Opcional: defina a variável de ambiente `JAVA_HOME` apontando para a pasta do JDK e adicione `%JAVA_HOME%\bin` ao `PATH`.

### Obter o projeto

```bash
git clone <URL-DO-REPOSITORIO>
```
ou baixe o `.zip` e extraia. **Evite caminhos com acentos/espaços** quando possível (ex.: `C:\projetos\MercadaoUnisul`).

---

## 2. Rodando no IntelliJ IDEA

### 2.1 Abrir o projeto

1. Abra o IntelliJ IDEA.
2. Menu **File -> Open...** (ou **Open** na tela inicial).
3. Selecione a **pasta raiz** do projeto (a que contém o `pom.xml`, **não** a pasta `src`).
4. Clique em **OK**. Se perguntar, escolha **Trust Project**.
5. O IntelliJ detecta o `pom.xml` e importa como projeto Maven automaticamente. Aguarde a barra de progresso (canto inferior direito) terminar de baixar/indexar.
   - Se não importar sozinho: clique com o botão direito no `pom.xml` -> **Add as Maven Project**. Ou abra a aba **Maven** (lateral direita) -> botão **Reload All Maven Projects** (ícone de setas circulares).

### 2.2 Configurar o JDK 24

1. **File -> Project Structure...** (`Ctrl+Alt+Shift+S`).
2. Aba **Project**:
   - **SDK**: selecione o JDK 24. Se não aparecer: **Add SDK -> Add JDK from disk...** e escolha a pasta de instalação (ou **Download JDK...** -> versão 24, vendor à sua escolha).
   - **Language level**: **24**.
3. Aba **Modules**: confirme que o módulo `mercadao` usa o mesmo SDK e *Language level* 24.
4. Clique em **Apply -> OK**.
5. Verifique também o JDK usado pelo Maven: **File -> Settings -> Build, Execution, Deployment -> Build Tools -> Maven -> Runner** -> campo **JRE** = *Use Project JDK* (ou JDK 24).
6. E o compilador: **Settings -> Build, Execution, Deployment -> Compiler -> Java Compiler** -> *Project bytecode version* = **24**.

### 2.3 Executar a aplicação

**Opção A - pelo botão da classe `Main`:**

1. No painel **Project**, navegue: `src/main/java/br/unisul/mercadao/Main.java`.
2. Abra o arquivo e clique no **ícone verde ▶** ao lado de `public class Main` ou `public static void main`.
3. Escolha **Run 'Main.main()'**.
4. A janela Swing (tela de boas-vindas e menu principal) deve abrir.

**Opção B - atalho:** com `Main.java` aberto, pressione `Ctrl+Shift+F10`.

**Opção C - configuração fixa:**

1. **Run -> Edit Configurations...** -> **+** -> **Application**.
2. **Name**: `Mercadao`.
3. **Build and run**: JDK 24, *module* `mercadao`, **Main class**: `br.unisul.mercadao.Main`.
4. **Apply -> OK** e execute com `Shift+F10`.

### 2.4 Executar os testes (`TesteSistema`)

Os testes são uma classe com `main` (não usam JUnit):

1. Abra `src/test/java/br/unisul/mercadao/TesteSistema.java`.
2. Clique no **▶** ao lado do `main` -> **Run 'TesteSistema.main()'**.
3. O console (aba **Run**) exibe o resultado das verificações.

### 2.5 Gerar o JAR executável (opcional)

1. Abra a aba **Maven** (lateral direita) -> `mercadao` -> **Lifecycle**.
2. Duplo clique em **clean** e depois em **package**.
3. O JAR é gerado em `target/mercadao-1.0-SNAPSHOT.jar`.
4. Execute no terminal:
   ```bash
   java -jar target/mercadao-1.0-SNAPSHOT.jar
   ```

### 2.6 Problemas comuns no IntelliJ

| Sintoma | Solução |
|---------|---------|
| `Error: java: invalid source release: 24` | JDK do projeto/Maven/compilador não é 24. Revise os itens 2.2 (passos 2, 5 e 6). |
| `Cannot resolve symbol` / classes em vermelho | Aba Maven -> **Reload All Maven Projects**; depois **File -> Invalidate Caches -> Invalidate and Restart**. |
| Pasta `src/main/java` sem cor azul | Clique direito em `src/main/java` -> **Mark Directory as -> Sources Root**; `src/test/java` -> **Test Sources Root**; `src/main/resources` -> **Resources Root**. |
| Acentos quebrados | **Settings -> Editor -> File Encodings** -> *Project Encoding* = **UTF-8**. |
| Ícones/textos não aparecem | Rode via **Maven -> compile** ou *Build -> Rebuild Project* para copiar `resources` para `target/classes`. |
| Janela não abre / erro de *headless* | Execute em ambiente com interface gráfica (não via SSH/WSL sem display). |

---

## 3. Rodando no Eclipse

> Use o **Eclipse IDE for Java Developers** (2025-03 ou superior) com **JDK 24** instalado.

### 3.1 Registrar o JDK 24 no Eclipse

1. **Window -> Preferences -> Java -> Installed JREs**.
2. Clique em **Add... -> Standard VM -> Next**.
3. **JRE home**: **Directory...** e selecione a pasta do JDK 24 (ex.: `C:\Program Files\Java\jdk-24`). **Finish**.
4. Marque a caixa do JDK 24 (para ser o padrão) -> **Apply**.
5. Em **Java -> Compiler**, defina **Compiler compliance level** = **24** -> **Apply and Close**.

> Se o nível 24 não aparecer, atualize o Eclipse para a versão mais recente (**Help -> Check for Updates**) ou instale o suporte Java 24 via **Help -> Eclipse Marketplace** (busque "Java 24 Support").

### 3.2 Importar o projeto Maven

1. **File -> Import...**.
2. Expanda **Maven -> Existing Maven Projects -> Next**.
3. **Root Directory -> Browse...** e selecione a pasta raiz do projeto (a que contém o `pom.xml`).
4. Confirme que `/pom.xml` (`br.unisul:mercadao:1.0-SNAPSHOT`) está marcado -> **Finish**.
5. Aguarde o m2e importar e compilar (barra de progresso no canto inferior direito).

### 3.3 Ajustar o JDK do projeto

1. Clique com o botão direito no projeto -> **Properties**.
2. **Java Build Path -> Libraries**: se aparecer *JRE System Library [JavaSE-xx]* com versão errada, selecione-a -> **Edit... -> Alternate JRE** -> escolha o JDK 24 -> **Finish**.
3. **Java Compiler**: desmarque *Use compliance from execution environment* se necessário e defina **24**.
4. **Apply and Close**.
5. Clique direito no projeto -> **Maven -> Update Project...** (`Alt+F5`) -> marque o projeto -> **OK**. Isso reaplica a versão 24 do `pom.xml`.

### 3.4 Executar a aplicação

1. No **Package Explorer**, navegue: `src/main/java -> br.unisul.mercadao -> Main.java`.
2. Clique com o botão direito em `Main.java` -> **Run As -> Java Application**.
3. A janela Swing deve abrir.

Para repetir a execução: botão **Run** (▶) na barra de ferramentas ou `Ctrl+F11`.

### 3.5 Executar os testes (`TesteSistema`)

1. Navegue: `src/test/java -> br.unisul.mercadao -> TesteSistema.java`.
2. Botão direito -> **Run As -> Java Application**.
3. O resultado aparece na aba **Console**.

### 3.6 Gerar o JAR executável (opcional)

1. Botão direito no projeto -> **Run As -> Maven build...**.
2. Em **Goals**, digite `clean package` -> **Run**.
3. JAR gerado em `target/mercadao-1.0-SNAPSHOT.jar`. Se não aparecer, selecione o projeto e pressione `F5` (Refresh).
4. Execute:
   ```bash
   java -jar target/mercadao-1.0-SNAPSHOT.jar
   ```

### 3.7 Problemas comuns no Eclipse

| Sintoma | Solução |
|---------|---------|
| Erro `Unsupported release version 24` / `release version 24 not supported` | Compilador ou JRE do projeto não é 24. Refaça 3.1 e 3.3. |
| Projeto com **X vermelho** logo após importar | **Maven -> Update Project...** (`Alt+F5`) com *Force Update of Snapshots* marcado. |
| `Could not find or load main class br.unisul.mercadao.Main` | Rode **Project -> Clean...**; confirme que `src/main/java` é *Source Folder* (Build Path -> Configure Build Path -> Source). |
| Acentos quebrados | **Window -> Preferences -> General -> Workspace -> Text file encoding** = **UTF-8** (ou clique direito no projeto -> Properties -> Resource). |
| Ícones/textos não carregam | Garanta que `src/main/resources` está no Build Path como *Source Folder*; rode **Project -> Clean**. |
| Janela não abre / `HeadlessException` | Execute em ambiente com interface gráfica. |

---

## 4. Rodando pela linha de comando (opcional)

Com JDK 24 e Maven instalados, na pasta raiz:

```bash
mvn clean package
java -jar target/mercadao-1.0-SNAPSHOT.jar
```

Executar os testes manualmente:

```bash
java -cp target/classes;target/test-classes br.unisul.mercadao.TesteSistema
```
*(Em Linux/macOS use `:` no lugar de `;`. Se `target/test-classes` não existir, rode `mvn test-compile` antes.)*

---

## 5. Como usar o sistema

1. Ao iniciar, aparece a **tela de boas-vindas**; avance para o **menu principal**.
2. Para testar rápido, use **Carregar exemplos** e preencha a lista com produtos de demonstração.
3. Opções do menu:
   - **Cadastrar** produto (código, nome, seção, quantidade, preço estimado)
   - **Listar** por ordem de cadastro ou alfabética
   - **Consultar** por código, nome, trecho ou seção
   - **Editar** / **Remover** produto
   - **Compra parcial** (quantidade comprada e preço pago) e **compra total**
   - **Adquiridos** / **Pendentes**
   - **Resumo** financeiro (estimado × gasto)
   - **Sair**

> Os dados **não são salvos**: ao fechar o programa, a lista é perdida (requisito do enunciado).
