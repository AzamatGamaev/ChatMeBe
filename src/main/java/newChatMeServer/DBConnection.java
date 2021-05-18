package newChatMeServer;


import java.sql.*;

//lesson2 - класс где прописаны методы
public class DBConnection {
    private static String pass;
    private static String nick;
    private static String userID;
    private static int count = DBConnection.numberOfRows();

    public static String getPassFromDB(String login) {
        try (
                Connection postgresConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatMeDB", "postgres", "12345")) {
            PreparedStatement loginPrepareStatement = postgresConnection.prepareStatement("select * from users where login = ?");
            loginPrepareStatement.setString(1, login);
            ResultSet userResultSet = loginPrepareStatement.executeQuery();
            while (userResultSet.next()) {
                pass = userResultSet.getString("password");
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
            System.out.println("Проблема с getPassFromDB");
        }
        return pass;
    }

    public static String getNickFromDB(String login) {
        try (
                Connection postgresConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatMeDB", "postgres", "12345")) {
            PreparedStatement loginPrepareStatement = postgresConnection.prepareStatement("select * from users where login = ?");
            loginPrepareStatement.setString(1, login);
            ResultSet userResultSet = loginPrepareStatement.executeQuery();
            while (userResultSet.next()) {
                pass = userResultSet.getString("nick");
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
            System.out.println("Проблема с getNickFromDB");
        }
        return nick;
    }

    public static String getUserIDFromDB(String nick) {
        try (
                Connection postgresConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatMeDB", "postgres", "12345")) {
            PreparedStatement loginPrepareStatement = postgresConnection.prepareStatement("select * from users where nick=?");
            loginPrepareStatement.setString(1, nick);
            ResultSet userResultSet = loginPrepareStatement.executeQuery();
            while (userResultSet.next()) {
                userID = userResultSet.getString("user_id");
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
            System.out.println("Проблема с getUserIDFromDB");
        }
        return userID;
    }

    public static int updateNick(int id, String newNick) {
        int affectedDraws = 0;
        try (Connection postgresConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatMeDB", "postgres", "12345")) {
            PreparedStatement idPrepareStatement = postgresConnection.prepareStatement("update users SET nick = ? where user_id = ?");
            idPrepareStatement.setString(1, newNick);
            idPrepareStatement.setInt(2, id);
            affectedDraws = idPrepareStatement.executeUpdate();
//            BaseAuthService.updateDB();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Проблема с updateNick");
        }
        return affectedDraws;
    }

    public static int numberOfRows() {
        int count = 0;
        try (Connection postgresConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatMeDB", "postgres", "12345")) {
            PreparedStatement idPrepareStatement = postgresConnection.prepareStatement("select count(*) from users");
            ResultSet numberOfRows = idPrepareStatement.executeQuery();
            numberOfRows.next();
            count = numberOfRows.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Проблема с numberOfRows");
        }
        System.out.println("Количество юзеров в базе = " + count);
        return count;
    }

    public static String[] getAll(int count) {
        String[] arr = new String[3];
        try (
                Connection postgresConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatMeDB", "postgres", "12345")) {
            PreparedStatement loginPrepareStatement = postgresConnection.prepareStatement("select * from users WHERE user_id = ?;");
            //TODO изменить запрос по id на запрос по номеру строки в базе, т.к. id может поменяться, или его может не быть, или он пойет не по порядку.
            loginPrepareStatement.setInt(1, count);
            ResultSet userResultSet = loginPrepareStatement.executeQuery();
            while (userResultSet.next()) {
                login = userResultSet.getString("login");
                pass = userResultSet.getString("password");
                nick = userResultSet.getString("nick");
                arr[0] = login;
                arr[1] = pass;
                arr[2] = nick;
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
            System.out.println("Проблема с getAll");
        }
        return arr;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        DBConnection.count = count;
    }

    private static String login;

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        DBConnection.login = login;
    }

    public static String getPass() {
        return pass;
    }

    public static void setPass(String pass) {
        DBConnection.pass = pass;
    }

    public static String getNick() {
        return nick;
    }

    public static void setNick(String nick) {
        DBConnection.nick = nick;
    }

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        DBConnection.userID = userID;
    }
}

