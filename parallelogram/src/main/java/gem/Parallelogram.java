package gem;

import java.awt.*;

import org.json.JSONObject;

public class Parallelogram implements IFlatFigure, Comparable<Parallelogram>{
    private Vector _second;
    private Vector _first;
    private double _angle;

    private String _name;
    private Color _color;

    public Parallelogram(Vector first, Vector second) throws InvalidParallelogramInputVectorsException{
        if (first.GetLength() == 0 || second.GetLength() == 0)
            throw new InvalidParallelogramInputVectorsException("Вектора, задающие параллелограмм не должны иметь длину 0");

        if (first.GetAngleToHorizon() == second.GetAngleToHorizon())
            throw new InvalidParallelogramInputVectorsException("Вектора не должны лежать на одной линии");

        _second = second; _first = first;
        _angle = Vector.GetAngle(second, first);
    }

    public Parallelogram(Vector first, Vector second, String _name, Color color) throws InvalidParallelogramInputVectorsException {
        this(first, second);
        this._name = _name;
        this._color = color;
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
        return _name;
    }
    public void SetName(String name){
        _name = name;
    }
    public Color GetColor(){ 
        return _color;
    }
    public void SetColor(Color color){
        _color = color;
    }
    public Vector GetFirstVector(){
        return _first;
    }
    public Vector GetSecondVector(){
        return _second;
    }
    public JSONObject ToJSON(){
        JSONObject json = new JSONObject();

        json.put("Type", "Parallelogram");
        json.put("Vec1", _first.x + "," + _first.y);
        json.put("Vec2", _second.x + "," + _second.y);
        json.put("Name", _name);
        json.put("Color", _color.getRed() + "," + _color.getGreen() + "," + _color.getBlue());

        return json;
    }

    public double GetArea(){
        double height = _second.GetLength() * Math.sin(_angle);

        return _first.GetLength() * height;
    }

    public double GetPerimeter(){
        return 2 * (_second.GetLength() + _first.GetLength());
    }

    public int compareTo(Parallelogram b){
        double areaA = this.GetArea();
        double areaB = b.GetArea();

        if (areaA > areaB) return 1;
        if (areaA == areaB) return 0;
        return -1;
    }
}