import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javalib.impworld.*;
import javalib.worldimages.*;



//CS 2510, Assignment 9
//Assignment 9 - part 2
//McKenzie Camaryn
//mckenzie.cam
//Beckwith Louisa
//beckwith.l

//---------------------------------------------------------------------------------
//interface for IList<T>

interface IList<T> extends Iterable<T> {

  //returns the size of the IList
  public int size();

  //determines if the IList is a ConsList
  public boolean isCons();

  //casts the IList as a ConsList
  public ConsList<T> asCons();
}

//---------------------------------------------------------------------------------
//class for an empty list of T

class MtList<T> implements IList<T> {

  //returns the size of this
  public int size() {
    return 0;
  }

  //determines if the IList is a ConsList
  public boolean isCons() {
    return false;
  }

  //throws and exception because MtList cannot be cast as a ConsList
  public ConsList<T> asCons() {
    throw new UnsupportedOperationException("Don't do this!");
  }

  //returns a new iterator given this
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

}


//---------------------------------------------------------------------------------
//class for a nonempty list of T

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  //returns the size of this
  public int size() {
    return 1 + this.rest.size();
  }

  //determines if this is a ConsList
  public boolean isCons() {
    return true;
  }

  //casts as a ConsList
  public ConsList<T> asCons() {
    return this;
  }

  //returns a new iterator given this
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

}

//---------------------------------------------------------------------------------
//IListIterator<T> class 

class IListIterator<T> implements Iterator<T> {
  IList<T> items;

  IListIterator(IList<T> items) {
    this.items = items;
  }

  //does this sequence of items in IList have at least one more value?
  public boolean hasNext() {
    return this.items.isCons();
  }

  //get the next value in this sequence
  //effect: advance the iterator to the subsequent value
  public T next() {
    ConsList<T> itemsAsCons = this.items.asCons();
    T answer = itemsAsCons.first;
    this.items = itemsAsCons.rest;
    return answer;
  }

  //throws an exception
  public void remove() {
    throw new UnsupportedOperationException("Don't do this!");
  }
}
//---------------------------------------------------------------------------------
//class ForbiddenIslandWorld 

class ForbiddenIslandWorld extends World {
  // all the cells of the game, including the ocean
  IList<Cell> board;
  ArrayList<ArrayList<Cell>> cells;
  Player player;
  HelicopterTarget helicopter;
  ArrayList<Target> pieces;

  // the current height of the ocean
  int waterHeight;

  //counter for ticks
  int counter;

  // defines an int constant
  static final int ISLAND_SIZE = 64;

  //constructor for testing
  ForbiddenIslandWorld(String type) {

    //determines what type of island to make
    if (type.equals("mountain")) {
      this.cells = this.convertToCells(this.makeMountainIsland());
      this.board = this.convertToIList(this.cells);

    } else if (type.equals("random")) {
      this.cells = this.convertToCells(this.makeRandomMountain());
      this.board = this.convertToIList(this.cells);
    }

    else if (type.equals("randomterrain")) {
      ArrayList<ArrayList<Cell>> randomTerrainCellArrayList = 
          this.convertToCells(this.makeRandomTerrain());

      this.cells = randomTerrainCellArrayList;
      this.board = this.convertToIList(randomTerrainCellArrayList);
    }

    //creates a new player at random location
    Posn playerPosn = this.randomLoc();
    this.player = new Player(
        playerPosn.x * 10, playerPosn.y * 10, 10, Color.red, this.cells.get(playerPosn.x).get(playerPosn.y));

    //adds 5 targets to an ArrayList<Target>
    this.pieces = new ArrayList<Target>();
    for (int i = 0; i < 5; i++) {
      Posn targetPosn = this.randomLoc();
      Target t = new Target(targetPosn.x * 10, targetPosn.y * 10);

      this.pieces.add(t);
    }

    //adds a helicopter to the center of the island
    int x = ISLAND_SIZE / 2;
    int y = ISLAND_SIZE / 2;
    this.helicopter = new HelicopterTarget(x * 10, y * 10);

    this.counter = 0; 
    this.waterHeight = 0;
    this.initNeighbors();

  }

  //---------------------------------------------------------------------------------
  //returns a random posn on land
  public Posn randomLoc() {

    Random rand = new Random();

    int xLocation = rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE);
    int yLocation = rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE);

    while (this.cells.get(yLocation).get(xLocation).height <= 0) {
      xLocation = rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE);
      yLocation = rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE);
    }

    return new Posn(xLocation, yLocation);
  }
  //---------------------------------------------------------------------------------
  //removes targets from list (pieces) if they are in the same location as the player

  public ArrayList<Target> pickUpPieces() {
    ArrayList<Target> tempPieces = new ArrayList<Target>(this.pieces);

    for (Target t : pieces) {
      if (t.samePlayerLoc(this.player)) {
        tempPieces.remove(t);
      } 
    }
    return tempPieces;
  }

  //---------------------------------------------------------------------------------
  //returns an ArrayList<ArrayList<Double>> with cell heights based on equation
  public ArrayList<ArrayList<Double>> makeMountainIsland() {

    ArrayList<ArrayList<Double>> cellsHeights = new ArrayList<ArrayList<Double>>();
    Double maxHeight = ForbiddenIslandWorld.ISLAND_SIZE / 2.0;
    Integer center = maxHeight.intValue();

    //for each arraylist (row)
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {
      ArrayList<Double> row = new ArrayList<Double>();

      //for the objects in the inner arraylists (column)
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        int xDistance = Math.abs(center - j);
        int yDistance = Math.abs(center - i);
        int manhattanDistance = xDistance + yDistance;
        Double cellHeight = (double) (maxHeight - manhattanDistance);

        row.add(cellHeight);
      }
      cellsHeights.add(row);
    }
    return cellsHeights;
  }

  //--------------------------------------------------------------------------------- 

  //returns an ArrayList<ArrayList<Double>> with random cell heights
  public ArrayList<ArrayList<Double>> makeRandomMountain() {

    ArrayList<ArrayList<Double>> cellsHeights = new ArrayList<ArrayList<Double>>();
    Double maxHeight = ForbiddenIslandWorld.ISLAND_SIZE / 2.0;
    Integer center = maxHeight.intValue();
    Random rand = new Random();

    //for each arraylist (row)
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {
      ArrayList<Double> row = new ArrayList<Double>();

      //for the objects in the inner arraylists (column)
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        int xDistance = Math.abs(center - j);
        int yDistance = Math.abs(center - i);
        int manhattanDistance = xDistance + yDistance;
        Double cellHeight = (double) (maxHeight - manhattanDistance);

        if (cellHeight <= 0) {
          row.add(cellHeight);
        } 
        else {
          cellHeight = (rand.nextDouble() * maxHeight); 
          row.add(cellHeight);
        }
      }
      cellsHeights.add(row);
    }
    return cellsHeights;
  }
  //--------------------------------------------------------------------------------- 

  //returns an ArrayList<ArrayList<Double>> with random cell heights
  public ArrayList<ArrayList<Double>> makeRandomTerrain() {
    ArrayList<ArrayList<Double>> cellsHeights = new ArrayList<ArrayList<Double>>();
    Double maxHeight = ForbiddenIslandWorld.ISLAND_SIZE / 2.0;
    int centerInt = ForbiddenIslandWorld.ISLAND_SIZE / 2;

    //for each arraylist (row)
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE + 1; i = i + 1) {
      ArrayList<Double> row = new ArrayList<Double>();
      //for the objects in the inner arraylists (column)
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE + 1; j = j + 1) {
        row.add(0.0);
      }
      cellsHeights.add(row);
    }

    // set the mids of the outside
    ArrayList<Double> topRow = cellsHeights.get(0);
    ArrayList<Double> bottomRow = cellsHeights.get(cellsHeights.size() - 1);

    int mid = (cellsHeights.size() - 1) / 2; 
    ArrayList<Double> midRow = cellsHeights.get(mid);
    topRow.set(mid, 1.0);
    bottomRow.set(mid, 1.0);
    midRow.set(0, 1.0); 
    midRow.set(midRow.size() - 1, 1.0);

    // set the center
    ArrayList<Double> tempRow2 = cellsHeights.get(centerInt);
    tempRow2.set(centerInt, maxHeight);

    //top left quadrant 
    this.setCenter(new Posn(0, 0), 
        new Posn(mid, 0), 
        new Posn(0, mid), 
        new Posn(mid, mid),
        cellsHeights);

    //top right quadrant 
    this.setCenter(new Posn(mid, 0), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, 0), 
        new Posn(mid, mid), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, mid),
        cellsHeights);

    //bottom left quadrant 
    this.setCenter(new Posn(0, mid), 
        new Posn(mid, mid), 
        new Posn(0, ForbiddenIslandWorld.ISLAND_SIZE), 
        new Posn(mid, ForbiddenIslandWorld.ISLAND_SIZE),
        cellsHeights);

    //bottom right quadrant 
    this.setCenter(new Posn(mid, mid), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, mid), 
        new Posn(mid, ForbiddenIslandWorld.ISLAND_SIZE), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, ForbiddenIslandWorld.ISLAND_SIZE),
        cellsHeights);

    return cellsHeights;
  }

  //-------------------------------------------------------------------------------------------------

  public ArrayList<ArrayList<Double>> setCenter(Posn upperL, Posn upperR, Posn lowerL, Posn lowerR, 
      ArrayList<ArrayList<Double>> arr) {

    int centerx = (upperR.x + upperL.x) / 2;
    int centery = (upperR.y + lowerR.y) / 2;

    if ((Math.abs((upperR.x - upperL.x)) > 1) && (Math.abs((lowerR.x - lowerL.x)) > 1)
        && (Math.abs((upperR.y - lowerR.y)) > 1) && (Math.abs((upperL.y - lowerL.y)) > 1)) {

      Random rand = new Random(); 

      double upperLeft = arr.get(upperL.y).get(upperL.x);
      double upperRight = arr.get(upperR.y).get(upperR.x);
      double lowerLeft = arr.get(lowerL.y).get(lowerL.x); 
      double lowerRight = arr.get(lowerR.y).get(lowerR.x); 

      double quadrantSize = Math.abs(upperR.x - upperL.x);

      //left
      double random = (rand.nextDouble() * (quadrantSize / 4));
      double randomNegative = (rand.nextDouble() * (quadrantSize / 4));
      double l = ((upperLeft + lowerLeft) / 2) + random - randomNegative;

      //top
      random = (rand.nextDouble() * (quadrantSize / 4));
      randomNegative = (rand.nextDouble() * (quadrantSize / 4));
      double t = ((upperLeft + upperRight) / 2) + random - randomNegative; 

      //bottom
      random = (rand.nextDouble() * (quadrantSize / 4));
      double b = ((lowerLeft + lowerRight) / 2) + random; 

      //right
      random = (rand.nextDouble() * (quadrantSize / 4));
      randomNegative = (rand.nextDouble() * (quadrantSize / 4));
      double r = ((lowerRight + upperRight) / 2) + random - randomNegative; 

      // mid
      random = (rand.nextDouble() * (quadrantSize / 4));
      randomNegative = (rand.nextDouble() * (quadrantSize / 4));
      double average = (upperLeft + upperRight + lowerLeft + lowerRight) / 4; 
      double mid = average + random - randomNegative; 

      ArrayList<Double> centerRow = arr.get(centery);
      ArrayList<Double> topRow = arr.get(upperL.y);
      ArrayList<Double> bottomRow = arr.get(lowerL.y);

      centerRow.set(centerx, mid);

      if (centerRow.get(lowerL.x) == 0) {
        centerRow.set(lowerL.x, l); 
      }
      if (centerRow.get(lowerR.x) == 0) {
        centerRow.set(lowerR.x, r);
      }
      if (topRow.get(centerx) == 0) {
        topRow.set(centerx, t);
      }
      if (bottomRow.get(centerx) == 0) {
        bottomRow.set(centerx, b);
      }

      // quad 1
      Posn quad1UpperLeft = upperL; 
      Posn quad1UpperRight = new Posn(centerx, upperR.y);
      Posn quad1LowerLeft = new Posn(lowerL.x, centery);
      Posn quad1LowerRight = new Posn(centerx, centery);
      // quad 2
      Posn quad2UpperLeft = new Posn(centerx, upperL.y);
      Posn quad2UpperRight = upperR;
      Posn quad2LowerLeft = new Posn(centerx, centery);
      Posn quad2LowerRight = new Posn(lowerR.x, centery);
      // quad 3
      Posn quad3UpperLeft = new Posn(upperL.x, centery);
      Posn quad3UpperRight = new Posn(centerx, centery);
      Posn quad3LowerLeft = lowerL;
      Posn quad3LowerRight = new Posn(centerx, lowerR.y);
      // quad 4
      Posn quad4UpperLeft = new Posn(centerx, centery);
      Posn quad4UpperRight = new Posn(upperR.x, centery);
      Posn quad4LowerLeft = new Posn(centerx, lowerL.y);
      Posn quad4LowerRight = lowerR;

      this.setCenter(quad1UpperLeft, quad1UpperRight, quad1LowerLeft, quad1LowerRight, arr);
      this.setCenter(quad2UpperLeft, quad2UpperRight, quad2LowerLeft, quad2LowerRight, arr);
      this.setCenter(quad3UpperLeft, quad3UpperRight, quad3LowerLeft, quad3LowerRight, arr);
      this.setCenter(quad4UpperLeft, quad4UpperRight, quad4LowerLeft, quad4LowerRight, arr);
      return arr;

    }
    else {
      return arr;
    }
  }


  //--------------------------------------------------------------------------------- 

  //converts the doubles into cells within the ArrayList<ArrayList<>>
  public ArrayList<ArrayList<Cell>> convertToCells(ArrayList<ArrayList<Double>> heights) {
    ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();

    //for each arraylist (row)
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {
      ArrayList<Cell> row = new ArrayList<Cell>();

      //for the objects in the inner arraylists (column)
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        Double cellHeight = heights.get(i).get(j);

        if (cellHeight <= 0) {
          OceanCell oceanCell = new OceanCell(i, j);

          row.add(oceanCell);
        }
        else {
          Cell cell = new Cell(i, j, cellHeight);

          row.add(cell);
        }
      }
      cells.add(row);
    }
    return cells;
  }

  //-------------------------------------------------------------------------------------------------
  //initializes the neighboring cells

  void initNeighbors() {
    for (int i = 0; i < this.cells.size(); i = i + 1) {
      ArrayList<Cell> row = this.cells.get(i);
      for (int j = 0; j < row.size(); j = j + 1) {
        Cell currentCell = this.cells.get(i).get(j);

        if (i == 0 && j == 0) {
          currentCell.left = this.cells.get(i).get(j + 1);
          currentCell.top = this.cells.get(i + 1).get(j);
        }
        else if (i == this.cells.size() - 1
            && j == this.cells.size() - 1) {
          currentCell.right = this.cells.get(i).get(j - 1);
          currentCell.bottom = this.cells.get(i - 1).get(j);
        }
        else if (i == this.cells.size() - 1
            && j == 0) {
          currentCell.left = this.cells.get(i).get(j + 1);
          currentCell.bottom = this.cells.get(i - 1).get(j);
        }
        else if (i == 0
            && j == this.cells.size() - 1) {
          currentCell.right =  this.cells.get(i).get(j - 1);
          currentCell.top = this.cells.get(i + 1).get(j);
        }
        else if (j == 0) {
          currentCell.left = this.cells.get(i).get(j + 1);
          currentCell.top = this.cells.get(i + 1).get(j);
          currentCell.bottom = this.cells.get(i - 1).get(j);
        }
        else if (i == 0) {
          currentCell.left = this.cells.get(i).get(j + 1);
          currentCell.right = this.cells.get(i).get(j - 1);
          currentCell.top = this.cells.get(i + 1).get(j);
        }
        else if (j == this.cells.size() - 1) {
          currentCell.bottom = this.cells.get(i - 1).get(j);
          currentCell.right = this.cells.get(i).get(j - 1);
          currentCell.top = this.cells.get(i + 1).get(j);
        }
        else if (i == this.cells.size() - 1) {
          currentCell.right = this.cells.get(i).get(j - 1);
          currentCell.left = this.cells.get(i).get(j + 1);
          currentCell.bottom = this.cells.get(i - 1).get(j);
        }
        else {
          currentCell.left = this.cells.get(i).get(j + 1);
          currentCell.right = this.cells.get(i).get(j - 1);
          currentCell.top = this.cells.get(i + 1).get(j);
          currentCell.bottom = this.cells.get(i - 1).get(j);
        }
        // row.set(j, currentCell);
      }

      // this.cells.set(i, row);
    }
    //  this.board = this.convertToIList(this.cells);
  }

  //---------------------------------------------------------------------------------

  //converts the given ArrayList<ArrayList<Cell>> into an IList
  public IList<Cell> convertToIList(ArrayList<ArrayList<Cell>> cells) {
    IList<Cell> iListCells = new MtList<Cell>();

    //for each arraylist (row)
    for (int i = 0; i < ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {

      //for the objects in the inner arraylists (column)
      for (int j = 0; j < ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        iListCells = new ConsList<Cell>(cells.get(i).get(j), iListCells);
      }
    }
    return iListCells;
  }

  //--------------------------------------------------------------------------------- 
  //events that happen when specified keys are pressed

  //keeps score of player's moves
  int score = 0;

  public void onKeyEvent(String key) {

    //resets the game and creates a new island, player, and targets
    if (key.equals("m")) {
      this.cells = this.convertToCells(this.makeMountainIsland());
      this.board = this.convertToIList(this.cells);

      Posn playerPosn = this.randomLoc();
      this.player = new Player(
          playerPosn.x * 10, playerPosn.y * 10, 10, Color.red, 
          this.cells.get(playerPosn.x).get(playerPosn.y));

      this.pieces = new ArrayList<Target>();
      for (int i = 0; i < 5; i++) {
        Posn targetPosn = this.randomLoc();
        Target t = new Target(targetPosn.x * 10, targetPosn.y * 10);

        this.pieces.add(t);

      }

      int x = ISLAND_SIZE / 2;
      int y = ISLAND_SIZE / 2;
      this.helicopter = new HelicopterTarget(x * 10, y * 10);

      this.waterHeight = 0;
    }

    else if (key.equals("r")) {
      this.cells =  this.convertToCells(this.makeRandomMountain());
      this.board = this.convertToIList(this.cells);

      Posn playerPosn = this.randomLoc();
      this.player = new Player(
          playerPosn.x * 10, playerPosn.y * 10, 10, Color.red, 
          this.cells.get(playerPosn.x).get(playerPosn.y));

      this.pieces = new ArrayList<Target>();
      for (int i = 0; i < 5; i++) {
        Posn targetPosn = this.randomLoc();
        Target t = new Target(targetPosn.x * 10, targetPosn.y * 10);

        this.pieces.add(t);
      }

      int x = ISLAND_SIZE / 2;
      int y = ISLAND_SIZE / 2;
      this.helicopter = new HelicopterTarget(x * 10, y * 10);

      this.waterHeight = 0;
    }

    else if (key.equals("t")) {
      this.cells = this.convertToCells(this.makeRandomTerrain());
      this.board = this.convertToIList(this.cells);

      Posn playerPosn = this.randomLoc();
      this.player = new Player(
          playerPosn.x * 10, playerPosn.y * 10, 10, Color.red, 
          this.cells.get(playerPosn.x).get(playerPosn.y));

      this.pieces = new ArrayList<Target>();
      for (int i = 0; i < 5; i++) {
        Posn targetPosn = this.randomLoc();
        Target t = new Target(targetPosn.x * 10, targetPosn.y * 10);

        this.pieces.add(t);
      }

      int x = ISLAND_SIZE / 2;
      int y = ISLAND_SIZE / 2;
      this.helicopter = new HelicopterTarget(x * 10, y * 10);

      this.waterHeight = 0;
    }

    //moves the player right, left, up, and down
    else if (key.equals("right")) {
      this.player.update(this.player.x + 10, this.player.y, this.player.onCell.right);
      score++;
    }

    else if (key.equals("left")) {
      this.player.update(this.player.x - 10, this.player.y, this.player.onCell.left);
      score++;
    }

    else if (key.equals("up")) {
      this.player.update(this.player.x, this.player.y - 10, this.player.onCell.top);
      score++;
    }

    else if (key.equals("down")) {
      this.player.update(this.player.x, this.player.y + 10, this.player.onCell.bottom);
      score++;
    }

    this.pieces = this.pickUpPieces();

  }

  //raises the water level by one foot every ten ticks
  public void onTick() {
    if (this.counter == 10) {
      this.waterHeight = this.waterHeight + 1;
      this.counter = 0;
      //flood the cell 
      for (Cell cell : this.board) {
        cell.floodCell(this.waterHeight);
      }
    } 
    else {
      this.counter = this.counter + 1;
    }   
  } 

  //returns the WorldScene after the cells are placed on it
  public WorldScene makeScene() {
    int drawSize = ForbiddenIslandWorld.ISLAND_SIZE * 10;
    WorldScene scene = new WorldScene(drawSize, drawSize);

    for (Cell cell : board) {
      cell.drawCell(scene, ForbiddenIslandWorld.ISLAND_SIZE / 2, this.waterHeight);
    }

    helicopter.drawTarget(scene);
    for (Target target : pieces) {
      target.drawTarget(scene);
    }
    player.drawPlayer(scene);

    TextImage scoreText = new TextImage("score: " + String.valueOf(score), Color.white);
    scene.placeImageXY(scoreText, ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 4);

    return scene;
  }


  //ends the World
  public WorldEnd worldEnds() {
    if (this.player.onCell.isFlooded) {
      return new WorldEnd(true, this.makeLosingScene());

    } else if (this.helicopter.samePlayerLoc(this.player) 
        && this.pieces.size() == 0) {
      return new WorldEnd(true, this.makeWinningScene());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }


  //the WorldScene if the player loses
  public WorldScene makeLosingScene() {
    int drawSize = ForbiddenIslandWorld.ISLAND_SIZE * 10;
    int x = drawSize / 2;
    int y = drawSize / 2;  
    WorldScene scene = new WorldScene(drawSize, drawSize);
    WorldImage lose = new FromFileImage("lose.png");

    scene.placeImageXY(lose, x, y);
    return scene;

  }

  //the WorldScene if they player wins
  public WorldScene makeWinningScene() {
    int drawSize = ForbiddenIslandWorld.ISLAND_SIZE * 10;
    int x = ForbiddenIslandWorld.ISLAND_SIZE / 2;
    int y = ForbiddenIslandWorld.ISLAND_SIZE / 2;  
    WorldScene scene = new WorldScene(drawSize, drawSize);
    WorldImage win = new FromFileImage("win.png");

    scene.placeImageXY(win, x, y);
    return scene;

  }
}

//--------------------------------------------------------------------------------- 
//class for the player 

class Player {
  int x;
  int y;
  int radius;
  Color color;
  Cell onCell;

  Player(int x, int y, int radius, Color color, Cell onCell) {
    this.x = x;
    this.y = y;
    this.radius = radius;
    this.color = color;
    this.onCell = onCell;

  }

  //draws the player
  public void drawPlayer(WorldScene scene) {
    WorldImage player = new FromFileImage("pilot-icon.png");
    scene.placeImageXY(player, this.x, this.y);
  }

  //moves the player
  public void update(int x, int y, Cell onCell) {
    this.x = x;
    this.y = y;

    this.onCell = onCell;
  }
}
//--------------------------------------------------------------------------------- 
//class for Target

class Target {
  int x;
  int y;
  WorldImage image = new FromFileImage("star.png");

  Target(int x, int y) {
    this.x = x;
    this.y = y;
  }

  //determines if this is in the same location as the player
  boolean samePlayerLoc(Player player) {
    return this.x == player.x 
        && this.y == player.y;
  } 

  //draws this
  public void drawTarget(WorldScene scene) {
    scene.placeImageXY(this.image, (this.x) + 5, (this.y) + 5);
  }
}
//--------------------------------------------------------------------------------- 
//class for HelicopterTarget

class HelicopterTarget extends Target {
  WorldImage image = new FromFileImage("helicopter.png");

  HelicopterTarget(int x, int y) {
    super(x, y);
  }

  //draws this
  public void drawTarget(WorldScene scene) {
    scene.placeImageXY(this.image, (this.x) + 5, (this.y) + 5);
  }

}











