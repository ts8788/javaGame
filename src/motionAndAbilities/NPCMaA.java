package motionAndAbilities;
import controller.Controller;
import core.Position;
import core.ScreenSize;
import core.Vector2D;
import gameObjects.GameObject;
import game.state.State;

import static core.Direction.*;

public class NPCMaA extends MotionAndAbilities {
    private boolean cooldownRunning = false;
    private GameObject player;

    public NPCMaA(double speed) {
        super(speed);
        canHit = true;
        jumpHeight = 160;
    }

    @Override
    public void update(Controller controller, Position position, State state) {
        this.controller = controller;
        this.mapObjects = state.getMapObjects();
        this.gameObjects = state.getGameObjects();
        this.position = position;
        this.state = state;
        this.player = gameObjects.get(playerPosInList);
        setThisGameObject();
        controlMotionAndAbilities();

        double deltaX = 0;
        double deltaY = 0;

        x = position.getX();
        y = position.getY();

        int screenHeight = ScreenSize.getHeight();

        //wenn Position 64p (Character-Größe = 64, deswegen 128) über Boden wird fallling true
        //wenn Position größer als Boden und nicht Up requestet wird wird falling true
        if(y < savePosYJump-jumpHeight || (!controller.isRequestingUp() && !hasGround()) || !topSpace()){
            falling = true;
        }

        if(hasGround()){
            falling = false;
            gravity = 0;
        }

        if(y > screenHeight + 64){
            thisGameObject.subtractLifes(1);
        }

        if(falling) {
            deltaY += getFallSpeed(gravity);
            gravity -= 0.1;
            sitting = false;
        }


        if (controller.isRequestingUp() && !falling) {
            if(gravity == 0) {savePosYJump = (int) Math.round(y);}

            deltaY -= getFallSpeed(gravity);
            gravity += 0.1;
            sitting = false;
        }

        sprint(controller.isRequestingSprint() && thisGameObject.getDirection() == player.getDirection(), 1.3);

        if(!controller.isRequestingHit()) {
            canHit = true;
        }

        if(isHitting()) {
            sitting = false;
            damage(1);

        } else {

            if (controller.isRequestingDown()) {
                deltaY -= 1E-100;
                sitting = true;
            }

            if (controller.isRequestingLeft() && leftSpace()) {
                deltaX -= 1;
                sitting = false;
            }

            if (controller.isRequestingRight() && rightSpace()) {
                deltaX += 1;
                sitting = false;
            }

        }


        vector = new Vector2D(deltaX, deltaY);
        vector.multiply(speed, 1);

    }

    private void controlMotionAndAbilities() {
        int playerWidth = player.getSize().getWidth();
        int playerHeight = player.getSize().getHeight();
        int playerSideSpace = (64-playerWidth)/2;
        int playerTopSpace = (64-playerHeight);
        int pPosX = player.getPosition().intX() + playerSideSpace;
        int pPosY = player.getPosition().intY() + playerTopSpace;

        int thisGameObjectWidth = thisGameObjectSize.getWidth();
        int thisGameObjectHeight = thisGameObjectSize.getHeight();
        int thisGameObjectSideSpace = (64-thisGameObjectWidth)/2;
        int thisGameObjectTopSpace = (64-thisGameObjectHeight);
        int posX = (int) (x+thisGameObjectSideSpace);
        int posY = (int) (y+thisGameObjectTopSpace);

        if(thisGameObject.isShown()) {
            if (posX < pPosX) {
                controller.setRequestingLeft(false);
                controller.setRequestingRight(true);
            }
            if (posX > pPosX) {
                controller.setRequestingRight(false);
                controller.setRequestingLeft(true);
            }


            if(pPosY < posY + 32 && pPosY > posY - 32){
                if (pPosX < posX + thisGameObjectWidth + 32 && pPosX > posX-thisGameObjectWidth-32) {
                    if(!cooldownRunning) {
                        new Thread(() -> {
                            cooldownRunning = true;
                            try {
                                Thread.sleep(400);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            controller.setRequestingHit(true);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            controller.setRequestingHit(false);
                            cooldownRunning = false;
                        }).start();
                    }
                }
            }

            if(hasGround()){
                if(thisGameObject.getDirection() == L && !hasGround(-thisGameObjectWidth/2)){
                    controller.setRequestingJump(true);
                } else if(thisGameObject.getDirection() == R && !hasGround(thisGameObjectWidth/2)){
                    controller.setRequestingJump(true);
                }
            }

            if(falling) {
                controller.setRequestingJump(false);
            }

            controller.setRequestingSprint(player.getController().isRequestingSprint());

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
    public boolean isJumping() {
        return controller.isRequestingUp();
    }

    @Override
    public boolean isHitting() {
        return controller.isRequestingHit();
    }

    @Override
    public boolean isSitting(){
        return sitting;
    }

    @Override
    public boolean canCauseBlockAction() {
        return false;
    }
}
