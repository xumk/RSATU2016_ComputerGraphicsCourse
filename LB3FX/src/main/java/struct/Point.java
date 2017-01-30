package struct;

import com.sun.javafx.geom.Vec3f;

public class Point {
    private Vec3f v;
    private Vec3f vt;
    private Vec3f vn;

    public Point() {
        v = new Vec3f();
        vn = new Vec3f();
        vt = new Vec3f();
    }

    public Vec3f getV() {
        return v;
    }

    public void setV(Vec3f v) {
        this.v = v;
    }

    public Vec3f getVt() {
        return vt;
    }

    public void setVt(Vec3f vt) {
        this.vt = vt;
    }

    public Vec3f getVn() {
        return vn;
    }

    public void setVn(Vec3f vn) {
        this.vn = vn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return point.v.equals(this.v);
    }
}
