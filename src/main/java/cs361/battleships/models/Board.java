package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
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
	    if (doesPLayerHaveOneShipSunk()){
	        return laser(x,y);
        }
	    return bomb(x,y);
	}

    public Result bomb(int x, char y) {
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

		/*if(result == AttackStatus.SUNK){
			for(HealthSquare hs : attackRes.getShip().getHealthSquares()){
				attacks.add(new Result(AttackStatus.SUNK, attackRes.getShip(), new Square(hs.getRow(), hs.getColumn())));
			}
		}

		attackRes.setResult(result);
		attacks.add(attackRes);*/

        attackRes.setResult(result);
        attacks.add(attackRes);

        if(result == AttackStatus.SUNK){
            for(HealthSquare hs : attackRes.getShip().getHealthSquares()){
                boolean ifsquareishit = false;
                for(int i = 0; i < attacks.size(); i++) {
                    if(hs.getRow() == attacks.get(i).getLocation().getRow()
                            && hs.getColumn() == attacks.get(i).getLocation().getColumn()){

                        if(ifsquareishit){
                            attacks.remove(i);
                            i--;
                        }else {
                            attacks.set(i, new Result(AttackStatus.SUNK, attackRes.getShip(), new Square(hs.getRow(), hs.getColumn())));
                            ifsquareishit = true;
                        }
                    }
                }
                if(! ifsquareishit) {
                    attacks.add(new Result(AttackStatus.SUNK, attackRes.getShip(), new Square(hs.getRow(), hs.getColumn())));
                }
            }
        }


        if ( !doesPlayerHaveShipsAlive() ){
            attackRes.setResult(AttackStatus.SURRENDER);
            attacks.add(attackRes);
        }

        return attackRes;
    }

    public Result laser(int x, char y) {
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
            if(result == AttackStatus.HIT || result == AttackStatus.HITARMR || result == AttackStatus.SUNK) {


                attackRes.setResult(result);
                attacks.add(attackRes);

                if (result == AttackStatus.SUNK) {
                    for (HealthSquare hs : attackRes.getShip().getHealthSquares()) {
                        boolean ifsquareishit = false;
                        for (int j = 0; j < attacks.size(); j++) {
                            System.out.println("ROW: " + Integer.toString(hs.getRow()) + "COLUMN: " + hs.getColumn());
                            System.out.println("ROW: " + Integer.toString(attacks.get(j).getLocation().getRow()) + "COLUMN: " + attacks.get(j).getLocation().getColumn());
                            if (hs.getRow() == attacks.get(j).getLocation().getRow()
                                    && hs.getColumn() == attacks.get(j).getLocation().getColumn()) {

                                if (ifsquareishit) {
                                    attacks.remove(j);
                                    i--;
                                } else {

                                    System.out.println("here");
                                    attacks.set(j, new Result(AttackStatus.SUNK, attackRes.getShip(), new Square(hs.getRow(), hs.getColumn())));
                                    ifsquareishit = true;
                                }
                            }
                        }
                        if (!ifsquareishit) {
                            attacks.add(new Result(AttackStatus.SUNK, attackRes.getShip(), new Square(hs.getRow(), hs.getColumn())));
                        }
                    }
                }

                if ( !doesPlayerHaveShipsAlive() ){
                    attackRes.setResult(AttackStatus.SURRENDER);
                    attacks.add(attackRes);
                    return attackRes;
                }

            }
        }




        if(attackRes.getResult() == AttackStatus.INVALID){
            attackRes.setResult(AttackStatus.MISS);
            attacks.add(attackRes);
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

	private boolean doesPLayerHaveOneShipSunk(){
        for (Ship ship : placedShips) {
            if (!ship.isAlive())
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
