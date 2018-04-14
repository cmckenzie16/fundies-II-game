import java.awt.Color;

import javalib.impworld.WorldScene;
import javalib.worldimages.*;

//CS 2510, Assignment 9
//Assignment 9 - part 2
//McKenzie Camaryn
//mckenzie.cam
//Beckwith Louisa
//beckwith.l

//---------------------------------------------------------------------------------
//Represents a single square of the game area

class Cell {
  // represents absolute height of this cell, in feet
  double height;
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;
  // reports whether this cell is flooded or not
  boolean isFlooded;

  Cell(int x, int y, double height) {
    this.x = x;
    this.y = y;
    this.height = height;
    this.isFlooded = false;
    this.left = this; 
    this.right = this; 
    this.top = this;
    this.bottom = this;
  }

  Cell(int x, int y, double height, boolean isFlooded) {
    this(x, y, height);
    this.isFlooded = isFlooded;
  }

  //draws this as a square on the given scene
  public void drawCell(WorldScene scene, int maxSize, int waterHeight) {
    Color color;

    if (this.isFlooded) {
      int changeColor = (int) ((255 / maxSize) * Math.abs(waterHeight - this.height));

      if (changeColor > 255) {
        changeColor = 255;
      }

      int blue = 255 - changeColor;

      color = new Color(0, 0, blue);
    }
    else if (this.height < waterHeight) {
      int changeColor = (int) ((255 / maxSize) * Math.abs(waterHeight - this.height));

      if (changeColor > 255) {
        changeColor = 255;
      }
      int red = 0 + changeColor;
      int green = 255 - changeColor;  

      color = new Color(red, green, 0);
    }
    else {
      int red = (int) ((255 / maxSize) * this.height);
      int blue = (int) ((255 / maxSize) * this.height);

      color = new Color(red, 255, blue);

    }
    RectangleImage cellDrawing = new RectangleImage(10, 10, OutlineMode.SOLID, color);
    scene.placeImageXY(cellDrawing, (this.x * 10) + 5, (this.y * 10) + 5);
  }

  // floods the cell
  public void floodCell(int waterHeight) {
    if (this.height <= waterHeight
        && (this.right.isFlooded
            || this.left.isFlooded
            || this.bottom.isFlooded
            || this.top.isFlooded)) {

      this.isFlooded = true;
    }
  }
}