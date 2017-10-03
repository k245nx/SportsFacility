package components.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

/**
 * Rappresentazione grafica di un campo spartivo
 * @author Roberto Tarullo, Pasquale Tarullo
 */
public class StadiumComponent extends JComponent {
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        
        // fattore di scala per lo stadio, default = 1
        // scale = 1;
        
        final int FIELD_WIDTH = (int)(250*scale);
        final int FIELD_HEIGHT = (int)(FIELD_WIDTH*0.74);

        // setta come colore il giallo
        g2.setColor(new Color(250, 235, 215));
        
        // crea rettangolo
        final int ROUNDRECTANGLE_WIDTH = (int)(FIELD_WIDTH*1.18);
        final int ROUNDRECTANGLE_HEIGHT = (int)(FIELD_HEIGHT*1.18);
        RoundRectangle2D.Double roundRectangle = new RoundRectangle2D.Double(getWidth()/2-ROUNDRECTANGLE_WIDTH/2, getHeight()/2-ROUNDRECTANGLE_HEIGHT/2, ROUNDRECTANGLE_WIDTH, ROUNDRECTANGLE_HEIGHT, 40, 40);
        g2.fill(roundRectangle);
        
        // setta come colore il verde
        g2.setColor(new Color(143, 188, 139));
        
        // Disegna il campo
        Rectangle field = new Rectangle(getWidth()/2-FIELD_WIDTH/2, getHeight()/2-FIELD_HEIGHT/2, FIELD_WIDTH, FIELD_HEIGHT);
        g2.fill(field);
        
        // setta come colore il bianco
        g2.setColor(new Color(255, 250, 250));
        
        // Disegna il cerchio al centro
        final int CENTERCIRCLE_LENGHT = (int)(FIELD_WIDTH*0.14);
        Ellipse2D.Double circle = new Ellipse2D.Double(getWidth()/2-CENTERCIRCLE_LENGHT/2, getHeight()/2-CENTERCIRCLE_LENGHT/2, CENTERCIRCLE_LENGHT, CENTERCIRCLE_LENGHT); 
        g2.draw(circle);
        
        // Crea la cornice del campo
        final int FIELDFRAME_WIDTH = (int)(FIELD_WIDTH*0.90);
        final int FIELDFRAME_HEIGHT = (int)(FIELD_HEIGHT*0.91);
        Rectangle fieldFrame = new Rectangle(getWidth()/2-FIELDFRAME_WIDTH/2, getHeight()/2-FIELDFRAME_HEIGHT/2, FIELDFRAME_WIDTH, FIELDFRAME_HEIGHT);
        g2.draw(fieldFrame);
        
        // Disegna la linea che separa la cornice del campo in due
        Point2D.Double r1 = new Point2D.Double(getWidth()/2, getHeight()/2-FIELDFRAME_HEIGHT/2);
        Point2D.Double r2 = new Point2D.Double(getWidth()/2, getHeight()/2+FIELDFRAME_HEIGHT/2);
        Line2D.Double centerLine = new Line2D.Double(r1, r2);
        g2.draw(centerLine);
        
        // Disegna il pallino al centro del campo
        final int CENTERPOINT_LENGHT = 6;
        Ellipse2D.Double centerPoint = new Ellipse2D.Double(getWidth()/2-CENTERPOINT_LENGHT/2, getHeight()/2-CENTERPOINT_LENGHT/2, CENTERPOINT_LENGHT, CENTERPOINT_LENGHT); 
        g2.fill(centerPoint);
        
        // Disegna il rettangolo dell'area sinistra
        final int AREA_WIDTH = (int)(FIELD_WIDTH*0.12);
        final int AREA_HEIGHT = (int)(FIELD_HEIGHT*0.40);    
        Rectangle leftArea = new Rectangle(getWidth()/2-FIELDFRAME_WIDTH/2, getHeight()/2-AREA_HEIGHT/2, AREA_WIDTH, AREA_HEIGHT);
        g2.draw(leftArea);
        // Disegna il rettangolo dell'area destra
        Rectangle rightArea = new Rectangle(getWidth()/2-FIELDFRAME_WIDTH/2 + FIELDFRAME_WIDTH - AREA_WIDTH, getHeight()/2-AREA_HEIGHT/2, AREA_WIDTH, AREA_HEIGHT);
        g2.draw(rightArea);
        
        // Disegna la porta sinistra
        final int GOAL_WIDTH = (int)(FIELD_WIDTH*0.04);
        final int GOAL_HEIGHT = (int)(FIELD_HEIGHT*0.18);
        Rectangle rightGoal = new Rectangle(getWidth()/2-FIELDFRAME_WIDTH/2, getHeight()/2-GOAL_HEIGHT/2, GOAL_WIDTH, GOAL_HEIGHT);
        g2.draw(rightGoal);
        
        // Disegna la porta destra
        Rectangle leftGoal = new Rectangle(getWidth()/2-FIELDFRAME_WIDTH/2 + FIELDFRAME_WIDTH - GOAL_WIDTH, getHeight()/2-GOAL_HEIGHT/2, GOAL_WIDTH, GOAL_HEIGHT);
        g2.draw(leftGoal);
        
        System.out.println(getPreferredSize());
    }
    
    private double scale = 1;
}
