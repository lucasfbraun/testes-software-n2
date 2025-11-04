package br.university.biblioteca.service;

import br.university.biblioteca.model.Emprestimo;
import br.university.biblioteca.model.Livro;
import br.university.biblioteca.model.Usuario;
import br.university.biblioteca.repository.InMemoryEmprestimoRepository;
import br.university.biblioteca.stub.EmailServiceStub;
import br.university.biblioteca.stub.RelogioStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VerificadorVencidosTest {
    private RelogioStub relogio;
    private InMemoryEmprestimoRepository repository;
    private EmprestimoService emprestimoService;
    private EmailServiceStub emailStub;
    private VerificadorVencidos verificador;

    private Livro livro;
    private Usuario usuario;
    private LocalDate hoje;

    @BeforeEach
    void setUp() {
        hoje = LocalDate.of(2024, 11, 10);
        relogio = new RelogioStub(hoje);
        repository = new InMemoryEmprestimoRepository();
        emprestimoService = new EmprestimoService(relogio, repository);
        emailStub = new EmailServiceStub();
        verificador = new VerificadorVencidos(relogio, repository, emprestimoService, emailStub);
        livro = new Livro(1L, "Clean Code", 2.0);
        usuario = new Usuario(1L, "Lucas");
    }

    @Test
    void deveAplicarMultaEEnviarEmailParaVencidos() {
        Emprestimo e = emprestimoService.criarEmprestimo(usuario, livro, 7);
        relogio.avancarDias(10); // 3 dias de atraso

        verificador.processarVencidos();

        Emprestimo salvo = repository.buscarPorId(e.getId()).orElseThrow();
        assertEquals(0.06, salvo.getMulta(), 0.001);
        assertEquals(1, emailStub.quantidadeEnviados());
    }

    @Test
    void naoDeveEnviarEmailNemAplicarMultaSeNaoVencido() {
        Emprestimo e = emprestimoService.criarEmprestimo(usuario, livro, 7);

        verificador.processarVencidos();

        Emprestimo salvo = repository.buscarPorId(e.getId()).orElseThrow();
        assertEquals(0.0, salvo.getMulta(), 0.001);
        assertEquals(0, emailStub.quantidadeEnviados());
    }

    @ParameterizedTest
    @CsvSource({
        "1, 0.02",
        "5, 0.10",
        "30, 0.60",
        "31, 0.60"
    })
    void deveCalcularMultaCorretamenteParaAtrasos(int diasAposPrevisto, double multaEsperada) {
        Emprestimo e = emprestimoService.criarEmprestimo(usuario, livro, 7);
        relogio.avancarDias(7 + diasAposPrevisto); // hoje -> previsto + diasAposPrevisto

        verificador.processarVencidos();

        Emprestimo salvo = repository.buscarPorId(e.getId()).orElseThrow();
        assertEquals(multaEsperada, salvo.getMulta(), 0.001);
        assertEquals(1, emailStub.quantidadeEnviados());
    }
}
