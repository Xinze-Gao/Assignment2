import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ComboSystem detects tag combinations from played cards
 * and triggers bonus effects on the player state.
 */
public class ComboSystem {

    /**
     * Check all played cards this turn for tag combos.
     * @param playedCards cards played this turn
     * @param state player state to apply bonus effects to
     * @return combo name if triggered, null otherwise
     */
    public static String checkCombo(List<Card> playedCards, PlayerState state) {
        if (playedCards == null || playedCards.size() < 2) {
            return null;
        }

        // Collect all tags from played cards this turn
        Set<Tag> tags = new HashSet<>();
        for (Card c : playedCards) {
            tags.addAll(c.getTags());
        }

        // === Combo Definitions ===

        // Combo 1: REFRESH + STUDY = "Caffeinated Study" -> bonus GPA +2
        if (tags.contains(Tag.REFRESH) && tags.contains(Tag.STUDY)) {
            state.applyComboEffect(2.0f, 0, 3);
            return "Caffeinated Study! Bonus GPA +2";
        }

        // Combo 2: STAY_UP + STUDY = "Night Mode" -> bonus GPA +3, mental -5
        if (tags.contains(Tag.STAY_UP) && tags.contains(Tag.STUDY)) {
            state.applyComboEffect(3.0f, -5, -3);
            return "Night Mode! GPA +3, Mental -5";
        }

        // Combo 3: AI + STUDY = "AI-Powered Study" -> bonus GPA +2, mental -3
        if (tags.contains(Tag.AI) && tags.contains(Tag.STUDY)) {
            state.applyComboEffect(2.0f, -3, 2);
            return "AI-Powered Study! GPA +2, Mental -3";
        }

        // Combo 4: HIGH_RISK + STAY_UP = "Extreme Mode" -> bonus GPA +4, mental -10
        if (tags.contains(Tag.HIGH_RISK) && tags.contains(Tag.STAY_UP)) {
            state.applyComboEffect(4.0f, -10, -5);
            return "Extreme Mode! GPA +4, Mental -10";
        }

        // Combo 5: ENTERTAINMENT + REST = "Quality Break" -> bonus Happy +10, Mental +5
        if (tags.contains(Tag.ENTERTAINMENT) && tags.contains(Tag.REST)) {
            state.applyComboEffect(0.0f, 5, 10);
            return "Quality Break! Happy +10, Mental +5";
        }

        // Combo 6: REFRESH + STAY_UP = "Overdrive" -> Mental +15, Happy -5
        if (tags.contains(Tag.REFRESH) && tags.contains(Tag.STAY_UP)) {
            state.applyComboEffect(0.0f, 15, -5);
            return "Overdrive! Mental +15, Happy -5";
        }

        return null; // No combo triggered
    }
}