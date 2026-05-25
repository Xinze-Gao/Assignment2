import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Game Main UI - Professional Styled Version
 * Features: Gradient backgrounds, rounded cards, shadows, modern fonts
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

    // Modern color scheme
    private static final Color COLOR_BG_DARK = new Color(18, 25, 35);
    private static final Color COLOR_BG_LIGHT = new Color(30, 40, 55);
    private static final Color COLOR_CARD = new Color(45, 55, 75);
    private static final Color COLOR_CARD_HOVER = new Color(60, 75, 100);
    private static final Color COLOR_GPA = new Color(76, 175, 80);
    private static final Color COLOR_MENTAL = new Color(33, 150, 243);
    private static final Color COLOR_HAPPY = new Color(255, 193, 7);
    private static final Color COLOR_ACCENT = new Color(100, 200, 100);
    private static final Color COLOR_BUTTON = new Color(244, 67, 54);
    private static final Color COLOR_TEXT = new Color(240, 240, 245);

    public MainUI() {
        handButtons = new JButton[5];
        showStartMenu();
    }

    // ==================== START MENU ====================

    private void showStartMenu() {
        if (frame == null) {
            frame = new JFrame("🎓 CAMPUS SURVIVOR");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setMinimumSize(new Dimension(1024, 600));
        }

        frame.getContentPane().removeAll();

        JPanel menuPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COLOR_BG_DARK, getWidth(), getHeight(), COLOR_BG_LIGHT);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        menuPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title with shadow effect
        JLabel titleLabel = new JLabel("🎓 CAMPUS SURVIVOR 🎓");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("A Roguelike Deck-Building Game");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        subtitleLabel.setForeground(COLOR_TEXT);
        gbc.gridy = 1;
        menuPanel.add(subtitleLabel, gbc);

        // New Game button
        JButton newGameBtn = createStyledButton("🆕 NEW GAME", new Color(76, 175, 80), 22);
        newGameBtn.addActionListener(e -> startNewGame());
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 50, 15, 50);
        menuPanel.add(newGameBtn, gbc);

        // Continue button
        JButton continueBtn = createStyledButton("📂 CONTINUE", new Color(33, 150, 243), 22);
        continueBtn.addActionListener(e -> {
            loadSavedGame();
        });
        if (!SaveManager.saveExists()) {
            continueBtn.setEnabled(false);
            continueBtn.setBackground(Color.GRAY);
            continueBtn.setToolTipText("No saved game found");
        }
        gbc.gridy = 3;
        menuPanel.add(continueBtn, gbc);

        // Exit button
        JButton exitBtn = createStyledButton("❌ EXIT", new Color(100, 100, 100), 18);
        exitBtn.addActionListener(e -> System.exit(0));
        gbc.gridy = 4;
        menuPanel.add(exitBtn, gbc);

        frame.add(menuPanel);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color color, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    private void startNewGame() {
        gm = new GameManager();
        gm.startNewGame();
        initUI();
        refreshUI();
    }

    private void loadSavedGame() {
        GameManager loaded = SaveManager.load();
        if (loaded != null) {
            gm = loaded;
            initUI();
            refreshUI();
        } else {
            JOptionPane.showMessageDialog(frame, "No save file found!", "Error", JOptionPane.ERROR_MESSAGE);
            showStartMenu();
        }
    }

    // ==================== MAIN GAME UI ====================

    private void initUI() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout(10, 10));

        // Create gradient background panel
        JPanel bgPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COLOR_BG_DARK, getWidth(), getHeight(), COLOR_BG_LIGHT);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setOpaque(false);
        bgPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel topPanel = createTopPanel();
        bgPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = createCenterPanel();
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = createBottomPanel();
        bgPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(bgPanel);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        dayLabel = new JLabel();
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        dayLabel.setForeground(COLOR_TEXT);
        dayLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 2, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        statusLabel = new JLabel("Game in Progress");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(255, 235, 59));

        JButton saveButton = new JButton("💾 SAVE");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> showSaveConfirmDialog());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(statusLabel);
        rightPanel.add(saveButton);

        topPanel.add(dayLabel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.28;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(createLeftPanel(), gbc);

        gbc.weightx = 0.44;
        gbc.gridx = 1;
        centerPanel.add(createPlayedPanel(), gbc);

        gbc.weightx = 0.28;
        gbc.gridx = 2;
        centerPanel.add(createBuffPanel(), gbc);

        return centerPanel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 2, true),
                "📊 STATISTICS",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                COLOR_TEXT
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.gridx = 0;

        // GPA
        gbc.gridy = 0;
        JLabel gpaLabel = new JLabel("🎯 GPA");
        gpaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gpaLabel.setForeground(COLOR_GPA);
        panel.add(gpaLabel, gbc);

        gbc.gridy = 1;
        gpaBar = createStyledProgressBar(COLOR_GPA);
        panel.add(gpaBar, gbc);

        gbc.gridy = 2;
        gpaValueLabel = new JLabel("0.0 / 4.0");
        gpaValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gpaValueLabel.setForeground(COLOR_TEXT);
        panel.add(gpaValueLabel, gbc);

        // Mental
        gbc.gridy = 3;
        JLabel mentalLabel = new JLabel("🧠 MENTAL");
        mentalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mentalLabel.setForeground(COLOR_MENTAL);
        panel.add(mentalLabel, gbc);

        gbc.gridy = 4;
        mentalBar = createStyledProgressBar(COLOR_MENTAL);
        panel.add(mentalBar, gbc);

        gbc.gridy = 5;
        mentalValueLabel = new JLabel("0 / 100");
        mentalValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mentalValueLabel.setForeground(COLOR_TEXT);
        panel.add(mentalValueLabel, gbc);

        // Happiness
        gbc.gridy = 6;
        JLabel happyLabel = new JLabel("😊 HAPPINESS");
        happyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        happyLabel.setForeground(COLOR_HAPPY);
        panel.add(happyLabel, gbc);

        gbc.gridy = 7;
        happyBar = createStyledProgressBar(COLOR_HAPPY);
        panel.add(happyBar, gbc);

        gbc.gridy = 8;
        happyValueLabel = new JLabel("0 / 100");
        happyValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        happyValueLabel.setForeground(COLOR_TEXT);
        panel.add(happyValueLabel, gbc);

        // Actions
        gbc.gridy = 9;
        actionsLabel = new JLabel("⚡ ACTIONS: 0");
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        actionsLabel.setForeground(COLOR_ACCENT);
        actionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(actionsLabel, gbc);

        gbc.weighty = 1.0;
        gbc.gridy = 10;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JProgressBar createStyledProgressBar(Color color) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setForeground(color);
        bar.setBackground(new Color(60, 70, 90));
        bar.setStringPainted(true);
        bar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        return bar;
    }

    private JPanel createPlayedPanel() {
        playedPanel = new JPanel();
        playedPanel.setLayout(new BoxLayout(playedPanel, BoxLayout.Y_AXIS));
        playedPanel.setOpaque(false);
        playedPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 2, true),
                "🎴 PLAYED THIS TURN",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                COLOR_TEXT
        ));
        return playedPanel;
    }

    private JPanel createBuffPanel() {
        buffPanel = new JPanel();
        buffPanel.setLayout(new BoxLayout(buffPanel, BoxLayout.Y_AXIS));
        buffPanel.setOpaque(false);
        buffPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 2, true),
                "✨ ACTIVE BUFFS",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                COLOR_TEXT
        ));
        return buffPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Hand cards area
        JPanel handPanel = new JPanel(new GridLayout(1, 5, 12, 0));
        handPanel.setOpaque(false);
        handPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 2, true),
                "🃏 HAND CARDS",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_TEXT
        ));

        for (int i = 0; i < handButtons.length; i++) {
            JButton btn = createCardButton();
            final int index = i;
            btn.addActionListener(e -> playCardAndRefresh(index));
            handButtons[i] = btn;
            handPanel.add(btn);
        }
        bottomPanel.add(handPanel, BorderLayout.CENTER);

        // End turn button
        endTurnButton = new JButton("⏹️ END TURN");
        endTurnButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        endTurnButton.setBackground(COLOR_BUTTON);
        endTurnButton.setForeground(Color.WHITE);
        endTurnButton.setFocusPainted(false);
        endTurnButton.setBorder(BorderFactory.createEmptyBorder(15, 35, 15, 35));
        endTurnButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        endTurnButton.addActionListener(e -> endTurnAndRefresh());
        bottomPanel.add(endTurnButton, BorderLayout.EAST);

        return bottomPanel;
    }

    private JButton createCardButton() {
        JButton btn = new JButton();
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);

        // Custom rounded card look
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(3, 3, c.getWidth() - 1, c.getHeight() - 1, 15, 15);

                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, COLOR_CARD, 0, c.getHeight(), COLOR_CARD.darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 15, 15);

                // Border
                g2d.setColor(COLOR_ACCENT);
                g2d.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 15, 15);

                super.paint(g, c);
            }
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_CARD_HOVER);
                btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.repaint();
            }
        });

        return btn;
    }

    private String getCardIcon(Card card) {
        if (card.hasTag(Tag.STUDY)) return "📚";
        if (card.hasTag(Tag.REFRESH)) return "☕";
        if (card.hasTag(Tag.REST)) return "😴";
        if (card.hasTag(Tag.ENTERTAINMENT)) return "🎮";
        if (card.hasTag(Tag.HIGH_RISK)) return "⚠️";
        if (card.hasTag(Tag.AI)) return "🤖";
        if (card.hasTag(Tag.STAY_UP)) return "🌙";
        return "🃏";
    }

    // ==================== GAME ACTIONS ====================

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

        if (gm.isWaitingForEventResolution()) {
            showEventDialog();
        }

        if (gm.isWaitingForGrowthChoice()) {
            showGrowthChoiceDialog();
        }

        if (gm.isGameOver()) {
            String message = "";
            boolean isVictory = gm.isGameWon();

            if (isVictory) {
                message = "Congratulations! You survived the semester!\nYour GPA: " +
                        String.format("%.1f", gm.getPlayerState().getGpa());
            } else {
                String reason = "";
                if (gm.getPlayerState().isMentalDepleted()) {
                    reason = "Mental collapse - You burned out from stress.";
                } else if (gm.getPlayerState().isGPAFailed()) {
                    reason = "GPA too low - Academic dismissal.";
                } else {
                    reason = "Semester ended - GPA target not reached.";
                }
                message = "Game Over\n\n" + reason;
            }
            showGameOverMenu(isVictory, message);
        } else {
            statusLabel.setText("Entering Day " + gm.getCurrentDay());
        }
    }

    private void refreshUI() {
        PlayerState state = gm.getPlayerState();

        dayLabel.setText("📅 DAY " + state.getCurrentDay() + "  |  " + gm.getDaysRemaining() + " DAYS LEFT");

        float gpa = state.getGpa();
        int gpaPercent = (int) (gpa / 4.0f * 100);
        gpaBar.setValue(Math.min(100, Math.max(0, gpaPercent)));
        gpaValueLabel.setText(String.format("%.2f / 4.0", gpa));

        int mental = state.getMental();
        mentalBar.setValue(Math.min(100, Math.max(0, mental)));
        mentalValueLabel.setText(mental + " / 100");

        int happy = state.getHappiness();
        happyBar.setValue(Math.min(100, Math.max(0, happy)));
        happyValueLabel.setText(happy + " / 100");

        actionsLabel.setText("⚡ ACTIONS: " + state.getActionsLeft());

        // Update hand cards
        List<Card> hand = gm.getHandCards();
        for (int i = 0; i < handButtons.length; i++) {
            if (i < hand.size()) {
                Card card = hand.get(i);
                String cardIcon = getCardIcon(card);
                handButtons[i].setText("<html><center><b>" + cardIcon + " " + card.getName() +
                        "</b><br><font size='2'>⚡ " + card.getCost() +
                        "  |  📊 " + (card.getGpaEffect() >= 0 ? "+" : "") + card.getGpaEffect() +
                        "  |  🧠 " + (card.getMentalEffect() >= 0 ? "+" : "") + card.getMentalEffect() +
                        "  |  😊 " + (card.getHappyEffect() >= 0 ? "+" : "") + card.getHappyEffect() +
                        "</font></center></html>");
                handButtons[i].setEnabled(true);
                handButtons[i].setToolTipText(card.getDescription());
            } else {
                handButtons[i].setText("<html><center>───<br>EMPTY</center></html>");
                handButtons[i].setEnabled(false);
                handButtons[i].setToolTipText(null);
            }
        }

        // Update played cards
        playedPanel.removeAll();
        List<Card> played = gm.getPlayedCards();
        if (played.isEmpty()) {
            JLabel emptyLabel = new JLabel("No cards played yet");
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playedPanel.add(emptyLabel);
        } else {
            for (Card card : played) {
                JPanel cardPanel = new JPanel(new BorderLayout());
                cardPanel.setOpaque(false);
                cardPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                JLabel cardLabel = new JLabel(getCardIcon(card) + " " + card.getName());
                cardLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                cardLabel.setForeground(COLOR_TEXT);
                cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cardPanel.add(cardLabel, BorderLayout.CENTER);

                playedPanel.add(cardPanel);
                playedPanel.add(Box.createVerticalStrut(8));
            }
        }
        playedPanel.revalidate();
        playedPanel.repaint();

        // Update buffs
        List<Buff> buffs = gm.getActiveBuffs();
        buffPanel.removeAll();
        if (buffs.isEmpty()) {
            JLabel placeholder = new JLabel("✨ No active buffs");
            placeholder.setForeground(Color.GRAY);
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            buffPanel.add(placeholder);
        } else {
            for (Buff buff : buffs) {
                JLabel buffLabel = new JLabel("✨ " + buff.getName());
                buffLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                buffLabel.setForeground(new Color(255, 215, 0));
                buffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                buffLabel.setToolTipText(buff.getDescription());
                buffPanel.add(buffLabel);
            }
        }
        buffPanel.revalidate();
        buffPanel.repaint();

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

    // ==================== DIALOGS ====================

    private void showEventDialog() {
        GameEvent event = gm.getCurrentEvent();
        if (event == null) return;

        String message = "📅 " + event.getTitle() + "\n\n" +
                event.getDescription() + "\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "📊 EFFECTS:\n" +
                "  • GPA: " + (event.getGpaEffect() >= 0 ? "+" : "") + event.getGpaEffect() + "\n" +
                "  • Mental: " + (event.getMentalEffect() >= 0 ? "+" : "") + event.getMentalEffect() + "\n" +
                "  • Happy: " + (event.getHappyEffect() >= 0 ? "+" : "") + event.getHappyEffect();

        int option = JOptionPane.showConfirmDialog(frame, message, "📅 Daily Event",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            gm.confirmEvent();
            refreshUI();
        } else {
            gm.confirmEvent();
            refreshUI();
        }
    }

    private void showGrowthChoiceDialog() {
        String[] options = { "📚 Learn New Skill", "⬆️ Upgrade Cards", "✨ Gain New Buff" };

        int choice = JOptionPane.showOptionDialog(frame,
                "🎉 SEMESTER MILESTONE! 🎉\n\nYou've reached Day " + gm.getCurrentDay() + "!\nChoose your growth path:",
                "Growth Opportunity",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice >= 0) {
            gm.confirmGrowthChoice(choice);
            refreshUI();
        }
    }

    private void showGameOverMenu(boolean isVictory, String message) {
        frame.getContentPane().removeAll();

        JPanel gameOverPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COLOR_BG_DARK, getWidth(), getHeight(), COLOR_BG_LIGHT);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gameOverPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel titleLabel = new JLabel(isVictory ? "🎉 VICTORY! 🎉" : "💀 GAME OVER 💀");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(isVictory ? new Color(255, 215, 0) : Color.RED);
        gbc.gridy = 0;
        gameOverPanel.add(titleLabel, gbc);

        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        messageLabel.setForeground(COLOR_TEXT);
        gbc.gridy = 1;
        gameOverPanel.add(messageLabel, gbc);

        PlayerState state = gm.getPlayerState();
        String stats = String.format(
                "<html><center>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<br>" +
                        "📊 FINAL STATISTICS:<br>" +
                        "  • GPA: %.2f / 4.0<br>" +
                        "  • Mental: %d / 100<br>" +
                        "  • Happiness: %d / 100<br>" +
                        "  • Days Survived: %d / 30<br>" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</center></html>",
                state.getGpa(), state.getMental(), state.getHappiness(), state.getCurrentDay() - 1
        );

        JLabel statsLabel = new JLabel(stats);
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsLabel.setForeground(COLOR_TEXT);
        gbc.gridy = 2;
        gameOverPanel.add(statsLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton playAgainBtn = createStyledButton("🔄 PLAY AGAIN", new Color(76, 175, 80), 16);
        playAgainBtn.addActionListener(e -> startNewGame());
        buttonPanel.add(playAgainBtn);

        JButton menuBtn = createStyledButton("🏠 MAIN MENU", new Color(33, 150, 243), 16);
        menuBtn.addActionListener(e -> showStartMenu());
        buttonPanel.add(menuBtn);

        JButton exitBtn = createStyledButton("❌ EXIT", new Color(100, 100, 100), 16);
        exitBtn.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitBtn);

        gbc.gridy = 3;
        gameOverPanel.add(buttonPanel, gbc);

        frame.add(gameOverPanel);
        frame.revalidate();
        frame.repaint();
    }

    private void showSaveConfirmDialog() {
        if (gm.isGameOver()) {
            JOptionPane.showMessageDialog(frame, "Cannot save - Game is already over!", "Save Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame,
                "Save current game progress?\n\nDay: " + gm.getCurrentDay() + "\nGPA: " +
                        String.format("%.2f", gm.getPlayerState().getGpa()),
                "Save Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SaveManager.save(gm);
            statusLabel.setText("💾 Game saved at Day " + gm.getCurrentDay() + "!");
            statusLabel.setForeground(new Color(76, 175, 80));
            JOptionPane.showMessageDialog(frame, "✅ Game saved successfully!", "Save Successful", JOptionPane.INFORMATION_MESSAGE);

            Timer resetTimer = new Timer(3000, evt -> {
                if (!gm.isGameOver()) {
                    statusLabel.setForeground(new Color(255, 235, 59));
                    statusLabel.setText("Game in Progress");
                }
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
        }
    }

    // ==================== MAIN ====================

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
