package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private List<Ship> placedShips;
	private List<Result> attacks;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		this.placedShips = new ArrayList<>();
		this.attacks = new ArrayList<>();
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		List<Square> occupiedSquares = new ArrayList<>();
		// Check to insure you can only place 1 ship of each kind
		for ( Ship currentShip : placedShips ) {
			if (ship.getKind().equals(currentShip.getKind())) {
				return false;
			}
		}
		if (isVertical) {
			if (x + ship.getLength() > 10 || x < 1) {
				return false;
			}
			for (int i = 0; i < ship.getLength(); i++) {
				occupiedSquares.add(new Square(x + i, y));
			}
		} else {
			if (y + ship.getLength() - 'A' > 10 || y < 'A') {
				return false;
			}
			for (int i = 0; i < ship.getLength(); i++) {
				occupiedSquares.add(new Square(x, (char)(y + i)));
			}
		}
		for (Square square : occupiedSquares) {
			for (Ship currentShip : placedShips) {
				for (Square filledSquare : currentShip.getOccupiedSquares()) {
					if (square.isEqual(filledSquare)) {
						return false;
					}
				}
			}
		}
		Ship newShip = new Ship(ship.getKind());
		newShip.setLocation(occupiedSquares);
		placedShips.add(newShip);
		return true;
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {
		Result attackRes = new Result();
		attackRes.setResult(AttackStatus.INVALID);
		attackRes.setLocation(new Square(x,y));

		// Bounds Checking
		if(x < 0 || x > 10 || y < 'A' || y > 'J'){
			attackRes.setResult(AttackStatus.INVALID);
			return attackRes;
		}

		// Check if hits enemy ship
			//If so, does it hit an good part of ship
		AttackStatus result = AttackStatus.INVALID;
		for (int i = 0; i < placedShips.size(); i++) {
			result = placedShips.get(i).takeDamageFrom(attackRes);
			if(result == AttackStatus.HIT || result == AttackStatus.HITARMR)
				break;
		}

		attackRes.setResult(result);
		attacks.add(attackRes);

		for(Result a : attacks){
			if(a.getLocation().isEqual(attackRes.getLocation()) && ( a.getResult() == AttackStatus.MISS || a.getResult() == AttackStatus.INVALID)){
				attackRes.setResult(AttackStatus.INVALID);
				attacks.remove(attackRes);
			}
		}


		if ( !doesPlayerHaveShipsAlive() ){
			attackRes.setResult(AttackStatus.SURRENDER);
		}


		return attackRes;
	}



	public boolean doesPlayerHaveShipsAlive() {
		for (Ship ship : placedShips) {
			if (ship.isAlive())
				return true;
		}
		return false;
	}

	public List<Ship> getShips() {
		return placedShips;
	}

	public void setShips(List<Ship> ships) {
		placedShips = ships;
	}

	public List<Result> getAttacks() {
		return this.attacks;
	}

	public void setAttacks(List<Result> attacks) {
		this.attacks = attacks;
	}
}
