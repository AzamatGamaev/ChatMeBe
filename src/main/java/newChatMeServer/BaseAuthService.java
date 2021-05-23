package newChatMeServer;

import java.util.HashMap;
import java.util.Map;

public class BaseAuthService implements AuthService {

   static Map<String, User> users;

    @Override
    public void start() {
        DBConnection dbConnection = new DBConnection();
        users = new HashMap<>();
        for (int i = 1; i <= DBConnection.getCount(); i++) {
            String[] arr = dbConnection.getAll(i);
            users.put(arr[0], new User(arr[0], arr[1], arr[2]));
        }
        System.out.println("В базе обнаружены пользователи со следующими никами:");
        System.out.println(getNickByLoginPass("login1","pass1"));
        System.out.println(getNickByLoginPass("login2","pass2"));
        System.out.println(getNickByLoginPass("login3","pass3"));
    }


    @Override
    public String getNickByLoginPass(String login, String password) {
        User user = users.get(login);
        if (user != null && user.getPassword().equals(password)) {
            return user.getNick();
        }
        return null;
    }


    //TODO при изменении Ника пользователем HASHMAP не изменяет свои значения, соответвенно ник пользователя
    //TODO не изменится пока все не перезайдут в чат. Для этого доработать следующий блок кода, или отказаться от HASHMAP вообще.
//    public static void updateDB() {
//        users = new HashMap<>();
//        for (int i = 1; i <= DBConnection.count; i++) {
//            String[] arr = DBConnection.getAll(i);
//            users.put(arr[0], new User(arr[0], arr[1], arr[2]));
//        }
//        System.out.println("NEW!!!!!!");
//    }


    @Override
    public void stop() {
        System.out.println("Сервис остановился.");
    }
}
