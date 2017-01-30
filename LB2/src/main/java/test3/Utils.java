package test3;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Алексей on 02.01.2017.
 */
public final class Utils {

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

    public static void drawCircle(int x0, int y0, int radius, GraphicsContext gc) {
        //gc.strokeOval(x0, y0, radius, radius);
        initPoints(x0, y0, radius);
        Point2D p1 = points.get(0);
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);

        for (int j = 1; j <= points.size(); j++) {
            Point2D p2 = points.get(j % points.size());
            drawBresenhamLine(new LineDescription(p1, p2, Color.RED), gc.getPixelWriter());
            //gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            p1 = p2;
        }
        gc.getPixelWriter().setColor(x0, y0, Color.GREEN);
    }

    static final int INC = 1;
    private static java.util.List<Point2D> points = new ArrayList<>();

    private static void initPoints(double cx, double cy, double r) {
        for (int theta = 0; theta < 360; theta += INC) {
            int x = (int) (cx + r * Math.cos(Math.toRadians(theta)));
            int y = (int) (cy + r * Math.sin(Math.toRadians(theta)));
            points.add(new Point2D(x, y));
        }
    }

}
