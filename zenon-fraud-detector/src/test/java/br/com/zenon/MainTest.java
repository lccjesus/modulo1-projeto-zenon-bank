package br.com.zenon;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

    @Test
    void reportsFileErrorsThroughSpecificException() {
        assertThrows(TransactionImportException.class,
                () -> new TransactionIngestor().read(Path.of("data/arquivo-inexistente.csv")));
    }
}
