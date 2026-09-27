package br.com.zenon;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionIngestorTest {

    @Test
    void deveLimitarImportacaoACinquentaMilTransacoes(@TempDir Path tempDir) throws IOException {
        var csv = tempDir.resolve("data.csv");
        var header = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";
        var row = "1,PAYMENT,1.00,C1000001,1.00,0.00,M1000001,0.00,0.00,0,0";
        var content = java.util.stream.Stream.concat(
                java.util.stream.Stream.of(header),
                java.util.stream.Stream.generate(() -> row).limit(50001)
        ).toList();
        Files.write(csv, content);

        assertEquals(50000, new TransactionIngestor().read(csv).size());
    }

    @Test
    void deveIgnorarLinhasInvalidasEManterTransacoesValidas() {
        var transactions = new TransactionIngestor().read(Path.of("data", "bad_data.csv"));

        assertEquals(9, transactions.size());

        assertTrue(transactions.stream().noneMatch(transaction -> transaction.step() < 0
                || transaction.amount().signum() < 0
                || transaction.origin().oldBalance().signum() < 0
                || transaction.origin().newBalance().signum() < 0
                || transaction.recipient().oldBalance().signum() < 0
                || transaction.recipient().newBalance().signum() < 0));
    }
}
