import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central game manager controlling the entire game loop.
 * Phase 1: Implements initialization, draw, play card, and end turn.
 * Phase 2+: Combo system, Buff system, events, AI will be added.
 */
public class GameManager {
    private PlayerState playerState;
    private List<Card> deck;            // full draw pile
    private List<Card> handCards;       // current hand (5 cards)
    private List<Card> playedCards;     // cards played this turn
    private List<Card> discardPile;     // used cards (for reshuffle later)
    private List<Buff> activeBuffs;     // currently active buffs
    private GameEvent currentEvent;     // event triggered this turn
    private boolean gameOver;
    private boolean gameWon;

    private static final int HAND_SIZE = 5;
    private static final int MAX_DAYS = 30;
    private static final float GPA_TARGET = 3.0f;

    public GameManager() {
        this.playerState = new PlayerState();
        this.handCards = new ArrayList<>();
        this.playedCards = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.activeBuffs = new ArrayList<>();
        this.currentEvent = null;
        this.gameOver = false;
        this.gameWon = false;
        this.deck = new ArrayList<>();
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
        gameOver = false;
        gameWon = false;

        buildDeck();
        shuffleDeck();
        drawCards(HAND_SIZE);
    }

    /**
     * Create all cards in the game. Phase 1: only 5 test cards.
     * More cards will be added in Phase 3.
     */
    private void buildDeck() {
        deck.clear();

        // Test Card 1: Library Study
        deck.add(new Card(1, "Library Study", "Study quietly in the library. GPA +3, Mental -1.",
                List.of(Tag.STUDY), 1, 3.0f, -1, 0));

        // Test Card 2: Coffee Boost
        deck.add(new Card(2, "Coffee Boost", "Drink coffee to stay awake. Mental +10, Happy +5.",
                List.of(Tag.REFRESH), 1, 0.0f, 10, 5));

        // Test Card 3: Gaming Break
        deck.add(new Card(3, "Gaming Break", "Play games to relieve stress. Happy +15, GPA -1.",
                List.of(Tag.ENTERTAINMENT), 1, -1.0f, 0, 15));

        // Test Card 4: All-Nighter
        deck.add(new Card(4, "All-Nighter", "Pull an all-nighter. GPA +5, Mental -20, Happy -10.",
                List.of(Tag.STUDY, Tag.STAY_UP, Tag.HIGH_RISK), 1, 5.0f, -20, -10));

        // Test Card 5: AI Study Helper
        deck.add(new Card(5, "AI Study Helper", "Use AI to summarize notes. GPA +4, Mental -5.",
                List.of(Tag.STUDY, Tag.AI), 1, 4.0f, -5, 0));

        // Test Card 6: Power Nap
        deck.add(new Card(6, "Power Nap", "Take a short nap. Mental +15, Happy +5.",
                List.of(Tag.REST), 1, 0.0f, 15, 5));

        // Test Card 7: Cram Exam
        deck.add(new Card(7, "Cram Exam", "Cram the night before the exam. GPA +6, Mental -25, Happy -15.",
                List.of(Tag.STUDY, Tag.STAY_UP, Tag.HIGH_RISK), 1, 6.0f, -25, -15));

        // Test Card 8: Social Media Scroll
        deck.add(new Card(8, "Social Media Scroll", "Endless scrolling. Happy +10, GPA -2, Mental -5.",
                List.of(Tag.ENTERTAINMENT), 1, -2.0f, -5, 10));

        // Test Card 9: Study Group
        deck.add(new Card(9, "Study Group", "Study with classmates. GPA +3, Happy +5.",
                List.of(Tag.STUDY), 1, 3.0f, 0, 5));

        // Test Card 10: Energy Drink
        deck.add(new Card(10, "Energy Drink", "Chug an energy drink. Mental +20, Happy -5.",
                List.of(Tag.REFRESH, Tag.STAY_UP, Tag.HIGH_RISK), 1, 0.0f, 20, -5));
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
        if (handIndex < 0 || handIndex >= handCards.size()) {
            System.out.println("[ERROR] Invalid card index.");
            return false;
        }

        Card card = handCards.get(handIndex);

        if (playerState.getActionsLeft() < card.getCost()) {
            System.out.println("[ERROR] Not enough actions to play: " + card.getName());
            return false;
        }

        // Apply card effects to player state
        playerState.applyCardEffects(card);

        // Move card from hand to played area
        handCards.remove(handIndex);
        playedCards.add(card);

        System.out.println("[PLAYED] " + card.getName() + " -> " + card);
        return true;
    }

    // ======================================================
    //  TURN MANAGEMENT
    // ======================================================

    /**
     * End the current turn: discard played cards, draw new hand, advance day.
     */
    public void endTurn() {
        // Move played cards to discard pile
        discardPile.addAll(playedCards);
        playedCards.clear();

        // Discard remaining hand cards too (player didn't play them)
        discardPile.addAll(handCards);
        handCards.clear();

        // Advance to next day
        playerState.nextDay();

        // Check win/lose conditions
        checkGameEndConditions();

        // Draw new hand if game continues
        if (!gameOver) {
            drawCards(HAND_SIZE);
        }

        System.out.println("[TURN END] Moving to Day " + playerState.getCurrentDay());
        System.out.println(playerState);
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
    //  STUB METHODS (for Phase 2+)
    // ======================================================

    /**
     * Trigger a random event for the current day.
     * Phase 2+: will select from event pool based on difficulty.
     */
    public void triggerRandomEvent() {
        // Stub - Phase 2 implementation
    }

    /**
     * Player makes a growth choice after a semester milestone.
     * Phase 2+ implementation.
     */
    public void makeGrowthChoice(int choiceIndex) {
        // Stub - Phase 2 implementation
    }

    /**
     * Player closes the event popup.
     * Phase 2+ implementation.
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
    public int getCurrentDay() { return playerState.getCurrentDay(); }
    public int getDaysRemaining() { return MAX_DAYS - playerState.getCurrentDay() + 1; }
    public boolean isGameOver() { return gameOver; }
    public boolean isGameWon() { return gameWon; }
    public int getActionsLeft() { return playerState.getActionsLeft(); }
    public int getDeckSize() { return deck.size(); }
    public int getDiscardSize() { return discardPile.size(); }

    // ======================================================
    //  CONSOLE TEST (Phase 1 self-testing)
    // ======================================================

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  CAMPUS SURVIVOR - Phase 1 Test");
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
        System.out.println("Actions left: " + gm.getActionsLeft());

        // Play another card if possible
        System.out.println("\n--- Playing card [0] again ---");
        gm.playCard(0);
        System.out.println(gm.getPlayerState());
        System.out.println("Actions left: " + gm.getActionsLeft());

        // End turn
        System.out.println("\n--- Ending Turn ---");
        gm.endTurn();

        // Print new hand
        System.out.println("\n=== Day " + gm.getCurrentDay() + " ===");
        System.out.println(gm.getPlayerState());
        System.out.println("\nNew Hand Cards:");
        for (int i = 0; i < gm.getHandCards().size(); i++) {
            System.out.println("  [" + i + "] " + gm.getHandCards().get(i));
        }

        System.out.println("\n====================================");
        System.out.println("  Phase 1 Test Complete");
        System.out.println("====================================");
    }
}