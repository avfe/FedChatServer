package tech.fedorov;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

// Implement the Runnable interface that allows you to work with threads
public class ClientHandler implements Runnable {
    // Our server instance
    private Server server;
    // Incoming message
    private PrintWriter outMessage;
    // Outgoing message
    private Scanner inMessage;
    private static int PORT = 60606;
    // Client's socket
    private Socket clientSocket = null;
    // Number of clients in the chat, static field
    private static int clients_count = 0;

    // Constructor that accepts a client socket and server
    public ClientHandler(Socket socket, Server server) {
        this.PORT = server.PORT;
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // We override the run() method, which is called when
    // we write new Thread(client).start();
    @Override
    public void run() {
        try {
            while (true) {
                // The server sends a message
                server.sendMessageToAllClients("A new member has entered the chat!");
                server.sendMessageToAllClients("Clients in chat = " + clients_count);
                break;
            }

            while (true) {
                // If a message came from a client
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    // If the client sends this message, then the loop is interrupted and
                    // client leaves the chat
                    if (clientMessage.equalsIgnoreCase("exit")) {
                        break;
                    }
                    // Print a message to the console (for test)
                    System.out.println(clientMessage);
                    // Sending this message to all clients
                    server.sendMessageToAllClients(clientMessage);
                }
                // Stop execution of the thread for 100 ms
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }

    // Send message
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Client leaves the chat
    public void close() {
        // Remove a client from the list
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Clients in chat = " + clients_count);
    }
}