import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ComboSystem detects tag combinations and streaks from played cards.
 */
public class ComboSystem {

    /**
     * Check 2-tag combos.
     */
    public static String checkCombo(List<Card> playedCards, PlayerState state) {
        if (playedCards == null || playedCards.size() < 2) return null;

        Set<Tag> tags = new HashSet<>();
        for (Card c : playedCards) {
            tags.addAll(c.getTags());
        }

        if (tags.contains(Tag.REFRESH) && tags.contains(Tag.STUDY)) {
            state.applyComboEffect(2.0f, 0, 3);
            return "Caffeinated Study! Bonus GPA +2";
        }
        if (tags.contains(Tag.STAY_UP) && tags.contains(Tag.STUDY)) {
            state.applyComboEffect(3.0f, -5, -3);
            return "Night Mode! GPA +3, Mental -5";
        }
        if (tags.contains(Tag.AI) && tags.contains(Tag.STUDY)) {
            state.applyComboEffect(2.0f, -3, 2);
            return "AI-Powered Study! GPA +2, Mental -3";
        }
        if (tags.contains(Tag.HIGH_RISK) && tags.contains(Tag.STAY_UP)) {
            state.applyComboEffect(4.0f, -10, -5);
            return "Extreme Mode! GPA +4, Mental -10";
        }
        if (tags.contains(Tag.ENTERTAINMENT) && tags.contains(Tag.REST)) {
            state.applyComboEffect(0.0f, 5, 10);
            return "Quality Break! Happy +10, Mental +5";
        }
        if (tags.contains(Tag.REFRESH) && tags.contains(Tag.STAY_UP)) {
            state.applyComboEffect(0.0f, 15, -5);
            return "Overdrive! Mental +15, Happy -5";
        }

        return null;
    }

    /**
     * Check streak: 3+ cards with the same tag.
     */
    public static String checkStreak(List<Card> playedCards, PlayerState state) {
        if (playedCards == null || playedCards.size() < 3) return null;

        java.util.Map<Tag, Integer> tagCount = new java.util.HashMap<>();
        for (Card c : playedCards) {
            for (Tag t : c.getTags()) {
                tagCount.put(t, tagCount.getOrDefault(t, 0) + 1);
            }
        }

        for (java.util.Map.Entry<Tag, Integer> entry : tagCount.entrySet()) {
            if (entry.getValue() >= 3) {
                Tag tag = entry.getKey();
                switch (tag) {
                    case STUDY:
                        state.applyComboEffect(5.0f, 0, 0);
                        return "STUDY STREAK x" + entry.getValue() + "! GPA +5!";
                    case REFRESH:
                        state.applyComboEffect(0.0f, 20, 10);
                        return "ENERGY STREAK x" + entry.getValue() + "! Mental +20!";
                    case ENTERTAINMENT:
                        state.applyComboEffect(-2.0f, 0, 25);
                        return "FUN STREAK x" + entry.getValue() + "! Happy +25!";
                    case HIGH_RISK:
                        state.applyComboEffect(8.0f, -15, -10);
                        return "DANGER STREAK x" + entry.getValue() + "! GPA +8!";
                    case AI:
                        state.applyComboEffect(6.0f, -5, 0);
                        return "AI STREAK x" + entry.getValue() + "! GPA +6!";
                    case STAY_UP:
                        state.applyComboEffect(4.0f, -20, -5);
                        return "NIGHT STREAK x" + entry.getValue() + "! GPA +4!";
                    case REST:
                        state.applyComboEffect(0.0f, 25, 15);
                        return "REST STREAK x" + entry.getValue() + "! Mental +25!";
                    default:
                        state.applyComboEffect(3.0f, 5, 5);
                        return "STREAK x" + entry.getValue() + "!";
                }
            }
        }
        return null;
    }
}