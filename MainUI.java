import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * Game Main UI - Premium Styled Version
 * Features: Gradient backgrounds, color-coded cards by rarity, glow effects,
 *           hover animations, card draw animation, smooth card flight with easing,
 *           combo effects, full sound integration.
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

    // Card flight animation (play card)
    private Timer flightTimer;
    private JPanel flyingCard;
    private int flyStartX, flyStartY;
    private int flyEndX, flyEndY;
    private int flyStepsRemaining;
    private static final int FLY_DURATION = 25;

    // Star particle system for menu
    private Timer starTimer;
    private java.util.List<StarParticle> stars;
    private JPanel menuPanelRef;

    // Modern color scheme
    private static final Color COLOR_BG_DARK = new Color(10, 15, 25);
    private static final Color COLOR_BG_LIGHT = new Color(22, 30, 48);
    private static final Color COLOR_CARD = new Color(45, 55, 75);
    private static final Color COLOR_ACCENT = new Color(100, 200, 130);
    private static final Color COLOR_TEXT = new Color(240, 240, 245);

    // Card size constants
    private static final int CARD_WIDTH = 160;
    private static final int CARD_HEIGHT = 200;

    public MainUI() {
        handButtons = new JButton[5];
        stars = new java.util.ArrayList<>();
        showStartMenu();
    }

    // ==================== STAR PARTICLE ====================

    private static class StarParticle {
        double x, y;
        double speedX, speedY;
        double alpha;
        double size;

        StarParticle(int w, int h) {
            this.x = Math.random() * w;
            this.y = Math.random() * h;
            this.speedX = (Math.random() - 0.5) * 1.5;
            this.speedY = (Math.random() - 0.5) * 1.5;
            this.alpha = Math.random() * 0.6 + 0.2;
            this.size = Math.random() * 3 + 1;
        }

        void update(int w, int h) {
            x += speedX;
            y += speedY;
            if (x < 0) x = w;
            if (x > w) x = 0;
            if (y < 0) y = h;
            if (y > h) y = 0;
            alpha += (Math.random() - 0.5) * 0.03;
            alpha = Math.max(0.1, Math.min(1.0, alpha));
        }

        void draw(Graphics2D g2d) {
            g2d.setColor(new Color(255, 255, 255, (int) (alpha * 200)));
            g2d.fillOval((int) x, (int) y, (int) size, (int) size);
        }
    }

    // ==================== START MENU ====================

    private void showStartMenu() {
        if (frame == null) {
            frame = new JFrame("CAMPUS SURVIVOR");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setMinimumSize(new Dimension(1024, 600));
        }

        SoundManager.stopBGM();
        stopStarAnimation();
        frame.getContentPane().removeAll();

        JPanel menuPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COLOR_BG_DARK, getWidth(), getHeight(), COLOR_BG_LIGHT);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                for (StarParticle star : stars) {
                    star.draw(g2d);
                }
            }
        };
        menuPanelRef = menuPanel;
        menuPanel.setOpaque(false);

        stars.clear();
        for (int i = 0; i < 60; i++) {
            stars.add(new StarParticle(1280, 800));
        }

        starTimer = new Timer(40, e -> {
            for (StarParticle star : stars) {
                star.update(menuPanel.getWidth(), menuPanel.getHeight());
            }
            menuPanel.repaint();
        });
        starTimer.start();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        JLabel titleLabel = new JLabel("CAMPUS SURVIVOR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 215, 0, 60));
                g2d.setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() + 6));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2 - 3, fm.getAscent() + 3);
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                fm = g2d.getFontMetrics();
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, fm.getAscent());
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 56));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("A Roguelike Deck-Building Game");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        subtitleLabel.setForeground(COLOR_TEXT);
        gbc.gridy = 1;
        menuPanel.add(subtitleLabel, gbc);

        JButton newGameBtn = createStyledButton("NEW GAME", new Color(46, 160, 67), 22);
        newGameBtn.addActionListener(e -> {
            stopStarAnimation();
            startNewGame();
        });
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 50, 12, 50);
        menuPanel.add(newGameBtn, gbc);

        JButton continueBtn = createStyledButton("CONTINUE", new Color(30, 136, 229), 22);
        continueBtn.addActionListener(e -> {
            stopStarAnimation();
            loadSavedGame();
        });
        if (!SaveManager.saveExists()) {
            continueBtn.setEnabled(false);
            continueBtn.setBackground(new Color(80, 80, 80));
            continueBtn.setToolTipText("No saved game found");
        }
        gbc.gridy = 3;
        menuPanel.add(continueBtn, gbc);

        JButton exitBtn = createStyledButton("EXIT", new Color(100, 100, 110), 18);
        exitBtn.addActionListener(e -> System.exit(0));
        gbc.gridy = 4;
        menuPanel.add(exitBtn, gbc);

        JLabel versionLabel = new JLabel("v2.0  |  Made with Java Swing");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(150, 150, 160));
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 50, 10, 50);
        menuPanel.add(versionLabel, gbc);

        frame.add(menuPanel);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private void stopStarAnimation() {
        if (starTimer != null) {
            starTimer.stop();
        }
    }

    private JButton createStyledButton(String text, Color color, int fontSize) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 22, 22);
                GradientPaint gp = new GradientPaint(0, 0, getBackground().brighter(), 0, getHeight(), getBackground().darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(3, 3, getWidth() - 7, getHeight() / 2 - 3, 22, 22);
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 22, 22);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2 + 1,
                        (getHeight() + fm.getAscent()) / 2 - 1);
                g2d.setColor(getForeground());
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(16, 44, 16, 44));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.brighter().brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    private void startNewGame() {
        SoundManager.loadSound("play", "assets/play.wav");
        SoundManager.loadSound("combo", "assets/combo.wav");
        SoundManager.loadSound("event", "assets/event.wav");
        SoundManager.loadSound("victory", "assets/victory.wav");
        SoundManager.loadSound("defeat", "assets/defeat.wav");
        SoundManager.playBGM("assets/bgm.wav");

        gm = new GameManager();
        gm.startNewGame();
        initUI();
        refreshUI();
        animateDrawCards();
    }

    private void loadSavedGame() {
        GameManager loaded = SaveManager.load();
        if (loaded != null) {
            SoundManager.loadSound("play", "assets/play.wav");
            SoundManager.loadSound("combo", "assets/combo.wav");
            SoundManager.loadSound("event", "assets/event.wav");
            SoundManager.loadSound("victory", "assets/victory.wav");
            SoundManager.loadSound("defeat", "assets/defeat.wav");
            SoundManager.playBGM("assets/bgm.wav");

            gm = loaded;
            initUI();
            refreshUI();
            animateDrawCards();
        } else {
            JOptionPane.showMessageDialog(frame, "No save file found!", "Error", JOptionPane.ERROR_MESSAGE);
            showStartMenu();
        }
    }

    // ==================== MAIN GAME UI ====================

    private void initUI() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout(10, 10));

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

        JPanel topPanel = createTopPanel();
        bgPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        bgPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(bgPanel);

        JPanel glassPanel = new JPanel();
        glassPanel.setOpaque(false);
        glassPanel.setLayout(new GridBagLayout());
        frame.setGlassPane(glassPanel);

        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        dayLabel = new JLabel();
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dayLabel.setForeground(COLOR_TEXT);
        dayLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 2, true),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)
        ));

        statusLabel = new JLabel("Game in Progress");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statusLabel.setForeground(new Color(255, 235, 59));

        JButton saveButton = new JButton("SAVE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, getBackground().brighter(), 0, getHeight(), getBackground().darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2d.setColor(new Color(255, 255, 255, 70));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.setColor(getForeground());
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
            }
        };
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setBackground(new Color(46, 160, 67));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setContentAreaFilled(false);
        saveButton.setOpaque(false);
        saveButton.addActionListener(e -> showSaveConfirmDialog());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
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
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 2, true),
                "STATISTICS",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                COLOR_TEXT
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel gpaLabel = new JLabel("GPA");
        gpaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gpaLabel.setForeground(new Color(76, 220, 100));
        panel.add(gpaLabel, gbc);

        gbc.gridy = 1;
        gpaBar = createStyledProgressBar(new Color(76, 220, 100));
        panel.add(gpaBar, gbc);

        gbc.gridy = 2;
        gpaValueLabel = new JLabel("0.0 / 4.0");
        gpaValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gpaValueLabel.setForeground(COLOR_TEXT);
        panel.add(gpaValueLabel, gbc);

        gbc.gridy = 3;
        JLabel mentalLabel = new JLabel("MENTAL");
        mentalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mentalLabel.setForeground(new Color(66, 165, 245));
        panel.add(mentalLabel, gbc);

        gbc.gridy = 4;
        mentalBar = createStyledProgressBar(new Color(66, 165, 245));
        panel.add(mentalBar, gbc);

        gbc.gridy = 5;
        mentalValueLabel = new JLabel("0 / 100");
        mentalValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mentalValueLabel.setForeground(COLOR_TEXT);
        panel.add(mentalValueLabel, gbc);

        gbc.gridy = 6;
        JLabel happyLabel = new JLabel("HAPPINESS");
        happyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        happyLabel.setForeground(new Color(255, 213, 79));
        panel.add(happyLabel, gbc);

        gbc.gridy = 7;
        happyBar = createStyledProgressBar(new Color(255, 213, 79));
        panel.add(happyBar, gbc);

        gbc.gridy = 8;
        happyValueLabel = new JLabel("0 / 100");
        happyValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        happyValueLabel.setForeground(COLOR_TEXT);
        panel.add(happyValueLabel, gbc);

        gbc.gridy = 9;
        actionsLabel = new JLabel("ACTIONS: 0");
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        actionsLabel.setForeground(COLOR_ACCENT);
        actionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(actionsLabel, gbc);

        gbc.weighty = 1.0;
        gbc.gridy = 10;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JProgressBar createStyledProgressBar(Color color) {
        JProgressBar bar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2d.fillRoundRect(-2, -2, getWidth() + 4, getHeight() + 4, 12, 12);
                super.paintComponent(g);
            }
        };
        bar.setForeground(color);
        bar.setBackground(new Color(40, 50, 70));
        bar.setStringPainted(true);
        bar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bar.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        bar.setOpaque(false);
        return bar;
    }

    private JPanel createPlayedPanel() {
        playedPanel = new JPanel();
        playedPanel.setLayout(new BoxLayout(playedPanel, BoxLayout.Y_AXIS));
        playedPanel.setOpaque(false);
        playedPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 2, true),
                "PLAYED THIS TURN",
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
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 2, true),
                "ACTIVE BUFFS",
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

        JPanel handPanel = new JPanel(new GridLayout(1, 5, 16, 0));
        handPanel.setOpaque(false);
        handPanel.setPreferredSize(new Dimension(1000, 210));
        handPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 2, true),
                "HAND CARDS",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 15),
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

        endTurnButton = new JButton("END TURN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 22, 22);
                GradientPaint gp = new GradientPaint(0, 0, getBackground().brighter(), 0, getHeight(), getBackground().darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 22, 22);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.setColor(getForeground());
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
            }
        };
        endTurnButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        endTurnButton.setBackground(new Color(229, 57, 53));
        endTurnButton.setForeground(Color.WHITE);
        endTurnButton.setFocusPainted(false);
        endTurnButton.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40));
        endTurnButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        endTurnButton.setContentAreaFilled(false);
        endTurnButton.setOpaque(false);
        endTurnButton.addActionListener(e -> endTurnAndRefresh());
        bottomPanel.add(endTurnButton, BorderLayout.EAST);

        return bottomPanel;
    }

    private JButton createCardButton() {
        JButton btn = new JButton();
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("cardColor", COLOR_CARD);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color cardColor = (Color) ((JButton) c).getClientProperty("cardColor");
                if (cardColor == null) cardColor = COLOR_CARD;

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(6, 6, c.getWidth() - 2, c.getHeight() - 2, 20, 20);

                // Card body
                GradientPaint gp = new GradientPaint(0, 0, cardColor.brighter(), 0, c.getHeight(), cardColor.darker().darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 20, 20);

                // Top highlight
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(4, 4, c.getWidth() - 9, c.getHeight() / 2 - 4, 20, 20);

                // Border
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, 20, 20);

                super.paint(g, c);
            }
        });

        return btn;
    }

    private Color getCardColor(Card card) {
        if (card.hasTag(Tag.HIGH_RISK)) return new Color(180, 40, 40);
        if (card.hasTag(Tag.AI)) return new Color(100, 40, 160);
        if (card.hasTag(Tag.ENTERTAINMENT)) return new Color(200, 120, 20);
        if (card.hasTag(Tag.STUDY)) return new Color(30, 80, 180);
        if (card.hasTag(Tag.REFRESH)) return new Color(30, 150, 130);
        if (card.hasTag(Tag.REST)) return new Color(50, 140, 50);
        if (card.hasTag(Tag.STAY_UP)) return new Color(80, 40, 140);
        return COLOR_CARD;
    }

    private String getCardIcon(Card card) {
        if (card.hasTag(Tag.HIGH_RISK)) return "!!";
        if (card.hasTag(Tag.AI)) return "AI";
        if (card.hasTag(Tag.ENTERTAINMENT)) return ">";
        if (card.hasTag(Tag.STUDY)) return "[]";
        if (card.hasTag(Tag.REFRESH)) return "~";
        if (card.hasTag(Tag.REST)) return "Zz";
        if (card.hasTag(Tag.STAY_UP)) return "@";
        return "?";
    }

    // ==================== GAME ACTIONS ====================

    private void playCardAndRefresh(int index) {
        if (gm.isGameOver()) {
            statusLabel.setText("Game Over. Please restart.");
            return;
        }

        JButton clickedBtn = handButtons[index];
        Point handLocation = clickedBtn.getLocationOnScreen();
        flyStartX = handLocation.x + clickedBtn.getWidth() / 2;
        flyStartY = handLocation.y + clickedBtn.getHeight() / 2;

        boolean success = gm.playCard(index);
        if (success) {
            SoundManager.play("play");
            if (gm.getLastCombo() != null) {
                SoundManager.play("combo");
                showComboEffect(gm.getLastCombo());
            }
            statusLabel.setText("Card played!");
            refreshUI();

            Point playedLoc = playedPanel.getLocationOnScreen();
            flyEndX = playedLoc.x + playedPanel.getWidth() / 2;
            flyEndY = playedLoc.y + playedPanel.getHeight() / 2;

            startCardFlight(clickedBtn);
        } else {
            statusLabel.setText("Not enough actions or invalid card!");
        }
    }

    // ==================== CARD FLIGHT ANIMATION (play card with easing) ====================

    private void startCardFlight(JButton sourceBtn) {
        flyingCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color cardColor = (Color) sourceBtn.getClientProperty("cardColor");
                if (cardColor == null) cardColor = COLOR_CARD;
                GradientPaint gp = new GradientPaint(0, 0, cardColor.brighter(), 0, getHeight(), cardColor.darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        flyingCard.setSize(sourceBtn.getWidth(), sourceBtn.getHeight());
        flyingCard.setOpaque(false);

        JPanel glass = (JPanel) frame.getGlassPane();
        glass.setLayout(null);
        glass.removeAll();
        glass.add(flyingCard);
        glass.setVisible(true);

        flyStepsRemaining = FLY_DURATION;
        Point frameLocation = frame.getLocationOnScreen();
        flyingCard.setLocation(flyStartX - frameLocation.x - flyingCard.getWidth() / 2,
                flyStartY - frameLocation.y - flyingCard.getHeight() / 2);

        flightTimer = new Timer(16, e -> animateCardFlight());
        flightTimer.start();
    }

    private void animateCardFlight() {
        if (flyStepsRemaining <= 0 || flyingCard == null) {
            flightTimer.stop();
            JPanel glass = (JPanel) frame.getGlassPane();
            glass.removeAll();
            glass.setVisible(false);
            flyingCard = null;
            frame.repaint();
            return;
        }

        // Ease-in-out using sine curve
        double progress = 1.0 - (double) flyStepsRemaining / FLY_DURATION;
        double eased = progress < 0.5
                ? 2 * progress * progress
                : 1 - Math.pow(-2 * progress + 2, 2) / 2;

        Point frameLocation = frame.getLocationOnScreen();
        int targetX = flyEndX - frameLocation.x - flyingCard.getWidth() / 2;
        int targetY = flyEndY - frameLocation.y - flyingCard.getHeight() / 2;
        int startX = flyStartX - frameLocation.x - flyingCard.getWidth() / 2;
        int startY = flyStartY - frameLocation.y - flyingCard.getHeight() / 2;

        int currentX = startX + (int)((targetX - startX) * eased);
        int currentY = startY + (int)((targetY - startY) * eased);

        flyingCard.setLocation(currentX, currentY);
        flyStepsRemaining--;

        // Scale: enlarge then shrink
        double scale;
        if (progress < 0.25) {
            scale = 1.0 + 0.15 * (progress / 0.25);
        } else {
            scale = 1.15 - 0.55 * ((progress - 0.25) / 0.75);
        }
        int baseW = flyingCard.getWidth();
        int baseH = flyingCard.getHeight();
        flyingCard.setSize((int)(baseW * scale), (int)(baseH * scale));

        frame.repaint();
    }

    // ==================== DRAW CARD ANIMATION (new cards fly from deck to hand) ====================

    private void animateDrawCards() {
        // Calculate deck position (to the right of played panel)
        Point playedLoc = playedPanel.getLocationOnScreen();
        int deckX = playedLoc.x + playedPanel.getWidth() + 40;
        int deckY = playedLoc.y + playedPanel.getHeight() - 50;

        List<Card> hand = gm.getHandCards();
        for (int i = 0; i < hand.size(); i++) {
            final int index = i;
            JButton btn = handButtons[i];
            if (btn == null || hand.get(index) == null) continue;

            Point handLoc = btn.getLocationOnScreen();
            int handX = handLoc.x + btn.getWidth() / 2;
            int handY = handLoc.y + btn.getHeight() / 2;

            Color color = getCardColor(hand.get(index));

            // Stagger each card's animation
            Timer drawTimer = new Timer(index * 100, e -> {
                flyCardFromTo(deckX, deckY, handX, handY, color);
            });
            drawTimer.setRepeats(false);
            drawTimer.start();
        }
    }

    private void flyCardFromTo(int fromX, int fromY, int toX, int toY, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            }
        };
        card.setSize(80, 110);
        card.setOpaque(false);

        JPanel glass = (JPanel) frame.getGlassPane();
        glass.setLayout(null);
        glass.add(card);
        glass.setVisible(true);

        Point frameLoc = frame.getLocationOnScreen();
        card.setLocation(fromX - frameLoc.x - 40, fromY - frameLoc.y - 55);

        final int[] step = {0};
        final int totalSteps = 22;
        Timer animTimer = new Timer(16, null);
        animTimer.addActionListener(e -> {
            step[0]++;
            if (step[0] >= totalSteps) {
                animTimer.stop();
                glass.remove(card);
                glass.repaint();
                return;
            }
            double progress = (double) step[0] / totalSteps;
            double eased = progress < 0.5
                    ? 2 * progress * progress
                    : 1 - Math.pow(-2 * progress + 2, 2) / 2;
            int cx = fromX - frameLoc.x - 40 + (int)((toX - fromX) * eased);
            int cy = fromY - frameLoc.y - 55 + (int)((toY - fromY) * eased);
            card.setLocation(cx, cy);

            // Rotate slightly
            double angle = (1 - eased) * 0.3;
            // Skipping actual rotation for simplicity, just fade in
        });
        animTimer.start();
    }

    // ==================== END TURN ====================

    private void endTurnAndRefresh() {
        if (gm.isGameOver()) {
            statusLabel.setText("Game Over");
            return;
        }

        gm.endTurn();
        refreshUI();

        // Animate new cards flying from deck to hand
        animateDrawCards();

        if (gm.isWaitingForEventResolution()) {
            // Delay event dialog until draw animation finishes
            Timer eventTimer = new Timer(600, e -> showEventDialog());
            eventTimer.setRepeats(false);
            eventTimer.start();
        }

        if (gm.isWaitingForGrowthChoice()) {
            Timer growthTimer = new Timer(800, e -> showGrowthChoiceDialog());
            growthTimer.setRepeats(false);
            growthTimer.start();
        }

        if (gm.isGameOver()) {
            String message;
            boolean isVictory = gm.isGameWon();
            if (isVictory) {
                message = "Congratulations! You survived the semester!\nYour GPA: " +
                        String.format("%.1f", gm.getPlayerState().getGpa());
            } else {
                String reason;
                if (gm.getPlayerState().isMentalDepleted()) {
                    reason = "Mental collapse - You burned out from stress.";
                } else if (gm.getPlayerState().isGPAFailed()) {
                    reason = "GPA too low - Academic dismissal.";
                } else {
                    reason = "Semester ended - GPA target not reached.";
                }
                message = "Game Over\n\n" + reason;
            }
            Timer gameOverTimer = new Timer(1000, e -> showGameOverMenu(isVictory, message));
            gameOverTimer.setRepeats(false);
            gameOverTimer.start();
        } else {
            statusLabel.setText("Entering Day " + gm.getCurrentDay());
        }
    }

    // ==================== REFRESH UI ====================

    private void refreshUI() {
        PlayerState state = gm.getPlayerState();

        dayLabel.setText("DAY " + state.getCurrentDay() + "  |  " + gm.getDaysRemaining() + " DAYS LEFT");

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

        actionsLabel.setText("ACTIONS: " + state.getActionsLeft());

        // Update hand cards with colors
        List<Card> hand = gm.getHandCards();
        for (int i = 0; i < handButtons.length; i++) {
            if (i < hand.size()) {
                Card card = hand.get(i);
                String icon = getCardIcon(card);
                Color cardColor = getCardColor(card);
                handButtons[i].putClientProperty("cardColor", cardColor);
                handButtons[i].setText("<html><center><b style='font-size:16px'>" + icon + " " + card.getName() +
                        "</b><br><font size='4'>Cost:" + card.getCost() +
                        "  G:" + (card.getGpaEffect() >= 0 ? "+" : "") + card.getGpaEffect() +
                        "</font><br><font size='3'>M:" + (card.getMentalEffect() >= 0 ? "+" : "") + card.getMentalEffect() +
                        "  H:" + (card.getHappyEffect() >= 0 ? "+" : "") + card.getHappyEffect() +
                        "</font></center></html>");
                handButtons[i].setEnabled(true);
                handButtons[i].setToolTipText(card.getDescription());
            } else {
                handButtons[i].setText("<html><center>---<br>EMPTY</center></html>");
                handButtons[i].putClientProperty("cardColor", COLOR_CARD);
                handButtons[i].setEnabled(false);
                handButtons[i].setToolTipText(null);
            }
        }

        // Update played cards
        playedPanel.removeAll();
        List<Card> played = gm.getPlayedCards();
        if (played.isEmpty()) {
            JLabel emptyLabel = new JLabel("No cards played yet");
            emptyLabel.setForeground(new Color(150, 150, 160));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playedPanel.add(emptyLabel);
        } else {
            for (Card card : played) {
                Color c = getCardColor(card);
                JPanel cardPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 80));
                        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    }
                };
                cardPanel.setOpaque(false);
                cardPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

                JLabel cardLabel = new JLabel(getCardIcon(card) + " " + card.getName());
                cardLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
                cardLabel.setForeground(COLOR_TEXT);
                cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cardPanel.add(cardLabel, BorderLayout.CENTER);

                playedPanel.add(cardPanel);
                playedPanel.add(Box.createVerticalStrut(6));
            }
        }
        playedPanel.revalidate();
        playedPanel.repaint();

        // Update buffs
        List<Buff> buffs = gm.getActiveBuffs();
        buffPanel.removeAll();
        if (buffs.isEmpty()) {
            JLabel placeholder = new JLabel("No active buffs");
            placeholder.setForeground(new Color(150, 150, 160));
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            buffPanel.add(placeholder);
        } else {
            for (Buff buff : buffs) {
                JLabel buffLabel = new JLabel("* " + buff.getName());
                buffLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                buffLabel.setForeground(new Color(255, 215, 0));
                buffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                buffLabel.setToolTipText(buff.getDescription());
                buffPanel.add(buffLabel);
                buffPanel.add(Box.createVerticalStrut(4));
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

    // ==================== COMBO VISUAL EFFECT ====================

    private void showComboEffect(String comboText) {
        JLabel comboLabel = new JLabel("⚡ " + comboText + " ⚡") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 215, 0, 50));
                g2d.setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() + 4));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2 - 2, fm.getAscent() + 2);
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                fm = g2d.getFontMetrics();
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, fm.getAscent());
            }
        };
        comboLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        comboLabel.setForeground(new Color(255, 215, 0));
        comboLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel glass = (JPanel) frame.getGlassPane();
        glass.setLayout(new GridBagLayout());
        glass.removeAll();
        glass.add(comboLabel);
        glass.setVisible(true);

        Timer timer = new Timer(1600, e -> {
            glass.removeAll();
            glass.setVisible(false);
            frame.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ==================== DIALOGS ====================

    private void showEventDialog() {
        SoundManager.play("event");
        GameEvent event = gm.getCurrentEvent();
        if (event == null) return;

        String message = "[" + event.getTitle() + "]\n\n" +
                event.getDescription() + "\n\n" +
                "-------------------------\n" +
                "EFFECTS:\n" +
                "  GPA: " + (event.getGpaEffect() >= 0 ? "+" : "") + event.getGpaEffect() + "\n" +
                "  Mental: " + (event.getMentalEffect() >= 0 ? "+" : "") + event.getMentalEffect() + "\n" +
                "  Happy: " + (event.getHappyEffect() >= 0 ? "+" : "") + event.getHappyEffect();

        JOptionPane.showConfirmDialog(frame, message, "Daily Event",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);

        gm.confirmEvent();
        refreshUI();
    }

    private void showGrowthChoiceDialog() {
        String[] options = { "Learn New Skill (New Card)", "Upgrade Cards", "Gain New Buff" };

        int choice = JOptionPane.showOptionDialog(frame,
                "SEMESTER MILESTONE!\n\nYou've reached Day " + gm.getCurrentDay() + "!\nChoose your growth path:",
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
        SoundManager.stopBGM();
        if (isVictory) {
            SoundManager.play("victory");
        } else {
            SoundManager.play("defeat");
        }

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
        gbc.insets = new Insets(12, 12, 12, 12);

        JLabel titleLabel = new JLabel(isVictory ? "VICTORY!" : "GAME OVER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color glow = isVictory ? new Color(255, 215, 0, 50) : new Color(255, 0, 0, 50);
                g2d.setColor(glow);
                g2d.setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() + 6));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2 - 3, fm.getAscent() + 3);
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                fm = g2d.getFontMetrics();
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, fm.getAscent());
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        titleLabel.setForeground(isVictory ? new Color(255, 215, 0) : Color.RED);
        gbc.gridy = 0;
        gameOverPanel.add(titleLabel, gbc);

        JLabel messageLabel = new JLabel("<html><center>" + message.replace("\n", "<br>") + "</center></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        messageLabel.setForeground(COLOR_TEXT);
        gbc.gridy = 1;
        gameOverPanel.add(messageLabel, gbc);

        PlayerState state = gm.getPlayerState();
        String stats = String.format(
                "<html><center>-------------------------<br>" +
                        "FINAL STATISTICS:<br>" +
                        "  GPA: %.2f / 4.0<br>" +
                        "  Mental: %d / 100<br>" +
                        "  Happiness: %d / 100<br>" +
                        "  Days Survived: %d / 30<br>" +
                        "-------------------------</center></html>",
                state.getGpa(), state.getMental(), state.getHappiness(), state.getCurrentDay() - 1
        );

        JLabel statsLabel = new JLabel(stats);
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statsLabel.setForeground(COLOR_TEXT);
        gbc.gridy = 2;
        gameOverPanel.add(statsLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton playAgainBtn = createStyledButton("PLAY AGAIN", new Color(46, 160, 67), 17);
        playAgainBtn.addActionListener(e -> startNewGame());
        buttonPanel.add(playAgainBtn);

        JButton menuBtn = createStyledButton("MAIN MENU", new Color(30, 136, 229), 17);
        menuBtn.addActionListener(e -> showStartMenu());
        buttonPanel.add(menuBtn);

        JButton exitBtn = createStyledButton("EXIT", new Color(100, 100, 110), 17);
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
            statusLabel.setText("Game saved at Day " + gm.getCurrentDay() + "!");
            statusLabel.setForeground(new Color(76, 220, 100));
            JOptionPane.showMessageDialog(frame, "Game saved!", "Save Successful", JOptionPane.INFORMATION_MESSAGE);

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
