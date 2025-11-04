package br.university.biblioteca.stub;

import br.university.biblioteca.port.EmailService;
import java.util.ArrayList;
import java.util.List;

public class EmailServiceStub implements EmailService {
    private List<EmailEnviado> emails = new ArrayList<>();

    @Override
    public void enviar(String destinatario, String assunto, String corpo) {
        emails.add(new EmailEnviado(destinatario, assunto, corpo));
    }

    public int quantidadeEnviados() {
        return emails.size();
    }

    public List<EmailEnviado> getEmails() {
        return new ArrayList<>(emails);
    }

    public void limpar() {
        emails.clear();
    }

    public static class EmailEnviado {
        private final String destinatario;
        private final String assunto;
        private final String corpo;

        public EmailEnviado(String destinatario, String assunto, String corpo) {
            this.destinatario = destinatario;
            this.assunto = assunto;
            this.corpo = corpo;
        }

        public String getDestinatario() {
            return destinatario;
        }

        public String getAssunto() {
            return assunto;
        }

        public String getCorpo() {
            return corpo;
        }
    }
}
