import java.io.Serializable;
/**
 * Passive modifier that changes game rules while active.
 */
public class Buff implements Serializable{
    private int id;
    private String name;
    private String description;
    private boolean isActive;

    public Buff(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = false;
    }

    /**
     * Apply this buff's effect when a card is played.
     * Called before the card's own effects.
     * Returns the modified card (or the same card if no change).
     */
    public void modifyCardEffect(Card card, PlayerState state) {
        if (!isActive) return;

        switch (id) {
            case 1: // Caffeine Addiction: REFRESH cards give +5 Mental
                if (card.hasTag(Tag.REFRESH)) {
                    state.applyRawEffect(0, 5, 0);
                }
                break;
            case 2: // Study Machine: STUDY cards give +1 GPA
                if (card.hasTag(Tag.STUDY)) {
                    state.applyRawEffect(1.0f, 0, 0);
                }
                break;
            case 3: // Night Owl: STAY_UP cards cost 0
                // Handled in GameManager before checking cost
                break;
            case 4: // Gambler: HIGH_RISK double GPA but extra -5 Mental
                if (card.hasTag(Tag.HIGH_RISK)) {
                    state.applyRawEffect(card.getGpaEffect(), -5, 0);
                }
                break;
            case 5: // AI Mastery: AI cards double GPA
                if (card.hasTag(Tag.AI)) {
                    state.applyRawEffect(card.getGpaEffect(), 0, 0);
                }
                break;
            case 6: // Iron Will: Mental loss reduced by 3
                if (card.getMentalEffect() < 0) {
                    state.applyRawEffect(0, 3, 0);
                }
                break;
            case 7: // Optimist: all cards +3 Happy
                state.applyRawEffect(0, 0, 3);
                break;
            case 8: // Efficient: +1 action per turn
                // Handled in GameManager at turn start
                break;
            case 9: // Resilient: Mental floor at 20
                // Handled in PlayerState clamp
                break;
            case 10: // Fresh Air: REST cards give +2 GPA
                if (card.hasTag(Tag.REST)) {
                    state.applyRawEffect(2.0f, 0, 0);
                }
                break;
        }
    }

    public void activate() { this.isActive = true; }
    public void deactivate() { this.isActive = false; }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }

    @Override
    public String toString() {
        return (isActive ? "[ON] " : "[OFF] ") + name + " - " + description;
    }
}