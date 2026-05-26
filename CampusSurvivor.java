import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

public class CampusSurvivor extends GameEngine {
    private GameManager gm;

    // Audio clips
    private AudioClip bgmClip, playClip, comboClip, eventClip, victoryClip, defeatClip;

    // Game states
    private enum GameState { MENU, PLAYING, GAME_OVER }
    private GameState state = GameState.MENU;

    // Button rectangles
    private Rectangle newGameBtn, continueBtn, exitBtn, saveBtn, endTurnBtn;
    private Rectangle[] handCardRects;

    // Animation
    private double animTimer = 0;
    private Star[] stars;

    // Flight animation
    private boolean flying = false;
    private double flyX, flyY, flyStartX, flyStartY, flyEndX, flyEndY;
    private double flyProgress = 0;
    private Color flyColor = Color.GRAY;
    private static final double FLY_DURATION = 0.4; // seconds

    // Draw card animation
    private boolean drawing = false;
    private double drawTimer = 0;
    private int drawCardIndex = 0;
    private static final double DRAW_INTERVAL = 0.12;

    // Combo effects
    private String comboText = null;
    private double comboTextTimer = 0;
    private java.util.List<double[]> particles = new java.util.ArrayList<>();
    private Random particleRand = new Random();

    // Colors
    private static final Color GPA_COLOR = new Color(80, 220, 100);
    private static final Color MTL_COLOR = new Color(70, 170, 255);
    private static final Color HAP_COLOR = new Color(255, 170, 60);
    private static final Color GOLD = new Color(255, 205, 60);
    private static final Color TEXT = new Color(230, 235, 245);
    private static final Color SUBTEXT = new Color(140, 150, 170);

    public CampusSurvivor() {
        super(1200, 800);
        handCardRects = new Rectangle[5];
        stars = new Star[50];
        for (int i = 0; i < 50; i++) stars[i] = new Star();
    }

    // Star for menu background
    private class Star {
        double x, y, speedX, speedY, size, alpha;
        Star() {
            x = Math.random() * 1200;
            y = Math.random() * 800;
            speedX = (Math.random() - 0.5) * 0.8;
            speedY = (Math.random() - 0.5) * 0.8;
            size = Math.random() * 2.5 + 1;
            alpha = Math.random() * 0.4 + 0.1;
        }
        void update() {
            x += speedX;
            y += speedY;
            if (x < 0) x = 1200; if (x > 1200) x = 0;
            if (y < 0) y = 800; if (y > 800) y = 0;
        }
    }

    @Override
    public void init() {}

    @Override
    public void update(double dt) {
        animTimer += dt;
        if (state == GameState.MENU) {
            for (Star s : stars) s.update();
        }

        // Flight animation
        if (flying) {
            flyProgress += dt / FLY_DURATION;
            if (flyProgress >= 1.0) {
                flying = false;
                flyX = flyEndX;
                flyY = flyEndY;
            } else {
                double eased = easeInOut(flyProgress);
                flyX = flyStartX + (flyEndX - flyStartX) * eased;
                flyY = flyStartY + (flyEndY - flyStartY) * eased;
            }
        }

        // Draw card animation
        if (drawing) {
            drawTimer -= dt;
            if (drawTimer <= 0) {
                drawCardIndex++;
                if (drawCardIndex >= 5) {
                    drawing = false;
                } else {
                    drawTimer = DRAW_INTERVAL;
                }
            }
        }

        // Combo text timer
        if (comboText != null) {
            comboTextTimer -= dt;
            if (comboTextTimer <= 0) comboText = null;
        }

        // Update particles
        for (int i = particles.size() - 1; i >= 0; i--) {
            double[] p = particles.get(i);
            p[0] += p[2]; // x + vx
            p[1] += p[3]; // y + vy
            p[4] -= dt;   // life
            if (p[4] <= 0) particles.remove(i);
        }
    }

    private double easeInOut(double t) {
        return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
    }

    @Override
    public void paintComponent() {
        drawGradientBG();

        switch (state) {
            case MENU: paintMenu(); break;
            case PLAYING: paintGame(); break;
            case GAME_OVER: paintGameOver(); break;
        }

        // Draw flying card on top
        if (flying) {
            int w = 155, h = 195;
            changeColor(new Color(0, 0, 0, 100));
            drawSolidRectangle((int)flyX - w/2 + 4, (int)flyY - h/2 + 4, w, h);
            changeColor(flyColor);
            drawSolidRectangle((int)flyX - w/2, (int)flyY - h/2, w, h);
            changeColor(new Color(255, 255, 255, 30));
            drawSolidRectangle((int)flyX - w/2 + 3, (int)flyY - h/2 + 3, w - 6, h/2 - 3);
        }

        // Combo text
        if (comboText != null) {
            changeColor(new Color(0, 0, 0, 120));
            drawSolidRectangle(mWidth/2 - 200, mHeight/2 - 60, 400, 80);
            changeColor(GOLD);
            drawBoldText(mWidth/2 - comboText.length() * 8, mHeight/2 + 10, comboText, "Segoe UI", 30);
        }

        // Particles
        for (double[] p : particles) {
            changeColor(new Color((int)p[5], (int)p[6], (int)p[7], (int)(p[4] * 255)));
            drawSolidCircle(p[0], p[1], 4);
        }
    }

    // ==================== BACKGROUND ====================
    private void drawGradientBG() {
        for (int y = 0; y < mHeight; y++) {
            float ratio = (float)y / mHeight;
            int r = (int)(12 + 10 * ratio);
            int g = (int)(14 + 11 * ratio);
            int b = (int)(22 + 16 * ratio);
            changeColor(r, g, b);
            drawLine(0, y, mWidth, y);
        }
    }

    // ==================== MENU ====================
    private void paintMenu() {
        for (Star s : stars) {
            changeColor(new Color(255, 255, 255, (int)(s.alpha * 150)));
            drawSolidCircle(s.x, s.y, s.size);
        }

        changeColor(GOLD);
        drawBoldText(mWidth/2 - 270, 200, "CAMPUS SURVIVOR", "Segoe UI", 56);
        changeColor(SUBTEXT);
        drawText(mWidth/2 - 150, 260, "Roguelike Deck-Building  Survive 30 Days", "Segoe UI", 17);

        int btnW = 280, btnH = 60, btnX = mWidth/2 - btnW/2;
        newGameBtn = drawButton(btnX, 340, btnW, btnH, "NEW GAME", new Color(50, 180, 70));
        boolean hasSave = SaveManager.saveExists();
        continueBtn = drawButton(btnX, 420, btnW, btnH, "CONTINUE", hasSave ? new Color(50, 150, 240) : new Color(60, 60, 70));
        exitBtn = drawButton(btnX, 500, btnW, btnH, "EXIT", new Color(90, 90, 100));
    }

    private Rectangle drawButton(int x, int y, int w, int h, String text, Color color) {
        changeColor(new Color(0, 0, 0, 80));
        drawSolidRectangle(x + 3, y + 3, w, h);
        changeColor(color);
        drawSolidRectangle(x, y, w, h);
        changeColor(new Color(255, 255, 255, 40));
        drawSolidRectangle(x + 3, y + 3, w - 6, h/2 - 3);
        changeColor(Color.WHITE);
        drawBoldText(x + w/2 - text.length()*7, y + h/2 + 8, text, "Segoe UI", 20);
        return new Rectangle(x, y, w, h);
    }

    // ==================== GAME SCREEN ====================
    private void paintGame() {
        if (gm == null) return;
        PlayerState s = gm.getPlayerState();

        // Top bar
        changeColor(new Color(28, 32, 45));
        drawSolidRectangle(20, 15, mWidth - 40, 50);
        changeColor(GOLD);
        drawBoldText(35, 48, "Day " + s.getCurrentDay() + " / 30", "Segoe UI", 20);

        // Save button
        saveBtn = new Rectangle(mWidth - 120, 18, 80, 35);
        changeColor(new Color(50, 180, 70));
        drawSolidRectangle(saveBtn.x, saveBtn.y, saveBtn.width, saveBtn.height);
        changeColor(Color.WHITE);
        drawBoldText(saveBtn.x + 18, saveBtn.y + 24, "SAVE", "Segoe UI", 13);

        // Left stats panel
        int leftX = 20, leftY = 80, leftW = 160, leftH = mHeight - 320;
        changeColor(new Color(28, 32, 45));
        drawSolidRectangle(leftX, leftY, leftW, leftH);
        int statY = leftY + 30;
        changeColor(SUBTEXT);
        drawBoldText(leftX + leftW/2 - 20, statY, "STATS", "Segoe UI", 12);
        statY += 25;
        drawStat(leftX + 20, statY, leftW - 40, "GPA", s.getGpa(), 4.0f, GPA_COLOR);
        statY += 60;
        drawStat(leftX + 20, statY, leftW - 40, "MENTAL", s.getMental(), 100, MTL_COLOR);
        statY += 60;
        drawStat(leftX + 20, statY, leftW - 40, "HAPPY", s.getHappiness(), 100, HAP_COLOR);
        statY += 70;
        changeColor(GOLD);
        drawBoldText(leftX + leftW/2 - 30, statY, "ACTIONS: " + s.getActionsLeft(), "Segoe UI", 18);

        // Center played area
        int centerX = 190, centerY = 80, centerW = 620, centerH = leftH;
        changeColor(new Color(28, 32, 45));
        drawSolidRectangle(centerX, centerY, centerW, centerH);
        changeColor(SUBTEXT);
        drawBoldText(centerX + centerW/2 - 55, centerY + 25, "PLAYED CARDS", "Segoe UI", 14);

        List<Card> played = gm.getPlayedCards();
        if (!played.isEmpty()) {
            int cardW = (centerW - 40) / played.size() - 10;
            for (int i = 0; i < played.size(); i++) {
                Card c = played.get(i);
                int cx = centerX + 15 + i * (cardW + 10);
                int cy = centerY + 45;
                changeColor(getCardColor(c));
                drawSolidRectangle(cx, cy, cardW, centerH - 60);
                changeColor(Color.WHITE);
                drawBoldText(cx + 10, cy + 35, c.getName(), "Segoe UI", 14);
                changeColor(SUBTEXT);
                drawText(cx + 10, cy + 55, "G:" + fs(c.getGpaEffect()) + " M:" + fs(c.getMentalEffect()) + " H:" + fs(c.getHappyEffect()), "Segoe UI", 11);
            }
        }

        // Right buffs panel
        int rightX = 820, rightY = 80, rightW = 360, rightH = leftH;
        changeColor(new Color(28, 32, 45));
        drawSolidRectangle(rightX, rightY, rightW, rightH);
        changeColor(SUBTEXT);
        drawBoldText(rightX + rightW/2 - 25, rightY + 25, "BUFFS", "Segoe UI", 14);
        List<Buff> buffs = gm.getActiveBuffs();
        int buffY = rightY + 50;
        if (buffs.isEmpty()) {
            changeColor(SUBTEXT);
            drawText(rightX + 40, buffY, "No active buffs", "Segoe UI", 12);
        } else {
            for (Buff bf : buffs) {
                changeColor(GOLD);
                drawText(rightX + 30, buffY, "✦ " + bf.getName(), "Segoe UI", 13);
                buffY += 22;
            }
        }

        // Hand cards
        int handY = mHeight - 250;
        int handW = 155, handH = 195;
        int handStartX = (mWidth - (handW * 5 + 16 * 4)) / 2;
        List<Card> hand = gm.getHandCards();
        for (int i = 0; i < 5; i++) {
            int hx = handStartX + i * (handW + 16);
            handCardRects[i] = new Rectangle(hx, handY, handW, handH);

            // Skip card if it's currently being flown (flying animation)
            if (flying && i == drawCardIndex - 1) continue;

            if (i < hand.size()) {
                Card c = hand.get(i);
                changeColor(new Color(0, 0, 0, 80));
                drawSolidRectangle(hx + 4, handY + 4, handW, handH);
                changeColor(getCardColor(c));
                drawSolidRectangle(hx, handY, handW, handH);
                changeColor(new Color(255, 255, 255, 30));
                drawSolidRectangle(hx + 3, handY + 3, handW - 6, handH/2 - 3);
                changeColor(Color.WHITE);
                drawBoldText(hx + 10, handY + 50, icon(c) + " " + c.getName(), "Segoe UI", 14);
                changeColor(SUBTEXT);
                drawText(hx + 10, handY + 90, "G:" + fs(c.getGpaEffect()) + " M:" + fs(c.getMentalEffect()) + " H:" + fs(c.getHappyEffect()), "Segoe UI", 11);
                changeColor(TEXT);
                drawText(hx + 10, handY + 120, "Cost: " + c.getCost(), "Segoe UI", 12);
            }
        }

        // End Turn button
        int endX = mWidth - 180, endY = mHeight - 100;
        endTurnBtn = new Rectangle(endX, endY, 140, 55);
        changeColor(new Color(220, 60, 50));
        drawSolidRectangle(endX, endY, 140, 55);
        changeColor(Color.WHITE);
        drawBoldText(endX + 14, endY + 35, "END TURN", "Segoe UI", 18);
    }

    private void drawStat(int x, int y, int w, String label, float value, float max, Color color) {
        changeColor(color);
        drawBoldText(x, y, label, "Segoe UI", 13);
        changeColor(new Color(40, 45, 55));
        drawSolidRectangle(x, y + 8, w, 12);
        float pct = Math.min(1, value / max);
        changeColor(color);
        drawSolidRectangle(x, y + 8, (int)(w * pct), 12);
        changeColor(TEXT);
        drawText(x + w/2 - 20, y + 40, String.format("%.1f / %.0f", value, max), "Segoe UI", 12);
    }

    // ==================== GAME OVER ====================
    private void paintGameOver() {
        boolean win = gm.isGameWon();
        changeColor(win ? GOLD : Color.RED);
        drawBoldText(mWidth/2 - 150, 300, win ? "VICTORY!" : "GAME OVER", "Segoe UI", 52);

        PlayerState s = gm.getPlayerState();
        changeColor(TEXT);
        drawText(mWidth/2 - 100, 370, "GPA: " + String.format("%.1f", s.getGpa()), "Segoe UI", 20);
        drawText(mWidth/2 - 100, 400, "Mental: " + s.getMental() + " / 100", "Segoe UI", 18);
        drawText(mWidth/2 - 100, 430, "Happy: " + s.getHappiness() + " / 100", "Segoe UI", 18);
        drawText(mWidth/2 - 100, 460, "Days Survived: " + (s.getCurrentDay() - 1) + " / 30", "Segoe UI", 18);

        drawButton(mWidth/2 - 140, 510, 280, 55, "PLAY AGAIN", new Color(50, 180, 70));
        drawButton(mWidth/2 - 140, 580, 280, 55, "MAIN MENU", new Color(50, 150, 240));
    }

    // ==================== MOUSE ====================
    @Override
    public void mouseReleased(MouseEvent e) {
        int mx = e.getX(), my = e.getY();

        if (state == GameState.MENU) {
            if (newGameBtn != null && newGameBtn.contains(mx, my)) {
                startNewGame();
            } else if (continueBtn != null && continueBtn.contains(mx, my) && SaveManager.saveExists()) {
                GameManager loaded = SaveManager.load();
                if (loaded != null) { gm = loaded; state = GameState.PLAYING; startDrawAnimation(); }
            } else if (exitBtn != null && exitBtn.contains(mx, my)) {
                System.exit(0);
            }
        }

        if (state == GameState.PLAYING && gm != null && !gm.isGameOver()) {
            // Hand cards
            for (int i = 0; i < 5; i++) {
                if (handCardRects[i] != null && handCardRects[i].contains(mx, my)) {
                    List<Card> hand = gm.getHandCards();
                    if (i < hand.size()) {
                        boolean success = gm.playCard(i);
                        if (success) {
                            if (playClip != null) playAudio(playClip);
                            // Start flight animation
                            Card c = hand.get(i);
                            flyColor = getCardColor(c);
                            flyStartX = handCardRects[i].x + handCardRects[i].width / 2.0;
                            flyStartY = handCardRects[i].y + handCardRects[i].height / 2.0;
                            flyEndX = 500;
                            flyEndY = 400;
                            flyProgress = 0;
                            flying = true;

                            // Combo check
                            String combo = gm.getLastCombo();
                            if (combo != null) {
                                if (comboClip != null) playAudio(comboClip);
                                showCombo(combo);
                            }
                        }
                    }
                    break;
                }
            }

            // End turn
            if (endTurnBtn != null && endTurnBtn.contains(mx, my)) {
                gm.endTurn();
                if (gm.isGameOver()) {
                    if (bgmClip != null) stopAudioLoop(bgmClip);
                    if (gm.isGameWon() && victoryClip != null) playAudio(victoryClip);
                    else if (!gm.isGameWon() && defeatClip != null) playAudio(defeatClip);
                    state = GameState.GAME_OVER;
                } else if (gm.isWaitingForEventResolution()) {
                    showEventPopup();
                } else if (gm.isWaitingForGrowthChoice()) {
                    showGrowthPopup();
                } else {
                    startDrawAnimation();
                }
            }

            // Save
            if (saveBtn != null && saveBtn.contains(mx, my)) {
                if (!gm.isGameOver()) SaveManager.save(gm);
            }
        }

        if (state == GameState.GAME_OVER) {
            if (mx > mWidth/2 - 140 && mx < mWidth/2 + 140 && my > 510 && my < 565) {
                startNewGame();
            }
            if (mx > mWidth/2 - 140 && mx < mWidth/2 + 140 && my > 580 && my < 635) {
                if (bgmClip != null) stopAudioLoop(bgmClip);
                state = GameState.MENU;
            }
        }
    }

    // ==================== POPUPS ====================
    private void showEventPopup() {
        GameEvent ev = gm.getCurrentEvent();
        if (ev == null) return;
        if (eventClip != null) playAudio(eventClip);

        // Use JOptionPane for simplicity (allowed in GameEngine)
        JOptionPane.showConfirmDialog(null,
                "[" + ev.getTitle() + "]\n\n" + ev.getDescription() + "\n\n" +
                        "GPA: " + fs(ev.getGpaEffect()) + "  Mental: " + fs(ev.getMentalEffect()) + "  Happy: " + fs(ev.getHappyEffect()),
                "Daily Event", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);

        gm.confirmEvent();
        if (!gm.isGameOver()) startDrawAnimation();
    }

    private void showGrowthPopup() {
        String[] opts = {"New Card", "Upgrade STUDY +1 GPA", "Gain Random Buff"};
        int ch = JOptionPane.showOptionDialog(null,
                "Milestone! Day " + gm.getCurrentDay() + "\nChoose:",
                "Growth Opportunity", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);

        if (ch >= 0) gm.confirmGrowthChoice(ch);
        if (!gm.isGameOver()) startDrawAnimation();
    }

    // ==================== EFFECTS ====================
    private void showCombo(String text) {
        comboText = text;
        comboTextTimer = 1.5;
        spawnParticles();
    }

    private void spawnParticles() {
        particles.clear();
        int cx = mWidth / 2, cy = mHeight / 2;
        for (int i = 0; i < 20; i++) {
            double angle = particleRand.nextDouble() * Math.PI * 2;
            double speed = 100 + particleRand.nextDouble() * 200;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            double life = 0.5 + particleRand.nextDouble() * 0.5;
            int r = particleRand.nextInt(200) + 55;
            int g = particleRand.nextInt(200) + 55;
            int b = particleRand.nextInt(100) + 155;
            particles.add(new double[]{cx, cy, vx, vy, life, r, g, b});
        }
    }

    // ==================== DRAW ANIMATION ====================
    private void startDrawAnimation() {
        drawing = true;
        drawCardIndex = 0;
        drawTimer = 0;
    }

    // ==================== INIT ====================
    private void startNewGame() {
        playClip = loadAudio("assets/play.wav");
        comboClip = loadAudio("assets/combo.wav");
        eventClip = loadAudio("assets/event.wav");
        victoryClip = loadAudio("assets/victory.wav");
        defeatClip = loadAudio("assets/defeat.wav");
        bgmClip = loadAudio("assets/bgm.wav");

        if (bgmClip != null) startAudioLoop(bgmClip);

        gm = new GameManager();
        gm.startNewGame();
        state = GameState.PLAYING;
        startDrawAnimation();
    }

    // ==================== HELPERS ====================
    private Color getCardColor(Card c) {
        if (c.hasTag(Tag.HIGH_RISK)) return new Color(165, 38, 38);
        if (c.hasTag(Tag.AI)) return new Color(90, 38, 145);
        if (c.hasTag(Tag.ENTERTAINMENT)) return new Color(185, 105, 20);
        if (c.hasTag(Tag.STUDY)) return new Color(28, 78, 165);
        if (c.hasTag(Tag.REFRESH)) return new Color(28, 135, 115);
        if (c.hasTag(Tag.REST)) return new Color(48, 125, 48);
        if (c.hasTag(Tag.STAY_UP)) return new Color(72, 38, 125);
        return new Color(45, 52, 75);
    }

    private String icon(Card c) {
        if (c.hasTag(Tag.HIGH_RISK)) return "!!";
        if (c.hasTag(Tag.AI)) return "AI";
        if (c.hasTag(Tag.ENTERTAINMENT)) return ">";
        if (c.hasTag(Tag.STUDY)) return "[]";
        if (c.hasTag(Tag.REFRESH)) return "~";
        if (c.hasTag(Tag.REST)) return "Zz";
        if (c.hasTag(Tag.STAY_UP)) return "@";
        return "?";
    }

    private String fs(float v) { return (v>=0?"+":"")+(v==(int)v?String.valueOf((int)v):String.format("%.1f",v)); }
    private String fs(int v) { return (v>=0?"+":"")+v; }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        CampusSurvivor game = new CampusSurvivor();
        createGame(game, 60);
    }
}