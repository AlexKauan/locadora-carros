package Projeto.locadora.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private String cnh;
    private static final List<Cliente> listClientes = new ArrayList<>();

    public Cliente(int id, String nome, String cpf, String email, String senha, Status status, String cnh) {
        super(id, nome, cpf, email, senha, status);
        this.cnh = cnh;
    }

    public static void cadastrarCliente(String nome, String cpf, String email, String senha, String cnh) {
        int id = proximoId();
        listClientes.add(new Cliente(id, nome, cpf, email, senha, Status.ATIVO, cnh));
    }

    public static void visualizarCliente(String cpf) {
        for (Cliente c : listClientes) {
            if (c.getCpf().equals(cpf)) {
                System.out.println(c.verUsuario() + " | CNH: " + c.cnh);
                return;
            }
        }
        System.out.println("Cliente não encontrado.");
    }

    public static void atualizarCliente(int id, String nome, String cpf, String email, String senha, Status status,
            String cnh) {
        Cliente c = buscarPorId(id);
        if (c == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        c.atualizar(nome, cpf, email, senha);
        c.setStatus(status);
        c.cnh = cnh;
    }

    public static void excluirCliente(int id) {
        Cliente c = buscarPorId(id);
        if (c == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        if (c.getStatus() != Status.ATIVO) {
            throw new IllegalStateException("Cliente não pode ser excluído (status " + c.getStatus() + ").");
        }
        listClientes.remove(c);
    }

    public void desativarCliente() {
        desativarConta();
    }

    public static List<Cliente> listarClientes() {
        return listClientes;
    }

    public String getCnh() {
        return cnh;
    }

    private static int proximoId() {
        int maior = 0;
        for (Cliente c : listClientes) {
            if (c.getId() > maior) {
                maior = c.getId();
            }
        }
        return maior + 1;
    }

    private static Cliente buscarPorId(int id) {
        for (Cliente c : listClientes) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }
}
