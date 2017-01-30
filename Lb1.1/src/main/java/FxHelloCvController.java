
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import utils.LineDescription;
import utils.Utils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static utils.Utils.getBresenhamLine;

/**
 * Created by Алексей on 02.01.2017.
 */
public class FxHelloCvController implements Initializable {

    private static List<LineDescription> lineDescriptions;
    public static Stage stage;
    private int currentLine = 0;
    LineChart<Number, Number> lineChart;
    LineDescription previousLine;
    boolean isC = false;

    static {
        lineDescriptions = Arrays.asList(
                new LineDescription(0, 240, 639, 240, Color.GREEN),
                new LineDescription(320, 0, 320, 479, Color.RED),
                new LineDescription(0, 0, 639, 479, Color.RED),
                new LineDescription(639, 0, 0, 479, Color.RED)
        );
    }

    public Canvas canvas;
    // the FXML button
    @FXML
    private Button button;
    // the FXML image view
    @FXML
    private ImageView currentFrame;

    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that realizes the video capture
    private VideoCapture capture = new VideoCapture();
    // a flag to change the button behavior
    private boolean cameraActive = false;
    private boolean isCreateLineWindow = false;
    // the id of the camera to be used
    private static int cameraId = 0;
    private int coutCamera = 0;

    /**
     * The action triggered by pushing the button on the GUI
     *
     * @param event the push button event
     */
    @FXML
    protected void startCamera(ActionEvent event) {
        if (!isCreateLineWindow) {
            Stage secondStage = new Stage();
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Number of Month");
            //creating the chart
            lineChart = new LineChart<>(xAxis, yAxis);
            secondStage.setScene(new Scene(new HBox(4, lineChart)));
            secondStage.show();
        }
        if (!this.cameraActive) {
            // start the video capture
            this.capture.open(cameraId);

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = () -> {
                    // effectively grab and process a single frame
                    Mat frame = grabFrame();
                    // convert and show the frame
                    Image imageToShow = Utils.mat2Image(frame);
                    updateImageView(currentFrame, imageToShow);
                    List<Color> line = getBresenhamLine(lineDescriptions.get(currentLine), imageToShow.getPixelReader());
                    ++coutCamera;
                    if (coutCamera > 15) {
                        Platform.runLater(() -> {
                            createGraphic(line);
                        });
                        coutCamera = 0;
                    }
                    if (isC) {
                        Platform.runLater(() -> {
                            redrawLine();
                        });
                        isC = false;
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 100, TimeUnit.MILLISECONDS);

                // update the button content
                this.button.setText("Stop Camera");
            } else {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.button.setText("Start Camera");

            // stop the timer
            this.stopAcquisition();
        }
    }

    private void redrawLine() {
        PixelWriter pixelWrite = canvas.getGraphicsContext2D().getPixelWriter();
        Utils.drawBresenhamLine(previousLine, pixelWrite);
        Utils.drawBresenhamLine(lineDescriptions.get(currentLine).setColor(Color.GREEN), pixelWrite);
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Mat} to show
     */
    private Mat grabFrame() {
        // init everything
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame
                this.capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                }

            } catch (Exception e) {
                // log the error
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *
     * @param view the {@link ImageView} to update
     * @param image the {@link Image} to show
     */
    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    /**
     * On application close, stop the acquisition from the camera
     */
    protected void setClosed() {
        this.stopAcquisition();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PixelWriter pixelWrite = canvas.getGraphicsContext2D().getPixelWriter();
        lineDescriptions.stream().forEach(item -> Utils.drawBresenhamLine(item, pixelWrite));
    }

    public void setStageAndSetupListeners(Scene scene) {
        scene.setOnKeyPressed(ke -> {
            if (ke.getCode().getName().equals("F12") && (coutCamera > 5 && coutCamera < 15)) {
                previousLine = lineDescriptions.get(currentLine).setColor(Color.RED);
                ++currentLine;
                if (currentLine > 3) {
                    currentLine = 0;
                }
                isC = true;
            }
        });
    }

    private void createGraphic(List<Color> pixs) {
        lineChart.getData().clear();
        LineChart.Series<Number, Number> series1 = new LineChart.Series<>();
        series1.setName("Максимальный вес");
        double i = 0;
        for (Color pix : pixs) {
            series1.getData().add(
                    new XYChart.Data<>(i, pix.getRed())
            );
            ++i;
        }
        lineChart.getData().add(series1);
    }
}
