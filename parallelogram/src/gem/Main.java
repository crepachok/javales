package gem;

public class Main {
    public static void main(String[] args){

        try{
            GeometryParser parser = new GeometryParser("config.txt");

            IFlatFigure figure;
            while ((figure = parser.ReadFigure()) != null){
                if (figure instanceof Parallelogram){
                    Parallelogram par = (Parallelogram)figure;
    
                    System.out.println("Угол между векторами параллелограмма: " + par.GetAngle());
                }
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
