package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Sonar {
    @JsonProperty private int row;
    @JsonProperty private char column;
    @JsonProperty private List<Square> foundShips = new ArrayList<>();

    public List<Square> getSonar() {
        return this.foundShips;
    }

    public void setSonar(List<Ship> ships, int row, char column) {
        this.row = row;
        this.column = column;

        for(Ship ship : ships) {
            for(Square square : ship.getOccupiedSquares()) {
                if((Math.abs(square.getRow() - row) <= 1 && Math.abs((int)square.getColumn() - (int) column) <= 1) ||
                (Math.abs(square.getRow() - row) <= 2 && square.getColumn() == column) ||
                (Math.abs((int)square.getColumn() - (int)column) <= 2 && square.getRow() == row)) {
                    foundShips.add(square);
                }
            }
        }
    }

    public int getRow() {
        return this.row;
    }

    public char getColumn() {
        return this.column;
    }
}
