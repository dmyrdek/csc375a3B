import java.io.Serializable;
import java.util.ArrayList;

public class Grid implements Cloneable, Serializable{
    private Blocks blocks;
    private Integer score;

    public Blocks getBlocks() {
        return blocks;
    }

    public void setBlocks(Blocks blocks) {
        this.blocks = blocks;
    }

    public Grid(){
        this.blocks = new Blocks();
        this.score = 0;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = this.score + score;
    }

    public void setBlockNeighbors(){
        for (int i = 0; i < 25; i++){
            for (int j = 0; j < 25; j++) {
                if (blocks.getList()[i].getY() - 1 == blocks.getList()[j].getY() && blocks.getList()[i].getX() == blocks.getList()[j].getX()){
                    blocks.getList()[i].setTop(blocks.getList()[j]);
                } else if (blocks.getList()[i].getY() + 1 == blocks.getList()[j].getY() && blocks.getList()[i].getX() == blocks.getList()[j].getX()){
                    blocks.getList()[i].setBottom(blocks.getList()[j]);
                } else if (blocks.getList()[i].getX() - 1 == blocks.getList()[j].getX() && blocks.getList()[i].getY() == blocks.getList()[j].getY()){
                    blocks.getList()[i].setLeft(blocks.getList()[j]);
                } else if (blocks.getList()[i].getX() + 1 == blocks.getList()[j].getX() && blocks.getList()[i].getY() == blocks.getList()[j].getY()){
                    blocks.getList()[i].setRight(blocks.getList()[j]);
                }
            }
        }
    }

    public ArrayList<Block> getBlockNeighbors(Block block, ArrayList<Block> blocksList){
        if (block.isTop()) {
            for (int i = 0; i < 25; i++) {
                if (block.getY() - 1 == blocks.getList()[i].getY() && block.getX() == blocks.getList()[i].getX()){
                    if (!blocksList.contains(blocks.getList()[i])) {
                        blocksList.add(blocks.getList()[i]);
                        blocksList = getBlockNeighbors(blocks.getList()[i], blocksList);
                    }
                }
            }
        }

        if (block.isBottom()) {
            for (int i = 0; i < 25; i++) {
                if (block.getY() + 1 == blocks.getList()[i].getY() && block.getX() == blocks.getList()[i].getX()){
                    if (!blocksList.contains(blocks.getList()[i])) {
                        blocksList.add(blocks.getList()[i]);
                        blocksList = getBlockNeighbors(blocks.getList()[i], blocksList);
                    }
                }
            }
        }

        if (block.isLeft()) {
            for (int i = 0; i < 25; i++) {
                if (block.getX() - 1 == blocks.getList()[i].getX() && block.getY() == blocks.getList()[i].getY()){
                    if (!blocksList.contains(blocks.getList()[i])) {
                        blocksList.add(blocks.getList()[i]);
                        blocksList = getBlockNeighbors(blocks.getList()[i], blocksList);
                    }
                }
            }
        }

        if (block.isRight()) {
            for (int i = 0; i < 25; i++) {
                if (block.getX() + 1 == blocks.getList()[i].getX() && block.getY() == blocks.getList()[i].getY()){
                    if (!blocksList.contains(blocks.getList()[i])) {
                        blocksList.add(blocks.getList()[i]);
                        blocksList = getBlockNeighbors(blocks.getList()[i], blocksList);
                    }
                }
            }
        }

        return blocksList;
    }

    public void removeBlocks(ArrayList<Block> blocksList){
        if (!blocksList.isEmpty() && blocksList.get(0).getColor() != 3){
        Integer intScore = blocksList.size() >= 3 ? blocksList.size() - 2 : 0;
        intScore = intScore * intScore;
        setScore(intScore);
        for (Block b: blocksList) {
            b.setColor(3);
            if (b.getY() != 0) {
                for (int i = 0; i < 25; i++) {
                    int y = b.getY() - 1;
                    while (y >= 0) {
                        if (y == blocks.getList()[i].getY() && b.getX()
                                == blocks.getList()[i].getX()
                                && b.getColor() != blocks.getList()[i].getColor()
                                && blocks.getList()[i].getColor() != 3) {
                            Coordinates temp = b.getCoordinates();
                            b.setCoordinates(blocks.getList()[i].getCoordinates());
                            blocks.getList()[i].setCoordinates(temp);
                        } else if (y == blocks.getList()[i].getY()
                                && b.getX() == blocks.getList()[i].getX()
                                && blocks.getList()[i].getColor() == 3) {
                            popBlockToTheRight(b);
                        }
                        y = y - 1;
                    }
                }
            } else if (b.getY() == 0 && b.getX() != 3) {
                popBlockToTheRight(b);
            }
        }
        }
    }

    public void popBlockToTheRight(Block b){
        for (int j = 0; j < 25; j++) {
            int x = b.getX() + 1;
            while (x <= 4) {
                if (x == blocks.getList()[j].getX() && b.getY() == blocks.getList()[j].getY()  && b.getColor() != blocks.getList()[j].getColor()) {
                    Coordinates temp = b.getCoordinates();
                    b.setCoordinates(blocks.getList()[j].getCoordinates());
                    blocks.getList()[j].setCoordinates(temp);
                }
                x = x + 1;
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        Grid clone = (Grid) super.clone();
        Blocks cloneBlocks = (Blocks) blocks.clone();
        clone.setBlocks(cloneBlocks);
        clone.setBlockNeighbors();
        clone.score = this.score;
        return clone;
    }
}
