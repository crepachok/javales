package gem;

import java.sql.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DbContext {
    private String _conString;
    public DbContext(String con){
        _conString = con;
    }

    public void InitDb() throws SQLException{
        try (Connection sql = DriverManager.getConnection(_conString); 
        Statement statement = sql.createStatement()){
            String createFirst = """
                CREATE TABLE IF NOT EXISTS figures (
                    Id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    Type  VARCHAR NOT NULL,
                    Name  VARCHAR NOT NULL,
                    Color VARCHAR NOT NULL,
                    Vec1  VARCHAR NULL,
                    Vec2  VARCHAR NULL
                );
                """;

            String createSecond = """
                CREATE TABLE IF NOT EXISTS json (
                    Id   INTEGER PRIMARY KEY REFERENCES figures(Id),
                    Data BLOB    NOT NULL
                );
                """;

            statement.execute(createFirst);
            statement.execute(createSecond);
        }
    }

    public List<IFlatFigure> GetAll() throws Exception{
        List<IFlatFigure> result = new ArrayList<>();
        String sql = "SELECT Type, Name, Color, Vec1, Vec2 FROM figures";

        try (Connection c = DriverManager.getConnection(_conString);
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String type  = rs.getString("Type");
                String name  = rs.getString("Name");
                Color color = ColorUtils.FromString(rs.getString("Color"));

                if (type.equals("Parallelogram")) {
                    Vector v1 = Vector.FromString(rs.getString("Vec1"));
                    Vector v2 = Vector.FromString(rs.getString("Vec2"));
                    result.add(new Parallelogram(v1, v2, name, color));
                }
            }
        }
        return result;
    }

    public void SetAll(List<IFlatFigure> figures) throws SQLException{
        try (Connection c = DriverManager.getConnection(_conString)) {
            c.setAutoCommit(false);

            try (Statement st = c.createStatement()) {
                st.executeUpdate("DELETE FROM json");
                st.executeUpdate("DELETE FROM figures");
            }

            String insertFigureRaw = "INSERT INTO figures(Type, Name, Color, Vec1, Vec2) VALUES (?,?,?,?,?)";
            String insertBlobRaw = "INSERT INTO json(id, Data) VALUES (?,?)";

            try (PreparedStatement fs = c.prepareStatement(insertFigureRaw);
                PreparedStatement ss = c.prepareStatement(insertBlobRaw)) {
                for (IFlatFigure f : figures) {
                    fs.setString(1, f.GetType());
                    fs.setString(2, f.GetName());
                    fs.setString(3, ColorUtils.ToString(f.GetColor()));

                    if (f instanceof Parallelogram p) {
                        fs.setString(4, p.GetFirstVector().toString());
                        fs.setString(5, p.GetSecondVector().toString());
                    } else {
                        fs.setNull(4, Types.VARCHAR);
                        fs.setNull(5, Types.VARCHAR);
                    }
                    
                    fs.executeUpdate();

                    ResultSet key = fs.getGeneratedKeys();
                    int generatedId = key.getInt(1);

                    byte[] serialized = f.ToJSON().toString().getBytes();

                    ss.setInt(1, generatedId);
                    ss.setBytes(2, serialized);

                    ss.executeUpdate();
                }
            }

            c.commit();
        }
    }
}
