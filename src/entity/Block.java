package entity;

import core.Motion;
import gfx.ImageUtils;

import java.awt.*;

public abstract class Block extends GameObject {

    private String block;

    public Block(int posX, int posY, String texture) {
        super(64, 64, posX, posY);
        this.block = texture;
    }

    @Override
    public void update() {

    }


    @Override
    public Image getSprite() {
        return ImageUtils.loadImage("/game/themes/" + menu.Menu.getGameTheme() + "/blocks/" + block + ".png");
    }
}
