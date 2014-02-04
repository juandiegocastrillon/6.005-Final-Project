package client;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import server.Server;

/**
 * Testing Strategy:
 * 
 *  Update Users:
 *      No Users
 *      Multiple Users
 *      One Hundred Users
 *  Update Available Boards:
 *      No Boards
 *      Two Boards
 *      Multiple Boards
 *      Way more Boards
 *  Check and Add User
 *      Add one User - true
 *      Add one User - false
 *      Add combinations of true and false users
 *  New Board:
 *      Add one new board
 *      Add two new boards
 *      Add two false boards
 *      Add one true, one false board
 *      Add multiple true and false boards
 *  Exit:
 *      Test exit connection
 */
public class ClientReceiveProtocolTest {
    
    /******************** Update Users  *****************/
    @Test 
    // no users on board
    public void noUsersUpdateUsersTest() throws Exception {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "users";
        protocol.testHandleRequest(input);
        
        String[] users = {};
        Arrays.equals(client.getUsers(), users);
    }
    
    @Test 
    // mulitple users one board
    public void multipleUsersUpdateUsersTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "users user1 user2 user3 user4";
        protocol.testHandleRequest(input);
        
        String[] users = {"user1", "user2", "user3", "user4"};
        Arrays.equals(client.getUsers(), users);
    }
    
    @Test 
    // more users one board
    public void hundredUsersUpdateUsersTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "users";
        List<String> users = new ArrayList<String>();
        for (int i=0; i<100; i++) {
            input += " user" + i;
            users.add("user" + i);
        }
        protocol.testHandleRequest(input);
        
        Arrays.equals(client.getUsers(), users.toArray());
    }

    /******************** Update Available Boards*******/
    @Test 
    // no boards
    public void noBoardsUpdateBoardsTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "boards";
        protocol.testHandleRequest(input);
        
        String[] boards = {};
        Arrays.equals(client.getBoards(), boards);
    }
    
    @Test 
    // three boards
    public void twoBoardsUpdateBoardsTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "boards board1 board2 board3";
        protocol.testHandleRequest(input);
        
        String[] boards = {"board1", "board2", "board3"};
        Arrays.equals(client.getBoards(), boards);
    }
    
    @Test 
    // multiple boards
    public void multipleBoardsUpdateBoardsTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "boards";
        List<String> boards = new ArrayList<String>();
        for (int i=0; i<100; i++) {
            input += " board" + i;
            boards.add("board" + i);
        }
        protocol.testHandleRequest(input);
        
        Arrays.equals(client.getBoards(), boards.toArray());
    }
    
    @Test 
    // way more boards
    public void wayMoreBoardsUpdateBoardsTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "boards";
        List<String> boards = new ArrayList<String>();
        for (int i=0; i<500; i++) {
            input += " board" + i;
            boards.add("board" + i);
        }
        protocol.testHandleRequest(input);
        
        Arrays.equals(client.getBoards(), boards.toArray());
    }

    /******************** Check and Add User ***********/
    @Test
    // add one true user
    public void basicCheckAndAddUserTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "checkAndAddUser user board true";
        protocol.testHandleRequest(input);
        
        assertEquals(client.getUsername(), "user");
        assertEquals(client.getCurrentBoardName(), "board");
    }
    
    @Test
    // add one false user
    public void basicFalseCheckAndAddUserTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "checkAndAddUser user2 board false";
        protocol.testHandleRequest(input);
        client.setUsername("user");
        
        assertEquals(client.getUsername(), "user");
    }
    
    @Test
    // add users to multiple boards
    public void basicMultipleBoardsCheckAndAddUserTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "checkAndAddUser user board1 true";
        protocol.testHandleRequest(input);
        assertEquals(client.getUsername(), "user");
        assertEquals(client.getCurrentBoardName(), "board1");
        
        String input2 = "checkAndAddUser user2 board2 false";
        protocol.testHandleRequest(input2);
        assertEquals(client.getUsername(), "user");
        assertEquals(client.getCurrentBoardName(), "board1");
        
        String input3 = "checkAndAddUser user3 board3 true";
        protocol.testHandleRequest(input3);
        assertEquals(client.getUsername(), "user3");
        assertEquals(client.getCurrentBoardName(), "board3");
        
        String input4 = "checkAndAddUser user4 board2 false";
        protocol.testHandleRequest(input4);
        assertEquals(client.getUsername(), "user3");
        assertEquals(client.getCurrentBoardName(), "board3");
        
        
    }
    
    /******************** New Board ********************/
    @Test
    // add one new board true
    public void basicNewBoard() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "newBoard board true";
        protocol.testHandleRequest(input);
        
        assertTrue(client.getBoardSuccessful().containsKey("board"));
        assertTrue(client.getBoardSuccessful().get("board") == true);
    }
    
    @Test
    // add two true boards
    public void addTwoNewBoardTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "newBoard board true";
        protocol.testHandleRequest(input);
        assertTrue(client.getBoardSuccessful().containsKey("board"));
        assertTrue(client.getBoardSuccessful().get("board") == true);
        
        String input2 = "newBoard board2 true";
        protocol.testHandleRequest(input2);
        assertTrue(client.getBoardSuccessful().containsKey("board2"));
        assertTrue(client.getBoardSuccessful().get("board2") == true);
    }
    
    @Test
    // add two true boards
    public void addTwoFalseNewBoardTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "newBoard board false";
        protocol.testHandleRequest(input);
        assertTrue(client.getBoardSuccessful().containsKey("board"));
        assertTrue(client.getBoardSuccessful().get("board") == false);
        
        String input2 = "newBoard board2 false";
        protocol.testHandleRequest(input2);
        assertTrue(client.getBoardSuccessful().containsKey("board2"));
        assertTrue(client.getBoardSuccessful().get("board2") == false);
    }
    @Test
    // add one true one false
    public void addOneTrueOneFalseNewBoardTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        String input = "newBoard board false";
        protocol.testHandleRequest(input);
        assertTrue(client.getBoardSuccessful().containsKey("board"));
        assertTrue(client.getBoardSuccessful().get("board") == false);
        
        String input2 = "newBoard board2 true";
        protocol.testHandleRequest(input2);
        assertTrue(client.getBoardSuccessful().containsKey("board2"));
        assertTrue(client.getBoardSuccessful().get("board2") == true);
    }
    
    @Test
    // add multiple true boards
    public void addMultipleTrueNewBoardTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        for (int i=0; i<100; i++) {
            String input = "newBoard board" + i + " true";
            protocol.testHandleRequest(input);
            assertTrue(client.getBoardSuccessful().containsKey("board" + i));
            assertTrue(client.getBoardSuccessful().get("board" + i) == true);
        }
    }
    
    @Test
    // add multiple false boards
    public void addMultipleFalseNewBoardTest() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        for (int i=0; i<100; i++) {
            String input = "newBoard board" + i + " false";
            protocol.testHandleRequest(input);
            assertTrue(client.getBoardSuccessful().containsKey("board" + i));
            assertTrue(client.getBoardSuccessful().get("board" + i) == false);
        }
    }
    
    /****************************** Exit *******************************/
    
    @Test
    // exit program
    public void basicExit() throws Exception{
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Server server;
                try {
                    server = new Server(4444);
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        Client client = new Client("localhost", 4444);
        ClientReceiveProtocol protocol = client.getClientReceiveProtocol();
        
        assertFalse(client.getExitComplete());
        String input = "exit user";
        protocol.testHandleRequest(input);
        assertTrue(client.getExitComplete());
    }
}