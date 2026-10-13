package gem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    // --- связка "фигура + её чекбокс" ---
    private static class ShapeRow {
        final IFlatFigure figure;
        final JCheckBox   checkBox;
        ShapeRow(IFlatFigure figure, JCheckBox checkBox) {
            this.figure = figure;
            this.checkBox = checkBox;
        }
    }

    private final JFrame frame = new JFrame("Фигуры");
    private final JPanel shapesPanel = new JPanel();     // список фигур (левая половина)
    private final JPanel viewPanel   = new JPanel();     // правая половина
    private final List<ShapeRow> rows = new ArrayList<>();

    public Main() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // ---------- Верхняя панель: 3 кнопки ----------
        JPanel topPanel  = new JPanel(new BorderLayout());
        JPanel topLeft   = new JPanel(new FlowLayout(FlowLayout.LEFT,  8, 8));
        JPanel topRight  = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));

        JButton openBtn  = new JButton("Открыть");
        JButton closeBtn = new JButton("Закрыть");
        JButton exitBtn  = new JButton("Выйти");

        topLeft.add(openBtn);
        topLeft.add(closeBtn);
        topRight.add(exitBtn);
        topPanel.add(topLeft,  BorderLayout.WEST);
        topPanel.add(topRight, BorderLayout.EAST);

        // ---------- Левая половина: список фигур ----------
        shapesPanel.setLayout(new BoxLayout(shapesPanel, BoxLayout.Y_AXIS));
        shapesPanel.setBackground(Color.WHITE);

        JScrollPane leftScroll = new JScrollPane(shapesPanel);
        leftScroll.setBorder(BorderFactory.createTitledBorder("Загруженные фигуры"));

        // ---------- Правая половина ----------
        viewPanel.setBackground(new Color(245, 245, 245));
        viewPanel.setBorder(BorderFactory.createTitledBorder(""));

        // ---------- Разделитель ----------
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          leftScroll, viewPanel);
        split.setResizeWeight(0.5);
        split.setDividerLocation(450);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(split,    BorderLayout.CENTER);

        // ---------- Обработчики ----------
        openBtn.addActionListener(e -> onOpen());
        closeBtn.addActionListener(e -> onClose());
        exitBtn.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    // =================== Обработчики кнопок ===================

    /** Открыть: выбор файла + чтение через GeometryParser. */
    private void onOpen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выберите файл с фигурами");
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        // очищаем предыдущий список
        clearShapes();

        try {
            GeometryParser parser = new GeometryParser(file.getAbsolutePath());

            // читаем фигуры, пока ReadFigure не вернёт null
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

    /** Закрыть: очищаем список фигур. */
    private void onClose() {
        clearShapes();
    }

    // =================== Работа со списком ===================

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
        // растягиваем по ширине контейнера
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        shapesPanel.add(row);
        rows.add(new ShapeRow(figure, cb));

        shapesPanel.revalidate();
        shapesPanel.repaint();

        // Если хочешь реагировать на изменение чекбокса в правой панели — раскомментируй:
        // cb.addActionListener(e -> viewPanel.repaint());
    }

    private void clearShapes() {
        shapesPanel.removeAll();
        rows.clear();
        shapesPanel.revalidate();
        shapesPanel.repaint();
        viewPanel.repaint();   // если правая панель что-то рисует
    }

    /** Отмеченные чекбоксами фигуры — пригодятся в правой панели. */
    public List<IFlatFigure> getSelectedFigures() {
        List<IFlatFigure> result = new ArrayList<>();
        for (ShapeRow r : rows) {
            if (r.checkBox.isSelected()) result.add(r.figure);
        }
        return result;
    }

    // =================== Точка входа ===================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}