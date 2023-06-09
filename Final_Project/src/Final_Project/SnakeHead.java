package Final_Project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
public class SnakeHead extends SnakePart {
    public SnakeHead(int x, int y, GraphicsContext graphics) {
        super(x, y, graphics);
    }

    @Override
    public SnakePart move(int dX, int dY) {
        x += dX;
        y += dY;
        graphics.drawImage(new Image(SnakeHead.class.getResourceAsStream("/pics/head.jpg")), x, y, len, len);
        return this;
    }

    @Override
    public SnakePart updateLocation(int pX, int pY) {
        x = pX;
        y = pY;
        graphics.drawImage(new Image(SnakeHead.class.getResourceAsStream("/pics/head.jpg")), x, y, len, len);
        return this;
    }
}
