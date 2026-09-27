package br.com.zenon;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

public class Main {

    void main() {
        /*var t1 = new Transaction(1, TransactionType.PAYMENT, new BigDecimal("9839.64"),
                new TransactionCustomer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
                new TransactionCustomer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false, false
        );

        var t2 = new Transaction(743, TransactionType.CASH_OUT, new BigDecimal("850002.52"),
                new TransactionCustomer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
                new TransactionCustomer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
                true, false
        );

        IO.println(t1);
        IO.println(t2);*/

        IO.println("----------------------------------------------------------------------------------------");

        var transactionIngestor = new TransactionIngestor();
        List<Transaction> transactions;
        try {
            transactions = transactionIngestor.read(Path.of("data/data.csv"));
        } catch (TransactionImportException ex) {
            System.err.println(ex.getMessage());
            return;
        } catch (Exception ex) {
            System.err.println("Erro inesperado durante a execução: " + ex.getMessage());
            return;
        }
        var analyzer = new FraudAnalyzer(transactions);
        IO.println("1. Total de Fraudes: " + analyzer.fraudCount());
        IO.println("2. Top 3 Fraudes de Maior Valor:");
        analyzer.topFraudsByAmount(3).forEach(IO::println);
        IO.println("3. Clientes Suspeitos:");
        analyzer.topSuspiciousClients(5).forEach(IO::println);
        IO.println("4. Prejuízo Total: " + analyzer.totalLoss());
        IO.println("5. Fraudes por Tipo:");
        analyzer.fraudsByType().forEach((type, count) -> IO.println(" - " + type + ": " + count));

    }

}
