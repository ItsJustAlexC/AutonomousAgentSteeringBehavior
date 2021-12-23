package app;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import processing.core.PVector;
import java.util.Random;

public class Seeker extends Polygon
{
    static int numberOfHits = 0;
    float maxVelocity = 3f;
    float maxForce = .1f;
    double seekerRadius = 15;
    TargetInfo targetInfo = new TargetInfo();

    PVector position = new PVector();
    PVector velocity = new PVector();
    PVector steering = new PVector();

    Circle awarenessCircle = new Circle(100,null);
    Circle circle1 = new Circle();
    Circle circle2 = new Circle();
    Line line = new Line();
    Line line2 = new Line();

    double angleA, posX, posY;
    private java.lang.Object Object;

    Seeker()
    {
        this.position.x = (float) 0;
        this.position.y = (float) 0;
        this.getPoints().addAll(-this.seekerRadius, -this.seekerRadius / 2, -this.seekerRadius, this.seekerRadius / 2, this.seekerRadius, 0.0);
    }

    Seeker(float x, float y)
    {
        this.position.x = x;
        this.position.y = y;
        this.getPoints().addAll(-this.seekerRadius, -this.seekerRadius / 2, -this.seekerRadius, this.seekerRadius / 2, this.seekerRadius, 0.0);
    }

    void show ()
    {
        setTranslateX(this.position.x);
        setTranslateY(this.position.y);
        setFill(Color.TRANSPARENT);
        setStroke(Color.WHITE);
        awarenessCircle.setCenterX(this.position.x);
        awarenessCircle.setCenterY(this.position.y);
        awarenessCircle.setStroke(Color.WHITE);
        awarenessCircle.setStrokeWidth(.1f);

    }
    private void edge()
    {
        int offset = 2;

        if(this.position.x > Run.width + this.seekerRadius * offset)
        {
            this.position.x = (float) (0 - this.seekerRadius * offset);
        }
        if(this.position.x < 0 - this.seekerRadius * offset)
        {
            this.position.x = (float) (Run.width + this.seekerRadius * offset);
        }
        if(this.position.y > Run.height + this.seekerRadius * offset)
        {
            this.position.y = (float) (0 - this.seekerRadius * offset);
        }
        if(this.position.y < 0 - this.seekerRadius * offset)
        {
            this.position.y = (float) (Run.height + this.seekerRadius * offset);
        }
    }

    void update() {
        setRotate(Math.toDegrees(this.velocity.heading()));
        if(inAwareness())
        {
            this.steering = collisionAvoidance(Target.targetList.get(targetInfo.targetID));
        }else{
            this.steering = wander();
        }
        this.velocity.add(this.steering);
        this.position.add(this.velocity);
        edge();
    }



    private PVector seek(Target target)
    {
        PVector desired_velocity = PVector.sub(target.position, this.position).normalize().mult(maxVelocity);
        this.steering = PVector.sub(desired_velocity, velocity).limit(maxForce);
        return this.steering;
    }
    private PVector intercept(Target target)
    {
        PVector future_position = PVector.add(target.position,PVector.mult(target.velocity,10));
        PVector desired_velocity = PVector.sub(future_position, this.position).normalize().mult(maxVelocity);
        this.steering = PVector.sub(desired_velocity, velocity).limit(maxForce);
        return this.steering;
    }
    private PVector flee(Target target)
    {
        PVector desired_velocity = PVector.sub(this.position, target.position).normalize().mult(maxVelocity);
        this.steering = PVector.sub(desired_velocity, velocity).limit(maxForce);
        return this.steering;
    }
    private PVector arrival(Target target)
    {
        PVector desired_velocity = PVector.sub(target.position,this.position);
        float distance = desired_velocity.mag();
        System.out.println(distance);
        float slowDownRadius = 100;


        if(distance < slowDownRadius)
        {
            desired_velocity = desired_velocity.normalize().mult(maxVelocity).mult(distance/slowDownRadius);
        }else{
            desired_velocity = desired_velocity.normalize().mult(maxVelocity);
        }

        this.steering = PVector.sub(desired_velocity, velocity).limit(maxForce);
        return this.steering;
    }
    private PVector wander()
    {
        awarenessCircle.setRadius(100);
        PVector c_velocity = this.velocity.copy().normalize().setMag(75);
        PVector circleCenter = PVector.add(this.position, PVector.mult(c_velocity, 1));

        circle1.setRadius(50);
        circle1.setCenterX(circleCenter.x);
        circle1.setCenterY(circleCenter.y);
        circle1.setFill(null);
        //circle1.setStroke(Color.WHITE);

        circle2.setRadius(5);
        circle2.setFill(null);
        //circle2.setStroke(Color.WHITE);

        PVector circle2Pos = new PVector((float) circle2.getCenterX(),(float) circle2.getCenterY());

        double x = circle2.getCenterX() - circleCenter.x;
        double y = circleCenter.y - circle2.getCenterY();
        double z = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));

        Random random = new Random();
        double x1 = random.nextInt(6);
        if(x1 == 0)
        {
            angleA += 1;
        }
        else if(x1 == 1)
        {
            angleA += 3;
        }
        else if(x1 == 2)
        {
            angleA += 5;
        }
        else if(x1 == 3)
        {
            angleA -= 5;
        }
        else if(x1 == 4)
        {
            angleA -= 3;
        }
        else
        {
            angleA -= 1;
        }

        posX = (float) (circleCenter.x + (circle1.getRadius() * Math.cos(Math.toRadians(angleA))));
        posY = (float) (circleCenter.y - (circle1.getRadius() * Math.sin(Math.toRadians(angleA))));

        circle2.setCenterX(posX);
        circle2.setCenterY(posY);

        PVector desired_velocity = PVector.sub(circle2Pos, this.position).normalize().mult(maxVelocity);
        this.steering = PVector.sub(desired_velocity, velocity).limit(maxForce);
        return this.steering;
    }
    private PVector collisionAvoidance(Target target)
    {
        awarenessCircle.setRadius(this.seekerRadius * 2 );
        final float MAX_SEE_AHEAD = 50;
        final float MAX_AVOID_FORCE = 2.5f;
        PVector c_velocity = this.velocity.copy();
        PVector ahead = PVector.add(this.position,PVector.mult(c_velocity.normalize(), MAX_SEE_AHEAD));
        PVector ahead2 = PVector.add(this.position,PVector.mult(c_velocity.normalize(), MAX_SEE_AHEAD * 0.5f));

        PVector avoidanceForce = PVector.sub(ahead, target.position);
        avoidanceForce = PVector.mult(avoidanceForce.normalize(), MAX_AVOID_FORCE);

        this.steering = PVector.sub(avoidanceForce, velocity).limit(maxForce);

        line.setStartX(this.position.x);
        line.setStartY(this.position.y);
        line.setEndX(ahead.x);
        line.setEndY(ahead.y);
        line.setStrokeWidth(2);
        line.setStroke(Color.RED);

//        line2.setStartX(this.position.x);
//        line2.setStartY(this.position.y);
//        line2.setEndX(ahead2.x);
//        line2.setEndY(ahead2.y);
//        line2.setStrokeWidth(2);
//        line2.setStroke(Color.WHITE);
        System.out.println(intersects(ahead,ahead2,target));
        return this.steering;

    }

    private boolean intersects(PVector ahead, PVector ahead2, Target target)
    {
        if((PVector.dist(target.position, ahead) <= target.targetRadius) || PVector.dist(target.position, ahead2) <= target.targetRadius || PVector.dist(target.position, this.position) < target.targetRadius)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public double distance(Circle awarenessCircle, Target target)
    {
        return Math.sqrt((awarenessCircle.getCenterX() - target.position.x) * (awarenessCircle.getCenterX() - target.position.x) + (awarenessCircle.getCenterY() - target.position.y) * (awarenessCircle.getCenterY() - target.position.y));
    }

    private boolean inAwareness()
    {
        for (int i = 0; i < Target.NumberOfTargets; i++)
        {
            if(distance(awarenessCircle, Target.targetList.get(i)) <= awarenessCircle.getRadius() + Target.targetList.get(i).getRadius())
            {
                targetInfo.isAware = true;
                targetInfo.targetID = getTarget(i);
                return true;
            }
        }
        targetInfo.isAware = false;
        targetInfo.targetID = -1;
        return false;

    }
    private int getTarget(int target)
    {
        return Target.targetList.get(target).targetID;
    }
}

class TargetInfo
{
    boolean isAware;
    int targetID;
    TargetInfo()
    {
    }
    TargetInfo(boolean isAware, int targetID)
    {
        this.isAware = isAware;
        this.targetID = targetID;
    }
    TargetInfo(int targetID, boolean isAware)
    {
        this.isAware = isAware;
        this.targetID = targetID;
    }
}