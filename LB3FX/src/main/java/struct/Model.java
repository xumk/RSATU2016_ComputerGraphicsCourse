package struct;

import com.sun.javafx.geom.Vec3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import static java.lang.Float.parseFloat;

/**
 * Created by Алексей on 22.01.2017.
 */
public class Model {
    //vector<Point> points;
    Vector<Face> faces;
    Vector<Edge> edges;

    boolean drawContour;
    boolean lit;
    Vec3f translation;
    Vec3f rotation;
    Vec3f scale;

    Vec3f color;

    public Model() {
        drawContour = false;
        lit = false;
        translation = new Vec3f(0, 0, 0);
        rotation = new Vec3f(0, 0, 0);
        scale = new Vec3f(1, 1, 1);
    }

    public Model(String fileName) throws IOException {
       this();
        loadModelFromFile(fileName, this);
    }

    void addEdges(Face face) {
        Point[] points = face.getPoints();
        addEdge(points[0],points[1], face);
        addEdge(points[1], points[2], face);
        addEdge(points[2], points[0], face);
    }

    void addEdge(Point pointA, Point pointB, Face face) {
        boolean edgeExists = false;
        for (int i = 0; i < edges.size() && !edgeExists; i++) {
            if (edges.get(i).pointA == pointA && edges.get(i).pointB == pointB
                    || edges.get(i).pointA == pointB && edges.get(i).pointB == pointA) {
                edges.get(i).faces.addElement(face);
                edgeExists = true;
            }
        }

        if (!edgeExists) {
            Edge edge = new Edge();
            edge.pointA = pointA;
            edge.pointB = pointB;
            edge.wasVisible = true;
            edge.faces.addElement(face);

            edges.addElement(edge);
        }
    }

    static void loadModelFromFile(String fileName, Model model) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader (fileName));;
        loadModelFromStream(br, model);
        br.close();
    }

    static void loadModelFromStream(BufferedReader input, Model model) throws IOException {
        Vector<Vec3f> vertices = new Vector<>();
        Vector<Vec3f> textureCoords = new Vector<>();
        Vector<Vec3f> normals = new Vector<>();

        String str;
        while ((str = input.readLine()) != null) {
            String[] arrayCharStr = str.split(" ");
            if (arrayCharStr[0].equals("v")) {
                vertices.addElement(loadVec3FromStream(Arrays.copyOfRange(arrayCharStr, 1, arrayCharStr.length)));
                continue;
            }

            if (arrayCharStr[0].equals("vt")) {
                textureCoords.addElement(loadVec3FromStream(Arrays.copyOfRange(arrayCharStr, 1, arrayCharStr.length)));
                continue;
            }

            if (arrayCharStr[0].equals("vn")) {
                normals.addElement(loadVec3FromStream(Arrays.copyOfRange(arrayCharStr, 1, arrayCharStr.length)));
                continue;
            }

            if (arrayCharStr[0].equals("f")) {
                Face face = new Face();
                for (int i = 0; i < 3; i++) {
                    Point p = new Point();
                    Vector<Integer> pointInfo;

                    str = arrayCharStr[i + 1];

                    pointInfo = parsePointInfo(str.toCharArray());
                    if (pointInfo.get(0) > 0) {
                        p.setV(vertices.get(pointInfo.get(0) - 1));
                    }
                    if (pointInfo.get(1) > 0) {
                        p.setVt(textureCoords.get(pointInfo.get(1) - 1));
                    }
                    if (pointInfo.get(2) > 0) {
                        p.setVn(normals.get(pointInfo.get(2) - 1));
                    }

                    face.points[i] = p;
                }
                face.calcNormal();
                model.faces.addElement(face);
                model.addEdges(face);
                continue;
            }
        }
    }

    static Vec3f loadVec3FromStream(String[] input) {
        return new Vec3f(parseFloat(input[0]), parseFloat(input[1]), parseFloat(input[2]));
    }

    static Vector<Integer> parsePointInfo(char[] pointInfo){
        Vector<Integer> result = new Vector<>();
        StringBuilder dataNum = new StringBuilder();
        for (char aPointInfo : pointInfo) {
            if (aPointInfo != '/')
                dataNum.append(aPointInfo);
            else {
                result.add(strToInt(dataNum.toString()));
                dataNum.setLength(0);
            }
        }
        result.addElement(strToInt(dataNum.toString()));

        while(result.size() < 3){
            result.addElement(0);
        }

        return result;
    }

    static int strToInt(String str){
        int result = 0;
        char[] strChar = str.toCharArray();
        for (char cNum : strChar) {
            result *= 10;
            result += (cNum - '0');
        }

        return result;
    }

    public Vector<Face> getFaces() {
        return faces;
    }

    public void setFaces(Vector<Face> faces) {
        this.faces = faces;
    }

    public Vector<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Vector<Edge> edges) {
        this.edges = edges;
    }

    public boolean isDrawContour() {
        return drawContour;
    }

    public void setDrawContour(boolean drawContour) {
        this.drawContour = drawContour;
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    public Vec3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vec3f translation) {
        this.translation = translation;
    }

    public Vec3f getRotation() {
        return rotation;
    }

    public void setRotation(Vec3f rotation) {
        this.rotation = rotation;
    }

    public Vec3f getScale() {
        return scale;
    }

    public void setScale(Vec3f scale) {
        this.scale = scale;
    }

    public Vec3f getColor() {
        return color;
    }

    public void setColor(Vec3f color) {
        this.color = color;
    }
}
