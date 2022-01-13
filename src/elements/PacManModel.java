/**
 * @author Jessie Baskauf and Ellie Mamantov
 * The Model stores information about the game state, including the underlying grid of CellValues (as loaded from the
 * text file), various boolean indicators about game state, level, score, and the movement of PacMan and ghosts.
 */

package elements;

import engine.*;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.fxml.FXML;

import java.io.*;
import java.util.*;
import static java.lang.Math.abs;

public class PacManModel {
    public enum CellValue {
        EMPTY, SMALLDOT, BIGDOT, WALL, BLINKYHOME, PINKYHOME, INKYHOME, CLYDEHOME, PACMANHOME
    };
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    };

    //load the IDs from the FXML file
    @FXML private int rowCount;
    @FXML private int columnCount;

    private CellValue[][] grid; //the grid with the location of everything
    private int score;
    private int level;
    private int lifes;
    private int dotCount;
    private int dotsEaten;
    private int fruit = 0;
    private int ghostsEaten = 0;
    private boolean canGetExtraLife;
    private boolean gameOver;
    private boolean youWon;
    private boolean ghostEatingMode;

    private Direction lastDirection;
    private Direction currentDirection;
    
    public character pacman;
    public character[] ghosts;
    public grafo Grafo;

    /**
     * Start a new game upon initializion
     */
    public PacManModel() {
        this.startNewGame();
    }

    /**
     * Configure the grid CellValues based on the txt file and place PacMan and ghosts at their starting locations.
     * "W" indicates a wall, "E" indicates an empty square, "B" indicates a big dot, "S" indicates
     * a small dot, "1" or "2" indicates the ghosts home, and "P" indicates Pacman's starting position.
     *
     * @param fileName txt file containing the board configuration
     */
    public void initializeLevel(String fileName) {
        File file = new File(fileName);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                lineScanner.next();
                columnCount++;
            }
            rowCount++;
            lineScanner.close();
        }
        columnCount = columnCount/rowCount;
        Scanner scanner2 = null;
        try {
            scanner2 = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        grid = new CellValue[rowCount][columnCount];
        int row = 0;
        while(scanner2.hasNextLine()){
            int column = 0;
            String line= scanner2.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()){
                String value = lineScanner.next();
                CellValue thisValue;
                
                if (value.equals("W")){
                    thisValue = CellValue.WALL;
                }
                else if (value.equals("S")){
                    thisValue = CellValue.SMALLDOT;
                    dotCount++;
                }
                else if (value.equals("B")){
                    thisValue = CellValue.BIGDOT;
                    dotCount++;
                }
                else if (value.equals("0")){
                    thisValue = CellValue.BLINKYHOME;

                    ghosts[0].SetLocation(new Point2D(row, column));
                    ghosts[0].SetVelocity(new Point2D(-1,0));
                    ghosts[0].setCell(thisValue);
                }
                else if (value.equals("1")){
                    thisValue = CellValue.PINKYHOME;

                    ghosts[1].SetLocation(new Point2D(row, column));
                    ghosts[1].SetVelocity(new Point2D(-1,0));
                    ghosts[1].setCell(thisValue);
                }
                else if (value.equals("2")){
                    thisValue = CellValue.INKYHOME;

                    ghosts[2].SetLocation(new Point2D(row, column));
                    ghosts[2].SetVelocity(new Point2D(-1,0));
                    ghosts[2].setCell(thisValue);
                }
                else if (value.equals("3")){
                    thisValue = CellValue.CLYDEHOME;

                    ghosts[3].SetLocation(new Point2D(row, column));
                    ghosts[3].SetVelocity(new Point2D(-1,0));
                    ghosts[3].setCell(thisValue);
                }
                else if (value.equals("P")){
                    thisValue = CellValue.PACMANHOME;
                    pacman.SetLocation(new Point2D(row, column));
                    pacman.SetVelocity(new Point2D(0,0));
                    pacman.setCell(thisValue);
                }
                else //(value.equals("E"))
                {
                    thisValue = CellValue.EMPTY;
                }
                grid[row][column] = thisValue;
                column++;
            }
            row++;
            lineScanner.close();
        }

        Grafo.AdicionaGrafo(this);
        currentDirection = Direction.NONE;
        lastDirection = Direction.NONE;
    }

    /** Initialize values of instance variables and initialize level map
     */
    public void startNewGame() {
        this.Grafo = new grafo();

        this.gameOver = false;
        this.youWon = false;
        this.ghostEatingMode = false;
        this.canGetExtraLife = true;
        this.lifes = 3;
        this.dotCount = 0;
        this.dotsEaten = 0;
        this.fruit = 0;
        this.rowCount = 0;
        this.columnCount = 0;
        this.score = 0;
        this.level = 1;

        pacman = new character();
        ghosts = new character[4];

        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i] = new character();
        }
        this.ghosts[0].setImage(new Image(getClass().getResourceAsStream("/res/gifs/blinky.gif")));
        this.ghosts[1].setImage(new Image(getClass().getResourceAsStream("/res/gifs/pinky.gif")));
        this.ghosts[2].setImage(new Image(getClass().getResourceAsStream("/res/gifs/inky.gif")));
        this.ghosts[3].setImage(new Image(getClass().getResourceAsStream("/res/gifs/clyde.gif")));

        this.initializeLevel(Controller.getLevelFile(0));
    }

    /** Initialize the level map for the next level
     *
     */
    public void startNextLevel() {
        if (this.isLevelComplete()) {
            this.level++;
            rowCount = 0;
            columnCount = 0;
            dotsEaten = 0;
            fruit = 0;
            youWon = false;
            ghostEatingMode = false;

            try {
                this.initializeLevel(Controller.getLevelFile(level - 1));
            }
            catch (ArrayIndexOutOfBoundsException e) {
                //if there are no levels left in the level array, the game ends
                youWon = true;
                gameOver = true;
                level--;
            }
        }
    }

    /**
     * Move PacMan based on the direction indicated by the user (based on keyboard input from the Controller)
     * @param direction the most recently inputted direction for PacMan to move in
     */
    public void movePacman(Direction direction) {
        Point2D potentialPacmanVelocity = changeVelocity(direction);
        Point2D potentialPacmanLocation = pacman.getLocation().add(potentialPacmanVelocity);
        //if PacMan goes offscreen, wrap around
        potentialPacmanLocation = setGoingOffscreenNewLocation(potentialPacmanLocation);
        //determine whether PacMan should change direction or continue in its most recent direction
        //if most recent direction input is the same as previous direction input, check for walls
        if (direction.equals(lastDirection)) {
            //if moving in the same direction would result in hitting a wall, stop moving
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL){
                pacman.SetVelocity(changeVelocity(Direction.NONE));
                setLastDirection(Direction.NONE);
            }
            else {
                pacman.SetVelocity(potentialPacmanVelocity);
                pacman.SetLocation(potentialPacmanLocation);
            }
        }
        //if most recent direction input is not the same as previous input, check for walls and corners before going in a new direction
        else {
            //if PacMan would hit a wall with the new direction input, check to make sure he would not hit a different wall if continuing in his previous direction
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL){
                potentialPacmanVelocity = changeVelocity(lastDirection);
                potentialPacmanLocation = pacman.getLocation().add(potentialPacmanVelocity);
                //if changing direction would hit another wall, stop moving
                if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL){
                    pacman.SetVelocity(changeVelocity(Direction.NONE));
                    setLastDirection(Direction.NONE);
                }
                else {
                    pacman.SetVelocity(changeVelocity(lastDirection));
                    pacman.SetLocation(pacman.getLocation().add(pacman.getVelocity()));
                }
            }
            //otherwise, change direction and keep moving
            else {
                pacman.SetVelocity(potentialPacmanVelocity);
                pacman.SetLocation(potentialPacmanLocation);
                setLastDirection(direction);
            }
        }
    }

    /** Move the ghosts based on the score and the level
     * 
     */
    public void moveGhosts() {
        int blinkyFramesToMovement = (int)(4-(score/2500));
        blinkyFramesToMovement = blinkyFramesToMovement > 4 ? 4 : blinkyFramesToMovement < 1 ? 1 : blinkyFramesToMovement;
        if(Controller.getFrameCount() % blinkyFramesToMovement == 0){ //every 4-(score/2500) blinky moves
            huntPacman(ghosts[0]);
        }

        if(Controller.getFrameCount() % (4-level) == 0){ //every 4-level the ghosts moves
            huntPacman(ghosts[1]);

            randomlyMoveGhost(ghosts[2]);
            randomlyMoveGhost(ghosts[3]);
        }
    }

    /**
     * Método de perseguição utilizado pelo fantasma com base no grafo A*. 
     * Percorre o caminho mais curto que o fantasma pode fazer até chegar na posição do pacman.
     * @param ghost The ghost that is making the persecution
     * @return An array list with the complete path
     */
    public ArrayList<vertice> Astar (character ghost){
        ArrayList<vertice> listaAberta = new ArrayList<vertice>();
        ArrayList<vertice> listaFechada = new ArrayList<vertice>();
        ArrayList<vertice> listaCaminho = new ArrayList<vertice>();

        Grafo.limpaVertice();

        // Procura as coordenadas do fantasma e do pacman na lista de vertices
        vertice Fantasma = Grafo.procuraVertice((int)ghost.getLocation().getX(), (int)ghost.getLocation().getY());
        vertice Pacman = Grafo.procuraVertice((int)pacman.getLocation().getX(), (int)pacman.getLocation().getY());

        // Faz o set do g, f e h da primeira posição (posFantasma)
        Fantasma.setG(0);
        int h = abs (Fantasma.getLinha() - Pacman.getLinha()) + 
                abs (Fantasma.getColuna() - Pacman.getColuna());  
        Fantasma.setF(h + Fantasma.getG());
        
        // Cria um vértice auxiliar e iguala ao vertice do fantasma
        vertice atual = Fantasma;

        // Enquanto a ultima posicao do caminho não for o pacman
        while(atual != Pacman){
            // Cria a lista de filhos
            ArrayList<vertice> listaFilhos = new ArrayList<vertice>();
            // Para cada adjacencia do vértice, insere na lista de filhos do vertice
            for(int i = 0; i<atual.retornaLista().size(); i++){
                listaFilhos.add(atual.retornaLista().get(i).getFim());            
            }
            
            // Enquanto houver filhos
            for (int i = 0; i < listaFilhos.size(); i++){
                vertice v = listaFilhos.get(i);
                
                // Se o vertice não estiver contido na lista aberta e na lista fechada
                if (listaFechada.contains(v)==false && listaAberta.contains(v)==false){
                    // Adiciona o vértice na lista aberta
                    listaAberta.add(v);
                    
                    // Calcula g, f e h
                    int auxG = atual.getG() + 1;
                    v.setG(auxG);
                    int auxH = abs(v.getLinha() - Pacman.getLinha()) + abs(v.getColuna() - Pacman.getColuna());
                    v.setH(auxH);                   
                    int auxF = auxG + auxH;
                    
                    // Se encontrar um F menor, faz o set dele no vertice e faz o set do pai (atual)
                    if (auxF < v.getF()){
                        v.setF(auxF);
                        v.setPai(atual);
                    }
                }
            }
            
            // Termino da utilizacao do vertice -> adiciona na lista fechada
            listaFechada.add(atual);
            
            // Set menor como "infinito"
            int menorF = 10000;
            int posMenorF = 0;
            
            for (int i = 0; i < listaAberta.size(); i++){
                // Se encontrar um f menor
                if (listaAberta.get(i).getF() < menorF){
                    // menorF atualiza para o encontrado da lista
                    menorF = listaAberta.get(i).getF();
                    // guarda a posicao do menorF
                    posMenorF = i;
                }
            }
            
            // Atualiza o atual para o menor caminho
            atual = listaAberta.get(posMenorF);

            // Remove o menor da lista aberta
            listaAberta.remove(posMenorF);
        }

        // Quando sair do laço -> encontrou o pacman -> gera o caminho
        // Enquanto o pacman não for nulo
        while(Pacman != null){
            // Vai adicionando os pais na lista de caminho
            listaCaminho.add(Pacman);
            // Atualiza a posicao
            Pacman = Pacman.getPai();
        }

        // Inverte a ordem da lista
        Collections.reverse(listaCaminho);
        return listaCaminho;
    }
    
    /** Responsible to follow the pacman 
     * 
     * @param ghost the ghost that is following the pacman
     */
    public void huntPacman(character ghost){
        if(!ghost.isAlive()){ //if the ghost is dead do nothing, ghost will be revived once the ghost eating mode ends
            return;
        }

        if (!ghostEatingMode) { //if is not in ghost eating mode, directly follow the pacman
            ArrayList<vertice> listaCaminho = Astar(ghost);
            //as the path is regenerated every move, in order to to arrive at the pacman
            //the ghost should move in the direction of the first vertex
            int linha = listaCaminho.get(1).getLinha() - (int)ghost.getLocation().getX();
            int coluna = listaCaminho.get(1).getColuna() - (int)ghost.getLocation().getY();
            Point2D dir = new Point2D(linha, coluna); //the direction that it should move in order to follow

            ghost.SetVelocity(dir);
            Point2D potentialLocation = ghost.getLocation().add(ghost.getVelocity());
            potentialLocation = setGoingOffscreenNewLocation(potentialLocation);

            if(grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL){
                ghost.SetVelocity(changeVelocity(Direction.NONE));
                potentialLocation = ghost.getLocation().add(ghost.getVelocity());
            }
            ghost.SetLocation(potentialLocation);
        } else{ //if in ghost eatin mode, moves randomly
            Random generator = new Random();

            if (ghost.getLocation().getY() == pacman.getLocation().getY()) { //if is in the same column, run in the oposite direction
                if (ghost.getLocation().getX() > pacman.getLocation().getX()) {
                    ghost.SetVelocity(changeVelocity(Direction.DOWN));
                } else {
                    ghost.SetVelocity(changeVelocity(Direction.UP));
                }
                Point2D potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    ghost.SetVelocity(changeVelocity(direction));
                    potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                }
                ghost.SetLocation(potentialLocation);
            } else if (ghost.getLocation().getX() == pacman.getLocation().getX()) { //if is in the same row, run in the oposite direction
                if (ghost.getLocation().getY() > pacman.getLocation().getY()) {
                    ghost.SetVelocity(changeVelocity(Direction.RIGHT));
                } else {
                    ghost.SetVelocity(changeVelocity(Direction.LEFT));
                }
                Point2D potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    ghost.SetVelocity(changeVelocity(direction));
                    potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                }
                ghost.SetLocation(potentialLocation);
            }else{ //moves to a random direction
                Point2D potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while(grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL){
                    int randomNum = generator.nextInt( 4);
                    Direction direction = intToDirection(randomNum);
                    ghost.SetVelocity(changeVelocity(direction));
                    potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                }
                ghost.SetLocation(potentialLocation);
            }
        }
    }

    /**
     * Move randomly when it hits a wall.
     * @param ghost the ghost moving
     */
    public void randomlyMoveGhost(character ghost){
        if(!ghost.isAlive()){
            return;
        }

        Random generator = new Random();
        if (!ghostEatingMode) {
            //move in a consistent random direction until it hits a wall, then choose a new random direction
            Point2D potentialLocation = ghost.getLocation().add(ghost.getVelocity());
            potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
            while(grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL){
                int randomNum = generator.nextInt( 4);
                Direction direction = intToDirection(randomNum);
                ghost.SetVelocity(changeVelocity(direction));
                potentialLocation = ghost.getLocation().add(ghost.getVelocity());
            }
            ghost.SetLocation(potentialLocation);
            }
        //if the ghost is in the same row or column as Pacman and in ghostEatingMode, go in the opposite direction
        // until it hits a wall, then go a different direction
        //otherwise, go in a random direction, and if it hits a wall go in a different random direction
        else{
            if (ghost.getLocation().getY() == pacman.getLocation().getY()) {
                if (ghost.getLocation().getX() > pacman.getLocation().getX()) {
                    ghost.SetVelocity(changeVelocity(Direction.DOWN));
                } else {
                    ghost.SetVelocity(changeVelocity(Direction.UP));
                }
                Point2D potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    ghost.SetVelocity(changeVelocity(direction));
                    potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                }
                ghost.SetLocation(potentialLocation);
            } else if (ghost.getLocation().getX() == pacman.getLocation().getX()) {
                if (ghost.getLocation().getY() > pacman.getLocation().getY()) {
                    ghost.SetVelocity(changeVelocity(Direction.RIGHT));
                } else {
                    ghost.SetVelocity(changeVelocity(Direction.LEFT));
                }
                Point2D potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    ghost.SetVelocity(changeVelocity(direction));
                    potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                }
                ghost.SetLocation(potentialLocation);
            }
            else{
                Point2D potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while(grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL){
                    int randomNum = generator.nextInt( 4);
                    Direction direction = intToDirection(randomNum);
                    ghost.SetVelocity(changeVelocity(direction));
                    potentialLocation = ghost.getLocation().add(ghost.getVelocity());
                }
                ghost.SetLocation(potentialLocation);
            }
        }
    }

    /**
     * Wrap around the gameboard if the object's location would be off screen
     * @param objectLocation the specified object's location
     * @return Point2D new wrapped-around location
     */
    public Point2D setGoingOffscreenNewLocation(Point2D objectLocation) {
        //if object goes offscreen on the right
        if (objectLocation.getY() >= columnCount) {
            objectLocation = new Point2D(objectLocation.getX(), 0);
        }
        //if object goes offscreen on the left
        if (objectLocation.getY() < 0) {
            objectLocation = new Point2D(objectLocation.getX(), columnCount - 1);
        }
        return objectLocation;
    }

    /**
     * Connects each Direction to an integer 0-3
     * @param x an integer
     * @return the corresponding Direction
     */
    public Direction intToDirection(int x){
        if (x == 0){
            return Direction.LEFT;
        }
        else if (x == 1){
            return Direction.RIGHT;
        }
        else if(x == 2){
            return Direction.UP;
        }
        else{
            return Direction.DOWN;
        }
    }

    /**
     * Send a character to its home location
     * @param charact the character that will be moved
     */
    public void sendCharacterHome(character charact) {
        charact.setAlive(false);
        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                if (grid[row][column] == charact.getCell()) {
                    charact.SetLocation(new Point2D(row, column));
                }
            }
        }
       charact.SetVelocity(new Point2D(-1, 0));
    }

    /**
     * Sends all the ghosts and the pacman to its home location
     */
    public void sendEverybodyHome(){
        for (character ghost : ghosts) {
            sendCharacterHome(ghost);
        }
        sendCharacterHome(pacman);
    }

    /**
     * checks the conditions for the extra life, and give it to the player
     */
    public void extraLife(){
        if(canGetExtraLife){
            if(score >= 10000){
                lifes++;
                canGetExtraLife = false;
            }
        }
    }

    /**
     * Spawns the fruits at the right moments.
     */
    public void fruitSpawner(){
        if(dotsEaten == 70 || dotsEaten == 170){
            for (int row = 0; row < this.rowCount; row++) {
                for (int column = 0; column < this.columnCount; column++) {
                    if (grid[row][column] == CellValue.PACMANHOME) {
                        fruit = level;
                    }
                }
            }
        }
    }

    /**
     * Updates the model to reflect the movement of PacMan and the ghosts and the change in state of any objects eaten
     * during the course of these movements. Switches game state to or from ghost-eating mode.
     * @param direction the most recently inputted direction for PacMan to move in
     */
    public void step(Direction direction) {
        extraLife();
        fruitSpawner();

        this.movePacman(direction);
        CellValue pacmanLocationCellValue = grid[(int) pacman.getLocation().getX()][(int) pacman.getLocation().getY()];
        
        //if pacman is at home, he can collect fruit
        if(pacmanLocationCellValue == CellValue.PACMANHOME){
            if(fruit != 0){
                score += 200*(fruit) - 100; //if fruit 1, score 100, if fruit 2, score 300...
                fruit = 0;
            }
        }
        //if PacMan is on a small dot, delete small dot
        if (pacmanLocationCellValue == CellValue.SMALLDOT) {
            grid[(int) pacman.getLocation().getX()][(int) pacman.getLocation().getY()] = CellValue.EMPTY;
            dotCount--;
            dotsEaten++;
            score += 10;
        }
        //if PacMan is on a big dot, delete big dot and change game state to ghost-eating mode and initialize the counter
        if (pacmanLocationCellValue == CellValue.BIGDOT) {
            grid[(int) pacman.getLocation().getX()][(int) pacman.getLocation().getY()] = CellValue.EMPTY;
            dotCount--;
            dotsEaten++;
            score += 50;
            ghostEatingMode = true;
            Controller.setGhostEatingModeCounter(this.level);
        }
        
        for (character ghost : ghosts) {
            if (ghostEatingMode) {
                if (pacman.getLocation().equals(ghost.getLocation())) {
                    sendCharacterHome(ghost);
                    score += 100 * 2^ghostsEaten;
                    ghostsEaten++;
                }
            }else { //loses life if PacMan is eaten by a ghost
                ghostsEaten = 0; //always reset the ghostEaten counter when not in ghost eating mode
                ghost.setAlive(true);
                
                if (pacman.getLocation().equals(ghost.getLocation())) {
                    lifes--;
                    if(lifes<=0){
                        gameOver = true;
                    }
                    sendEverybodyHome();
                    pacman.SetVelocity(new Point2D(0, 0));
                }
            }
        }
        this.moveGhosts();


        if (this.isLevelComplete()) {
            pacman.SetVelocity(new Point2D(0, 0));
            startNextLevel();
        }
    }

    /**
     * Connects each direction to Point2D velocity vectors (Left = (-1,0), Right = (1,0), Up = (0,-1), Down = (0,1))
     * @param direction
     * @return Point2D velocity vector
     */
    public Point2D changeVelocity(Direction direction){
        if(direction == Direction.LEFT){
            return new Point2D(0,-1);
        }
        else if(direction == Direction.RIGHT){
            return new Point2D(0,1);
        }
        else if(direction == Direction.UP){
            return new Point2D(-1,0);
        }
        else if(direction == Direction.DOWN){
            return new Point2D(1,0);
        }
        else{
            return new Point2D(0,0);
        }
    }


    //#region getters
    public character[] getGhosts(){return ghosts;}

    public boolean isGhostEatingMode() {return ghostEatingMode;}

    public boolean isYouWon(){return youWon;}
    
    /**
     * When all dots are eaten, level is complete
     * @return boolean
     */
    public boolean isLevelComplete(){return this.dotCount == 0;}
    
    public boolean isGameOver(){return gameOver;}
    
    public CellValue[][] getGrid(){return grid;}
    
    /**
     * @param row
     * @param column
     * @return the Cell Value of cell (row, column)
     */
    public CellValue getCellValue(int row, int column){
        assert row >= 0 && row < this.grid.length && column >= 0 && column < this.grid[0].length;
        return this.grid[row][column];
    }
    
    public int GetFruit(){return fruit;}
    
    public Direction getCurrentDirection(){return currentDirection;}
    
    public Direction getLastDirection(){return lastDirection;}
    
    public int getScore(){return score;}
    
    public int getLevel(){return level;}
    
    public int getLifes(){return lifes;}
    
    /**
     * @return the total number of dots left (big and small)
     */
    public int getDotCount(){return dotCount;}
    
    public int getRowCount(){return rowCount;}
    
    public int getColumnCount(){return columnCount;}
    
    //#endregion
    
    //#region Setters
    public void setGhostEatingMode(boolean ghostEatingModeBool) {
        ghostEatingMode = ghostEatingModeBool;
    }
    public void setCurrentDirection(Direction direction) {
        currentDirection = direction;
    }
    public void setLastDirection(Direction direction) {
        lastDirection = direction;
    }
    public void setScore(int score) {
        this.score = score;
    }
    /** Add new points to the score
     *
     * @param points
     */
    public void addToScore(int points) {
        this.score += points;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setLifes(int _lifes) {
        this.lifes = _lifes;
    }
    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
    //#endregion
}
