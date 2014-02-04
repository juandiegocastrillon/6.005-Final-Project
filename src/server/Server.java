package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import Command.Command;

/**
 * Server for collaborative whiteboard application
 * 
 * Concurrency Argument:
 *   - All board objects are thread safe (see Board.java)
 *   - All methods that modify this objects data representation are 
 *     made concurrent via the monitor pattern
 * 
 * @author Josh
 */
public class Server {
    
    //stores all the boards created as Board objects associated with names
    private Hashtable<String, Board> boards = new Hashtable<String, Board>();
    private List<Socket> clients = new LinkedList<Socket>();
    private final ServerSocket serverSocket;
    
    /**
     * Create our server on port port
     * @param port: port for server to listen on
     * @throws IOException 
     */
    public Server(int port) throws IOException {
    	serverSocket = new ServerSocket(port);
    	// Add shutdown hook to close server gracefully
    	addShutDownHook();
    }
 
    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * 
     * @throws IOException if the main server socket is broken
     *   Note: (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
    	System.out.println("Server serving");
        while (true) {
        	
            // block until a client connects
            Socket socket = serverSocket.accept();
            clients.add(socket);

            // create new thread for each connection
            new Thread(new ServerProtocol(socket, this)).start();
        }
    }
    
    /**
     * Add the command on the server's queue of commands Requires valid board
     * name
     * 
     * @param boardName: the board to draw on
     * @param command: the command to perform on the board
     */
    public void updateBoard(String boardName, Command command) {
        boards.get(boardName).addCommand(command);
    }
    
    /**
     * Checks if the board name is unique
     * Creates a new board with the specified board name
     * 
     * @param boardName: the name of the new board
     * @return: whether or not the new board was successfully made
     */
    public synchronized boolean newBoard(String boardName) {
        if(boards.containsKey(boardName)) {
        	return false;
        } else {
            boards.put(boardName, new Board());
            return true;
        }
    }
    
    /**
     * Iterates through all the sockets and sends the command to each
     * 
     * @param Command - command to be sent to all clients 
     */
    public void sendCommandToClients(Command command) {
    	for (Socket client: clients) {
			try {
	    		if (!client.isClosed()) {
					PrintWriter out = new PrintWriter(client.getOutputStream(), true);
					out.println(command.toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Gets the users from a board
     * 
     * @param boardName
     * @return String which is a list of all users on the board
     */
    public String getUsers(String boardName) {
        Board board = boards.get(boardName);
        String[] users = board.getUsers();
        StringBuilder usersString = new StringBuilder("");
        for (String user: users) {
            usersString.append(user + " ");
        }
        
        if(usersString.length() > 0) {
            usersString.deleteCharAt(usersString.length() - 1);
        }
        
        return usersString.toString();
    }
    
    /**
     * Removes the user from the old board and adds the user to the new board.
     * 
     * @param username: the username of the user making the switch
     * @param oldBoardName: name of the board the user is switching from
     * @param newBoardName: the name of the board the user is switching to
     * @return: List of Commands of the new Board the user is switching to           
     */
    public List<Command> switchBoard(String username, String oldBoardName, String newBoardName) {
        boards.get(oldBoardName).deleteUser(username);
        boards.get(newBoardName).addUser(username);
        return boards.get(newBoardName).getCommands();
    }
    
    /**
     * Removes the user from all boards
     * @param username: the username of the user exiting
     */
    public synchronized void exit(String username) {
        for(String boardName: boards.keySet()) {
            Board board = boards.get(boardName);
            board.deleteUser(username);
        }
    }
    
    /**
     * Adds the user to a board for the first time
     * @param username: the entering user
     * @param boardName: the board they have chosen to enter
     */
    public synchronized void enter(String username, String boardName) {
        Board board = boards.get(boardName);
        board.addUser(username);
    }
    
    /**
     * Gets a list of all the board names
     * @return: a list of a all the board names
     */
    public synchronized String getBoards() {
        String[] boardsArray = boards.keySet().toArray(new String[0]);
       
        StringBuilder boardsString = new StringBuilder("");
        for (String board: boardsArray) {
        	boardsString.append(board + " ");
        }
        
        if(boardsString.length() > 0) {
        	boardsString.deleteCharAt(boardsString.length() - 1);
        }
        
        return boardsString.toString();
    }
    
    /**
     * Checks if the username is unique and if it is, return true and enter the user
     * @param username: the username to check
     * @param boardName: the board the user wants to enter
     * @return: whether or not the user entered successfully
     */
    public synchronized boolean checkUser(String username, String boardName) {
        for (String board : boards.keySet()) {
            if (!boards.get(board).checkUsernameAvailable(username)) {
                return false;
            }
        }
        // If user is unique, add them to board
        enter(username, boardName);
        return true;
    }
    
    /**
     * Returns clients connected to server
     * @return
     */
    public List<Socket> getClients() {
        return clients;
    }
    
    /**
     * Gets all commands sent to a specific board
     * @param boardName
     * @return
     */
    public Board getCommands(String boardName) {
        return boards.get(boardName);
    }
    
    /**
     * Shuts down all client connections and then shuts down serverSocket
     * @throws IOException
     */
    public void shutDown() throws IOException {
    	for (Socket client: clients) {
    		client.close();
    	}
    	serverSocket.close();
    }
    
    public void addShutDownHook() {
    	// Add shutdown hook to shutdown server gracefully
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
					shutDown();
					mainThread.join();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        });
    }
    
    /**
     * Main method to launch server from command line
     * @param args
     */
    public static void main(String[] args) {

        int port = 4444; // default port

        // Check for and parse command line arguments
        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while ( ! arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > 65535) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: Server [--port PORT]");
            return;
        }
    	
    	
    	// Try to launch the server
		try {
			Server server = new Server(4444);
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
}
