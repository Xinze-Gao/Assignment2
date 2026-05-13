/**
 * Passive modifier that changes game rules while active.
 * Buffs are unlocked through growth choices after each semester.
 * Full implementation in Phase 3. This is the data skeleton.
 */
public class Buff {
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

    public void activate() { this.isActive = true; }
    public void deactivate() { this.isActive = false; }

    // ===== Getters =====
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }

    @Override
    public String toString() {
        return (isActive ? "[ON] " : "[OFF] ") + name + " - " + description;
    }
}