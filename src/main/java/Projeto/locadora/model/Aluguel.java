package Projeto.locadora.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Aluguel {
    private LocalDate dataInicio;
    private LocalDate dataFimPrevista;
    private LocalDate dataDevolucaoEfetiva;
    private double valorDiaria;
    private double valorAluguel;
    private int id;
    private boolean finalizado;

    private Funcionario funcionario;
    private Cliente cliente;
    private Carro carro;

    private static final List<Aluguel> listAlugueis = new ArrayList<>();

    public Aluguel(
            int id,
            LocalDate dataInicio,
            LocalDate dataFimPrevista,
            LocalDate dataDevolucaoEfetiva,
            double valorDiaria,
            double valorAluguel,
            boolean finalizado,
            Funcionario funcionario,
            Cliente cliente,
            Carro carro) {
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFimPrevista = dataFimPrevista;
        this.dataDevolucaoEfetiva = dataDevolucaoEfetiva;
        this.valorDiaria = valorDiaria;
        this.valorAluguel = valorAluguel;
        this.finalizado = finalizado;
        this.funcionario = funcionario;
        this.cliente = cliente;
        this.carro = carro;
    }

    public static void cadastrarAluguel(
            LocalDate dataInicio,
            LocalDate dataFimPrevista,
            double valorAluguel,
            boolean finalizado,
            Cliente cliente,
            Carro carro,
            Funcionario funcionario) {
        if (cliente.getStatus() != Status.ATIVO) {
            throw new IllegalStateException("Cliente não pode iniciar locação (status " + cliente.getStatus() + ").");
        }
        if (existeAluguelAtivoCliente(cliente.getCpf())) {
            throw new IllegalStateException("Cliente já possui um aluguel ativo.");
        }
        if (!carro.isDisponivel()) {
            throw new IllegalStateException("Carro indisponível.");
        }
        int id = proximoId();
        double diaria = carro.getValorDiaria();
        Aluguel novo = new Aluguel(id, dataInicio, dataFimPrevista, null, diaria, valorAluguel, finalizado, funcionario,
                cliente, carro);
        listAlugueis.add(novo);
        carro.atualizarStatus(false);
    }

    public static void visualizarAluguel(int id) {
        Aluguel a = buscarPorId(id);
        if (a == null) {
            System.out.println("Aluguel não encontrado.");
            return;
        }
        System.out.println(a.emitirRecibo());
    }

    public static void atualizarAluguel(
            int id,
            LocalDate dataInicio,
            LocalDate dataFimPrevista,
            LocalDate dataDevolucaoEfetiva,
            double valorAluguel,
            boolean finalizado,
            Cliente cliente,
            Carro carro,
            Funcionario funcionario) {
        Aluguel a = buscarPorId(id);
        if (a == null) {
            throw new IllegalArgumentException("Aluguel não encontrado.");
        }
        a.dataInicio = dataInicio;
        a.dataFimPrevista = dataFimPrevista;
        a.dataDevolucaoEfetiva = dataDevolucaoEfetiva;
        a.valorDiaria = carro.getValorDiaria();
        a.valorAluguel = valorAluguel;
        a.finalizado = finalizado;
        a.cliente = cliente;
        a.carro = carro;
        a.funcionario = funcionario;
    }

    public void cancelarAluguel() {
        finalizado = true;
        carro.atualizarStatus(true);
    }

    public static List<Aluguel> listarAlugueis() {
        return listAlugueis;
    }

    public static List<Aluguel> listarAlugueisAtrasados() {
        List<Aluguel> atrasados = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        for (Aluguel aluguel : listAlugueis) {
            if (!aluguel.finalizado && hoje.isAfter(aluguel.dataFimPrevista)) {
                atrasados.add(aluguel);
            }
        }
        return atrasados;
    }

    public int calcularDiasAtraso() {
        if (dataDevolucaoEfetiva == null) {
            return 0;
        }
        long diasAtraso = ChronoUnit.DAYS.between(dataFimPrevista, dataDevolucaoEfetiva);
        if (diasAtraso <= 0) {
            return 0;
        }
        return (int) diasAtraso;
    }

    public double calcularValorBase() {
        long diasPrevistos = ChronoUnit.DAYS.between(dataInicio, dataFimPrevista);
        if (diasPrevistos < 0)
            diasPrevistos = 0;
        return diasPrevistos * valorDiaria;
    }

    public double calcularMulta() {
        if (dataDevolucaoEfetiva == null) {
            return 0.0;
        }
        int diasAtraso = calcularDiasAtraso();
        if (diasAtraso <= 0) {
            return 0.0;
        }
        return diasAtraso * (valorDiaria + 50.0);
    }

    public double calcularValorTotal() {
        return calcularValorBase() + calcularMulta();
    }

    public void finalizarContrato() {
        if (finalizado) {
            throw new IllegalStateException("Aluguel já está finalizado.");
        }
        dataDevolucaoEfetiva = LocalDate.now();
        finalizado = true;
        valorAluguel = calcularValorTotal();
        carro.atualizarStatus(true);
    }

    public String emitirRecibo() {
        String devolucao = (dataDevolucaoEfetiva == null) ? "EM ABERTO" : dataDevolucaoEfetiva.toString();
        double base = calcularValorBase();
        double multa = calcularMulta();
        double total = (finalizado ? valorAluguel : base);

        return "RECIBO DE LOCAÇÃO\n"
                + "ID do Aluguel: " + id + "\n"
                + "Cliente: " + cliente.getNome() + " | CPF: " + cliente.getCpf() + "\n"
                + "Carro: " + carro.getModelo() + " | Placa: " + carro.getPlaca() + "\n"
                + "Início: " + dataInicio + "\n"
                + "Fim Previsto: " + dataFimPrevista + "\n"
                + "Devolução Efetiva: " + devolucao + "\n"
                + "Valor Base: R$ " + String.format("%.2f", base) + "\n"
                + "Multa: R$ " + String.format("%.2f", multa) + "\n"
                + "Total: R$ " + String.format("%.2f", total) + "\n";
    }

    public static void verificarEAtualizarBloqueios() {
        for (Aluguel aluguel : listarAlugueisAtrasados()) {
            if (aluguel.cliente.getStatus() == Status.ATIVO) {
                aluguel.cliente.setStatus(Status.BLOQUEADO);
            }
        }
    }

    public int getId() {
        return id;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFimPrevista() {
        return dataFimPrevista;
    }

    public LocalDate getDataDevolucaoEfetiva() {
        return dataDevolucaoEfetiva;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Carro getCarro() {
        return carro;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    public double getValorAluguel() {
        return valorAluguel;
    }

    private static int proximoId() {
        int maior = 0;
        for (Aluguel a : listAlugueis) {
            if (a.id > maior) {
                maior = a.id;
            }
        }
        return maior + 1;
    }

    private static Aluguel buscarPorId(int id) {
        for (Aluguel a : listAlugueis) {
            if (a.id == id) {
                return a;
            }
        }
        return null;
    }

    private static boolean existeAluguelAtivoCliente(String cpf) {
        for (Aluguel a : listAlugueis) {
            if (!a.finalizado && Objects.equals(a.cliente.getCpf(), cpf)) {
                return true;
            }
        }
        return false;
    }
}
