package br.com.zenon;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class TransactionIngestor {
    private static final int MAX_TRANSACTIONS = 50_000;
    private static final String[] COLUMNS = {
            "step", "type", "amount", "nameOrig", "oldbalanceOrg", "newbalanceOrig",
            "nameDest", "oldbalanceDest", "newbalanceDest", "isFraud", "isFlaggedFraud"
    };

    public List<Transaction> read(Path path) {
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            return lines.skip(1).limit(MAX_TRANSACTIONS)
                    .map(new LineParser()).flatMap(Optional::stream).toList();
        } catch (IOException | UncheckedIOException ex) {
            throw new TransactionImportException("Erro ao ler o arquivo: " + path, ex);
        }
    }

    private static Transaction parseTransaction(String line) {
        String[] columns = line.split(",", -1);
        if (columns.length != COLUMNS.length) {
            throw new IllegalArgumentException("eram esperadas " + COLUMNS.length
                    + " colunas, mas foram encontradas " + columns.length);
        }
        for (int index = 0; index < columns.length; index++) {
            columns[index] = columns[index].strip();
            if (columns[index].isBlank()) {
                throw new IllegalArgumentException("a coluna '" + COLUMNS[index] + "' está vazia");
            }
        }

        int step = Integer.parseInt(columns[0]);
        TransactionType type = TransactionType.valueOf(columns[1]);
        BigDecimal amount = new BigDecimal(columns[2]);
        var origin = new TransactionCustomer(columns[3], new BigDecimal(columns[4]), new BigDecimal(columns[5]));
        var recipient = new TransactionCustomer(columns[6], new BigDecimal(columns[7]), new BigDecimal(columns[8]));
        return new Transaction(step, type, amount, origin, recipient,
                parseFlag(columns[9], "isFraud"), parseFlag(columns[10], "isFlaggedFraud"));
    }

    private static boolean parseFlag(String value, String fieldName) {
        return switch (value) {
            case "0" -> false;
            case "1" -> true;
            default -> throw new IllegalArgumentException(fieldName + " deve ser 0 ou 1: " + value);
        };
    }

    private static final class LineParser implements Function<String, Optional<Transaction>> {
        @Override
        public Optional<Transaction> apply(String line) {
            try {
                return Optional.of(parseTransaction(line));
            } catch (IllegalArgumentException ex) {
                System.err.println("Erro: " + line + " | " + ex);
                return Optional.empty();
            }
        }
    }
}
