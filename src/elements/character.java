package elements;

import elements.PacManModel.CellValue;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class character {
    Point2D location;
    Point2D velocity;
    boolean alive;
    CellValue cell;
    Image image;

    public Image getImage(){return image;}
    public CellValue getCell(){return cell;}
    public boolean isAlive(){return alive;}
    public Point2D getLocation(){return location;}
    public Point2D getVelocity(){return velocity;}

    public void setImage(Image _im){this.image = _im;}
    public void setCell(CellValue _cell){this.cell = _cell;}
    public void setAlive(boolean _ali){this.alive = _ali;}
    public void SetLocation(Point2D _loc){ this.location = _loc;}
    public void SetVelocity(Point2D _vel){ this.velocity = _vel;}
}
