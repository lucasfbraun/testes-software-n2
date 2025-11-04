package br.university.biblioteca.repository;

import br.university.biblioteca.model.Emprestimo;
import br.university.biblioteca.port.EmprestimoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryEmprestimoRepository implements EmprestimoRepository {
    private final Map<Long, Emprestimo> emprestimos = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void salvar(Emprestimo emprestimo) {
        if (emprestimo.getId() == null) {
            emprestimo.setId(idGenerator.getAndIncrement());
        }
        emprestimos.put(emprestimo.getId(), emprestimo);
    }

    @Override
    public Optional<Emprestimo> buscarPorId(Long id) {
        return Optional.ofNullable(emprestimos.get(id));
    }

    @Override
    public List<Emprestimo> buscarTodos() {
        return new ArrayList<>(emprestimos.values());
    }

    @Override
    public void deletar(Long id) {
        emprestimos.remove(id);
    }

    public void limpar() {
        emprestimos.clear();
        idGenerator.set(1);
    }
}
