package struct;

import org.joml.Vector3f;

import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

/**
 * Created by Алексей on 22.01.2017.
 */
public class Face {
    Point[] points = new Point[3];
    Vector3f normal;

    public Face() {
        normal = new Vector3f();
    }

    public Point[] getPoints() {
        return points;
    }

    public void setPoints(Point[] points) {
        this.points = points;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    void calcNormal(){
        Vector3f[] vp = { points[0].getV(), points[1].getV(), points[2].getV() };
        normal = getNormal(vp);
    }

    public boolean equals(Object f) {
        if (this == f) return true;
        if (f == null || getClass() != f.getClass()) return false;

        Face face = (Face) f;
        boolean equals = true;
        for (int i = 0; i < 3 && equals; i++) {
            Point p = points[i];
            equals = false;
            for (int j = 0; j < 3 && !equals; j++) {
                equals = face.getPoints()[j].equals(p);
            }
        }
        return equals;
    }

    Vector3f getNormal(Vector3f[] p){
        Vector3f b = new Vector3f(p[2].x -p[0].x, p[2].y -p[0].y, p[2].z -p[0].z);
        Vector3f a = new  Vector3f(p[1].x -p[0].x, p[1].y -p[0].y, p[1].z -p[0].z);
        Vector3f normal = new Vector3f(a.y*b.z -a.z*b.y, a.z*b.x -a.x*b.z, a.x*b.y -a.y*b.x);
        return Normalize(normal);
    }


    Vector3f Normalize(Vector3f v){
       v.mul((float) (1.0 / getDistance(new Vector3f(), v)));
        return v;
    }

    double getDistance(Vector3f p1, Vector3f p2){
        return sqrt(pow(p1.x -p2.x, 2.0) +pow(p1.y -p2.y, 2.0) +pow(p1.z -p2.z, 2.0));
    }

}
