/**
 * Holds all player status values.
 * GPA is the primary score. Mental reaching 0 = game over.
 * Happiness is a secondary value affecting events.
 */
public class PlayerState {
    private float gpa;
    private int mental;
    private int happiness;
    private int actionsLeft;     // actions available this turn
    private int currentDay;

    private static final float GPA_MAX = 4.0f;
    private static final float GPA_MIN = 0.0f;
    private static final float GPA_PASS = 1.0f;   // below this = academic failure
    private static final int MENTAL_MAX = 100;
    private static final int MENTAL_MIN = 0;
    private static final int HAPPY_MAX = 100;
    private static final int HAPPY_MIN = 0;

    public PlayerState() {
        this.gpa = 2.0f;
        this.mental = 100;
        this.happiness = 80;
        this.actionsLeft = 3;
        this.currentDay = 1;
    }

    /**
     * Apply the numeric effects from a played card.
     */
    public void applyCardEffects(Card card) {
        this.gpa = clamp(this.gpa + card.getGpaEffect(), GPA_MIN, GPA_MAX);
        this.mental = clamp(this.mental + card.getMentalEffect(), MENTAL_MIN, MENTAL_MAX);
        this.happiness = clamp(this.happiness + card.getHappyEffect(), HAPPY_MIN, HAPPY_MAX);
        this.actionsLeft -= card.getCost();
    }

    /**
     * Reset actions to full at the start of a new turn.
     */
    public void resetActions() {
        this.actionsLeft = 3;
    }

    /**
     * Advance to the next day.
     */
    public void nextDay() {
        this.currentDay++;
        resetActions();
    }

    /**
     * Returns true if mental has reached zero (survival failure).
     */
    public boolean isMentalDepleted() {
        return this.mental <= MENTAL_MIN;
    }

    /**
     * Returns true if GPA drops below the passing threshold.
     */
    public boolean isGPAFailed() {
        return this.gpa < GPA_PASS;
    }

    // ===== Getters =====
    public float getGpa() { return gpa; }
    public int getMental() { return mental; }
    public int getHappiness() { return happiness; }
    public int getActionsLeft() { return actionsLeft; }
    public int getCurrentDay() { return currentDay; }

    // ===== Utility =====
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public String toString() {
        return "Day:" + currentDay + " | GPA:" + String.format("%.1f", gpa)
                + " | Mental:" + mental + "/" + MENTAL_MAX
                + " | Happy:" + happiness + "/" + HAPPY_MAX
                + " | Actions:" + actionsLeft;
    }
}