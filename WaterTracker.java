import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WaterTracker extends JFrame {
    private int totalIntake = 0;
    private int dailyGoal = 2000;
    private int streakCount = 0;
    private JLabel intakeLabel, goalLabel, streakLabel, statsLabel;
    private JTextField amountField, goalField, reminderField;
    private JProgressBar progressBar;
    private JTextArea historyArea;
    private ArrayList<String> intakeHistory;
    private Timer reminderTimer;

    public WaterTracker() {
        setTitle("Advanced Water Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        intakeHistory = new ArrayList<>();

        // North Panel: Goal Setting and Reminder
        JPanel northPanel = new JPanel(new FlowLayout());
        goalLabel = new JLabel("Daily Goal: " + dailyGoal + " ml");
        goalField = new JTextField("2000", 10);
        JButton setGoalButton = new JButton("Set Goal");
        reminderField = new JTextField("30", 5);
        JButton setReminderButton = new JButton("Set Reminder (min)");
        northPanel.add(new JLabel("Set Daily Goal (ml):"));
        northPanel.add(goalField);
        northPanel.add(setGoalButton);
        northPanel.add(goalLabel);
        northPanel.add(new JLabel("Reminder Interval:"));
        northPanel.add(reminderField);
        northPanel.add(setReminderButton);
        add(northPanel, BorderLayout.NORTH);

        // Center Panel: Intake, Progress, and Stats
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel intakePanel = new JPanel(new FlowLayout());
        intakeLabel = new JLabel("Total Intake: 0 ml");
        amountField = new JTextField(10);
        JButton addButton = new JButton("Add Intake");
        progressBar = new JProgressBar(0, dailyGoal);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        streakLabel = new JLabel("Goal Streak: 0 days");
        statsLabel = new JLabel("Stats: 0% of goal");
        intakePanel.add(new JLabel("Enter amount (ml):"));
        intakePanel.add(amountField);
        intakePanel.add(addButton);
        intakePanel.add(intakeLabel);
        intakePanel.add(progressBar);
        intakePanel.add(streakLabel);
        intakePanel.add(statsLabel);
        centerPanel.add(intakePanel, BorderLayout.NORTH);

        // History Area
        historyArea = new JTextArea(10, 30);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // South Panel: Reset
        JPanel southPanel = new JPanel(new FlowLayout());
        JButton resetButton = new JButton("Reset Daily Intake");
        southPanel.add(resetButton);
        add(southPanel, BorderLayout.SOUTH);

        // Action Listeners
        setGoalButton.addActionListener(e -> {
            try {
                dailyGoal = Integer.parseInt(goalField.getText());
                goalLabel.setText("Daily Goal: " + dailyGoal + " ml");
                progressBar.setMaximum(dailyGoal);
                updateProgressBar();
                updateStats();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid goal!");
            }
        });

        setReminderButton.addActionListener(e -> {
            try {
                int minutes = Integer.parseInt(reminderField.getText());
                if (reminderTimer != null) reminderTimer.stop();
                reminderTimer = new Timer(minutes * 60 * 1000, ev -> {
                    JOptionPane.showMessageDialog(null, "Time to drink water!");
                });
                reminderTimer.start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number for reminder interval!");
            }
        });

        addButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                totalIntake += amount;
                intakeLabel.setText("Total Intake: " + totalIntake + " ml");
                updateProgressBar();
                updateStats();
                addToHistory(amount);
                amountField.setText("");
                if (totalIntake >= dailyGoal) {
                    streakCount++;
                    streakLabel.setText("Goal Streak: " + streakCount + " days");
                    JOptionPane.showMessageDialog(null, "Goal of " + dailyGoal + "ml reached! Streak: " + streakCount);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number!");
            }
        });

        resetButton.addActionListener(e -> {
            totalIntake = 0;
            intakeLabel.setText("Total Intake: 0 ml");
            intakeHistory.clear();
            historyArea.setText("");
            updateProgressBar();
            updateStats();
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void updateProgressBar() {
        progressBar.setValue(totalIntake);
        progressBar.setString(totalIntake + "/" + dailyGoal + " ml");
    }

    private void updateStats() {
        double percentage = (double) totalIntake / dailyGoal * 100;
        statsLabel.setText(String.format("Stats: %.1f%% of goal", percentage));
    }

    private void addToHistory(int amount) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = timestamp + ": Added " + amount + " ml\n";
        intakeHistory.add(entry);
        historyArea.append(entry);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WaterTracker tracker = new WaterTracker();
            tracker.setVisible(true);
        });
    }
}