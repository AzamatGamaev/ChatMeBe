package newChatMeServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Long lastMsgTime;
    private MyServer server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name = "";


    public ClientHandler(Socket socket) {
        try {
            this.server = MyServer.getServer();
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    auth();
                    readMsg();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();

        } catch (IOException e) {
            System.out.println("Проблемы при создании обработчика клиента...");
            e.printStackTrace();
        }
    }

    private void auth() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split(" ");
                String login = parts[1];
                String password = parts[2];
                String nick = server.getAuthService().getNickByLoginPass(login, password);
                if (nick != null) {
                    if (!server.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        server.broadcastMsg(name + " зашел в чат");
                        server.subscribe(this);
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется!");
                    }
                } else {
                    if (!server.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = "Инкогнито";
                        server.broadcastMsg(name + " зашел в чат!");
                        server.subscribe(this);
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется!");
                    }
                    sendMsg("Неверные логин/пароль!");
                }
            } else {
                sendMsg("Перед тем как отправлять сообщения авторизуйтесь через команду </auth login1 pass1>");
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMsg() throws IOException {
        while (socket.isConnected()) {
            String strFromClient = in.readUTF();

            if (lastMsgTime != null && System.currentTimeMillis() - lastMsgTime < 5000) {
                server.sendSystemMsgToClient("ВНИМАНИЕ! Время между отправкой сообщений 5 секунд.");
                continue;
            }
            lastMsgTime = System.currentTimeMillis();

            System.out.println("От " + name + ": " + strFromClient);

            if (strFromClient.startsWith("/")) {
                if (strFromClient.equals("/end")) {
                    server.sendSystemMsgToClientAboutCommand(strFromClient, " Это команда для выхода из чата. Пока!" +
                            "\nНе забудьте закрыть окно чата.");
                    return;
                }
                if (strFromClient.startsWith("/howSetNick")) {
                    server.sendSystemMsgToClient("Чтобы изменить свой ник, " +
                            "Вам нужно узнать ваш уникальный id. Для этого введите /getMyID .");
                }
                if (strFromClient.startsWith("/getMyID")) {
                    server.sendSystemMsgToClient("Ваш id = " + DBConnection.getUserIDFromDB(name) + "." +
                            "Для смены ника введите /setMyNick вашID новыйНик");
                }
                if (strFromClient.startsWith("/setMyNick")) {
                    try {
                        String[] parts = strFromClient.split(" ");
                        int id = Integer.parseInt(parts[1]);
                        String newNick = parts[2];
                        DBConnection.updateNick(id, newNick);
                        server.sendSystemMsgToClient("Вы успешно изменили свой ник.");
                    } catch (Exception e) {
                        server.sendSystemMsgToClientAboutCommand(strFromClient, " Неправильная команда. " +
                                "Попробуйте ещё раз.\nДля смены ника введите /setMyNick вашID новыйНик");
                    }
                }
                if (strFromClient.startsWith("/w")) {
                    try {
                        String[] parts = strFromClient.split(" ");
                        String nickTo = parts[1];
                        String msg = strFromClient.substring(3 + nickTo.length() + 1);
                        server.sendMsgToClient(this, nickTo, msg);
                    } catch (Exception e) {
                        server.sendSystemMsgToClientAboutCommand(strFromClient, " Неправильная команда. " +
                                "Попробуйте ещё раз.");
                    }
                }
                continue;
            }
            server.broadcastMsg(name + ": " + strFromClient);
        }
    }

    public void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMsg(name + " вышел из чата.");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
