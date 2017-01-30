package test3;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

/**
 * Created by Алексей on 27.01.2017.
 */
public class Draw extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Canvas canvas = new Canvas(640, 480);
        Utils.drawCircle(300, 200, 150, canvas.getGraphicsContext2D());
        Group group = new Group();
        group.getChildren().addAll(canvas);
        Scene scene = new Scene(group, 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
