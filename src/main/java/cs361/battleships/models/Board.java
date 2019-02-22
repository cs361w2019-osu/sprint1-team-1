package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private List<Ship> placedShips;
	private List<Result> attacks;
	private List<Sonar> sonars;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		this.placedShips = new ArrayList<>();
		this.attacks = new ArrayList<>();
		this.sonars = new ArrayList<>();
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
			if (x + ship.getLength() - 1 > 10 || x < 1) {
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


		for(Result attack : attacks){
			if(attack.getLocation().isEqual(attackRes.getLocation()) && ( attack.getResult() == AttackStatus.MISS || attack.getResult() == AttackStatus.HIT || attack.getResult() == AttackStatus.SUNK)){
				return attackRes;
			}
		}

		// Check if hits enemy ship
			//If so, does it hit an good part of ship
		AttackStatus result = AttackStatus.INVALID;
		for (int i = 0; i < placedShips.size(); i++) {
			result = placedShips.get(i).takeDamageFrom(attackRes);
			attackRes.setShip(placedShips.get(i));
			if(result == AttackStatus.HIT || result == AttackStatus.HITARMR || result == AttackStatus.SUNK)
				break;
		}

		if(result == AttackStatus.SUNK){
			for(HealthSquare hs : attackRes.getShip().getHealthSquares()){
				attacks.add(new Result(AttackStatus.SUNK, attackRes.getShip(), new Square(hs.getRow(), hs.getColumn())));
			}
		}

		attackRes.setResult(result);
		attacks.add(attackRes);



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

	public boolean placeSonar(int row, char column) {
	    if(row< 1 || row > 10) {
	        return false;
        }
	    if((int) column - (int)'A' < 0 || (int) 'J' - (int) column < 0) {
	        return false;
        }
	    if(sonars.size() >= 2) {
	        return false;
        }

	    Sonar sonar = new Sonar();
	    sonar.setSonar(placedShips, row, column);
	    sonars.add(sonar);
	    return true;
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

	public List<Sonar> getSonars() {
	    return this.sonars;
    }

	public void setAttacks(List<Result> attacks) {
		this.attacks = attacks;
	}
}
