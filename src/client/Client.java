package client;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.SwingUtilities;

import Command.Command;

public class Client {
    
    //the username the client will go by in this session
    //must be unique; no other clients can have this user name
    private String username;
    //the name of the board currently being drawn upon
    private String currentBoardName;
    //the color the user is currently drawing in
    private Color currentColor = Color.BLACK;
    //the width of the brush the user is currently drawing with
    private float currentWidth = 10;
    private BufferedImage drawingBuffer;
    
    // used for comm
    private String[] boards = {};
    private boolean boardsUpdated;
    private Hashtable<String, Boolean> newBoardMade = new Hashtable<String, Boolean>();
    private Hashtable<String, Boolean> newBoardSuccessful = new Hashtable<String, Boolean>();
    private boolean userCheckMade;
    private boolean usersUpdated;
    private String[] users = {};
    private boolean exitComplete;
    private boolean isErasing;
    
    //the socket with which the user connects to the client
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ClientReceiveProtocol receiveProtocol;
    private Thread receiveThread;
    
    private ClientGUI clientGUI;
    
    
    public Client(String host, int port) throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        receiveProtocol = new ClientReceiveProtocol(in, this);
        receiveThread = new Thread(receiveProtocol);
        receiveThread.start();
        clientGUI = new ClientGUI(this);
        addShutdownHook();
    }
    
    
    public BufferedImage getDrawingBuffer() {
    	return drawingBuffer;
    }
    
    public void setDrawingBuffer(BufferedImage newImage) {
    	drawingBuffer = newImage;
    }
    
    public void setIsErasing(boolean newIsErasing) {
    	isErasing = newIsErasing;
    }
    
    public boolean isErasing() {
    	return isErasing;
    }

    /**
     * Confirms exit of client from server
     */
    public void completeExit() {
        exitComplete = true;
    }

    /**
     * Adds commands to shutdown in order to shutdown gracefully
     */
    public void addShutdownHook() {
        // close socket on exit
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                try {
                	// kill receiving thread and wait for it to close out
                    if (username!= null) {
                        try {
                            exitComplete = false;
                            makeRequest("exit "+username).join();
                            
                            boolean timeout = false;
                            int timeoutCounter = 0;
                            int maxAttempts = 100;
                            int timeoutDelay = 10;
                            while(!exitComplete && !timeout) {
                                timeoutCounter++;
                                if (timeoutCounter >= maxAttempts) {
                                    timeout = true;
                                    System.out.println("timeout on exit");
                                }
                                Thread.sleep(timeoutDelay);
                            }
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
					receiveProtocol.kill();
					socket.shutdownInput();
					socket.shutdownOutput();
					
                	socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        });
    }

    /**
     * Checks with the server to make sure the username hasn't already been taken and if it hasn't, create the user
     * @param username: the user's choice of username
     * @return: true if username creation is successful, false if not
     */
    public boolean createUser(String username, String boardName) throws Exception {
        makeRequest("checkAndAddUser "+username+" "+boardName).join();
        userCheckMade = false;
        boolean timeout = false;
        int timeoutCounter = 0;
        int maxAttempts = 100;
        int timeoutDelay = 10;
        while(!userCheckMade && !timeout) {
            timeoutCounter++;
            if (timeoutCounter >= maxAttempts) {
                timeout = true;
                System.out.println("timeout on new user "+username);
            }
            Thread.sleep(timeoutDelay);
        }
        return (this.username != null && currentBoardName != null);
    }
    
    public void parseNewUserFromServerResponse(String response) throws Exception {
        String[] elements = response.split(" ");
        if(elements[0]!="check"&& elements.length!=4) {
            throw new Exception("Server returned unexpected result: " + response);
        }
        
        boolean created = Boolean.valueOf(elements[3]);
        if (created) {
            this.username = elements[1];
            this.currentBoardName = elements[2];
        }
        userCheckMade = true;
    }
    
    /**
     * Switches the current board to the board with the given name
     * server switch command
     * Updates the current users of the canvas
     * @param newBoardName: the name of the new board
     */
    public void switchBoard(String newBoardName) {
        try {
            makeRequest("switch "+username+" "+currentBoardName+" "+newBoardName);
            currentBoardName = newBoardName;
            getCanvas().updateCurrentUserBoard();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void applyCommand(Command command) {
        command.invokeCommand(getCanvas());
    }
    
    public void makeDrawRequest(String command) throws IOException {
        makeRequest("draw "+currentBoardName+" "+command);
    }

    public ClientGUI getClientGUI() {
    	return clientGUI;
    }
    
    /**
     * Checks that the board name hasn't already been taken and if it hasn't,
     * creates a new board on the server and names it with the given name
     * 
     * @param newBoardName
     *            the name to name the new board with
     * @return true if the board creation is successful, false if not
     */
    public boolean newBoard(String newBoardName) throws Exception {
        if (newBoardMade.containsKey(newBoardName)) return false;
        newBoardMade.put(newBoardName, false);
        newBoardSuccessful.put(newBoardName, true);
        makeRequest("newBoard "+newBoardName).join();
        boolean timeout = false;
        int timeoutCounter = 0;
        int maxAttempts = 100;
        int timeoutDelay = 10;
        while(!newBoardMade.get(newBoardName) && !timeout) {
            timeoutCounter++;
            if (timeoutCounter >= maxAttempts) {
                timeout = true;
                System.out.println("timeout on new board "+newBoardName);
            }
            Thread.sleep(timeoutDelay);
        }
        boolean successful = newBoardSuccessful.get(newBoardName);
        newBoardMade.remove(newBoardName);
        newBoardSuccessful.remove(newBoardName);
        return successful;
    }
    
    public void parseNewBoardFromServerResponse(String response) throws Exception {
        if(!response.contains("new")) {
            throw new Exception("Server returned unexpected result: " + response);
        }
        String[] elements = response.split(" ");
        String boardName = elements[1];
        boolean successful = Boolean.valueOf(elements[2]);
        newBoardSuccessful.put(boardName, successful);
        newBoardMade.put(boardName, true);
    }
    
    /**
     * Check that the boardName and currentBoardName are the same and then perform the command on the canvas
     * @param boardName: the board this command is for
     * @param command: the command to perform on the canvas
     */
    public void commandCanvas(String boardName, Command command) {
        if (command.checkBoardName(boardName)) {
            command.invokeCommand(getCanvas());
        }
    }
    
    /**
     * Gets the users for the current board from the server and sets them
     */
    public String[] getUsers() throws Exception {
        usersUpdated = false;
        makeRequest("users "+currentBoardName);
        boolean timeout = false;
        int timeoutCounter = 0;
        int maxAttempts = 100;
        int timeoutDelay = 10;
        while(!usersUpdated && !timeout) {
            timeoutCounter++;
            if (timeoutCounter >= maxAttempts) {
                timeout = true;
                System.out.println("timeout on new users");
            }
            Thread.sleep(timeoutDelay);
        }
        return users;
    }
    
    public String[] parseUsersFromServerResponse(String response) throws Exception {
        String[] elements = response.split(" ");
        if(!elements[0].equals("users")) {
            throw new Exception("Server returned unexpected result: " + response);
        }
        return Arrays.copyOfRange(elements, 2, elements.length);
    }
    
    public void setUsers(String[] newUsers) {
        users = newUsers;
        usersUpdated = true;
    }
    
    /**
     * Gets the current color to use for drawing a line segment on the canvas
     * @return the currentColor being used to draw
     */
    public Color getCurrentColor() {
        return currentColor;
    }
    
    /**
     * Gets the current width to use for drawing a line segment on the canvas
     * @return the currentWidth being used to draw
     */
    public float getCurrentWidth() {
        return currentWidth;
    }
    
    /**
     * Sets the newWidth, probably based off of a slider movement on the canvas
     * @param newWidth: the new width of the stroke
     */
    public void setCurrentWidth(float newWidth) {
        currentWidth = newWidth;
    }
    
    /**
     * Sets the newColor, probably based off of a color picker selection on the canvas
     * @param newWidth: the new color of the stroke
     */
    public void setCurrentColor(Color newColor) {
        currentColor = newColor;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setCurrentBoardName(String currentBoardName) {
        this.currentBoardName = currentBoardName;
    }
    

    public String[] getBoards() throws Exception {
    	
    	boardsUpdated = false;
    	// make request for board update and wait for it to finish
    	makeRequest("boards").join();
    	
    	// Wait for response from server
    	boolean timeout = false;
    	int timeoutCounter = 0;
    	int maxAttempts = 100;
    	int timeoutDelay = 10;
    	while(!boardsUpdated && !timeout) {
    		timeoutCounter++;
    		if (timeoutCounter >= maxAttempts) {
    			timeout = true;
    			System.out.println("timeout on boards update");
    		}
    		Thread.sleep(timeoutDelay);
    	}
 
    	// boards by now will have either been updated, or if it times out
    	// then it will return what it last had
    	return this.boards;
 
    }
    
    /**
     * 
     * @param response
     * @return
     * @throws Exception
     */
    public String[] parseBoardsFromServerResponse(String response) throws Exception {
    	
        if(!response.contains("boards")) {
        	throw new Exception("Server returned unexpected result: " + response);
        }
        
        String[] boardsListStrings = response.split(" ");
        return Arrays.copyOfRange(boardsListStrings, 1, boardsListStrings.length);
    }
    
    /**
     * Used to set boards
     */
    public void setBoards(String[] newBoards) {
    	boards = newBoards;
    	boardsUpdated = true;
    }
    
    public String getCurrentBoardName() {
        return currentBoardName;
    }
    
    // Make request in new thread
    public Thread makeRequest(String request) throws IOException {
    	
    	Thread requestThread = new Thread(new ClientSendProtocol(out, request));
        requestThread.start();
        
        return requestThread;
    }
    
    public Canvas getCanvas() {
        return clientGUI.getCanvas();
    }
    
    public String getUsername() {
        return username;
    }
    
    public boolean checkForCorrectBoard(String boardName) {
        return boardName.equals(currentBoardName);
    }
    
    /**
     * For testing purposes. Gets the ClientReceiveProtocol
     * @param args
     */
    public ClientReceiveProtocol getClientReceiveProtocol() { 
        return receiveProtocol;
    }
    
    /**
     * Testing purposes for new board method
     * @return
     */
    public Hashtable<String, Boolean> getBoardSuccessful() {
        return newBoardSuccessful;
    }
    
    /**
     * Get exitComplete
     */
    public boolean getExitComplete() {
        return exitComplete;
    }
    
    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					@SuppressWarnings("unused")
					Client client = new Client("localhost", 4444);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }

}
