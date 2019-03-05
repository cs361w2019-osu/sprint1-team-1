package controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs361.battleships.models.Game;

public class MoveShipGameAction {
    @JsonProperty private Game game;
    @JsonProperty private char direction;

    public Game getGame() {
        return game;
    }

    public char getDirection() {
        return direction;
    }
}
