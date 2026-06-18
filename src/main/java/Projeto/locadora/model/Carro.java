package Projeto.locadora.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Carro {
    private String modelo;
    private String placa;
    private String cambio;
    private double valorDiaria;
    private boolean disponivel;

    private static final List<Carro> listCarros = new ArrayList<>();

    public Carro(String modelo, String placa, String cambio, double valorDiaria, boolean disponivel) {
        this.modelo = modelo;
        this.placa = placa;
        this.cambio = cambio;
        this.valorDiaria = valorDiaria;
        this.disponivel = disponivel;
    }

    public static void cadastrarCarro(String modelo, String placa, String cambio, double valorDiaria,
            boolean disponivel) {
        if (buscarPorPlaca(placa) != null) {
            throw new IllegalArgumentException("Já existe carro com essa placa.");
        }
        listCarros.add(new Carro(modelo, placa, cambio, valorDiaria, disponivel));
    }

    public static void atualizarCarro(int id, String modelo, String placa, String cambio, double valorDiaria,
            boolean disponivel) {
        Carro c = buscarPorIdOuPlaca(id, placa);
        if (c == null) {
            throw new IllegalArgumentException("Carro não encontrado.");
        }
        c.modelo = modelo;
        c.placa = placa;
        c.cambio = cambio;
        c.valorDiaria = valorDiaria;
        c.disponivel = disponivel;
    }

    public static void visualizarCarro(String placa) {
        Carro c = buscarPorPlaca(placa);
        if (c == null) {
            System.out.println("Carro não encontrado.");
            return;
        }
        System.out.println(c.exibirFichaTecnica());
    }

    public static List<Carro> listarCarros() {
        return listCarros;
    }

    public void desativarCarro() {
        disponivel = false;
    }

    public static List<Carro> listarCarrosDisponiveis() {
        List<Carro> disp = new ArrayList<>();
        for (Carro c : listCarros) {
            if (c.disponivel) {
                disp.add(c);
            }
        }
        return disp;
    }

    public String exibirFichaTecnica() {
        return "Modelo: " + modelo
                + " | Placa: " + placa
                + " | Câmbio: " + cambio
                + " | Diária: R$ " + String.format("%.2f", valorDiaria)
                + " | Disponível: " + (disponivel ? "SIM" : "NÃO");
    }

    public void atualizarStatus(boolean status) {
        this.disponivel = status;
    }

    public String getModelo() {
        return modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public String getCambio() {
        return cambio;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    private static Carro buscarPorPlaca(String placa) {
        for (Carro c : listCarros) {
            if (Objects.equals(c.placa, placa)) {
                return c;
            }
        }
        return null;
    }

    private static Carro buscarPorIdOuPlaca(int id, String placa) {
        if (id > 0 && id <= listCarros.size()) {
            return listCarros.get(id - 1);
        }
        return buscarPorPlaca(placa);
    }
}
