package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Main extends Application {
    // variable
    static int speed = 5;
    static int colorfruta = 0;
    static int ancho = 20;
    static int alto = 20;
    static int frutaX = 0;
    static int frutaY = 0;
    static int pixelsize = 25;  // Con pixelsize hago referencia a cada cuadrado como es la serpiente o el "tablero"
    static List<pixel> snake = new ArrayList<>();
    static Dir direccion = Dir.left;
    static boolean gameOver = false;
    static Random rand = new Random();
        
    public enum Dir {
	left, right, up, down
    }

    public static class pixel {
	int x;
	int y;
        
	public pixel(int x, int y) {
            this.x = x;
            this.y = y;
	}
    }

    @Override
    public void start(Stage primaryStage) {
	try {
            newFood();
            VBox root = new VBox();
            // Creación del tablero
            Canvas c = new Canvas(ancho * pixelsize, alto * pixelsize);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer() {
                long lastTick = 0;
                @Override
		public void handle(long now) {
                    if (lastTick == 0) {
			lastTick = now;
			tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / speed) {
			lastTick = now;
			tick(gc);
                    }
		}

            }.start();

            Scene scene = new Scene(root, ancho * pixelsize, alto * pixelsize);

            // control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
		if (key.getCode() == KeyCode.W) {
                    direccion = Dir.up;
                }
		if (key.getCode() == KeyCode.A) {
                    direccion = Dir.left;
		}
		if (key.getCode() == KeyCode.S) {
                    direccion = Dir.down;
		}
		if (key.getCode() == KeyCode.D) {
                    direccion = Dir.right;
		}
            });

            // tamaño de la serpiente al comenzar
            snake.add(new pixel(ancho / 2, alto / 2));
            snake.add(new pixel(ancho / 2, alto / 2));
            snake.add(new pixel(ancho / 2, alto / 2));
            primaryStage.setScene(scene);
            primaryStage.setTitle("SNAKE GAME");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
          }
    }

    public static void tick(GraphicsContext gc) {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 100, 250);
            return;
	}

	for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
	}

	switch (direccion) {
            case up:
            snake.get(0).y--;
            if (snake.get(0).y < 0) {
                gameOver = true;
            }
            break;
            case down:
            snake.get(0).y++;
            if (snake.get(0).y > alto) {
		gameOver = true;
            }
            break;
            case left:
            snake.get(0).x--;
            if (snake.get(0).x < 0) {
		gameOver = true;
            }
            break;
            case right:
            snake.get(0).x++;
            if (snake.get(0).x > ancho) {
            	gameOver = true;
            }
            break;
	}

	// crecimiento de la serpiente
	if (frutaX == snake.get(0).x && frutaY == snake.get(0).y) {
            snake.add(new pixel(-1, -1));
            newFood();
	}

	// comerse así misma
	for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
	}


	// background
	gc.setFill(Color.BLACK);
	gc.fillRect(0, 0, ancho * pixelsize, alto * pixelsize);

	// puntuación 
	gc.setFill(Color.WHITE);
	gc.setFont(new Font("", 30));
	gc.fillText("Score: " + (speed - 6), 10, 30);

	// fruta aleatoria  // pc: pixel color ()
	Color pc = Color.WHITE;

	switch (colorfruta) {
            case 0: pc = Color.PURPLE;
            break;
            case 1: pc = Color.LIGHTBLUE;
            break;
            case 2: pc = Color.YELLOW;
            break;
            case 3: pc = Color.PINK;
            break;
            case 4: pc = Color.ORANGE;
            break;
	}
	gc.setFill(pc);
        gc.fillOval(frutaX * pixelsize, frutaY * pixelsize, pixelsize, pixelsize);

		// snake
	for (pixel c : snake) {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(c.x * pixelsize, c.y * pixelsize, pixelsize - 1, pixelsize - 1);
            gc.setFill(Color.GREEN);
            gc.fillRect(c.x * pixelsize, c.y * pixelsize, pixelsize - 2, pixelsize - 2);
	}
    }

	// fruta
    public static void newFood() {
        start: while (true) {
            frutaX = rand.nextInt(ancho);
            frutaY = rand.nextInt(alto);

            for (pixel c : snake) {
                if (c.x == frutaX && c.y == frutaY) {
		continue start;
            	}
            }
            colorfruta = rand.nextInt(5);
            speed++;
            break;
        }
    }

    public static void main(String[] args) {
	launch(args);
    }
}