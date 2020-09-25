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
import java.net.*;
// Importing Java utility libraries for using Collections/LinkedLists
import java.util.*;

// Server-Side Code for JokeServer Multithreading Environment
public class JokeServer {

    // Declaring and initializing ToggleModeJokeProverb variable.
    // This variable will help to switch between joke and proverb mode.
    static boolean ToggleModeJokeProverb = true;

    // JokeServer main method
    public static void main(String[] args) throws IOException {

        // Queue length - for number of request to be queued by OS
        int queue_length = 6;
        // JokeServer socket port listening at port #4545 (primary port)
        int port = 4545;
        // Variable declaration for local socket
        Socket socket;

        // Launching message on the JokeServer terminal
        System.out.println("Riddhi Damani's Joke Server launching. Listening to port " + port + "...");

        // Creating AdminModeServer's class - modeServerInstance instance
        AdminModeServer modeServerInstance = new AdminModeServer();
        // Passing the modeServerInstance to each new thread created
        Thread  modeThread = new Thread(modeServerInstance);
        // Invoke the run() method in the AdminModeServer class - This will start listening to JokeClientAdmin port i.e.
        // Port number : 5050
        modeThread.start();

        // Passing port number and queue length in the constructor of ServerSocket
        ServerSocket serverSocket = new ServerSocket(port, queue_length);

        // Listens to all the incoming client request uninterruptedly and allocates each worker thread for its processing.
        while (true) {
            // connecting to the next client request, put the connection in socket variable
            socket = serverSocket.accept();
            // spawning worker to handle the client connection by passing the connection we received in the socket variable
            // and invoke the thread i.e. run method in the Worker class
            new Worker(socket).start();
        }
    }
}

// AdminModeServer Class which executes the run() method (processes the thread) that was invoked from the JokeServer class
class AdminModeServer implements Runnable {

    // If adminToggleSwitch is true, creates a new adminWorker thread
    // and constantly listens to the incoming request.
    public static boolean adminToggleSwitch = true;
    // AdminModeServer run method.
    public void run() {

        // Prints acknowledgement message indicating admin looper thread initiated
        System.out.println("In the Admin Looper Thread");
        // Queue length - for number of request to be queued by OS
        int queue_length = 6;
        // JokeServer listens to ClientAdmin port 5050 -> toggling between Jokes and Proverbs asynchronously
        int port = 5050;
        // Declaring local socket variable
        Socket socket;
        ServerSocket serverSocket = null;

        try {
            // Creating local AdminServerMode object that will take in port 5050 and 6 connection request
            serverSocket = new ServerSocket(port, queue_length);
        }
        // Handling input-out exceptions!
        catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // Printing message on the JokeServer terminal stating it has started listening to ClientAdmin port
        System.out.println("Riddhi Damani's Mode Server launching. Listening to port " + port + "... \n");

        // Based on the adminToggleSwitch - create the admin worker thread for servicing client request
        while (adminToggleSwitch) {
            try {
                // connecting to the next client request, put the connection in socket variable
                socket = serverSocket.accept();
                // spawning Admin_Worker to handle the client connection by passing the connection we received in the
                // socket variable and invoke the thread i.e. run method in the JokeClientAdmin.Admin_Worker class
                new JokeClientAdmin.Admin_Worker(socket).start();
            }
            // if exception occurs, prints the ioException stack trace on the terminal.
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

// Worker Code for Joke Server
class Worker extends Thread {

    // Creating jokeList LL object
    LinkedList<String> jokeList = new LinkedList<>();
    // Creating proverbList LL object
    LinkedList<String> proverbList = new LinkedList<>();
    // Variable joke2DList declaration
    String [][] joke2DList;
    // Variable proverb2DList declaration
    String [][] proverb2DList;
    // Variable randomJokeString declaration
    String randomJokeString;
    // Variable randomProverbString declaration
    String randomProverbString;
    // Declaration of printStream output variable
    PrintStream outputData = null;
    // Declaring a new socket variable for local socket processing
    Socket socket;
    // Worker constructor that takes in socket data and assigns it to local socket variable
    Worker (Socket s) {
        socket = s;
    }

    // Run() method
    public void run() {

        // jokeIndicator variable declaration
        int jokeIndicator;
        // proverbIndicator variable declaration
        int proverbIndicator;
        // clientData variable declaration
        String clientData;
        // jokeIndicatorMessage variable declaration
        String jokeIndicatorMessage;
        // proverbIndicatorMessage variable declaration
        String proverbIndicatorMessage;
        // Declaring buffered reader inputData variable
        BufferedReader inputData;

        try {
            // creating inputData object to read from the socket
            inputData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // creating outputData object to write output to the socket
            outputData = new PrintStream(socket.getOutputStream());
            try {

                // Declaring userName variable
                String usrName;
                // Declaring uniqueClientID variable
                String uniqueClientID;

                // Reading UUID from the jokeClient
                uniqueClientID = inputData.readLine();
                // Reading user name, joke indicator and proverb indicator from the joke client
                clientData = inputData.readLine();

                // Breaking the above input provided into clientDataDisplay LL
                String[] clientDataDisplay = clientData.split(":");

                // setting user name to the first position
                usrName = clientDataDisplay[0];
                // setting joke indicator string to second position
                randomJokeString = clientDataDisplay[1];
                // setting proverb indicator string to third position
                randomProverbString =  clientDataDisplay[2];
                // Reading string form of the joke indicator  from the joke client
                jokeIndicatorMessage = inputData.readLine();
                // Parsing jokeIndicatorMessage to int value i.e. Joke Indicator for receiveJokeAndProverb method
                jokeIndicator = Integer.parseInt(jokeIndicatorMessage);
                // Reading string form of the proverb indicator  from the joke client
                proverbIndicatorMessage = inputData.readLine();
                // Parsing proverbIndicatorMessage to int value i.e. Proverb Indicator for receiveJokeAndProverb method
                proverbIndicator = Integer.parseInt(proverbIndicatorMessage);

                // Invoking receiveJokeAndProverb method
                // Sends appropriate state along with joke/proverb to joke client
                receiveJokeAndProverb(uniqueClientID, usrName, jokeIndicator,
                    proverbIndicator, outputData);

            }
            // if exception occurs, prints the ioException stack trace on the terminal.
            catch (IOException ioException) {
                System.out.println("Error in Server Read Operation!");
                ioException.printStackTrace();
            }
            // Closing socket connection
            socket.close();
        }
        // if exception occurs, prints the ioException stack trace on the terminal.
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // Method that returns appropriate joke/proverb to the joke client
    // Takes in input - uuid, user name, joke indicator, proverb indicator and output data
    // The state of clients are not yet stored on the server. Working on it
    private void receiveJokeAndProverb(String uniqueClientID, String usrName, int jokeIndicator,
                                       int proverbIndicator, PrintStream outputData) {

        //  Creates new UUIDList
        LinkedList<String> UUIDList = new LinkedList<>();
        // Invokes buildJokeList method
        buildJokeList(usrName);
        // Invokes buildProverb method
        buildProverbList(usrName);

        // Initiates Joke Mode
        if(JokeServer.ToggleModeJokeProverb) {
            try {
                // Checking whether client id is present in the server's UUID list
                // if no, adds to the UUIDList
                if(UUIDList.contains(uniqueClientID))
                    System.out.println("User Already Exists on the Server");
                else
                    UUIDList.add(uniqueClientID);

                //System.out.println(UUIDList);
                // If joke indicator = 4 i.e. last joke in the list
                if (jokeIndicator == 4) {
                    System.out.println(jokeList);
                    // shuffles the joke list
                    Collections.shuffle(jokeList);
                }
                // else fetch the appropriate joke based on the position
                else {
                    // Based on joke indicator value joke2Dlist is invoked and corresponding joke is returned.
                    switch (jokeIndicator) {
                        case 0 :
                            setJoke2DList(0);
                            break;
                        case 1 :
                            setJoke2DList(1);
                            break;
                        case 2 :
                            setJoke2DList(2);
                            break;
                        case 3 :
                            setJoke2DList(3);
                            break;
                        default :
                            System.out.println("No Joke Indicator set!");
                    }
                    // Increment the jokeIndicator counter for subsequent processing
                    jokeIndicator++;
                }
            }
            // if index out of bound exception occurs, prints the stack trace on the terminal.
            catch (IndexOutOfBoundsException ex) {
                outputData.println("Unable to get details for: " + uniqueClientID);
            }
        }
        // Initiates Proverb Mode
        else {
            try {
                // Checking whether client id is present in the server's UUID list
                // if no, adds to the UUIDList
                if(UUIDList.contains(uniqueClientID))
                    System.out.println("User Already Exists on the Server");
                else
                    UUIDList.add(uniqueClientID);
                //System.out.println(UUIDlist)

                // If proverb indicator = 4 i.e. last proverb in the list
                if (proverbIndicator == 4) {
                    System.out.println(proverbList);
                    // shuffles the proverb list
                    Collections.shuffle(proverbList);
                }
                // else fetch the appropriate proverb based on the position
                else {
                    // Based on proverb indicator value proverb2Dlist is invoked and corresponding proverb is returned.
                    switch (proverbIndicator) {
                        case 0:
                            setProverb2DList(0);
                            break;
                        case 1:
                            setProverb2DList(1);
                            break;
                        case 2:
                            setProverb2DList(2);
                            break;
                        case 3:
                            setProverb2DList(3);
                            break;
                        default:
                            System.out.println("No Proverb Indicator set!");
                    }
                    // Increment the proverbIndicator counter for subsequent processing
                    proverbIndicator++;
                }
            }
            // if index out of bound exception occurs, prints the stack trace on the terminal.
            catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                outputData.println("Unable to locate: " + uniqueClientID);
            }
        }
        // Returns joke indicator to joke client to record it's latest state
        outputData.println(jokeIndicator);
        // Returns proverb indicator to joke client to record it's latest state
        outputData.println(proverbIndicator);
    }

    // Method that Checks for the jokeIndicator and matches the corresponding letter with the jokes2DList and
    // send back relevant joke to the client terminal
    private void setJoke2DList(int i) {
        switch (jokeList.get(i)) {
            case "W":
                outputData.println(joke2DList[0][1]);
                System.out.println((joke2DList[0][1]));
                break;
            case "X":
                outputData.println(joke2DList[1][1]);
                System.out.println((joke2DList[1][1]));
                break;
            case "Y":
                outputData.println(joke2DList[2][1]);
                System.out.println((joke2DList[2][1]));
                break;
            case "Z":
                outputData.println(joke2DList[3][1]);
                System.out.println((joke2DList[3][1]));
                break;
        }
    }

    // Method that Checks for the proverbIndicator and matches the corresponding letter with the proverb2DList and
    // send back relevant proverb to the client terminal
    private void setProverb2DList(int i) {
        switch (proverbList.get(i)) {
            case "W":
                outputData.println(proverb2DList[0][1]);
                System.out.println((proverb2DList[0][1]));
                break;
            case "X":
                outputData.println(proverb2DList[1][1]);
                System.out.println((proverb2DList[1][1]));
                break;
            case "Y":
                outputData.println(proverb2DList[2][1]);
                System.out.println((proverb2DList[2][1]));
                break;
            case "Z":
                outputData.println(proverb2DList[3][1]);
                System.out.println((proverb2DList[3][1]));
                break;
        }
    }

    // Method to build Joke List - Parses random jokes sent by JokeClient and adds to jokeList LL
    private void buildJokeList(String usrName) {
        for(int i=0; i<=3; i++) {
            jokeList.add(String.valueOf(randomJokeString.charAt(i)));
        }

        joke2DList = new String[][] {
                {"W", "JA " + usrName + ": I accidentally handed my wife a glue stick instead of a chapstick. She still isn’t talking to me."},
                {"X", "JB " + usrName + ": Why do bees hum? They don’t remember the lyrics!"},
                {"Y", "JC " + usrName + ": I'm reading a book about anti-gravity. It's impossible to put down."},
                {"Z", "JD " + usrName + ": Why can’t a bike stand on its own? It’s two tired."}
        };
    }

    // Method to build Proverb List - Parses random proverb sent by JokeClient and adds to proverbList LL
    private void buildProverbList(String usrName) {
        for(int i=0; i<=3; i++) {
            proverbList.add(String.valueOf(randomProverbString.charAt(i)));
        }

        proverb2DList = new String[][] {
                {"W", "PA " + usrName + ": Absence makes the heart grow fonder."},
                {"X", "PB " + usrName + ": Always put your best foot forward. "},
                {"Y", "PC " + usrName + ": A ship in the harbor is safe, but that is not what a ship is for."},
                {"Z", "PD " + usrName + ": Cleanliness is next to Godliness."}};
    }
}
