import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Blocks implements Cloneable, Serializable{
    private Block[] blocks = new Block[25];

    public Block[] getList() {
        return blocks;
    }

    public Blocks() {
        Random rand = new Random();
        int count = 0;

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                blocks[count] = new Block(rand.nextInt(3), x, y);
                count++;
            }
        }
    }

    public Blocks(Block[] b) {
        blocks = b;
    }

    public void resetNeighbors(){
        for (Block b: blocks) {
            b.resetAll();
        }
    }

    public Object clone() throws CloneNotSupportedException{
        Blocks clone = (Blocks) super.clone();
        Block[] cloneArray = new Block[25];


        for (int i = 0; i < blocks.length; i++) {
            Block temp = (Block) blocks[i].clone();
            temp.resetAll();
            cloneArray[i] = temp;
        }

        clone.blocks = cloneArray;

        return clone;
    }
}
