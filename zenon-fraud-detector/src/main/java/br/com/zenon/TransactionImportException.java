package br.com.zenon;

public class TransactionImportException extends RuntimeException {
    public TransactionImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
