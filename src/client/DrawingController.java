package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/*
 * DrawingController handles the user's freehand drawing.
 */
public class DrawingController implements MouseListener, MouseMotionListener {
    // store the coordinates of the last mouse event, so we can
    // draw a line segment from that last point to the point of the next mouse event.
    private int lastX, lastY;
    private final Client client;

    public DrawingController(Client client) {
        this.client = client;
    }
    /*
     * When mouse button is pressed down, start drawing.
     */
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    /*
     * When mouse moves while a button is pressed down,
     * draw a line segment.
     */
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        Color color = client.getCurrentColor();
        if (client.isErasing()) { color = Color.white; }
        
        // to make up for the height of the menu
        client.getCanvas().drawLineSegmentAndCall(lastX, lastY, x, y, color.getRGB(), client.getCurrentWidth());
        lastX = x;
        lastY = y;
    }

    // Ignore all these other mouse events.
    public void mouseMoved(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    
    

} 