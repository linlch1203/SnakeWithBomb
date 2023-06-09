package Final_Project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bomb {
    private final int x;
    private final int y;
    private final int size;
    private final GraphicsContext graphics;

    public Bomb(int x, int y, GraphicsContext graphics) {
        this.x = x;
        this.y = y;
        this.size = SnakePart.DEF_LEN;
        this.graphics = graphics;
    }

    public Bomb show() {
        graphics.drawImage(new Image(Bomb.class.getResourceAsStream("/pics/black.jpg")), x, y, size, size);
        return this;
    }

    public void vanish() {
        graphics.clearRect(x, y, size, size);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }
}
