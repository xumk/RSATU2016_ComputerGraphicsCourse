package struct;

import com.sun.javafx.geom.Vec3f;

/**
 * Created by Алексей on 22.01.2017.
 */
public class Face {
    Point[] points = new Point[3];
    Vec3f normal;

    public Face() {
        normal = new Vec3f();
    }

    public Point[] getPoints() {
        return points;
    }

    public void setPoints(Point[] points) {
        this.points = points;
    }

    public Vec3f getNormal() {
        return normal;
    }

    public void setNormal(Vec3f normal) {
        this.normal = normal;
    }

    void calcNormal(){
        Vec3f[] vp = { points[0].getV(), points[1].getV(), points[2].getV() };
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
                equals = face.getPoints()[j] == p;
            }
        }
        return equals;
    }

    Vec3f getNormal(Vec3f[] p){
        Vec3f b = new Vec3f(p[2].x -p[0].x, p[2].y -p[0].y, p[2].z -p[0].z);
        Vec3f a = new  Vec3f(p[1].x -p[0].x, p[1].y -p[0].y, p[1].z -p[0].z);
        Vec3f normal = new Vec3f(a.y*b.z -a.z*b.y, a.z*b.x -a.x*b.z, a.x*b.y -a.y*b.x);
        normal.normalize();
        return normal;
    }


}
