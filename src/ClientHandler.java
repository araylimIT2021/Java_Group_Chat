import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // every instance of this class
    private Socket socket; //new socket from the server, to establish connection with client and server
    private BufferedReader bufferedReader; //reading specific msg sent from the client
    private BufferedWriter bufferedWriter; //send msg to clients
    private Server server;

    private String clientUsername;

    public ClientHandler(Socket socket, Server server){
        try{
            this.socket = socket;
            this.server = server;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
        }
        catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        //run on seperate threads
        String messageFromClient;
        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient != null) {
                    server.storeMessage(messageFromClient); // Store the message using Server
                    broadcastMessage(messageFromClient);
                }
            }
            catch(IOException e){
                removeClientHandler();
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for(ClientHandler clientHandler: clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
       try {
           if (bufferedReader != null) {
               bufferedReader.close();
           }
           if(bufferedWriter != null){
               bufferedWriter.close();
           }
           if(socket != null){
               socket.close();
           }
       } catch(IOException e){
           e.printStackTrace();
       }
    }
}
