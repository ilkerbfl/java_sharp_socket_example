import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by İlker ÇATAK on 18.10.2018.
 */
public class ThreadedSocket {


    public static void main(String[] args) {
        new ThreadedSocket().startServer();
    }

    public void startServer() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(1);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(2626);
                    System.out.println("Waiting for reader to connect...");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        clientProcessingPool.submit(new ClientTask(clientSocket));
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process reader request");
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    private class ClientTask implements Runnable {
        private final Socket clientSocket;
        private  InputStream is;

        private ClientTask(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            is = clientSocket.getInputStream();
        }

        @Override
        public void run() {
            byte[] lenBytes = new byte[4];
            try {
                is.read(lenBytes, 0, 4);
                int len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) | ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
                byte[] receivedBytes = new byte[len];
                is.read(receivedBytes, 0, len);
                String received = new String(receivedBytes, 0, len);
                System.out.println(received);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
