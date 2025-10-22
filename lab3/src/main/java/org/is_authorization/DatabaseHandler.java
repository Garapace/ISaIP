package org.is_authorization;

import org.is_authorization.data.Configs;
import org.is_authorization.data.Const;

import java.sql.*;

public class DatabaseHandler extends Configs {
    Connection connection;

    public Connection getConnection()
        throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(
                connectionString,
                dbUser, dbPass);

        return connection;
    }

    public void signUpUser(String login, String password)
            throws ClassNotFoundException, SQLException {
        String insert = "INSERT INTO " + "\"" + Const.USER_SCHEME + "\"" + "." + Const.USER_TABLE + "("
                + Const.USER_LOGIN + ","
                + Const.USER_PASSWORD + ")"
                + "VALUES(?,?)";

        PreparedStatement prSt = getConnection().prepareStatement(insert);
        prSt.setString(1, login);
        prSt.setString(2, password);

        prSt.executeUpdate();
    }

    public ResultSet getUser(String login, String password)
            throws ClassNotFoundException, SQLException{
        ResultSet resultSet = null;

        String select = "SELECT * FROM" + "\"" + Const.USER_SCHEME + "\"" + "." + Const.USER_TABLE
                + " WHERE " + Const.USER_LOGIN + "=? AND " + Const.USER_PASSWORD + "=?";

        PreparedStatement prSt = getConnection().prepareStatement(select);
        prSt.setString(1, login);
        prSt.setString(2, password);

        resultSet = prSt.executeQuery();

        return resultSet;
    }

    public ResultSet getUserAdmin(String login)
            throws ClassNotFoundException, SQLException{
        ResultSet resultSet = null;

        String select = "SELECT * FROM" + "\"" + Const.USER_SCHEME + "\"" + "." + Const.USER_TABLE
                + " WHERE " + Const.USER_LOGIN + "=?";

        PreparedStatement prSt = getConnection().prepareStatement(select);
        prSt.setString(1, login);

        resultSet = prSt.executeQuery();

        return resultSet;
    }

    public ResultSet getAllUser()
            throws ClassNotFoundException, SQLException{
        ResultSet resultSet = null;

        String select = "SELECT * FROM" + "\"" + Const.USER_SCHEME + "\"" + "." + Const.USER_TABLE
                + " ORDER BY " + Const.USERS_ID + " ASC";

        PreparedStatement prSt = getConnection().prepareStatement(select);
        resultSet = prSt.executeQuery();

        return resultSet;
    }

    public void updatePassword(String login, String password)
            throws ClassNotFoundException, SQLException{
        String update = "UPDATE "+ "\"" + Const.USER_SCHEME + "\"" + "." + Const.USER_TABLE +
                " SET " + Const.USER_PASSWORD + " =?" +
                " WHERE " + Const.USER_LOGIN + " =?";

        PreparedStatement prSt = getConnection().prepareStatement(update);
        prSt.setString(1, password);
        prSt.setString(2, login);

        prSt.executeUpdate();
    }

    public void banUser(String login)
            throws ClassNotFoundException, SQLException{
        String ban = "UPDATE "+ "\"" + Const.USER_SCHEME + "\"" + "." + Const.USER_TABLE +
                " SET " + Const.USER_FLAG + " =?" +
                " WHERE " + Const.USER_LOGIN + " =?";

        PreparedStatement prSt = getConnection().prepareStatement(ban);
        prSt.setBoolean(1, true);
        prSt.setString(2, login);

        prSt.executeUpdate();
    }

    public void updatePasswordSetting(String login, int password_length, boolean upperCase, boolean lowerCase, boolean special)
            throws ClassNotFoundException, SQLException{
        String select = "SELECT " + Const.PASSWORD_ID + " FROM " + "\"" + Const.USER_SCHEME + "\"" + "." +  Const.PASSWORD_TABLE
                + " WHERE " + Const.PASSWORD_LENGTH + "=? AND "
                + Const.PASSWORD_UPPERCASE + "=? AND "
                + Const.PASSWORD_LOWERCASE + "=? AND "
                + Const.PASSWORD_SPECIAL + "=?";

        PreparedStatement prSt = getConnection().prepareStatement(select);
        prSt.setInt(1, password_length);
        prSt.setBoolean(2, upperCase);
        prSt.setBoolean(3, lowerCase);
        prSt.setBoolean(4, special);

        ResultSet resultSet = prSt.executeQuery();

        int passwordId;
        if (resultSet.next()) {
            // Если запись существует, используем её ID
            passwordId = resultSet.getInt(Const.PASSWORD_ID);
        } else {
            // Если записи нет, вставляем новую
            String insert = "INSERT INTO " + "\"" + Const.USER_SCHEME + "\"" + "." + Const.PASSWORD_TABLE + "("
                    + Const.PASSWORD_LENGTH + ","
                    + Const.PASSWORD_UPPERCASE + ","
                    + Const.PASSWORD_LOWERCASE + ","
                    + Const.PASSWORD_SPECIAL + ")"
                    + " VALUES(?,?,?,?)";

            prSt = getConnection().prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            prSt.setInt(1, password_length);
            prSt.setBoolean(2, upperCase);
            prSt.setBoolean(3, lowerCase);
            prSt.setBoolean(4, special);

            prSt.executeUpdate();

            // Получаем ID новой записи
            ResultSet generatedKeys = prSt.getGeneratedKeys();
            if (generatedKeys.next()) {
                passwordId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Не удалось получить ID новой записи.");
            }
        }

        // Обновляем запись в таблице пользователей
        String update = "UPDATE " + "\"" + Const.USER_SCHEME + "\"" + "." + Const.USER_TABLE
                + " SET " + Const.PASSWORD_ID + "=?"
                + " WHERE " + Const.USER_LOGIN + "=?";

        prSt = getConnection().prepareStatement(update);
        prSt.setInt(1, passwordId);
        prSt.setString(2, login);
        prSt.executeUpdate();
    }

    public ResultSet getPasswordSetting(String login)
            throws SQLException, ClassNotFoundException {
        ResultSet resultSet = null;

        ResultSet user = getUserAdmin(login);
        user.next();

        int passwordId = user.getInt(Const.PASSWORD_ID);

        String select = "SELECT * FROM" + "\"" + Const.USER_SCHEME + "\"" + "." + Const.PASSWORD_TABLE
                + " WHERE " + Const.PASSWORD_ID + "=?";

        PreparedStatement prSt = getConnection().prepareStatement(select);
        prSt.setInt(1, passwordId);

        resultSet = prSt.executeQuery();

        return resultSet;
    }
}
