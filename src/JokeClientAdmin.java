import java.io.*;
import java.net.Socket;

public class JokeClientAdmin {
    public static void main(String args[]) throws IOException {

        String server_name;
        String mode1 = "";
        String mode2 = "";
        String printMessage;

        server_name = (args.length < 1) ? "localhost" : args[0];

        System.out.println("Connected to : " + server_name + ", Port: 8001");
        System.out.println("Interaction in progress..");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please press enter key to toggle between Joke Mode and Proverb Mode");

        while (mode1.toLowerCase().contains("quit") == false) {

            mode1 = input.readLine();
            if(mode1.toLowerCase().contains("quit") == false) {
                mode2 = toggleSwitch(server_name);
                printMessage = mode2.contains("JOKE_MODE") ? "Toggled to Proverb Mode" : "Toggled to Joke Mode";
                System.out.println(printMessage);
            }
            else {
                System.out.println ("Connection disconnected by the client..");
            }
        } ;
    }

    private static String toggleSwitch(String server_name) throws IOException {
        Socket socket;
        PrintStream outputClientAdmin;
        BufferedReader inputClientAdmin;
        String toggledModeName = "";

        socket = new Socket(server_name, 8001);

        outputClientAdmin = new PrintStream(socket.getOutputStream());
        inputClientAdmin = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        outputClientAdmin.println(toggledModeName);
        toggledModeName = inputClientAdmin.readLine();

        socket.close();


        return toggledModeName;
    }

    public static class Admin_Worker extends Thread {

        Socket socket;

        public Admin_Worker(Socket s) { socket = s;}

        public void run() {
            PrintStream output = null;
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintStream(socket.getOutputStream());

                try {
                    if(JokeServer.ToggleModeJokeProverb == true) {
                        System.out.println("Toggled Mode State: Proverb Mode");
                        JokeServer.ToggleModeJokeProverb = false;
                        output.println("JOKE_MODE");
                    }
                    else{
                        System.out.println("Toggled Mode State: Joke Mode");
                        JokeServer.ToggleModeJokeProverb = true;
                        output.println("PROVERB_MODE");
                    }

                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    System.out.println("Error reading data from Server");
                    indexOutOfBoundsException.printStackTrace();
                }
                socket.close();
            } catch (IOException ioException) {
                System.out.println(ioException);
            }
        }
    }
}
