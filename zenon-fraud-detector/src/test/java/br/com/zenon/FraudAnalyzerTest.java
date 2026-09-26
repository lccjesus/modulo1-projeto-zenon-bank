package br.com.zenon;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FraudAnalyzerTest {
    private final List<Transaction> transactions = List.of(
            transaction(TransactionType.TRANSFER, "C1", "10.00", true),
            transaction(TransactionType.CASH_OUT, "C2", "50.00", true),
            transaction(TransactionType.TRANSFER, "C1", "30.00", true),
            transaction(TransactionType.PAYMENT, "C3", "90.00", false),
            transaction(TransactionType.CASH_OUT, "C4", "20.00", true)
    );

    @Test
    void calculaResumoDasFraudesComStreams() {
        var analyzer = new FraudAnalyzer(transactions);

        assertEquals(4, analyzer.fraudCount());
        assertEquals(List.of(new BigDecimal("50.00"), new BigDecimal("30.00"), new BigDecimal("20.00")),
                analyzer.topFraudsByAmount(3));
        assertEquals(List.of("C2", "C1", "C4"), analyzer.topSuspiciousClients(5));
        assertEquals(new BigDecimal("110.00"), analyzer.totalLoss());
        assertEquals(2L, analyzer.fraudsByType().get(TransactionType.TRANSFER));
        assertEquals(2L, analyzer.fraudsByType().get(TransactionType.CASH_OUT));
    }

    private static Transaction transaction(TransactionType type, String origin, String amount, boolean fraud) {
        var customer = new TransactionCustomer(origin, new BigDecimal("100.00"), new BigDecimal("0.00"));
        return new Transaction(1, type, new BigDecimal(amount), customer, customer, fraud, false);
    }
}
