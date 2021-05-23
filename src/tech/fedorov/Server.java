package tech.fedorov;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class Server {
    // Port that our server will listen to
    static int PORT = 60606;
    Scanner in = new Scanner(System.in);
    // List of clients that will connect to the server
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public Server() {
        // Client's socket
        Socket clientSocket = null;
        // Server's socket
        ServerSocket serverSocket = null;
        try {
            // Creating a server socket on a specific port
            System.out.println("Enter port: ");
            PORT = in.nextInt();
            serverSocket = new ServerSocket(PORT, 0);
            System.out.println("Server is running!");
            // Getting IP of our machine
            System.out.println("Your adapters and IPs:");
            System.out.println("---------------");
            String ip;
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    // filters out 127.0.0.1 and inactive interfaces
                    if (iface.isLoopback() || !iface.isUp())
                        continue;

                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while(addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        ip = addr.getHostAddress();
                        System.out.println(iface.getDisplayName() + " " + ip);
                    }
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            System.out.println("---------------");
            System.out.println("Waiting for connection...");
            // Start an endless loop
            while (true) {
                // Thus we are waiting for connections
                clientSocket = serverSocket.accept();
                System.out.println("New client connected!");
                // Create a client handler that connected to the server
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                // Each client connection is processed in a new thread
                new Thread(client).start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                // Close the connection
                clientSocket.close();
                System.out.println("Server stopped");
                serverSocket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Sending a message to all clients
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o: clients) {
            o.sendMsg(msg);
        }

    }

    // Remove the client from the collection when leaving the chat
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}