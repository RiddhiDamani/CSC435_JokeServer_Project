import java.io.*;
import java.net.*;

// Server Code for InetServer Multithreading Assignment
public class JokeServer {

    //Toggle between Joke and Proverb Mode
    static boolean ToggleModeJokeProverb = true;
    public static void main(String args[]) throws IOException {

        // Queue length - for number of request to be queued by OS
        int queue_length = 6;

        // server socket port listening at port #
        int port = 7900;

        // Create object for Socket
        Socket socket;
        //ModeServer modeServerInstance;
        //Thread  modeThread= new Thread(new ModeServer());
        System.out.println("Riddhi Damani's Joke Server launching. Listening to port " + port + "... \n");

        AdminModeServer modeServerInstance = new AdminModeServer();
        Thread  modeThread = new Thread(modeServerInstance);
        modeThread.start();

        // Passing port number and queue length in the constructor of ServerSocket
        ServerSocket serverSocket = new ServerSocket(port, queue_length);

        // Printing out the Server startup message


        while (true) {
            // connecting to the next client request
            socket = serverSocket.accept();

            // make worker handle the client request
            new Worker(socket).start();
        }
    }
}

class AdminModeServer implements Runnable {
    public static boolean adminToggleSwitch = true;
    public void run() {

        System.out.println("In the Admin Looper Thread");
        int queue_length = 6;
        int port = 8001;
        Socket socket = null;

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, queue_length);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        System.out.println("Riddhi Damani's Mode Server launching. Listening to port " + port + "... \n");

        while (adminToggleSwitch) {
            try {
                socket = serverSocket.accept();
                new JokeClientAdmin.Admin_Worker(socket).start();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

// Worker Code for Joke Server
class Worker extends Thread {
    Socket socket;
    Worker (Socket s) {
        socket = s;
    }

    public void run() {
        PrintStream output = null;
        BufferedReader input = null;

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintStream(socket.getOutputStream());

            try {
                String name;
                name = input.readLine();
                System.out.println("Looking up " + name);
                //printRemoteAddress(name, output);
            }
            catch (IOException ioException) {
                System.out.println("Server read error");
                ioException.printStackTrace();
            }
            socket.close();
        }
        catch (IOException ioException) {
            System.out.println(ioException);
        }
    }
}
