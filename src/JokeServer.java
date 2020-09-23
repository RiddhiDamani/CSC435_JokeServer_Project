import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.LinkedList;


// Server Code for InetServer Multithreading Assignment
public class JokeServer {

    //Toggle between Joke and Proverb Mode
    static boolean ToggleModeJokeProverb = true;
    public static void main(String args[]) throws IOException {

        // Queue length - for number of request to be queued by OS
        int queue_length = 6;

        // server socket port listening at port #
        int port = 4545;

        // Create object for Socket
        Socket socket;
        System.out.println("Riddhi Damani's Joke Server launching. Listening to port " + port + "... \n");

        AdminModeServer modeServerInstance = new AdminModeServer();
        Thread  modeThread = new Thread(modeServerInstance);
        modeThread.start();

        // Passing port number and queue length in the constructor of ServerSocket
        ServerSocket serverSocket = new ServerSocket(port, queue_length);

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
        int port = 5050;
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
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

// Worker Code for Joke Server
class Worker extends Thread {
    LinkedList<String> proverbList = new LinkedList<>();
    LinkedList<String> jokeList = new LinkedList<>();
    String [][] joke2DList;
    String [][] proverb2DList;
    String randomJokeString;
    String randomProverbString;
    PrintStream outputData = null;
    Socket socket;
    Worker (Socket s) {
        socket = s;
    }

    public void run() {

        int jokeIndicator = 0;
        int proverbIndicator = 0;
        String clientData;
        String jokeIndicatorMessage;
        String proverbIndicatorMessage;
        BufferedReader inputData = null;


        try {
            inputData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputData = new PrintStream(socket.getOutputStream());

            try {
                String user_Name;
                String uniqueClientID;

                uniqueClientID = inputData.readLine();
                clientData = inputData.readLine();

                String[] clientDataDisplay = clientData.split(":");

                user_Name = clientDataDisplay[0];
                randomJokeString = clientDataDisplay[1];
                randomProverbString =  clientDataDisplay[2];

                jokeIndicatorMessage = inputData.readLine();
                jokeIndicator = Integer.parseInt(jokeIndicatorMessage);

                proverbIndicatorMessage = inputData.readLine();
                proverbIndicator = Integer.parseInt(proverbIndicatorMessage);


                receiveJokeAndProverb(user_Name, uniqueClientID, jokeIndicator,
                    proverbIndicator, outputData);

            }
            catch (IOException ioException) {
                System.out.println("Error in Server Read Operation!");
                ioException.printStackTrace();
            }
            socket.close();
        }
        catch (IOException ioException) {
            System.out.println(ioException);
        }
    }

    private void receiveJokeAndProverb(String user_name, String uniqueClientID, int jokeIndicator,
                                       int proverbIndicator, PrintStream outputData) {

        LinkedList<String> UUIDList = new LinkedList<>();

        buildJokeList();
        buildProverbList();

         joke2DList = new String[][] {

                {"A", "JA " + user_name + ": I accidentally handed my wife a glue stick instead of a chapstick. She still isn’t talking to me."},
                {"B", "JB " + user_name + ": Why do bees hum? They don’t remember the lyrics!"},
                {"C", "JC " + user_name + ": I'm reading a book about anti-gravity. It's impossible to put down."},
                {"D", "JD " + user_name + ": Why can’t a bike stand on its own? It’s two tired."}
        };

        proverb2DList = new String[][] {

                {"A", "PA " + user_name + ": Absence makes the heart grow fonder."},
                {"B", "PB " + user_name + ": Always put your best foot forward. "},
                {"C", "PC " + user_name + ": A ship in the harbor is safe, but that is not what a ship is for."},
                {"D", "PD " + user_name + ": Cleanliness is next to Godliness."}
        };

        if(JokeServer.ToggleModeJokeProverb){
            try {

                if(UUIDList.contains(uniqueClientID))
                    System.out.println("User Already Exists on the Server");
                else
                    UUIDList.add(uniqueClientID);

                if (jokeIndicator == 4) {
                    System.out.println(jokeList);
                    Collections.shuffle(jokeList);
                }
                else {
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
                    jokeIndicator++;
                }
            }
            catch (IndexOutOfBoundsException ex) {
                outputData.println("Unable to get details for: " + uniqueClientID);
            }
        }
        else {
            try {
                if(UUIDList.contains(uniqueClientID))
                    System.out.println("User Already Exists on the Server");
                else
                    UUIDList.add(uniqueClientID);

                if (proverbIndicator == 4) {
                    System.out.println(proverbList);
                    Collections.shuffle(proverbList);

                } else {
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
                    proverbIndicator++;
                }
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                outputData.println("Unable to locate: " + uniqueClientID);
            }
        }
        outputData.println(jokeIndicator);
        outputData.println(proverbIndicator);
    }

    private void setJoke2DList(int i) {
        if(jokeList.get(i).equals("A")){
            outputData.println(joke2DList[0][1]);
            System.out.println((joke2DList[0][1]));
        } else if(jokeList.get(i).equals("B")){
            outputData.println(joke2DList[1][1]);
            System.out.println((joke2DList[1][1]));
        } else if(jokeList.get(i).equals("C")) {
            outputData.println(joke2DList[2][1]);
            System.out.println((joke2DList[2][1]));
        } else if(jokeList.get(i).equals("D")) {
            outputData.println(joke2DList[3][1]);
            System.out.println((joke2DList[3][1]));
        }
    }

    private void setProverb2DList(int i) {
        if(proverbList.get(i).equals("A")){
            outputData.println(proverb2DList[0][1]);
            System.out.println((proverb2DList[0][1]));
        } else if(proverbList.get(i).equals("B")){
            outputData.println(proverb2DList[1][1]);
            System.out.println((proverb2DList[1][1]));
        } else if(proverbList.get(i).equals("C")) {
            outputData.println(proverb2DList[2][1]);
            System.out.println((proverb2DList[2][1]));
        } else if(proverbList.get(i).equals("D")) {
            outputData.println(proverb2DList[3][1]);
            System.out.println((proverb2DList[3][1]));
        }
    }

    private void buildJokeList() {
        for(int i=0; i<=3; i++) {
            jokeList.add(String.valueOf(randomJokeString.charAt(i)));
        }
        //System.out.println(jokeList);
    }

    private void buildProverbList() {
        for(int i=0; i<=3; i++) {
            proverbList.add(String.valueOf(randomProverbString.charAt(i)));
        }
        //System.out.println(proverbList);
    }
}
