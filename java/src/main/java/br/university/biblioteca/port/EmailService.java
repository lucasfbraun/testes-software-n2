package br.university.biblioteca.port;

public interface EmailService {
    void enviar(String destinatario, String assunto, String corpo);
}
