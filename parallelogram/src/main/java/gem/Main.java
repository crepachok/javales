package gem;

import javax.swing.*;
import java.awt.*;
import org.json.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;

public class Main {

    private static class ShapeRow {
        final IFlatFigure figure;
        final JCheckBox   checkBox;
        ShapeRow(IFlatFigure figure, JCheckBox checkBox) {
            this.figure = figure;
            this.checkBox = checkBox;
        }
    }

    private final JFrame frame = new JFrame("Фигуры");
    private final JPanel shapesPanel = new JPanel();
    private final JPanel viewPanel   = new JPanel();
    private final List<ShapeRow> rows = new ArrayList<>();

    public Main() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel topPanel  = new JPanel(new BorderLayout());
        JPanel topLeft   = new JPanel(new FlowLayout(FlowLayout.LEFT,  8, 8));
        JPanel topRight  = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));

        JButton openBtn  = new JButton("Открыть");
        JButton saveBtn  = new JButton("Сохранить");
        JButton closeBtn = new JButton("Закрыть");
        JButton exitBtn  = new JButton("Выйти");

        topLeft.add(openBtn);
        topLeft.add(saveBtn);
        topLeft.add(closeBtn);
        topRight.add(exitBtn);
        topPanel.add(topLeft,  BorderLayout.WEST);
        topPanel.add(topRight, BorderLayout.EAST);

        shapesPanel.setLayout(new BoxLayout(shapesPanel, BoxLayout.Y_AXIS));
        shapesPanel.setBackground(Color.WHITE);

        JScrollPane leftScroll = new JScrollPane(shapesPanel);
        leftScroll.setBorder(BorderFactory.createTitledBorder("Загруженные фигуры"));

        viewPanel.setBackground(new Color(245, 245, 245));
        viewPanel.setBorder(BorderFactory.createTitledBorder(""));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          leftScroll, viewPanel);
        split.setResizeWeight(0.5);
        split.setDividerLocation(450);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(split,    BorderLayout.CENTER);

        openBtn.addActionListener(e -> onOpen());
        saveBtn.addActionListener(e -> onSave());
        closeBtn.addActionListener(e -> onClose());
        exitBtn.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    private void onSave(){
        List<JSONObject> toSerialize = new ArrayList<>();

        for (ShapeRow row : rows){
            if (!row.checkBox.isSelected())
                continue;

            IFlatFigure fig = row.figure;

            JSONObject json = new JSONObject();
            json.put("Type", fig.GetType());
            json.put("Area", fig.GetArea());
            json.put("Perimeter", fig.GetPerimeter());
            json.put("Name", fig.GetName());
            json.put("Color", fig.GetColor().getRGB());

            if(fig instanceof Parallelogram par){
                json.put("VectorF", par.GetFirstVector().toString());
                json.put("VectorS", par.GetSecondVector().toString());
            }

            toSerialize.add(json);
        }

        JSONArray arr = new JSONArray(toSerialize);
        
        try{
            Files.writeString(Path.of("figures.json"), arr.toString(2), StandardCharsets.UTF_8);
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void onOpen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выберите файл с фигурами");
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        clearShapes();

        try {
            GeometryParser parser = new GeometryParser(file.getAbsolutePath());

            IFlatFigure f;
            while ((f = parser.ReadFigure()) != null) {
                addShape(f);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Ошибка чтения файла: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onClose() {
        clearShapes();
    }

    private void addShape(IFlatFigure figure) {
        JCheckBox cb = new JCheckBox();
        cb.setSelected(true);
        cb.setOpaque(false);
        cb.setForeground(figure.GetColor() != null ? figure.GetColor() : Color.BLACK);

        JLabel label = new JLabel(String.format(
                "%s — %s, периметр: %.2f",
                figure.GetName(), figure.GetType(), figure.GetPerimeter()));
        label.setForeground(figure.GetColor() != null ? figure.GetColor() : Color.BLACK);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.setOpaque(false);
        row.add(cb);
        row.add(label);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        shapesPanel.add(row);
        rows.add(new ShapeRow(figure, cb));

        shapesPanel.revalidate();
        shapesPanel.repaint();
    }

    private void clearShapes() {
        shapesPanel.removeAll();
        rows.clear();
        shapesPanel.revalidate();
        shapesPanel.repaint();
        viewPanel.repaint();
    }

    public List<IFlatFigure> getSelectedFigures() {
        List<IFlatFigure> result = new ArrayList<>();
        for (ShapeRow r : rows) {
            if (r.checkBox.isSelected()) result.add(r.figure);
        }
        return result;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}