package br.university.biblioteca.port;

import br.university.biblioteca.model.Emprestimo;
import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository {
    void salvar(Emprestimo emprestimo);
    Optional<Emprestimo> buscarPorId(Long id);
    List<Emprestimo> buscarTodos();
    void deletar(Long id);
}
