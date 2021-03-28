package com.clustering.visualization;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.clustering.main.Cluster;

public class ClusterLink  {

	private Cluster currentCluster;
	private Point endPoint;
	private Point initialPoint;
    private List<ClusterLink> clusterChildren;
    
    public ClusterLink (Cluster currentCluster, Point initialPoint) {
		this.currentCluster = currentCluster;
		this.initialPoint = initialPoint;
		this.endPoint = initialPoint;
	}
    
    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public Point getInitialPoint() {
        return initialPoint;
    }

    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }
    
    public List<ClusterLink> getChildren() {
        if (clusterChildren == null) {
            clusterChildren = new ArrayList<ClusterLink>();
        }
        return clusterChildren;
    }
      
    public void setChildren(List<ClusterLink> children) {
        this.clusterChildren = children;
    }
    
	public Cluster getCurrentCluster() {
		return currentCluster;
	}

	public void setCurrentCluster(Cluster currentCluster) {
		this.currentCluster = currentCluster;
	}
	
	public int getLeafNameWidth(Graphics2D g) {
    	int width = 0;
        if (currentCluster.isLeaf()) {
            Rectangle2D rect =  g.getFontMetrics().getStringBounds(currentCluster.getName(), g);
            width = (int)rect.getWidth();
        }
        for (int i=0; i < getChildren().size(); i++) {
        	ClusterLink link = getChildren().get(i);
            int childWidth = link.getLeafNameWidth(g);
            width = Math.max(childWidth, width);
        }
        return width;
    }
	
    public double[] getRectangle() {
    	double[] rectangle = new double [4];
    	double initX = initialPoint.getX();
    	double joinX = endPoint.getX();
    	double initY = initialPoint.getY();
    	double joinY = endPoint.getY();
    	
    	rectangle[0] = getMinX(initX, joinX);
    	rectangle[1] = getMinY(initY, joinY);
    	rectangle[2] = getMaxX(initX, joinX);
    	rectangle[3] = getMaxY(initY, joinY);
    	
    	return rectangle;
    }
    
    private double getMinX(double initX, double joinX) {
        double temp = (initX < joinX ? initX : joinX);
        for (int i=0; i < getChildren().size(); i++) {
        	ClusterLink child = getChildren().get(i);
            temp = Math.min(temp, child.getMinX(child.getInitialPoint().getX(), child.getEndPoint().getX()));
        }
        return temp;
    }

    private double getMinY(double initY, double joinY) {
        double temp = (initY < joinY ? initY : joinY);
        for (int i=0; i < getChildren().size(); i++) {
        	ClusterLink child = getChildren().get(i);
            temp = Math.min(temp, child.getMinY(child.getInitialPoint().getY(), child.getEndPoint().getY()));
        }
        return temp;
    }
    
    private double getMaxX(double initX, double joinX) {
        double temp = (initX > joinX ? initX : joinX);
        for (int i=0; i < getChildren().size(); i++) {
        	ClusterLink child = getChildren().get(i);
            temp = Math.max(temp, child.getMaxX(child.getInitialPoint().getX(), child.getEndPoint().getX()));
        }
        return temp;
    }

    private double getMaxY(double initY, double joinY) {
        double temp = (initY > joinY ? initY : joinY);
        for (int i=0; i < getChildren().size(); i++) {
        	ClusterLink child = getChildren().get(i);
            temp = Math.max(temp, child.getMaxY(child.getInitialPoint().getY(), child.getEndPoint().getY()));
        }
        return temp;
    }
   
    public void paint(Graphics2D g, int xOff, int yOff, double xFac, double yFac) {
        FontMetrics metrics =  g.getFontMetrics();
        int x1 = (int)(initialPoint.getX() * xFac + xOff);
        int y1 = (int)(initialPoint.getY() * yFac + yOff);
        int x2 = (int)(endPoint.getX() * xFac + xOff);
        int y2 = y1;
        
        //draw the small circle
        if (!currentCluster.isRoot())
        	g.fillOval(x1 - 2, y1 - 2, 2 * 2, 2 * 2);
        
        //draw the horizontal line of the link
        g.drawLine(x1, y1, x2, y2); 

        //in case it is a leaf node, write the name on the right side
        if (currentCluster.isLeaf()) {
            g.drawString(currentCluster.getName(), x1 + 10, y1 + (metrics.getHeight()/2) - 2); 
        }
        
        //write the distance value on the joining point
        if (currentCluster.getDistance() > 0 && !currentCluster.isRoot()) {
            String formattedDistance = String.format("%.2f", currentCluster.getDistance());
            Rectangle2D rect = metrics.getStringBounds(formattedDistance, g);
            g.drawString(formattedDistance, x1 - (int)rect.getWidth() , y1 - 2);
        }
        
        //draw the vertical line
        x1 = x2;
        y1 = y2;
        y2 = (int)(endPoint.getY() * yFac + yOff);
        g.drawLine(x1, y1, x2, y2);
        
        //continue drawing the children of the current node
        for (int i=0; i<clusterChildren.size(); i++) {
        	ClusterLink child = clusterChildren.get(i);
            child.paint(g, xOff, yOff, xFac, yFac);
        }
    }
}
