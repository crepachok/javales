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
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    Type  VARCHAR NOT NULL,
                    Name  VARCHAR NOT NULL,
                    Color VARCHAR NOT NULL,
                    Vec1  VARCHAR NULL,
                    Vec2  VARCHAR NULL
                );
                """;

            String createSecond = """
                CREATE TABLE IF NOT EXISTS json (
                    id   INTEGER PRIMARY KEY REFERENCES figures(id),
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

                if ("Parallelogram".equals(type)) {
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
                st.executeUpdate("DELETE FROM figures");
            }

            String sql = "INSERT INTO figures(Type, Name, Color, Vec1, Vec2) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (IFlatFigure f : figures) {
                    ps.setString(1, f.GetType());
                    ps.setString(2, f.GetName());
                    ps.setString(3, ColorUtils.ToString(f.GetColor()));

                    if (f instanceof Parallelogram p) {
                        ps.setString(4, p.GetFirstVector().toString());
                        ps.setString(5, p.GetSecondVector().toString());
                    } else {
                        ps.setNull(4, Types.VARCHAR);
                        ps.setNull(5, Types.VARCHAR);
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();
        }
    }
}
