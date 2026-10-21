package gem;

import org.json.JSONObject;

public class JsonFigureFactory {
    public static IFlatFigure Create(JSONObject json) throws Exception{
        IFlatFigure result;

        switch (json.getString("Type")) {
            case "Parallelogram":
                Vector first = Vector.FromString(json.getString("Vec1"));
                Vector second = Vector.FromString(json.getString("Vec2"));

                result = new Parallelogram(first, second);
                break;
        
            default:
                throw new Exception("Unidentified figure type");
        }

        result.SetName(json.getString("Name"));
        result.SetColor(ColorUtils.FromString(json.getString("Color")));

        return result;
    }
}
