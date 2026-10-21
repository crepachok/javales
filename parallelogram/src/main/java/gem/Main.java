package gem;

import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    private final DbContext context = new DbContext("jdbc:sqlite:app.db");

    public Main() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel topPanel  = new JPanel(new BorderLayout());
        JPanel topLeft   = new JPanel(new FlowLayout(FlowLayout.LEFT,  8, 8));
        JPanel topRight  = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));

        JButton openBtn  = new JButton("Открыть json");
        JButton saveBtn  = new JButton("Сохранить json");
        JButton openDbBtn  = new JButton("Открыть из бд");
        JButton saveDbBtn  = new JButton("Сохранить в бд");
        JButton closeBtn = new JButton("Закрыть");
        JButton exitBtn  = new JButton("Выйти");

        topLeft.add(openBtn);
        topLeft.add(openDbBtn);
        topLeft.add(saveBtn);
        topLeft.add(saveDbBtn);
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
        openDbBtn.addActionListener(e -> onDbOpen());
        saveBtn.addActionListener(e -> onSave());
        saveDbBtn.addActionListener(e -> onDbSave());
        closeBtn.addActionListener(e -> onClose());
        exitBtn.addActionListener(e -> System.exit(0));

        frame.setVisible(true);

        try{
            context.InitDb();
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(frame,
                    "Ошибка инициализации бд: " + ex.getMessage());
        }
    }

    private void onDbOpen(){
        clearShapes();

        try{
            List<IFlatFigure> figures = context.GetAll();

            for (IFlatFigure figure : figures){
                addShape(figure);
            }
        } catch (Exception ex){
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }
    }

    private void onDbSave(){
        try{
            context.SetAll(getSelectedFigures());

            JOptionPane.showMessageDialog(frame, "Фигуры сохранены в базу данных!");
        }   catch (Exception ex){
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }
    }

    private void onOpen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выберите файл с фигурами");
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        clearShapes();

        try {
            String json = Files.readString(file.toPath());

            JSONArray figures = new JSONObject(json).getJSONArray("Figures");
            for (int i = 0; i < figures.length(); i++){
                JSONObject figure = figures.getJSONObject(i);

                addShape(JsonFigureFactory.Create(figure));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Ошибка чтения файла: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSave(){
        List<IFlatFigure> selected = getSelectedFigures();

        JSONArray arr = new JSONArray();
        for (IFlatFigure f : selected) {
            arr.put(f.ToJSON());
        }

        JSONObject json = new JSONObject();
        json.put("Figures", arr);

        try {
            Files.writeString(Path.of("saved.json"), json.toString(2), StandardCharsets.UTF_8);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Ошибка: " + e.getMessage());
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