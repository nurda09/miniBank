package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        AccountRepository accountRepository = new JdbcAccountRepository(DataBaseConfig.getDataSource());
        TransactionRepository repository = new JDBCTransactionRepository(DataBaseConfig.getDataSource());

        Service service = new Service(DataBaseConfig.getDataSource(), accountRepository,repository);
        BankingCLI cli = new BankingCLI(service);
        cli.run();
    }
}