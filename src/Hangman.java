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
    private static final int ABSOLUTE_MAX_MISTAKES = 10;
    private static int selectedDifficultyMaxMistakes = 0;
    private static int counterOfMistakes = 0;
    private static double difficultyMultiplier = 0;

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
        System.out.println(dictionaryList.size());
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
        do {
            System.out.println("    Хотите начать новую игру? \n (" + AGREEMENT + ") - Новая игра   (" + DISAGREEMENT + ") - Выход");
            String decision = scanner.nextLine().toLowerCase();
            if (decision.equals(AGREEMENT)) {
                refreshGame();
                return true;
            } else if (decision.equals(DISAGREEMENT)) {
                System.out.println("Благодарю Вас за игру!");
                return false;
            } else
                System.out.println("Уважаемый пользователь, пожалуйста, введите только (" + AGREEMENT + ") либо (" + DISAGREEMENT + ")");
        } while (true);
    }

    private static void refreshGame() {
        counterOfMistakes = 0;
        difficultyMultiplier = 0;
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
        boolean isGameOver = true;
        for (int _ : RANDOM_WORD)
            HIDDEN_WORD.add(HIDDEN_LETTER);
        setCurrentDifficulty(scanner);
        while (isGameOver) {
            char currentLetter = inputLetter(scanner);
            if (currentLetter == BRAKE_GAME) {
                boolean decision = breakingGame(scanner);
                if (decision) {
                    System.out.println("Очень жаль! Искомое слово: " + RANDOM_WORD);
                    showHangCondition();
                    break;
                } else
                    continue;
            }
            boolean isLetterMatched = checkMatch(currentLetter);
            if (isLetterMatched) {
                matchedLetter(currentLetter);
            } else
                mismatchedLetter(currentLetter);
            isGameOver = isGameContinues();
        }
    }

    private static void setCurrentDifficulty(Scanner scanner) {
        setDifficultyMultiplier(selectDifficulty(scanner));
        getCurrentDifficultyMaxMistakes();
        easyDifficultyCase();
    }

    private static void setDifficultyMultiplier(int difficulty) {
        switch (difficulty) {
            case (1) -> difficultyMultiplier = 1;
            case (2) -> difficultyMultiplier = 1.5;
            case (3) -> difficultyMultiplier = 2;
            case (4) -> difficultyMultiplier = 3;
        }
    }

    private static int selectDifficulty(Scanner scanner) {
        int difficulty;
        while (true) {
            System.out.println("""
                    Выберите уровень сложности:
                    (1) - очень легкий (10 ошибок, одна буква открыта)
                    (2) - легкий (6 ошибок, одна буква открыта)
                    (3) - средний (5 ошибок, все буквы скрыты)
                    (4) - чудовище (3 ошибки, все буквы скрыты)""");
            try {
                difficulty = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Уважаемый пользователь, пожалуйста, введите только 1, 2, 3 или 4");
                continue;
            }

            if (difficulty >= 1 && difficulty <= 4)
                return difficulty;
            else
                System.out.println("Уважаемый пользователь, пожалуйста, введите только 1, 2, 3 или 4");
        }
    }

    private static void getCurrentDifficultyMaxMistakes() {
        selectedDifficultyMaxMistakes = ((int) (ABSOLUTE_MAX_MISTAKES / difficultyMultiplier));
    }

    private static void easyDifficultyCase() {

        if (difficultyMultiplier < 2) {
            HIDDEN_WORD.set(0, RANDOM_WORD.getFirst());
            for (int i = 0; i < RANDOM_WORD.size(); i++) {
                if (RANDOM_WORD.get(i).equals(HIDDEN_WORD.getFirst())) {
                    HIDDEN_WORD.set(i, RANDOM_WORD.get(i));
                }
            }
            CORRECT_LETTERS.add(HIDDEN_WORD.getFirst());
        }
        System.out.println("Приветсвую в игре Виселица! \n" +
                "Начальное состояние:");
        showHangCondition();
    }

    private static char inputLetter(Scanner scanner) {
        char letter;
        while (true) {
            System.out.println("Введите букву русского алфавита (либо введите (" + BRAKE_GAME + ") чтобы выйти из текущей игры): ");
            String input = scanner.nextLine().toLowerCase();
            if (input.length() == 1) {
                letter = input.charAt(0);
            } else continue;
            if (WRONG_LETTERS.contains(letter) || CORRECT_LETTERS.contains(letter)) {
                System.out.println("Вы уже вводили эту букву");
            } else if (isLetterAllowed(letter)) {
                break;
            } else
                System.out.println("Вы ввели не букву, попробуйте еще раз");
        }
        return letter;
    }

    private static boolean isLetterAllowed(char letter) {
        return ((letter >= 'а' && letter <= 'я') || letter == 'ё' || letter == BRAKE_GAME);
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

    private static void showHangCondition() {
        System.out.println("Угаданные буквы: " + CORRECT_LETTERS);
        System.out.println("Неправильные буквы: " + WRONG_LETTERS);
        System.out.println("Количество ошибок: " + counterOfMistakes + " / " + selectedDifficultyMaxMistakes);
        System.out.println("Загаданное слово: " + HIDDEN_WORD);
        System.out.println(getHang());
    }

    private static String getHang() {
        String hangRepresentation = "";
        switch (numberOfCurrentHang()) {
            case (0) -> hangRepresentation = Mistakes.ZERO_MISTAKES.getHangRepresentation();
            case (1) -> hangRepresentation = Mistakes.ONE_MISTAKE.getHangRepresentation();
            case (2) -> hangRepresentation = Mistakes.TWO_MISTAKES.getHangRepresentation();
            case (3) -> hangRepresentation = Mistakes.THREE_MISTAKES.getHangRepresentation();
            case (4) -> hangRepresentation = Mistakes.FOUR_MISTAKES.getHangRepresentation();
            case (5) -> hangRepresentation = Mistakes.FIVE_MISTAKES.getHangRepresentation();
            case (6) -> hangRepresentation = Mistakes.SIX_MISTAKES.getHangRepresentation();
            case (7) -> hangRepresentation = Mistakes.SEVEN_MISTAKES.getHangRepresentation();
            case (8) -> hangRepresentation = Mistakes.EIGHT_MISTAKES.getHangRepresentation();
            case (9), (10) -> hangRepresentation = Mistakes.GAME_OVER.getHangRepresentation();
        }
        return hangRepresentation;
    }

    private static int numberOfCurrentHang(){
        return ((int) (counterOfMistakes * difficultyMultiplier));
    }

    private static boolean checkMatch(char letter) {
        boolean match = false;
        for (char checkingLetter : RANDOM_WORD) {
            if (checkingLetter == letter) {
                match = true;
                break;
            }
        }
        return match;
    }

    private static void matchedLetter(char letter) {
        CORRECT_LETTERS.add(letter);
        for (int letterIndex = 0; letterIndex < RANDOM_WORD.size(); letterIndex++) {
            if (letter == RANDOM_WORD.get(letterIndex)) {
                HIDDEN_WORD.set(letterIndex, letter);
            }
        }
        System.out.println("Вы угадали букву!");
        showHangCondition();
    }

    private static void mismatchedLetter(char letter) {
        WRONG_LETTERS.add(letter);
        counterOfMistakes++;
        System.out.println("Вы не угадали букву!");
        showHangCondition();
    }

    private static boolean isGameContinues() {
        if (counterOfMistakes == ((int) (ABSOLUTE_MAX_MISTAKES / difficultyMultiplier))) {
            System.out.println("Увы, Вы проиграли");
            System.out.println("Искомое слово: " + RANDOM_WORD);
            return false;
        } else if (counterOfMistakes == 9) {
            System.out.println("\n^ОН ДЕРЖИТСЯ ИЗ ПОСЛЕДНИХ СИЛ^\n ЭТО ЖЕ САМЫЙ ПРОСТОЙ УРОВЕНЬ! СОБЕРИТЕСЬ, У ВАС ОСТАЛАСЬ 1 ОШИБКА\n");
        }

        for (char checkingLetter : HIDDEN_WORD) {
            if (checkingLetter == HIDDEN_LETTER)
                return true;
        }
        System.out.println("Поздравляю, Вы Выиграли!");
        System.out.println("Искомое слово: " + RANDOM_WORD);
        return false;
    }
}
