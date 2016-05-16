import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class CurveFit extends JPanel implements ActionListener {
    ButtonManager buttonManager;
    Point[] points;
    Path2D.Double path;
    Line2D.Double[] connectors;
    boolean showConnections = false;
    boolean removePoint = false;
    boolean firstTime = true;

    CurveFit() {
        int[][] cds = {
            { 100, 150 }, { 220, 300 }, { 260, 150 }
        };
        points = new Point[cds.length];
        for(int j = 0; j < cds.length; j++) {
            points[j] = new Point(cds[j][0], cds[j][1]);
        }
        path = new Path2D.Double();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if(ac.equals("add")) {
            connectors = new Line2D.Double[points.length-1];
            showConnections = true;
            repaint();
        }
        if(ac.equals("cancel")) {
            showConnections = false;
            removePoint = false;
            repaint();
        }
        if(ac.equals("remove")) {
            removePoint = true;
            repaint();
        }
    }

    public void addPoint(Point p, int index) {
        int size = points.length;
        Point[] temp = new Point[size+1];
        System.arraycopy(points, 0, temp, 0, index);
        temp[index] = p;
        System.arraycopy(points, index, temp, index+1, size-index);
        points = temp;
        buttonManager.reset();
        showConnections = false;
        setPath();
        repaint();
    }

    public void removePoint(Point p) {
        int size = points.length;
        Point[] temp = new Point[size-1];
        for(int j = 0, k = 0; j < size; j++) {
            if(points[j] == p)
                continue;
            temp[k++] = points[j];
        }
        points = temp;
        buttonManager.reset();
        removePoint = false;
        setPath();
        repaint();
    }

    public void setPoint(Point p, int x, int y) {
        p.setLocation(x, y);
        setPath();
        repaint();
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                            RenderingHints.VALUE_STROKE_PURE);
        if(firstTime) {
            firstTime = false;
            setPath();
        }
        g2.setPaint(Color.green.darker());
        g2.draw(path);
        g2.setPaint(Color.red);
        for(int j = 0; j < points.length; j++) {
            mark(g2, points[j]);
        }
        // For adding a point.
        if(showConnections) {
            g2.setPaint(Color.yellow);
            for(int j = 0; j < points.length-1; j++) {
                connectors[j] = new Line2D.Double(points[j], points[j+1]);
                g2.draw(connectors[j]);
            }
        }
    }


    private void setPath() {
        path.reset();
        int n = points.length;
        int w = getWidth();
        for(int j = 0; j <= w; j++) {
            double t = (double)j/w;          // [0 <= t <= 1.0]
            double x = 0;
            double y = 0;
            for(int k = 0; k < n; k++) {
                x += B(n-1,k,t)*points[k].x;
                y += B(n-1,k,t)*points[k].y;
            }
            if(j > 0)
                path.lineTo(x,y);
            else
                path.moveTo(x,y);
        }
    }

    private double B(int n, int m, double t) {
        return C(n,m) * Math.pow(t, m) * Math.pow(1.0 - t, n-m);
    }

    private double C(int n, int m) {
        return factorial(n) / (factorial(m)*factorial(n-m));
    }

    private int factorial(int n) {
        return (n > 1) ? n*factorial(n-1) : 1;
    }

    private void mark(Graphics2D g2, Point p) {
        g2.fill(new Ellipse2D.Double(p.x-2, p.y-2, 4, 4));
    }

    private JPanel getButtonPanel() {
        buttonManager = new ButtonManager();
        String[] ids = { "add", "cancel", "remove" };
        JPanel panel = new JPanel();   
        
        for(int j = 0; j < ids.length; j++) {
            JButton button = new JButton(ids[j]);
            button.setEnabled(j != 1);
            buttonManager.add(button);
            button.setActionCommand(ids[j]);
            button.addActionListener(this);
            panel.add(button);
        }
        return panel;
    }

    public static void main(String[] args) {
        CurveFit test = new CurveFit();
        PointMover mover = new PointMover(test);
        test.addMouseListener(mover);
        test.addMouseMotionListener(mover);
        JFrame f = new JFrame("Krzywa Beziera");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(test);
        f.add(test.getButtonPanel(), "Last");
        f.setSize(500,500);
        f.setLocation(200,200);
        f.setVisible(true);
    }
}