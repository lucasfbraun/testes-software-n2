Estrutura do projeto
•	pom.xml
    o	Arquivo Maven que declara dependências e plugins do build.

•	java/src/main/java/br/university/biblioteca/
    o	Código de produção (classes do domínio e serviços).

•	PlaceholderTest.java
    o	Código de testes (JUnit 5). O placeholder garante que mvn test execute.

•	java/target/ (gerado pelo build)
    o	Binários compilados e relatórios (ex.: cobertura JaCoCo).

Ferramentas e bibliotecas do setup e por que são importantes
•	Maven
    o	Gerencia dependências, compila, roda testes e integra plugins (JaCoCo). Facilita CI.

•	JUnit 5 (junit-jupiter)
    o	Framework de testes. Suporta @Test, @BeforeEach/@AfterEach (fixtures) e testes parametrizados.

•	Mockito (mockito-junit-jupiter)
    o	Criação de mocks/stubs e verificação de interações. Necessário para dobrar dependências (Relogio, EmailService).

•	JaCoCo (plugin Maven)
    o	Mede cobertura de linhas e ramos e gera relatórios HTML/XML, atendendo a meta de cobertura e ao CI.

O pom.xml é o arquivo de configuração do Maven que define:

para gerar o teste em html:
mvn -f java/pom.xml test
start java/target/site/jacoco/index.html

Primeiro: mvn -f [pom.xml](http://_vscodecontentref_/0) test
Esse comando compila o código, roda os 19 testes implementados e gera automaticamente um relatório de cobertura usando o JaCoCo.

Segundo: start java/target/site/jacoco/index.html
Esse comando abre o relatório de cobertura no navegador, mostrando visualmente quais linhas e branches do código foram testadas, com porcentagens 

Mockito é uma biblioteca Java para criar objetos falsos (mocks/stubs) que imitam dependências em testes.

O que é Stub (em poucas palavras)
Stub é um objeto falso que retorna valores pré-definidos para simular o comportamento de uma dependência real nos testes.

 o	Objeto falso que retorna valores pré-definidos para simular dependências.
    o	Exemplo: RelogioStub permite controlar datas nos testes sem depender do relógio do sistema.
    o	Diferença para Mock: stub retorna valores; mock verifica chamadas de métodos.