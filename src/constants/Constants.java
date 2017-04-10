package constants;

public class Constants {
    public static final int MAX_SLOTS = 3;
    public static final int MAX_NUMBER_CHARM_SKILL = 2;
    public static final int GENERATED_EQUIPMENT_ID = -1;

    public static final int MAX_SKILL = 10;
    public static final int MAX_SKILL_POINT = 90;

    public static final int MAX_PROGRESS_BAR = 100;

    // offset hyper threading.
    public static final int THREAD_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);

}
