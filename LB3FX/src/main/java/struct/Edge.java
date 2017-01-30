package struct;

import java.util.Vector;

/**
 * Created by Алексей on 22.01.2017.
 */
public class Edge {
    Point pointA;
    Point pointB;
    boolean wasVisible;

    Vector<Face> faces;

    public Edge() {
        Point pointA = new Point();
        Point pointB = new Point();
        faces = new Vector<>();
    }

    public Point getPointA() {
        return pointA;
    }

    public void setPointA(Point pointA) {
        this.pointA = pointA;
    }

    public Point getPointB() {
        return pointB;
    }

    public void setPointB(Point pointB) {
        this.pointB = pointB;
    }

    public boolean isWasVisible() {
        return wasVisible;
    }

    public void setWasVisible(boolean wasVisible) {
        this.wasVisible = wasVisible;
    }

    public Vector<Face> getFaces() {
        return faces;
    }

    public void setFaces(Vector<Face> faces) {
        this.faces = faces;
    }
}
