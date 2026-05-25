import java.util.List;
import java.io.Serializable;

/**
 * Represents a single card in the game.
 * Each card has effects on GPA, mental, and happiness values.
 * Tags are used for Combo detection.
 */
public class Card implements Serializable{
    private int id;
    private String name;
    private String description;
    private List<Tag> tags;
    private int cost;           // action points required to play
    private float gpaEffect;    // change to GPA (can be decimal)
    private int mentalEffect;   // change to mental value
    private int happyEffect;    // change to happiness value

    public Card(int id, String name, String description, List<Tag> tags,
                int cost, float gpaEffect, int mentalEffect, int happyEffect) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.cost = cost;
        this.gpaEffect = gpaEffect;
        this.mentalEffect = mentalEffect;
        this.happyEffect = happyEffect;
    }

    // ===== Getters =====
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Tag> getTags() { return tags; }
    public int getCost() { return cost; }
    public float getGpaEffect() { return gpaEffect; }
    public int getMentalEffect() { return mentalEffect; }
    public int getHappyEffect() { return happyEffect; }

    /**
     * Check if this card has a specific tag.
     */
    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    @Override
    public String toString() {
        return name + " | Cost:" + cost + " | GPA:" + (gpaEffect >= 0 ? "+" : "") + gpaEffect
                + " | Mental:" + (mentalEffect >= 0 ? "+" : "") + mentalEffect
                + " | Happy:" + (happyEffect >= 0 ? "+" : "") + happyEffect;
    }
}