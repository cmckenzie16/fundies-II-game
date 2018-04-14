import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;

//CS 2510, Assignment 9
//Assignment 9 - part 2
//McKenzie Camaryn
//mckenzie.cam
//Beckwith Louisa
//beckwith.l

//---------------------------------------------------------------------------------
// examples for ForbiddenWorld game

class ExamplesForbiddenWorld {

  //examples

  Target target1 = new Target(5, 5);
  Target target2 = new Target(10, 10);
  Target target3 = new Target(15, 5);

  Player player1 = new Player(5, 5, 10, Color.pink, new Cell(0, 0, 0));

  ArrayList<Target> targetList1 = new ArrayList<Target>(Arrays.asList(target1, target2, target3));
  ArrayList<Target> targetList2 = new ArrayList<Target>(Arrays.asList(target2, target3));



  ForbiddenIslandWorld world1;
  ForbiddenIslandWorld world2;
  ForbiddenIslandWorld world3;

  void initData() {
    world1 = new ForbiddenIslandWorld("mountain");
    world2 = new ForbiddenIslandWorld("random");
    world3 = new ForbiddenIslandWorld("randomterrain");
  }

  // for testing 
  //integer lists
  IList<Integer> empty = new MtList<Integer>(); 
  IList<Integer> list1 = new ConsList<Integer>(1, 
      new ConsList<Integer>(2, 
          new ConsList<Integer>(3, empty)));
  ConsList<Integer> consList1 = new ConsList<Integer>(1, 
      new ConsList<Integer>(2, 
          new ConsList<Integer>(3, empty)));

  // cells
  Cell cell1 = new Cell(1, 1, 2.0);
  Cell cell2 = new Cell(2, 3, 2.0);
  Cell cell1Copy = new Cell(1, 1, 2.0);
  Cell cell3 = new Cell(1, 5, 2.0);
  Cell cell1Same = cell1;

  // cell lists
  IList<Cell> emptyCells = new MtList<Cell>(); 
  IList<Cell> list2 = new ConsList<Cell>(cell1, 
      new ConsList<Cell>(cell2, 
          new ConsList<Cell>(cell2, 
              new ConsList<Cell>(cell1Same, emptyCells))));
  ConsList<Cell> consList2 = new ConsList<Cell>(cell1, 
      new ConsList<Cell>(cell2, 
          new ConsList<Cell>(cell2, 
              new ConsList<Cell>(cell1Same, emptyCells))));
  IList<Cell> list3 = new ConsList<Cell>(cell3, 
      new ConsList<Cell>(cell2, 
          new ConsList<Cell>(cell1Same, 
              new ConsList<Cell>(cell1Same, emptyCells))));
  IList<Cell> list4 = new ConsList<Cell>(cell3, 
      new ConsList<Cell>(cell2, 
          new ConsList<Cell>(cell1Copy, emptyCells)));

  // iterator
  IListIterator<Cell> iter1 = new IListIterator<Cell>(this.emptyCells);
  IListIterator<Cell> iter2 = new IListIterator<Cell>(this.list2);


  //tests

  // test for size 
  boolean testSize(Tester t) {

    return t.checkExpect(this.empty.size(), 0)
        && t.checkExpect(this.list1.size(), 3)
        && t.checkExpect(this.emptyCells.size(), 0)
        && t.checkExpect(this.list2.size(), 4)
        && t.checkExpect(this.list3.size(), 4)
        && t.checkExpect(this.list4.size(), 3);
  }

  // test for isCons 
  boolean testIsCons(Tester t) {

    return t.checkExpect(this.empty.isCons(), false)
        && t.checkExpect(this.list1.isCons(), true)
        && t.checkExpect(this.emptyCells.isCons(), false)
        && t.checkExpect(this.list2.isCons(), true);
  }

  // test for asCons 
  boolean testAsCons(Tester t) {
    Exception error = new UnsupportedOperationException("Don't do this!");

    return t.checkException(error, this.empty, "asCons")
        && t.checkExpect(this.list1.asCons(), this.consList1)
        && t.checkException(error, this.emptyCells, "asCons")
        && t.checkExpect(this.list2.asCons(), this.consList2);
  }

  // test for hasNext 
  boolean testHasNext(Tester t) {

    return t.checkExpect(this.iter1.hasNext(), false)
        && t.checkExpect(this.iter2.hasNext(), true);
  }


  // test for next 
  boolean testNext(Tester t) {
    Exception error = new UnsupportedOperationException("Don't do this!");

    return t.checkException(error, this.iter1, "next")
        && t.checkExpect(this.iter2.next(), this.cell1);
  }

  // test for remove 
  boolean testRemove(Tester t) {
    Exception error = new UnsupportedOperationException("Don't do this!");

    return t.checkException(error, this.iter1, "remove")
        && t.checkException(error, this.iter2, "remove");
  }

  // test for iterator 
  boolean testIterator(Tester t) {

    return t.checkExpect(this.emptyCells.iterator(), this.iter1)
        && t.checkExpect(this.list3.iterator(), new IListIterator<Cell>(this.list3));
  }

  //test makeMountainIsland
  void testMakeMountainIsland(Tester t) {
    initData();

    ArrayList<ArrayList<Double>> heights = world1.makeMountainIsland();

    t.checkExpect(heights.size(), ForbiddenIslandWorld.ISLAND_SIZE);
    t.checkExpect(heights.get(0).size(), ForbiddenIslandWorld.ISLAND_SIZE);
    t.checkExpect(heights.get(0).get(0) < 0, true);

  }

  //test makeRandomMoutain
  void testMakeRandomMountain(Tester t) {
    initData();

    ArrayList<ArrayList<Double>> heights = world1.makeRandomMountain();
    int center = ForbiddenIslandWorld.ISLAND_SIZE / 2;
    int size = ForbiddenIslandWorld.ISLAND_SIZE;

    t.checkExpect(heights.size(), ForbiddenIslandWorld.ISLAND_SIZE);
    t.checkExpect(heights.get(0).size(), ForbiddenIslandWorld.ISLAND_SIZE);
    t.checkExpect(heights.get(0).get(0) < 0, true);
    t.checkNumRange(heights.get(2).get(2), size * -1, size);
    t.checkNumRange(heights.get(0).get(0), size * -1, size);
    t.checkNumRange(heights.get(center).get(center), size * -1, size);

  }

  //test convertToCell
  void testConvertToCell(Tester t) {
    initData();

    ArrayList<ArrayList<Double>> heights = world1.makeMountainIsland();
    ArrayList<ArrayList<Double>> heights2 = world2.makeMountainIsland();
    ArrayList<ArrayList<Cell>> cells = world1.convertToCells(heights);
    ArrayList<ArrayList<Cell>> cells2 = world2.convertToCells(heights2);


    t.checkExpect(cells.size(), ForbiddenIslandWorld.ISLAND_SIZE);
    t.checkExpect(cells2.size(), ForbiddenIslandWorld.ISLAND_SIZE);
    t.checkExpect(cells.get(0).size(), ForbiddenIslandWorld.ISLAND_SIZE);
    t.checkExpect(cells2.get(0).size(), ForbiddenIslandWorld.ISLAND_SIZE);
    t.checkExpect(cells.get(0).get(0).height, 0.0);
    t.checkNumRange(cells2.get(0).get(0).height, 0.0, ForbiddenIslandWorld.ISLAND_SIZE);
    t.checkExpect(cells.get(0).get(0).x, 0);
    t.checkExpect(cells2.get(0).get(0).x, 0);
    t.checkExpect(cells.get(0).get(0).y, 0);
    t.checkExpect(cells2.get(0).get(0).y, 0);
  }

  //test convertToIlist
  void testConvertToIList(Tester t) {
    initData();

    ArrayList<ArrayList<Double>> heights = world1.makeMountainIsland();
    ArrayList<ArrayList<Double>> heights2 = world2.makeRandomMountain();
    ArrayList<ArrayList<Double>> empty = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Cell>> cells = world1.convertToCells(heights);
    ArrayList<ArrayList<Cell>> cells2 = world2.convertToCells(heights2);
    //    ArrayList<ArrayList<Cell>> cellsEmpty = world1.convertToCells(empty);
    //    IList<Cell> ilists = world1.convertToIList(cells);
    //    IList<Cell> ilists2 = world2.convertToIList(cells2);
    //    IList<Cell> ilistsEmpty = world1.convertToIList(cellsEmpty);
  }

  //test pickUpPieces
  void testPickUpPieces(Tester t) {

    initData();

    t.checkExpect(target1.samePlayerLoc(player1), true);

    world1.pickUpPieces();

  }



  //test draw
  void testBigBang(Tester t) {
    initData();

    int drawSize = ForbiddenIslandWorld.ISLAND_SIZE * 10;

    this.world1.bigBang(drawSize, drawSize, .1);
    //this.world2.bigBang(drawSize, drawSize, .2);
    //this.world3.bigBang(drawSize, drawSize, .1);
  }
}