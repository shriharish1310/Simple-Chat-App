import java.net.*;
public class Server implements Runnable {
    @Override
    public void run(){
        try{
            ServerSocket server = new ServerSocket(40000);
            Socket client = server.accept();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    class ConnectionHandler implements Runnable{ //This class handles client connections
        private Socket client;
        BufferedReader in;
        PrintWriter out;
        public ConnectionHandler(Socket client){
            this.client = client;
        }
        @Override
        public void run(){
            try {
                out = new PrintWriter(client.getOutputStream)
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}