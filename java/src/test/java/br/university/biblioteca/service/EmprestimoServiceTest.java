package br.university.biblioteca.service;

import br.university.biblioteca.model.Emprestimo;
import br.university.biblioteca.model.Livro;
import br.university.biblioteca.model.Usuario;
import br.university.biblioteca.stub.RelogioStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoServiceTest {
    private EmprestimoService service;
    private RelogioStub relogio;
    private Livro livro;
    private Usuario usuario;
    private LocalDate hoje;

    @BeforeEach
    void setUp() {
        hoje = LocalDate.of(2024, 11, 10);
        relogio = new RelogioStub(hoje);
        service = new EmprestimoService(relogio);
        livro = new Livro(1L, "Clean Code", 2.0);
        usuario = new Usuario(1L, "Lucas");
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
}
