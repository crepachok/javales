package gem;

import java.awt.*;

public class Parallelogram implements IFlatFigure, Comparable<Parallelogram>{
    private Vector _first;
    private Vector _second;
    private double _angle;

    public String Name;
    public Color Color;

    public Parallelogram(Vector bot, Vector top) throws InvalidParallelogramInputVectorsException{
        if (bot.GetLength() == 0 || top.GetLength() == 0)
            throw new InvalidParallelogramInputVectorsException("Вектора, задающие параллелограмм не должны иметь длину 0");

        if (bot.GetAngleToHorizon() == top.GetAngleToHorizon())
            throw new InvalidParallelogramInputVectorsException("Вектора не должны лежать на одной линии");

        _first = top; _second = bot;
        _angle = Vector.GetAngle(top, bot);
    }

    public Parallelogram(Vector bot, Vector top, String name, Color color) throws InvalidParallelogramInputVectorsException {
        this(bot, top);
        this.Name = name;
        this.Color = color;
    }

    public String GetTitle(ITitled title){
        return title.GetTitle();
    }
    public double GetAngle(){
        return _angle;
    }
    public String GetType(){
        return "Parallelogram";
    }
    public String GetName(){ 
        return Name;
    }
    public Color GetColor(){ 
        return Color;
    }
    public Vector GetFirstVector(){
        return _second;
    } 
    public Vector GetSecondVector(){
        return _first;
    }

    public double GetArea(){
        double height = _first.GetLength() * Math.sin(_angle);

        return _second.GetLength() * height;
    }

    public double GetPerimeter(){
        return 2 * (_first.GetLength() + _second.GetLength());
    }

    public int compareTo(Parallelogram b){
        double areaA = this.GetArea();
        double areaB = b.GetArea();

        if (areaA > areaB) return 1;
        if (areaA == areaB) return 0;
        return -1;
    }
}