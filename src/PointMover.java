
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.swing.event.MouseInputAdapter;

class PointMover extends MouseInputAdapter {
    CurveFit component;
    Point selectedPoint;
    Cursor cursor;
    Cursor defaultCursor = Cursor.getDefaultCursor();
    Point offset = new Point();
    boolean dragging = false;
    final int PROX_DIST = 5;

    PointMover(CurveFit cf) {
        component = cf;
        BufferedImage image = getImage();
        Point hotspot = new Point(17,17);
        cursor = Toolkit.getDefaultToolkit()
                        .createCustomCursor(image, hotspot, null);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(selectedPoint != null) {
            if(component.removePoint) {                // remove
                component.removePoint(selectedPoint);
            } else {                                   // drag
                offset.x = e.getX() - selectedPoint.x;
                offset.y = e.getY() - selectedPoint.y;
                dragging = true;
            }
        } else if(component.showConnections) {         // add
            Point p = e.getPoint();
            Line2D.Double[] lines = component.connectors;
            for(int j = 0; j < lines.length; j++) {
                if(lines[j].ptSegDist(p) < PROX_DIST) {
                    component.addPoint(p, j+1);
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(dragging) {
            int x = e.getX() - offset.x;
            int y = e.getY() - offset.y;
            component.setPoint(selectedPoint, x, y);
        }
    }

    /** For point selection. */
    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        Point[] pts = component.points;
        boolean hovering = false;
        for (Point pt : pts) {
            if (pt.distance(p) < PROX_DIST) {
                hovering = true;
                if (selectedPoint != pt) {
                    selectedPoint = pt;
                    component.setCursor(cursor);
                    break;
                }
            }
        }

        if(!hovering && selectedPoint != null) {
            selectedPoint = null;
            component.setCursor(defaultCursor);
        }
    }

    private BufferedImage getImage() {
        int w = 27, h = 27,
            type = BufferedImage.TYPE_INT_ARGB_PRE;
        BufferedImage image = new BufferedImage(w, h, type);
        java.awt.Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new Color(0x333333));
        g2.draw(new Line2D.Double(w/2, 0, w/2, 8));    // n
        g2.draw(new Line2D.Double(0, h/2, 8, h/2));    // w
        g2.draw(new Line2D.Double(w/2, h-8, w/2, h));  // s
        g2.draw(new Line2D.Double(w-8, h/2, w, h/2));  // e
        g2.dispose();
        return image;
    }
}