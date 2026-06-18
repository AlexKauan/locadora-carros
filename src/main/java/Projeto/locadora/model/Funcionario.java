package Projeto.locadora.model;

import java.util.ArrayList;
import java.util.List;

public class Funcionario extends Usuario {
    private Cargo cargo;
    private static final List<Funcionario> listFuncionarios = new ArrayList<>();

    public Funcionario(int id, String nome, String cpf, String email, String senha, Status status, Cargo cargo) {
        super(id, nome, cpf, email, senha, status);
        this.cargo = cargo;
    }

    public static void cadastrarFuncionario(String nome, String cpf, String email, String senha, Cargo cargo) {
        int id = proximoId();
        listFuncionarios.add(new Funcionario(id, nome, cpf, email, senha, Status.ATIVO, cargo));
    }

    public static void visualizarFuncionario(String cpf) {
        for (Funcionario f : listFuncionarios) {
            if (f.getCpf().equals(cpf)) {
                System.out.println(f.verUsuario() + " | Cargo: " + f.cargo);
                return;
            }
        }
        System.out.println("Funcionário não encontrado.");
    }

    public static void atualizarFuncionario(int id, String nome, String cpf, String email, String senha, Status status,
            Cargo cargo) {
        Funcionario f = buscarPorId(id);
        if (f == null) {
            throw new IllegalArgumentException("Funcionário não encontrado.");
        }
        f.atualizar(nome, cpf, email, senha);
        f.setStatus(status);
        f.cargo = cargo;
    }

    public static void excluirFuncionario(int id) {
        Funcionario f = buscarPorId(id);
        if (f == null) {
            throw new IllegalArgumentException("Funcionário não encontrado.");
        }
        if (f.getStatus() != Status.ATIVO) {
            throw new IllegalStateException("Funcionário não pode ser excluído (status " + f.getStatus() + ").");
        }
        listFuncionarios.remove(f);
    }

    public void desativarFuncionario() {
        desativarConta();
    }

    public static List<Funcionario> listarFuncionarios() {
        return listFuncionarios;
    }

    public Cargo getCargo() {
        return cargo;
    }

    private static int proximoId() {
        int maior = 0;
        for (Funcionario f : listFuncionarios) {
            if (f.getId() > maior) {
                maior = f.getId();
            }
        }
        return maior + 1;
    }

    private static Funcionario buscarPorId(int id) {
        for (Funcionario f : listFuncionarios) {
            if (f.getId() == id) {
                return f;
            }
        }
        return null;
    }
}
