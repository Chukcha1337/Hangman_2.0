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
    private static final int MAX_MISTAKES = 10;


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

    private static double selectDifficulty(Scanner scanner) {
        while (true) {
            System.out.println("""
                    Выберите уровень сложности:\s
                    (1) - очень легкий (10 ошибок, одна буква открыта)\s
                    (2) - легкий (6 ошибок, одна буква открыта)\s
                    (3) - средний (5 ошибок, все буквы скрыты)\s
                    (4) - чудовище (3 ошибки, все буквы скрыты)""");
            int difficulty = Integer.parseInt(scanner.nextLine());

            switch (difficulty) {
                case (1) -> {return 1;}
                case (2) -> {return 1.5;}
                case (3) -> {return 2;}
                case (4) -> {return 3;}
                default -> {System.out.println("Уважаемый пользователь, пожалуйста, введите только 1, 2, 3 или 4");}
            };
        }
    }

    private static void setDifficulty(double difficultyMultiplier,List<Character> wrongLetters,List<Character> correctLetters, List<Character> hiddenWord, List<Character> randomWord, int counterOfMistakes) {

        if (difficultyMultiplier < 3) {
            hiddenWord.set(0, randomWord.getFirst());
            for (int i = 0; i < randomWord.size(); i++){
                if (randomWord.get(i).equals(hiddenWord.getFirst())){
                    hiddenWord.set(i, randomWord.get(i));
                }
            }
            correctLetters.add(hiddenWord.getFirst());
        }
        System.out.println("Приветсвую в игре Виселица! \n" +
                "Начальное состояние:");
        showHangCondition(wrongLetters,  correctLetters,  hiddenWord,counterOfMistakes,difficultyMultiplier);
    }

    private static void startCurrentGameLoop(List<Character> randomWord, Scanner scanner) {
        List<Character> wrongLetters = new ArrayList<>();
        List<Character> correctLetters = new ArrayList<>();
        List<Character> hiddenWord = new ArrayList<>();
        double difficultyMultiplier = selectDifficulty(scanner);
        boolean gameIsNotOver = true;
        for (int _ : randomWord)
            hiddenWord.add(HIDDEN_LETTER);

        setDifficulty(difficultyMultiplier, wrongLetters, correctLetters,hiddenWord,randomWord, counterOfMistakes );
        while (gameIsNotOver) {
            char currentLetter = inputLetter(wrongLetters, correctLetters, scanner);
            if (currentLetter == BRAKE_GAME) {
                boolean decision = breakingGame(scanner);
                if (decision) {
                    System.out.println("Очень жаль! Искомое слово: " + randomWord);
                    showHangCondition(wrongLetters, correctLetters, hiddenWord, counterOfMistakes,difficultyMultiplier);
                    break;
                } else
                    continue;
            }
            boolean matchLetter = checkMatch(randomWord, currentLetter);
            if (matchLetter) {
                matchedLetter(wrongLetters, correctLetters, currentLetter, hiddenWord, randomWord,difficultyMultiplier);
            } else
                mismatchedLetter(wrongLetters, correctLetters, currentLetter, hiddenWord,difficultyMultiplier);
            gameIsNotOver = checkIfGameContinues(counterOfMistakes, randomWord, hiddenWord,difficultyMultiplier);
        }
    }

    private static char inputLetter(List<Character> wrongLetters, List<Character> correctLetters, Scanner scanner) {
        char letter;
        while (true) {
            System.out.println("Введите букву русского алфавита (либо введите (" + BRAKE_GAME + ") чтобы выйти из текущей игры): ");
            String input = scanner.nextLine().toLowerCase();
            if (input.length() == 1) {
                letter = input.charAt(0);
            } else continue;
            if (wrongLetters.contains(letter) || correctLetters.contains(letter)) {
                System.out.println("Вы уже вводили эту букву");
            } else if ((letter >= 'а' && letter <= 'я') || letter == 'ё' || letter == BRAKE_GAME) {
                break;
            } else
                System.out.println("Вы ввели не букву, попробуйте еще раз");
        }
        return letter;
    }

    private static boolean checkMatch(List<Character> randomWord, char letter) {
        boolean match = false;
        for (char checkingLetter : randomWord) {
            if (checkingLetter == letter) {
                match = true;
                break;
            }
        }
        return match;
    }

    private static void matchedLetter(List<Character> wrongLetters, List<Character> correctLetters, char letter, List<Character> hiddenWord, List<Character> randomWord,double difficultyMultiplier) {
        correctLetters.add(letter);
        for (int letterIndex = 0; letterIndex < randomWord.size(); letterIndex++) {
            if (letter == randomWord.get(letterIndex)) {
                hiddenWord.set(letterIndex, letter);
            }
        }
        System.out.println("Вы угадали букву!");
        showHangCondition(wrongLetters, correctLetters, hiddenWord, counterOfMistakes, difficultyMultiplier);
    }

    private static void mismatchedLetter(List<Character> wrongLetters, List<Character> correctLetters, char letter, List<Character> hiddenWord,double difficultyMultiplier) {
        wrongLetters.add(letter);
        counterOfMistakes++;
        System.out.println("Вы не угадали букву!");
        showHangCondition(wrongLetters, correctLetters, hiddenWord, counterOfMistakes,difficultyMultiplier);
    }

    private static void showHangCondition(List<Character> wrongLetters, List<Character> correctLetters, List<Character> hiddenWord, int counterOfMistakes, double difficultyMultiplier) {
        System.out.println("Угаданные буквы: " + correctLetters);
        System.out.println("Неправильные буквы: " + wrongLetters);
        System.out.println("Количество ошибок: " + counterOfMistakes + " / " + ((int)(MAX_MISTAKES/difficultyMultiplier)));
        System.out.println("Загаданное слово: " + hiddenWord);
        File hang = getHang(difficultyMultiplier);
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

    private static File getHang(double difficultyMultiplier) {
        String path = "";
        switch ((int) (counterOfMistakes*difficultyMultiplier)) {
            case (0) -> path = "src/resources/0_mistakes.txt";
            case (1) -> path = "src/resources/1_mistake.txt";
            case (2) -> path = "src/resources/2_mistakes.txt";
            case (3) -> path = "src/resources/3_mistakes.txt";
            case (4) -> path = "src/resources/4_mistakes.txt";
            case (5) -> path = "src/resources/5_mistakes.txt";
            case (6) -> path = "src/resources/6_mistakes.txt";
            case (7) -> path = "src/resources/7_mistakes.txt";
            case (8) -> path = "src/resources/8_mistakes.txt";
            case (9), (10) -> path = "src/resources/gameOver.txt";
        }
        return new File(path);
    }

    private static boolean checkIfGameContinues(int counterOfMistakes, List<Character> randomWord, List<Character> hiddenWord, double difficultyMultiplier) {
        if (counterOfMistakes == (int)(MAX_MISTAKES/difficultyMultiplier)) {
            System.out.println("Увы, Вы проиграли");
            System.out.println("Искомое слово: " + randomWord);
            return false;
        }
        for (char checkingLetter : hiddenWord) {
            if (checkingLetter == HIDDEN_LETTER)
                return true;
        }
        System.out.println("Поздравляю, Вы Выиграли!");
        System.out.println("Искомое слово: " + randomWord);
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
