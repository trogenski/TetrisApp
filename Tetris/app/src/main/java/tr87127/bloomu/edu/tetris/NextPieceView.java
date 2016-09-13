package tr87127.bloomu.edu.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Gets and draws the nextPiece that will be falling down on the grid for the user to see.
 * Created by Thomas Rogenski
 */
public class NextPieceView extends View {
    private Paint mSquarePaint;

    private float mSquareHeight;
    private float mSquareWidth;
    private int mWidth;
    private int mHeight;
    private float mX;
    private float mY;
    private TetrisPieces nextShape;

    public NextPieceView(Context context) {
        super(context);
        mSquarePaint = new Paint();
        nextShape = null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        //8x8 grid
        mSquareWidth = w / 8;
        mSquareHeight = h / 8;
        //starting position for the first squares (middle of screen)
        mX = 8 / 2;
        mY = 8 / 2 - 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Drawing the main filled in shape.
        mSquarePaint.setColor(nextShape.getColor());
        mSquarePaint.setStyle(Paint.Style.FILL);
        drawShape(nextShape, canvas);
        //Drawing an outline for every filled in rectangle.
        mSquarePaint.setColor(Color.BLACK);
        mSquarePaint.setStyle(Paint.Style.STROKE);
        mSquarePaint.setStrokeWidth(2);
        drawShape(nextShape, canvas);
    }

    /**
     * Draws shape onto canvas
     * @param shape shape to be drawn
     * @param canvas
     */
    public void drawShape(TetrisPieces shape, Canvas canvas) {
        /**
         * To draw the shape, we first have to get an array of squares that depends on the
         * rotation of the piece (always 0 for this case). Then we iterate through the rows
         * and columns to find where the array = 1, which means draw a square; array = 0 means
         * empty space.
         */
        for (int i = 0; i < shape.getRows(); i++) {
            for (int j = 0; j < shape.getCols(); j++) {
                if (shape.getSquares()[0][i][j] == 1) {
                    canvas.drawRect(new RectF(
                            (mX - 1 + j) * mSquareWidth,
                            (mY + i) * mSquareHeight - mSquareHeight,
                            (mX + j) * mSquareWidth,
                            (mY + i) * mSquareHeight), mSquarePaint);
                }
            }
        }
    }

    /**
     * This method receives a shape from TetrisActivity and updates the next shape so that it can
     * be drawn.
     * @param nextShape shape to be drawn
     */
    public void setNextPiece(TetrisPieces nextShape) {
        this.nextShape = nextShape;
    }
}
