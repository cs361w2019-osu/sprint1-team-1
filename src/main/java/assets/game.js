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
    BATTLESHIP:4,
    SUBMARINE:5
};


function makeMoveFleetModal(){
    var arrows = document.getElementsByClassName("hoverable");

    Array.prototype.forEach.call(arrows, function(arrow){
        arrow.addEventListener("click", function() {
            console.log("In arrow", arrow);
            if (arrow.textContent === "▲") {
                console.log("Up");
                sendXhr("POST", "/move_ships", {game: game, direction: 'N'}, function (data) {
                    game = data;
                    redrawGrid();
                });
            } else if (arrow.textContent === "◀") {
                sendXhr("POST", "/move_ships", {game: game, direction: 'W'}, function (data) {
                    game = data;
                    redrawGrid();
                });
            } else if (arrow.textContent === "▶") {

                sendXhr("POST", "/move_ships", {game: game, direction: 'E'}, function (data) {
                    game = data;
                    redrawGrid();
                });
            } else if (arrow.textContent === "▼"){
                sendXhr("POST", "/move_ships", {game: game, direction: 'S'}, function (data) {
                    game = data;
                    redrawGrid();
                });
            }
            document.getElementById('move-fleet-modal').classList.add("hidden");
            document.getElementById('opponent').classList.remove("hidden");
        });
    });

    // Add modal button event stuff
    modalBody = document.getElementById("move-fleet-modal");
    modalBody.classList.add("hidden");

    modalButton = document.getElementById('move-fleet');
    modalButton.disabled = true;
    modalButton.addEventListener("click", function(){
       modalBody.classList.remove("hidden");
       document.getElementById("opponent").classList.add("hidden");
    });
}



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

function incrHits(elementId,hits) {
    if (elementId === 'opponent') {
        opponentHits.textContent = hits;
    } else if (elementId === 'player') {
        playerHits.textContent = hits;
    } else {

    }
}

function incrSinks(elementId,sinks) {
    if (elementId === 'opponent') {
        opponentSinks.textContent = sinks;
    } else if (elementId === 'player') {
        playerSinks.textContent = sinks;
    } else {

    }
}

function checkCounters(board, elementId) {
    var hits = 0, sinks = 0;
    var arr = [];
    var numAttacks = board.attacks.length;
    if (numAttacks > 0) {
        for( let i = 0; i < board.attacks.length; i++) {
            if (board.attacks[i].result === "HIT" || board.attacks[i].result === "SUNK" ) {
                hits++;
            }
            if (board.attacks[i].result === "SUNK" && ! arr.includes(board.attacks[i].ship.length) ) {
                arr.push( board.attacks[i].ship.length)
                sinks++;
            }
        }
        incrHits(elementId, hits);
        incrSinks(elementId, sinks);
    }
    console.log('sinks', sinks, board);
    if(sinks >= 2){
        moveFleet = document.getElementById('move-fleet')
        moveFleet.disabled = false;
    }



}

function missingHealthIndices(board, ship) {
    var missingInd = [];
    var missing = [];


    var i;
    var j;
    for(i = 0; i < board.attacks.length; i++) {
        if(board.attacks[i].ship.kind == ship.kind) {
          if(board.attacks[i].result == "HIT" || board.attacks[i].result == "SUNK") {
              missing.push(board.attacks[i].location);
          }
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
        className = "sink";
    } else if (attack.result === "SURRENDER") {
        alert(surrenderText);
    } else if(attack.result === "HITARMR"){
        className = "hitarmr";
    } else if (attack.result === "MISS_SUB") {
        className = "misssub";
    }

    document.getElementById(elementId).rows[attack.location.row-1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);


    });
    shipsArr = board.ships;

    var i;
    var j;
    for(i = 0; i < shipsArr.length; i++) {
        ship = shipsArr[i];
        indices = missingHealthIndices(board, ship);
        if(indices.length > 0) {
            scoreId = "";
            if(elementId == "player") {

                scoreId = "left-table-shipscore";
            } else {
                scoreId = "right-table-shipscore";
            }
            var classname = (indices.length == ship.length ? "sink" : "hit");
            console.log(indices);
            for(j = 0; j < indices.length; j++) {
                switch(ship.kind) {
                    case "MINESWEEPER":
                        document.getElementById(scoreId).rows[indices[j]].cells[0].classList.add(classname);
                        break;
                    case "DESTROYER":
                        document.getElementById(scoreId).rows[indices[j]].cells[1].classList.add(classname);
                        break;
                    case "BATTLESHIP":
                        document.getElementById(scoreId).rows[indices[j]].cells[2].classList.add(classname);
                        break;
                }

            }
        }
    }
}

function drawPlayer() {
  var i;
  var j;
  for(i = 0; i < game.playersBoard.ships.length; i++) {
    var currShip = game.playersBoard.ships[i];
    for(j = 0; j < currShip.occupiedSquares.length; j++) {
      var square = currShip.occupiedSquares[j];
      var image;
      if(j === 0) {
          if (currShip.kind === "SUBMARINE") {
              image = document.createElement("img");
              imageScore = document.createElement("img");
              image.src = "/assets/images/ship_middle.png";
              imageScore.src = "/assets/images/ship_middle.png";
          } else {
              image = document.createElement("img");
              imageScore = document.createElement("img");
              image.src = "/assets/images/ship_tip.png";
              imageScore.src = "/assets/images/ship_tip.png";
          }
      } else if (j === 1 && currShip.kind === "SUBMARINE") {
          image = document.createElement("img");
          imageScore = document.createElement("img");
          image.src = "/assets/images/ship_tip.png";
          imageScore.src = "/assets/images/ship_tip.png";
      } else if (!(currShip.kind === "SUBMARINE") && j === currShip.occupiedSquares.length - 1) {
          image = document.createElement("img");
          imageScore = document.createElement("img");
          image.src = "/assets/images/flag_tip_white.png";
          imageScore.src = "/assets/images/flag_tip_white.png";
      } else if (currShip.kind === "SUBMARINE" && j === currShip.occupiedSquares.length - 1) {
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


      if ( document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].childElementCount === 0 )
         document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].appendChild(image);
      else{
          document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].removeChild(document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].childNodes[0]);
          document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].appendChild(image);
      }
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
          /*case "SUBMARINE":
              document.getElementById("left-table-shipscore").rows[j].cells[3].appendChild(imageScore);
              break;*/
      }

    }
  }
}

function drawOponnent() {
  var i;
  var j;
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
}

function drawSonar() {
  var i;
  for(i = 0; i < game.opponentsBoard.sonars.length; i++) {
    var sonar = game.opponentsBoard.sonars[i];


    var left = Math.max(0, sonar.column.charCodeAt(0) - 'A'.charCodeAt(0) - 2);
    var right = Math.min(9, sonar.column.charCodeAt(0) + 2 - 'A'.charCodeAt(0));
    var bottom = Math.min(10, sonar.row + 2);
    var top = Math.max(1, sonar.row - 2);

    var emptyArea = [];
    var j;
    for(j = top; j < sonar.row; j++) {
      var square = {row:j,column:sonar.column};
      if(JSON.stringify(sonar.foundShips).includes(JSON.stringify(square)) == false) {
        emptyArea.push(square);
      }
    }
    for(j = bottom; j > sonar.row; j--) {
      var square = {row:j,column:sonar.column};
      if(JSON.stringify(sonar.foundShips).includes(JSON.stringify(square)) == false) {
        emptyArea.push(square);
      }
    }
    for(j = left; j < right + 1; j++) {
      var currColumn = String.fromCharCode(65 + j)
      var square = {row:sonar.row,column:currColumn};
      if(JSON.stringify(sonar.foundShips).includes(JSON.stringify(square)) == false) {
        emptyArea.push(square);
      }
    }
    //Finds the corners around the sonar starting from top right, goes clockwise
    var corners = [{row:Math.max(top,sonar.row - 1),column:String.fromCharCode(65 + Math.min(sonar.column.charCodeAt(0) - 65 + 1,right))},
                   {row:Math.min(bottom,sonar.row + 1),column:String.fromCharCode(65 + Math.min(sonar.column.charCodeAt(0) - 65 + 1,right))},
                   {row:Math.min(bottom,sonar.row + 1),column:String.fromCharCode(65 + Math.max(sonar.column.charCodeAt(0) - 65 - 1, left))},
                   {row:Math.max(top,sonar.row - 1),column:String.fromCharCode(65 + Math.max(sonar.column.charCodeAt(0) - 65 - 1, left))}
                  ]
    for(j = 0; j < corners.length; j++) {
      var square = corners[j];
      if(JSON.stringify(sonar.foundShips).includes(JSON.stringify(square)) == false
      && JSON.stringify(emptyArea).includes(JSON.stringify(square)) == false) {
        emptyArea.push(square);
      }
    }



    for(j = 0; j < sonar.foundShips.length; j++) {
      var square = sonar.foundShips[j];

      document.getElementById("opponent").rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("sonarFound");
    }

    for(j = 0; j < emptyArea.length; j++) {
      var square = emptyArea[j];

      document.getElementById("opponent").rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("sonarEmpty");
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


    drawPlayer()
    drawOponnent()
    drawSonar()

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
            placedShips++;
            placedShipsList.push(shipList[shipType]);
            redrawGrid();
            showHideShipModal(false);
            if (placedShips == 4) {
                isSetup = false;
                document.getElementById("place-ship").classList.add("hidden");
                registerCellListener((e) => {});
            }
        });
    } else if (usingSonar) {
        if (sonarUses < parseInt(opponentSinks.textContent)) {
          sendXhr("POST", "/sonar", {game: game, x: row, y: col}, function(data) {
              sonarUses++; // increment the number of sonar pulses
              // Use the Sonar Pulse

              game = data;
              redrawGrid();
          });
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
            if(placedShips != 4)
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
                case 5:
                    document.getElementById("place_submarine_div").style.display = "none";
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
    makeMoveFleetModal();
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
    document.getElementById("place_submarine").addEventListener("click", function(e) {
        shipType = "SUBMARINE";
        registerCellListener(place(5));
    });
    document.getElementById("place-ship").addEventListener("click", function(e) {
        showHideShipModal(false);
    });
    sendXhr("GET", "/game", {}, function(data) {
        game = data;
    });
};
