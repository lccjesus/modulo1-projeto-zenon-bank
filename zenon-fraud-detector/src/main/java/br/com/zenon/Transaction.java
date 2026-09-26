package br.com.zenon;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(int step, TransactionType type, BigDecimal amount, TransactionCustomer origin,
                          TransactionCustomer recipient, boolean isFraud, boolean isFlaggedFraud) {
    public Transaction {
        Objects.requireNonNull(type, "type should not be null");
        Objects.requireNonNull(amount, "amount should not be null");
        Objects.requireNonNull(origin, "origin should not be null");
        Objects.requireNonNull(recipient, "recipient should not be null");
        if (step < 0) {
            throw new IllegalArgumentException("o valor de step não pode ser negativo: " + step);
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount should be positive: " + amount);
        }
    }
}
