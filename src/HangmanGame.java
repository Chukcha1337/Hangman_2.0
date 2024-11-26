
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLOutput;
import java.util.*;

public class HangmanGame {
    private final static File DICTIONARY = new File(("src/resources/dictionary.txt"));

    public static void main(String[] args) {
//        System.out.println(createDictionaryList());
//        System.out.println(createDictionaryList().size());

        gameLoop(createDictionaryList());


    }

    public static void gameLoop(List<String> dictionaryList) {
do {
    currentGameLoop(getRandomWord(dictionaryList));

}while(true);
    }

    private static List<String> createDictionaryList() {
        List<String> dictionaryList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(DICTIONARY);
            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if ((!line.contains("-")) && (!line.contains(" ")) && (line.length()) > 5 && (line.length() < 13))
                    dictionaryList.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Файл словаря не найден");
        }
        return dictionaryList;
    }

    private static char[] getRandomWord(List<String> dictionaryList) {
        int numberOfWord;
        Random random = new Random();
        numberOfWord = random.nextInt(dictionaryList.size());
        return dictionaryList.get(numberOfWord).toCharArray();
    }

    private static void currentGameLoop(char[] RandomWord) {
        Scanner scanner = new Scanner(System.in);
        List<Character> wrongLetters = new ArrayList<>();
        List<Character> correctLetters = new ArrayList<>();
        int counterOfMistakes = 0;

        char[] hiddenWord = new char[RandomWord.length];
        for (char hiddenChar : hiddenWord) {
            hiddenChar = '*';
        }
        CURRENT_GAME:
        do {
            char currentLetter = inputLetter(scanner, wrongLetters, correctLetters);
            boolean matchLetter = checkMatch(RandomWord, currentLetter);
            if (matchLetter = true) {
                matchedLetter(wrongLetters, correctLetters, currentLetter, hiddenWord, RandomWord, counterOfMistakes);
            } else
                mismatchedLetter(wrongLetters, correctLetters, currentLetter, hiddenWord, counterOfMistakes);
            int selection = checkWinCondition(counterOfMistakes, RandomWord, hiddenWord);
            switch (selection) {
                case (1) -> {
                    break CURRENT_GAME;
                }
                case (0) -> {
                    break;
                }
            }
        } while (true);
    }

    private static char inputLetter(Scanner scanner, List<Character> wrongLetters, List<Character> correctLetters) {
        char letter;
        do {
            System.out.print("Введите букву (кириллица): ");
            letter = scanner.nextLine().toLowerCase().charAt(0);

            if (wrongLetters.contains(letter) || correctLetters.contains(letter)) {
                System.out.println("Вы уже вводили эту букву");
            } else if ((letter >= 'а' && letter <= 'я') || letter == 'ё') {
                break;
            } else
                System.out.println("Вы ввели не букву, попробуйте еще раз");

        } while (true);
        return letter;
    }

    private static boolean checkMatch(char[] RandomWord, char letter) {
        boolean match = false;
        for (char checkingLetter : RandomWord) {
            if (checkingLetter == letter)
                match = true;
        }
        return match;
    }

    private static void matchedLetter(List<Character> wrongLetters, List<Character> correctLetters, char letter, char[] hiddenWord, char[] RandomWord, int counterOfMistakes) {
        correctLetters.add(letter);
        for (int letterIndex = 0; letterIndex < RandomWord.length; letterIndex++) {
            if (letter == RandomWord[letterIndex])
                hiddenWord[letterIndex] = letter;
        }
        System.out.println("Вы угадали букву!");
        showHangCondition(correctLetters, wrongLetters, hiddenWord, counterOfMistakes);
    }

    private static void mismatchedLetter(List<Character> wrongLetters, List<Character> correctLetters, char letter, char[] hiddenWord, int counterOfMistakes) {
        wrongLetters.add(letter);
        counterOfMistakes++;
        System.out.println("Вы не угадали букву!");
        showHangCondition(correctLetters, wrongLetters, hiddenWord, counterOfMistakes);
    }

    private static void showHangCondition(List<Character> wrongLetters, List<Character> correctLetters, char[] hiddenWord, int counterOfMistakes) {
        System.out.println("Угаданные буквы: " + correctLetters);
        System.out.println("Неправильные буквы: " + wrongLetters);
        System.out.println("Количество ошибок: " + counterOfMistakes);
        System.out.println("Загаданное слово: " + Arrays.toString(hiddenWord));
        Scanner scanner = new Scanner(System.in);
        String path = "";
        File hang = new File(path);
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
        try {
            Scanner hangScanner = new Scanner(hang);
            while (hangScanner.hasNextLine()) {
                System.out.println(hangScanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Виселица не найдена");
        }
    }

    private static int checkWinCondition(int counterOfMistakes, char[] RandomWord, char[] hiddenWord) {
        if (counterOfMistakes == 8) {
            System.out.println("Увы, Вы проиграли");
            System.out.println("Искомое слово: " + Arrays.toString(RandomWord));
            return 1;
        }
        for (char checkingLetter : hiddenWord) {
            if (checkingLetter == '*') {
                return 0;
            }
        }
        System.out.println("Поздравляю, Вы Выиграли!");
        System.out.println("Искомое слово: " + Arrays.toString(RandomWord));
        return 1;
    }
}
