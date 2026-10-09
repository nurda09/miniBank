package org.example;

import javax.security.auth.login.AccountNotFoundException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Service {
    private final DataSource dataSource;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Service(DataSource dataSource, AccountRepository accountRepository, TransactionRepository transactionRepository){
        this.dataSource = dataSource;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account openAccount(String name, double initialBalance){
        if(initialBalance < 0) throw new IllegalArgumentException("Начальный баланс не может быть меньше 0, брот ищка");
        return accountRepository.save(new Account(name,initialBalance));
    }

    public Account getAccount(int id) throws AccountNotFoundException{
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("счет#" + id + "не найден"));
    }
    public void deposit(int accountId, double amount){
        validAmount(amount);

        try(Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try {
                Account account = accountRepository.findByIdForUpdate(con,accountId)
                        .orElseThrow(()-> new AccountNotFoundException("Счет #" + accountId + "не найден"));
                double newBalance = account.getBalance() + amount;
                accountRepository.updateBalance(con, accountId,newBalance);

                Transactions transactions = new Transactions(null, accountId, amount, Transactions.Type.Deposit);
                transactionRepository.save(con,transactions);

                con.commit();
            } catch (Exception e){
                con.rollback();
                throw new RuntimeException("Не удалось выполнить пополнение" + e);
            } finally {
                con.setAutoCommit(true);
            }


        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключение к базе данных",e);
        }

    }

    public void withdraw(int accountid, double amount){
        validAmount(amount);

        try(Connection con = dataSource.getConnection()){

            con.setAutoCommit(false);
            try {
                Account account = accountRepository.findByIdForUpdate(con, accountid)
                        .orElseThrow(()-> new AccountNotFoundException("счет#"+accountid + "не найден"));
                double newBalance = account.getBalance() - amount;
                accountRepository.updateBalance(con, accountid, newBalance);

                Transactions transactions = new Transactions(accountid, null, amount,Transactions.Type.Withdrawal);
                transactionRepository.save(con, transactions);
                con.commit();

            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException("Не смогли найти твой ебаный акк");
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к базе данных",e);
        }

    }

    public void transfer(int fromAccountId, int toAccountId, double amount){
        validAmount(amount);

        if(fromAccountId == toAccountId){
            throw new IllegalArgumentException("Нельзя перевести деньги самому себе");
        }
        try(Connection con = dataSource.getConnection()){
            con.setAutoCommit(false);

            try{
                // блокируем счета сначало с помощью метода потом
                // проверка аккаунт на то что  если вообще такой аккаунт
                // проверка есть ли деньги на счету этого аккаунта
                // сколько остается при переводе !< 0
                // перезапись этих данных

                int firstLockId = Math.max(fromAccountId, toAccountId);
                int secondLockId = Math.min(fromAccountId, toAccountId);

                // проверка есть ли аккаунт
                Account from = accountRepository.findByIdForUpdate(con, firstLockId)
                        .orElseThrow(()-> new AccountNotFoundException("Счет #" + firstLockId + " не найден"));
                Account to = accountRepository.findByIdForUpdate(con, secondLockId)
                        .orElseThrow(()-> new AccountNotFoundException("Счет#" + secondLockId + " не найден"));

                // проверка есть ли деньги вообще на счет from account, можно посути заодно сделать не будет ли минус при переводе

                if (from.getBalance() > 0 && (from.getBalance() - amount) >= 0){
                    accountRepository.updateBalance(con, from.getId(), from.getBalance()-amount);
                    accountRepository.updateBalance(con, to.getId(), to.getBalance()+amount);

                    Transactions transactions = new Transactions(fromAccountId, toAccountId, amount, Transactions.Type.Transfer);
                    transactionRepository.save(con, transactions);

                    con.commit();
                }
                else System.out.println("Нету достаточно средств");


            } catch (Exception e){
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<Transactions> getHistoryById(int accountId) throws AccountNotFoundException {
        getAccount(accountId); // проверка есть ли у нас такой аккаунт
        return transactionRepository.findByAccountId(accountId);
    }



    public void validAmount(double amount){
        if(amount <= 0) throw new IllegalArgumentException("Сумма должна быть больше 0");
    }

}
