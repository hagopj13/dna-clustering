package com.clustering.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import com.clustering.main.Cluster;

public class Dendrogram extends JPanel {

    private Cluster root;
    private ClusterLink clusterLink;

    private double rootX;
    private double rootY;
    private double width;
    private double height;
    
    private Color lineColor = Color.BLACK;

    public void setRoot(Cluster root) {
        this.root = root;
        clusterLink = createLink(root);
        setDimensions();
    }
    
    private ClusterLink createLink(Cluster root) {
        Point initialPoint = new Point(0, 0.5);
        ClusterLink link = createLink(root, initialPoint, 1);
        link.setEndPoint(initialPoint);
        return link;
    }
    
    private ClusterLink createLink(Cluster cluster, Point initialPoint, double height) {
        ClusterLink link = null;
        if (cluster != null) {
            link = new ClusterLink(cluster, initialPoint);
            double leafHeight = height / cluster.countLeafs();
            double childY = initialPoint.getY() - (height / 2);
            double dist = cluster.getDistance();
            for (int i=0; i<cluster.getChildren().size(); i++) {
            	Cluster child = cluster.getChildren().get(i);
                int childLeafCnt = child.countLeafs();
                double childDist = child.getDistance();
                double childHeight = childLeafCnt * leafHeight;
                Point childInitialPoint = new Point(initialPoint.getX() + (dist - childDist), childY + childHeight
                        / 2.0);
                childY += childHeight;
                ClusterLink childLink = createLink(child, childInitialPoint, childHeight);
                childLink.setEndPoint(initialPoint);
                link.getChildren().add(childLink);
            }
        }
        return link;
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g_2D = (Graphics2D) g;
        g_2D.setColor(lineColor);
        
        int borderL = 20, borderR = 20, borderU = 20, borderD = 20;
        int widthDisp = getWidth() - borderL - borderR;
        int heightDisp = getHeight() - borderU - borderD;
        int xDisp = borderL;
        int yDisp = borderD;

        if (clusterLink != null) {
            int leafNameWidth = clusterLink.getLeafNameWidth(g_2D) + 10;
            widthDisp = getWidth() - borderL - borderR - leafNameWidth;
            double xFac = widthDisp / width;
            double yFac = heightDisp / height;
            int xOff = (int) (xDisp - rootX * xFac);
            int yOff = (int) (yDisp - rootY * yFac);
            clusterLink.paint(g_2D, xOff, yOff, xFac, yFac); 
        } 
    }
    
    private void setDimensions() {
    	double [] rectangle = clusterLink.getRectangle();
        rootX = rectangle[0];
        rootY = rectangle[1];
        width = rectangle[2] - rectangle[0];
        height = rectangle[3] - rectangle[1];
    }
}