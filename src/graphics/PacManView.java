package graphics;

import elements.*;
import elements.PacManModel.CellValue;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PacManView extends Group {
    public final static double CELL_WIDTH = 20.0;

    //load the IDs from the FXML file
    @FXML private int rowCount;
    @FXML private int columnCount;

    private ImageView[][] cellViews;
    private Image pacmanRightImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image blueGhostImage;
    private Image blueWallImage;
    private Image greenWallImage;
    private Image redWallImage;
    private Image bigDotImage;
    private Image smallDotImage;
    private Image cerejaImage;
    private Image morangoImage;
    private Image laranjaImage;

    /**
     * Initializes the values of the image instance variables from files
     */
    public PacManView() {
        this.pacmanRightImage = new Image(getClass().getResourceAsStream("/res/gifs/pacmanRight.gif"));
        this.pacmanUpImage = new Image(getClass().getResourceAsStream("/res/gifs/pacmanUp.gif"));
        this.pacmanDownImage = new Image(getClass().getResourceAsStream("/res/gifs/pacmanDown.gif"));
        this.pacmanLeftImage = new Image(getClass().getResourceAsStream("/res/gifs/pacmanLeft.gif"));
        this.blueGhostImage = new Image(getClass().getResourceAsStream("/res/gifs/blueghost.gif"));
        this.blueWallImage = new Image(getClass().getResourceAsStream("/res/images/blueWall.png"));
        this.greenWallImage = new Image(getClass().getResourceAsStream("/res/images/greenWall.png"));
        this.redWallImage = new Image(getClass().getResourceAsStream("/res/images/redWall.png"));
        this.bigDotImage = new Image(getClass().getResourceAsStream("/res/images/whitedot.png"));
        this.smallDotImage = new Image(getClass().getResourceAsStream("/res/images/smalldot.png"));
        this.cerejaImage = new Image(getClass().getResourceAsStream("/res/images/cereja.png"));
        this.morangoImage = new Image(getClass().getResourceAsStream("/res/images/morango.png"));
        this.laranjaImage = new Image(getClass().getResourceAsStream("/res/images/laranja.png"));
    }

    /**
     * Constructs an empty grid of ImageViews
     */
    private void initializeGrid() {
        if (this.rowCount > 0 && this.columnCount > 0) {
            this.cellViews = new ImageView[this.rowCount][this.columnCount];
            for (int row = 0; row < this.rowCount; row++) {
                for (int column = 0; column < this.columnCount; column++) {
                    ImageView imageView = new ImageView();
                    imageView.setX((double)column * CELL_WIDTH);
                    imageView.setY((double)row * CELL_WIDTH);
                    imageView.setFitWidth(CELL_WIDTH);
                    imageView.setFitHeight(CELL_WIDTH);
                    this.cellViews[row][column] = imageView;
                    this.getChildren().add(imageView);
                }
            }
        }
    }

    /** Updates the view to reflect the state of the model
     *
     * @param model the PacManModel that will be viewed
     */
    public void updateView(PacManModel model) {
        assert model.getRowCount() == this.rowCount && model.getColumnCount() == this.columnCount;
        //for each ImageView, set the image to correspond with the CellValue of that cell
        for (int row = 0; row < this.rowCount; row++){
            for (int column = 0; column < this.columnCount; column++){
                CellValue value = model.getCellValue(row, column);
                if (value == CellValue.WALL) {
                    switch (model.getLevel()) {
                        case 1:
                            this.cellViews[row][column].setImage(this.blueWallImage);
                            break;
                        case 2:
                            this.cellViews[row][column].setImage(this.greenWallImage);
                            
                            break;
                        case 3:
                            this.cellViews[row][column].setImage(this.redWallImage);
                            break;
                    }
                }
                else if (value == CellValue.BIGDOT) {
                    this.cellViews[row][column].setImage(this.bigDotImage);
                }
                else if (value == CellValue.SMALLDOT) {
                    this.cellViews[row][column].setImage(this.smallDotImage);
                }
                else if(value == CellValue.PACMANHOME){
                    if(model.GetFruit() == 1){
                        this.cellViews[row][column].setImage(this.cerejaImage);
                    }else if (model.GetFruit() == 2){
                        this.cellViews[row][column].setImage(this.morangoImage);
                    }else if (model.GetFruit() == 3){
                        this.cellViews[row][column].setImage(this.laranjaImage);
                    }else{
                        this.cellViews[row][column].setImage(null);
                    }
                }
                else {
                    this.cellViews[row][column].setImage(null);
                }
                //check which direction PacMan is going in and display the corresponding image
                if (row == model.pacman.getLocation().getX() && column == model.pacman.getLocation().getY() && (model.getLastDirection() == PacManModel.Direction.RIGHT || model.getLastDirection() == PacManModel.Direction.NONE)) {
                    this.cellViews[row][column].setImage(this.pacmanRightImage);
                }
                else if (row == model.pacman.getLocation().getX() && column == model.pacman.getLocation().getY() && model.getLastDirection() == PacManModel.Direction.LEFT) {
                    this.cellViews[row][column].setImage(this.pacmanLeftImage);
                }
                else if (row == model.pacman.getLocation().getX() && column == model.pacman.getLocation().getY() && model.getLastDirection() == PacManModel.Direction.UP) {
                    this.cellViews[row][column].setImage(this.pacmanUpImage);
                }
                else if (row == model.pacman.getLocation().getX() && column == model.pacman.getLocation().getY() && model.getLastDirection() == PacManModel.Direction.DOWN) {
                    this.cellViews[row][column].setImage(this.pacmanDownImage);
                }
                //make ghosts "blink" towards the end of ghostEatingMode (display regular ghost images on alternating updates of the counter)
                if (model.isGhostEatingMode() && (Controller.getGhostEatingModeCounter() == 6 ||Controller.getGhostEatingModeCounter() == 4 || Controller.getGhostEatingModeCounter() == 2)) {
                    for (character ghost : model.getGhosts()) { //para cada fantasma 
                        if (row == ghost.getLocation().getX() && column == ghost.getLocation().getY()) {
                            this.cellViews[row][column].setImage(ghost.getImage());
                        }
                    }
                }
                //display blue ghosts in ghostEatingMode
                else if (model.isGhostEatingMode()) {
                    for (character ghost : model.getGhosts()) {
                        if (row == ghost.getLocation().getX() && column == ghost.getLocation().getY()) {
                            this.cellViews[row][column].setImage(this.blueGhostImage);
                        }
                    }
                }
                //dispaly regular ghost images otherwise
                else {
                    for (character ghost : model.getGhosts()) {
                        if (row == ghost.getLocation().getX() && column == ghost.getLocation().getY()) {
                            this.cellViews[row][column].setImage(ghost.getImage());
                        }
                    }
                }
            }
        }
    }

    //#region Getters
    public int getRowCount() {return this.rowCount;}
    
    public int getColumnCount() {return this.columnCount;}
    //#endregion

    //#region Setters
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        this.initializeGrid();
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        this.initializeGrid();
    }
    //#endregion
}
