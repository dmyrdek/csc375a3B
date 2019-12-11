import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

public class Solutions extends RecursiveAction {
    private Grid grid;
    private ArrayList<Grid> clientGrids = new ArrayList<>();
    private boolean done = false;
    int count = 0;
    Map<Integer,ArrayList<Move>> moves = new HashMap<>();
    int blocksLeft = 0;
    private ArrayList<Integer> previousColors = new ArrayList<>();

    public Solutions(Grid g) {
        try {
            this.grid = (Grid) g.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i<25; i++){
            moves.put(i,new ArrayList<>());
        }
    }

    public Solutions(ArrayList<Grid> grids) {
        try {
            for (Grid g : grids){
                clientGrids.add((Grid) g.clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i<25; i++){
            moves.put(i,new ArrayList<>());
        }
    }

    @Override
    protected void compute() {
        try {
            if (clientGrids.isEmpty()) {
                this.solve(getNextGrids(grid));
            }else{
                this.solve(clientGrids);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Grid> getNextGrids(Grid g) throws CloneNotSupportedException {
        ArrayList<Grid> grids = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            if (g.getBlocks().getList()[i].getColor() != 3) {
                Grid clone = (Grid) g.clone();
                Block temp = clone.getBlocks().getList()[i];
                ArrayList<Block> tempNeighbors = clone.getBlockNeighbors(temp, new ArrayList<>());

                if (tempNeighbors.size() > 1){

                    Move move = new Move(tempNeighbors.get(0).getColor(), tempNeighbors.size(), tempNeighbors.get(0).getX(), tempNeighbors.get(0).getY(), i);

                    clone.removeBlocks(tempNeighbors);
                    move.setScore(clone.getScore());
                    moves.get(i).add(move);

                    try {
                        Socket socket = new Socket(Main.hostName, Main.portNumber);

                        ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                        outStream.writeObject(move.toString());

                        socket.close();
                        System.exit(0);

                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    //System.out.println(move.toString());


                    grids.add((Grid) clone.clone());
                }
            }
        }

        return grids;
    }

    public void solve(ArrayList<Grid> grids) throws CloneNotSupportedException {
        while (!done) {
            if (!grids.isEmpty()) {
                ArrayList<Integer> currentColors = new ArrayList<>();

                for (Grid g : grids) {
                    int blocks = 0;
                    count++;
                    for (Block block : g.getBlocks().getList()) {
                        if (block.getColor() == 3) {
                            ++blocks;
                        } else {
                            currentColors.add(block.getColor());
                        }
                    }
                    if (previousColors.containsAll(currentColors)){
                        done = true;
                    }else {
                        previousColors = new ArrayList<>();
                        previousColors.addAll(currentColors);
                    }

                    if (blocks == 24) {
                        done = true;
                    }

                    if (blocks <= 6) {
                        blocksLeft++;
                    }

                    System.out.println("Solving grid: " + count + " Score: " + g.getScore() + "   blocks left: " + (25 - blocks));
                    solve(getNextGrids(g));
                }
            } else{
                break;
            }
        }
        /*for (Map.Entry<Integer,ArrayList<Move>> entry : moves.entrySet()){
            for (Move m: entry.getValue()) {
                System.out.println(m);
            }
        }*/

    }
}

class Move{
    String color;
    int size;
    int count;
    int score;
    int x;
    int y;

    public Move(int c, int size, int x, int y, int count) {
        if (c == 0){
            color = "red";
        } else if(c == 1){
            color = "blue";
        } else if(c == 2){
            color = "green";
        }

        this.size = size;

        this.x =x;
        this.y =y;

        this.count = count;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Move " + count + ": group of " + size + " " + color + " blocks at [" + x + "," + y + "] ; Current score: " + score;
    }
}

/*public class Solutions extends RecursiveAction implements Comparable {
    private Grid grid;
    private static volatile ArrayList<ArrayList<Block>> allBlockNeighbors = new ArrayList<>();
    private Solutions parent;
    private boolean done = false;
    private static volatile Solutions solution;

    public Solutions(Grid grid, Solutions parent) {
        this.grid = grid;
        this.parent = parent;
    }

    public ArrayList<Solutions> getNextGrids(){
        this.parent = this;
        ArrayList<Solutions> grids = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            Block temp = this.grid.getBlocks().getList()[i];
            ArrayList<Block> tempNeighbors = this.grid.getBlockNeighbors(temp, new ArrayList<>());

            grid.getBlocks().removeBlocks(tempNeighbors);

            grids.add(this);
        }

        return grids;
    }

    @Override
    protected void compute() {
        if (!done && solution == null) {

            ArrayList<Solutions> subtasks = new ArrayList<>(getNextGrids());

            for (Solutions g : subtasks) {
                System.out.println(g.toString());
                g.fork();
            }

        }




    }

    @Override
    public String toString() {
        for (Block b : grid.getBlocks().getList()){
            return b.getColor() + " ";
        }
        return "";
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}*/
