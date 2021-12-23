package app;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

public class Target extends Circle {
    static int NumberOfTargets = 0;
    int targetID;
    static ArrayList<Target> targetList = new ArrayList<>();
    PVector position = new PVector();
    PVector velocity = PVector.random2D().mult(2f);
    Label label = new Label();

    double targetRadius = 20;
    Color color = Color.WHITE;

    Target(){

        this.position.x = (float) new Random().nextInt((int) Run.width - 100);
        this.position.y = (float) new Random().nextInt((int) Run.height - 100);
        this.targetID = NumberOfTargets;
        NumberOfTargets++;
        targetList.add(this);
        this.label.setText(""+this.targetID);
        this.label.setTextFill(color);

    }

    void show(){
        setCenterX(this.position.x);
        setCenterY(this.position.y);
        setRadius(targetRadius);
        setFill(Color.TRANSPARENT);
        setStroke(color);
    }
    private void edge()
    {
        int offset = 2;

        if(this.position.x > Run.width + this.targetRadius * offset)
        {
            this.position.x = (float) (0 - this.targetRadius * offset);
        }
        if(this.position.x < 0 - this.targetRadius * offset)
        {
            this.position.x = (float) (Run.width + this.targetRadius * offset);
        }
        if(this.position.y > Run.height + this.targetRadius * offset)
        {
            this.position.y = (float) (0 - this.targetRadius * offset);
        }
        if(this.position.y < 0 - this.targetRadius * offset)
        {
            this.position.y = (float) (Run.height + this.targetRadius * offset);
        }
    }
    void update(Seeker seeker)
    {
        this.label.setTranslateX(this.position.x);
        this.label.setTranslateY(this.position.y);
        edge();
//        this.position.add(this.velocity);
//
//        if(this.position.x + this.targetRadius >= Run.width || this.position.x - this.targetRadius <= 0)
//        {
//            this.velocity.x *= -1;
//
//        }
//        if(this.position.y + this.targetRadius >= Run.height || this.position.y - this.targetRadius <= 0)
//        {
//            this.velocity.y *= -1;
//        }

        Random random = new Random();
        if(this.position.dist(seeker.position) < this.targetRadius/2 + seeker.seekerRadius/2)
        {
            this.position.x = random.nextInt((int) Run.width - 20) + 20;
            this.position.y = random.nextInt((int) Run.height - 20) + 20;
            velocity = PVector.random2D().mult(2f);
            if (this.position.dist(seeker.position) > 150f)
            {
                Seeker.numberOfHits++;
                System.out.println("Number of hits: " + Seeker.numberOfHits);
                System.out.println(this.position.dist(seeker.position));
            }
            else
            {
                this.position.x = random.nextInt((int) Run.width - 20) + 20;
                this.position.y = random.nextInt((int) Run.height - 20) + 20;
                velocity = PVector.random2D().mult(2f);
                Seeker.numberOfHits++;
                System.out.println("Number of hits: " + Seeker.numberOfHits);
                System.out.println(this.position.dist(seeker.position));
            }
        }
        setOnMouseDragged(event ->
        {
            this.position.x = (float) event.getX();
            this.position.y = (float) event.getY();
            //System.out.println(this.targetID);
            // System.out.println(getTarget(getTargetID()).targetID);
        });
    }
}