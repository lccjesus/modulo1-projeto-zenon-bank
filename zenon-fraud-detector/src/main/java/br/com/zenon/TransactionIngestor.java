package br.com.zenon;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionIngestor {

    // Define o limite de transações processadas e a quantidade de colunas de cada registro.
    private static final int MAX_TRANSACTIONS = 1_000;
    private static final int EXPECTED_COLUMNS = 11;

    // Recebe o caminho do CSV e retorna as transações convertidas e validadas.
    public List<Transaction> read(Path path) {
        try {
            // Carrega todo o arquivo em memória usando UTF-8, antes de aplicar o limite.
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            // O cabeçalho ocupa a linha 1; a primeira transação será identificada como linha 2.
            // O contador é atualizado pela lambda no processamento sequencial do stream.
            AtomicInteger lineNumber = new AtomicInteger(1);

            return lines.stream() // Cria um fluxo sequencial a partir das linhas lidas.
                    .skip(1) // Ignora o cabeçalho do CSV.
                    .limit(MAX_TRANSACTIONS) // Processa no máximo 1.000 transações.
                    // Converte cada linha e fornece sua posição para identificar possíveis erros.
                    .map(line -> parseTransaction(line, path, lineNumber.incrementAndGet()))
                    .toList(); // Reúne as transações em uma lista não modificável.
        } catch (IOException ex) {
            // Diferencia falhas de acesso/leitura do arquivo de falhas no conteúdo do CSV.
            throw new UncheckedIOException("Erro ao ler o arquivo: " + path, ex);
        }
    }

    // Acrescenta o arquivo e o número da linha aos erros de conversão ou validação.
    private static Transaction parseTransaction(String line, Path path, int lineNumber) {
        try {
            return parseTransaction(line);
        } catch (IllegalArgumentException ex) {
            // Preserva a exceção original como causa para facilitar o diagnóstico.
            throw new IllegalArgumentException(
                    "Transação inválida no arquivo " + path + ", linha " + lineNumber
                            + ": " + ex.getMessage(), ex);
        }
    }

    // Converte uma linha do CSV em um objeto Transaction.
    private static Transaction parseTransaction(String line) {
        // Separa por vírgulas; -1 preserva campos vazios no final para permitir sua validação.
        // Este formato simples não trata campos entre aspas que contenham vírgulas.
        String[] columns = line.split(",", -1);
        // Confere a estrutura antes de acessar as posições das colunas.
        if (columns.length != EXPECTED_COLUMNS) {
            throw new IllegalArgumentException(
                    "Esperadas " + EXPECTED_COLUMNS + " colunas, mas encontradas " + columns.length);
        }

        // Remove espaços nas extremidades e rejeita qualquer campo vazio.
        for (int index = 0; index < columns.length; index++) {
            columns[index] = columns[index].strip();
            if (columns[index].isEmpty()) {
                throw new IllegalArgumentException("Coluna " + (index + 1) + " está vazia");
            }
        }

        // Converte etapa, tipo e valor; BigDecimal representa o valor monetário decimal.
        int step = Integer.parseInt(columns[0]);
        TransactionType type = TransactionType.valueOf(columns[1]);
        BigDecimal amount = new BigDecimal(columns[2]);

        // Monta o cliente de origem com seu identificador e os saldos anterior e posterior.
        var origin = new TransactionCustomer(
                columns[3], new BigDecimal(columns[4]), new BigDecimal(columns[5]));
        // Monta o destinatário com seu identificador e os saldos anterior e posterior.
        var recipient = new TransactionCustomer(
                columns[6], new BigDecimal(columns[7]), new BigDecimal(columns[8]));

        // Converte os indicadores de fraude e de sinalização de fraude para booleanos.
        boolean isFraud = parseFlag(columns[9], "isFraud");
        boolean isFlaggedFraud = parseFlag(columns[10], "isFlaggedFraud");

        // Reúne os dados convertidos na transação que será incluída na lista de resultados.
        return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
    }

    // Aceita somente 0 (false) e 1 (true); outros valores geram erro com o nome do campo.
    private static boolean parseFlag(String value, String fieldName) {
        return switch (value) {
            case "0" -> false;
            case "1" -> true;
            default -> throw new IllegalArgumentException(fieldName + " deve ser 0 ou 1: " + value);
        };
    }
}
