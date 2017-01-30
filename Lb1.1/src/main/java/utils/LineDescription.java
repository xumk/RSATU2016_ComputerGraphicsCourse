package utils;

import javafx.scene.paint.Color;

/**
 * Created by Алексей on 02.01.2017.
 */
public class LineDescription {
    private int startPositionX;
    private int startPositionY;
    private int endPositionX;
    private int endPositionY;
    private Color color;

    public LineDescription() {

    }

    public LineDescription(int startPositionX, int startPositionY, int endPositionX, int endPositionY, Color color) {
        this.startPositionX = startPositionX;
        this.endPositionX = endPositionX;
        this.startPositionY = startPositionY;
        this.endPositionY = endPositionY;
        this.color = color;
    }

    public int getStartPositionX() {
        return startPositionX;
    }

    public LineDescription setStartPositionX(int startPositionX) {
        this.startPositionX = startPositionX;
        return this;
    }

    public int getStartPositionY() {
        return startPositionY;
    }

    public LineDescription setStartPositionY(int startPositionY) {
        this.startPositionY = startPositionY;
        return this;
    }

    public int getEndPositionX() {
        return endPositionX;
    }

    public LineDescription setEndPositionX(int endPositionX) {
        this.endPositionX = endPositionX;
        return this;
    }

    public int getEndPositionY() {
        return endPositionY;
    }

    public LineDescription setEndPositionY(int endPositionY) {
        this.endPositionY = endPositionY;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public LineDescription setColor(Color color) {
        this.color = color;
        return this;
    }
}
