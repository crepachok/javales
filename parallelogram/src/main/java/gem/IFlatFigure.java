package gem;

import java.awt.*;

import org.json.JSONObject;

public interface IFlatFigure{
    double GetArea();
    double GetPerimeter();
    String GetType();
    String GetName();
    void SetName(String name);
    Color GetColor();
    void SetColor(Color color);
    JSONObject ToJSON();
}