package edu.reaction;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        GameView view=(GameView)findViewById(R.id.game_view);
        GameLogic logic=new GameLogic(view, this);
        view.setLogic(logic);
    }

    void setPlayerName(int playerID){

    }

    void setScore(int[] score){

    }

    void setMoveNumber(int moveNumber){

    }

    void endGame(int winnerID, int score){

    }
}
