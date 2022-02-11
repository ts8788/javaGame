package game.state;

import controller.NPCController;
import controller.PlayerController;
import core.Position;
import core.ScreenSize;
import gameObjects.*;
import motionAndAbilities.NPCMaA;
import motionAndAbilities.PlayerMaA;
import game.Game;
import input.Input;
import utils.FileLoader;


// child class of State

public class GameState extends State {

    private Player player;


    public GameState(Input input, Game game) {
        super(input, game);

        int ground = ScreenSize.getGround();

        String[][] sMap = FileLoader.loadMap();

        createMap(sMap, ground);

        // player needs to be initialized as first, so it is at index 0 in gameObjects, and it's position will not change, even if other gameObjects are added ore removed
        initializePlayer(sMap, ground);

        initializeNPCs(sMap, ground);
    }

    private void initializePlayer(String[][] sMap, int ground) {

        // searching through map and adding player at defined position
        for (int i = 0; i < sMap.length; i++) {
            for (int j = 0; j < sMap[i].length-1; j++) {
                String letter = sMap[i][j];
                Position relativePosition = new Position((j-1)*64, ground-(i-1)*64);
                // !, because it definetly won't be used as block
                if(letter.equalsIgnoreCase("!")){
                    this.player = new Player(
                            new PlayerController(input),
                            new PlayerMaA(3.5),
                            relativePosition,
                            spriteLibrary,
                            5,
                            this
                    );
                    gameObjects.add(player);
                }
            }
        }
    }
    private void initializeNPCs(String[][] sMap, int ground) {

        // searching through map and adding nps at defined position
        for (int i = 0; i < sMap.length; i++) {
            for (int j = 0; j < sMap[i].length-1; j++) {
                String letter = sMap[i][j];
                Position relativePosition = new Position((j-1)*64, ground-(i-1)*64);
                try{
                    int npc = Integer.parseInt(letter);
                    gameObjects.add(
                            new NPC(
                                    new NPCController(),
                                    new NPCMaA(1.5),
                                    relativePosition,
                                    npc,
                                    spriteLibrary,
                                    5,
                                    this
                            )
                    );
                } catch(Exception ignored){
                    if(letter.equalsIgnoreCase("C")){
                        gameObjects.add(
                                new Coin(
                                        relativePosition,
                                        StaticEntity.COIN
                                )
                        );
                    }
                }

            }
        }
    }


    private void createMap(String[][] sMap, int ground) {

        // searching through map and adding blocks at defined positions
        for (int i = 0; i < sMap.length; i++) {
            for (int j = 0; j < sMap[i].length-1; j++) {
                String letter = sMap[i][j];
                Position relativePosition = new Position((j-1)*64, ground-(i-1)*64);
                GameObject object = null;
                if(letter.equals("G")){
                    object = new NormalBlock(
                                    relativePosition,
                                    Block.GROUND_BLOCK,
                                    true
                            );
                } else if(letter.equals("W")){
                    object = new NormalBlock(
                                    relativePosition,
                                    Block.WALL_BLOCK,
                                    true
                            );
                } else if(letter.equals("B")){
                    object = new NormalBlock(
                            relativePosition,
                            Block.BRICKS,
                            true
                    );
                } else if(letter.equals("b")){
                    object = new NormalBlock(
                            relativePosition,
                            Block.TRANS_BRICKS,
                            false
                    );
                } else if(letter.equals("P")){
                    object = new NormalBlock(
                            relativePosition,
                            Block.PLANKS,
                            true
                    );
                } else if(letter.equals("p")){
                    object = new NormalBlock(
                            relativePosition,
                            Block.TRANS_PLANKS,
                            false
                    );
                } else if(letter.equals("g")){
                    object = new NormalBlock(
                                    relativePosition,
                                    Block.TRANS_GROUND_BLOCK,
                                    false
                            );
                } else if(letter.equals("w")){
                    object = new NormalBlock(
                                    relativePosition,
                                    Block.TRANS_WALL_BLOCK,
                                    false
                            );
                } else if(letter.equalsIgnoreCase("A")){
                    object = new ActionBlock(
                                    relativePosition,
                                    Block.ACTION_BLOCK
                            );
                } else if(letter.equalsIgnoreCase("F")){
                    object = new FinishBlock(
                                    relativePosition,
                                    Block.GROUND_BLOCK
                            );
                }

                if(object!=null) {
                    mapObjects.add(object);
                }

                // saving lowest block for playerMaA
                if(i == 0 && j == 0){
                    lowestBlock = object;
                }
            }
        }
    }

    public MovingEntity getPlayer() {
        return player;
    }
}
