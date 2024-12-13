import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Hangman {
    private static File DICTIONARY;
    private static final List<Character> WRONG_LETTERS = new ArrayList<>();
    private static final List<Character> CORRECT_LETTERS = new ArrayList<>();
    private static final List<Character> HIDDEN_WORD = new ArrayList<>();
    private static final List<Character> RANDOM_WORD = new ArrayList<>();
    private static final List<String> FORBIDDEN_CHARS = Arrays.asList("-", " ");
    private static final String AGREEMENT = "д";
    private static final String DISAGREEMENT = "н";
    private static final char HIDDEN_LETTER = '*';
    private static final char BRAKE_GAME = '!';
    private static final int MIN_WORD_LENGTH = 5;
    private static final int MAX_WORD_LENGTH = 13;
    private static int counterOfMistakes = 0;
    private static Level currentLevel;

    public static void main(String[] args) {
        getDictionary();
        startGame(createDictionaryList());
    }

    private static void getDictionary() {
        String dictionaryPath = "src/resources/dictionary.txt";
        DICTIONARY = new File(dictionaryPath);
    }

    private static List<String> createDictionaryList() {
        List<String> dictionaryList = new ArrayList<>();
        try (Scanner scanner = new Scanner(DICTIONARY)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (isWordAllowed(line))
                    dictionaryList.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл словаря не найден");
        }
        return dictionaryList;
    }

    private static boolean isWordAllowed(String line) {
        return (!FORBIDDEN_CHARS.contains(line)) &&
                (line.length()) > MIN_WORD_LENGTH &&
                (line.length() < MAX_WORD_LENGTH);
    }

    private static void startGame(List<String> dictionaryList) {
        Scanner scanner = new Scanner(System.in);
        while (wannaStartNewGame(scanner)) {
            getRandomWord(dictionaryList);
            startCurrentGameLoop(scanner);
        }
    }

    private static boolean wannaStartNewGame(Scanner scanner) {
        while (true) {
            System.out.printf("    Хотите начать новую игру? \n ('%s') - Новая игра   ('%s') - Выход \n", AGREEMENT, DISAGREEMENT);
            String decision = scanner.nextLine().toLowerCase();
            if (decision.equals(AGREEMENT)) {
                refreshGame();
                return true;
            } else if (decision.equals(DISAGREEMENT)) {
                System.out.println("Благодарю Вас за игру!");
                return false;
            } else
                System.out.printf("Уважаемый пользователь, пожалуйста, введите только ('%s') либо ('%s') \n", AGREEMENT, DISAGREEMENT);
        }
    }

    private static void refreshGame() {
        counterOfMistakes = 0;
        WRONG_LETTERS.clear();
        CORRECT_LETTERS.clear();
        HIDDEN_WORD.clear();
        RANDOM_WORD.clear();
    }

    private static void getRandomWord(List<String> dictionaryList) {
        int numberOfWord;
        Random random = new Random();
        numberOfWord = random.nextInt(dictionaryList.size());

        for (char currentLetter : dictionaryList.get(numberOfWord).toCharArray()) {
            RANDOM_WORD.add(currentLetter);
        }
    }

    private static void startCurrentGameLoop(Scanner scanner) {
        for (int charIndex = 0; charIndex < RANDOM_WORD.size(); charIndex++) {
            HIDDEN_WORD.add(HIDDEN_LETTER);
        }
        setCurrentDifficulty(scanner);
        System.out.println("Приветствую в игре Виселица! \n" +
                "Начальное состояние:");
        showStartGameCondition();
        while (true) {
            char currentLetter = inputLetter(scanner);
            if (isExitCommand(currentLetter)) {
                boolean decision = breakingGame(scanner);
                if (decision) {
                    System.out.println("Очень жаль! Искомое слово: " + RANDOM_WORD);
                    showHangCondition();
                    break;
                }
                continue;
            }
            boolean isLetterMatched = isLetterMatched(currentLetter);
            if (isLetterMatched) {
                CORRECT_LETTERS.add(currentLetter);
                revealMatchedLetter(currentLetter);
                showMatchedLetterCase();
            } else {
                WRONG_LETTERS.add(currentLetter);
                counterOfMistakes++;
                showMismatchedLetterCase();
            }
            if (isItLose()) {
                showLoseMessage();
                break;
            } else if (isItWin()) {
                showWinMessage();
                break;
            }
        }
    }

    private static void showWinMessage() {
        System.out.println("Поздравляю, Вы Выиграли!");
        System.out.println("Искомое слово: " + RANDOM_WORD);
    }

    private static void showLoseMessage() {
        System.out.println("Увы, Вы проиграли");
        System.out.println("Искомое слово: " + RANDOM_WORD);
    }

    private static void revealMatchedLetter(char letter) {
        for (int letterIndex = 0; letterIndex < RANDOM_WORD.size(); letterIndex++) {
            if (letter == RANDOM_WORD.get(letterIndex)) {
                HIDDEN_WORD.set(letterIndex, letter);
            }
        }
    }

    private static void setCurrentDifficulty(Scanner scanner) {
        selectDifficulty(scanner);
        if (currentLevel.getAmountOpenLetters() != 0) {
            openSomeLetters();
        }
    }

    private static void selectDifficulty(Scanner scanner) {
        while (true) {
            System.out.println("Выберите уровень сложности:");
            showLevel(Level.VERY_EASY, "очень легко");
            showLevel(Level.EASY, "легко");
            showLevel(Level.MEDIUM, "средний");
            showLevel(Level.HARD, "зверь");
            int numLevel;
            try {
                numLevel = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Вы ввели не число");
                continue;
            }
            if (numLevel < Level.VERY_EASY.ordinal() || numLevel > Level.HARD.ordinal()) {
                System.out.println("Уважаемый пользователь, пожалуйста, введите только 0, 1, 2 или 3");
                continue;
            }
            currentLevel = Level.values()[numLevel];
            break;
        }
    }

    private static void showLevel(Level level, String title) {
        System.out.printf("(%d) - %s (ошибок: %d, открытых букв: %d) \n",
                level.ordinal(),
                title,
                level.getMaxMistakes(),
                level.getAmountOpenLetters()
        );
    }

    private static void openSomeLetters() {

        for (int letterNumber = 0; letterNumber < currentLevel.getAmountOpenLetters(); letterNumber++) {
            HIDDEN_WORD.set(letterNumber, RANDOM_WORD.get(letterNumber));
            for (int i = 0; i < RANDOM_WORD.size(); i++) {
                if (RANDOM_WORD.get(i).equals(HIDDEN_WORD.get(letterNumber))) {
                    HIDDEN_WORD.set(i, RANDOM_WORD.get(letterNumber));
                }
            }
            CORRECT_LETTERS.add(HIDDEN_WORD.get(letterNumber));
        }
    }

    private static char inputLetter(Scanner scanner) {
        char letter;
        while (true) {
            System.out.printf("Введите букву русского алфавита (либо введите ('%s') чтобы выйти из текущей игры): ", BRAKE_GAME);
            String input = scanner.nextLine().toLowerCase();
            if (input.length() == 1) {
                letter = input.charAt(0);
            } else continue;
            if (WRONG_LETTERS.contains(letter) || CORRECT_LETTERS.contains(letter)) {
                System.out.println("Вы уже вводили эту букву");
            } else if (isLetterAllowed(letter)) {
                break;
            } else
                System.out.println("Вы ввели не букву русского алфавита, попробуйте еще раз");
        }
        return letter;
    }

    private static boolean isLetterAllowed(char letter) {
        return isRussianLetter(letter) || isExitCommand(letter);
    }

    private static boolean isRussianLetter(char letter) {
        return ((letter >= 'а' && letter <= 'я') || letter == 'ё');
    }

    private static boolean isExitCommand(char letter) {
        return letter == BRAKE_GAME;
    }

    private static boolean breakingGame(Scanner scanner) {
        while (true) {
            System.out.printf("Вы уверены, что хотите завершить текущую игру и выйти в главное меню? \n " +
                    "      ('%s') - Выйти в меню   ('%s') - Продолжить игру", AGREEMENT, DISAGREEMENT);
            String decision = scanner.nextLine().toLowerCase();
            if (decision.equals(AGREEMENT)) {
                return true;
            } else if (decision.equals(DISAGREEMENT)) {
                System.out.println("А Вы - крепкий орешек...");
                return false;
            } else
                System.out.printf("Уважаемый пользователь, пожалуйста, введите только ('%s') либо ('%s') \n", AGREEMENT, DISAGREEMENT);
        }
    }

    private static void showProgress() {
        System.out.println("Угаданные буквы: " + CORRECT_LETTERS);
        System.out.println("Неправильные буквы: " + WRONG_LETTERS);
        System.out.println("Количество ошибок: " + counterOfMistakes + " / " + currentLevel.getMaxMistakes());
        System.out.println("Загаданное слово: " + HIDDEN_WORD);
    }

    private static void showStartGameCondition() {
        showProgress();
        HangmanRenderer.print(Level.HARD, 3);
    }

    private static void showHangCondition() {
        showProgress();
        HangmanRenderer.print(currentLevel, counterOfMistakes);
    }

    private static boolean isLetterMatched(char letter) {
        for (char checkingLetter : RANDOM_WORD) {
            if (checkingLetter == letter) {
                return true;
            }
        }
        return false;
    }

    private static void showMatchedLetterCase() {
        System.out.println("Вы угадали букву!");
        showHangCondition();
    }

    private static void showMismatchedLetterCase() {

        System.out.println("Вы не угадали букву!");
        showHangCondition();
    }

    private static boolean isItWin() {
        for (char checkingLetter : HIDDEN_WORD) {
            if (checkingLetter == HIDDEN_LETTER) {
                return false;
            }
        }
        return true;
    }

    private static boolean isItLose() {
        return (counterOfMistakes == currentLevel.getMaxMistakes());
    }
}
