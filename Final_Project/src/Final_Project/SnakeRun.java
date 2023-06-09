package Final_Project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.Optional;
import java.util.Random;

public class SnakeRun extends Application {
    private static final int multiple = 18;

    private static final int width = SnakePart.DEF_LEN * (multiple + 10);

    private static final int height = SnakePart.DEF_LEN * multiple;

    private double windowXOffset = 0;

    private double windowYOffset = 0;

    private final ArrayBlockingQueue<Food> foodQueue = new ArrayBlockingQueue<>(1);
    
    private BlockingQueue<Bomb> bombQueue = new LinkedBlockingQueue<>();

    private AnchorPane pane;
    
    private Pane start_pane;
    
    private Canvas canvas;
    
    private boolean stop;
    
    private boolean isCancelled = false;
    
    @Override
    public void start(Stage stage) throws Exception {
    	canvas = new Canvas(width, height);
        start_pane = new Pane();
        start_pane.setBackground(getBackground());
        pane = new AnchorPane();
        
        Button start1 = new Button("START");
        start1.setLayoutX(620);
        start1.setLayoutY(500);
        start1.setPrefSize(100,50);
        start_pane.getChildren().add(start1);
        Scene scene1 = new Scene(start_pane,1120,720);
        Scene scene2 = new Scene(pane,1120,720);
        start1.setOnAction((ActionEvent e)->{
        	stage.setScene(scene2);
        	Snake snake = new Snake(SnakePart.DEF_LEN * 6, SnakePart.DEF_LEN * 6, 5, 10, canvas, foodQueue, bombQueue);
            snake.setSnakeEvent(type -> alert(type, stage));
            pane.setOnKeyPressed(snake::turn);
            snake.init();
            genFood(snake, canvas);
            genBomb(snake, canvas);
            snake.run();
            snake.run2();
            });
        
        pane.getChildren().add(canvas);
        pane.setOnMousePressed(event -> {
            windowXOffset = event.getSceneX();
            windowYOffset = event.getSceneY();
        });
        pane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - windowXOffset);
            stage.setY(event.getScreenY() - windowYOffset);
        });
        
        stage.setScene(scene1);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
        canvas.requestFocus();
    }
    
    private Background getBackground() {
        BackgroundImage bImg = new BackgroundImage(
                new Image(this.getClass().getResourceAsStream("/pics/background.jpg")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(-1, -1, true, true, false, true));
        return new Background(bImg);
    }

    
    private void genFood(Snake snake, Canvas canvas) {
        new Thread(() -> {
            Random random = new Random();
            while (!stop) {
            	boolean check=true;
            	int x = random.nextInt(multiple - 1);
                int y = random.nextInt(multiple - 1);
            	while(check==true) {
                    check=false;
                    x = random.nextInt(multiple - 1);
                    y = random.nextInt(multiple - 1);
                    Bomb[] bombs=bombQueue.toArray(new Bomb[0]);
                    for(Bomb bomb:bombs) {
                    	if (x == bomb.getX() && y == bomb.getY()) {
                    		check=true;
                            break;
                        }
                    }
                    for (SnakePart snakePart : snake.getHeadAndBody()) {
                        if (snakePart.getX() == x && snakePart.getY() == y) {
                            check=true;
                            break;
                        }
                    }
            	}
                try {
                    Food food = new Food(x * SnakePart.DEF_LEN, y * SnakePart.DEF_LEN, canvas.getGraphicsContext2D());
                    foodQueue.put(food);
                    if (!stop) {
                        Platform.runLater(food::show);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    /*private void genFood(Snake snake, Canvas canvas) {
        new Thread(() -> {
            Random random = new Random();
            while (!stop) {
                boolean check = true;
                int x;
                int y;
                synchronized (snake) {
                    x = random.nextInt(multiple - 1);
                    y = random.nextInt(multiple - 1);
                    while (check) {
                        check = false;
                        x = random.nextInt(multiple - 1);
                        y = random.nextInt(multiple - 1);
                        Bomb[] bombs = bombQueue.toArray(new Bomb[0]);
                        for (Bomb bomb : bombs) {
                            if (x == bomb.getX() && y == bomb.getY()) {
                                check = true;
                                break;
                            }
                        }
                        for (SnakePart snakePart : snake.getHeadAndBody()) {
                            if (snakePart.getX() == x && snakePart.getY() == y) {
                                check = true;
                                break;
                            }
                        }
                    }
                }
                
                boolean isFoodOnSnake = false;
                synchronized (snake) {
                    for (SnakePart snakePart : snake.getHeadAndBody()) {
                        if (snakePart.getX() == x * SnakePart.DEF_LEN && snakePart.getY() == y * SnakePart.DEF_LEN) {
                            isFoodOnSnake = true;
                            break;
                        }
                    }
                }
                
                if (!isFoodOnSnake) {
                    try {
                        Food food = new Food(x * SnakePart.DEF_LEN, y * SnakePart.DEF_LEN, canvas.getGraphicsContext2D());
                        foodQueue.put(food);
                        if (!stop) {
                            Platform.runLater(food::show);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }*/


    private void genBomb(Snake snake, Canvas canvas) {
        Thread thread = new Thread(() -> {
            Random random = new Random();
            while (!isCancelled) {
                int x = random.nextInt(multiple - 1);
                int y = random.nextInt(multiple - 1);
                
                for (SnakePart snakePart : snake.getHeadAndBody()) {
                    if (snakePart.getX() == x && snakePart.getY() == y) {
                        x = random.nextInt(multiple - 1);
                        y = random.nextInt(multiple - 1);
                        break;
                    }
                }

                try {
                    Bomb bomb = new Bomb(x * SnakePart.DEF_LEN, y * SnakePart.DEF_LEN, canvas.getGraphicsContext2D());
                    bombQueue.put(bomb);

                    Platform.runLater(bomb::show);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void alert(Snake.EventType eventType, Stage stage) {
        if (eventType == Snake.EventType.DIE) {
        	isCancelled=true;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("You are dead");
            alert.setHeaderText("Restart or Close?");
            alert.setContentText("Score: " + eventType.getData().getScore() + "Point");
            ButtonType buttonTypeOne = new ButtonType("Restart");
            ButtonType buttonTypeTwo = new ButtonType("Close");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
            	isCancelled=false;
                eventType.getData().getSnake().clearAll();
                Snake snake = new Snake(SnakePart.DEF_LEN * 6, SnakePart.DEF_LEN * 6, 5, 10, canvas, foodQueue, bombQueue);
                snake.setSnakeEvent(type -> alert(type, stage));
                pane.setOnKeyPressed(snake::turn);
                snake.init();
                snake.run();
                snake.run2();
                genBomb(snake, canvas);
            } else if (result.get() == buttonTypeTwo) {
                stop = true;
                eventType.getData().getSnake().clearAll();
                stage.close();
            } else {
                alert.close();
            }
        }
    }
}
