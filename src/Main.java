import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.System.out;

public class Main {
    public static ArrayList<String> wordList;
    static {
        try (FileInputStream listFile = new FileInputStream("src/WordList")) {
            while (listFile.available() != 0) {
                wordList.add(read_to(listFile, ' '));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String read_to(InputStream stream, char... terminator) throws IOException {
        String output = "";
        byte buf;
        mainLoop:
        while (true) {
            buf = stream.readNBytes(1)[0];
            for (char t : terminator)
                if (buf == t)
                    break mainLoop;
            output += (char) buf;
        }
        return output;
    }

    public static char readMenu(char... options) throws IOException {
        String input;
        while (true) {
            input = read_to(System.in, '\n');
            for (char i : options)
                if (input.equals("" + i))
                    return input.charAt(0);
            out.println("Bad input; Please try again");
        }
    }

    public static void main(String[] args) throws IOException {
        out.println("-- IKB Command Line Wordle Game --");
        out.println("p: Start new Game");
        out.println("r: Show last results");
        out.println("q: Quit Game");
        out.println("p/r/q>");
        char menuChoice = readMenu('p', 'r', 'q');
    }
}