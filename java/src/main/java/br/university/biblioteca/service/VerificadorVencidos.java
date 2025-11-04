package br.university.biblioteca.service;

import br.university.biblioteca.model.Emprestimo;
import br.university.biblioteca.port.EmprestimoRepository;
import br.university.biblioteca.port.EmailService;
import br.university.biblioteca.port.Relogio;

public class VerificadorVencidos {
    private final EmprestimoRepository repository;
    private final EmprestimoService emprestimoService;
    private final EmailService emailService;

    public VerificadorVencidos(Relogio relogio, EmprestimoRepository repository,
                               EmprestimoService emprestimoService, EmailService emailService) {
        this.repository = repository;
        this.emprestimoService = emprestimoService;
        this.emailService = emailService;
    }

    public void processarVencidos() {
        for (Emprestimo e : repository.buscarTodos()) {
            if (e.getDataDevolucao() != null) {
                continue;
            }

            double multa = emprestimoService.calcularMulta(e);
            if (multa > 0.0) {
                e.setMulta(multa);
                repository.salvar(e);
                String destinatario = e.getUsuario().getNome();
                String assunto = "Emprestimo vencido";
                String corpo = "Seu emprestimo do livro '" + e.getLivro().getTitulo() + "' esta vencido.";
                emailService.enviar(destinatario, assunto, corpo);
            }
        }
    }
}
