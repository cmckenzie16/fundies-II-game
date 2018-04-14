import java.awt.Color;

import javalib.impworld.WorldScene;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;

//CS 2510, Assignment 9
//Assignment 9 - part 2
//McKenzie Camaryn
//mckenzie.cam
//Beckwith Louisa
//beckwith.l

//---------------------------------------------------------------------------------
//Represents a single square of the game area that is the "ocean"

class OceanCell extends Cell {

  OceanCell(int x, int y) {
    super(x, y, 0);
    this.isFlooded = true;
  }

  //draws this as a square on the given scene
  public void drawCell(WorldScene scene, int maxSize, int waterHeight) {
    RectangleImage cellDrawing = new RectangleImage(10, 10, OutlineMode.SOLID, Color.BLUE);
    scene.placeImageXY(cellDrawing, (this.x * 10) + 5, (this.y * 10) + 5);
  }
}