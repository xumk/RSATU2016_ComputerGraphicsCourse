import java.util.Date;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.Core;

/**
 * Created by Алексей on 02.01.2017.
 */
public class FxHelloCv extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FxHelloCvController.stage = primaryStage;
            // load the FXML resource
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FxHelloCv.fxml"));
            // store the root element so that the controllers can use it
            BorderPane rootElement = loader.load();
            // create and style a scene
            Scene scene = new Scene(rootElement, 800, 600);
            // create the stage with the given title and the previously created
            // scene
            primaryStage.setTitle("JavaFX meets OpenCV");
            primaryStage.setScene(scene);
            // show the GUI
            primaryStage.show();

            // set the proper behavior on closing the application
            FxHelloCvController controller = loader.getController();
            controller.setStageAndSetupListeners(scene);
            primaryStage.setOnCloseRequest((we -> controller.setClosed()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For launching the application...
     *
     * @param args optional params
     */
    public static void main(String[] args) throws Exception {
        // load the native OpenCV library
        String path = "JPanelOpenCV_" + new Date().getTime();
        OpenCVLoader.loadLib(path, Core.NATIVE_LIBRARY_NAME);

        launch(args);
    }
}
