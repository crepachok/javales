package gem;

public class Main {
    public static void main(String[] args){

        try{
            GeometryParser parser = new GeometryParser("config.txt");

            IFlatFigure figure = parser.ReadFigure();
            System.out.println(figure.GetArea());
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
