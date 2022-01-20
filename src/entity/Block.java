package entity;

import entity.motionAndAction.MotionAndAction;
import gfx.ImageUtils;

import java.awt.*;

public abstract class Block extends GameObject {

    public static final int GRASS_BLOCK = 0;
    public static final int ACTION_BLOCK = 1;

    private String block;
    protected boolean actionUsed = false;

    public Block(int posX, int posY, int texture) {
        super(64, 64, posX, posY);
        if(texture == GRASS_BLOCK) {
            this.block = "grass";
        } else if(texture == ACTION_BLOCK) {
            this.block = "action";
        }
    }

    @Override
    public void update() {

    }

    @Override
    public Image getSprite() {
        return ImageUtils.loadImage("/game/themes/" + menu.Menu.getGameTheme() + "/blocks/" + block + ".png");
    }

    @Override
    public MotionAndAction getMotion() {
        return null;
    }


}
