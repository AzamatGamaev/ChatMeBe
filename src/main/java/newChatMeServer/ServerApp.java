package newChatMeServer;


public class ServerApp {
        static {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        new MyServer();
    }
}
