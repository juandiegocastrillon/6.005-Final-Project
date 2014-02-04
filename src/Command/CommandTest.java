package Command;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import client.Client;

public class CommandTest {
    
    /*
     * Testing strategy:
     * 
     * -Constructor
     * -toString
     * -checkBoardName
     * -compare a canvas with a command invoked on it and a canvas just drawn on
     */
    
    String noArguments = "draw board1 drawNothing";
    Command noArgumentsObject = new Command(noArguments);
    String lineSegment = "draw board2 drawLineSegment 50 50 60 60 0 10";
    Command lineSegmentObject = new Command(lineSegment);
    
    @Test
    public void testConstructor() {
        Command noArgumentsCorrect = new Command("drawNothing", "board1", new String[0]);
        assertTrue(noArgumentsObject.equals(noArgumentsCorrect));
        Command lineSegmentCorrect = new Command("drawLineSegment", "board2", new String[]{"50", "50", "60", "60", "0", "10"});
        assertTrue(lineSegmentObject.equals(lineSegmentCorrect));
    }
    
    @Test
    public void testToString() {
        assertTrue(noArgumentsObject.toString().equals(noArguments));
        assertTrue(lineSegmentObject.toString().equals(lineSegment));
    }
    
    @Test
    public void checkBoardNameTest() {
        assertTrue(!lineSegmentObject.checkBoardName("board1"));
        assertTrue(lineSegmentObject.checkBoardName("board2"));
    }
    
    @Test
    public void invokeCommandTest() {
        try {
            Client clientInvoked = new Client("localhost", 4444);
            clientInvoked.getClientGUI().setupCanvas();
            lineSegmentObject.invokeCommand(clientInvoked.getCanvas());
            Client clientDrawn = new Client("localhost", 4444);
            clientDrawn.getClientGUI().setupCanvas();
            clientDrawn.getCanvas().drawLineSegment(50, 50, 60, 60, 0, 10);
            BufferedImage imageInvoked = clientInvoked.getDrawingBuffer();
            BufferedImage imageDrawn = clientDrawn.getDrawingBuffer();
            boolean same = true;
            for (int x = 0; x < imageInvoked.getWidth(); x++) {
                for (int y = 0; y < imageInvoked.getHeight(); y++) {
                    if (imageInvoked.getRGB(x, y) != imageDrawn.getRGB(x, y) ) same = false;
                 }
            }
            assertTrue(same);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
}
