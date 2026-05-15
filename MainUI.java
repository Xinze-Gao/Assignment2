import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Game Main UI - Pure block style
 * Calls all GameManager interfaces
 */
public class MainUI {
    private GameManager gm;
    private JFrame frame;

    // Left panel status components
    private JLabel dayLabel;
    private JProgressBar gpaBar;
    private JLabel gpaValueLabel;
    private JProgressBar mentalBar;
    private JLabel mentalValueLabel;
    private JProgressBar happyBar;
    private JLabel happyValueLabel;
    private JLabel actionsLabel;

    // Center played cards panel
    private JPanel playedPanel;

    // Right Buff panel
    private JPanel buffPanel;

    // Bottom hand cards (5 buttons)
    private JButton[] handButtons;

    // End turn button
    private JButton endTurnButton;

    // Status message
    private JLabel statusLabel;

    // Color definitions
    private static final Color COLOR_GPA = new Color(76, 175, 80);
    private static final Color COLOR_MENTAL = new Color(33, 150, 243);
    private static final Color COLOR_HAPPY = new Color(255, 193, 7);
    private static final Color COLOR_HAND = new Color(103, 58, 183);
    private static final Color COLOR_PLAYED = new Color(121, 85, 72);
    private static final Color COLOR_BG = new Color(245, 245, 245);

    public MainUI() {
        gm = new GameManager();
        gm.startNewGame();
        handButtons = new JButton[5];
        initUI();
        refreshUI();
    }

    private void initUI() {
        frame = new JFrame("Campus Survivor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(COLOR_BG);

        // ========== Top Panel: Day + Event Hint ==========
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(63, 81, 181));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        dayLabel = new JLabel();
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dayLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("Game in Progress");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(255, 235, 59));

        topPanel.add(dayLabel, BorderLayout.WEST);
        topPanel.add(statusLabel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        // ========== Center: Left Status + Middle Played + Right Buff ==========
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        centerPanel.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.25;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(createLeftPanel(), gbc);

        gbc.weightx = 0.5;
        gbc.gridx = 1;
        centerPanel.add(createPlayedPanel(), gbc);

        gbc.weightx = 0.25;
        gbc.gridx = 2;
        centerPanel.add(createBuffPanel(), gbc);

        frame.add(centerPanel, BorderLayout.CENTER);

        // ========== Bottom: Hand Cards + End Turn Button ==========
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        bottomPanel.setBackground(COLOR_BG);

        // Hand cards area
        JPanel handPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        handPanel.setBackground(COLOR_BG);
        handPanel.setBorder(BorderFactory.createTitledBorder("Hand Cards"));

        for (int i = 0; i < handButtons.length; i++) {
            JButton btn = new JButton();
            btn.setBackground(COLOR_HAND);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    BorderFactory.createEmptyBorder(20, 10, 20, 10)
            ));
            final int index = i;
            btn.addActionListener(e -> playCardAndRefresh(index));
            handButtons[i] = btn;
            handPanel.add(btn);
        }
        bottomPanel.add(handPanel, BorderLayout.CENTER);

        // End turn button
        endTurnButton = new JButton("End Turn");
        endTurnButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        endTurnButton.setBackground(new Color(244, 67, 54));
        endTurnButton.setForeground(Color.WHITE);
        endTurnButton.setFocusPainted(false);
        endTurnButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        endTurnButton.addActionListener(e -> endTurnAndRefresh());
        bottomPanel.add(endTurnButton, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(BorderFactory.createTitledBorder("Status"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        // GPA bar
        gbc.gridy = 0;
        JLabel gpaLabel = new JLabel("GPA");
        gpaLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(gpaLabel, gbc);

        gbc.gridy = 1;
        gpaBar = new JProgressBar(0, 100);
        gpaBar.setForeground(COLOR_GPA);
        gpaBar.setStringPainted(true);
        panel.add(gpaBar, gbc);

        gbc.gridy = 2;
        gpaValueLabel = new JLabel("0.0");
        panel.add(gpaValueLabel, gbc);

        // Mental bar
        gbc.gridy = 3;
        JLabel mentalLabel = new JLabel("Mental");
        mentalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(mentalLabel, gbc);

        gbc.gridy = 4;
        mentalBar = new JProgressBar(0, 100);
        mentalBar.setForeground(COLOR_MENTAL);
        mentalBar.setStringPainted(true);
        panel.add(mentalBar, gbc);

        gbc.gridy = 5;
        mentalValueLabel = new JLabel("0");
        panel.add(mentalValueLabel, gbc);

        // Happiness bar
        gbc.gridy = 6;
        JLabel happyLabel = new JLabel("Happiness");
        happyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(happyLabel, gbc);

        gbc.gridy = 7;
        happyBar = new JProgressBar(0, 100);
        happyBar.setForeground(COLOR_HAPPY);
        happyBar.setStringPainted(true);
        panel.add(happyBar, gbc);

        gbc.gridy = 8;
        happyValueLabel = new JLabel("0");
        panel.add(happyValueLabel, gbc);

        // Actions left
        gbc.gridy = 9;
        actionsLabel = new JLabel("Actions Left: 0");
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(actionsLabel, gbc);

        // Fill empty space
        gbc.weighty = 1.0;
        gbc.gridy = 10;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createPlayedPanel() {
        playedPanel = new JPanel();
        playedPanel.setLayout(new BoxLayout(playedPanel, BoxLayout.Y_AXIS));
        playedPanel.setBackground(COLOR_BG);
        playedPanel.setBorder(BorderFactory.createTitledBorder("Played This Turn"));
        return playedPanel;
    }

    private JPanel createBuffPanel() {
        buffPanel = new JPanel();
        buffPanel.setLayout(new BoxLayout(buffPanel, BoxLayout.Y_AXIS));
        buffPanel.setBackground(COLOR_BG);
        buffPanel.setBorder(BorderFactory.createTitledBorder("Buffs"));

        JLabel placeholder = new JLabel("No Buffs");
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        buffPanel.add(placeholder);

        return buffPanel;
    }

    private void playCardAndRefresh(int index) {
        if (gm.isGameOver()) {
            statusLabel.setText("Game Over. Please restart.");
            return;
        }

        boolean success = gm.playCard(index);
        if (success) {
            statusLabel.setText("Card played successfully!");
            refreshUI();
        } else {
            statusLabel.setText("Not enough actions or invalid card!");
            JOptionPane.showMessageDialog(frame, "Not enough actions or invalid card!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void endTurnAndRefresh() {
        if (gm.isGameOver()) {
            statusLabel.setText("Game Over");
            return;
        }

        gm.endTurn();
        refreshUI();

        if (gm.isGameOver()) {
            if (gm.isGameWon()) {
                statusLabel.setText("🎉 VICTORY! GPA target reached 🎉");
                JOptionPane.showMessageDialog(frame, "Congratulations! You survived the semester!\nFinal GPA: " + String.format("%.1f", gm.getPlayerState().getGpa()), "Victory", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLabel.setText("💀 GAME OVER 💀");
                String reason = "";
                if (gm.getPlayerState().isMentalDepleted()) reason = "Mental collapse";
                else if (gm.getPlayerState().isGPAFailed()) reason = "GPA too low - Academic dismissal";
                else reason = "Semester ended - Target not reached";
                JOptionPane.showMessageDialog(frame, "Game Over\nReason: " + reason, "Defeat", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            statusLabel.setText("Entering Day " + gm.getCurrentDay());
        }
    }

    private void refreshUI() {
        PlayerState state = gm.getPlayerState();

        // Update top day display
        dayLabel.setText("Day " + state.getCurrentDay() + " | " + gm.getDaysRemaining() + " days left");

        // Update status bars
        float gpa = state.getGpa();
        int gpaPercent = (int)(gpa / 4.0f * 100);
        gpaBar.setValue(gpaPercent);
        gpaValueLabel.setText(String.format("%.2f / 4.0", gpa));

        int mental = state.getMental();
        mentalBar.setValue(mental);
        mentalValueLabel.setText(mental + " / 100");

        int happy = state.getHappiness();
        happyBar.setValue(happy);
        happyValueLabel.setText(happy + " / 100");

        actionsLabel.setText("Actions Left: " + state.getActionsLeft());

        // Update hand cards
        List<Card> hand = gm.getHandCards();
        for (int i = 0; i < handButtons.length; i++) {
            if (i < hand.size()) {
                Card card = hand.get(i);
                handButtons[i].setText("<html><center>" + card.getName() + "<br>Cost:" + card.getCost() + "</center></html>");
                handButtons[i].setEnabled(true);
                handButtons[i].setToolTipText(card.getDescription() + " | GPA:" + card.getGpaEffect() + " Mental:" + card.getMentalEffect() + " Happy:" + card.getHappyEffect());
            } else {
                handButtons[i].setText("Empty");
                handButtons[i].setEnabled(false);
                handButtons[i].setToolTipText(null);
            }
        }

        // Update played cards area
        playedPanel.removeAll();
        List<Card> played = gm.getPlayedCards();
        if (played.isEmpty()) {
            JLabel emptyLabel = new JLabel("No cards played yet");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playedPanel.add(emptyLabel);
        } else {
            for (Card card : played) {
                JLabel cardLabel = new JLabel(card.getName());
                cardLabel.setOpaque(true);
                cardLabel.setBackground(COLOR_PLAYED);
                cardLabel.setForeground(Color.WHITE);
                cardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                cardLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                cardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                playedPanel.add(cardLabel);
                playedPanel.add(Box.createVerticalStrut(5));
            }
        }
        playedPanel.revalidate();
        playedPanel.repaint();

        // Update buff area
        List<Buff> buffs = gm.getActiveBuffs();
        buffPanel.removeAll();
        if (buffs.isEmpty()) {
            JLabel placeholder = new JLabel("No Buffs");
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            buffPanel.add(placeholder);
        } else {
            for (Buff buff : buffs) {
                JLabel buffLabel = new JLabel(buff.toString());
                buffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                buffPanel.add(buffLabel);
            }
        }
        buffPanel.revalidate();
        buffPanel.repaint();

        // Disable buttons if game over
        if (gm.isGameOver()) {
            for (JButton btn : handButtons) {
                btn.setEnabled(false);
            }
            endTurnButton.setEnabled(false);
        } else {
            for (JButton btn : handButtons) {
                btn.setEnabled(true);
            }
            endTurnButton.setEnabled(true);
        }

        frame.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainUI();
        });
    }
}