package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface AccountRepository {
    // account save(Account account)
    // через этот репозитории я могу написать сам сервис типо
    // если допустим я могу найти человека по айдишке,Я могу сделать все что можно с ним по сервису
    // а что насчет транзакшн репозитории, я про него пока хз; напишу только аккаунт репо


    Account save(Account account); // <- возвращает обьект класса аккаунт
    Optional<Account> findById(int id); // <- не должен возвращать нулл

    boolean updateBalance(Connection con, int accountId, Double newBalance) throws SQLException; // в sql коде если будут ошибки то нужны будут роллбаки, or commits
    // throws SQLException это для того чтобы не писать catch


    Optional<Account> findByIdForUpdate(Connection con, int accountId) throws SQLException; // также с коннекшн траблс, и не должен возвращать нуллпоинтер експешн

}
