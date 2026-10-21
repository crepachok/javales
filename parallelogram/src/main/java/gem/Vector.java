package gem;

public class Vector {
    public double x;
    public double y;

    public Vector(Point start, Point end){
        x = end.x - start.x;
        y = end.y - start.y;
    }
    
    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double GetLength(){
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public static Vector FromString(String str) throws NumberFormatException{
        String[] parts = str.split(",");

        return new Vector(
            Double.parseDouble(parts[0].trim()),
            Double.parseDouble(parts[1].trim()));
    }

    @Override 
    public String toString(){
        return x + "," + y;
    }

    public static double GetAngle(Vector a, Vector b){
        double scalar = a.x * b.x + a.y * b.y;
        double lenProd = a.GetLength() * b.GetLength();

        return Math.acos(scalar / lenProd);
    }

    public double GetAngleToHorizon(){
        Vector horizon = new Vector(1, 0);

        return GetAngle(this, horizon);
    }
}
