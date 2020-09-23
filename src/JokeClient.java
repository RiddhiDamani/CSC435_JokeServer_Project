import java.io.*;
import java.net.Socket;
import java.util.*;

// Client Code for JokeServer Multithreading Assignment

public class JokeClient {

    public static void main(String args[]) {
        // Defining variable name for the server name
        String serverName;
        String user_Name;
        int jokePosition = 0;
        int proverbPosition = 0;
        LinkedList<String> randomOrder = new LinkedList<>();
        LinkedList<Integer>  listPosition = new LinkedList<>();
        LinkedList<String> randomJokeDisplay = new LinkedList<>();
        String uniqueClientID;
        StringBuilder randomJokeString;
        LinkedList<String> randomProverbDisplay;
        StringBuilder randomProverbString;

        serverName = (args.length < 1) ? "localhost" : args[0];

        System.out.println("Interaction with : " + serverName + " is in progress, Port: 4545");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        randomJokeDisplay.add("A");
        randomJokeDisplay.add("B");
        randomJokeDisplay.add("C");
        randomJokeDisplay.add("D");

        Collections.shuffle(randomJokeDisplay);

        randomJokeString = new StringBuilder(randomJokeDisplay.size());
        for(String joke: randomJokeDisplay){
            randomJokeString.append(joke);
        }

        randomOrder.add(randomJokeString.toString());


        randomProverbDisplay = new LinkedList<>();

        randomProverbDisplay.add("A");
        randomProverbDisplay.add("B");
        randomProverbDisplay.add("C");
        randomProverbDisplay.add("D");

        Collections.shuffle(randomProverbDisplay);

        randomProverbString = new StringBuilder(randomProverbDisplay.size());
        for(String joke: randomProverbDisplay){
            randomProverbString.append(joke);
        }

        randomOrder.add(randomProverbString.toString());

//        performJokeRandomization();
//        performProverbRandomization();

        try {

            String nextJoke;

            System.out.println("Please enter your name or enter 'quit' to end communication: ");
            System.out.flush();
            user_Name = input.readLine();
            System.out.println("Welcome to a fun session of joke and proverb cycle: " + user_Name);
            System.out.println("If you would like to receive joke or proverb, please press enter! \n");
            System.out.flush();
            uniqueClientID = UUID.randomUUID().toString();

//            generateUUID();

            do {
                nextJoke = input.readLine();
                listPosition.clear();

                if(nextJoke.indexOf("quit") < 0 && user_Name.indexOf("quit")<0) {
                    listPosition = receiveJokeAndProverb(user_Name, uniqueClientID, randomOrder.get(0),
                            randomOrder.get(1), jokePosition, proverbPosition, serverName);

                    jokePosition = listPosition.get(0);
                    proverbPosition = listPosition.get(1);

                    if(jokePosition == 4) {
                        jokePosition = 0;
                        randomJokeDisplay.clear();

                        for(int i=0;i<randomJokeString.length();i++){
                            randomJokeDisplay.add(String.valueOf(randomJokeString.charAt(i)));
                        }
                        Collections.shuffle(randomJokeDisplay);


                        StringBuilder newJokeOrderString = new StringBuilder(randomJokeDisplay.size());
                        for(String s: randomJokeDisplay){
                            newJokeOrderString.append(s);
                        }

                        randomOrder.set(0,newJokeOrderString.toString());
                    }

                    if(proverbPosition == 4){
                        proverbPosition=0;

                        randomProverbDisplay.clear();

                        for(int i=0;i<randomProverbString.length();i++){
                            randomProverbDisplay.add(String.valueOf(randomJokeString.charAt(i)));
                        }
                        Collections.shuffle(randomProverbDisplay);


                        StringBuilder newProverbOrderString = new StringBuilder(randomProverbDisplay.size());
                        for(String s: randomProverbDisplay){
                            newProverbOrderString.append(s);
                        }

                        randomOrder.set(1,newProverbOrderString.toString());
                    }

                }
            } while (nextJoke.indexOf("quit") < 0 && user_Name.indexOf("quit") < 0);
            System.out.println("Cancelled by user request. ");
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static LinkedList<Integer> receiveJokeAndProverb(String user_name, String uniqueClientID,
                                                             String randomJokeString, String randomProverbString,
                                                             int jokePosition, int proverbPosition, String serverName)
    {
        Socket socket;
        BufferedReader readFrmServer;
        PrintStream printToServer;
        String textFromServer;
        LinkedList<Integer> indicatorList = new LinkedList<>();
        indicatorList.clear();

        try{
            socket = new Socket(serverName, 4545);

            readFrmServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printToServer = new PrintStream(socket.getOutputStream());

            printToServer.println(uniqueClientID);
            printToServer.println(user_name+":"+randomJokeString+":"+randomProverbString);
            printToServer.println(jokePosition);
            printToServer.println(proverbPosition);

            printToServer.flush();

            for(int i=0; i<3; i++) {
                textFromServer = readFrmServer.readLine();
                if (textFromServer !=null && i==0) System.out.println(textFromServer);
                else if (textFromServer != null && i==1) {
                    jokePosition = Integer.parseInt(textFromServer);
                    if(jokePosition==4){
                        System.out.println("Joke Cycle Completed!!!!");
                        indicatorList.add(jokePosition);
                    } else{
                        indicatorList.add(jokePosition);
                    }
                } else if (textFromServer != null && i==2) {
                    proverbPosition = Integer.parseInt(textFromServer);
                    if(proverbPosition==4){
                        System.out.println("Proverb Cycle Completed!!!!");
                        indicatorList.add(proverbPosition);
                    } else{
                        indicatorList.add(proverbPosition);
                    }
                }
            }

            socket.close();
        }
        catch(IOException ioException) {
            System.out.println ("Error in the Socket Connection!");
            ioException.printStackTrace ();
        }

        return indicatorList;
    }

//    private static void performJokeRandomization() {
//
//    }
//
//    private static void performProverbRandomization() {
//    }

//    private static void generateUUID() {
//        uniqueClientID = UUID.randomUUID().toString();
//    }
}
