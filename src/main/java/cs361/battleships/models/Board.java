package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class Board {

	private List<Ship> placedShips;
	private List<Result> attacks;
	private List<Sonar> sonars;
	private int fleetMoves;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		fleetMoves = 0;
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
		if (isVertical) { // ship is vertical
			if (x + ship.getLength() - 1 > 10 || x < 1) {
				return false;
			} else if (ship.getKind().equals("SUBMARINE")){ // checking for submarine special cases
				if (y > 'I') { // extra piece sticks out of right side of board

					return false;
				} else {
					// add extra piece to the occupied squares
					occupiedSquares.add(new Square(x + 2, (char)(y + 1) ));
				}
			}
			for (int i = 0; i < ship.getLength(); i++) {
				occupiedSquares.add(new Square(x + i, y));
			}
		} else { // ship is horizontal
			if (y + ship.getLength() - 'A' > 10 || y < 'A') {
				return false;
			} else if (ship.getKind().equals("SUBMARINE")) {
				if (x < 2) { // extra piece sticks out of board

					return false;
				} else {
					// add extra piece to the occupied squares
					occupiedSquares.add(new Square(x - 1, (char)(y + 2) ));
				}
			}
			for (int i = 0; i < ship.getLength(); i++) {
				occupiedSquares.add(new Square(x, (char)(y + i)));
			}
		}
		// place if statement around this to allow submarines to placed over other ships
		Ship newShip = new Ship(ship.getKind());
		newShip.setLocation(occupiedSquares);
		if (!newShip.getKind().equals("SUBMARINE") ||newShip.getHealthSquares().get(0).isisSubmerged() == false){
			for (Square square : occupiedSquares) {
				for (Ship currentShip : placedShips) {
					for (Square filledSquare : currentShip.getOccupiedSquares()) {
						if (square.isEqual(filledSquare)) {
							if(currentShip.getKind().equals("SUBMARINE")) {
								if(currentShip.getHealthSquares().get(0).isisSubmerged() == false) {
									return false;
								}
							} else {
								return false;
							}
						}
					}
				}
			}
		}
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

		boolean isInvalid = false;
		for(Result attack : attacks){
			if(attack.getLocation().isEqual(attackRes.getLocation()) && ( attack.getResult() == AttackStatus.MISS || attack.getResult() == AttackStatus.HIT || attack.getResult() == AttackStatus.SUNK)){
				isInvalid = true;
			}
		}

        // Check if hits enemy ship
        //If so, does it hit an good part of ship
        AttackStatus result = AttackStatus.INVALID;
        for (int i = 0; i < placedShips.size(); i++) {
            result = placedShips.get(i).takeDamageFromBomb(attackRes);
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

        Ship sub = new Ship();
        for(Ship ship : placedShips) {
        	if(ship.getKind().equals("SUBMARINE")) {
        		sub = ship;
			}
		}

        boolean isInvalid = false;
        for(Result attack : attacks){
            if(attack.getLocation().isEqual(attackRes.getLocation()) && ( attack.getResult() == AttackStatus.MISS || attack.getResult() == AttackStatus.HIT || attack.getResult() == AttackStatus.SUNK)){
				for(HealthSquare hs : sub.getHealthSquares()) {
					if(hs.equals(attack.getLocation()) && hs.getHealth() > 0 ) {
						break;
					} else {
						isInvalid = true;
					}
				}
            }
        }

        // Check if hits enemy ship
        //If so, does it hit an good part of ship
        AttackStatus result = AttackStatus.INVALID;
        for (int i = 0; i < placedShips.size(); i++) {
            result = placedShips.get(i).takeDamageFromLaser(attackRes);
            attackRes.setShip(placedShips.get(i));
            if(result == AttackStatus.HIT || result == AttackStatus.HITARMR || result == AttackStatus.SUNK) {


                attackRes.setResult(result);
                attacks.add(attackRes);

                if (result == AttackStatus.SUNK) {
                    for (HealthSquare hs : attackRes.getShip().getHealthSquares()) {
                        boolean ifsquareishit = false;
                        for (int j = 0; j < attacks.size(); j++) {
                            if (hs.getRow() == attacks.get(j).getLocation().getRow()
                                    && hs.getColumn() == attacks.get(j).getLocation().getColumn()) {

                                if (ifsquareishit) {
                                    attacks.remove(j);
                                    j--;
                                } else {

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
            }
        }

        if(isInvalid && attackRes.getResult() == AttackStatus.INVALID){
            attackRes.setResult(AttackStatus.MISS);
            attacks.add(attackRes);
        }


        if ( !doesPlayerHaveShipsAlive() ){
            attackRes.setResult(AttackStatus.SURRENDER);
            attacks.add(attackRes);
            return attackRes;
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

    public int getFleetMoves() {
		return this.fleetMoves;
	}

    public void moveShips(char direction) {
		List<String> movedShips = new ArrayList<>();
		for(int i = 0; i < placedShips.size(); i++) {
			for(int j = 0; j < placedShips.size(); j++) {
				Ship currShip = placedShips.get(j);
				if (movedShips.contains(currShip.getKind()) == false && currShip.isAlive()) {
					if (placedShips.get(j).move(direction, placedShips)) {
						movedShips.add(placedShips.get(j).getKind());
					}
				}
			}
		}
		fleetMoves++;
	}

	public int shipsAlive() {
		int alive = 0;
		for(Ship ship : placedShips) {
			if(ship.isAlive() == false) {
				alive++;
			}
		}
		return alive;
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
