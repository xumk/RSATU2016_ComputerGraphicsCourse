package fx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * Created by Алексей on 11.01.2017.
 */
public class OutputWireframe extends Application {
   MeshView[] meshViews;

    public OutputWireframe() {
    }

    public void start(Stage primaryStage) throws Exception {
        // TODO: 11.01.2017 обработка видимых граней
        Arrays.stream(meshViews).forEach(it->{
            it.setDrawMode(DrawMode.LINE);
        });

        // Create a Camera to view the 3D Shapes
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(-200);
        camera.setTranslateY(-50);
        camera.setTranslateZ(-200);

        // Add the Shapes and the Light to the Group
        Group root = new Group(meshViews);

        // Create a Scene with depth buffer enabled
        Scene scene = new Scene(root, 400, 300, true);

        // Add the Camera to the Scene
        scene.setCamera(camera);
        // Add the Scene to the Stage
        primaryStage.setScene(scene);
        // Set the Title of the Stage
        primaryStage.setTitle("fx.OutputWireframe");
        // Display the Stage
        primaryStage.show();
    }
}
