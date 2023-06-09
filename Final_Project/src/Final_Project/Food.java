package Final_Project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Food {
    private final int x;
    private final int y;
    private final int size;
    private final GraphicsContext graphics;


    public Food(int x, int y, GraphicsContext graphics) {
        this.x = x;
        this.y = y;
        this.size = SnakePart.DEF_LEN;
        this.graphics = graphics;
    }

    public Food show() {
        graphics.drawImage(new Image(Food.class.getResourceAsStream("/pics/leaf.jpg")), x, y, size, size);

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
