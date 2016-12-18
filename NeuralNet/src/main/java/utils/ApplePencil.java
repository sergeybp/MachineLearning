package utils;

import java.awt.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Created by sergeybp on 18.12.16.
 */
public class ApplePencil extends JPanel implements MouseListener, MouseMotionListener {


    protected int lastX, lastY;
    Graphics2D g;
    Color painting;
    int strokeWidth;
    static Double[][] data = new Double[28][28];


    public ApplePencil() {
        this.setStroke(10);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setColor(Color.WHITE);
        setBackground(Color.BLACK);
        for(int i = 0; i < 28;i++){
            for(int j = 0 ; j < 28; j++){
                data[i][j] = 0d;
            }
        }
    }

    public static Double[][] getValue(){
        return data;
    }

    public void mousePressed(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();


    }

    public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            data[x/10][y/10] = 1d;
            g = (Graphics2D) this.getGraphics();
            g.setStroke(new BasicStroke((float) strokeWidth));
            g.setColor(painting);
            g.drawLine(lastX, lastY, x, y);
            lastX = x;
            lastY = y;
    }

    public void setColor(Color clr) {
        painting = clr;

    }


    public void setStroke(int width) {
        strokeWidth = width;

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }


}
