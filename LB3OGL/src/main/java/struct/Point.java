package struct;

import org.joml.Vector3f;

public class Point {
    private Vector3f v;
    private Vector3f vt;
    private Vector3f vn;

    public Point() {
        v = new Vector3f();
        vn = new Vector3f();
        vt = new Vector3f();
    }

    public Vector3f getV() {
        return v;
    }

    public void setV(Vector3f v) {
        this.v = v;
    }

    public Vector3f getVt() {
        return vt;
    }

    public void setVt(Vector3f vt) {
        this.vt = vt;
    }

    public Vector3f getVn() {
        return vn;
    }

    public void setVn(Vector3f vn) {
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
