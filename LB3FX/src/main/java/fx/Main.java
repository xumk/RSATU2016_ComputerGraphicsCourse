package fx;

import javafx.application.Application;
import javafx.scene.GroupBuilder;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCameraBuilder;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Depth Buffer");
        stage.setScene(makeScene());
        stage.show();
    }

    private Scene makeScene() {
        return SceneBuilder.create()
                .width(500)
                .height(500)
                .depthBuffer(true)
                .root(createRoot())
                .camera(PerspectiveCameraBuilder.create()

                        .build())
                .build();
    }

    private Parent createRoot() {
        Rectangle node1 = RectangleBuilder.create()
                .x(-150)
                .y(-150)
                .translateZ(-100)
                .width(200)
                .height(200)
                .fill(Color.RED)
                .build();

        Rectangle node2 = RectangleBuilder.create()
                .x(-100)
                .y(-100)
                .width(200)
                .height(200)
                .fill(Color.GREEN)
                .build();

        Rectangle node3 = RectangleBuilder.create()
                .x(-50)
                .y(-50)
                .translateZ(100)
                .width(200)
                .height(200)
                .fill(Color.BLUE)
                .build();

        return GroupBuilder.create()
                .children(node1, node2, node3)
                .translateX(250)
                .translateY(250)
                .build();
    }
}

