import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pool of random events, categorized by semester stage.
 * Phase 2: events trigger at the start of each day.
 */
public class EventPool {
    private static Random rand = new Random();

    /**
     * Get a random event based on the current day.
     * Day 1-10: Easy events (mostly positive or mild)
     * Day 11-20: Medium events (mixed)
     * Day 21-30: Hard events (mostly negative or high risk)
     */
    public static GameEvent getRandomEvent(int currentDay) {
        if (currentDay <= 10) {
            return getEasyEvent();
        } else if (currentDay <= 20) {
            return getMediumEvent();
        } else {
            return getHardEvent();
        }
    }

    private static GameEvent getEasyEvent() {
        List<GameEvent> events = new ArrayList<>();
        events.add(new GameEvent(1, "Sunny Day", "The weather is great! You feel energized.",
                0.0f, 5, 10));
        events.add(new GameEvent(2, "Free Coffee", "The campus cafe is giving away free coffee.",
                0.0f, 10, 5));
        events.add(new GameEvent(3, "Guest Lecture", "An interesting guest lecture boosts your knowledge.",
                1.0f, -2, 0));
        events.add(new GameEvent(4, "Friend Visit", "A friend drops by with snacks.",
                0.0f, 5, 15));
        events.add(new GameEvent(5, "Extended Library Hours", "The library stays open late tonight.",
                0.5f, -3, 0));
        return events.get(rand.nextInt(events.size()));
    }

    private static GameEvent getMediumEvent() {
        List<GameEvent> events = new ArrayList<>();
        events.add(new GameEvent(6, "Pop Quiz", "Surprise quiz in class!",
                -0.5f, -10, -5));
        events.add(new GameEvent(7, "Roommate Party", "Your roommate throws a loud party.",
                0.0f, -15, 10));
        events.add(new GameEvent(8, "Professor Office Hours", "Extra help session available.",
                1.5f, 0, 0));
        events.add(new GameEvent(9, "Internet Outage", "WiFi is down. Can't access online resources.",
                -1.0f, -5, -10));
        events.add(new GameEvent(10, "Study Group Invite", "Classmates invite you to study together.",
                1.0f, 0, 8));
        return events.get(rand.nextInt(events.size()));
    }

    private static GameEvent getHardEvent() {
        List<GameEvent> events = new ArrayList<>();
        events.add(new GameEvent(11, "Midterm Week", "It's midterm week! Double study effects, but high stress.",
                0.0f, -20, -15));
        events.add(new GameEvent(12, "Sickness", "You caught a cold. Everything feels harder.",
                -1.0f, -25, -20));
        events.add(new GameEvent(13, "Family Emergency", "Urgent call from home. Hard to focus.",
                -2.0f, -30, -25));
        events.add(new GameEvent(14, "Academic Warning", "You receive an academic warning. GPA must improve!",
                -0.5f, -15, -10));
        events.add(new GameEvent(15, "Final Push", "The end is near. Determination kicks in!",
                1.0f, 10, 5));
        return events.get(rand.nextInt(events.size()));
    }

    public static GameEvent getHardEventSimple() {
        List<GameEvent> events = new ArrayList<>();
        events.add(new GameEvent(100, "Pop Quiz", "Surprise test!", -1.0f, -10, -5));
        events.add(new GameEvent(101, "Bad Sleep", "You slept terribly.", 0.0f, -20, -10));
        events.add(new GameEvent(102, "Deadline Pressure", "Multiple deadlines hit.", -0.5f, -15, -10));
        return events.get(rand.nextInt(events.size()));
    }
}