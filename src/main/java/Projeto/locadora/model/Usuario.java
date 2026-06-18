package Projeto.locadora.model;

import java.util.Objects;

public abstract class Usuario {
    private String nome;
    private Status status;
    private String cpf;
    private String email;
    private String senha;
    private int id;

    protected Usuario(int id, String nome, String cpf, String email, String senha, Status status) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.status = status;
    }

    public String verUsuario() {
        return "ID: " + id
                + " | Nome: " + nome
                + " | CPF: " + cpf
                + " | Email: " + email
                + " | Status: " + status;
    }

    public boolean autenticar(String cpf, String senha) {
        return Objects.equals(this.cpf, cpf) && Objects.equals(this.senha, senha);
    }

    public void atualizar(String nome, String cpf, String email, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }

    public void desativarConta() {
        this.status = Status.DESATIVADO;
    }

    public void reativarConta() {
        this.status = Status.ATIVO;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
