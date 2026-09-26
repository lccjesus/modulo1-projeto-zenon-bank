package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FraudAnalyzer {
    private final List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        this.transactions = List.copyOf(transactions);
    }

    public List<Transaction> frauds() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .toList();
    }

    public long fraudCount() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .count();
    }

    public List<BigDecimal> topFraudsByAmount(int limit) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .sorted(java.util.Comparator.reverseOrder())
                .limit(limit)
                .toList();
    }

    public List<String> topSuspiciousClients(int limit) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(java.util.Comparator.comparing(Transaction::amount).reversed())
                .map(transaction -> transaction.origin().name())
                .distinct()
                .limit(limit)
                .toList();
    }

    public BigDecimal totalLoss() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType, Long> fraudsByType() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
    }
}
