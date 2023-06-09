package Final_Project;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Snake {
    private int initX;
    private int initY;
    private int initLen;
    public int speed;
    private KeyCode direction;
    public SnakeHead head;
    private List<SnakePart> body;
    private GraphicsContext graphics;
    private Canvas canvas;
    private ArrayBlockingQueue<Food> foodQueue;
    private BlockingQueue<Bomb> bombQueue;
    public Timer timer;
    private SnakeEvent snakeEvent;


    public Snake(int initX, int initY, int initLen, int speed, Canvas canvas, ArrayBlockingQueue<Food> foodQueue, BlockingQueue<Bomb> bombQueue) {
        this.initX = initX;
        this.initY = initY;
        this.initLen = initLen;
        this.speed = speed;
        this.canvas = canvas;
        this.foodQueue = foodQueue;
        this.bombQueue = bombQueue;
        this.graphics = canvas.getGraphicsContext2D();
    }

    public void init() {
        head = new SnakeHead(initX, initY, graphics);
        body = new ArrayList<>();
        for (int i = 0; i < initLen; i++) {
            body.add(new SnakePart(initX - SnakePart.DEF_LEN * (i + 1), initY, graphics).move(0, 0));
        }
        timer = new Timer();
        direction = KeyCode.D;
    }

    public void turn(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (code != KeyCode.W && code != KeyCode.S && code != KeyCode.A && code != KeyCode.D) {
            return;
        }

        if (code == KeyCode.W && direction == KeyCode.S) {
            return;
        }
        if (code == KeyCode.S && direction == KeyCode.W) {
            return;
        }
        if (code == KeyCode.A && direction == KeyCode.D) {
            return;
        }
        if (code == KeyCode.D && direction == KeyCode.A) {
            return;
        }
        direction = code;

    }

    public boolean eat() {
        Food food = foodQueue.peek();
        if (food == null) {
            return false;
        }
        if (head.getX() == food.getX() && head.getY() == food.getY()) {
            food = foodQueue.poll();
            if (food == null) {
                return false;
            }
            SnakePart tail = body.get(body.size() - 1);
            if (direction == KeyCode.W) {
                body.add(new SnakePart(tail.getX(), tail.getY() + SnakePart.DEF_LEN, graphics).move(0, 0));
            } else if (direction == KeyCode.S) {
                body.add(new SnakePart(tail.getX(), tail.getY() - SnakePart.DEF_LEN, graphics).move(0, 0));
            } else if (direction == KeyCode.A) {
                body.add(new SnakePart(tail.getX() + SnakePart.DEF_LEN, tail.getY(), graphics).move(0, 0));
            } else if (direction == KeyCode.D) {
                body.add(new SnakePart(tail.getX() - SnakePart.DEF_LEN, tail.getY(), graphics).move(0, 0));
            }
            food.vanish();
            return true;
        }
        return false;
    }
    
    public boolean eatBomb() {
    	Bomb Fbomb = bombQueue.peek();
        if (Fbomb == null) {
            return false;
        }
        Bomb[] bombs=bombQueue.toArray(new Bomb[0]);
        for(Bomb bomb:bombs) {
        	if (head.getX() == bomb.getX() && head.getY() == bomb.getY()) {
                bomb.vanish();
                return true;
            }
        }
        return false;
    }
    
    public void remove() {
        for (SnakePart snakePart : body) {
            snakePart.remove();
        }
    }

    public void move(int dX, int dY) {
        if (dX == 0 && dY == 0) {
            return;
        }
        remove();
        for (int i = body.size() - 1; i > 0; i--) {
            body.get(i).updateLocation(body.get(i - 1).getX(), body.get(i - 1).getY());
        }
        int headX = head.getX();
        int headY = head.getY();
        head.remove();
        body.get(0).updateLocation(headX, headY);
        head.move(dX, dY);

    }

    public void run() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                	eatBomb();
                    die();
                    eat();
                    switch (direction) {
                        case W: {
                            move(0, -SnakePart.DEF_LEN);
                            break;
                        }
                        case S: {
                            move(0, SnakePart.DEF_LEN);
                            break;
                        }
                        case A: {
                            move(-SnakePart.DEF_LEN, 0);
                            break;
                        }
                        case D: {
                            move(SnakePart.DEF_LEN, 0);
                            break;
                        }
					default:
						break;
                    }
                });
            }
        }, 0, 2000 / speed);
    }
    
    public void run2() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                	eatBomb();
                    die();
                    eat();
                });
            }
        }, 0, 1500 / speed);
    }
    
    public void die() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (head.getX() < 0 || head.getY() < 0 ||
                (head.getX() + SnakePart.DEF_LEN) > width ||
                (head.getY() + SnakePart.DEF_LEN) > height) {
            timer.cancel();
            EventType die = EventType.DIE;
            die.getData().setScore((body.size() - initLen) * 5);
            die.getData().setSnake(this);
            Platform.runLater(() -> snakeEvent.onEvent(die));
        }
        else if(eatBomb()==true) {
        	timer.cancel();
            EventType die = EventType.DIE;
            die.getData().setScore((body.size() - initLen) * 5);
            die.getData().setSnake(this);
            Platform.runLater(() -> snakeEvent.onEvent(die));
        }
        else {
        	for (int i = 0; i < body.size(); i++) {
        		if(head.getX() == body.get(i).getX() && head.getY() == body.get(i).getY()) {
            		timer.cancel();
                    EventType die = EventType.DIE;
                    die.getData().setScore((body.size() - initLen) * 5);
                    die.getData().setSnake(this);
                    Platform.runLater(() -> snakeEvent.onEvent(die));
        		}
        	}
        }
    }

    public void clearAll() {
        remove();
        head.remove();
        body.clear();
        Food food;
        Bomb bomb;
        if ((food = foodQueue.poll()) != null) {
            food.vanish();
        }
        while ((bomb = bombQueue.poll()) != null) {
            bomb.vanish();
        }
    }

    public List<SnakePart> getHeadAndBody() {
        ArrayList<SnakePart> snakeParts = new ArrayList<>();
        snakeParts.add(head);
        snakeParts.addAll(body);
        return snakeParts;
    }

    public void setSnakeEvent(SnakeEvent snakeEvent) {
        this.snakeEvent = snakeEvent;
    }

    public interface SnakeEvent {
       void onEvent(EventType type);
    }

    public enum EventType {
        DIE(new Data(0));
        private final Data data;

        EventType(Data data) {
            this.data = data;
        }

        public Data getData() {
            return data;
        }
    }
    public static class Data {
        private Snake snake;
        private int score;
        public Data() {
        }
        public Data(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public Snake getSnake() {
            return snake;
        }

        public void setSnake(Snake snake) {
            this.snake = snake;
        }
    }
}
