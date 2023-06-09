package Final_Project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SnakePart {
    public static final int DEF_LEN = 40;
    protected int x;
    protected int y;
    protected final int len;
    protected final GraphicsContext graphics;

    public SnakePart(int x, int y, GraphicsContext graphics) {
        this.x = x;
        this.y = y;
        this.len = DEF_LEN;
        this.graphics = graphics;
    }

    public void remove() {
        graphics.clearRect(x, y, len, len);
    }

    public SnakePart move(int dX, int dY) {
        x += dX;
        y += dY;
        graphics.drawImage(new Image(SnakePart.class.getResourceAsStream("/pics/body.jpg")), x, y, len, len);
        return this;
    }
    public SnakePart updateLocation(int pX, int pY) {
        x = pX;
        y = pY;
        graphics.drawImage(new Image(SnakePart.class.getResourceAsStream("/pics/body.jpg")), x, y, len, len);
        return this;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLen() {
        return len;
    }

    @Override
    public String toString() {
        return "SnakePart{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
