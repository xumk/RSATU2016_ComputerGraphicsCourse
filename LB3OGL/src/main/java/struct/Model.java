package struct;

import org.joml.Vector3f;
import utils.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static java.lang.Float.parseFloat;

/**
 * Created by Алексей on 22.01.2017.
 */
public class Model {
    //vector<Point> points;
    private Vector<Face> faces;
    private Vector<Edge> edges;

    private boolean drawContour;
    private boolean lit;
    private Vector3f translation;
    private Vector3f rotation;
    private Vector3f scale;

    private Vector3f color;

    public Model() {
        color = new Vector3f(1, 1, 1);
        faces = new Vector<>();
        edges = new Vector<>();
        drawContour = false;
        lit = false;
        translation = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
    }

    public Model(String fileName) throws Exception {
        this();
        loadModelFromFile(fileName, this);
    }

    void addEdges(Face face) {
        Point[] points = face.getPoints();
        addEdge(points[0], points[1], face);
        addEdge(points[1], points[2], face);
        addEdge(points[2], points[0], face);
    }

    void addEdge(Point pointA, Point pointB, Face face) {
        boolean edgeExists = false;
        for (int i = 0; i < edges.size() && !edgeExists; i++) {
            if (edges.get(i).pointA.equals(pointA) && edges.get(i).pointB.equals(pointB)
                    || edges.get(i).pointA.equals(pointB) && edges.get(i).pointB.equals(pointA)) {
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

    static void loadModelFromFile(String fileName, Model model) throws Exception {
        List<String> lines = Utils.readAllLines(fileName);
        loadModelFromStream(lines, model);
    }

    static void loadModelFromStream(List<String> input, Model model) throws IOException {
        Vector<Vector3f> vertices = new Vector<>();
        Vector<Vector3f> textureCoords = new Vector<>();
        Vector<Vector3f> normals = new Vector<>();

        input.stream().forEach( str -> {
            String[] arrayCharStr = str.split(" ");
            String[] subArray = Arrays.copyOfRange(arrayCharStr, 1, arrayCharStr.length);
            if (arrayCharStr[0].equals("v")) {
                vertices.addElement(loadVec3FromStream(subArray));
            }

            if (arrayCharStr[0].equals("vt")) {
                textureCoords.addElement(loadVec3FromStream(subArray));
            }

            if (arrayCharStr[0].equals("vn")) {
                normals.addElement(loadVec3FromStream(subArray));
            }

            if (arrayCharStr[0].equals("f")) {
                Face face = new Face();
                for (int i = 0; i < 3; i++) {
                    Point p = new Point();
                    Vector<Integer> pointInfo;

                    pointInfo = parsePointInfo(subArray[i].toCharArray());
                    if (pointInfo.get(0) > 0) {
                        p.setV(vertices.get(pointInfo.get(0) - 1));
                    }
                    if (pointInfo.get(1) > 0 && textureCoords.size() > pointInfo.get(1) - 1) {
                        p.setVt(textureCoords.get(pointInfo.get(1) - 1));
                    }
                    if (pointInfo.get(2) > 0 && textureCoords.size() > pointInfo.get(2) - 1) {
                        p.setVn(normals.get(pointInfo.get(2) - 1));
                    }

                    face.points[i] = p;
                }
                face.calcNormal();
                model.faces.addElement(face);
                model.addEdges(face);
            }
        });
    }

    static Vector3f loadVec3FromStream(String[] input) {
        return new Vector3f(parseFloat(input[0]), parseFloat(input[1]), parseFloat(input[2]));
    }

    static Vector<Integer> parsePointInfo(char[] pointInfo) {
        Vector<Integer> result = new Vector<>();
        StringBuilder dataNum = new StringBuilder();
        for (char aPointInfo : pointInfo) {
            if (aPointInfo != '/')
                dataNum.append(aPointInfo);
            else {
                if (!dataNum.toString().isEmpty()) {
                    result.add(Integer.valueOf(dataNum.toString()));
                }
                dataNum.setLength(0);
            }
        }
        result.addElement(Integer.valueOf(dataNum.toString()));

        while (result.size() < 3) {
            result.addElement(0);
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

    public Vector3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
}
