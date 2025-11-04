package br.university.biblioteca.service;

import br.university.biblioteca.model.Emprestimo;
import br.university.biblioteca.model.Livro;
import br.university.biblioteca.model.Usuario;
import br.university.biblioteca.port.EmprestimoRepository;
import br.university.biblioteca.port.Relogio;

import java.time.temporal.ChronoUnit;

public class EmprestimoService {
    private static final double TAXA_MULTA_DIARIA = 0.01;
    private static final double PERCENTUAL_MULTA_MAXIMA = 0.30;
    private static final int LIMITE_EMPRESTIMOS_POR_USUARIO = 3;

    private final Relogio relogio;
    private final EmprestimoRepository repository;

    public EmprestimoService(Relogio relogio, EmprestimoRepository repository) {
        this.relogio = relogio;
        this.repository = repository;
    }

    public double calcularMulta(Emprestimo emprestimo) {
        validarEmprestimo(emprestimo);

        long diasAtraso = calcularDiasAtraso(emprestimo);
        if (diasAtraso <= 0) {
            return 0.0;
        }

        double valorDiario = emprestimo.getLivro().getValorDiario();
        double multa = valorDiario * TAXA_MULTA_DIARIA * diasAtraso;
        double multaMaxima = valorDiario * PERCENTUAL_MULTA_MAXIMA;

        return Math.min(multa, multaMaxima);
    }

    private void validarEmprestimo(Emprestimo emprestimo) {
        if (emprestimo == null) {
            throw new IllegalArgumentException("Emprestimo nao pode ser nulo");
        }
        if (emprestimo.getLivro() == null) {
            throw new IllegalArgumentException("Livro nao pode ser nulo");
        }
    }

    private long calcularDiasAtraso(Emprestimo emprestimo) {
        return ChronoUnit.DAYS.between(
            emprestimo.getDataPrevistaDevolucao(),
            relogio.now()
        );
    }

    public Emprestimo criarEmprestimo(Usuario usuario, Livro livro, int diasEmprestimo) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao pode ser nulo");
        }
        if (livro == null) {
            throw new IllegalArgumentException("Livro nao pode ser nulo");
        }
        if (diasEmprestimo <= 0) {
            throw new IllegalArgumentException("Dias de emprestimo deve ser maior que zero");
        }

        long emprestimosAtivos = contarEmprestimosAtivos(usuario);
        if (emprestimosAtivos >= LIMITE_EMPRESTIMOS_POR_USUARIO) {
            throw new IllegalStateException("Usuario atingiu o limite de emprestimos");
        }

        Emprestimo emprestimo = new Emprestimo(
            null,
            livro,
            usuario,
            relogio.now(),
            relogio.now().plusDays(diasEmprestimo)
        );

        repository.salvar(emprestimo);
        return emprestimo;
    }

    private long contarEmprestimosAtivos(Usuario usuario) {
        return repository.buscarTodos().stream()
            .filter(e -> e.getUsuario().getId().equals(usuario.getId()))
            .filter(e -> e.getDataDevolucao() == null)
            .count();
    }

    public void devolverEmprestimo(Long emprestimoId) {
        Emprestimo emprestimo = repository.buscarPorId(emprestimoId)
            .orElseThrow(() -> new IllegalArgumentException("Emprestimo nao encontrado"));

        if (emprestimo.getDataDevolucao() != null) {
            throw new IllegalStateException("Emprestimo ja foi devolvido");
        }

        emprestimo.setDataDevolucao(relogio.now());

        double multa = calcularMulta(emprestimo);
        emprestimo.setMulta(multa);

        repository.salvar(emprestimo);
    }
}
