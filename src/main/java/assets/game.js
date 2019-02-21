var isSetup = true;
var placedShips = 0;
var game;
var shipType;
var vertical;
var placedShipsList = [];
var opponentHits = document.getElementById('opponent-hits');
var opponentSinks = document.getElementById('opponent-sinks');
var playerHits = document.getElementById('player-hits');
var playerSinks = document.getElementById('player-sinks');
var useSonar = document.getElementById('use-sonar');
var usingSonar = false;
var sonarUses = 0;


var shipList = {
    MINESWEEPER:2,
    DESTROYER:3,
    BATTLESHIP:4
};

function makeScoreGrid(table) {

  var i;
  var j;
  for (i=0; i<4; i++) {
      let row = document.createElement('tr');
      for (j=0; j<3; j++) {
          let column = document.createElement('td');
          row.appendChild(column);
      }
      table.appendChild(row);
  }
}

function makeGrid(table, isPlayer) {

  var i;
  for (i=0; i<10; i++) {
      let row = document.createElement('tr');
      for (j=0; j<10; j++) {
          let column = document.createElement('td');
          column.addEventListener("click", cellClick);
          row.appendChild(column);
      }
      table.appendChild(row);
  }
}

function incrHits(elementId) {
    if (elementId === 'opponent') {
        opponentHits.textContent = parseInt(opponentHits.textContent) + 1;
    } else if (elementId === 'player') {
        playerHits.textContent = parseInt(playerHits.textContent) + 1;
    } else {
        console.log("elementId for incrHits: ", elementId);
    }
}

function incrSinks(elementId) {
    if (elementId === 'opponent') {
        opponentSinks.textContent = parseInt(opponentSinks.textContent) + 1;
    } else if (elementId === 'player') {
        playerSinks.textContent = parseInt(playerSinks.textContent) + 1;
    } else {
        console.log("elementId for incrSinks: ", elementId);
    }
}

function checkCounters(board, elementId) {
    var numAttacks = board.attacks.length;
    if (numAttacks > 0) {
        if (board.attacks[numAttacks - 1].result === "HIT") {
            incrHits(elementId);
        } else if (board.attacks[numAttacks - 1].result === "SUNK") {
            incrHits(elementId);
            incrSinks(elementId);
        }
    }
}

function missingHealthIndices(ship) {
    missing = [];
    missingInd = [];

    var i;
    var j;
    for(i = 0; i < ship.occupiedSquares.length; i++) {
        flag = false;
        for(j = 0; j < ship.healthSquares.length; j++) {
            if(JSON.stringify(ship.occupiedSquares[i]) == JSON.stringify(ship.healthSquares[j])) {
                flag = true;
                break;
            }
        }
        if(flag == false) {
            missing.push(ship.occupiedSquares[i]);
        }
    }
    if(missing.length > 0) {
        if (ship.occupiedSquares[0].row == ship.occupiedSquares[1].row) {
            for (i = 0; i < missing.length; i++) {
              missingInd.push(missing[i].column.charCodeAt(0) - ship.occupiedSquares[0].column.charCodeAt(0));
            }
        } else {
            for (i = 0; i < missing.length; i++) {
              missingInd.push(missing[i].row - ship.occupiedSquares[0].row);
            }
        }
    }
    return missingInd;
}

useSonar.addEventListener('click', function(event) {
    usingSonar = true;
    // TODO make the next attack a sonar pulse, don't have opponent make an attack
});

function markHits(board, elementId, surrenderText) {
    board.attacks.forEach((attack) => {
        let className;
    if (attack.result === "MISS") {
        className = "miss";
    } else if (attack.result === "HIT") {
        className = "hit";
    } else if (attack.result === "SUNK") {
        className = "hit";
    } else if (attack.result === "SURRENDER") {
        alert(surrenderText);
    }

    document.getElementById(elementId).rows[attack.location.row-1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);


});
    shipsArr = board.ships;

    var i;
    var j;
    for(i = 0; i < shipsArr.length; i++) {
        if(shipsArr[i].healthSquares.length < shipsArr[i].length) {
          ship = shipsArr[i];
          indices = missingHealthIndices(ship);
          scoreId = "";
          if(elementId == "player") {
            console.log(indices);
            scoreId = "left-table-shipscore";
          } else {
            scoreId = "right-table-shipscore";
          }
          for(j = 0; j < indices.length; j++) {
            switch(ship.kind) {
              case "MINESWEEPER":
                  document.getElementById(scoreId).rows[indices[j]].cells[0].classList.add("hit");
                  break;
              case "DESTROYER":
                  document.getElementById(scoreId).rows[indices[j]].cells[1].classList.add("hit");
                  break;
              case "BATTLESHIP":
                  document.getElementById(scoreId).rows[indices[j]].cells[2].classList.add("hit");
                  break;
            }

          }
        }
    }
}

function redrawGrid() {
    Array.from(document.getElementById("opponent").childNodes).forEach((row) => row.remove());
    Array.from(document.getElementById("player").childNodes).forEach((row) => row.remove());
    Array.from(document.getElementById("left-table-shipscore").childNodes).forEach((row) => row.remove());
    Array.from(document.getElementById("right-table-shipscore").childNodes).forEach((row) => row.remove());
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    makeScoreGrid(document.getElementById("left-table-shipscore"));
    makeScoreGrid(document.getElementById("right-table-shipscore"));
    if (game === undefined) {
        return;
    }


    var i;
    var j;
    for(i = 0; i < game.playersBoard.ships.length; i++) {
      var currShip = game.playersBoard.ships[i];
      for(j = 0; j < currShip.occupiedSquares.length; j++) {
        var square = currShip.occupiedSquares[j];
        var image;
        if(j == 0) {
          image = document.createElement("img");
          imageScore = document.createElement("img");
          image.src = "/assets/images/ship_tip.png";
          imageScore.src = "/assets/images/ship_tip.png";
        } else if (j == currShip.occupiedSquares.length - 1) {
          image = document.createElement("img");
          imageScore = document.createElement("img");
          image.src = "/assets/images/flag_tip_white.png";
          imageScore.src = "/assets/images/flag_tip_white.png";
        } else {
          image = document.createElement("img");
          imageScore = document.createElement("img");
          image.src = "/assets/images/ship_middle.png";
          imageScore.src = "/assets/images/ship_middle.png";
        }

        if(currShip.shipVertical == false) {
            image.classList.add("rotate");
        }

        document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].appendChild(image);
        switch(currShip.kind) {
            case "MINESWEEPER":
                document.getElementById("left-table-shipscore").rows[j].cells[0].appendChild(imageScore);
                break;
            case "DESTROYER":
                document.getElementById("left-table-shipscore").rows[j].cells[1].appendChild(imageScore);
                break;
            case "BATTLESHIP":
                document.getElementById("left-table-shipscore").rows[j].cells[2].appendChild(imageScore);
                break;
        }

      }
    }

    for(i = 0; i < game.opponentsBoard.ships.length; i++) {
        var currShip = game.opponentsBoard.ships[i];
        for (j = 0; j < currShip.occupiedSquares.length; j++) {
            var image;
            if(j == 0) {
                image = document.createElement("img");
                image.src = "/assets/images/ship_tip.png";
            } else if (j == currShip.occupiedSquares.length - 1) {
                image = document.createElement("img");
                image.src = "/assets/images/flag_tip_white.png";
            } else {
                image = document.createElement("img");
                image.src = "/assets/images/ship_middle.png";
            }

            switch(currShip.kind) {
                case "MINESWEEPER":
                    document.getElementById("right-table-shipscore").rows[j].cells[0].appendChild(image);
                    break;
                case "DESTROYER":
                    document.getElementById("right-table-shipscore").rows[j].cells[1].appendChild(image);
                    break;
                case "BATTLESHIP":
                    document.getElementById("right-table-shipscore").rows[j].cells[2].appendChild(image);
                    break;
            }
        }
    }

    /*
    game.playersBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
        document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("occupied");

    }));
    */
    markHits(game.opponentsBoard, "opponent", "You won the game");
    markHits(game.playersBoard, "player", "You lost the game");

    checkCounters(game.opponentsBoard, "opponent");
    checkCounters(game.playersBoard, "player")
}

var oldListener;
function registerCellListener(f) {
    showHideShipModal(true);

    var i;
    var j;
    let el = document.getElementById("player");
    for (i=0; i<10; i++) {
        for (j=0; j<10; j++) {
            let cell = el.rows[i].cells[j];
            cell.removeEventListener("mouseover", oldListener);
            cell.removeEventListener("mouseout", oldListener);
            cell.addEventListener("mouseover", f);
            cell.addEventListener("mouseout", f);
        }
    }
    oldListener = f;
}



function cellClick() {
    let row = this.parentNode.rowIndex + 1;
    let col = String.fromCharCode(this.cellIndex + 65);
    if (isSetup) {
        sendXhr("POST", "/place", {game: game, shipType: shipType, x: row, y: col, isVertical: vertical}, function(data) {
            game = data;
            game.playersBoard.ships[game.playersBoard.ships.length - 1].shipVertical = vertical;
            redrawGrid();
            placedShips++;
            placedShipsList.push(shipList[shipType]);
            showHideShipModal(false);
            if (placedShips == 3) {
                isSetup = false;
                document.getElementById("place-ship").classList.add("hidden");
                registerCellListener((e) => {});
            }
        });
    } else if (usingSonar) {
        if (sonarUses < parseInt(opponentSinks.textContent)) {
            sonarUses++; // increment the number of sonar pulses
            // Use the Sonar Pulse
            console.log("You used the sonar");
        } else {
            alert("You must sink a ship before you can use Sonar");
        }
        usingSonar = false; // reset using sonar after one pulse
    } else {
        sendXhr("POST", "/attack", {game: game, x: row, y: col}, function(data) {
            game = data;
            redrawGrid();
        })
    }
}

function sendXhr(method, url, data, handler) {
    var req = new XMLHttpRequest();
    req.addEventListener("load", function(event) {
        if (req.status != 200) {
            alert("Cannot complete the action");
            if(placedShips != 3)
                showHideShipModal(false);
            return;
        }
        handler(JSON.parse(req.responseText));
    });
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/json");
    req.send(JSON.stringify(data));
}

function place(size) {
    return function() {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
        vertical = document.getElementById("is_vertical").checked;
        let table = document.getElementById("player");
        for (let i=0; i<size; i++) {
            let cell;
            if(vertical) {
                let tableRow = table.rows[row+i];
                if (tableRow === undefined) {
                    // ship is over the edge; let the back end deal with it
                    break;
                }
                cell = tableRow.cells[col];
            } else {
                cell = table.rows[row].cells[col+i];
            }
            if (cell === undefined) {
                // ship is over the edge; let the back end deal with it
                break;
            }
            cell.classList.toggle("placed");
        }

    }
}

function showHideShipModal(doHide){
    if(!doHide) {
        document.getElementById("modal-backdrop").classList.remove("hidden");
        document.getElementById("start-phase-modal").classList.remove("hidden");

        placedShipsList.forEach(function(len){
            switch (len) {
                case 2:
                    document.getElementById("place_minesweeper_div").style.display = "none";
                    break;
                case 3:
                    document.getElementById("place_destroyer_div").style.display = "none";
                    break;
                case 4:
                    document.getElementById("place_battleship_div").style.display = "none";
                    break;
            }
        });
    }
    else if(doHide){
        document.getElementById("modal-backdrop").classList.add("hidden");
        document.getElementById("start-phase-modal").classList.add("hidden");
    }
}

function initGame() {
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    makeScoreGrid(document.getElementById("right-table-shipscore"));
    makeScoreGrid(document.getElementById("left-table-shipscore"));
    document.getElementById("place_minesweeper").addEventListener("click", function(e) {
        shipType = "MINESWEEPER";
        registerCellListener(place(2));
    });
    document.getElementById("place_destroyer").addEventListener("click", function(e) {
        shipType = "DESTROYER";
        registerCellListener(place(3));
    });
    document.getElementById("place_battleship").addEventListener("click", function(e) {
        shipType = "BATTLESHIP";
        registerCellListener(place(4));
    });
    document.getElementById("place-ship").addEventListener("click", function(e) {
        showHideShipModal(false);
    });
    sendXhr("GET", "/game", {}, function(data) {
        game = data;
    });
};
