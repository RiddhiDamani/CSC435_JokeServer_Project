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
// Importing Java utility libraries for using Collections/LinkedLists
import java.util.*;

// Client-Side Code for JokeServer Multithreading Environment
public class JokeClient {
    // Main method
    public static void main(String[] args) {
        // Variable declaration for server name
        String server_Name;
        try {
            // Initializing server name
            server_Name = (args.length < 1) ? "localhost" : args[0];
            // Creating a new JokeClientWorker object to process the request
            JokeClientWorker jcw = new JokeClientWorker(server_Name);
            // Executing Run method
            jcw.run();
        }
        // if exception occurs, prints the exception stack trace to JokeClient terminal.
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
// JokeClientWorker Class
class JokeClientWorker {
    // Variable server name declaration
    String server_Name;
    // Variable user name declaration
    String user_Name;
    // Variable joke indicator declaration - keeps track of jokes displayed on terminal
    int jokeIndicator = 0;
    // Variable proverb indicator declaration - keeps track of proverb displayed on terminal
    int proverbIndicator = 0;
    // Variable uniqueClient ID declaration - UUID
    String uniqueClientID;
    // Variable randomJokeString declaration - Format: XWYZ
    StringBuilder randomJokeString;
    // Variable randomProverbString declaration - Format: XWYZ
    StringBuilder randomProverbString;
    // randomOrder list variable declaration
    LinkedList<String> randomOrder = new LinkedList<>();
    // indicatorPosition list variable declaration format: [joke, proverb]
    LinkedList<Integer> indicatorPosition = new LinkedList<>();
    // randomJokeList list variable declaration format: [W, X, Y, Z]
    LinkedList<String> randomJokeList = new LinkedList<>();
    // randomProverbList list variable declaration format: [W, X, Y, Z]
    LinkedList<String> randomProverbList = new LinkedList<>();

    // JokeClientWorker constructor - Takes in sever name and stores into local server name variable
    public JokeClientWorker(String server_Name) {
        // Assigning server name to local server_Name variable
        this.server_Name = server_Name;
    }

    // Run() method
    public void run() {
        // Displaying message on the JokeClient terminal
        System.out.println("Interaction with : " + server_Name + " is in progress, Port: 4545");
        // Creating buffered reader object to read the input from the joke client's terminal
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // Invoking joke shuffle functionality
        buildRandomJokeOrder();
        // Invoking proverb shuffle functionality
        buildRandomProverbOrder();

        try {

            // Message to display on Joke Client terminal requesting to enter their name
            // If, need to disconnect from the terminal - enter 'quit'
            System.out.println("Please enter your name or enter 'quit' to end communication: ");
            // Clearing out the output buffer
            System.out.flush();
            // reading the input provided by the client and assigning it to user_Name variable
            user_Name = input.readLine();
            // Greeting message on the client terminal
            System.out.println("Welcome to a fun session of joke and proverb cycle: " + user_Name);
            // Printing message on the client terminal - request to press enter
            System.out.println("If you would like to receive joke or proverb, please press enter! \n");
            // Clearing out the output buffer
            System.out.flush();
            // Variable nextJoke Declaration
            String nextJoke;
            //  Generating unique ID for each client and storing on the server.
            //  Created for future modifications on this code.
            uniqueClientID = UUID.randomUUID().toString();

            // Executes this do..while loop till the client enters quit request
            do {
                // Reading the client request for next joke/proverb from the server
                nextJoke = input.readLine();
                // clearing out listPosition Index
                indicatorPosition.clear();

                // checks whether client entered "quit" or not.
                // if not, proceeds on serving the joke/proverb request
                if(!nextJoke.toLowerCase().contains("quit") && !user_Name.toLowerCase().contains("quit")) {

                    // stores the indicator position of joke and proverb received from receiveJokeAndProverb method
                    // Format: [joke, proverb]
                    indicatorPosition = receiveJokeAndProverb(uniqueClientID, user_Name, jokeIndicator, randomOrder.get(0),
                            proverbIndicator, randomOrder.get(1), server_Name);

                    // Setting JokeIndicator to the first position of the indicatorPosition list and
                    jokeIndicator = indicatorPosition.get(0);
                    // Setting ProverbIndicator to the second position of the indicatorPosition list and
                    proverbIndicator = indicatorPosition.get(1);

                    // Checks whether the joke indicator have reached its limit of 4
                    // if yes, sets the indicator to 0 and
                    // re-randomizes the jokes order
                    if(jokeIndicator == 4) {
                        // Setting jokeIndicator to 0 i.e. first position
                        jokeIndicator = 0;
                        // Clearing out the randomJoke LL for next build up
                        randomJokeList.clear();
                        // initializing temporary i variable
                        int i=0;
                        // parses through the randomJokeString
                        while (i < randomJokeString.length()) {
                            // Adds backs to the randomJokeList LL
                            randomJokeList.add(String.valueOf(randomJokeString.charAt(i)));
                            i++;
                        }
                        // Shuffles the list
                        Collections.shuffle(randomJokeList);
                        // Creates new joke order string of size randomJokeList
                        StringBuilder newJokeOrderString = new StringBuilder(randomJokeList.size());
                        // Parses and appends the new ordered string
                        for(String rjd: randomJokeList){
                            newJokeOrderString.append(rjd);
                        }
                        // Sets new randomized key to the Random Order Linked List
                        randomOrder.set(0, newJokeOrderString.toString());
                    }
                    // Checks whether the proverb indicator have reached its limit of 4
                    // if yes, sets the indicator to 0 and
                    // re-randomizes the proverb order
                    if(proverbIndicator == 4) {
                        // Setting proverbIndicator to 0 i.e. first position
                        proverbIndicator = 0;
                        // Clearing out the randomProverb LL for next build up
                        randomProverbList.clear();
                        // initializing temporary i variable
                        int i=0;
                        // parses through the randomProverbString : Format : XWYZ
                        while (i < randomProverbString.length()) {
                            // Adds backs to the randomProverbList LL : Format : [X, W, Y, Z]
                            randomProverbList.add(String.valueOf(randomProverbString.charAt(i)));
                            i++;
                        }
                        // Shuffles the list
                        Collections.shuffle(randomProverbList);
                        // Creates new proverb order string of size randomProverbList
                        StringBuilder newProverbOrderString = new StringBuilder(randomProverbList.size());
                        for(String rpd: randomProverbList){
                            // Parses and appends the new ordered string
                            newProverbOrderString.append(rpd);
                        }
                        // Sets new randomized key to the Random Order Linked List
                        randomOrder.set(1, newProverbOrderString.toString());
                    }
                }
            } while (!nextJoke.toLowerCase().contains("quit") && !user_Name.toLowerCase().contains("quit"));
            // Prints the message if the client opts to quit the session
            System.out.println("Connection disconnected by the client..");
        }
        // if exception occurs, prints the ioException stack trace on the terminal.
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Method to build random joke order string on initial call ex: [W, X, Y, Z]
    private void buildRandomJokeOrder() {
        // Adds letter W to randomJokeList LinkedList
        randomJokeList.add("W");
        // Adds letter X to randomJokeList LinkedList
        randomJokeList.add("X");
        // Adds letter Y to randomJokeList LinkedList
        randomJokeList.add("Y");
        // Adds letter Z to randomJokeList LinkedList
        randomJokeList.add("Z");
        // Shuffles/Randomizes the joke order for each client on initial connection
        Collections.shuffle(randomJokeList);
        // Initializing variable randomJokeString of size randomJokeList
        randomJokeString = new StringBuilder(randomJokeList.size());
        // Takes in shuffled key - ex: XYWZ
        for(String joke: randomJokeList){
            randomJokeString.append(joke);
        }
        // Adds shuffled key to randomOrder LinkedList
        randomOrder.add(randomJokeString.toString());
    }

    // Method to build random proverb order string on initial call ex: [W, X, Y, Z]
    private void buildRandomProverbOrder() {
        // Adds letter W to randomProverbList LinkedList
        randomProverbList.add("W");
        // Adds letter X to randomProverbList LinkedList
        randomProverbList.add("X");
        // Adds letter Y to randomProverbList LinkedList
        randomProverbList.add("Y");
        // Adds letter Z to randomProverbList LinkedList
        randomProverbList.add("Z");
        // Shuffles/Randomizes the proverb order for each client on initial connection
        Collections.shuffle(randomProverbList);
        // Initializing variable randomProverbString of size randomProverbList
        randomProverbString = new StringBuilder(randomProverbList.size());
        // Takes in shuffled key - ex: ZWXY
        for(String proverb: randomProverbList){
            randomProverbString.append(proverb);
        }
        // Adds shuffled key to randomOrder LinkedList
        randomOrder.add(randomProverbString.toString());
    }

    // Method call to receive the jokes and proverbs for specific client connection
    private LinkedList<Integer> receiveJokeAndProverb(String uniqueClientID, String user_name, int jokeIndicator,
                                                      String randomJokeString, int proverbIndicator, String randomProverbString,
                                                             String server_Name)
    {
        // Variable socket declaration for local use
        Socket socket;
        // Variable dataFrmServer declaration
        String dataFrmServer;
        // Variable readFrmServer declaration
        BufferedReader readFrmServer;
        // Variable printToServer declaration
        PrintStream printToServer;
        LinkedList<Integer> indicatorList = new LinkedList<>();
        //indicatorList.clear();

        try{
            // Creating new socket object and binding it to port 4545
            socket = new Socket(server_Name, 4545);
            // Creating new buffered reader object and setting it to readFrmServer variable
            readFrmServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Creating new print stream object and setting it to printToServer variable
            printToServer = new PrintStream(socket.getOutputStream());

            // Sending uniqueClientID(UUID) to JokeServer
            printToServer.println(uniqueClientID);
            // Sending user/client name, random joke string and random proverb string to the JokeServer
            printToServer.println(user_name + ":" + randomJokeString + ":" + randomProverbString);
            // Sending joke indicator to the JokeServer - Keeps track in maintaining state of each client
            printToServer.println(jokeIndicator);
            // Sending proverb indicator to the JokeServer - Keeps track in maintaining state of each client
            printToServer.println(proverbIndicator);
            // Flushing/ Clearing out the printToServer
            printToServer.flush();

            // Initializing temp variable
            int temp = 0;
            // Receiving response from the Joke Server - gets the joke/proverb, joke indicator, proverb indicator
            while (temp < 3) {
                dataFrmServer = readFrmServer.readLine();
                if(dataFrmServer != null) {
                    switch (temp) {
                        // Displays the joke/proverb on the client terminal
                        case 0:  System.out.println(dataFrmServer);
                            break;
                        // Gets and parses to integer - the joke indicator from the server and adds to indicatorList List
                        // Displays JOKE CYCLE COMPLETE!! if the indicator = 4
                        case 1:  jokeIndicator = Integer.parseInt(dataFrmServer);
                            if(jokeIndicator == 4){
                                System.out.println("JOKE CYCLE COMPLETE!!");
                            }
                            indicatorList.add(jokeIndicator);
                            break;
                        // Gets and parses to integer - the proverb indicator from the server and adds to indicatorList List
                        // Displays PROVERB CYCLE COMPLETE!! if the indicator = 4
                        case 2:  proverbIndicator = Integer.parseInt(dataFrmServer);
                            if(proverbIndicator == 4){
                                System.out.println("PROVERB CYCLE COMPLETE!!");
                            }
                            indicatorList.add(proverbIndicator);
                            break;
                    }
                }
                temp++;
            }
            // Closing the socket connection
            socket.close();
        }
        // if exception occurs, prints the ioException stack trace on the terminal.
        catch(IOException ioException) {
            System.out.println ("Error in the Socket Connection!");
            ioException.printStackTrace ();
        }
        // Returns the indicatorList to confirm the current state of [joke, proverb] to the client connection
        return indicatorList;
    }
}


