package com.example.anders.drawingapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private GameView gameView;
    private boolean executeOnResume = false;
    private boolean executeOnStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pauseGame();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //gameView.resumeGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(executeOnResume){
            gameView.resumeGame();
        }
        else executeOnResume = true;

    }

    @Override
    protected void onStop() {
        super.onStop();
        gameView.pauseGame();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        gameView.resumeGame();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(executeOnStart){
            gameView.resumeGame();
        }
        else executeOnStart = true;
    }
}
