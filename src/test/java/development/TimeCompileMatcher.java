package development;

import com.cmdjojo.util.Time;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeCompileMatcher {
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("([0-9\\s]*[0-9](?:.[0-9\\s]*[0-9])?)\\s*([a-zA-Z]+)");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter text to format:");
            String input = scanner.nextLine();
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                System.out.printf("Number: '%s'%nUnit: '%s'%n", matcher.group(1), matcher.group(2));
            }
            try {
                System.out.println("Millis: " + Time.parse(input));
            } catch (Exception e) {
                System.out.println("Couldnt convert to millis");
            }
        }

    }
}
