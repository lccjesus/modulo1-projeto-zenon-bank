package br.com.zenon;

import java.math.BigDecimal;
import java.util.Objects;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {
    public TransactionCustomer {
        Objects.requireNonNull(name, "name should not be null");
        Objects.requireNonNull(oldBalance, "oldBalance should not be null");
        Objects.requireNonNull(newBalance, "newBalance should not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("o nome não pode estar vazio");
        }
        if (oldBalance.signum() < 0) {
            throw new IllegalArgumentException("o valor de oldBalance não pode ser negativo: " + oldBalance);
        }
        if (newBalance.signum() < 0) {
            throw new IllegalArgumentException("o valor de newBalance não pode ser negativo: " + newBalance);
        }
    }
}
