package br.com.zenon;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FraudAnalyzerTest {

    @Test
    void calculaOperacoesDeFraudeComStreams() {
        var transactions = List.of(
                transaction(1, TransactionType.TRANSFER, "200.00", "C1", true),
                transaction(2, TransactionType.CASH_OUT, "500.00", "C2", true),
                transaction(3, TransactionType.TRANSFER, "300.00", "C1", true),
                transaction(4, TransactionType.PAYMENT, "900.00", "C3", false)
        );
        var analyzer = new FraudAnalyzer(transactions);

        assertEquals(3, analyzer.fraudCount());
        assertEquals(List.of(new BigDecimal("500.00"), new BigDecimal("300.00"), new BigDecimal("200.00")),
                analyzer.topFraudsByAmount(3));
        assertEquals(List.of("C2", "C1"), analyzer.topSuspiciousClients(5));
        assertEquals(new BigDecimal("1000.00"), analyzer.totalLoss());
        assertEquals(1L, analyzer.fraudsByType().get(TransactionType.CASH_OUT));
        assertEquals(2L, analyzer.fraudsByType().get(TransactionType.TRANSFER));
    }

    private static Transaction transaction(int step, TransactionType type, String amount,
                                           String originName, boolean fraud) {
        return new Transaction(step, type, new BigDecimal(amount),
                new TransactionCustomer(originName, BigDecimal.ZERO, BigDecimal.ZERO),
                new TransactionCustomer("M1", BigDecimal.ZERO, BigDecimal.ZERO), fraud, false);
    }
}
