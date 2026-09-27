# Tarefa 05 Streams Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Process up to 50,000 transactions with Stream API operations for fraud filtering, sorting, aggregation, and grouping.

**Architecture:** `TransactionIngestor` loads the CSV into a list, `FraudAnalyzer` exposes independent stream-based calculations, and `Main` invokes and prints each result.

**Tech Stack:** Java 25, Maven, JUnit 5, Stream API.

**Spec:** User-provided Tarefa 05 - Streams.

## Global Constraints

- Load a maximum of 50,000 data rows.
- Use a new stream from the transaction list for each calculation.
- Report fraud count, top three fraud amounts, five distinct suspicious origin clients, total loss, and frauds grouped by type.

## Review Focus

- More than 50,000 rows must be truncated at exactly 50,000.
- Non-fraud transactions must not affect any calculation.
- Suspicious clients must be distinct and ordered by fraud amount.
- Empty fraud input must produce zero/empty results without errors.
- Each analyzer operation must remain reusable after another operation has consumed its stream.

### Task 1: Align ingestion limit and verify analyzer behavior

**Files:**
- Modify: `src/main/java/br/com/zenon/TransactionIngestor.java`
- Test: `src/test/java/br/com/zenon/TransactionIngestorTest.java`
- Test: `src/test/java/br/com/zenon/FraudAnalyzerTest.java`

**Interfaces:**
- Consumes: `List<Transaction>` from `TransactionIngestor`.
- Produces: Existing `FraudAnalyzer` methods used by `Main`.

- [ ] Write tests for the 50,000-row limit and analyzer calculations.
- [ ] Run the focused tests and verify the limit test fails while the existing analyzer behavior is preserved.
- [ ] Set `MAX_TRANSACTIONS` to `50_000`.
- [ ] Run the full Maven test suite.

### Task 2: Verify application output and repository delivery

**Files:**
- Inspect: `src/main/java/br/com/zenon/Main.java`

- [ ] Run the application against `data/data.csv` when present and inspect all five output sections.
- [ ] Commit the completed implementation on `tarefa/05-streams`.
- [ ] Push `tarefa/05-streams` to the configured remote.
