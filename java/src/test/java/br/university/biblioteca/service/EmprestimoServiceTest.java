package br.university.biblioteca.service;

import br.university.biblioteca.model.Emprestimo;
import br.university.biblioteca.model.Livro;
import br.university.biblioteca.model.Usuario;
import br.university.biblioteca.repository.InMemoryEmprestimoRepository;
import br.university.biblioteca.stub.RelogioStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoServiceTest {
    private EmprestimoService service;
    private RelogioStub relogio;
    private InMemoryEmprestimoRepository repository;
    private Livro livro;
    private Usuario usuario;
    private LocalDate hoje;

    @BeforeEach
    void setUp() {
        hoje = LocalDate.of(2024, 11, 10);
        relogio = new RelogioStub(hoje);
        repository = new InMemoryEmprestimoRepository();
        service = new EmprestimoService(relogio, repository);
        livro = new Livro(1L, "Clean Code", 2.0);
        usuario = new Usuario(1L, "Lucas");
    }

    @AfterEach
    void tearDown() {
        repository.limpar();
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0.0",
        "1, 0.02",
        "5, 0.10",
        "10, 0.20",
        "30, 0.60",
        "31, 0.60",
        "100, 0.60"
    })
    void deveCalcularMultaPorDiasDeAtraso(int diasAtraso, double multaEsperada) {
        LocalDate dataPrevista = hoje.minusDays(diasAtraso);
        Emprestimo emprestimo = new Emprestimo(1L, livro, usuario, dataPrevista.minusDays(7), dataPrevista);

        double multa = service.calcularMulta(emprestimo);

        assertEquals(multaEsperada, multa, 0.001);
    }

    @Test
    void naoDeveCalcularMultaSeNaoVencido() {
        LocalDate dataPrevista = hoje.plusDays(5);
        Emprestimo emprestimo = new Emprestimo(1L, livro, usuario, hoje.minusDays(2), dataPrevista);

        double multa = service.calcularMulta(emprestimo);

        assertEquals(0.0, multa, 0.001);
    }

    @Test
    void deveLimitarMultaA30PorcentoDoValor() {
        LocalDate dataPrevista = hoje.minusDays(50);
        Emprestimo emprestimo = new Emprestimo(1L, livro, usuario, dataPrevista.minusDays(7), dataPrevista);

        double multa = service.calcularMulta(emprestimo);

        assertEquals(0.60, multa, 0.001);
    }

    @Test
    void deveLancarExcecaoSeEmprestimoNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.calcularMulta(null));
    }

    @Test
    void deveLancarExcecaoSeLivroNulo() {
        Emprestimo emprestimo = new Emprestimo(1L, null, usuario, hoje.minusDays(7), hoje);
        assertThrows(IllegalArgumentException.class, () -> service.calcularMulta(emprestimo));
    }

    @Test
    void deveCriarEmprestimoComSucesso() {
        Emprestimo emprestimo = service.criarEmprestimo(usuario, livro, 7);

        assertNotNull(emprestimo);
        assertNotNull(emprestimo.getId());
        assertEquals(usuario, emprestimo.getUsuario());
        assertEquals(livro, emprestimo.getLivro());
        assertEquals(hoje, emprestimo.getDataEmprestimo());
        assertEquals(hoje.plusDays(7), emprestimo.getDataPrevistaDevolucao());
        assertNull(emprestimo.getDataDevolucao());
        assertEquals(0.0, emprestimo.getMulta());
    }

    @Test
    void deveLancarExcecaoAoCriarEmprestimoComUsuarioNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.criarEmprestimo(null, livro, 7));
    }

    @Test
    void deveLancarExcecaoAoCriarEmprestimoComLivroNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.criarEmprestimo(usuario, null, 7));
    }

    @Test
    void deveLancarExcecaoAoCriarEmprestimoComDiasInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> service.criarEmprestimo(usuario, livro, 0));
        assertThrows(IllegalArgumentException.class, () -> service.criarEmprestimo(usuario, livro, -1));
    }

    @ParameterizedTest
    @CsvSource({
        "1, 1",
        "2, 2",
        "3, 3"
    })
    void devePermitirCriarAteOLimiteDeEmprestimos(int quantidade, int esperado) {
        for (int i = 0; i < quantidade; i++) {
            service.criarEmprestimo(usuario, livro, 7);
        }
        assertEquals(esperado, repository.buscarTodos().size());
    }

    @Test
    void deveLancarExcecaoAoExcederLimiteDeEmprestimosPorUsuario() {
        service.criarEmprestimo(usuario, livro, 7);
        service.criarEmprestimo(usuario, livro, 7);
        service.criarEmprestimo(usuario, livro, 7);

        assertThrows(IllegalStateException.class, () -> service.criarEmprestimo(usuario, livro, 7));
    }

    @Test
    void deveDevolverEmprestimoComSucesso() {
        Emprestimo emprestimo = service.criarEmprestimo(usuario, livro, 7);
        Long emprestimoId = emprestimo.getId();

        service.devolverEmprestimo(emprestimoId);

        Emprestimo devolvido = repository.buscarPorId(emprestimoId).orElseThrow();
        assertNotNull(devolvido.getDataDevolucao());
        assertEquals(hoje, devolvido.getDataDevolucao());
        assertEquals(0.0, devolvido.getMulta());
    }

    @Test
    void deveAplicarMultaAoDevolverEmprestimoAtrasado() {
        Emprestimo emprestimo = service.criarEmprestimo(usuario, livro, 7);
        Long emprestimoId = emprestimo.getId();
        
        relogio.avancarDias(10);

        service.devolverEmprestimo(emprestimoId);

        Emprestimo devolvido = repository.buscarPorId(emprestimoId).orElseThrow();
        assertNotNull(devolvido.getDataDevolucao());
        assertEquals(hoje.plusDays(10), devolvido.getDataDevolucao());
        assertEquals(0.06, devolvido.getMulta(), 0.001);
    }

    @Test
    void deveLancarExcecaoAoDevolverEmprestimoInexistente() {
        assertThrows(IllegalArgumentException.class, () -> service.devolverEmprestimo(999L));
    }

    @Test
    void deveLancarExcecaoAoDevolverEmprestimoJaDevolvido() {
        Emprestimo emprestimo = service.criarEmprestimo(usuario, livro, 7);
        Long emprestimoId = emprestimo.getId();
        
        service.devolverEmprestimo(emprestimoId);

        assertThrows(IllegalStateException.class, () -> service.devolverEmprestimo(emprestimoId));
    }

    @Test
    void deveEstenderEmprestimoComSucesso() {
        Emprestimo emprestimo = service.criarEmprestimo(usuario, livro, 7);
        Long emprestimoId = emprestimo.getId();
        LocalDate dataPrevistaOriginal = emprestimo.getDataPrevistaDevolucao();

        service.estenderEmprestimo(emprestimoId, 5);

        Emprestimo estendido = repository.buscarPorId(emprestimoId).orElseThrow();
        assertEquals(dataPrevistaOriginal.plusDays(5), estendido.getDataPrevistaDevolucao());
    }

    @Test
    void deveLancarExcecaoAoEstenderEmprestimoVencido() {
        Emprestimo emprestimo = service.criarEmprestimo(usuario, livro, 7);
        Long emprestimoId = emprestimo.getId();
        
        relogio.avancarDias(10);

        assertThrows(IllegalStateException.class, () -> service.estenderEmprestimo(emprestimoId, 5));
    }

    @Test
    void deveLancarExcecaoAoEstenderEmprestimoInexistente() {
        assertThrows(IllegalArgumentException.class, () -> service.estenderEmprestimo(999L, 5));
    }

    @Test
    void deveLancarExcecaoAoEstenderEmprestimoJaDevolvido() {
        Emprestimo emprestimo = service.criarEmprestimo(usuario, livro, 7);
        Long emprestimoId = emprestimo.getId();
        
        service.devolverEmprestimo(emprestimoId);

        assertThrows(IllegalStateException.class, () -> service.estenderEmprestimo(emprestimoId, 5));
    }

    @Test
    void deveLancarExcecaoAoEstenderComDiasInvalidos() {
        Emprestimo emprestimo = service.criarEmprestimo(usuario, livro, 7);
        Long emprestimoId = emprestimo.getId();

        assertThrows(IllegalArgumentException.class, () -> service.estenderEmprestimo(emprestimoId, 0));
        assertThrows(IllegalArgumentException.class, () -> service.estenderEmprestimo(emprestimoId, -1));
    }
}
