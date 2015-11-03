package robosim.ai;

/**
 * Created by john on 11/2/15.
 */
public enum QState {
    NEAR, FAR, MIDDLE;

    @Override
    public String toString() {
        switch (this){
            case NEAR:
                return "NEAR";
            case MIDDLE:
                return "MIDDLE";
            case FAR:
                return "FAR";
            default:
                return "";
        }
    }

    public int index(){
        switch (this){
            case NEAR:
                return 0;
            case MIDDLE:
                return 1;
            case FAR:
                return 2;
            default:
                return -1;
        }
    }

    public int numStates(){
        return 3;
    }
}
