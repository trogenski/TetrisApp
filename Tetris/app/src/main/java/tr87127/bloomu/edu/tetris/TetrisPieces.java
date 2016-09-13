package tr87127.bloomu.edu.tetris;

import android.graphics.Color;

/**
 * This enum is used to keep track of all possible tetris pieces, and their rotations.
 * Each tetris piece has a color, rows, columns, and 3D array parameters. The 3D array
 * contains all of the rotations for the piece.
 * Created by Thomas Rogenski
 */
public enum TetrisPieces {

    PieceI(Color.CYAN, 4, 4, new int[][][]{
            {
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
            },
            {
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},

            },
            {
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
            },
            {
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0},

            },
    }),
    PieceJ(Color.BLUE, 3, 3, new int[][][]{
            {
                    {1, 1, 1},
                    {0, 0, 1},
                    {0, 0, 0},
            },
            {
                    {0, 0, 1},
                    {0, 0, 1},
                    {0, 1, 1},

            },
            {
                    {0, 0, 0},
                    {1, 0, 0},
                    {1, 1, 1},
            },
            {
                    {1, 1, 0},
                    {1, 0, 0},
                    {1, 0, 0},

            },
    }),
    PieceL(Color.MAGENTA, 3, 3, new int[][][]{
            {
                    {1, 1, 1},
                    {1, 0, 0},
                    {0, 0, 0},
            },
            {
                    {0, 1, 1},
                    {0, 0, 1},
                    {0, 0, 1},

            },
            {
                    {0, 0, 0},
                    {0, 0, 1},
                    {1, 1, 1},
            },
            {
                    {1, 0, 0},
                    {1, 0, 0},
                    {1, 1, 0},

            },
    }),
    PieceO(Color.YELLOW, 2, 2, new int[][][]{
            {
                    {1, 1},
                    {1, 1},
            },
            {
                    {1, 1},
                    {1, 1},
            },
            {
                    {1, 1},
                    {1, 1},
            },
            {
                    {1, 1},
                    {1, 1},
            },
    }),
    PieceZ(Color.RED, 3, 3, new int[][][]{
            {
                    {0, 1, 1},
                    {1, 1, 0},
                    {0, 0, 0},
            },
            {
                    {1, 0, 0},
                    {1, 1, 0},
                    {0, 1, 0},

            },
            {
                    {0, 1, 1},
                    {1, 1, 0},
                    {0, 0, 0},
            },
            {
                    {0, 1, 0},
                    {0, 1, 1},
                    {0, 0, 1},

            },

    }),
    PieceT(Color.GREEN, 3, 3, new int[][][]{
            {
                    {1, 1, 1},
                    {0, 1, 0},
                    {0, 0, 0},
            },
            {
                    {0, 0, 1},
                    {0, 1, 1},
                    {0, 0, 1},
            },
            {
                    {0, 0, 0},
                    {0, 1, 0},
                    {1, 1, 1},
            },
            {
                    {1, 0, 0},
                    {1, 1, 0},
                    {1, 0, 0},
            },
    }),
    PieceS(Color.GRAY, 3, 3, new int[][][]{
            {
                    {1, 1, 0},
                    {0, 1, 1},
                    {0, 0, 0},
            },
            {
                    {0, 1, 0},
                    {1, 1, 0},
                    {1, 0, 0},

            },
            {
                    {1, 1, 0},
                    {0, 1, 1},
                    {0, 0, 0},
            },
            {
                    {0, 0, 1},
                    {0, 1, 1},
                    {0, 1, 0},

            },

    });

    private final int cols;
    private final int rows;
    private final int[][][] squares;
    private final int color;

    /**
     * Constructor for the TetrisPieces
     * @param color of the piece
     * @param rows amount of rows in the 3D array
     * @param cols amount of cols in the 3D array
     * @param squares the 3D array
     */
    TetrisPieces(int color, int rows, int cols, int[][][] squares) {
        this.cols = rows;
        this.rows = cols;
        this.squares = squares;
        this.color = color;
    }

    /**
     * Given a rotation, this method finds the first top square of said rotation
     * Used for error checking.
     * @param rotation
     * @return
     */
    public int findFirstTopSquare(int rotation) {
        for (int i = 0; i < getRows(); i++)
            for (int j = 0; j < getCols(); j++)
                if (squares[rotation][i][j] == 1) {
                    return i;
                }
        return -1;
    }
    /**
     * Given a rotation, this method finds the first bottom square of said rotation
     * Used for error checking.
     * @param rotation
     * @return
     */
    public int findFirstBottomSquare(int rotation) {
        for (int i = getRows() - 1; i >= 0; i--)
            for (int j = 0; j < getCols(); j++)
                if (squares[rotation][i][j] == 1) {
                    return getRows() - i;
                }
        return -1;
    }

    /**
     * Given a rotation, this method finds the first right square of said rotation
     * Used for error checking.
     * @param rotation
     * @return
     */
    public int findFirstRightSquare(int rotation) {
        for (int i = getCols() - 1; i >= 0; i--)
            for (int j = 0; j < getRows(); j++)
                if (squares[rotation][j][i] == 1)
                    return getCols() - i;
        return -1;
    }
    /**
     * Given a rotation, this method finds the first left square of said rotation
     * Used for error checking.
     * @param rotation
     * @return
     */
    public int findFirstLeftSquare(int rotation) {
        for (int i = 0; i < getCols(); i++)
            for (int j = 0; j < getRows(); j++)
                if (squares[rotation][j][i] == 1)
                    return i;
        return -1;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int[][][] getSquares() {
        return squares;
    }

    public int getColor() {
        return color;
    }

}


