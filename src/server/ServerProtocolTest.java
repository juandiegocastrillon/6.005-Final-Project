package server;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import Command.Command;
import client.Client;

/**
 * Testing Strategy:
 *  New Board:
 *      make new board
 *      make multiple boards
 *      make error new board
 *      make error multiple boards
 *      make multiple correct and error boards
 *  Switch Board:
 *      basic switch board
 *      switch board with few Commands
 *      switch board with 1,000 Commands
 *  Check Boards:
 *      Check no boards
 *      Check one board
 *      Check one hundred boards
 *  Get Users:
 *      No Users
 *      One user
 *      Multiple Users
 *  Check and Add user:
 *      Add one user
 *      Add multiple users
 *      Add same user
 *      Add same user multiple times
 *      Add same user different boards
 *      Add different users to multiple boards
 *  Exit:
 *      Basic Exit
 *      Multiple Users Exit
 *      Multiple Users, multiple Boards Exit
 *  Draw:
 *      Basic Draw
 *      Multiple Draw Commands
 *  Invalid Input
 */
public class ServerProtocolTest {
    String newLine = System.getProperty("line.separator");

    /**************** New Board *************************/
    // new basic board
    @Test
    public void makeNewBoardBasicTest() throws IllegalArgumentException,
            IOException, InterruptedException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        String input = "newBoard board";
        String output = protocol.testHandleRequest(input);
        assertEquals("newBoard board true", output);

        server.shutDown();
    }

    // multiple new boards
    @Test
    public void makeMultipleNewBoardBasicTest()
            throws IllegalArgumentException, IOException, InterruptedException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        for (int i = 0; i < 1000; i++) {
            String input = "newBoard board" + i;
            String output = protocol.testHandleRequest(input);
            assertEquals("newBoard board" + i + " true", output);
        }

        server.shutDown();
    }

    // error on two boards with same name
    @Test
    public void makeErrorNewBoardBasicTest() throws IllegalArgumentException,
            IOException, InterruptedException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        String input = "newBoard board";
        String output = protocol.testHandleRequest(input);
        assertEquals("newBoard board true", output);

        String input2 = "newBoard board";
        String output2 = protocol.testHandleRequest(input2);
        assertEquals("newBoard board false", output2);

        server.shutDown();
    }

    // error on multiple boards with same name
    @Test
    public void makeErrorMultipleNewBoardBasicTest()
            throws IllegalArgumentException, IOException, InterruptedException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        String input = "newBoard board";
        String output = protocol.testHandleRequest(input);
        assertEquals("newBoard board true", output);

        for (int i = 0; i < 1000; i++) {
            String output2 = protocol.testHandleRequest(input);
            assertEquals("newBoard board false", output2);
        }

        server.shutDown();
    }

    // various correct and incorrect boards
    @Test
    public void MultipleErrorAndCorrectNewBoardBasicTest()
            throws IllegalArgumentException, IOException, InterruptedException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        String input = "newBoard board";
        String output = protocol.testHandleRequest(input);
        assertEquals("newBoard board true", output);

        String input2 = "newBoard board2";
        String output2 = protocol.testHandleRequest(input2);
        assertEquals("newBoard board2 true", output2);

        String input3 = "newBoard board";
        String output3 = protocol.testHandleRequest(input3);
        assertEquals("newBoard board false", output3);

        String input4 = "newBoard board3";
        String output4 = protocol.testHandleRequest(input4);
        assertEquals("newBoard board3 true", output4);

        String input5 = "newBoard board2";
        String output5 = protocol.testHandleRequest(input5);
        assertEquals("newBoard board2 false", output5);

        server.shutDown();
    }

    /*********************** Switch Board ******************/

    @Test
    // switch board basic test
    public void basicSwitchBoardTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        server.newBoard("board1");
        server.newBoard("board2");

        String input = "switch user board1 board2";
        String output = protocol.testHandleRequest(input);

        assertEquals("switch user board1 board2" + newLine, output);

        server.shutDown();
    }

    @Test
    // switch board with few commands
    public void fewCommandsSwitchBoardTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        // add two boards
        server.newBoard("board1");
        server.newBoard("board2");

        // add 4 commands to board2
        String[] elements = { "draw", "board2", "drawLine" };
        Command command = new Command(elements);
        for (int i = 0; i < 4; i++) {
            server.updateBoard("board2", command);
        }

        String input = "switch user board1 board2";
        String output = protocol.testHandleRequest(input);
        String check = "switch user board1 board2" + newLine;
        for (int i = 0; i < 4; i++) {
            check += "draw board2 drawLine" + newLine;
        }
        assertEquals(check, output);

        server.shutDown();
    }

    @Test
    // switch board with one thousand commands
    public void oneThousandCommandsSwitchBoardTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        // add two boards
        server.newBoard("board1");
        server.newBoard("board2");

        // add 1000 commands to board2
        String[] elements = { "draw", "board2", "drawLine" };
        Command command = new Command(elements);
        for (int i = 0; i < 1000; i++) {
            server.updateBoard("board2", command);
        }

        String input = "switch user board1 board2";
        String output = protocol.testHandleRequest(input);
        String check = "switch user board1 board2" + newLine;
        for (int i = 0; i < 1000; i++) {
            check += "draw board2 drawLine" + newLine;
        }
        assertEquals(check, output);

        server.shutDown();
    }

    /********************** Check boards *******************/
    @Test
    // no boards on server
    public void noBoardsCheckBoardsTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        String input = "boards";
        String output = protocol.testHandleRequest(input);

        assertEquals("boards ", output);

        server.shutDown();
    }

    @Test
    // add two boards
    public void basicCheckBoardsTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        // add boards
        server.newBoard("board1");
        server.newBoard("board2");

        String input = "boards";
        String output = protocol.testHandleRequest(input);

        assertEquals("boards board2 board1", output);

        server.shutDown();
    }

    @Test
    // add 1000 boards
    public void oneHundredCheckBoardsTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        // add boards
        for (int i = 0; i < 1000; i++) {
            server.newBoard("board" + i);
        }

        String input = "boards";
        String output = protocol.testHandleRequest(input);
        assertTrue(output.indexOf("boards") >= 0);
        for (int i = 999; i >= 0; i--) {
            assertTrue(output.indexOf("board" + i) >= 0);
        }

        server.shutDown();
    }

    /********************* Get Users **********************/

    @Test
    // no users
    public void noUsersGetUsersTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        server.newBoard("board");
        String input = "users board";
        String output = protocol.testHandleRequest(input);

        assertEquals(output, "users board ");

        server.shutDown();
    }

    @Test
    // two users on one board
    public void twoUsersOneBoardGetUsersTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        // add users to board(s)
        server.newBoard("board");
        server.checkUser("user", "board");
        server.checkUser("user2", "board");

        String input = "users board";
        String output = protocol.testHandleRequest(input);

        assertEquals(output, "users board user user2");

        server.shutDown();
    }

    @Test
    // users on multiple boards
    public void usersMultipleBoardsGetUsersTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        // add users to board(s)
        server.newBoard("board1");
        server.newBoard("board2");
        server.newBoard("board3");
        server.checkUser("user1", "board1");
        server.checkUser("user2", "board1");
        server.checkUser("user3", "board1");
        server.checkUser("user4", "board2");

        String input1 = "users board1";
        String output1 = protocol.testHandleRequest(input1);
        assertEquals(output1, "users board1 user1 user2 user3");

        String input2 = "users board2";
        String output2 = protocol.testHandleRequest(input2);
        assertEquals(output2, "users board2 user4");

        String input3 = "users board3";
        String output3 = protocol.testHandleRequest(input3);
        assertEquals(output3, "users board3 ");

        server.shutDown();
    }

    /*********************** Check and Add User ************/
    @Test
    // add one user
    public void addOneUserCheckAndAddUserTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        server.newBoard("board");
        String input = "checkAndAddUser user board";
        String output = protocol.testHandleRequest(input);

        assertEquals(output, "checkAndAddUser user board true");

        server.shutDown();
    }

    @Test
    // add multiple different users
    public void addMultipleDifferentUsersCheckAndAddUserTest()
            throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);
        server.newBoard("board");

        // add 100 different users
        for (int i = 0; i < 100; i++) {
            String input = "checkAndAddUser user" + i + " board";
            String output = protocol.testHandleRequest(input);
            assertEquals(output, "checkAndAddUser user" + i + " board true");
        }

        server.shutDown();
    }

    @Test
    // add two of the same user
    public void addSameUserCheckAndAddUserTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);
        server.newBoard("board");

        String input = "checkAndAddUser user board";
        String output = protocol.testHandleRequest(input);

        assertEquals(output, "checkAndAddUser user board true");

        String output2 = protocol.testHandleRequest(input);
        assertEquals(output2, "checkAndAddUser user board false");

        server.shutDown();
    }

    @Test
    // add the same user multiple times
    public void addSameUserMultipleTimesCheckAndAddUserTest()
            throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);
        server.newBoard("board");

        String input = "checkAndAddUser user board";
        String output = protocol.testHandleRequest(input);
        assertEquals(output, "checkAndAddUser user board true");

        // add 100 different users
        for (int i = 0; i < 100; i++) {
            String input2 = "checkAndAddUser user board";
            String output2 = protocol.testHandleRequest(input2);
            assertEquals(output2, "checkAndAddUser user board false");
        }

        server.shutDown();
    }

    @Test
    // add same user to different board
    public void addSameUserDifferentBoardCheckAndAddUserTest()
            throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        server.newBoard("board");
        String input = "checkAndAddUser user board";
        String output = protocol.testHandleRequest(input);
        assertEquals(output, "checkAndAddUser user board true");

        server.newBoard("board2");
        String input2 = "checkAndAddUser user board2";
        String output2 = protocol.testHandleRequest(input2);
        assertEquals(output2, "checkAndAddUser user board2 false");

        server.newBoard("board3");
        String input3 = "checkAndAddUser user board3";
        String output3 = protocol.testHandleRequest(input3);
        assertEquals(output3, "checkAndAddUser user board3 false");

        server.shutDown();
    }

    @Test
    // add same and different users in different orders
    public void addSameAndDifferentUsersCheckAndAddUserTest()
            throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        server.newBoard("board");
        String input = "checkAndAddUser user board";
        String output = protocol.testHandleRequest(input);
        assertEquals(output, "checkAndAddUser user board true");

        String input2 = "checkAndAddUser user board";
        String output2 = protocol.testHandleRequest(input2);
        assertEquals(output2, "checkAndAddUser user board false");

        server.newBoard("board2");
        String input3 = "checkAndAddUser user2 board2";
        String output3 = protocol.testHandleRequest(input3);
        assertEquals(output3, "checkAndAddUser user2 board2 true");

        String input4 = "checkAndAddUser user board2";
        String output4 = protocol.testHandleRequest(input4);
        assertEquals(output4, "checkAndAddUser user board2 false");

        server.newBoard("board3");
        String input5 = "checkAndAddUser user3 board";
        String output5 = protocol.testHandleRequest(input5);
        assertEquals(output5, "checkAndAddUser user3 board true");

        server.shutDown();
    }

    /********************** Exit ***************************/
    @Test
    // exit connection on one user
    public void basicExitTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        server.newBoard("board");
        server.checkUser("user", "board");

        String input = "exit user";
        String output = protocol.testHandleRequest(input);

        assertEquals(output, "exit user");

        server.shutDown();
    }

    @Test
    // exit connection on multiple users
    public void multipleUsersExitTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        server.newBoard("board");
        server.checkUser("user1", "board");
        server.checkUser("user2", "board");
        server.checkUser("user3", "board");

        for (int i = 1; i < 4; i++) {
            String input = "exit user" + i;
            String output = protocol.testHandleRequest(input);
            assertEquals(output, "exit user" + i);
        }
        server.shutDown();
    }

    @Test
    // exit connection on multiple users on different boards
    public void multipleUsersMultipleBoardsExitTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        server.newBoard("board");
        server.newBoard("board2");
        server.newBoard("board3");
        server.checkUser("user1", "board");
        server.checkUser("user2", "board2");
        server.checkUser("user3", "board2");

        for (int i = 1; i < 4; i++) {
            String input = "exit user" + i;
            String output = protocol.testHandleRequest(input);
            assertEquals(output, "exit user" + i);
        }

        server.shutDown();
    }

    /*********************** Draw **************************/
    @Test
    // draw basic
    public void basicDrawTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);
        server.newBoard("board");

        String input = "draw board drawLineSegment 1 2 3 4 Color.BLACK 4";
        String output = protocol.testHandleRequest(input);
        assertEquals(output, "draw");

        server.shutDown();
    }

    @Test
    // draw multiple times on different boards
    public void multipleDrawTest() throws IOException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);
        server.newBoard("board1");
        server.newBoard("board2");
        server.newBoard("board3");

        String input1 = "draw board3 drawLineSegment 1 2 3 4 Color.BLACK 4";
        String input2 = "draw board2 drawLineSegment 1 2 3 4 Color.BLACK 4";
        String input3 = "draw board1 drawLineSegment 1 2 3 4 Color.BLACK 4";
        String input4 = "draw board2 drawLineSegment 1 2 3 4 Color.BLACK 4";

        String output1 = protocol.testHandleRequest(input1);
        String output2 = protocol.testHandleRequest(input2);
        String output3 = protocol.testHandleRequest(input3);
        String output4 = protocol.testHandleRequest(input4);

        assertEquals(output1, "draw");
        assertEquals(output2, "draw");
        assertEquals(output3, "draw");
        assertEquals(output4, "draw");

        server.shutDown();
    }

    /*********************** Invalid Input *****************/

    // invalid input new board
    @Test
    public void invalidInputNewBoardBasicTest()
            throws IllegalArgumentException, IOException, InterruptedException {
        Server server = new Server(4444);
        ServerProtocol protocol = new ServerProtocol(null, server);

        String input = "newnewBoard bo#fd";
        String output = protocol.testHandleRequest(input);
        assertEquals(null, output);

        server.shutDown();
    }

}
