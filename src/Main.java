import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.Thread.sleep;

public class Main {
    public static ArrayList<String> wordList = new ArrayList<>();
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
        while (stream.available() != 0) {
            buf = stream.readNBytes(1)[0];
            for (char t : terminator)
                if (buf == t)
                    break mainLoop;
            output += (char) buf;
        }
        return output;
    }

    public static char readMenu(char... options) {
        String input;
        Scanner sc = new Scanner(System.in);
        while (true) {
            input = sc.nextLine();
            for (char i : options)
                if (input.equals("" + i))
                    return input.charAt(0);
            err.println("Bad input; Please try again");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            out.println("-- IKB Command Line Wordle Game --");
            out.println("p: Start new Game");
            out.println("q: Quit Game");
            out.print("p/q>");
            char menuChoice = readMenu('p', 'q');
            switch (menuChoice) {
                case 'p':
                    Random rnd = new Random();
                    play_game(wordList.get(rnd.nextInt(wordList.size()-1)));
                    break;
                case 'q':
                    System.exit(0);
            }
        }
    }

    public static String guess() {
        String guess;
        Scanner sc = new Scanner(System.in);
        while (true) {
            out.print("Make your guess. The word is 5 characters long>");
            guess = sc.nextLine();
            if (guess.length() != 5) {
                err.println("Invalid word. Must be 5 characters in length");
                continue;
            }
            return guess;
        }
    }

    public static void play_game(String word) throws InterruptedException {
        String guess = "";
        int attempts = 1;
        while (attempts <= 5 && !guess.equals(word)) {
            out.printf("Guess %s/5\n", attempts);
            guess = guess().toUpperCase();
            out.printf("Entered word: %s\n", guess.toUpperCase());
            Rating[] rating = new Rating[5];
            HashMap<Character, Integer> repeat = new HashMap<>();
            for (int i = 0 ; i < 5 ; i++) {
                if (repeat.containsKey(word.charAt(i))) {
                    int count = repeat.get(word.charAt(i));
                    count++;
                    repeat.replace(word.charAt(i), count);
                }
                else {
                    repeat.put(word.charAt(i), 1);
                }
            }
            for (int i = 0 ; i < 5 ; i++) {
                if (guess.charAt(i) == word.charAt(i)) {
                    rating[i] = Rating.GREEN;
                    int count = repeat.get(guess.charAt(i));
                    count--;
                    repeat.replace(guess.charAt(i), count);
                    for (int j = i-1 ; j >= 0 ; j--) {
                        if (guess.charAt(j) == guess.charAt(i) && rating[j] == Rating.YELLOW) {
                            rating[j] = Rating.GRAY;
                            int otherCount = repeat.get(guess.charAt(i));
                            otherCount++;
                            repeat.replace(guess.charAt(i), otherCount);
                        }
                    }
                }
                else if (word.contains("" + guess.charAt(i)) && repeat.get(guess.charAt(i)) > 0) {
                    rating[i] = Rating.YELLOW;
                    int count = repeat.get(guess.charAt(i));
                    count--;
                    repeat.replace(guess.charAt(i), count);
                }
                else {
                    rating[i] = Rating.GRAY;
                }
            }
            for (int i = 0 ; i < 5 ; i++) {
                out.printf("%s: ", guess.charAt(i));
                sleep(500);
                out.println(rating[i]);
                sleep(500);
            }
            if (guess.equals(word)) {
                out.println("\\o/ You Win \\o/");
                return;
            }
            attempts++;
        }
        err.println("You failed");
        sleep(1500);
        out.println("The word was: " + word);
        sleep(1500);
    }
}

enum Rating {
    GREEN,
    YELLOW,
    GRAY,
}