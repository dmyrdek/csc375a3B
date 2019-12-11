public class Coordinates implements Cloneable{
        private int x, y;

        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}
