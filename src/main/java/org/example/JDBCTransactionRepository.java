package org.example;

import com.mysql.cj.protocol.Resultset;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class JDBCTransactionRepository implements TransactionRepository {
    private final DataSource dataSource; // -> это вернет нам соединение с пулом

    public JDBCTransactionRepository(DataSource dataSource){
        this.dataSource = dataSource;                        // это дает гибкость в будущем при тестах и в целом мы за один клик дадим всем соединение
    }





    @Override
    public void save(Connection con, Transactions transactions) throws SQLException {
        String sql = "INSERT INTO transactions(from_account_id, to_account_id, amount, type) VALUES(?,?,?,?)";

        try(PreparedStatement ps = con.prepareStatement(sql)){
            if(transactions.getFromAccountId() != null) ps.setInt(1, transactions.getFromAccountId());
            else ps.setNull(1, Types.INTEGER);
            if(transactions.getToAccountId() != null) ps.setInt(2,transactions.getToAccountId());
            else ps.setNull(2,Types.INTEGER);

            ps.setDouble(3, transactions.getAmount());
            ps.setString(4,transactions.getType().name());
            ps.executeUpdate();

        }

    }



    @Override
    public List<Transactions> findByAccountId(int accountId) {
        String sql = """
            SELECT * FROM transactions
            WHERE from_account_id = ? OR to_account_id = ?
            ORDER BY created_at DESC
            """;
        List<Transactions> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);
            ps.setInt(2, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer id = rs.getInt("id");
                    Integer fromId = (rs.getObject("from_account_id", Integer.class));
                    Integer toId = rs.getObject("to_account_id", Integer.class);
                    double amount = rs.getDouble("amount");
                    Transactions.Type type = Transactions.Type.valueOf(rs.getString("type"));
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    result.add(new Transactions(id,fromId,toId,amount,type));
                }
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить историю операций", e);
        }
    }
}
