import javax.tools.Tool;
import java.util.Random;

public class Tools {

    public Tools()
    {

    }

    public static String randomString(final int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static String randomPlayerName()
    {
        String newName = "player";
        newName += Tools.randomInt(0, 20000);
        return newName;
    }

    public static int randomInt(int min, int max)
    {
        Random rand = new Random();

        return rand.nextInt(max) + min;
    }

    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


}
