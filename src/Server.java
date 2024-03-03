import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {
    private ServerSocket serverSocket;
    private PrintWriter writer;

    public Server(ServerSocket serverSocket){
        try{
            this.serverSocket = serverSocket;
            writer = new PrintWriter(new FileWriter("C:/Users/Arailym/Documents/Basics of Distributed Systems/chat_message_group_chat.txt"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected! ");

                ClientHandler clientHandler = new ClientHandler(socket, this);
                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        }
        catch(IOException e){
            closeServer();
        }
    }

    public void storeMessage(String message){
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTimestamp = timestamp.format(formatter);
        writer.println(formattedDateTimestamp + " - " + message);
        writer.flush();
    }
    public void closeServer(){
        try{
            if (writer != null) {
                writer.close();
            }
            if(serverSocket != null){
                serverSocket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket); // creating socket to connect with client socket
        server.startServer();
    }
}

