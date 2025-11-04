package br.university.biblioteca.stub;

import br.university.biblioteca.port.Relogio;
import java.time.LocalDate;

public class RelogioStub implements Relogio {
    private LocalDate dataAtual;

    public RelogioStub(LocalDate dataInicial) {
        this.dataAtual = dataInicial;
    }

    public void setData(LocalDate data) {
        this.dataAtual = data;
    }

    public void avancarDias(int dias) {
        this.dataAtual = this.dataAtual.plusDays(dias);
    }

    @Override
    public LocalDate now() {
        return dataAtual;
    }
}
