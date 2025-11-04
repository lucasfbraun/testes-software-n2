package br.university.biblioteca.service;

import br.university.biblioteca.model.Emprestimo;
import br.university.biblioteca.port.Relogio;

import java.time.temporal.ChronoUnit;

public class EmprestimoService {
    private static final double TAXA_MULTA_DIARIA = 0.01;
    private static final double PERCENTUAL_MULTA_MAXIMA = 0.30;

    private final Relogio relogio;

    public EmprestimoService(Relogio relogio) {
        this.relogio = relogio;
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
}
