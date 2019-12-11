public class Block implements Cloneable {
    private int color;
    private Coordinates coordinates;
    private boolean left = false, right = false, top = false, bottom = false;

    public Block(int color, int x, int y){
        this.color = color;
        this.coordinates = new Coordinates(x,y);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setBottom(Block block) {
        if (block.color == this.color && block.color != 3){
            this.bottom = true;
        }
    }

    public void setTop(Block block) {
        if (block.color == this.color && block.color != 3){
            this.top = true;
        }
    }

    public void setLeft(Block block) {
        if (block.color == this.color && block.color != 3){
            this.left = true;
        }
    }

    public void setRight(Block block) {
        if (block.color == this.color && block.color != 3){
            this.right = true;
        }
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public int getColor() {
        return color;
    }

    public int getX() {
        return this.coordinates.getX();
    }

    public int getY() {
        return this.coordinates.getY();
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isTop() {
        return top;
    }

    public boolean isBottom() {
        return bottom;
    }

    public void resetAll(){
        top = false;
        bottom = false;
        left = false;
        right = false;
    }

    public Object clone() throws CloneNotSupportedException{
        Block clone = (Block) super.clone();
        clone.coordinates = (Coordinates) coordinates.clone();

        return clone;
    }
}
