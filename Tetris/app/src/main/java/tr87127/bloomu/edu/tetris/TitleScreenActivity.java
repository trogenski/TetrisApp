package tr87127.bloomu.edu.tetris;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Contains the title screen image as well as the start game imagebutton and the scores
 * imagebutton
 * Created by Thomas Rogenski
 */
public class TitleScreenActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createFiles();
        setContentView(R.layout.activity_title_screen);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title_screen, menu);
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
     * method for Start button
     * @param view
     */
    public void startGame(View view) {
        Intent intent = new Intent(this, TetrisActivity.class);
        startActivity(intent);
    }
    /**
     * method for Scores button
     * @param view
     */
    public void showScores(View view) {
        Intent intent = new Intent(this, TetrisScoreHistory.class);
        intent.putExtra("chaos", 0);
        startActivity(intent);
    }

    /**
     * Creates the high_score_file if it's the first time the user is running the app.
     */
    private void createFiles(){
        File highScoreFile = new File(getFilesDir(), "high_score_file");
        if(!highScoreFile.exists()){
            FileOutputStream fos;
            String firstRun = "No-data -1"; // initializes the high score to be "No-data" and score of -1
            try {
                fos = openFileOutput("high_score_file", Context.MODE_PRIVATE);
                fos.write(firstRun.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
