import java.io.Serializable;
/**
 * Random event that triggers at the start or end of a day.
 * Events can affect GPA, mental, and happiness.
 * Full implementation in Phase 3. This is the data skeleton.
 */
public class GameEvent implements Serializable{
    private int id;
    private String title;
    private String description;
    private float gpaEffect;
    private int mentalEffect;
    private int happyEffect;

    public GameEvent(int id, String title, String description,
                     float gpaEffect, int mentalEffect, int happyEffect) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.gpaEffect = gpaEffect;
        this.mentalEffect = mentalEffect;
        this.happyEffect = happyEffect;
    }

    // ===== Getters =====
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public float getGpaEffect() { return gpaEffect; }
    public int getMentalEffect() { return mentalEffect; }
    public int getHappyEffect() { return happyEffect; }

    @Override
    public String toString() {
        return "[" + title + "] " + description + " | GPA:" + gpaEffect
                + " Mental:" + mentalEffect + " Happy:" + happyEffect;
    }
}