package app;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import javafx.stage.Stage;
import processing.core.PApplet;
import processing.core.PVector;

public class Run extends Application{
    static float width = 600;
    static float height = 600;

    public void start(Stage stage) throws Exception{
        try{
            Seeker seeker = new Seeker(width/2,height/2);
            Target[] target = new Target[20];
            for (int i = 0; i < target.length; i++)
            {
                target[i] = new Target();
            }
            Group root = new Group(seeker,seeker.awarenessCircle);
            for (int i = 0; i < target.length; i++)
            {
                root.getChildren().add(target[i]);
                root.getChildren().add(target[i].label);
            }
            Scene scene = new Scene(root, width,height, Color.BLACK);
            stage.setScene(scene);
            stage.toFront();
            stage.show();

            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    seeker.show();
                    seeker.update();
                    for (int i = 0; i < target.length; i++) {
                        target[i].show();
                        target[i].update(seeker);
                    }

                }
            }.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
