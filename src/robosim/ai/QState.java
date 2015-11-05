package robosim.ai;

/**
 * Created by john on 11/2/15.
 */
public enum QState {
    BLOCKED, NEAR, FAR, MIDDLE;

    @Override
    public String toString() {
        switch (this){
            case NEAR:
                return "NEAR";
            case MIDDLE:
                return "MIDDLE";
            case FAR:
                return "FAR";
            case BLOCKED:
                return "BLOCKED";
            default:
                return "";
        }
    }

    public int index(){
        switch (this){
            case BLOCKED:
                return 0;
            case NEAR:
                return 1;
            case MIDDLE:
                return 2;
            case FAR:
                return 3;
            default:
                return -1;
        }
    }

    public double distanceTo(){
        switch (this){
            case BLOCKED:
                return 5;
            case NEAR:
                return 25;
            case MIDDLE:
                return 50;
            case FAR:
                return Double.MAX_VALUE;
            default:
                return 0;
        }
    }

    public int numStates(){
        return 4;
    }

    public static QState forDistance(double distance){
        if (distance < BLOCKED.distanceTo())
            return BLOCKED;
        if (distance < NEAR.distanceTo())
            return NEAR;
        if (distance < MIDDLE.distanceTo())
            return MIDDLE;
        if (distance < FAR.distanceTo())
            return FAR;

        return null;
    }
}
