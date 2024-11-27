import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Hangman {
    private final static File DICTIONARY = new File(("src/resources/dictionary.txt"));
    private final static int MIN_WORD_LENGTH = 5;
    private final static int MAX_WORD_LENGTH = 13;
    private final static String FORBIDDEN_CHAR_HYPHEN = "-";
    private final static String FORBIDDEN_CHAR_SPACE = " ";
    private final static String AGREEMENT = "д";
    private final static String DISAGREEMENT = "н";
    private final static char HIDDEN_LETTER = '*';
    private final static char BRAKE_GAME = '!';
    private static int counterOfMistakes = 0;

    public static void main(String[] args) {
        startGame(createDictionaryList());
    }

    public static List<String> createDictionaryList() {
        List<String> dictionaryList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(DICTIONARY);
            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if ((!line.contains(FORBIDDEN_CHAR_HYPHEN)) && (!line.contains(FORBIDDEN_CHAR_SPACE)) && (line.length()) > MIN_WORD_LENGTH && (line.length() < MAX_WORD_LENGTH))
                    dictionaryList.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Файл словаря не найден");
        }
        return dictionaryList;
    }

    public static void startGame(List<String> dictionaryList) {
        Scanner scanner = new Scanner(System.in);
        while (wannaStartNewGame(scanner))
            startCurrentGameLoop(getRandomWord(dictionaryList), scanner);
    }

    private static List<Character> getRandomWord(List<String> dictionaryList) {
        int numberOfWord;
        Random random = new Random();
        numberOfWord = random.nextInt(dictionaryList.size());
        List<Character> randomWord = new ArrayList<>();
        for (char c : dictionaryList.get(numberOfWord).toCharArray()) {
            randomWord.add(c);
        }
        return randomWord;
    }

    private static boolean wannaStartNewGame(Scanner scanner) {
        do {
            System.out.println("    Хотите начать новую игру? \n (" + AGREEMENT + ") - Новая игра   (" + DISAGREEMENT + ") - Выход");
            String decision = scanner.nextLine().toLowerCase();
            if (decision.equals(AGREEMENT)) {
                counterOfMistakes = 0;
                return true;
            } else if (decision.equals(DISAGREEMENT)) {
                System.out.println("Благодарю Вас за игру!");
                return false;
            } else
                System.out.println("Уважаемый пользователь, пожалуйста, введите только (" + AGREEMENT + ") либо (" + DISAGREEMENT + ")");
        } while (true);
    }

    private static void startCurrentGameLoop(List<Character> RandomWord, Scanner scanner) {
        List<Character> wrongLetters = new ArrayList<>();
        List<Character> correctLetters = new ArrayList<>();
        List<Character> hiddenWord = new ArrayList<>();
        boolean gameIsNotOver = true;
        for (int _ : RandomWord)
            hiddenWord.add(HIDDEN_LETTER);
        while (gameIsNotOver) {
            char currentLetter = inputLetter(wrongLetters, correctLetters, scanner);
            if (currentLetter == BRAKE_GAME) {
                boolean decision = breakingGame(scanner);
                if (decision) {
                    System.out.println("Очень жаль! Искомое слово: " + RandomWord);
                    showHangCondition(wrongLetters, correctLetters, hiddenWord, counterOfMistakes);
                    break;
                } else
                    continue;
            }
            boolean matchLetter = checkMatch(RandomWord, currentLetter);
            if (matchLetter) {
                matchedLetter(wrongLetters, correctLetters, currentLetter, hiddenWord, RandomWord);
            } else
                mismatchedLetter(wrongLetters, correctLetters, currentLetter, hiddenWord);
            gameIsNotOver = checkIfGameContinues(counterOfMistakes, RandomWord, hiddenWord);
        }
    }

    private static char inputLetter(List<Character> wrongLetters, List<Character> correctLetters, Scanner scanner) {
        char letter;
        while (true) {
            System.out.println("Введите букву русского алфавита (либо введите (" + BRAKE_GAME + ") чтобы выйти из текущей игры): ");
            letter = scanner.nextLine().toLowerCase().charAt(0);
            if (wrongLetters.contains(letter) || correctLetters.contains(letter)) {
                System.out.println("Вы уже вводили эту букву");
            } else if ((letter >= 'а' && letter <= 'я') || letter == 'ё' || letter == BRAKE_GAME) {
                break;
            } else
                System.out.println("Вы ввели не букву, попробуйте еще раз");
        }
        return letter;
    }

    private static boolean checkMatch(List<Character> RandomWord, char letter) {
        boolean match = false;
        for (char checkingLetter : RandomWord) {
            if (checkingLetter == letter) {
                match = true;
                break;
            }
        }
        return match;
    }

    private static void matchedLetter(List<Character> wrongLetters, List<Character> correctLetters, char letter, List<Character> hiddenWord, List<Character> RandomWord) {
        correctLetters.add(letter);
        for (int letterIndex = 0; letterIndex < RandomWord.size(); letterIndex++) {
            if (letter == RandomWord.get(letterIndex)) {
                hiddenWord.set(letterIndex, letter);
            }
        }
        System.out.println("Вы угадали букву!");
        showHangCondition(wrongLetters, correctLetters, hiddenWord, counterOfMistakes);
    }

    private static void mismatchedLetter(List<Character> wrongLetters, List<Character> correctLetters, char letter, List<Character> hiddenWord) {
        wrongLetters.add(letter);
        counterOfMistakes++;
        System.out.println("Вы не угадали букву!");
        showHangCondition(wrongLetters, correctLetters, hiddenWord, counterOfMistakes);
    }

    private static void showHangCondition(List<Character> wrongLetters, List<Character> correctLetters, List<Character> hiddenWord, int counterOfMistakes) {
        System.out.println("Угаданные буквы: " + correctLetters);
        System.out.println("Неправильные буквы: " + wrongLetters);
        System.out.println("Количество ошибок: " + counterOfMistakes);
        System.out.println("Загаданное слово: " + hiddenWord);
        File hang = getHang();
        try {
            Scanner hangScanner = new Scanner(hang);
            while (hangScanner.hasNextLine()) {
                System.out.println(hangScanner.nextLine());
            }
            hangScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Виселица не найдена");
        }
    }

    private static File getHang() {
        String path = "";
        switch (counterOfMistakes) {
            case (0) -> path = "src/resources/0_mistakes.txt";
            case (1) -> path = "src/resources/1_mistake.txt";
            case (2) -> path = "src/resources/2_mistakes.txt";
            case (3) -> path = "src/resources/3_mistakes.txt";
            case (4) -> path = "src/resources/4_mistakes.txt";
            case (5) -> path = "src/resources/5_mistakes.txt";
            case (6) -> path = "src/resources/6_mistakes.txt";
            case (7) -> path = "src/resources/7_mistakes.txt";
            case (8) -> path = "src/resources/8_mistakes.txt";
        }
        return new File(path);
    }

    private static boolean checkIfGameContinues(int counterOfMistakes, List<Character> RandomWord, List<Character> hiddenWord) {
        if (counterOfMistakes == 8) {
            System.out.println("Увы, Вы проиграли");
            System.out.println("Искомое слово: " + RandomWord);
            return false;
        }
        for (char checkingLetter : hiddenWord) {
            if (checkingLetter == HIDDEN_LETTER)
                return true;
        }
        System.out.println("Поздравляю, Вы Выиграли!");
        System.out.println("Искомое слово: " + RandomWord);
        return false;
    }

    private static boolean breakingGame(Scanner scanner) {
        do {
            System.out.println("Вы уверены, что хотите завершить текущую игру и выйти в главное меню? \n " +
                    "      (" + AGREEMENT + ") - Выйти в меню   (" + DISAGREEMENT + ") - Продолжить игру");
            String decision = scanner.nextLine().toLowerCase();
            if (decision.equals(AGREEMENT)) {
                return true;
            } else if (decision.equals(DISAGREEMENT)) {
                System.out.println("А Вы - крепкий орешек...");
                return false;
            } else
                System.out.println("Уважаемый пользователь, пожалуйста, введите только (" + AGREEMENT + ") либо (" + DISAGREEMENT + ")");
        } while (true);
    }
}
