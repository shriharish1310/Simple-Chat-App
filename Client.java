import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

public class Client implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run(){
        try {
            Socket client = new Socket("localhost", 40000);
            out = new PrintWriter(client.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            InputHandler inhandler  = new InputHandler();
            Thread t = new Thread(inhandler);
            t.start();
            String inmessage;
            while((inmessage=in.readLine())!=null){
                System.out.println(inmessage);
            }
        } catch (Exception e) {
            shutdown();
        }
    }
    public void shutdown(){
        done = true;
        try {
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    class InputHandler implements Runnable{
        @Override
        public void run(){
                try {
                    BufferedReader inreader = new BufferedReader(new InputStreamReader(System.in));
                    while(!done){
                        String message = inreader.readLine();
                        if(message.equals("/quit")){
                            inreader.close();
                            shutdown();
                        }else{
                            out.println(message);
                        }
                    }
                } catch (Exception e) {
                    shutdown();
                }
        }
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
