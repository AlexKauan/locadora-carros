package Projeto.locadora.app;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import Projeto.locadora.infra.ArquivoCsv;
import Projeto.locadora.model.Aluguel;
import Projeto.locadora.model.Cargo;
import Projeto.locadora.model.Carro;
import Projeto.locadora.model.Cliente;
import Projeto.locadora.model.Funcionario;
import Projeto.locadora.model.Status;
import Projeto.locadora.ui.Console;

public class Main {
    public static void main(String[] args) {
        ArquivoCsv.garantirEstrutura();

        Funcionario.listarFuncionarios().clear();
        Funcionario.listarFuncionarios().addAll(ArquivoCsv.carregarFuncionarios());

        Cliente.listarClientes().clear();
        Cliente.listarClientes().addAll(ArquivoCsv.carregarClientes());

        Carro.listarCarros().clear();
        Carro.listarCarros().addAll(ArquivoCsv.carregarCarros());

        Aluguel.listarAlugueis().clear();
        Aluguel.listarAlugueis().addAll(ArquivoCsv.carregarAlugueis(
                Funcionario.listarFuncionarios(),
                Cliente.listarClientes(),
                Carro.listarCarros()));

        aplicarBloqueioAutomatico();

        Scanner scanner = new Scanner(System.in);
        Console console = new Console(scanner);

        while (true) {
            if (Funcionario.listarFuncionarios().isEmpty()) {
                System.out.println("\n=== LOCADORA DE CARROS ===");
                System.out.println("Nenhum funcionário cadastrado.");
                System.out.println("1) Cadastrar primeiro GERENTE");
                System.out.println("2) Sair");
                int op = console.lerOpcao("Escolha: ", 1, 2);
                if (op == 2)
                    return;
                cadastrarPrimeiroGerente(console);
                continue;
            }

            System.out.println("\n=== LOCADORA DE CARROS ===");
            System.out.println("1) Login Funcionário");
            System.out.println("2) Login Cliente");
            System.out.println("3) Sair");
            int op = console.lerOpcao("Escolha: ", 1, 3);
            if (op == 3)
                return;
            if (op == 1)
                loginFuncionario(console);
            if (op == 2)
                loginCliente(console);
        }
    }

    private static void aplicarBloqueioAutomatico() {
        Aluguel.verificarEAtualizarBloqueios();
        ArquivoCsv.salvarClientes(Cliente.listarClientes());
    }

    private static void cadastrarPrimeiroGerente(Console console) {
        String nome = console.lerNaoVazio("Nome: ");
        String cpf = console.lerNaoVazio("CPF: ");
        String email = console.lerNaoVazio("Email: ");
        String senha = console.lerNaoVazio("Senha: ");
        validarCpfUnico(cpf);
        Funcionario.cadastrarFuncionario(nome, cpf, email, senha, Cargo.GERENTE);
        ArquivoCsv.salvarFuncionarios(Funcionario.listarFuncionarios());
        System.out.println("Gerente cadastrado.");
    }

    private static void loginFuncionario(Console console) {
        System.out.println("\n--- Login Funcionário ---");
        String cpf = console.lerNaoVazio("CPF: ");
        String senha = console.lerNaoVazio("Senha: ");

        Funcionario f = buscarFuncionarioPorCpf(cpf);
        if (f == null) {
            System.out.println("CPF ou senha inválidos.");
            return;
        }
        if (f.getStatus() != Status.ATIVO) {
            System.out.println("Conta não pode acessar (status " + f.getStatus() + ").");
            return;
        }
        if (!f.autenticar(cpf, senha)) {
            System.out.println("CPF ou senha inválidos.");
            return;
        }

        menuFuncionario(console, f);
    }

    private static void loginCliente(Console console) {
        System.out.println("\n--- Login Cliente ---");
        String cpf = console.lerNaoVazio("CPF: ");
        String senha = console.lerNaoVazio("Senha: ");

        Cliente c = buscarClientePorCpf(cpf);
        if (c == null) {
            System.out.println("CPF ou senha inválidos.");
            return;
        }
        if (c.getStatus() != Status.ATIVO) {
            System.out.println("Conta não pode acessar (status " + c.getStatus() + ").");
            return;
        }
        if (!c.autenticar(cpf, senha)) {
            System.out.println("CPF ou senha inválidos.");
            return;
        }

        menuCliente(console, c);
    }

    private static void menuFuncionario(Console console, Funcionario funcionario) {
        while (true) {
            System.out.println("\n=== MENU FUNCIONÁRIO (" + funcionario.getCargo() + ") ===");
            System.out.println("1) Gerenciar Clientes");
            System.out.println("2) Gerenciar Aluguéis");
            if (funcionario.getCargo() == Cargo.GERENTE) {
                System.out.println("3) Gerenciar Carros");
                System.out.println("4) Gerenciar Funcionários");
                System.out.println("5) Logout");
                int op = console.lerOpcao("Escolha: ", 1, 5);
                if (op == 1)
                    menuClientes(console);
                if (op == 2)
                    menuAlugueis(console, funcionario);
                if (op == 3)
                    menuCarros(console);
                if (op == 4)
                    menuFuncionarios(console);
                if (op == 5)
                    return;
            } else {
                System.out.println("3) Logout");
                int op = console.lerOpcao("Escolha: ", 1, 3);
                if (op == 1)
                    menuClientes(console);
                if (op == 2)
                    menuAlugueis(console, funcionario);
                if (op == 3)
                    return;
            }
        }
    }

    private static void menuCliente(Console console, Cliente cliente) {
        while (true) {
            System.out.println("\n=== MENU CLIENTE ===");
            System.out.println("1) Listar carros disponíveis");
            System.out.println("2) Consultar recibo do aluguel ativo");
            System.out.println("3) Logout");
            int op = console.lerOpcao("Escolha: ", 1, 3);
            if (op == 3)
                return;

            if (op == 1) {
                List<Carro> disp = Carro.listarCarrosDisponiveis();
                if (disp.isEmpty()) {
                    System.out.println("Nenhum carro disponível no momento.");
                } else {
                    for (Carro c : disp) {
                        System.out.println(c.exibirFichaTecnica());
                    }
                }
            }

            if (op == 2) {
                Aluguel aluguel = buscarAluguelAtivoPorCliente(cliente.getCpf());
                if (aluguel == null) {
                    System.out.println("Você não possui aluguel ativo.");
                } else {
                    System.out.println();
                    System.out.println(aluguel.emitirRecibo());
                }
            }
        }
    }

    private static void menuClientes(Console console) {
        while (true) {
            System.out.println("\n--- Clientes ---");
            System.out.println("1) Cadastrar");
            System.out.println("2) Visualizar");
            System.out.println("3) Atualizar");
            System.out.println("4) Desativar");
            System.out.println("5) Voltar");
            int op = console.lerOpcao("Escolha: ", 1, 5);
            if (op == 5)
                return;

            try {
                if (op == 1) {
                    String nome = console.lerNaoVazio("Nome: ");
                    String cpf = console.lerNaoVazio("CPF: ");
                    String email = console.lerNaoVazio("Email: ");
                    String senha = console.lerNaoVazio("Senha: ");
                    String cnh = console.lerNaoVazio("CNH: ");
                    validarCpfUnico(cpf);
                    Cliente.cadastrarCliente(nome, cpf, email, senha, cnh);
                    ArquivoCsv.salvarClientes(Cliente.listarClientes());
                    System.out.println("Cliente cadastrado.");
                }
                if (op == 2) {
                    System.out.println();
                    if (Cliente.listarClientes().isEmpty()) {
                        System.out.println("Nenhum cliente cadastrado.");
                    } else {
                        for (Cliente cliente : Cliente.listarClientes()) {
                            System.out.println(cliente.verUsuario() + " | CNH: " + cliente.getCnh());
                        }
                    }
                }
                if (op == 3) {
                    int id = console.lerInteiro("ID do cliente: ");
                    String nome = console.lerNaoVazio("Novo nome: ");
                    String cpf = console.lerNaoVazio("Novo CPF: ");
                    String email = console.lerNaoVazio("Novo email: ");
                    String senha = console.lerNaoVazio("Nova senha: ");
                    String cnh = console.lerNaoVazio("Nova CNH: ");
                    Status status = lerStatusAtivoOuDesativado(console);
                    validarCpfUnicoAoAtualizarCliente(id, cpf);
                    Cliente.atualizarCliente(id, nome, cpf, email, senha, status, cnh);
                    ArquivoCsv.salvarClientes(Cliente.listarClientes());
                    System.out.println("Cliente atualizado.");
                }
                if (op == 4) {
                    int id = console.lerInteiro("ID do cliente: ");
                    Cliente cliente = buscarClientePorId(id);
                    if (cliente == null) {
                        throw new IllegalArgumentException("Cliente não encontrado.");
                    }
                    cliente.desativarCliente();
                    ArquivoCsv.salvarClientes(Cliente.listarClientes());
                    System.out.println("Cliente desativado.");
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuFuncionarios(Console console) {
        while (true) {
            System.out.println("\n--- Funcionários ---");
            System.out.println("1) Cadastrar");
            System.out.println("2) Visualizar");
            System.out.println("3) Atualizar");
            System.out.println("4) Desativar");
            System.out.println("5) Voltar");
            int op = console.lerOpcao("Escolha: ", 1, 5);
            if (op == 5)
                return;

            try {
                if (op == 1) {
                    String nome = console.lerNaoVazio("Nome: ");
                    String cpf = console.lerNaoVazio("CPF: ");
                    String email = console.lerNaoVazio("Email: ");
                    String senha = console.lerNaoVazio("Senha: ");
                    Cargo cargo = lerCargo(console);
                    validarCpfUnico(cpf);
                    Funcionario.cadastrarFuncionario(nome, cpf, email, senha, cargo);
                    ArquivoCsv.salvarFuncionarios(Funcionario.listarFuncionarios());
                    System.out.println("Funcionário cadastrado.");
                }
                if (op == 2) {
                    System.out.println();
                    if (Funcionario.listarFuncionarios().isEmpty()) {
                        System.out.println("Nenhum funcionário cadastrado.");
                    } else {
                        for (Funcionario func : Funcionario.listarFuncionarios()) {
                            System.out.println(func.verUsuario() + " | Cargo: " + func.getCargo());
                        }
                    }
                }
                if (op == 3) {
                    int id = console.lerInteiro("ID do funcionário: ");
                    String nome = console.lerNaoVazio("Novo nome: ");
                    String cpf = console.lerNaoVazio("Novo CPF: ");
                    String email = console.lerNaoVazio("Novo email: ");
                    String senha = console.lerNaoVazio("Nova senha: ");
                    Status status = lerStatusAtivoOuDesativado(console);
                    Cargo cargo = lerCargo(console);
                    validarCpfUnicoAoAtualizarFuncionario(id, cpf);
                    Funcionario.atualizarFuncionario(id, nome, cpf, email, senha, status, cargo);
                    ArquivoCsv.salvarFuncionarios(Funcionario.listarFuncionarios());
                    System.out.println("Funcionário atualizado.");
                }
                if (op == 4) {
                    int id = console.lerInteiro("ID do funcionário: ");
                    Funcionario funcionario = buscarFuncionarioPorId(id);
                    if (funcionario == null) {
                        throw new IllegalArgumentException("Funcionário não encontrado.");
                    }
                    funcionario.desativarFuncionario();
                    ArquivoCsv.salvarFuncionarios(Funcionario.listarFuncionarios());
                    System.out.println("Funcionário desativado.");
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuCarros(Console console) {
        while (true) {
            System.out.println("\n--- Carros ---");
            System.out.println("1) Cadastrar");
            System.out.println("2) Listar (ficha técnica)");
            System.out.println("3) Atualizar");
            System.out.println("4) Desativar");
            System.out.println("5) Voltar");
            int op = console.lerOpcao("Escolha: ", 1, 5);
            if (op == 5)
                return;

            try {
                if (op == 1) {
                    String modelo = console.lerNaoVazio("Modelo: ");
                    String placa = console.lerNaoVazio("Placa: ");
                    String cambio = console.lerNaoVazio("Câmbio: ");
                    double diaria = console.lerDouble("Valor da diária: ");
                    Carro.cadastrarCarro(modelo, placa, cambio, diaria, true);
                    ArquivoCsv.salvarCarros(Carro.listarCarros());
                    System.out.println("Carro cadastrado.");
                }
                if (op == 2) {
                    if (Carro.listarCarros().isEmpty()) {
                        System.out.println("Nenhum carro cadastrado.");
                    } else {
                        for (Carro c : Carro.listarCarros()) {
                            System.out.println(c.exibirFichaTecnica());
                        }
                    }
                }
                if (op == 3) {
                    String placa = console.lerNaoVazio("Placa do carro: ");
                    String modelo = console.lerNaoVazio("Novo modelo: ");
                    String cambio = console.lerNaoVazio("Novo câmbio: ");
                    double diaria = console.lerDouble("Novo valor da diária: ");
                    boolean disponivel = lerDisponibilidade(console);
                    Carro.atualizarCarro(0, modelo, placa, cambio, diaria, disponivel);
                    ArquivoCsv.salvarCarros(Carro.listarCarros());
                    System.out.println("Carro atualizado.");
                }
                if (op == 4) {
                    String placa = console.lerNaoVazio("Placa do carro: ");
                    Carro carro = buscarCarroPorPlaca(placa);
                    if (carro == null) {
                        throw new IllegalArgumentException("Carro não encontrado.");
                    }
                    carro.desativarCarro();
                    ArquivoCsv.salvarCarros(Carro.listarCarros());
                    System.out.println("Carro desativado.");
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuAlugueis(Console console, Funcionario funcionario) {
        while (true) {
            System.out.println("\n--- Aluguéis ---");
            System.out.println("1) Registrar aluguel");
            System.out.println("2) Encerrar aluguel");
            System.out.println("3) Listar aluguéis");
            System.out.println("4) Emitir recibo por ID");
            System.out.println("5) Voltar");
            int op = console.lerOpcao("Escolha: ", 1, 5);
            if (op == 5)
                return;

            try {
                if (op == 1) {
                    String clienteCpf = console.lerNaoVazio("CPF do cliente: ");
                    String cnh = console.lerNaoVazio("CNH do cliente: ");
                    String placa = console.lerNaoVazio("Placa do carro: ");
                    LocalDate inicio = console.lerData("Data início (yyyy-MM-dd): ");
                    LocalDate fimPrevisto = console.lerData("Data fim prevista (yyyy-MM-dd): ");

                    Cliente cliente = buscarClientePorCpf(clienteCpf);
                    if (cliente == null) {
                        throw new IllegalArgumentException("Cliente não encontrado.");
                    }
                    if (cliente.getStatus() != Status.ATIVO) {
                        throw new IllegalStateException(
                                "Cliente não pode iniciar locação (status " + cliente.getStatus() + ").");
                    }
                    if (!Objects.equals(cliente.getCnh(), cnh)) {
                        throw new IllegalArgumentException("CNH inválida para o cliente informado.");
                    }
                    if (!fimPrevisto.isAfter(inicio)) {
                        throw new IllegalArgumentException("Data de fim prevista deve ser após a data de início.");
                    }

                    Carro carro = buscarCarroPorPlaca(placa);
                    if (carro == null) {
                        throw new IllegalArgumentException("Carro não encontrado.");
                    }
                    if (!carro.isDisponivel()) {
                        throw new IllegalStateException("Carro indisponível.");
                    }

                    int idAntes = maiorIdAluguel();
                    Aluguel.cadastrarAluguel(inicio, fimPrevisto, 0.0, false, cliente, carro, funcionario);
                    ArquivoCsv.salvarAlugueis(Aluguel.listarAlugueis());
                    ArquivoCsv.salvarCarros(Carro.listarCarros());
                    int idDepois = maiorIdAluguel();
                    System.out.println("Aluguel registrado com ID: " + ((idDepois > idAntes) ? idDepois : idDepois));
                }
                if (op == 2) {
                    int id = console.lerInteiro("ID do aluguel: ");
                    LocalDate devolucao = console
                            .lerDataOpcional("Data devolução efetiva (yyyy-MM-dd) [vazio = hoje]: ");
                    if (devolucao != null) {
                        ajustarDevolucao(id, devolucao);
                    } else {
                        Aluguel aluguel = buscarAluguelPorId(id);
                        if (aluguel == null) {
                            throw new IllegalArgumentException("Aluguel não encontrado.");
                        }
                        aluguel.finalizarContrato();
                    }
                    ArquivoCsv.salvarAlugueis(Aluguel.listarAlugueis());
                    ArquivoCsv.salvarCarros(Carro.listarCarros());
                    System.out.println();
                    Aluguel aluguel = buscarAluguelPorId(id);
                    if (aluguel == null) {
                        throw new IllegalArgumentException("Aluguel não encontrado.");
                    }
                    System.out.println(aluguel.emitirRecibo());
                }
                if (op == 3) {
                    if (Aluguel.listarAlugueis().isEmpty()) {
                        System.out.println("Nenhum aluguel registrado.");
                    } else {
                        for (Aluguel a : Aluguel.listarAlugueis()) {
                            String status = a.isFinalizado() ? "FINALIZADO" : "ATIVO";
                            System.out.println("ID: " + a.getId()
                                    + " | Cliente: " + a.getCliente().getCpf()
                                    + " | Carro: " + a.getCarro().getPlaca()
                                    + " | Início: " + a.getDataInicio()
                                    + " | Fim Previsto: " + a.getDataFimPrevista()
                                    + " | " + status);
                        }
                    }
                }
                if (op == 4) {
                    int id = console.lerInteiro("ID do aluguel: ");
                    System.out.println();
                    Aluguel aluguel = buscarAluguelPorId(id);
                    if (aluguel == null) {
                        throw new IllegalArgumentException("Aluguel não encontrado.");
                    }
                    System.out.println(aluguel.emitirRecibo());
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void ajustarDevolucao(int id, LocalDate devolucao) {
        for (Aluguel a : Aluguel.listarAlugueis()) {
            if (a.getId() == id) {
                if (a.isFinalizado()) {
                    throw new IllegalStateException("Aluguel já está finalizado.");
                }
                long diasPrevistos = java.time.temporal.ChronoUnit.DAYS.between(a.getDataInicio(),
                        a.getDataFimPrevista());
                if (diasPrevistos < 0)
                    diasPrevistos = 0;
                double valorBase = diasPrevistos * a.getValorDiaria();

                long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(a.getDataFimPrevista(), devolucao);
                if (diasAtraso < 0)
                    diasAtraso = 0;
                double multa = diasAtraso * (a.getValorDiaria() + 50.0);
                double total = valorBase + multa;

                Aluguel.atualizarAluguel(
                        a.getId(),
                        a.getDataInicio(),
                        a.getDataFimPrevista(),
                        devolucao,
                        total,
                        true,
                        a.getCliente(),
                        a.getCarro(),
                        a.getFuncionario());
                a.getCarro().atualizarStatus(true);
                return;
            }
        }
        throw new IllegalArgumentException("Aluguel não encontrado.");
    }

    private static Status lerStatusAtivoOuDesativado(Console console) {
        System.out.println("Status:");
        System.out.println("1) ATIVO");
        System.out.println("2) DESATIVADO");
        int op = console.lerOpcao("Escolha: ", 1, 2);
        return (op == 1) ? Status.ATIVO : Status.DESATIVADO;
    }

    private static Cargo lerCargo(Console console) {
        System.out.println("Cargo:");
        System.out.println("1) ATENDENTE");
        System.out.println("2) GERENTE");
        int op = console.lerOpcao("Escolha: ", 1, 2);
        return (op == 1) ? Cargo.ATENDENTE : Cargo.GERENTE;
    }

    private static boolean lerDisponibilidade(Console console) {
        System.out.println("Disponibilidade:");
        System.out.println("1) SIM");
        System.out.println("2) NÃO");
        int op = console.lerOpcao("Escolha: ", 1, 2);
        return op == 1;
    }

    private static void validarCpfUnico(String cpf) {
        if (buscarFuncionarioPorCpf(cpf) != null) {
            throw new IllegalArgumentException("CPF já cadastrado como funcionário.");
        }
        if (buscarClientePorCpf(cpf) != null) {
            throw new IllegalArgumentException("CPF já cadastrado como cliente.");
        }
    }

    private static void validarCpfUnicoAoAtualizarCliente(int id, String cpfNovo) {
        for (Cliente c : Cliente.listarClientes()) {
            if (c.getId() != id && Objects.equals(c.getCpf(), cpfNovo)) {
                throw new IllegalArgumentException("CPF já cadastrado como cliente.");
            }
        }
        for (Funcionario f : Funcionario.listarFuncionarios()) {
            if (Objects.equals(f.getCpf(), cpfNovo)) {
                throw new IllegalArgumentException("CPF já cadastrado como funcionário.");
            }
        }
    }

    private static void validarCpfUnicoAoAtualizarFuncionario(int id, String cpfNovo) {
        for (Funcionario f : Funcionario.listarFuncionarios()) {
            if (f.getId() != id && Objects.equals(f.getCpf(), cpfNovo)) {
                throw new IllegalArgumentException("CPF já cadastrado como funcionário.");
            }
        }
        for (Cliente c : Cliente.listarClientes()) {
            if (Objects.equals(c.getCpf(), cpfNovo)) {
                throw new IllegalArgumentException("CPF já cadastrado como cliente.");
            }
        }
    }

    private static Funcionario buscarFuncionarioPorCpf(String cpf) {
        for (Funcionario f : Funcionario.listarFuncionarios()) {
            if (Objects.equals(f.getCpf(), cpf))
                return f;
        }
        return null;
    }

    private static Funcionario buscarFuncionarioPorId(int id) {
        for (Funcionario f : Funcionario.listarFuncionarios()) {
            if (f.getId() == id)
                return f;
        }
        return null;
    }

    private static Cliente buscarClientePorCpf(String cpf) {
        for (Cliente c : Cliente.listarClientes()) {
            if (Objects.equals(c.getCpf(), cpf))
                return c;
        }
        return null;
    }

    private static Cliente buscarClientePorId(int id) {
        for (Cliente c : Cliente.listarClientes()) {
            if (c.getId() == id)
                return c;
        }
        return null;
    }

    private static Carro buscarCarroPorPlaca(String placa) {
        for (Carro c : Carro.listarCarros()) {
            if (Objects.equals(c.getPlaca(), placa))
                return c;
        }
        return null;
    }

    private static Aluguel buscarAluguelAtivoPorCliente(String cpfCliente) {
        for (Aluguel a : Aluguel.listarAlugueis()) {
            if (!a.isFinalizado() && Objects.equals(a.getCliente().getCpf(), cpfCliente)) {
                return a;
            }
        }
        return null;
    }

    private static Aluguel buscarAluguelPorId(int id) {
        for (Aluguel a : Aluguel.listarAlugueis()) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    private static int maiorIdAluguel() {
        int maior = 0;
        for (Aluguel a : Aluguel.listarAlugueis()) {
            if (a.getId() > maior)
                maior = a.getId();
        }
        return maior;
    }
}
