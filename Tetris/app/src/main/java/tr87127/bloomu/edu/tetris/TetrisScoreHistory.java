package tr87127.bloomu.edu.tetris;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the score history and high score files and updates the scores activity
 */
public class TetrisScoreHistory extends Activity {
    private ListView namesList;
    private List<String> namesArrayList;
    private List<String> scoresArrayList;
    private ListView scoresList;
    private TextView highScoreName;
    private TextView highScoreScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris_score_history);
        namesList = (ListView) findViewById(R.id.names_list);
        scoresList = (ListView) findViewById(R.id.scores_list);
        namesArrayList = new ArrayList<>();
        scoresArrayList = new ArrayList<>();
        highScoreName = (TextView) findViewById(R.id.high_score_name);
        highScoreScore = (TextView) findViewById(R.id.high_score_score);
        populateHistory();

        //Array adapters used to populate the listviews for the score history
        ArrayAdapter<String> namesArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, namesArrayList);
        ArrayAdapter<String> scoresArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, scoresArrayList);

        namesList.setAdapter(namesArrayAdapter);
        scoresList.setAdapter(scoresArrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tetris_leaderboard, menu);
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
     * This method reads the high_score_file and sets the text of the appropriate fields to
     * the current high score and the name associated to the high score.
     * It also reads the file history_file which contains the name and score
     * of all previously played games on the device.
     * Both of these files are stored in the internal storage of the device.
     */
    private void populateHistory() {

        String historyFile = "history_file";
        String highScoreFile = "high_score_file";
        FileInputStream fis;
        BufferedReader reader;
        String line;
        String[] pair;
        try {
            fis = openFileInput(highScoreFile);
            reader = new BufferedReader(new InputStreamReader(fis));
            while ((line = reader.readLine()) != null) {
                pair = line.split("\\s+");
                highScoreName.setText(pair[0].replaceAll("-", " "));
                highScoreScore.setText(pair[1]);
            }
            reader.close();
            fis.close();

            fis = openFileInput(historyFile);
            reader = new BufferedReader(new InputStreamReader(fis));
            while ((line = reader.readLine()) != null) {
                if (line != "\n") {
                    pair = line.split("\\s+");
                    namesArrayList.add(pair[0].replaceAll("-", " "));
                    scoresArrayList.add(pair[1]);
                }
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
