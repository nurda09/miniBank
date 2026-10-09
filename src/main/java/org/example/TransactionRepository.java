package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TransactionRepository {
    void save(Connection con, Transactions transactions) throws SQLException;
    List<Transactions> findByAccountId(int accountId); // -> возвращает список всех транзакции которое совершал конкретный аккаунт
}
