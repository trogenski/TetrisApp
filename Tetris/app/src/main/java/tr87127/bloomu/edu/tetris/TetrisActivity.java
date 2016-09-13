package tr87127.bloomu.edu.tetris;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Activity that starts after Start button is pressed. Contains the view for the game, as well as
 * other views to show the next piece, score, level and lines.
 * Created by Thomas Rogenski
 */
public class TetrisActivity extends Activity {

    private TetrisView tetrisView;
    private NextPieceView nextPieceView;
    private Handler handler;
    private Runnable runnable;
    private TextView scoreView;
    private TextView linesView;
    private TextView levelView;
    private String name;
    private int score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris_game);
        FrameLayout gameFrame = (FrameLayout) findViewById(R.id.game_frame);
        FrameLayout nextPieceFrame = (FrameLayout) findViewById(R.id.next_piece);

        linesView = (TextView) findViewById(R.id.lines_count);
        levelView = (TextView) findViewById(R.id.level_count);
        scoreView = (TextView) findViewById(R.id.score_total);
        tetrisView = new TetrisView(this);
        nextPieceView = new NextPieceView(this);
        gameFrame.addView(tetrisView);
        nextPieceFrame.addView(nextPieceView);

        nextPieceView.setNextPiece(tetrisView.getNextPiece()); //sends the next piece to the nextPieceView so that it can be displayed.

        /**
         * A handler is used for updating the TetrisView. It executes the run method of the
         * runnable object with a delay set by the developer.
         */
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!tetrisView.isGameOver() && !tetrisView.isPaused()) {
                    tetrisView.updateView(); //
                    scoreView.setText(String.valueOf(tetrisView.getScore()));
                    linesView.setText(String.valueOf(tetrisView.getTotalLines()));
                    levelView.setText(String.valueOf(tetrisView.getLevel()));
                    nextPieceView.setNextPiece(tetrisView.getNextPiece());
                    nextPieceView.invalidate();
                    handler.postDelayed(runnable, tetrisView.getGameSpeed());
                } else if (tetrisView.isGameOver()) {
                    handler.removeCallbacks(runnable);
                    gameOverDialog().show();
                } else if (tetrisView.isPaused()) {
                    pauseDialog().show();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    /**
     * Stop the game if the user presses the back button
     */
    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tetris_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Builds an AlertDialog to tell the user that the game is currently paused.
     * User can click the button to unpause the game.
     *
     * @return
     */
    private AlertDialog pauseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TetrisActivity.this);
        builder.setTitle("Game Paused").
                setPositiveButton("UNPAUSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tetrisView.pauseGame();
                        handler.postDelayed(runnable, 500);
                    }
                });
        builder.setCancelable(false);
        return builder.create();
    }

    /**
     * Builds an AlertDialog that tells the user the game is over, the score,
     * and returns the user to the title screen.
     *
     * @return the gameover alert dialog
     */
    private AlertDialog gameOverDialog() {
        score = Integer.valueOf(scoreView.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(TetrisActivity.this);
        final EditText nameField = new EditText(this);
        builder.setCancelable(false);
        builder.setTitle("GAME OVER!").
                setMessage("Score: " + String.valueOf(score) + "\nEnter your name: ").
                setView(nameField).
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    Intent i = new Intent(TetrisActivity.this, TitleScreenActivity.class);

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = nameField.getText().toString().replaceAll(" ", "-");
                        updateHistory();
                        startActivity(i);
                    }
                });
        return builder.create();
    }

    /**
     * Tells TetrisView to pause the game whenever the user clicks on the pause button.
     *
     * @param view
     */
    public void pauseGame(View view) {
        tetrisView.pauseGame();
    }

    /**
     * Toggles the accelerometer when the user clicks on the accelerometer button.
     * It also shows a Toast with the current status of the accelerometer
     *
     * @param view
     */
    public void toggleAccelerometer(View view) {
        tetrisView.toggleAccelerometer();
        if (tetrisView.isAccelerometerEnabled()) {
            Toast.makeText(this, "Accelerometer Enabled!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Accelerometer Disabled!", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * This method is called after a game is over and it checks if the score obtained by the user
     * is higher than the current highscore, if it is, it replaces the current highscore file
     * with a new file with the updated name and highscore. It also adds the game that just ended
     * to a history regardless of score or name.
     */
    private void updateHistory() {
        String highScoreFile = "high_score_file";
        String historyFile = "history_file";
        String[] pair;
        String updated = name + " " + String.valueOf(score);
        String line;
        FileInputStream fis;
        FileOutputStream fos;
        BufferedReader reader;
        try {
            fis = openFileInput(highScoreFile);
            reader = new BufferedReader(new InputStreamReader(fis));
            line = reader.readLine();
            pair = line.split("\\s+");
            if (Integer.parseInt(pair[1]) < score) {
                fos = openFileOutput(highScoreFile, Context.MODE_PRIVATE);
                fos.write(updated.getBytes());
                fos.close();
            }
            fos = openFileOutput(historyFile, Context.MODE_APPEND);
            fos.write((updated + "\n").getBytes());

            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
