package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

import static cs361.battleships.models.AttackStatus.*;

public class Game {

    @JsonProperty private Board playersBoard = new Board();
    @JsonProperty private Board opponentsBoard = new Board();
    private Random rand = new Random();

    /*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
    public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
        boolean successful = playersBoard.placeShip(ship, x, y, isVertical);
        if (!successful)
            return false;

        boolean opponentPlacedSuccessfully;
        do {
            // AI places random ships, so it might try and place overlapping ships
            // let it try until it gets it right
            opponentPlacedSuccessfully = opponentsBoard.placeShip(ship, randRow(), randCol(), randVertical());
        } while (!opponentPlacedSuccessfully);

        return true;
    }

    public boolean movePlayer(char direction) {
        if(opponentsBoard.shipsAlive() >= 2 && playersBoard.getFleetMoves() < 2) {
            playersBoard.moveShips(direction);
            return true;
        }
        return false;
    }

    public boolean placeSonar(int row, char column) {
        return opponentsBoard.placeSonar(row, column);
    }

    /*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
    public boolean attack(int x, char  y) {
        if(playersBoard.shipsAlive() >= 2 && opponentsBoard.getFleetMoves() < 2) {
            int chance = rand.nextInt(100);
            if(chance >= 0 && chance <= 29) {
                int dirChance = rand.nextInt(4);
                char dir = 'N';
                switch(dirChance) {
                    case 0:
                        dir = 'N';
                    case 1:
                        dir = 'E';
                    case 2:
                        dir = 'S';
                    case 3:
                        dir = 'W';
                }
                opponentsBoard.moveShips(dir);
            }
        }

        Result playerAttack = opponentsBoard.attack(x, y);
        if (playerAttack.getResult() == INVALID) {
            return false;
        }

        Result opponentAttackResult;
        do {
            // AI does random attacks, so it might attack the same spot twice
            // let it try until it gets it right
            opponentAttackResult = playersBoard.attack(randRow(), randCol());
        } while(opponentAttackResult.getResult() == INVALID);

        return true;
    }

    private char randCol() {
        String alphabet = "ABCDEFGHIJ";
        char col = alphabet.charAt(rand.nextInt(alphabet.length()));
        return col;
    }

    private int randRow() {
        // creates a random int off of the random rand from the top of Game
        int rowInt = rand.nextInt(10) + 1;
        return rowInt;
    }

    private boolean randVertical() {
        return rand.nextBoolean();
    }
}
