/*--------------------------------------------------------

1. Name / Date: Riddhi Damani / 09-27-2020

2. Java version used, if not the official version for the class:
java version "9.0.4"
Java(TM) SE Runtime Environment (build 9.0.4+11)
Java HotSpot(TM) 64-Bit Server VM (build 9.0.4+11, mixed mode)

3. Precise command-line compilation examples / instructions:
In separate terminal windows:
Compilation Steps:
> javac JokeServer.java
> javac JokeClientAdmin.java
> javac JokeClient.java

4. Precise examples / instructions to run this program:
In separate terminal windows:
Execution Steps:
> java JokeServer
> java JokeClientAdmin
> java JokeClient

5. List of files needed for running the program.
 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

6. Notes:
 a. I have used randomUUID() Java method to create unique client ID.
----------------------------------------------------------*/

// Importing Java input-output libraries
import java.io.*;
// Importing Java socket/networking libraries
import java.net.Socket;

//JokeClientAdmin Class
public class JokeClientAdmin {
    // main class
    public static void main(String[] args) {

        // Variable ServerName Declaration
        String server_name;
        // Variable Mode1 Declaration, initializing to default value
        String mode1 = "";
        // Variable Mode2 Declaration
        String mode2;
        // Variable PrintMessage Declaration
        String printMessage;
        // Declaring input stream from the socket
        BufferedReader input;

        // Sets "localhost" as primary/default server, if client does not explicitly enters server address through command line
        // else takes in the server address provided by the client through terminal
        server_name = (args.length < 1) ? "localhost" : args[0];

        // Prints out connection to the server message on the JokeClientAdmin terminal
        System.out.println("JokeClientAdmin Connected to : " + server_name + ", Port: 5050");
        System.out.println("Interaction in progress..");

        try {
            // Prints message on clientAdmin terminal - Requesting to press enter for toggling between Joke and Proverb Mode
            System.out.println("Please press enter key to toggle between Joke Mode and Proverb Mode");
            // Initializing new input stream object to receive command from the clientAdmin's terminal
            input = new BufferedReader(new InputStreamReader(System.in));

            // Execute this while loop until clientAdmin enters "quit" on the terminal
            while (!mode1.toLowerCase().contains("quit")) {
                // Reading the input entered by the clientAdmin
                mode1 = input.readLine();
                // Performs toggle operation between Joke mode and Proverb Mode if clientAdmin presses 'Enter' key
                if(!mode1.toLowerCase().contains("quit")) {

                    // Calls our inbuilt ToggleSwitch method. This method returns the toggled mode indicator - toggledModeName!
                    mode2 = toggleSwitch(server_name);
                    // If the toggle mode indicator is Joke Mode, it gets switched to Proverb Mode and acknowledges clientAdmin
                    // by printing - "Toggled to Proverb Mode" and vice versa.
                    printMessage = mode2.contains("JOKE_MODE") ? "Toggled to Proverb Mode" : "Toggled to Joke Mode";
                    // prints the message on clientAdmin terminal
                    System.out.println(printMessage);
                }
                else {
                    // Executes when the clientAdmin enters "quit" on its terminal
                    System.out.println ("Connection disconnected by the client..");
                }
            }
        }
        // Catches any input-output exception
        catch (IOException ioExcpt) {
            // if exception occurs, prints the io stack trace on the clientAdmin terminal.
            ioExcpt.printStackTrace();
        }
    }

    // AdminWorker Class that extends the java thread class
    public static class Admin_Worker extends Thread {
        // Variable socket declaration, local to Admin_Worker class
        Socket socket;
        // Defining Admin_Worker constructor, assigning argument s to local socket variable
        public Admin_Worker(Socket s)
        {
            socket = s;
        }
        // Run() method declaration - executes when .start() method is called from the class - JokeServer
        public void run() {

            // Declaring output stream
            PrintStream output;

            try {
                // Creating new output stream object and binding it with local socket variable
                output = new PrintStream(socket.getOutputStream());

                try {
                    // Declaring a trueOrFalse variable indicating whether mode has been toggled or not
                    // by the adminClient.
                    int trueOrFalse;
                    // Checks for the ToggleModeJokeProverb variable in the JokeServer class.
                    // Sets trueOrFalse to 1 -> if ToggleModeJokeProverb = true
                    // Sets trueOrFalse to 0 -> if ToggleModeJokeProverb = false (default mode)
                    trueOrFalse = JokeServer.ToggleModeJokeProverb ? 1 : 0;
                    // Checks for the mode state
                    switch (trueOrFalse) {

                        // This case executes when the ToggleModeJokeProverb = true
                        // Displays the relevant message on the JokeServer terminal about mode change i.e. Proverb Mode
                        // Then, modifies joke sever mode variable ToggleModeJokeProverb = false
                        // 'JOKE_MODE' string is sent to JokeClientAdmin to modify its state and thus, print relevant message
                        // on its' terminal
                        case 1:
                            System.out.println("Toggled Mode State: Proverb Mode");
                            JokeServer.ToggleModeJokeProverb = false;
                            output.println("JOKE_MODE");
                            break;
                        // This case executes when the ToggleModeJokeProverb = false
                        // Displays the relevant message on the JokeServer terminal about mode change i.e. Joke Mode
                        // Then, modifies joke sever mode variable ToggleModeJokeProverb = true
                        // 'PROVERB_MODE' string is sent to JokeClientAdmin to modify its state and thus, print relevant message
                        // on its' terminal
                        case 0:
                            System.out.println("Toggled Mode State: Joke Mode");
                            JokeServer.ToggleModeJokeProverb = true;
                            output.println("PROVERB_MODE");
                            break;
                        // Setting default message
                        default:
                            System.out.println("No Toggling Performed!");
                    }
                }
                // if exception occurs, prints the IndexOutOfBoundsException stack trace to JokeServer terminal
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    System.out.println("Error in read operation from Joke Server");
                    indexOutOfBoundsException.printStackTrace();
                }

                // Closing the socket connection
                socket.close();
            }
            // if exception occurs, prints the io stack trace to JokeServer terminal.
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    // In-built toggleSwitch method - helps clientAdmin to perform toggle action from Joke <=> Proverb
    private static String toggleSwitch(String server_name) {
        // Variable socket declaration
        Socket socket;
        // Declaring input-output stream to read and write from/to socket
        BufferedReader inputClientAdmin;
        PrintStream outputClientAdmin;
        // Declaring toggle mode indicator variable
        String toggledModeName = " ";

        try {
            // Creating new socket object and binding it to Port 5050 - Primary Server Port
            socket = new Socket(server_name, 5050);
            // Creating input object to read data from the socket connection
            inputClientAdmin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Creating output object to write data to the terminal/ socket connection
            outputClientAdmin = new PrintStream(socket.getOutputStream());
            // Reads the changed mode that was processed by admin worker
            toggledModeName = inputClientAdmin.readLine();
            // Sends toggledModeName (indicating change mode) to switch the mode
            outputClientAdmin.println(toggledModeName);

            // Closing the socket connection
            socket.close();
        }
        // if exception occurs, prints the io stack trace on the terminal.
        catch (IOException ioXcpt) {
            System.out.println("Error Connecting the Socket!");
            ioXcpt.printStackTrace();
        }
        // Returns the toggledModeName to calling method.
        return toggledModeName;
    }
}
