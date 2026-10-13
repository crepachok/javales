package gem;

public class Point{
    public double x;
    public double y;
    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double DistanceTo(Point to){
        return Math.sqrt(Math.pow(x - to.x, 2) + Math.pow(y - to.y, 2));
    }
}