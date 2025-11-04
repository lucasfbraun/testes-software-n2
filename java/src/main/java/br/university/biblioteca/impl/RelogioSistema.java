package br.university.biblioteca.impl;

import br.university.biblioteca.port.Relogio;
import java.time.LocalDate;

public class RelogioSistema implements Relogio {
    @Override
    public LocalDate now() {
        return LocalDate.now();
    }
}
