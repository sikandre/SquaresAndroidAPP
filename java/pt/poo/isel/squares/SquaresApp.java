package pt.poo.isel.squares;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import pt.poo.isel.squares.View.SquareView;
import pt.poo.isel.squares.model.Loader;
import pt.poo.isel.squares.model.Squares;
import pt.poo.isel.squares.model.square.Square;
import pt.poo.isel.tile.OnTileTouchListener;
import pt.poo.isel.tile.TilePanel;

public class SquaresApp extends Activity {

    public static final String LEVELS_FILE = "Levels.txt";
    private TilePanel grid;
    private TextView moves;
    private TilePanel goals;
    private Squares model;
    private Squares.Listener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squares);

        grid = findViewById(R.id.grid);
        moves = findViewById(R.id.moves);
        goals = findViewById(R.id.goals);

        initBoard();
        play();



        model.setListener(new Squares.Listener() {
            @Override
            public void notifyDelete(Square s, int l, int c) {
                grid.setTile(c,l,null);
            }

            @Override
            public void notifyMove(Square s, int lFrom, int c, int lTo) {
            }

            @Override
            public void notifyNew(Square s, int l, int c) {

            }

            @Override
            public void notifyPut(Square s, int l, int c) {
                grid.setTile(l,c, new SquareView(s));
            }
        });



    }

    private void play() {
        grid.setListener(new OnTileTouchListener() {
            @Override
            public boolean onClick(int xTile, int yTile) {
                message("Click on "+yTile+","+xTile);
                //grid.setTile(xTile,yTile,null);
                return model.touch(yTile,xTile);
                //return true;

            }

            @Override
            public boolean onDrag(int xFrom, int yFrom, int xTo, int yTo) {
                return false;
            }

            @Override
            public void onDragEnd(int x, int y) {

            }

            @Override
            public void onDragCancel() {

            }
        });


    }

    private void initBoard() {
        int height = grid.getHeightInTiles();
        int width = grid.getWidthInTiles();

        //Tile [][] t = model.grid //todo
        //grid.setAllTiles();
        int lvl = 0;
        loadLevel(++lvl);
        updateMoves();
        setGoals();
        for (int line = 0; line < height; line++) {
            for (int col = 0; col < width; col++) {
                grid.setTile(col, line, new SquareView(model.getSquare(line, col)));
            }
        }
    }

    private void setGoals() {
        int numGoals=model.getNumGoals();
        goals.setSize(numGoals,1);
        for (int i = 0; i < numGoals; i++) {
            goals.setTile(i,0,new SquareView(model.getGoal(i).square));

        }
    }

    private void updateMoves() {
        moves.setText(""+model.getTotalMoves());

    }


    private boolean loadLevel(int n) {
        Scanner in = null;
        AssetManager am = getAssets();
        try {
            in = new Scanner(am.open(LEVELS_FILE)); // Scanner to read the file
            model = new Loader(in).load(n);                     // Load level from scanner
            model.setListener(listener);                      // Set the listener of modifications
            return true;
        } catch (FileNotFoundException | InputMismatchException e) {
            System.out.println("Error loading file \"" + LEVELS_FILE + "\":\n" + e.getMessage());
            return false;
        } catch (Loader.LevelFormatException e) {
            System.out.println(e.getMessage() + " in file \"" + LEVELS_FILE + "\"");
            System.out.println(" " + e.getLineNumber() + ": " + e.getLine());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (in != null) in.close();   // Close the file
        }
    }

    private void message(String txt) {
        Toast.makeText(this,txt,Toast.LENGTH_LONG).show();
    }
}
