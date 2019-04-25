package development;

import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        UUID uuid = new UUID(0, 0);
        System.out.println(uuid.toString());

        UUID uuid2 = UUID.fromString("0-0-0-0-0");
        System.out.println(uuid2.toString());

        try {
            UUID uuid3 = UUID.fromString("----");
            System.out.println(uuid3.toString());
        } catch (Exception e) {
            System.out.println("Could not parse uuid3: " + e.getClass().getName());
        }
    }
}
