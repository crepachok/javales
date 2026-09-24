package gem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class GeometryParser {
    private FileInputStream _reader;

    public GeometryParser(String filename) throws FileNotFoundException, IOException{
        _reader = new FileInputStream(filename);        
    }
    public IFlatFigure ReadFigure() throws IOException, Exception{
        String word;
        while (!(word = ReadWord()).equals("FIGURE") && !word.equals("")) {}
        String type = ReadWord();

        switch (type){
            case "parallelogram":
                return ReadParallelogram();
            default:
                return null;
            
        }
    }
    private Parallelogram ReadParallelogram() throws IOException, InvalidParallelogramInputVectorsException {
        List<Vector> vectors = new ArrayList<>(2);
        String name = null;
        Color color = null;

        String word;
        while (!(word = ReadWord()).equals("END")) {
            if (word.isEmpty()) {
                throw new IOException("Unexpected end of file while reading parallelogram");
            }
            switch (word) {
                case "VEC":
                    vectors.add(ReadVector());
                    break;
                case "NAME":
                    name = ReadName();
                    break;
                case "COLOR":
                    color = ReadColor();
                    break;
                default:
                    throw new IOException("Unexpected token in parallelogram: '" + word + "'");
            }
        }

        if (vectors.size() != 2) {
            throw new IOException("Parallelogram requires exactly 2 vectors, got " + vectors.size());
        }
        if (name == null) {
            throw new IOException("Parallelogram has no NAME");
        }
        if (color == null) {
            throw new IOException("Parallelogram has no COLOR");
        }

        return new Parallelogram(vectors.get(0), vectors.get(1), name, color);
    }
    private Vector ReadVector() throws IOException {
        String raw = ReadWord();
        String[] parts = raw.split(",", -1);
        if (parts.length != 2) {
            throw new IOException("Invalid vector '" + raw + "' (expected x,y)");
        }
        try {
            return new Vector(
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim())
            );
        } catch (NumberFormatException e) {
            throw new IOException("Invalid vector '" + raw + "'", e);
        }
    }
    private String ReadName() throws IOException {
        String name = ReadWord();
        if (name.isEmpty()) {
            throw new IOException("Unexpected end of file while reading NAME");
        }
        return name;
    }
    private Color ReadColor() throws IOException {
        String raw = ReadWord();
        String[] parts = raw.split(",", -1);
        if (parts.length != 3) {
            throw new IOException("Invalid color '" + raw + "' (expected r,g,b)");
        }
        try {
            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());
            return new Color(r, g, b);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid color '" + raw + "'", e);
        }
    }
    private String ReadWord() throws IOException{
        int i;
        String word = "";
        boolean found = false;

        while ((i = _reader.read()) != -1){
            char symbol = (char)i;

            if (Character.isWhitespace(symbol)) {
                if (found) return word;
                else continue;
            }

            found = true;
            word = word + symbol;
        }

        return word;
    }
}
