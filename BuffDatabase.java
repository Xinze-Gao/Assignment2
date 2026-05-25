import java.util.ArrayList;
import java.util.List;

/**
 * Database of all available buffs in the game.
 */
public class BuffDatabase {

    public static List<Buff> getAllBuffs() {
        List<Buff> buffs = new ArrayList<>();

        buffs.add(new Buff(1, "Caffeine Addiction", "All REFRESH cards restore 5 extra Mental."));
        buffs.add(new Buff(2, "Study Machine", "All STUDY cards give +1 bonus GPA."));
        buffs.add(new Buff(3, "Night Owl", "STAY_UP cards cost 0 actions."));
        buffs.add(new Buff(4, "Gambler", "HIGH_RISK cards give double GPA, but extra -5 Mental."));
        buffs.add(new Buff(5, "AI Mastery", "AI cards give double GPA."));
        buffs.add(new Buff(6, "Iron Will", "Mental loss from all cards reduced by 3."));
        buffs.add(new Buff(7, "Optimist", "All cards give +3 bonus Happy."));
        buffs.add(new Buff(8, "Efficient", "You start each turn with 4 actions instead of 3."));
        buffs.add(new Buff(9, "Resilient", "Mental cannot drop below 20."));
        buffs.add(new Buff(10, "Fresh Air", "REST cards also give +2 GPA."));

        return buffs;
    }
}