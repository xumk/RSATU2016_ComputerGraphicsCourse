package fx;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;

/**
 * Created by Алексей on 05.01.2017.
 */
public class OutputModel extends Application {
    MeshView[] one;
    MeshView[] second;
    private double rotateX = 0;
    private double rotateY = 0;

    double cameraDistance = 450;
    double cameraX = 100;
    double cameraY = -300;

    private MeshView[] get3dModel() {
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = this.getClass().getResource("/teapot.obj");
            objImporter.read(modelUrl);
        } catch (ImportException e) {
            // handle exception
        }
        return objImporter.getImport();
    }

    public void start(Stage primaryStage) throws Exception {
        one = get3dModel();
        Arrays.stream(one).forEach(it -> {
            it.setTranslateX(450);
            it.setTranslateY(-30);
            it.setTranslateZ(450);
            it.setScaleX(200.0);
            it.setScaleY(200.0);
            it.setScaleZ(200.0);
            it.setCullFace(CullFace.BACK);
        });
        second = get3dModel();
        Arrays.stream(second).forEach(it -> {
            it.setTranslateX(450);
            it.setTranslateY(-30);
            it.setTranslateZ(450);
            it.setScaleX(200.0);
            it.setScaleY(200.0);
            it.setScaleZ(200.0);
            it.setDrawMode(DrawMode.LINE);
            it.setCullFace(CullFace.FRONT);

        });


        Stage secondStage = new Stage();
        // Add the Shapes and the Light to the Group
        Group subRootTwo = new Group(second);
        subRootTwo.setDepthTest(DepthTest.ENABLE);

        Group rootTwo = new Group(subRootTwo);
        // Create a Scene with depth buffer enabled
        Scene sceneSecond = new Scene(rootTwo, 400, 300, true, SceneAntialiasing.DISABLED);
        // Create a Camera to view the 3D Shapes
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(cameraX);
        camera.setTranslateY(cameraY);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(cameraDistance);

        PerspectiveCamera cameraTwo = new PerspectiveCamera(false);
        cameraTwo.setTranslateX(cameraX);
        cameraTwo.setTranslateY(cameraY);
        cameraTwo.setNearClip(0.1);
        cameraTwo.setFarClip(10000.0);
        cameraTwo.setTranslateZ(cameraDistance);
        // Add the Camera to the Scene
        sceneSecond.setCamera(cameraTwo);
        sceneSecond.setFill(Color.BLACK);
        // Add the Scene to the Stage
        secondStage.setScene(sceneSecond);
        // Set the Title of the Stage
        secondStage.setTitle("fx.OutputWireframe");
        // Display the Stage
        secondStage.show();

        // Add the Shapes and the Light to the Group

        Group subRoot = new Group(one);
        subRoot.setDepthTest(DepthTest.ENABLE);

        Group root = new Group(subRoot);
        // Create a Scene with depth buffer enabled
        Scene scene = new Scene(root, 400, 300, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);

       // camera.getTransforms().addAll(rotationX, rotationY);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.UP)) {
                rotateY += 5;
                root.setRotationAxis(Rotate.Y_AXIS);
                root.setRotate(rotateY);
                rootTwo.setRotationAxis(Rotate.Y_AXIS);
                rootTwo.setRotate(rotateY);
            }
            if (event.getCode().equals(KeyCode.DOWN)) {
                rotateY -= 5;
                root.setRotationAxis(Rotate.Y_AXIS);
                root.setRotate(rotateY);
                rootTwo.setRotationAxis(Rotate.Y_AXIS);
                rootTwo.setRotate(rotateY);
            }
            if (event.getCode().equals(KeyCode.LEFT)) {
                rotateX -= 5;
                subRoot.setRotationAxis(Rotate.X_AXIS);
                subRoot.setRotate(rotateX);
                subRootTwo.setRotationAxis(Rotate.X_AXIS);
                subRootTwo.setRotate(rotateX);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                rotateX += 5;
                subRoot.setRotationAxis(Rotate.X_AXIS);
                subRoot.setRotate(rotateX);
                subRootTwo.setRotationAxis(Rotate.X_AXIS);
                subRootTwo.setRotate(rotateX);
            }
            if (event.getCode().equals(KeyCode.EQUALS)) {
                cameraDistance += 10;
                camera.setTranslateZ(cameraDistance);
                cameraTwo.setTranslateZ(cameraDistance);
            }
            if (event.getCode().equals(KeyCode.MINUS)) {
                cameraDistance -= 10;
                camera.setTranslateZ(cameraDistance);
                cameraTwo.setTranslateZ(cameraDistance);
            }
            if (event.getCode().equals(KeyCode.W)) {
                cameraY += 5;
                camera.setTranslateY(cameraY);
                cameraTwo.setTranslateY(cameraY);
            }
            if (event.getCode().equals(KeyCode.S)) {
                cameraY -= 5;
                camera.setTranslateY(cameraY);
                cameraTwo.setTranslateY(cameraY);
            }
            if (event.getCode().equals(KeyCode.A)) {
                cameraX -= 5;
                camera.setTranslateX(cameraX);
                cameraTwo.setTranslateX(cameraX);
            }
            if (event.getCode().equals(KeyCode.D)) {
                cameraX += 5;
                camera.setTranslateX(cameraX);
                cameraTwo.setTranslateX(cameraX);
            }
            sendMeshViews(cameraTwo);
        });

        // Add the Camera to the Scene
        scene.setCamera(camera);
        // Add the Scene to the Stage
        primaryStage.setScene(scene);
        // Set the Title of the Stage
        primaryStage.setTitle("fx.OutputModel");
        // Display the Stage
        primaryStage.show();
    }

    private void sendMeshViews(PerspectiveCamera camera) {
        Point3D oz = new Point3D(camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ()).normalize();
        Arrays.stream(second).parallel().forEach(it -> {
           // it.getMesh()
        });
        // TODO: 11.01.2017 написать обработку, чтобы передавать лишь видимые грани
    }

    public static void main(String[] args) {
        launch(args);
    }
}
