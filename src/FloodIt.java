//Assignment9
//Qian Dayu
//qiandayu17
//Giancola Timothy
//tgiancola

import java.util.ArrayList;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the
  // screen
  int x;
  int y;
  String color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  Cell(int x, int y, String color) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
  }

  // sets the top and bottom neighbors
  void verticalNeighbors(Cell top, Cell bottom) {
    this.top = top;
    top.bottom = this;
    this.bottom = bottom;
    bottom.top = this;
  }

  // sets the left and right neighbors
  void horizontalNeighbors(Cell left, Cell right) {
    this.left = left;
    left.right = this;
    this.right = right;
    right.left = this;
  }

  // sets all the neighbors of this cell
  public void neighbors(Cell left, Cell right, Cell top, Cell bottom) {
    this.left = left;
    left.right = this;
    this.right = right;
    right.left = this;
    this.top = top;
    top.bottom = this;
    this.bottom = bottom;
    bottom.top = this;
  }

  // get the color of this cell
  public Color getColor() {
    if (this.color.equals("black")) {
      return new Color(0, 0, 0);
    }
    else if (this.color.equals("blue")) {
      return new Color(0, 0, 255);
    }
    else if (this.color.equals("red")) {
      return new Color(255, 0, 0);
    }
    else if (this.color.equals("white")) {
      return new Color(255, 255, 255);
    }
    else if (this.color.equals("green")) {
      return new Color(0, 255, 255);
    }
    else {
      return new Color(190, 190, 190);
    }
  }

  // draws this cell, color changing depending on the cell the player clicked
  WorldImage drawCell(Cell colorToBe) {
    WorldImage i;
    i = new RectangleImage(30, 30, "solid", this.getColor());
    return i.movePinholeTo(new Posn(0, 0));
  }

  // helper method to generate a random number in the range -n to n
  int randomInt(int n) {
    return new Random().nextInt(6);
  }

  // get the posn
  Posn getPosn() {
    return new Posn(this.x, this.y);
  }

  // make the cells near this cell flooded if they have the same color
  void turnOnFlooded() {
    if (this.bottom.color.equals(this.color) && this.bottom != this && !this.bottom.flooded) {
      this.bottom.flooded = true;
      this.bottom.turnOnFlooded();
    }
    if (this.right.color.equals(this.color) && this.right != this && !this.right.flooded) {
      this.right.flooded = true;
      this.right.turnOnFlooded();
    }
    if (this.left.color.equals(this.color) && this.left != this && !this.left.flooded) {
      this.left.flooded = true;
      this.left.turnOnFlooded();
    }
    if (this.top.color.equals(this.color) && this.top != this && !this.top.flooded) {
      this.top.flooded = true;
      this.top.turnOnFlooded();
    }
  }

  // infect the surrounding cells with the color
  void infect(String color) {
    this.color = color;
    if (this.bottom.flooded && this.bottom != this) {
      this.bottom.color = color;
      this.bottom.flooded = false;
      this.bottom.infect(color);
    }
    if (this.right.flooded && this.right != this) {
      this.right.color = color;
      this.right.flooded = false;
      this.right.infect(color);
    }
    if (this.left.flooded && this.left != this) {
      this.left.color = color;
      this.left.flooded = false;
      this.left.infect(color);
    }
    if (this.top.flooded && this.top != this) {
      this.top.color = color;
      this.top.flooded = false;
      this.top.infect(color);
    }
  }
}

// represents a list of T
interface IList<T> {

  // returns the object at index x in the list (the first element is 0);
  T get(int x);

  // returns the size of the list
  int size();

  // adds onto the start of the list
  IList<T> add(T x);

  // set the new item into the List
  void set(int i, T x);
}

// represents a non-empty list of T
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  public T get(int x) {
    if (x == 0) {
      return this.first;
    }
    else {
      return this.rest.get(x - 1);
    }
  }

  public int size() {
    return 1 + this.rest.size();
  }

  public IList<T> add(T x) {
    return new ConsList<T>(x, this);
  }

  // set the new item into the List
  public void set(int i, T x) {
    if (i == 0) {
      this.first = x;
    }
    else {
      this.rest.set(i - 1, x);
    }
  }
}

// represents an empty list of T
class MtList<T> implements IList<T> {

  MtList() {
    // empty list
  }

  public T get(int x) {
    throw new IndexOutOfBoundsException("Input x is larger than list size");
  }

  public int size() {
    return 0;
  }

  public IList<T> add(T x) {
    return new ConsList<T>(x, this);
  }

  // set the new item into the List
  public void set(int i, T x) {
    // you don't set items into an empty list
  }
}

class FloodItWorld extends World {

  // Defines an int constant. player can change this constant to
  // play games with different difficulties.
  static final int BOARD_SIZE = 20;

  static final int IMAGE_SCALE = 30;

  // All the cells of the game
  IList<Cell> board;

  int maxSteps = BOARD_SIZE * 2;

  int stepsTaken;

  // determine if all the cells are in the same color
  boolean end;

  FloodItWorld() {
    arrayListToIList(initialCells());
  }

  // draws the game scene
  public WorldScene makeScene() {
    return this.drawCells();
  }

  // draw the max steps
  public WorldImage makemaxStepsImage() {
    return new TextImage(Integer.toString(maxSteps), Color.red);
  }

  // draw the steps taken
  public WorldImage makestepsTakenImage() {
    return new TextImage(Integer.toString(stepsTaken), Color.black);
  }

  // draws all the cells
  public WorldScene drawCells() {
    WorldScene scene = new WorldScene(BOARD_SIZE, BOARD_SIZE + 70);
    for (int i = 0; i < board.size(); i++) {
      Cell cell = board.get(i);
      scene.placeImageXY(cell.drawCell(cell), cell.x * IMAGE_SCALE + IMAGE_SCALE / 2,
          cell.y * IMAGE_SCALE + IMAGE_SCALE / 2);
    }
    scene.placeImageXY(makemaxStepsImage(), BOARD_SIZE * IMAGE_SCALE / 3 * 2,
        BOARD_SIZE * IMAGE_SCALE + 35);
    scene.placeImageXY(makestepsTakenImage(), BOARD_SIZE * IMAGE_SCALE / 3,
        BOARD_SIZE * IMAGE_SCALE + 35);
    return scene;
  }

  public WorldImage lastImage(String s) {
    return new TextImage(s, Color.blue);
  }

  // helper method to generate a random number in the range -n to n
  int randomInt(int n) {
    return new Random().nextInt(6);
  }

  // produce the last image of this world by adding text to the image
  public WorldScene lastScene(String s) {
    WorldScene scene = this.drawCells();
    scene.placeImageXY(this.lastImage(s), BOARD_SIZE * IMAGE_SCALE / 2,
        BOARD_SIZE * IMAGE_SCALE + 35);
    return scene;
  }

  // the end of the world
  public WorldEnd worldEnds() {
    if (this.maxSteps >= this.stepsTaken && this.endCheck(board, board.get(0).color)) {
      return new WorldEnd(true, this.lastScene("You Win!"));
    }
    if (this.maxSteps < this.stepsTaken) {
      return new WorldEnd(true, this.lastScene("You Lose!"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // create a new color string based on the given int
  String createColor(int i) {
    if (i == 0) {
      return "black";
    }
    else if (i == 1) {
      return "blue";
    }
    else if (i == 2) {
      return "red";
    }
    else if (i == 3) {
      return "white";
    }
    else if (i == 4) {
      return "green";
    }
    else {
      return "grey";
    }
  }

  // Creates a 2D ArrayList of cells
  ArrayList<ArrayList<Cell>> initialCells() {
    ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();
    // i represents the y of the cell; j represents the x of the cell
    for (int i = 0; i < BOARD_SIZE; i++) {
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int j = 0; j < BOARD_SIZE; j++) {
        row.add(new Cell(j, i, this.createColor(this.randomInt(6))));
      }
      cells.add(row);
    }
    // i represents the y of the cell; j represents the x of the cell
    // sets up the neighbors of the cells
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        Cell curr = cells.get(i).get(j);
        if (i == 0) {
          curr.verticalNeighbors(curr, cells.get(i + 1).get(j));
        }
        else if (i == BOARD_SIZE - 1) {
          curr.verticalNeighbors(cells.get(i - 1).get(j), curr);
        }
        else {
          curr.verticalNeighbors(cells.get(i - 1).get(j), cells.get(i + 1).get(j));
        }
        if (j == 0) {
          curr.horizontalNeighbors(curr, cells.get(i).get(j + 1));
        }
        else if (j == BOARD_SIZE - 1) {
          curr.horizontalNeighbors(cells.get(i).get(j - 1), curr);
        }
        else {
          curr.horizontalNeighbors(cells.get(i).get(j - 1), cells.get(i).get(j + 1));
        }
      }
    }
    return cells;
  }

  // turns the 2D ArrayList cells into an IList<Cell> and sets it to be the
  // board
  void arrayListToIList(ArrayList<ArrayList<Cell>> cells) {
    board = new MtList<Cell>();
    for (int i = 0; i < cells.size(); i++) {
      for (int j = 0; j < cells.get(i).size(); j++) {
        board = new ConsList<Cell>(cells.get(i).get(j), board);
      }
    }
  }

  // determine if it is the end by checking if every cell hass the same color
  boolean endCheck(IList<Cell> cells, String color) {
    end = true;
    for (int i = 0; i < cells.size(); i++) {
      end = cells.get(i).color.equals(color) && end;
    }
    return end;
  }

  // find the cell based on the posn
  Cell findCell(Posn p, IList<Cell> l, int nextCell) {
    if (l.get(nextCell).getPosn().equals(p)) {
      return l.get(nextCell);
    }
    else {
      nextCell = nextCell + 1;
      return findCell(p, l, nextCell);
    }
  }

  // get the original posn of the cell based on the given posn which the mouse
  // clicked on
  Posn getActualPosn(Posn p) {
    return new Posn((p.x / IMAGE_SCALE), (p.y / IMAGE_SCALE));
  }

  // represents the mouse event
  public void onMouseClicked(Posn p) {
    stepsTaken = stepsTaken + 1;
    Posn actualP = this.getActualPosn(p);
    Cell clicked = this.findCell(actualP, board, 0);
    Cell initial = this.board.get(this.board.size() - 1);
    initial.flooded = true;
    initial.turnOnFlooded();
    initial.infect(clicked.color);
    this.worldEnds();
  }

  // if the player press r, reset the game
  public void onKeyEvent(String ke) {
    if (ke.equals("r")) {
      stepsTaken = 0;
      arrayListToIList(initialCells());
    }
  }
}

// examples and tests
class ExamplesFloodIt {
  void testGame(Tester t) {
    FloodItWorld g = new FloodItWorld();
    g.bigBang(FloodItWorld.BOARD_SIZE * 30, FloodItWorld.BOARD_SIZE * 30 + 70, .1);
  }

  void testLists(Tester t) {
    IList<Integer> mt = new MtList<Integer>();
    IList<Integer> l1 = new ConsList<Integer>(5, new ConsList<Integer>(6, mt));
    t.checkExpect(l1.get(0), 5);
    t.checkExpect(l1.add(3), new ConsList<Integer>(3, l1));
    t.checkExpect(l1.size(), 2);
  }

  void testCreateColor(Tester t) {
    FloodItWorld w = new FloodItWorld();
    t.checkExpect(w.createColor(0), "black");
    t.checkExpect(w.createColor(1), "blue");
    t.checkExpect(w.createColor(2), "red");
    t.checkExpect(w.createColor(3), "white");
    t.checkExpect(w.createColor(4), "green");
    t.checkExpect(w.createColor(5), "grey");
  }

  void testCells(Tester t) {
    Cell c1 = new Cell(0, 0, "black");
    Cell c2 = new Cell(1, 0, "blue");
    Cell c3 = new Cell(0, 1, "red");
    Cell c4 = new Cell(1, 1, "white");
    Cell c5 = new Cell(2, 1, "green");
    Cell c6 = new Cell(2, 2, "grey");

    t.checkExpect(c1.getColor(), new Color(0, 0, 0));
    t.checkExpect(c2.getColor(), new Color(0, 0, 255));
    t.checkExpect(c3.getColor(), new Color(255, 0, 0));
    t.checkExpect(c4.getColor(), new Color(255, 255, 255));
    t.checkExpect(c5.getColor(), new Color(0, 255, 255));
    t.checkExpect(c6.getColor(), new Color(190, 190, 190));
    t.checkExpect(c1.getPosn(), new Posn(0, 0));
    t.checkExpect(c2.getPosn(), new Posn(1, 0));
    t.checkExpect(c3.getPosn(), new Posn(0, 1));
    t.checkExpect(c4.getPosn(), new Posn(1, 1));
    t.checkExpect(c5.getPosn(), new Posn(2, 1));
    t.checkExpect(c6.getPosn(), new Posn(2, 2));
  }
}