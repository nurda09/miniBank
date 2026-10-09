package org.example;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class JdbcAccountRepository implements AccountRepository {

    private final DataSource dataSource;

    public JdbcAccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Account save(Account account) {
        //хорошо если рассуждать то при сохранений мы сохраняем что? вот вопрос который тут стоит, мы должен использовать
        // препайред стейтмент чтобы все заработалось
        // используем трай чтобы, в случае чего просто кинуло експешн
        String sql = "Insert into account(name, balance) values(?,?)";
        try(Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, account.getName());
            ps.setDouble(2,account.getBalance());

            ps.executeUpdate();

            try(ResultSet keys = ps.getGeneratedKeys()){ // запрос на число которое снегерировалось
                if(keys.next()){
                    account.setId(keys.getInt(1)); // если оно не сгенериролось то мы ее вставляем в поле id
                }
            }
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось сохранить аккаунт",e);
        }

    }

    @Override
    public Optional<Account> findById(int id) {
        // так тут у нас метод который возвращает опшионал что предотвращает возврашение нулл
        String sql = "Select * from account where id = ?";
        try(Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1,id);
            try(ResultSet rs = ps.executeQuery()){

                if(rs.next()){
                    // сохраняем данные в обьекты
                    String name = rs.getString("name");
                    double balance = rs.getDouble("balance");

                    Account account = new Account(id, name, balance);
                    return Optional.of(account);
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Введи инт айди сосунок",e);
        }
    }


    @Override
    public boolean updateBalance(Connection con, int accountId, Double newBalance)throws SQLException {
        // сначало надо понять что, мы получаем
        // мы получаем тру или фалс, и почему тут Connection con стоит?
        // иду получать ответ на этот вопрос
        // connection в самих аргументах нужен только чтобы были коммиты и роллбаки

        String sql = "Update account Set balance = ? where id = ?";

        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setInt(2,accountId);

            return ps.executeUpdate()>0;
        }
    }

    @Override
    public Optional<Account> findByIdForUpdate(Connection con, int accountId) throws SQLException {
        String sql = "Select * from account where id = ? for update"; // for update замораживает другие действия перед апдейтом

        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return Optional.of(mapRow(rs));
                else return Optional.empty();
            }
        }
    }

    private Account mapRow(ResultSet rs) throws SQLException{
        return new Account(rs.getInt("id"), rs.getString("name"), rs.getDouble("balance"));
        // так скажем упрощает создание нового аккаунта
        // тоже самое что мы записываем все данные в какие то переменные и потом передаем их к Optional.of(Account) как в примере с методом
        // findbyID где мы находили по айдишке и в случае не нахождение мы вызывали optional.empty();
    }
}
