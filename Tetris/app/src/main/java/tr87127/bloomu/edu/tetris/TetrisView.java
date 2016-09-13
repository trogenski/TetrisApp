package tr87127.bloomu.edu.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;


/**
 * Custom view where the game takes place.
 * Created by Thomas Rogenski
 */
public class TetrisView extends View {

    //Initializing the GRID
    private final int GRID_HEIGTH = 22;
    private final int GRID_WIDTH = 10;
    //Random seed to get a new piece
    private final int RANDOM_SEED = 100_000_000;
    //Current row is decreased by 1 every 700ms
    private final int INIT_GAME_SPEED = 600;


    //Paint objects to keep track of color and strokes
    private Paint mSquarePaint;
    private Paint mGridPaint;

    private Random rand;

    //Variables used to make each square of each piece
    private float mSquareHeight;
    private float mSquareWidth;
    private int mWidth;
    private int mHeight;
    private int mX;
    private int mY;

    // variables for accelerometer
    private SensorManager mSensorManager;
    private boolean enableAccelerometer;
    private final SensorEventListener mSensorListener;

    // Variables used to calculate deltaX for when the user swipes the screen left or right
    private MotionEvent.PointerCoords mDownPos;
    private MotionEvent.PointerCoords mMovePos;

    // Makes sure that the user doesn't move his finger while pressing / releases too soon
    private boolean isLongPress;

    //Variables used to keep track and draw pieces
    private TetrisPieces currentPiece;
    private TetrisPieces nextPiece;
    private TetrisPieces[] tetrisPieces; // Array of all of the possible pieces.
    private TetrisPieces[][] grid; // Grid used to keep track of pieces that dropped
    private int[][][] squares; // 1 means draw square, 0 means skip a square.
    private int rotation;

    //Variables to keep track of how the game plays
    private int score;
    private int level;
    private int totalLines;
    private int clearedLines;
    private int gameSpeed;
    private int chaos;
    private boolean gameOver;
    private boolean paused;


    public TetrisView(Context context) {
        super(context);
        gameOver = false;
        paused = false;
        enableAccelerometer = false; // do not enable accelerometer until user says so

        chaos = 0;
        score = 0;
        totalLines = 0;
        clearedLines = 0;
        level = 1;
        gameSpeed = INIT_GAME_SPEED;
        rotation = 0;

        rand = new Random();

        mDownPos = new MotionEvent.PointerCoords();
        mMovePos = new MotionEvent.PointerCoords();

        mGridPaint = new Paint();
        mGridPaint.setColor(Color.WHITE);
        mGridPaint.setStyle(Paint.Style.STROKE);
        grid = new TetrisPieces[GRID_HEIGTH][GRID_WIDTH];
        mSquarePaint = new Paint();
        mGridPaint.setStrokeWidth(1);
        mSquarePaint.setStrokeWidth(2);

        tetrisPieces = TetrisPieces.values();
        currentPiece = tetrisPieces[rand.nextInt(RANDOM_SEED) % 7];
        nextPiece = tetrisPieces[rand.nextInt(RANDOM_SEED) % 7];
        squares = new int[rotation][currentPiece.getRows()][currentPiece.getCols()];
        squares = currentPiece.getSquares();


        // if the screen presses down on the screen, it will always long unless the user
        // moves his finger or lets go too soon
        isLongPress = true;
        setLongClickable(true);
        /**
         * Implementing OnLongCLickListener to listen for long clicks. If the user holds on the
         * view for an extended period of time, the piece will drop down until it has reached
         * the bottom.
         */
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isLongPress) {
                    while (isValid(currentPiece, mX, mY + 1, rotation)) {
                        move(0, 1);
                    }
                    score+=20;
                }
                return true;
            }
        });

        /**
         * Accelerometer implemented to move the piece left or right.
         * This was implemented with the help of the FingerPaint app
         * that was made in class.
         */

        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                int x = (int) event.values[0]; // acceleration around x-axis
                move(-(x / 2), 0);
                invalidate();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

        };
    }


    private void enableAccelerometerListener() {
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        if (enableAccelerometer) {
            mSensorManager.registerListener(mSensorListener,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mSensorManager.unregisterListener(mSensorListener);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mSquareWidth = w / GRID_WIDTH; // 10 squares in a column
        mSquareHeight = h / (GRID_HEIGTH - 2); // 20 squares in a row (plus two hidden ones up top)
        //Initializing the variables for the first square;
        mX = GRID_WIDTH / 2;
        mY = currentPiece.findFirstTopSquare(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draw the outline of the grid behind the pieces.
        drawGrid(canvas);

        //Draw the pieces
        for (int i = 0; i < currentPiece.getRows(); i++) {
            for (int j = 0; j < currentPiece.getCols(); j++) {
                if (mY + i >= 2 && squares[rotation][i][j] == 1) {
                    makeSquare(((mX + j) * mSquareWidth), ((mY + i - 2) * mSquareHeight),
                            currentPiece, canvas);
                }
            }
        }

        //Draw the pieces that were added to the grid after dropping
        for (int i = 2; i < GRID_HEIGTH; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                if (grid[i][j] != null) {
                    makeSquare((j * mSquareWidth), ((i - 2) * mSquareHeight), grid[i][j], canvas);
                }
            }
        }

    }

    /**
     * Moves each piece depending on the x and y parameters
     *
     * @param x column displacement
     * @param y row displacement
     */
    private void move(int x, int y) {

        if (!paused) {
            if (isValid(currentPiece, mX, mY + y, rotation)) {
                mY += y;
            }
            if (isValid(currentPiece, mX + x, mY, rotation)) {
                mX += x;
            }
        }

    }

    /**
     * Makes a square at row y and column x. Piece is used to get the color.
     *
     * @param x      column
     * @param y      row
     * @param piece  piece being drawn
     * @param canvas
     */
    private void makeSquare(float x, float y, TetrisPieces piece, Canvas canvas) {
        mSquarePaint.setColor(piece.getColor());
        Color.alpha(piece.getColor());
        mSquarePaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + mSquareWidth, y + mSquareHeight, mSquarePaint);
        mSquarePaint.setColor(Color.BLACK);
        mSquarePaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(x, y, x + mSquareWidth, y + mSquareHeight, mSquarePaint);
    }

    /**
     * Draws the outline of the grid behind the pieces
     *
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {
        for (int i = 0; i < GRID_HEIGTH - 2; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                canvas.drawRect(0 + (j * mSquareWidth), 0 + (i * mSquareHeight),
                        mSquareWidth + (j * mSquareWidth),
                        mSquareHeight + (i * mSquareHeight), mGridPaint);
            }
        }
    }


    /**
     * Used to rotate the falling piece. User can do a quick tap on the screen to rotate the piece.
     * Cycles through all 4 rotations of a piece. Rotates clockwise.
     */
    private void rotatePiece() {
        int futureRotation;
        int tempX = mX;
        int tempY = mY;
        if (rotation != 3) {
            futureRotation = rotation + 1;
        } else {
            futureRotation = 0;
        }
        int left = currentPiece.findFirstLeftSquare(futureRotation);
        int right = currentPiece.findFirstRightSquare(futureRotation);
        int top = currentPiece.findFirstTopSquare(futureRotation);
        int bottom = currentPiece.findFirstBottomSquare(futureRotation);

        //Sometimes the piece might go offscreen or might overlap another piece if
        //rotated too close to the edge of the screen or too close to another piece.
        //Checks left / right;
        if (mX < -left) {
            tempX -= mX - left;
        } else if (mX + currentPiece.getCols() - right >= GRID_WIDTH) {
            tempX -= (mX + currentPiece.getCols() - right) - GRID_WIDTH + 1;
        }
        //Checks top / bottom.
        if (mY < -top) {
            tempY -= mY - top;
        } else if (mY + currentPiece.getRows() - bottom >= GRID_HEIGTH) {
            tempY -= (mY + currentPiece.getRows() - bottom) - GRID_HEIGTH + 1;
        }

        if (isValid(currentPiece, tempX, tempY, futureRotation)) {
            rotation = futureRotation;
            mX = tempX;
            mY = tempY;
        }

    }

    /**
     * Adds piece to the board for every square that the piece took up. Used to keep track of
     * what color square to draw at (y,x)
     *
     * @param piece piece to be added
     * @param y     rows
     * @param x     columns
     */
    private void addPiece(TetrisPieces piece, int y, int x) {
        for (int i = 0; i < piece.getRows(); i++) {
            for (int j = 0; j < piece.getCols(); j++) {
                if (squares[rotation][i][j] == 1) {
                    grid[y + i][x + j] = piece;
                }
            }
        }

    }

    /**
     * Checks if a piece can be placed at a future position (y+1,x) or (y,x+1)
     *
     * @param piece    piece being tested
     * @param x        columns
     * @param y        rows
     * @param rotation current piece rotation
     * @return true if the the next position is valid
     */
    private boolean isValid(TetrisPieces piece, int x, int y, int rotation) {
        //0's to the left and right of a piece have to be dismissed so that the
        //positions can be calculated correctly.

        //Makes sure that the piece does not go offscreen
        if (x < -piece.findFirstLeftSquare(rotation)
                || x + piece.getCols() - piece.findFirstRightSquare(rotation) >= GRID_WIDTH) {
            return false;
        }
        if (y < -piece.findFirstTopSquare(rotation)
                || y + piece.getRows() - piece.findFirstBottomSquare(rotation) >= GRID_HEIGTH) {
            return false;
        }

        //Iterates through the grid to see if the next position is being occupied by another
        //piece already.
        for (int i = 0; i < piece.getRows(); i++) {
            for (int j = 0; j < piece.getCols(); j++) {
                if (squares[rotation][i][j] == 1 && grid[y + i][x + j] != null) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Iterates though the board to check if there are any completed lines.
     * If so, remove all completed lines, increment totalLines removed,
     * and clearedLines (consecutive lines). Checks if the level of the game should be updated.
     */
    private void completedLines() {
        for (int i = 0; i < GRID_HEIGTH; i++) {
            boolean lineComplete = true;
            for (int j = 0; j < GRID_WIDTH; j++) {
                if (grid[i][j] == null) {
                    lineComplete = false;
                }
            }
            if (lineComplete) {
                for (int j = i - 1; j >= 0; j--) {
                    for (int k = 0; k < GRID_WIDTH; k++) {
                        grid[j + 1][k] = grid[j][k];
                    }
                }
                totalLines++;
                clearedLines++;
                updateGameLevel();
            }
        }
    }

    /**
     * Sets the current piece to a new piece and initializes the initial
     * values for drawing the squares.
     */
    private void newPiece() {
        currentPiece = nextPiece;
        squares = currentPiece.getSquares();
        nextPiece = tetrisPieces[rand.nextInt(RANDOM_SEED) % 7];
        mX = GRID_WIDTH / 2;
        mY = currentPiece.findFirstTopSquare(0);
        rotation = 0;
        //If the next position of the piece after being created is invalid, the game is over.
        if (!isValid(currentPiece, mX, mY + 1, rotation)) {
            gameOver = true;
        }
    }

    /**
     * Changes the level of the game every 10 lines
     * The speed of the game increases by 8% every time the level goes up.
     */
    private void updateGameLevel() {
        if (totalLines > 0 && totalLines % 3 == 0) {
            level++;
            gameSpeed = (int) (gameSpeed - (gameSpeed * .08));
        }
    }

    /**
     * Calculates score after every line is cleared. More lines cleared consecutively
     * equals more points.
     */
    private void updateGameScore() {
        switch (clearedLines) {
            case 1:
                score += 400 * (level);
                break;
            case 2:
                score += 1000 * (level);
                break;
            case 3:
                score += 3000 * (level);
                break;
            case 4:
                score += 12000 * (level);
                break;
        }
        clearedLines = 0;
    }

    /**
     * Used https://quangandroid.wordpress.com/2011/11/06/5/ to learn how to use
     * event.getPointerCoords.
     * Method used to modify the piece depending on the gesture the user
     * makes when touching the screen.
     * Tapping the screen once : rotate.
     * Swiping left or right : move piece left or right.
     * Swiping down : move piece down.
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        long endTime = event.getEventTime();
        long startTime = event.getDownTime();
        long duration = endTime - startTime;
        int actionIndex = event.getActionIndex();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                event.getPointerCoords(actionIndex, mDownPos);
                isLongPress = true;
                return true;

            case MotionEvent.ACTION_UP:
                isLongPress = false;
                //if the user releases his finger within 100ms of pressing down, rotate
                //the piece
                if (duration < 100) {
                    rotatePiece();
                    invalidate();
                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                event.getPointerCoords(actionIndex, mMovePos);
                //Find the change in x and change in y
                float dx = mMovePos.x - mDownPos.x;
                float dy = mMovePos.y - mDownPos.y;
                //dividing it by 2 so that the distance needed to move a piece is
                //twice as long as normal
                int moveX = (int) dx / (int) (mSquareWidth / 2);
                int moveY = (int) dy / (int) (mSquareHeight / 2);
                //Pieces that are moving left cannot move right at the same time,
                //pieces that are moving right cannot move left at the same time,
                //pieces that are moving left or right cannot move down at the same time.
                //pieces that are moving down cannot move left or right at the same time.
                if (moveX > 1) { //Move right
                    isLongPress = false;
                    move(1, 0);
                    event.getPointerCoords(actionIndex, mDownPos);
                    invalidate();
                } else if (moveX < -1) { // Move left
                    isLongPress = false;
                    move(-1, 0);
                    event.getPointerCoords(actionIndex, mDownPos);
                    invalidate();
                } else if (moveY > 1) { // Move down
                    isLongPress = false;
                    move(0, 1);
                    score++;
                    event.getPointerCoords(actionIndex, mDownPos);
                    invalidate();
                }
                return true;
        }
        return true;
    }

    /**
     * Method called by the TetrisActivity to update the view and move the piece down every
     * gameSpeed;
     */
    public void updateView() {
        if (isValid(currentPiece, mX, mY + 1, rotation)) {
            move(0, 1);
            invalidate();
        } else {
            addPiece(currentPiece, mY, mX);
            completedLines();
            updateGameScore();
            newPiece();
            invalidate();
        }
    }

    /**
     * Toggles pausing the game.
     * If the game is paused, pieces cannot move up, down, right, or left.
     * User must click unpause button to continue the game.
     */
    public void pauseGame() {
        paused = !paused;
    }

    /**
     * Toggles accelerometer when user taps the accelerometer button.
     */
    public void toggleAccelerometer() {
        enableAccelerometer = !enableAccelerometer;
        enableAccelerometerListener();
    }

    public boolean isAccelerometerEnabled() {
        return enableAccelerometer;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public TetrisPieces getNextPiece() {
        return nextPiece;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public int getGameSpeed() {
        return gameSpeed;
    }
}
