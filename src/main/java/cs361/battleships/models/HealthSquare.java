package cs361.battleships.models;

public class HealthSquare extends Square{

    private int health;
    private boolean isCaptain;

    public HealthSquare(Square s, int health, boolean isCaptain){
        super(s);
        this.health = health;
        this.isCaptain = isCaptain;
    }

    public HealthSquare(Square s){
        super(s);
        this.health = 1;
        this.isCaptain = false;
    }


    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isCaptain() {
        return isCaptain;
    }

    public void setCaptain(boolean captain) {
        isCaptain = captain;
    }
}
