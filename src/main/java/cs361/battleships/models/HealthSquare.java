package cs361.battleships.models;

public class HealthSquare extends Square{

    private int health;
    private boolean isCaptain;
    private boolean isSubmerged;

    public HealthSquare() {
        super();
    }

    public HealthSquare(Square s, int health, boolean isCaptain){
        super(s);
        this.health = health;
        this.isCaptain = isCaptain;
        this.isSubmerged = false;
    }

    public HealthSquare(Square s){
        super(s);
        this.health = 1;
        this.isCaptain = false;
        this.isSubmerged = false;
    }

    public HealthSquare(int health, boolean isCaptain) {
        this.health = health;
        this.isCaptain = isCaptain;
        this.isSubmerged = false;
    }

    public HealthSquare(int row, char column, int health, boolean isCaptain) {
        super(row, column);
        this.health = health;
        this.isCaptain = isCaptain;
        this.isSubmerged = false;
    }


    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    //public void getisCaptain() { return isCaptain;}

    public boolean isisCaptain() {
        return isCaptain;
    }


    public void setCaptain(boolean captain) {
        isCaptain = captain;
    }

    public boolean isisSubmerged() { return isSubmerged; }

    public void setIsSubmerged(boolean submerged) { isSubmerged = submerged; }
}
