import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

/**
 * Central game manager controlling the entire game loop.
 * Phase 1: Initialization, draw, play card, end turn.
 * Phase 2: Combo system and event system.
 * Phase 3: Buff system, growth choices.
 * Phase 4: Save system, adaptive difficulty, balance.
 * Phase 5: Streak system, card upgrade persistence, Boss events.
 */
public class GameManager implements Serializable {
    private PlayerState playerState;
    private List<Card> deck;
    private List<Card> handCards;
    private List<Card> playedCards;
    private List<Card> discardPile;
    private List<Buff> activeBuffs;
    private GameEvent currentEvent;
    private String lastCombo;
    private boolean gameOver;
    private boolean gameWon;
    private boolean waitingForEventResolution;
    private boolean waitingForGrowthChoice;
    private int studyBonus;           // Persistent upgrade for STUDY cards
    private int upgradeCount;         // How many times cards have been upgraded
    private static final int HAND_SIZE = 5;
    private static final int MAX_DAYS = 30;
    private static final float GPA_TARGET = 3.5f;

    public GameManager() {
        this.playerState = new PlayerState();
        this.handCards = new ArrayList<>();
        this.playedCards = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.activeBuffs = new ArrayList<>();
        this.currentEvent = null;
        this.lastCombo = null;
        this.gameOver = false;
        this.gameWon = false;
        this.deck = new ArrayList<>();
        this.waitingForEventResolution = false;
        this.waitingForGrowthChoice = false;
        this.studyBonus = 0;
        this.upgradeCount = 0;
    }

    // ======================================================
    //  INITIALIZATION
    // ======================================================

    public void startNewGame() {
        playerState = new PlayerState();
        handCards.clear();
        playedCards.clear();
        discardPile.clear();
        activeBuffs.clear();
        currentEvent = null;
        lastCombo = null;
        gameOver = false;
        gameWon = false;
        studyBonus = 0;
        upgradeCount = 0;
        waitingForEventResolution = false;
        waitingForGrowthChoice = false;

        buildDeck();
        shuffleDeck();
        drawCards(HAND_SIZE);
    }

    public void setGameOver(boolean over) { this.gameOver = over; }
    public void setGameWon(boolean won) { this.gameWon = won; }

    public int getStudyBonus() { return studyBonus; }
    public int getUpgradeCount() { return upgradeCount; }

    private void buildDeck() {
        deck.clear();

        // STUDY CARDS
        deck.add(new Card(1, "Library Study", "Study in the library.", List.of(Tag.STUDY), 1, 1.5f, -1, 0));
        deck.add(new Card(2, "Practice Problems", "Do past exam papers.", List.of(Tag.STUDY), 1, 2.0f, -3, 0));
        deck.add(new Card(3, "Study Group", "Study with classmates.", List.of(Tag.STUDY), 1, 1.5f, 0, 5));
        deck.add(new Card(4, "Take Notes", "Organize lecture notes.", List.of(Tag.STUDY), 1, 1.0f, -1, 2));
        deck.add(new Card(5, "Review Slides", "Go through lecture slides.", List.of(Tag.STUDY), 1, 1.0f, -2, 0));

        // REFRESH CARDS
        deck.add(new Card(6, "Coffee Boost", "Drink coffee.", List.of(Tag.REFRESH), 1, 0.0f, 10, 5));
        deck.add(new Card(7, "Energy Drink", "Chug an energy drink.", List.of(Tag.REFRESH, Tag.HIGH_RISK), 1, 0.0f, 20, -5));
        deck.add(new Card(8, "Tea Break", "A calming cup of tea.", List.of(Tag.REFRESH), 1, 0.0f, 5, 10));

        // REST CARDS
        deck.add(new Card(9, "Power Nap", "Take a short nap.", List.of(Tag.REST), 1, 0.0f, 15, 5));
        deck.add(new Card(10, "Full Sleep", "Get a full night's rest.", List.of(Tag.REST), 2, 0.0f, 30, 10));
        deck.add(new Card(11, "Take a Walk", "Go for a walk outside.", List.of(Tag.REST), 1, 0.0f, 3, 15));

        // ENTERTAINMENT CARDS
        deck.add(new Card(12, "Gaming Break", "Play video games.", List.of(Tag.ENTERTAINMENT), 1, -0.5f, 0, 15));
        deck.add(new Card(13, "Social Media", "Scroll through feeds.", List.of(Tag.ENTERTAINMENT), 1, -1.0f, -5, 10));
        deck.add(new Card(14, "Movie Night", "Watch a movie.", List.of(Tag.ENTERTAINMENT), 1, -0.5f, 5, 20));

        // HIGH RISK CARDS
        deck.add(new Card(15, "All-Nighter", "Pull an all-nighter.", List.of(Tag.STUDY, Tag.STAY_UP, Tag.HIGH_RISK), 2, 3.0f, -20, -10));
        deck.add(new Card(16, "Cram Exam", "Cram the night before.", List.of(Tag.STUDY, Tag.STAY_UP, Tag.HIGH_RISK), 2, 3.5f, -25, -15));
        deck.add(new Card(17, "Cheat Sheet", "Hidden cheat sheet. Risky!", List.of(Tag.STUDY, Tag.HIGH_RISK), 2, 4.0f, -30, -10));

        // AI CARDS
        deck.add(new Card(18, "AI Study Helper", "Use AI to summarize.", List.of(Tag.STUDY, Tag.AI), 1, 2.0f, -5, 0));
        deck.add(new Card(19, "AI Notes Generator", "AI generates notes.", List.of(Tag.STUDY, Tag.AI), 1, 2.5f, -3, 3));
        deck.add(new Card(20, "AI Essay Draft", "AI helps draft an essay.", List.of(Tag.STUDY, Tag.AI, Tag.HIGH_RISK), 2, 3.0f, -5, -2));

        // STAY UP CARDS
        deck.add(new Card(21, "Late Night Study", "Study late into the night.", List.of(Tag.STUDY, Tag.STAY_UP), 1, 2.0f, -15, -5));
        deck.add(new Card(22, "Midnight Coffee", "Coffee at midnight.", List.of(Tag.REFRESH, Tag.STAY_UP), 1, 0.5f, 15, -5));

        // HYBRID CARDS
        deck.add(new Card(23, "Study with Music", "Study with background music.", List.of(Tag.STUDY, Tag.ENTERTAINMENT), 1, 1.0f, 0, 8));
        deck.add(new Card(24, "Group Coffee Run", "Get coffee with friends.", List.of(Tag.REFRESH, Tag.REST), 1, 0.0f, 10, 15));
        deck.add(new Card(25, "Gym Break", "Work out at the gym.", List.of(Tag.REST), 1, -0.5f, 10, 15));
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    // ======================================================
    //  CARD DRAWING
    // ======================================================

    public void drawCards(int count) {
        for (int i = 0; i < count; i++) {
            if (deck.isEmpty()) {
                reshuffleDiscardIntoDeck();
            }
            if (!deck.isEmpty()) {
                Card drawn = deck.remove(0);
                handCards.add(drawn);
            }
        }
    }

    private void reshuffleDiscardIntoDeck() {
        deck.addAll(discardPile);
        discardPile.clear();
        shuffleDeck();
        System.out.println("[SYSTEM] Discard pile reshuffled into deck.");
    }

    // ======================================================
    //  PLAYING CARDS
    // ======================================================

    public boolean playCard(int handIndex) {
        if (handCards.isEmpty()) return false;

        if (handIndex < 0 || handIndex >= handCards.size()) {
            System.out.println("[ERROR] Invalid card index.");
            return false;
        }

        Card card = handCards.get(handIndex);

        int actualCost = card.getCost();
        if (hasBuff(3) && card.hasTag(Tag.STAY_UP)) actualCost = 0;

        if (playerState.getActionsLeft() < actualCost) {
            System.out.println("[ERROR] Not enough actions to play: " + card.getName());
            return false;
        }

        // Apply active buff modifications
        for (Buff buff : activeBuffs) {
            buff.modifyCardEffect(card, playerState);
        }

        // Critical hit: HIGH_RISK cards have 30% chance of double effect
        boolean isCrit = false;
        if (card.hasTag(Tag.HIGH_RISK) && Math.random() < 0.3) {
            isCrit = true;
            playerState.applyCardEffects(card);
            playerState.applyCardEffects(card);
            System.out.println("[CRIT] " + card.getName() + " CRITICAL HIT! Double effect!");
            lastCombo = "CRITICAL HIT! " + card.getName() + " x2!";
        }

        if (!isCrit) {
            // Apply card effects with study bonus included
            playerState.applyCardEffects(card);
            if (card.hasTag(Tag.STUDY) && studyBonus > 0) {
                playerState.applyRawEffect(studyBonus, 0, 0);
                System.out.println("[UPGRADE BONUS] +" + studyBonus + " GPA from card upgrades");
            }
        }

        handCards.remove(handIndex);
        playedCards.add(card);

        // Check combos (2 different tags)
        String combo = ComboSystem.checkCombo(playedCards, playerState);
        if (combo != null && lastCombo == null) {
            lastCombo = combo;
            System.out.println("[COMBO] " + combo);
        }

        // Check streak (3+ same tag) - overrides combo if both trigger
        String streak = ComboSystem.checkStreak(playedCards, playerState);
        if (streak != null) {
            lastCombo = streak;
            System.out.println("[STREAK] " + streak);
        }

        System.out.println("[PLAYED] " + card.getName());
        return true;
    }

    // ======================================================
    //  TURN MANAGEMENT
    // ======================================================

    public void endTurn() {
        lastCombo = null;
        discardPile.addAll(playedCards);
        playedCards.clear();
        discardPile.addAll(handCards);
        handCards.clear();

        playerState.nextDay();

        if (hasBuff(8)) playerState.setActions(4);

        triggerEvent();
        checkGameEndConditions();

        // Growth choice at Day 10 and Day 20
        if (playerState.getCurrentDay() == 10 || playerState.getCurrentDay() == 20) {
            waitingForGrowthChoice = true;
            return;
        }

        if (!waitingForEventResolution) {
            finishEndTurn();
        }
    }

    public void confirmEvent() {
        if (currentEvent != null) {
            playerState.applyEventEffects(
                    currentEvent.getGpaEffect(),
                    currentEvent.getMentalEffect(),
                    currentEvent.getHappyEffect()
            );
            System.out.println("[EVENT] Applied: " + currentEvent);
        }
        waitingForEventResolution = false;
        currentEvent = null;
        finishEndTurn();
    }

    public void confirmGrowthChoice(int choiceIndex) {
        makeGrowthChoice(choiceIndex);
        waitingForGrowthChoice = false;
        finishEndTurn();
    }

    private void finishEndTurn() {
        checkGameEndConditions();
        if (!gameOver) {
            drawCards(HAND_SIZE);
        }
        System.out.println("[TURN END] Day " + playerState.getCurrentDay());
    }

    private void triggerEvent() {
        if (gameOver) return;

        // Boss events on specific days
        if (playerState.getCurrentDay() == 15) {
            currentEvent = new GameEvent(200, "MIDTERM EXAMS",
                    "It's midterm week! Everything is harder.", -1.5f, -25, -20);
            waitingForEventResolution = true;
            return;
        }
        if (playerState.getCurrentDay() == 30) {
            currentEvent = new GameEvent(201, "FINAL EXAMS",
                    "The final exams are here! Give it everything!", -2.0f, -30, -25);
            waitingForEventResolution = true;
            return;
        }

        // Adaptive difficulty
        double badEventChance = 0.3;
        if (playerState.getGpa() > 3.5) badEventChance = 0.6;
        if (playerState.getGpa() < 1.5) badEventChance = 0.15;

        if (Math.random() < badEventChance) {
            currentEvent = EventPool.getHardEventSimple();
        } else {
            currentEvent = EventPool.getRandomEvent(playerState.getCurrentDay());
        }

        if (currentEvent != null) {
            waitingForEventResolution = true;
        }
    }

    private void checkGameEndConditions() {
        if (playerState.isMentalDepleted()) {
            gameOver = true; gameWon = false;
            System.out.println("[GAME OVER] Mental collapse.");
        } else if (playerState.isGPAFailed()) {
            gameOver = true; gameWon = false;
            System.out.println("[GAME OVER] GPA too low.");
        } else if (playerState.getCurrentDay() > MAX_DAYS) {
            gameOver = true;
            gameWon = playerState.getGpa() >= GPA_TARGET;
            System.out.println(gameWon ? "[VICTORY]" : "[GAME OVER] Target not reached.");
        }
    }

    // ======================================================
    //  BUFF SYSTEM
    // ======================================================

    public boolean hasBuff(int buffId) {
        for (Buff b : activeBuffs) {
            if (b.getId() == buffId && b.isActive()) return true;
        }
        return false;
    }

    // ======================================================
    //  GROWTH CHOICES
    // ======================================================

    public void makeGrowthChoice(int choiceIndex) {
        switch (choiceIndex) {
            case 0: addRandomCard(); break;
            case 1: upgradeAllCards(); break;
            case 2: addRandomBuff(); break;
        }
    }

    private void addRandomCard() {
        List<Card> pool = new ArrayList<>();
        pool.add(new Card(99, "Bonus Study", "Extra study session.", List.of(Tag.STUDY), 1, 3.0f, -2, 0));
        pool.add(new Card(98, "Quick Nap", "Quick power nap.", List.of(Tag.REST), 1, 0.0f, 10, 3));
        pool.add(new Card(97, "Snack Break", "Grab a snack.", List.of(Tag.REST), 1, 0.0f, 3, 10));
        pool.add(new Card(96, "Flash Cards", "Review with flash cards.", List.of(Tag.STUDY), 1, 2.0f, -1, 0));
        deck.add(pool.get((int)(Math.random() * pool.size())));
    }

    private void upgradeAllCards() {
        studyBonus++;
        upgradeCount++;
        System.out.println("[UPGRADE] All STUDY cards now give +" + studyBonus + " bonus GPA!");
    }

    private void addRandomBuff() {
        List<Buff> allBuffs = BuffDatabase.getAllBuffs();
        List<Buff> available = new ArrayList<>();
        for (Buff b : allBuffs) {
            if (!hasBuff(b.getId())) available.add(b);
        }
        if (!available.isEmpty()) {
            Buff selected = available.get((int)(Math.random() * available.size()));
            selected.activate();
            activeBuffs.add(selected);
        }
    }

    // ======================================================
    //  EVENT RESOLUTION
    // ======================================================

    public void resolveEvent() {
        this.currentEvent = null;
    }

    // ======================================================
    //  GETTERS
    // ======================================================

    public PlayerState getPlayerState() { return playerState; }
    public List<Card> getHandCards() { return new ArrayList<>(handCards); }
    public List<Card> getPlayedCards() { return new ArrayList<>(playedCards); }
    public List<Buff> getActiveBuffs() { return new ArrayList<>(activeBuffs); }
    public GameEvent getCurrentEvent() { return currentEvent; }
    public String getLastCombo() { return lastCombo; }
    public int getCurrentDay() { return playerState.getCurrentDay(); }
    public int getDaysRemaining() { return MAX_DAYS - playerState.getCurrentDay() + 1; }
    public boolean isGameOver() { return gameOver; }
    public boolean isGameWon() { return gameWon; }
    public int getActionsLeft() { return playerState.getActionsLeft(); }
    public int getDeckSize() { return deck.size(); }
    public int getDiscardSize() { return discardPile.size(); }
    public boolean isWaitingForEventResolution() { return waitingForEventResolution; }
    public boolean isWaitingForGrowthChoice() { return waitingForGrowthChoice; }

    // ======================================================
    //  CONSOLE TEST
    // ======================================================

    public static void main(String[] args) {
        GameManager gm = new GameManager();
        gm.startNewGame();
        System.out.println("Day " + gm.getCurrentDay());
        System.out.println(gm.getPlayerState());
        for (int i = 0; i < gm.getHandCards().size(); i++) {
            System.out.println("  [" + i + "] " + gm.getHandCards().get(i));
        }
        gm.playCard(0);
        gm.playCard(0);
        gm.endTurn();
        System.out.println("Day " + gm.getCurrentDay());
        System.out.println(gm.getPlayerState());
    }
}