package pdf;

public class Point<T> {
    public Point(T x, T y) {
        this.x = x;
        this.y = y;
    }
    public T getX() {
        return x;
    }

    public void setX(T x) {
        this.x = x;
    }

    private T x;

    public T getY() {
        return y;
    }

    public void setY(T y) {
        this.y = y;
    }

    private T y;

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
