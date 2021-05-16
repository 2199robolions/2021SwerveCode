package frc.robot;

public class Auto {
    //Class variables go here
    public enum ErrorMessages {
        FAIL(-1),
        PASS(1),
        DONE(2),
        CONTINUE(3);
    
        //The code below will make this enumerator work like the previous status codes
        private int value;
    
        private ErrorMessages(int value) {
            this.value = value;
        }
    
        private int getId() {
            return this.value;
        }
    }

    /**
     * Constructor
     */
    public Auto() {
        //
    }

    //Methods
    //I hate enumerators
}
