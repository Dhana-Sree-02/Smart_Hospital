package smarthospital.util;
/** Simple identifiers for the classroom application. */
public final class Ids {
    private static int patient=1006, appointment=2004;
    private Ids() {
    }
    public static String patient() {
        return "P"+patient++;
    }
    public static String appointment() {
        return "A"+appointment++;
    }
}
