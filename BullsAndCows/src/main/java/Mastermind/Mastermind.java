package Mastermind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Mastermind {

    private static final String[] m_Words = {"01010", "PROGRAMMING", "JAVA", "NSU", "AUTOPASS", "JETBRAINS", "LOVE", "HAZEL"};
    private final String m_Word;

    public Mastermind() {
        Random random = new Random();
        m_Word = m_Words[random.nextInt(m_Words.length)];
        System.out.println("Computer came up with the word of length: " + m_Word.length());
    }

    public void Run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("Your guess: ");
                String input;
                while ((input = reader.readLine()).length() != m_Word.length() || !input.matches("^[a-zA-Z0-9]*$")) {
                    System.out.println("Your input must be exactly (" + m_Word.length() + ") characters long and contain only alphanumeric characters.");
                }

                if (CompareUserInput(input)) {
                    System.out.println("GG!");
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private boolean CompareUserInput(String userInput) {
        Integer bulls = 0, cows = 0;

        Map<Character, Integer> guessTable = new HashMap<>();
        for (char c : m_Word.toCharArray()) {
            guessTable.put(c, guessTable.getOrDefault(c, 0) + 1);
        }

        for (Integer i = 0; i < m_Word.length(); ++i) {
            char upperCh = Character.toUpperCase(userInput.charAt(i));

            if (m_Word.charAt(i) == upperCh) {
                ++bulls;
                guessTable.put(upperCh, guessTable.getOrDefault(upperCh, 0) - 1);
            } else if (m_Word.contains(String.valueOf(upperCh)) && guessTable.get(upperCh) > 0) {
                ++cows;
                guessTable.put(upperCh, guessTable.getOrDefault(upperCh, 0) - 1);
            }
        }

        if (bulls == m_Word.length()) return true;

        System.out.println("Bad guess, (" + cows + ") cows, (" + bulls + ") bulls!");
        return false;
    }

}
