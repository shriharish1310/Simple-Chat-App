import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class socserver implements Runnable {
    private ArrayList<ConnectionHandler> connectionlist;
    private ServerSocket server;
    private boolean done;
    private ExecutorService threadpool;
    public socserver(){
        connectionlist = new ArrayList<>();
        done = false;
    }
    @Override
    public void run(){
        try{
            server = new ServerSocket(40000);
            threadpool = Executors.newCachedThreadPool();
            while(!done){
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connectionlist.add(handler);
                threadpool.execute(handler);
            }
        }catch(IOException e){
            e.printStackTrace();
            shutdown();
        }
    }
    public void broadcast(String message){
        for(ConnectionHandler ch : connectionlist){
            if(ch!=null){
                ch.sendMessage(message);
            }
        }
    }
    public void shutdown(){
        try{
            done = true;
        if(!server.isClosed()){
            server.close();
        }
        for(ConnectionHandler ch : connectionlist){
            ch.shutdown();
        }}catch(IOException e){
            //do nothing
        }
    }
    class ConnectionHandler implements Runnable{ //This class handles client connections
        private Socket client;
        private BufferedReader in; //get the stream from the socket when client sends somehting
        private PrintWriter out; // write something to the client
        private String nickname;
        public ConnectionHandler(Socket client){
            this.client = client;
        }
        @Override
        public void run(){
            try {
                out = new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Enter a nickname: ");
                nickname = in.readLine();
                System.out.println(nickname + "Connected!");
                broadcast(nickname + "joined the chat!");
                String message;
                while((message = in.readLine())!=null){
                    if(message.startsWith("/nick")){
                        String[] messagesplit = message.split(" ");
                        if(messagesplit.length==2){
                            broadcast(nickname + "renamed themselves to" + messagesplit[1]);
                            nickname = messagesplit[1];
                            out.println("You have changed the name successfully to:"+messagesplit[1]);
                        }else{
                            out.println("No nuckname provided");
                        }
                    }else if(message.startsWith("/quit")){
                        broadcast(nickname + "left the chat");
                        shutdown();
                    }else{
                        broadcast(nickname+":"+message);
                    }
                }
            } catch (Exception e) {
                shutdown();
            }
        }
        public void shutdown(){
            try{
                in.close();
                out.close();
                if(!client.isClosed()){
                client.close();
                }
            }catch(Exception e){
                e.printStackTrace();
                shutdown();
            }
        }
        public void sendMessage(String message){
            out.println(message);
        }
    }
    public static void main(String[] args) {
        socserver server = new socserver();
        server.run();
    }
}