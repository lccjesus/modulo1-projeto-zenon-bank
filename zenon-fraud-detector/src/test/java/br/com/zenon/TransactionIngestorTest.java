package br.com.zenon;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionIngestorTest {

    @Test
    void deveIgnorarLinhasInvalidasEManterTransacoesValidas() {
        var transactions = new TransactionIngestor().read(Path.of("data", "bad_data.csv"));

        assertEquals(7, transactions.size());

        assertTrue(transactions.stream().noneMatch(transaction -> transaction.step() <= 0
                || transaction.amount().signum() <= 0
                || transaction.origin().oldBalance().signum() < 0
                || transaction.origin().newBalance().signum() < 0
                || transaction.recipient().oldBalance().signum() < 0
                || transaction.recipient().newBalance().signum() < 0));
    }
}
