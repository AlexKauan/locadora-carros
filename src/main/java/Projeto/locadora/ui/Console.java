package Projeto.locadora.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Console {
    private final Scanner scanner;

    public Console(Scanner scanner) {
        this.scanner = scanner;
    }

    public String lerLinha(String rotulo) {
        System.out.print(rotulo);
        return scanner.nextLine();
    }

    public String lerNaoVazio(String rotulo) {
        while (true) {
            String v = lerLinha(rotulo).trim();
            if (!v.isBlank()) {
                return v;
            }
            System.out.println("Valor inválido. Tente novamente.");
        }
    }

    public int lerInteiro(String rotulo) {
        while (true) {
            String v = lerNaoVazio(rotulo);
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Tente novamente.");
            }
        }
    }

    public double lerDouble(String rotulo) {
        while (true) {
            String v = lerNaoVazio(rotulo).replace(",", ".");
            try {
                return Double.parseDouble(v);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Tente novamente.");
            }
        }
    }

    public LocalDate lerData(String rotulo) {
        while (true) {
            String v = lerNaoVazio(rotulo);
            try {
                return LocalDate.parse(v);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato yyyy-MM-dd.");
            }
        }
    }

    public LocalDate lerDataOpcional(String rotulo) {
        while (true) {
            String v = lerLinha(rotulo).trim();
            if (v.isBlank()) return null;
            try {
                return LocalDate.parse(v);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato yyyy-MM-dd.");
            }
        }
    }

    public int lerOpcao(String rotulo, int min, int max) {
        while (true) {
            int op = lerInteiro(rotulo);
            if (op >= min && op <= max) return op;
            System.out.println("Opção inválida. Tente novamente.");
        }
    }
}

