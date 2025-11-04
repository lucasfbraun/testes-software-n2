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

