import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BattleShip {
    public static void main(String[] args) {
        //System.out.println("Hello world!");
        Game x = new Game();
        x.player1Positions.printGameField();
        Scanner scanner = new Scanner(System.in);
        for (ShipCategory ship : ShipCategory.values()) {
            String shipName = ship.nameOfShipCategory;
            int shipSize = ship.size;
            boolean shipInSea = false;
            while (!shipInSea) {
                System.out.println("Enter the coordinates of the " + shipName + " (" + shipSize + " cells):");
                String inputCoordinateOne = scanner.next();
                String inputCoordinateTwo = scanner.next();
                Coordinate coordinateOne;
                Coordinate coordinateTwo;
                try {
                    coordinateOne = new Coordinate(inputCoordinateOne);
                    coordinateTwo = new Coordinate(inputCoordinateTwo);

                } catch (IllegalArgumentException e) {
                    System.out.println(e);
                    break;
                }

                try {
                    x.player1Positions.setShip(ship, coordinateOne, coordinateTwo);
                    shipInSea = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e);
                }
            }
            x.player1Positions.printGameField();
        }



    }
}

class Game {

    String status;
    String ss;
    GameField player1Positions;

    //Game x = new Game()
    public Game() {
        status = "Starting";
        player1Positions = new GameField();
    }

}

class Coordinate {
    private int rowCoordinate;
    private int lineCoordinate;

    Coordinate(String input) throws IllegalArgumentException {
        if (input.length() < 2 || input.length() > 3) {
            throw new IllegalArgumentException("Wrong format of input");
        } else {
            String letter = input.substring(0, 1).toUpperCase();
            rowCoordinate = Integer.parseInt(input.substring(1));
            for (GameFieldLines line : GameFieldLines.values()) {
                if (line.lineLetter.equalsIgnoreCase(letter)) {
                    lineCoordinate = line.lineNumber;
                    break;
                }
            }
        }
    }

    Coordinate(int x, int y) throws IllegalArgumentException {
        if (y > 0 || y <= GameField.fieldSize || x > 0 || x < GameField.fieldSize) {
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
}

class GameField {
    public static final int fieldSize = 11;
    public String[][] field;
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
                    for (int i = stern; i <= rostrum; i++) {
                        field[coordinateOne.getLineCoordinate()][i] = "O";
                    }
                } else {
                    throw new IllegalArgumentException("Error! You placed it too close to another one. Try again:");
                }

            } else if (isDirectionV) {
                rostrum = Math.max(coordinateOne.getLineCoordinate(), coordinateTwo.getLineCoordinate());
                stern = Math.min(coordinateOne.getLineCoordinate(), coordinateTwo.getLineCoordinate());

                //Ship Builder
                if (!collisionCheck(field, coordinateOne, coordinateTwo, isDirectionH, isDirectionV)) {
                    for (int i = stern; i <= rostrum; i++) {
                            field[i][coordinateOne.getRowCoordinate()] = "O";
                    }
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

    public void makeTurn() {

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

}

enum ShipCategory {
    AIRCRAFTCARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    final String nameOfShipCategory;
    int size;

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