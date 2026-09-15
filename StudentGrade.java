import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentGrade extends JFrame {

    private static class Student {
        String name;
        double grade;
        Student(String name, double grade) {
            this.name = name;
            this.grade = grade;
        }
    }

    private final ArrayList<Student> studentList = new ArrayList<>();

    private JTextField nameField, gradeField;
    private DefaultTableModel tableModel;
    private JLabel avgValue, highestValue, lowestValue;
    private JTextArea reportArea;

    public StudentGrade() {
        setTitle("Student Grade Tracker");
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField(12);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Score (0-100):"));
        gradeField = new JTextField(5);
        inputPanel.add(gradeField);

        JButton addButton = new JButton("Add Student");
        JButton clearButton = new JButton("Clear All");
        inputPanel.add(addButton);
        inputPanel.add(clearButton);
        add(inputPanel, BorderLayout.NORTH);

        String[] columnNames = {"Student Name", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.add(createStatBox("Average", avgValue = new JLabel("N/A")));
        statsPanel.add(createStatBox("Highest", highestValue = new JLabel("N/A")));
        statsPanel.add(createStatBox("Lowest", lowestValue = new JLabel("N/A")));
        bottomPanel.add(statsPanel, BorderLayout.NORTH);

        reportArea = new JTextArea(10, 40);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        bottomPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addStudent());
        clearButton.addActionListener(e -> clearAll());

        updateSummaryReport();
    }

    private JPanel createStatBox(String label, JLabel valueLabel) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBorder(BorderFactory.createTitledBorder(label));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        box.add(valueLabel, BorderLayout.CENTER);
        return box;
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String scoreText = gradeField.getText().trim();

        if (name.isEmpty() || scoreText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both name and score.");
            return;
        }

        try {
            double score = Double.parseDouble(scoreText);
            if (score < 0 || score > 100) {
                JOptionPane.showMessageDialog(this, "Score must be between 0 and 100.");
                return;
            }

            studentList.add(new Student(name, score));
            tableModel.addRow(new Object[]{name, score});

            calculateStatistics();
            updateSummaryReport();

            nameField.setText("");
            gradeField.setText("");
            nameField.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric score.");
        }
    }

    private void calculateStatistics() {
        if (studentList.isEmpty()) {
            avgValue.setText("N/A");
            highestValue.setText("N/A");
            lowestValue.setText("N/A");
            return;
        }

        double sum = 0;
        double highest = studentList.get(0).grade;
        double lowest = studentList.get(0).grade;

        for (Student s : studentList) {
            sum += s.grade;
            if (s.grade > highest) highest = s.grade;
            if (s.grade < lowest) lowest = s.grade;
        }

        double average = sum / studentList.size();

        avgValue.setText(String.format("%.2f", average));
        highestValue.setText(String.format("%.2f", highest));
        lowestValue.setText(String.format("%.2f", lowest));
    }

    private void updateSummaryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("======= STUDENT GRADE SUMMARY REPORT =======\n");
        sb.append(String.format("%-20s %s\n", "Name", "Grade"));
        sb.append("----------------------------------------------\n");

        if (studentList.isEmpty()) {
            sb.append("No student records available.\n");
        } else {
            for (Student s : studentList) {
                sb.append(String.format("%-20s %.2f\n", s.name, s.grade));
            }
            sb.append("----------------------------------------------\n");
            sb.append("Total Students : ").append(studentList.size()).append("\n");
            sb.append("Average Score  : ").append(avgValue.getText()).append("\n");
            sb.append("Highest Score  : ").append(highestValue.getText()).append("\n");
            sb.append("Lowest Score   : ").append(lowestValue.getText()).append("\n");
        }
        sb.append("================================================\n");

        reportArea.setText(sb.toString());
    }

    private void clearAll() {
        studentList.clear();
        tableModel.setRowCount(0);
        calculateStatistics();
        updateSummaryReport();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGrade().setVisible(true));
    }
}