public enum Mistakes {
    ZERO_MISTAKES ("""
            
            
            
            
            
            
            

            
            ___|_________________"""),
    ONE_MISTAKE ("""
            
            
            
            
            
               |
               |
               |
               |
            ___|_________________"""),
    TWO_MISTAKES ("""
            
               |
               |
               |
               |
               |
               |
               |
               |
            ___|_________________"""),
    THREE_MISTAKES ("""
                _________
               |/
               |
               |
               |
               |
               |
               |
               |
            ___|_________________"""),
    FOUR_MISTAKES("""
                _________
               |/
               |
               |
               |
               |
               |
               |
               |
            ___|______███████___"""),
    FIVE_MISTAKES("""
                _________
               |/
               |
               |
               |
               |
               |
               |        / \\
               |       /   \\
            ___|______███████___"""),
    SIX_MISTAKES("""
                _________
               |/
               |
               |         |
               |         |
               |         |
               |         |
               |        / \\
               |       /   \\
            ___|______███████___"""),
    SEVEN_MISTAKES("""
                _________
               |/
               |
               |         |
               |        /|\\
               |       / | \\
               |         |
               |        / \\
               |       /   \\
            ___|______███████___"""),
    EIGHT_MISTAKES("""
                _________
               |/
               |        ( )
               |         |
               |        /|\\
               |       / | \\
               |         |
               |        / \\
               |       /   \\
            ___|______███████___"""),
    GAME_OVER ("""
                _________
               |/        |
               |        ( )
               |         |
               |        /|\\
               |       / | \\
               |         |
               |        / \\
               |       /   \\
            ___|_________________""");

    private final String hang;

    Mistakes (String hang){
        this.hang = hang;
    }

    public String getHangRepresentation(){
        return hang;
    }
}
