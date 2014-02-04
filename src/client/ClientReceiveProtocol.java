package client;

import java.io.BufferedReader;
import java.io.IOException;

import Command.Command;

public class ClientReceiveProtocol implements Runnable {
    
    private final BufferedReader in;
    private final Client client;
    private boolean isRunning = true;
    
    public ClientReceiveProtocol(BufferedReader in, Client client) {
        this.in= in;
        this.client = client;
    }
    
    /**
     * Waits for message from server and calls appropriate request handler
     */
    @Override
    public void run() {
    	// provide a way to kill thread
    	while(isRunning) {
	    	//handle the client
		    try {
		        handleConnection(in);
		    } catch (IOException e) {
		    	// Means connection has closed
		    }
    	}
    }
	    
    
    /**
     * Handle connection to server. Returns when client disconnects.
     * 
     * @param socket socket where the client is connected
     * @throws IOException if connection has an error or terminates unexpectedly
     */
    private void handleConnection(BufferedReader in) throws IOException {        

        for (String line = in.readLine(); line != null; line = in.readLine()) {
        	System.out.println("Handle Request: " + line);
        	handleRequest(line);                
        }
    }
    
    /**
     * Handler for server input, performing requested operations and returning an output message.
     * Receives:
     * 
     * Update Users = "users boardName user1 user2 user3..."
     * Update Available Boards = "boards board1 board2 board3"
     * Draw = "draw boardName command param1 param2 param3"
     *      Example: "draw boardName drawLineSegment x1 y1 x2 y2 color width"
     * Check and add User = "checkAndAddUser username boardName boolean"
     * New Board = "newBoard boardName boolean"
     * 
     * @param input message from server
     * @return message to client
     * @throws IOException 
     */
    private void handleRequest(String input) throws IOException, IllegalArgumentException {
        
    	String nameReg = "[a-zA-Z0-9\\.]+";
    	String regex = "(draw "+nameReg+"( "+nameReg+")+)|"
		    			+ "(users( "+nameReg+")+)|"
						+ "(exit "+nameReg+")|"
		    	        +"(boards( "+nameReg+")*)|"
		        		+ "(checkAndAddUser ("+nameReg+" "+nameReg+" (true|false)))|"
		    	        +"(newBoard "+nameReg+" (true|false))|"
		        		+ "(switch "+nameReg+" "+nameReg+")|(testHello)";
    	
    	System.out.println("input: "+input);
    	// make sure it's a valid input
        if (input.matches(regex)) {
            try {
	        	String[] tokens = input.split(" ");
	        	
	            if (tokens[0].equals("boards")) {
					client.setBoards(client.parseBoardsFromServerResponse(input));
	            } 
	            else if (tokens[0].equals("newBoard")) {
	                client.parseNewBoardFromServerResponse(input);
	            } 
	            else if (tokens[0].equals("checkAndAddUser")) {
	                client.parseNewUserFromServerResponse(input);
	            } 
	            else if (tokens[0].equals("users")) {
	                if (client.checkForCorrectBoard(tokens[1])) {   
	                    client.setUsers(client.parseUsersFromServerResponse(input));
	                }
	            } 
	            else if (tokens[0].equals("exit")) {
	                client.completeExit();
	            } 
	            else if (tokens[0].equals("draw")) {
	                Command command = new Command(input);
	                if (command.checkBoardName(client.getCurrentBoardName())) {
	                    client.applyCommand(command);
	                }
	            }
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
   
    }
 
    
    /**
     * Checks that the board is the correct one, parses the message into an array of users and calls client.setCanvasUsers
     * @param usersMessage: the message in the format "users boardName user1 user2 user3..."
     */
    public void updateUsers(String usersMessage) {
    }
    
    /**
     * Checks that the board is the correct one, parses the message into an array of boards and calls client.setBoards
     * @param boardsMessage: the message in the format "boards boardName board1 board2 board3..."
     */
    public void updateBoards(String boardsMessage) {
    }
    
    /**
     * Uses the Command class to create a command object and calls client.updateCanvasCommand
     * @param commandMessage: the message in the format "draw boardName command param1 param2 param3..."
     */
    public void commandCanvas(String commandMessage) {
    }

    /**
     * Used to kill thread from outside
     */
    public void kill() {
    	isRunning = false;
    }
    
    /**
     * For testing purposes. Calls handleRequest on input
     * @param input
     * @throws IOException 
     * @throws IllegalArgumentException 
     */
    public void testHandleRequest(String input) throws IllegalArgumentException, IOException {
        handleRequest(input);
    }
    
}
