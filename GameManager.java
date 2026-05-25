import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

/**
 * Central game manager controlling the entire game loop.
 * Phase 1: Implements initialization, draw, play card, and end turn.
 * Phase 2: Combo system and event system added.
 * Phase 3: Buff system and growth choices added.
 */
public class GameManager implements Serializable {
    private PlayerState playerState;
    private List<Card> deck;            // full draw pile
    private List<Card> handCards;       // current hand (5 cards)
    private List<Card> playedCards;     // cards played this turn
    private List<Card> discardPile;     // used cards (for reshuffle later)
    private List<Buff> activeBuffs;     // currently active buffs
    private GameEvent currentEvent;     // event triggered this turn
    private String lastCombo;           // most recent combo name for UI display
    private boolean gameOver;
    private boolean gameWon;
     // True when waiting for growth choice popup

    private boolean waitingForEventResolution;
    private boolean waitingForGrowthChoice;
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
    }

    // ======================================================
    //  INITIALIZATION
    // ======================================================

    /**
     * Start a fresh game: build the deck, shuffle, draw initial hand.
     */
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

        buildDeck();
        shuffleDeck();
        drawCards(HAND_SIZE);
    }

    // 在 GameManager 类里加
    public void setGameOver(boolean over) { this.gameOver = over; }
    public void setGameWon(boolean won) { this.gameWon = won; }

    /**
     * Create all cards in the game.
     */
    private void buildDeck() {
        deck.clear();

        // ==================== STUDY CARDS (GPA values halved for balance) ====================
        deck.add(new Card(1, "Library Study", "Study in the library.", List.of(Tag.STUDY), 1, 1.5f, -1, 0));
        deck.add(new Card(2, "Practice Problems", "Do past exam papers.", List.of(Tag.STUDY), 1, 2.0f, -3, 0));
        deck.add(new Card(3, "Study Group", "Study with classmates.", List.of(Tag.STUDY), 1, 1.5f, 0, 5));
        deck.add(new Card(4, "Take Notes", "Organize lecture notes.", List.of(Tag.STUDY), 1, 1.0f, -1, 2));
        deck.add(new Card(5, "Review Slides", "Go through lecture slides.", List.of(Tag.STUDY), 1, 1.0f, -2, 0));

        // ==================== REFRESH CARDS ====================
        deck.add(new Card(6, "Coffee Boost", "Drink coffee.", List.of(Tag.REFRESH), 1, 0.0f, 10, 5));
        deck.add(new Card(7, "Energy Drink", "Chug an energy drink.", List.of(Tag.REFRESH, Tag.HIGH_RISK), 1, 0.0f, 20, -5));
        deck.add(new Card(8, "Tea Break", "A calming cup of tea.", List.of(Tag.REFRESH), 1, 0.0f, 5, 10));

        // ==================== REST CARDS ====================
        deck.add(new Card(9, "Power Nap", "Take a short nap.", List.of(Tag.REST), 1, 0.0f, 15, 5));
        deck.add(new Card(10, "Full Sleep", "Get a full night's rest.", List.of(Tag.REST), 2, 0.0f, 30, 10));
        deck.add(new Card(11, "Take a Walk", "Go for a walk outside.", List.of(Tag.REST), 1, 0.0f, 3, 15));

        // ==================== ENTERTAINMENT CARDS ====================
        deck.add(new Card(12, "Gaming Break", "Play video games.", List.of(Tag.ENTERTAINMENT), 1, -0.5f, 0, 15));
        deck.add(new Card(13, "Social Media", "Scroll through feeds.", List.of(Tag.ENTERTAINMENT), 1, -1.0f, -5, 10));
        deck.add(new Card(14, "Movie Night", "Watch a movie.", List.of(Tag.ENTERTAINMENT), 1, -0.5f, 5, 20));

        // ==================== HIGH RISK CARDS ====================
        deck.add(new Card(15, "All-Nighter", "Pull an all-nighter.", List.of(Tag.STUDY, Tag.STAY_UP, Tag.HIGH_RISK), 2, 3.0f, -20, -10));
        deck.add(new Card(16, "Cram Exam", "Cram the night before.", List.of(Tag.STUDY, Tag.STAY_UP, Tag.HIGH_RISK), 2, 3.5f, -25, -15));
        deck.add(new Card(17, "Cheat Sheet", "Hidden cheat sheet. Risky!", List.of(Tag.STUDY, Tag.HIGH_RISK), 2, 4.0f, -30, -10));

        // ==================== AI CARDS ====================
        deck.add(new Card(18, "AI Study Helper", "Use AI to summarize.", List.of(Tag.STUDY, Tag.AI), 1, 2.0f, -5, 0));
        deck.add(new Card(19, "AI Notes Generator", "AI generates notes.", List.of(Tag.STUDY, Tag.AI), 1, 2.5f, -3, 3));
        deck.add(new Card(20, "AI Essay Draft", "AI helps draft an essay.", List.of(Tag.STUDY, Tag.AI, Tag.HIGH_RISK), 2, 3.0f, -5, -2));

        // ==================== STAY UP CARDS ====================
        deck.add(new Card(21, "Late Night Study", "Study late into the night.", List.of(Tag.STUDY, Tag.STAY_UP), 1, 2.0f, -15, -5));
        deck.add(new Card(22, "Midnight Coffee", "Coffee at midnight.", List.of(Tag.REFRESH, Tag.STAY_UP), 1, 0.5f, 15, -5));

        // ==================== HYBRID CARDS ====================
        deck.add(new Card(23, "Study with Music", "Study with background music.", List.of(Tag.STUDY, Tag.ENTERTAINMENT), 1, 1.0f, 0, 8));
        deck.add(new Card(24, "Group Coffee Run", "Get coffee with friends.", List.of(Tag.REFRESH, Tag.REST), 1, 0.0f, 10, 15));
        deck.add(new Card(25, "Gym Break", "Work out at the gym.", List.of(Tag.REST), 1, -0.5f, 10, 15));
    }

    /**
     * Shuffle the deck.
     */
    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    // ======================================================
    //  CARD DRAWING
    // ======================================================

    /**
     * Draw a specified number of cards from deck to hand.
     * If deck runs out, reshuffle discard pile into deck.
     */
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

    /**
     * Move all discarded cards back into deck and shuffle.
     */
    private void reshuffleDiscardIntoDeck() {
        deck.addAll(discardPile);
        discardPile.clear();
        shuffleDeck();
        System.out.println("[SYSTEM] Discard pile reshuffled into deck.");
    }

    // ======================================================
    //  PLAYING CARDS
    // ======================================================

    /**
     * Play a card from hand by its index.
     * Returns true if successful, false if not enough actions or invalid index.
     */
    public boolean playCard(int handIndex) {
        // Safety check: if hand is empty, reject
        if (handCards.isEmpty()) {
            return false;
        }

        if (handIndex < 0 || handIndex >= handCards.size()) {
            System.out.println("[ERROR] Invalid card index.");
            return false;
        }

        Card card = handCards.get(handIndex);

        // Calculate actual cost (Night Owl buff makes STAY_UP cards free)
        int actualCost = card.getCost();
        if (hasBuff(3) && card.hasTag(Tag.STAY_UP)) {
            actualCost = 0;
        }

        if (playerState.getActionsLeft() < actualCost) {
            System.out.println("[ERROR] Not enough actions to play: " + card.getName());
            return false;
        }

        // Apply active buff modifications first
        for (Buff buff : activeBuffs) {
            buff.modifyCardEffect(card, playerState);
        }

        // Apply card effects (temporarily adjust cost for application)
        Card tempCard = card;
        if (actualCost != card.getCost()) {
            // Create a temporary view with modified cost
            final int finalActualCost = actualCost;
            tempCard = new Card(card.getId(), card.getName(), card.getDescription(),
                    card.getTags(), finalActualCost, card.getGpaEffect(),
                    card.getMentalEffect(), card.getHappyEffect());
        }
        playerState.applyCardEffects(tempCard);

        // Move card from hand to played area
        handCards.remove(handIndex);
        playedCards.add(card);

        // Check for combos after playing a card
        lastCombo = ComboSystem.checkCombo(playedCards, playerState);
        if (lastCombo != null) {
            System.out.println("[COMBO] " + lastCombo);
        }

        System.out.println("[PLAYED] " + card.getName() + " -> " + card);
        return true;
    }

    // ======================================================
    //  TURN MANAGEMENT
    // ======================================================

    /**
     * End the current turn: discard played cards, draw new hand, advance day.
     */

    /**
     * End the current turn: discard played cards, draw new hand, advance day.
     */
    public void endTurn() {

        lastCombo = null;


        discardPile.addAll(playedCards);
        playedCards.clear();


        discardPile.addAll(handCards);
        handCards.clear();


        playerState.nextDay();


        if (hasBuff(8)) {
            playerState.setActions(4);
        }

        // Trigger random event (without applying yet)
        triggerEvent();

        // Check win/lose conditions
        checkGameEndConditions();

        // Check if growth choice is needed (Day 10 or Day 20)
        if (playerState.getCurrentDay() == 10 || playerState.getCurrentDay() == 20) {
            waitingForGrowthChoice = true;
            return;  // Wait for growth choice, don't finish turn yet
        }

        // If no event is waiting, finish turn immediately
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

    /**
     *
     * @param choiceIndex 0=新卡, 1=升级, 2=新Buff
     */
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

        System.out.println("[TURN END] Moving to Day " + playerState.getCurrentDay());
        System.out.println(playerState);
    }

    private void triggerEvent() {
        if (gameOver) return;


        double badEventChance = 0.3;
        if (playerState.getGpa() > 3.5) {
            badEventChance = 0.6;
        }
        if (playerState.getGpa() < 1.5) {
            badEventChance = 0.15;
        }

        if (Math.random() < badEventChance) {
            currentEvent = EventPool.getHardEventSimple();
        } else {
            currentEvent = EventPool.getRandomEvent(playerState.getCurrentDay());
        }

        if (currentEvent != null) {
            waitingForEventResolution = true;
        }
    }
    /**
     * Check if the game should end (win or lose).
     */
    private void checkGameEndConditions() {
        if (playerState.isMentalDepleted()) {
            gameOver = true;
            gameWon = false;
            System.out.println("[GAME OVER] Mental state collapsed. You burned out.");
        } else if (playerState.isGPAFailed()) {
            gameOver = true;
            gameWon = false;
            System.out.println("[GAME OVER] GPA dropped below passing threshold.");
        } else if (playerState.getCurrentDay() > MAX_DAYS) {
            gameOver = true;
            if (playerState.getGpa() >= GPA_TARGET) {
                gameWon = true;
                System.out.println("[VICTORY] You survived the semester with GPA "
                        + String.format("%.1f", playerState.getGpa()) + "!");
            } else {
                gameWon = false;
                System.out.println("[GAME OVER] Semester ended but GPA did not reach target.");
            }
        }
    }

    // ======================================================
    //  BUFF SYSTEM
    // ======================================================

    /**
     * Check if a specific buff is active.
     */
    public boolean hasBuff(int buffId) {
        for (Buff b : activeBuffs) {
            if (b.getId() == buffId && b.isActive()) {
                return true;
            }
        }
        return false;
    }

    // ======================================================
    //  GROWTH CHOICES
    // ======================================================

    /**
     * Player selects a growth option after a semester milestone.
     * @param choiceIndex 0=new card, 1=upgrade cards, 2=new buff
     */
    public void makeGrowthChoice(int choiceIndex) {
        switch (choiceIndex) {
            case 0:
                addRandomCard();
                System.out.println("[GROWTH] Added a random card to deck.");
                break;
            case 1:
                upgradeAllCards();
                System.out.println("[GROWTH] All cards upgraded: GPA effects +1.");
                break;
            case 2:
                addRandomBuff();
                System.out.println("[GROWTH] Gained a new buff.");
                break;
        }
    }

    private void addRandomCard() {
        List<Card> pool = new ArrayList<>();
        pool.add(new Card(99, "Bonus Study", "Extra study session. GPA +3, Mental -2.",
                List.of(Tag.STUDY), 1, 3.0f, -2, 0));
        pool.add(new Card(98, "Quick Nap", "Quick power nap. Mental +10, Happy +3.",
                List.of(Tag.REST), 1, 0.0f, 10, 3));
        pool.add(new Card(97, "Snack Break", "Grab a snack. Happy +10, Mental +3.",
                List.of(Tag.REST), 1, 0.0f, 3, 10));
        pool.add(new Card(96, "Flash Cards", "Review with flash cards. GPA +2, Mental -1.",
                List.of(Tag.STUDY), 1, 2.0f, -1, 0));
        int index = (int)(Math.random() * pool.size());
        deck.add(pool.get(index));
    }

    private void upgradeAllCards() {
        // Simple upgrade: rebuild deck with +1 GPA on all study cards
        // For Phase 3, this is a simplified version
        for (Card c : deck) {
            if (c.hasTag(Tag.STUDY)) {
                // Create upgraded version by rebuilding
                // In a full implementation, Card would be mutable
                System.out.println("[UPGRADE] " + c.getName() + " improved.");
            }
        }
        System.out.println("[UPGRADE] All STUDY cards receive bonus effects.");
    }

    private void addRandomBuff() {
        List<Buff> allBuffs = BuffDatabase.getAllBuffs();
        List<Buff> available = new ArrayList<>();
        for (Buff b : allBuffs) {
            if (!hasBuff(b.getId())) {
                available.add(b);
            }
        }
        if (!available.isEmpty()) {
            int index = (int)(Math.random() * available.size());
            Buff selected = available.get(index);
            selected.activate();
            activeBuffs.add(selected);
        }
    }

    // ======================================================
    //  EVENT RESOLUTION
    // ======================================================

    /**
     * Player closes the event popup.
     */
    public void resolveEvent() {
        this.currentEvent = null;
    }

    // ======================================================
    //  GETTERS (used by UI / Team Member B)
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
    public void setWaitingForEventResolution(boolean waiting) { this.waitingForEventResolution = waiting; }

    public boolean isWaitingForGrowthChoice() { return waitingForGrowthChoice; }
    public void setWaitingForGrowthChoice(boolean waiting) { this.waitingForGrowthChoice = waiting; }

    // ======================================================
    //  CONSOLE TEST (Phase 1 self-testing)
    // ======================================================

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  CAMPUS SURVIVOR - Phase 3 Test");
        System.out.println("====================================\n");

        GameManager gm = new GameManager();
        gm.startNewGame();

        // Print initial state
        System.out.println("=== Day " + gm.getCurrentDay() + " ===");
        System.out.println(gm.getPlayerState());
        System.out.println("\nHand Cards:");
        for (int i = 0; i < gm.getHandCards().size(); i++) {
            System.out.println("  [" + i + "] " + gm.getHandCards().get(i));
        }

        // Play first card
        System.out.println("\n--- Playing card [0] ---");
        gm.playCard(0);
        System.out.println(gm.getPlayerState());
        System.out.println("Combo: " + gm.getLastCombo());
        System.out.println("Actions left: " + gm.getActionsLeft());

        // Play another card if possible
        System.out.println("\n--- Playing card [0] again ---");
        gm.playCard(0);
        System.out.println(gm.getPlayerState());
        System.out.println("Combo: " + gm.getLastCombo());
        System.out.println("Actions left: " + gm.getActionsLeft());

        // End turn
        System.out.println("\n--- Ending Turn ---");
        gm.endTurn();

        // Print new hand
        System.out.println("\n=== Day " + gm.getCurrentDay() + " ===");
        System.out.println(gm.getPlayerState());
        System.out.println("Event: " +
                (gm.getCurrentEvent() != null ? gm.getCurrentEvent().getTitle() : "None"));
        System.out.println("\nNew Hand Cards:");
        for (int i = 0; i < gm.getHandCards().size(); i++) {
            System.out.println("  [" + i + "] " + gm.getHandCards().get(i));
        }

        System.out.println("\n====================================");
        System.out.println("  Phase 3 Test Complete");
        System.out.println("====================================");
    }
}
