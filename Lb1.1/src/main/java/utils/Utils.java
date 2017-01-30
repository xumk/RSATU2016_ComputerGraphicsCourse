package utils;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 02.01.2017.
 */
public final class Utils {
    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    public static Image mat2Image(Mat frame) {
        try {
            return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
        } catch (Exception e) {
            System.err.println("Cannot convert the Mat obejct: " + e);
            return null;
        }
    }

    /**
     * Generic method for putting element running on a non-JavaFX thread on the
     * JavaFX thread, to properly update the UI
     *
     * @param property a {@link ObjectProperty}
     * @param value    the value to set for the given {@link ObjectProperty}
     */
    public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> property.set(value));
    }

    /**
     * @param original the {@link Mat} object in BGR or grayscale
     * @return the corresponding {@link BufferedImage}
     */
    private static BufferedImage matToBufferedImage(Mat original) {
        // init
        BufferedImage image = null;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

    // Этот код "рисует" все 9 видов отрезков. Наклонные (из начала в конец и из конца в начало каждый), вертикальный и горизонтальный - тоже из начала в конец и из конца в начало, и точку.
    private static int sign(int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
    }

    /**
     * startPositionX, startPositionY - начало;
     * endPositionX, endPositionY - конец;
     * "pixelWriter.drawLine (x, y, x, y);" используем в качестве "setPixel (x, y);"
     * Можно писать что-нибудь вроде pixelWriter.fillRect (x, y, 1, 1);
     */
    public static void drawBresenhamLine(LineDescription lineDescription, PixelWriter pixelWriter) {
        int x, y, dx, dy, incX, incY, pdx, pdy, es, el, err;

        dx = lineDescription.getEndPositionX() - lineDescription.getStartPositionX();//проекция на ось икс
        dy = lineDescription.getEndPositionY() - lineDescription.getStartPositionY();//проекция на ось игрек

        incX = sign(dx);
    /*
     * Определяем, в какую сторону нужно будет сдвигаться. Если dx < 0, т.е. отрезок идёт
	 * справа налево по иксу, то incX будет равен -1.
	 * Это будет использоваться в цикле постороения.
	 */
        incY = sign(dy);
    /*
     * Аналогично. Если рисуем отрезок снизу вверх -
	 * это будет отрицательный сдвиг для y (иначе - положительный).
	 */

        // if (dx < 0) dx = -dx;//далее мы будем сравнивать: "if (dx < dy)"
        //  if (dy < 0) dy = -dy;//поэтому необходимо сделать dx = |dx|; dy = |dy|
        dx = Math.abs(dx);
        dy = Math.abs(dy);

        //определяем наклон отрезка:
        if (dx > dy) {
     /*
      * Если dx > dy, то значит отрезок "вытянут" вдоль оси икс, т.е. он скорее длинный, чем высокий.
	  * Значит в цикле нужно будет идти по икс (строчка el = dx;), значит "протягивать" прямую по иксу
	  * надо в соответствии с тем, слева направо и справа налево она идёт (pdx = incX;), при этом
	  * по y сдвиг такой отсутствует.
	  */
            pdx = incX;
            pdy = 0;
            es = dy;
            el = dx;
        } else {
            //случай, когда прямая скорее "высокая", чем длинная, т.е. вытянута по оси y
            pdx = 0;
            pdy = incY;
            es = dx;
            el = dy;//тогда в цикле будем двигаться по y
        }

        x = lineDescription.getStartPositionX();
        y = lineDescription.getStartPositionY();
        err = el / 2;
        pixelWriter.setColor(x, y, lineDescription.getColor());//ставим первую точку
        //все последующие точки возможно надо сдвигать, поэтому первую ставим вне цикла

        //идём по всем точкам, начиная со второй и до последней
        for (int t = 0; t < el; t++) {
            err -= es;
            if (err < 0) {
                err += el;
                x += incX;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
                y += incY;//или сместить влево-вправо, если цикл проходит по y
            } else {
                x += pdx;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
                y += pdy;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
            }

            pixelWriter.setColor(x, y, lineDescription.getColor());
        }
    }

    /**
     * startPositionX, startPositionY - начало;
     * endPositionX, endPositionY - конец;
     * "pixelReader.drawLine (x, y, x, y);" используем в качестве "setPixel (x, y);"
     * Можно писать что-нибудь вроде pixelReader.fillRect (x, y, 1, 1);
     */
    public static List<Color> getBresenhamLine(LineDescription lineDescription, PixelReader pixelReader) {
        List<Color> result = new ArrayList<>();
        int x, y, dx, dy, incX, incY, pdx, pdy, es, el, err;

        dx = lineDescription.getEndPositionX() - lineDescription.getStartPositionX();//проекция на ось икс
        dy = lineDescription.getEndPositionY() - lineDescription.getStartPositionY();//проекция на ось игрек

        incX = sign(dx);
    /*
     * Определяем, в какую сторону нужно будет сдвигаться. Если dx < 0, т.е. отрезок идёт
	 * справа налево по иксу, то incX будет равен -1.
	 * Это будет использоваться в цикле постороения.
	 */
        incY = sign(dy);
    /*
     * Аналогично. Если рисуем отрезок снизу вверх -
	 * это будет отрицательный сдвиг для y (иначе - положительный).
	 */

        // if (dx < 0) dx = -dx;//далее мы будем сравнивать: "if (dx < dy)"
        //  if (dy < 0) dy = -dy;//поэтому необходимо сделать dx = |dx|; dy = |dy|
        dx = Math.abs(dx);
        dy = Math.abs(dy);

        //определяем наклон отрезка:
        if (dx > dy) {
     /*
      * Если dx > dy, то значит отрезок "вытянут" вдоль оси икс, т.е. он скорее длинный, чем высокий.
	  * Значит в цикле нужно будет идти по икс (строчка el = dx;), значит "протягивать" прямую по иксу
	  * надо в соответствии с тем, слева направо и справа налево она идёт (pdx = incX;), при этом
	  * по y сдвиг такой отсутствует.
	  */
            pdx = incX;
            pdy = 0;
            es = dy;
            el = dx / 2;
        } else {
            //случай, когда прямая скорее "высокая", чем длинная, т.е. вытянута по оси y
            pdx = 0;
            pdy = incY;
            es = dx;
            el = dy / 2;//тогда в цикле будем двигаться по y
        }

        x = lineDescription.getStartPositionX();
        y = lineDescription.getStartPositionY();
        err = el / 2;
        result.add(pixelReader.getColor(x, y));//ставим первую точку
        //все последующие точки возможно надо сдвигать, поэтому первую ставим вне цикла

        //идём по всем точкам, начиная со второй и до последней
        for (int t = 0; t < el; t++) {
            err -= es;
            if (err < 0) {
                err += el;
                x += incX;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
                y += incY;//или сместить влево-вправо, если цикл проходит по y
            } else {
                x += pdx;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
                y += pdy;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
            }

            result.add(pixelReader.getColor(x, y));
        }

        return result;
    }
}
