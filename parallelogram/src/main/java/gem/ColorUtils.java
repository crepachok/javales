package gem;

import java.awt.Color;

public class ColorUtils{
    public static Color FromString(String raw) throws Exception{
        String[] parts = raw.split(",", -1);
        if (parts.length != 3) {
            throw new Exception("Invalid color '" + raw + "' (expected r,g,b)");
        }
        try {
            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());
            return new Color(r, g, b);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid color '" + raw + "'", e);
        }
    }
    public static String ToString(Color c) {
        return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    }
}