public enum Level {
    VERY_EASY(10, 1),
    EASY(6, 1),
    MEDIUM(4, 0),
    HARD(3, 0);
    private final int maxMistakes;
    private final int amountOpenLetters;

    Level(int maxMistakes, int amountOpenLetters) {
        this.maxMistakes = maxMistakes;
        this.amountOpenLetters = amountOpenLetters;
    }

    public int getMaxMistakes() {
        return maxMistakes;
    }

    public int getAmountOpenLetters() {
        return amountOpenLetters;
    }
}
