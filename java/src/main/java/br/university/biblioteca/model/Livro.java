package br.university.biblioteca.model;

public class Livro {
    private Long id;
    private String titulo;
    private double valorDiario;

    public Livro() {}

    public Livro(Long id, String titulo, double valorDiario) {
        this.id = id;
        this.titulo = titulo;
        this.valorDiario = valorDiario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getValorDiario() {
        return valorDiario;
    }

    public void setValorDiario(double valorDiario) {
        this.valorDiario = valorDiario;
    }
}
