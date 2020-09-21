import java.io.*;

// Client Code for InetServer Multithreading Assignment
// Need to change for Joke Server Assignment
public class JokeClient {
    public static void main(String args[]) {
        // Defining variable name for the server name
        String serverName;

        if(args.length < 1) {
            serverName = "localhost";
        }
        else {
            serverName = args[0];
        }

        // Printing client's start-up message on the terminal
        System.out.println("Riddhi Damani's Inet Client..1.9. \n");
        System.out.println("Utilizing Server: " + serverName + ", Port: 1565");

        // Setting 'input' variable to read the Host name or IP address entered by the client.
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            String name;
            do {
                // Printing a message on the terminal to request for host name or an IP address
                System.out.println("Enter a host or an IP address, (quit) to end: ");
                // Flushes the print stream whenever a new line is written on the terminal
                System.out.flush();

                // Setting the name variable to the value entered by the client
                name = input.readLine();

//                if(name.indexOf("quit") < 0) {
//                    getRemoteAddress (name, serverName);
//                }
            }
            // Quiting the client connection with the server
            while (name.indexOf("quit") < 0);
            System.out.println("Cancelled by user request. ");
        }
        // Input Output Exception handling
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

//    static void getRemoteAddress(String name, String serverName) {
//        Socket socket;
//        BufferedReader fromServer;
//        PrintStream toServer;
//        String textFromServer;
//
//        try {
//            // Open server port connection by selecting the desired port number
//            socket = new Socket(serverName, 1565);
//
//            // Creating input stream for the socket.
//            fromServer = new BufferedReader(new InputStreamReader((socket.getInputStream())));
//            // Creating output stream for the socket.
//            toServer = new PrintStream(socket.getOutputStream());
//
//            // Passing the server data - IP address or the host name
//            toServer.println(name);
//            // Flushes the print stream whenever a new line is written on the terminal
//            toServer.flush();
//
//            // Reading response from the server textFromServer variable and then printing it out.
//            for(int i = 1; i <= 3; i++) {
//                textFromServer = fromServer.readLine();
//                if(textFromServer != null) {
//                    System.out.println(textFromServer);
//                }
//            }
//            // Closing Socket after transmission of the data
//            socket.close();
//        }
//        // Input Output Exception handling
//        catch (IOException ioException) {
//            System.out.println("Socket Error.");
//            ioException.printStackTrace();
//        }
//    }

    // String conversion for portability
//    static String toText (byte ipAddress[]) {
//        StringBuffer result = new StringBuffer();
//        for(int i = 0; i < ipAddress.length; i++) {
//            if(i > 0) result.append(".");
//            result.append(0xff & ipAddress[i]);
//        }
//        //System.out.println("Inside IP Address -Client: " + result);
//        return result.toString();
//    }

}
