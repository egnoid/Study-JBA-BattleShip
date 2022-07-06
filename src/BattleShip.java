import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BattleShip {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Game x = new Game();
        x.setupShipsToGameField(x.player1Positions, scanner);
        x.passMoveToAnotherPlayer(scanner);
        x.setupShipsToGameField(x.player2Positions, scanner);

//        System.out.println("The game starts!");
        x.status = GameStatuses.PLAYER1TURN;
        x.passMoveToAnotherPlayer(scanner);

        while (x.status != GameStatuses.PLAYER1WIN && x.status != GameStatuses.PLAYER2WIN) {
            GameField player = x.status == GameStatuses.PLAYER1TURN ? x.player1Positions : x.player2Positions;
            GameField enemy =  x.status == GameStatuses.PLAYER1TURN ? x.player2Positions : x.player1Positions;
            enemy.printGameField(true);
            System.out.println("---------------------");
            player.printGameField();

            if (x.status == GameStatuses.PLAYER1TURN) {
                System.out.println("Player 1, it's your turn:");
            } else {
                System.out.println("Player 2, it's your turn:");
            }


            boolean successReadTargetCoordinate = false;
            Coordinate shotTarget = null;
            while (!successReadTargetCoordinate) {
                try {
                    scanner.nextLine();
                    shotTarget = new Coordinate(scanner.next());
                    successReadTargetCoordinate = true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Error! You entered the wrong coordinates! Try again:");
                }
            }

            x.shot(shotTarget, enemy);
            System.out.println();
            x.updateGameStatus();
            if (x.status != GameStatuses.PLAYER1WIN && x.status != GameStatuses.PLAYER2WIN) {
                x.passMoveToAnotherPlayer(scanner);
            }
        }


    }

}

class Game {

    GameStatuses status;
    String whoseTurn;
    GameField player1Positions;
    GameField player2Positions;

    //Game x = new Game()
    public Game() {
        status = GameStatuses.STARTING;
        player1Positions = new GameField();
        player2Positions = new GameField();
        whoseTurn = "Player1";
    }

    void setupShipsToGameField(GameField player, Scanner scanner) {
        String setupMessage;
        if (player == player1Positions) {
            setupMessage = "Player 1, place your ships on the game field";
        } else {
            setupMessage = "Player 2, place your ships to the game field";
        }

        System.out.println(setupMessage);
        System.out.println();

        player.printGameField();
        System.out.println();
        for (ShipCategory ship : ShipCategory.values()) {
            String shipName = ship.nameOfShipCategory;
            int shipSize = ship.size;
            boolean shipInSea = false;
            while (!shipInSea) {
                System.out.println("Enter the coordinates of the " + shipName + " (" + shipSize + " cells):");

                System.out.println();
                String inputCoordinateOne = scanner.next();
                String inputCoordinateTwo = scanner.next();
                Coordinate coordinateOne;
                Coordinate coordinateTwo;
                System.out.println();
                try {
                    coordinateOne = new Coordinate(inputCoordinateOne);
                    coordinateTwo = new Coordinate(inputCoordinateTwo);

                } catch (IllegalArgumentException e) {
                    System.out.println(e);
                    break;
                }

                try {
                    player.setShip(ship, coordinateOne, coordinateTwo);
                    shipInSea = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e);
                }
            }

            player.printGameField();
            System.out.println();
        }
    }

    void passMoveToAnotherPlayer(Scanner s) {
        System.out.println("\nPress Enter and pass the move to another player");
        try{System.in.read();}
        catch(Exception e){}

    }

    public void shot(Coordinate target, GameField playerPos) {
        if ("O".equalsIgnoreCase(playerPos.field[target.getLineCoordinate()][target.getRowCoordinate()])
                || "X".equalsIgnoreCase(playerPos.field[target.getLineCoordinate()][target.getRowCoordinate()])) {
            playerPos.field[target.getLineCoordinate()][target.getRowCoordinate()] = "X";

            boolean youDestroyShip = false;

            for (Ship ship : playerPos.ships) {
                System.out.print(ship.category + " =|= ");
            } System.out.println();


            for (Ship ship: playerPos.ships) {
System.out.println("Корабель" + ship.category + " содержит ");
for (Coordinate c : ship.decks) {System.out.print("[" + c.getLineCoordinate() + "|" + c.getRowCoordinate() + "]");}
System.out.println(" ][ зарегистрировано " + ship.decks.size() + "/" + ship.deckIsDestroyed.size() + " палуб" );
                for (int i = 0; i < ship.decks.size(); i++) {
System.out.println("Палуба, № " + i);
                    if (ship.decks.get(i).equals(target)) {
                        youDestroyShip = true;
System.out.println("Совпадение цель-палуба №:" + i);
                        ship.deckIsDestroyed.set(i, true);
                        for (int j = 0; j < ship.deckIsDestroyed.size(); j++) {
                            youDestroyShip = youDestroyShip && ship.deckIsDestroyed.get(j);
System.out.println("Палуба разбита " + ship.deckIsDestroyed.get(j));
                        }
                        break;

                    }
                    }
                }
            if (youDestroyShip) {
                System.out.println("You sank a ship!");
            } else {
                System.out.println("You hit a ship!");
            }

        } else {
            playerPos.field[target.getLineCoordinate()][target.getRowCoordinate()] = "M";
            System.out.println("You missed!");
        }
    }

    public void updateGameStatus() {
        int shipCellsUndamaged = 0;
        GameField player = status == GameStatuses.PLAYER1TURN ? player2Positions : player1Positions;
        for (int i = 1; i < player.field.length; i++) {
            for (int j = 1; j < player.field[0].length; j++) {
                if ("O".equalsIgnoreCase(player.field[i][j])) {
                    shipCellsUndamaged++;
                }
            }
        }
        if (shipCellsUndamaged == 0) {
            status = status == GameStatuses.PLAYER1TURN ? GameStatuses.PLAYER1WIN : GameStatuses.PLAYER2WIN;
            System.out.println("You sank the last ship. You won. Congratulations!");
        } else {
            status = status == GameStatuses.PLAYER1TURN ? GameStatuses.PLAYER2TURN : GameStatuses.PLAYER1TURN;
        }

    }
}

class Coordinate {
    private int rowCoordinate;
    private int lineCoordinate;

    Coordinate(String input) throws IllegalArgumentException {
        lineCoordinate = -1;
        if (input.length() < 2 || input.length() > 3) {
            throw new IllegalArgumentException("Wrong format of input");
        } else {
            String letter = input.substring(0, 1).toUpperCase();
            rowCoordinate = Integer.parseInt(input.substring(1));
            if (rowCoordinate < 1 || rowCoordinate > 10) {
                throw new IllegalArgumentException("Error! You entered the wrong coordinates! Try again:");
            }
            for (GameFieldLines line : GameFieldLines.values()) {
                if (line.lineLetter.equalsIgnoreCase(letter)) {
                    lineCoordinate = line.lineNumber;
                    break;
                }
            }
        }
        if (lineCoordinate == -1) {
            throw new IllegalArgumentException("Error! You entered the wrong coordinates! Try again:");
        }
    }

    Coordinate(int x, int y) throws IllegalArgumentException {
        if (y > 0 && y <= GameField.fieldSize && x > 0 && x < GameField.fieldSize) {
            rowCoordinate = y;
            lineCoordinate = x;
        } else {
            throw new IllegalArgumentException("Coordinate is out of boundaries");
        }

    }

    public int getRowCoordinate() {
        return rowCoordinate;
    }
    public int getLineCoordinate() {
        return lineCoordinate;
    }

    public boolean equals(Coordinate coordinate) {
        if (this.rowCoordinate == coordinate.rowCoordinate && this.lineCoordinate == coordinate.lineCoordinate) {
            return true;
        } else {
            return false;
        }
    }
}

class Ship {
    public ArrayList<Coordinate> decks;
    public ArrayList<Boolean> deckIsDestroyed;
    public ShipCategory category;

    Ship(ShipCategory shipCategory) {
        category = shipCategory;
        decks = new ArrayList<>();
        deckIsDestroyed = new ArrayList<>();
    }
}

class GameField {
    public static final int fieldSize = 11;
    public String[][] field;
    public ArrayList<Ship> ships;
    public GameField() {
        field = new String[fieldSize][fieldSize];
        field[0][0] = " ";

        for (int j = 1; j < fieldSize; j++) {
            field[0][j] = String.valueOf(j);
        }

        int counterForGameField = 0;

        for (GameFieldLines line : GameFieldLines.values()) {
            counterForGameField++;
            field[counterForGameField][0] = line.lineLetter;
        }

        for (int i = 1; i < fieldSize; i++) {
            for (int j = 1; j < fieldSize; j++) {
                field[i][j] = "~";
            }
        }
        ships = new ArrayList<Ship>();
    }

    public void setShip(ShipCategory ship, Coordinate coordinateOne, Coordinate coordinateTwo)
            throws IllegalArgumentException {
        boolean isDirectionH = coordinateOne.getLineCoordinate() == coordinateTwo.getLineCoordinate();
        boolean isDirectionV = coordinateOne.getRowCoordinate() == coordinateTwo.getRowCoordinate();
        boolean isDirectionOk = isDirectionH || isDirectionV;
        int shipCalcSize;
        if (isDirectionOk) {
            shipCalcSize = Math.abs(coordinateOne.getLineCoordinate() + coordinateOne.getRowCoordinate() -
                    coordinateTwo.getLineCoordinate() - coordinateTwo.getRowCoordinate()) + 1;
        } else {
            throw new IllegalArgumentException("Error! Wrong ship location! Try again:");
        }

        int rostrum;
        int stern;

        if (shipCalcSize == ship.size) {
            if (isDirectionH) {
                rostrum = Math.max(coordinateOne.getRowCoordinate(), coordinateTwo.getRowCoordinate());
                stern = Math.min(coordinateOne.getRowCoordinate(), coordinateTwo.getRowCoordinate());

                //Ship Builder
                if (!collisionCheck(field, coordinateOne, coordinateTwo, isDirectionH, isDirectionV)) {
                    Ship newShip = new Ship(ship);

                    for (int i = stern; i <= rostrum; i++) {
                        field[coordinateOne.getLineCoordinate()][i] = "O";
                        newShip.decks.add(new Coordinate(coordinateOne.getLineCoordinate(), i));
                        newShip.deckIsDestroyed.add(false);
                    }
                    ships.add(newShip);

                } else {
                    throw new IllegalArgumentException("Error! You placed it too close to another one. Try again:");
                }

            } else if (isDirectionV) {
                rostrum = Math.max(coordinateOne.getLineCoordinate(), coordinateTwo.getLineCoordinate());
                stern = Math.min(coordinateOne.getLineCoordinate(), coordinateTwo.getLineCoordinate());
//There is repeated code == source of problems
//Please apply DRY design principle
                //Ship Builder
                if (!collisionCheck(field, coordinateOne, coordinateTwo, isDirectionH, isDirectionV)) {
                    Ship newShip = new Ship(ship);

                    for (int i = stern; i <= rostrum; i++) {
                            field[i][coordinateOne.getRowCoordinate()] = "O";
                            newShip.decks.add(new Coordinate(i, coordinateOne.getRowCoordinate()));
                            newShip.deckIsDestroyed.add(false);
                    }
                    ships.add(newShip);

                } else {
                    throw new IllegalArgumentException("Error! You placed it too close to another one. Try again:");
                }
            }
        } else {
            throw new IllegalArgumentException("Error! Wrong length of the " + ship + "! Try again:");
        }


    }

    public boolean collisionCheck(String[][] field, Coordinate a, Coordinate b, boolean isDirectionH, boolean isDirectionV) {
        List<Coordinate> shipCollisionModel = new ArrayList<>();
        int s;
        int f;
        if (isDirectionH) {
            if (a.getRowCoordinate() < b.getRowCoordinate()) {
                s = a.getRowCoordinate(); f = b.getRowCoordinate();
            } else {
                s = b.getRowCoordinate(); f = a.getRowCoordinate();
            }
            for (int i = s; i <= f; i++) {
                Coordinate addCoord = new Coordinate(a.getLineCoordinate(), i);
                shipCollisionModel.add(addCoord);
            }
        } else if (isDirectionV) {
            if (a.getLineCoordinate() < b.getLineCoordinate()) {
                s = a.getLineCoordinate(); f = b.getLineCoordinate();
            } else {
                s = b.getLineCoordinate(); f = a.getLineCoordinate();
            }
            for (int i = s; i <= f; i++) {
                Coordinate addCoord = new Coordinate(i, a.getRowCoordinate());
                shipCollisionModel.add(addCoord);
            }
        }

        boolean collisionExist = false;
        String target;
        for (Coordinate point: shipCollisionModel) {
            for (int i = point.getLineCoordinate() - 1; i <= point.getLineCoordinate() + 1; i++) {
                if (i < 1 || i >= GameField.fieldSize) {continue;}
                for (int j = point.getRowCoordinate() - 1; j <= point.getRowCoordinate() + 1; j++) {
                    if (j > 1 && j < GameField.fieldSize) {
                        target = field[i][j];
                        if ("O".equalsIgnoreCase(target)) {
                            collisionExist = true;
                            break;
                        }
                    }
                }
            }
        }
        // проитерировать shipCollisionModel, окружить каждую точку квадратом 3х3
        // проверить есть ли в облаке значений поле со значением "O"
        //это будет значить, что коллизия есть

        return collisionExist;
    }


    public void printGameField() {
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                System.out.print(field[i][j]);
                if (j < fieldSize - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    public void printGameField(boolean fog) {
        if (fog) {
            for (int i = 0; i < fieldSize; i++) {
                for (int j = 0; j < fieldSize; j++) {
                    if ("O".equalsIgnoreCase(field[i][j])) {
                        System.out.print("~");
                    } else {
                        System.out.print(field[i][j]);
                    }
                    if (j < fieldSize - 1) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        } else printGameField();

    }


}

enum ShipCategory {
    AIRCRAFTCARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    final String nameOfShipCategory;
    final int size;

    ShipCategory(String name, int size) {
        this.nameOfShipCategory = name;
        this.size = size;
    }
}

enum GameFieldLines {
    A("A", 1), B("B", 2), C("C", 3), D("D", 4),
    E("E", 5), F("F", 6), G("G", 7), H("H", 8),
    I("I", 9), J("J", 10);
    final String lineLetter;
    final int lineNumber;
    GameFieldLines(String row, int num) {
        this.lineLetter = row;
        this.lineNumber = num;
    }
}

enum GameStatuses {
    STARTING("Starting"),
    PLAYER1TURN("PLayer 1 turn"),
    PLAYER2TURN("PLayer 2 turn"),
    PLAYER1WIN("Player 1 win"),
    PLAYER2WIN("Player 2 win");

    final String statusName;

    GameStatuses(String statusName) {
        this.statusName = statusName;
    }
}