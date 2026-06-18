package Projeto.locadora.infra;

import Projeto.locadora.model.Aluguel;
import Projeto.locadora.model.Cargo;
import Projeto.locadora.model.Carro;
import Projeto.locadora.model.Cliente;
import Projeto.locadora.model.Funcionario;
import Projeto.locadora.model.Status;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArquivoCsv {
    private static final Path DIR = Paths.get("data");
    private static final Path ARQ_FUNCIONARIOS = DIR.resolve("funcionarios.csv");
    private static final Path ARQ_CLIENTES = DIR.resolve("clientes.csv");
    private static final Path ARQ_CARROS = DIR.resolve("carros.csv");
    private static final Path ARQ_ALUGUEIS = DIR.resolve("alugueis.csv");

    public static void garantirEstrutura() {
        try {
            Files.createDirectories(DIR);
            if (Files.notExists(ARQ_FUNCIONARIOS)) Files.createFile(ARQ_FUNCIONARIOS);
            if (Files.notExists(ARQ_CLIENTES)) Files.createFile(ARQ_CLIENTES);
            if (Files.notExists(ARQ_CARROS)) Files.createFile(ARQ_CARROS);
            if (Files.notExists(ARQ_ALUGUEIS)) Files.createFile(ARQ_ALUGUEIS);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao preparar arquivos: " + e.getMessage(), e);
        }
    }

    public static List<Funcionario> carregarFuncionarios() {
        List<Funcionario> funcionarios = new ArrayList<>();
        if (Files.notExists(ARQ_FUNCIONARIOS)) return funcionarios;

        try (BufferedReader reader = Files.newBufferedReader(ARQ_FUNCIONARIOS, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(";", -1);
                int id = Integer.parseInt(p[0]);
                String nome = p[1];
                String cpf = p[2];
                String email = p[3];
                String senha = p[4];
                Status status = Status.valueOf(p[5]);
                Cargo cargo = Cargo.valueOf(p[6]);
                funcionarios.add(new Funcionario(id, nome, cpf, email, senha, status, cargo));
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar funcionarios.csv: " + e.getMessage(), e);
        }

        return funcionarios;
    }

    public static void salvarFuncionarios(List<Funcionario> funcionarios) {
        try (BufferedWriter writer = Files.newBufferedWriter(ARQ_FUNCIONARIOS, StandardCharsets.UTF_8)) {
            for (Funcionario f : funcionarios) {
                writer.write(f.getId() + ";" + sanitizar(f.getNome()) + ";" + sanitizar(f.getCpf()) + ";"
                        + sanitizar(f.getEmail()) + ";" + sanitizar(f.getSenha()) + ";" + f.getStatus() + ";" + f.getCargo());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar funcionarios.csv: " + e.getMessage(), e);
        }
    }

    public static List<Cliente> carregarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        if (Files.notExists(ARQ_CLIENTES)) return clientes;

        try (BufferedReader reader = Files.newBufferedReader(ARQ_CLIENTES, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(";", -1);
                int id = Integer.parseInt(p[0]);
                String nome = p[1];
                String cpf = p[2];
                String email = p[3];
                String senha = p[4];
                Status status = Status.valueOf(p[5]);
                String cnh = p[6];
                clientes.add(new Cliente(id, nome, cpf, email, senha, status, cnh));
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar clientes.csv: " + e.getMessage(), e);
        }

        return clientes;
    }

    public static void salvarClientes(List<Cliente> clientes) {
        try (BufferedWriter writer = Files.newBufferedWriter(ARQ_CLIENTES, StandardCharsets.UTF_8)) {
            for (Cliente c : clientes) {
                writer.write(c.getId() + ";" + sanitizar(c.getNome()) + ";" + sanitizar(c.getCpf()) + ";"
                        + sanitizar(c.getEmail()) + ";" + sanitizar(c.getSenha()) + ";" + c.getStatus() + ";" + sanitizar(c.getCnh()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar clientes.csv: " + e.getMessage(), e);
        }
    }

    public static List<Carro> carregarCarros() {
        List<Carro> carros = new ArrayList<>();
        if (Files.notExists(ARQ_CARROS)) return carros;

        try (BufferedReader reader = Files.newBufferedReader(ARQ_CARROS, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(";", -1);
                String modelo = p[0];
                String placa = p[1];
                String cambio = p[2];
                double valorDiaria = Double.parseDouble(p[3]);
                boolean disponivel = Boolean.parseBoolean(p[4]);
                carros.add(new Carro(modelo, placa, cambio, valorDiaria, disponivel));
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar carros.csv: " + e.getMessage(), e);
        }

        return carros;
    }

    public static void salvarCarros(List<Carro> carros) {
        try (BufferedWriter writer = Files.newBufferedWriter(ARQ_CARROS, StandardCharsets.UTF_8)) {
            for (Carro c : carros) {
                writer.write(sanitizar(c.getModelo()) + ";" + sanitizar(c.getPlaca()) + ";"
                        + sanitizar(c.getCambio()) + ";" + c.getValorDiaria() + ";" + c.isDisponivel());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar carros.csv: " + e.getMessage(), e);
        }
    }

    public static List<Aluguel> carregarAlugueis(List<Funcionario> funcionarios, List<Cliente> clientes, List<Carro> carros) {
        List<Aluguel> alugueis = new ArrayList<>();
        if (Files.notExists(ARQ_ALUGUEIS)) return alugueis;

        try (BufferedReader reader = Files.newBufferedReader(ARQ_ALUGUEIS, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(";", -1);

                int id = Integer.parseInt(p[0]);
                String clienteCpf = p[1];
                String carroPlaca = p[2];
                String funcionarioCpf = p[3];
                LocalDate dataInicio = LocalDate.parse(p[4]);
                LocalDate dataFimPrevista = LocalDate.parse(p[5]);
                LocalDate devolucao = p[6].isBlank() ? null : LocalDate.parse(p[6]);
                double valorDiaria = Double.parseDouble(p[7]);
                double valorAluguel = p[8].isBlank() ? 0.0 : Double.parseDouble(p[8]);
                boolean finalizado = Boolean.parseBoolean(p[9]);

                Cliente cliente = buscarClientePorCpf(clientes, clienteCpf);
                Carro carro = buscarCarroPorPlaca(carros, carroPlaca);
                Funcionario funcionario = buscarFuncionarioPorCpf(funcionarios, funcionarioCpf);
                if (cliente == null || carro == null || funcionario == null) continue;

                alugueis.add(new Aluguel(id, dataInicio, dataFimPrevista, devolucao, valorDiaria, valorAluguel, finalizado, funcionario, cliente, carro));
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar alugueis.csv: " + e.getMessage(), e);
        }

        return alugueis;
    }

    public static void salvarAlugueis(List<Aluguel> alugueis) {
        try (BufferedWriter writer = Files.newBufferedWriter(ARQ_ALUGUEIS, StandardCharsets.UTF_8)) {
            for (Aluguel a : alugueis) {
                String devolucao = (a.getDataDevolucaoEfetiva() == null) ? "" : a.getDataDevolucaoEfetiva().toString();
                writer.write(a.getId() + ";" + a.getCliente().getCpf() + ";" + a.getCarro().getPlaca() + ";" + a.getFuncionario().getCpf() + ";"
                        + a.getDataInicio() + ";" + a.getDataFimPrevista() + ";" + devolucao + ";" + a.getValorDiaria() + ";"
                        + a.getValorAluguel() + ";" + a.isFinalizado());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar alugueis.csv: " + e.getMessage(), e);
        }
    }

    private static String sanitizar(String texto) {
        if (texto == null) return "";
        String t = texto.trim();
        return t.replace(";", ",");
    }

    private static Cliente buscarClientePorCpf(List<Cliente> clientes, String cpf) {
        for (Cliente c : clientes) {
            if (Objects.equals(c.getCpf(), cpf)) return c;
        }
        return null;
    }

    private static Funcionario buscarFuncionarioPorCpf(List<Funcionario> funcionarios, String cpf) {
        for (Funcionario f : funcionarios) {
            if (Objects.equals(f.getCpf(), cpf)) return f;
        }
        return null;
    }

    private static Carro buscarCarroPorPlaca(List<Carro> carros, String placa) {
        for (Carro c : carros) {
            if (Objects.equals(c.getPlaca(), placa)) return c;
        }
        return null;
    }
}

