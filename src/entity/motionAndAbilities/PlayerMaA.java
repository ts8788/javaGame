package entity.motionAndAbilities;
import controller.Controller;
import core.Position;
import core.ScreenSize;
import core.Vector2D;
import entity.GameObject;
import game.state.State;

import java.util.List;

public class PlayerMaA extends MotionAndAbilities {
    private boolean falling;
    private boolean sitting;
    private double gravity;
    private final int ground = ScreenSize.getGround();
    private int savePosYJump = ground;

    public PlayerMaA(double speed) {
        super(speed);
        canHit = true;
    }

    @Override
    public void update(Controller controller, Position position, State state) {
        this.controller = controller;
        this.mapObjects = state.getMapObjects();
        this.gameObjects = state.getGameObjects();
        this.position = position;
        this.state = state;

        double deltaX = 0;
        double deltaY = 0;

        x = position.getX();
        y = position.getY();

        int leftBorder = ScreenSize.getLeftBorder();
        int rightBorder = ScreenSize.getRightBorder();

        //wenn Position 64p (Character-Größe = 64, deswegen 128) über Boden wird fallling true
        //wenn Position größer als Boden und nicht Up requestet wird wird falling true
        if(y < savePosYJump-160 || (!controller.isRequestingUp() && !hasGround()) || !topSpace()){
            falling = true;
        }

        if(hasGround()){
            falling = false;
            gravity = 0;
        }

        if(falling) {
            deltaY += getFallSpeed(gravity);
            gravity -= 0.5;
            sitting = false;
        }

        if (controller.isRequestingUp() && !falling) {
            if(gravity == 0) {savePosYJump = (int) Math.round(y);}

            deltaY -= getFallSpeed(gravity);
            gravity += 0.5;
            sitting = false;
        }

        if(controller.isRequestingSprint()) {
            sprint(true);
        }

        if(!controller.isRequestingSprint()) {
            sprint(false);
        }

        if(controller.isNotRequestingHit()) {
            canHit = true;
        }

        if(isHitting()) {
            sitting = false;
            damage(state);
        } else {

            if (controller.isRequestingDown()) {
                deltaY -= 1E-100;
                sitting = true;
            }
            if (controller.isRequestingLeft() && leftSpace()) {
                deltaX -= 1.6;
                sitting = false;
                if(x <= leftBorder){
                    moveMap(new Vector2D(1.6,0));
                }
            }

            if (controller.isRequestingRight() && rightSpace()) {
                deltaX += 1.6;
                sitting = false;
                if(x >= rightBorder) {
                    moveMap(new Vector2D(-1.6,0));
                }
            }
        }

        doBlockPositionXAction();
        doBlockPositionAction();


        vector = new Vector2D(deltaX, deltaY);
        vector.multiply(speed, normalSpeed);

    }

    private void moveMap(Vector2D mapVector) {

        mapVector.multiply(speed, normalSpeed);

        for (GameObject mapObject : mapObjects) {
            int blockPosX = mapObject.getPosition().intX();
            int blockPosY = mapObject.getPosition().intY();

            mapObject.setPosition(new Position(blockPosX + (int) mapVector.getX(), blockPosY + (int) mapVector.getY()));
        }
        for (GameObject gameObject : gameObjects) {
            int objPosX = gameObject.getPosition().intX();
            int objPosY = gameObject.getPosition().intY();

            gameObject.setPosition(new Position(objPosX + (int) mapVector.getX(), objPosY + (int) mapVector.getY()));
        }
    }

    private void doBlockPositionXAction() {
        doBlockPositionXAction(mapObjects);
        doBlockPositionXAction(gameObjects);
    }

    private void doBlockPositionXAction(List<GameObject> objects) {

        for (GameObject object : objects) {
            int blockPosX = object.getPosition().intX();
            if(x >= blockPosX && x <= blockPosX + 64) {
                object.doActionOnPositionX(state);
            }
        }
    }

    private void doBlockPositionAction() {
        doBlockPositionAction(mapObjects);
        doBlockPositionAction(gameObjects);
    }

    private void doBlockPositionAction(List<GameObject> objects) {

        boolean updatable = state.getUpdatable();
        for (int i = 0; updatable && i < objects.size(); i++) {
            updatable = state.getUpdatable();
            GameObject object = objects.get(i);
            int blockPosX = object.getPosition().intX();
            int blockPosY = object.getPosition().intY();
            if(x > blockPosX - 32 && x < blockPosX + 32) {
                if(y > blockPosY - 64 && y < blockPosY + 64) {
                    object.doActionOnSamePosition(state);
                }
            }
        }
    }


    @Override
    public Vector2D getVector() {
        return vector;
    }

    @Override
    public boolean isMoving() {
        return vector.length() > 0;
    }

    @Override
    public boolean isHitting() {
        return controller.isRequestingHit();
    }

    @Override
    public boolean isJumping() {
        return controller.isRequestingUp();
    }

    @Override
    public boolean isSitting(){
        return sitting;
    }

    @Override
    public boolean canCauseBlockAction() {
        return true;
    }
}