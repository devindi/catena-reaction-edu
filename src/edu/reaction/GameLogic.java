package edu.reaction;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private final GameView view;
    private final GameActivity activity;
    private int moveNumber=0, currentPlayer=0;
    private final int COUNT_OF_PLAYERS, BOARD_WIDTH, BOARD_HEIGHT;
    private final Cell[][] cells;

    private final int[] colors={0xff1d76fc, 0xfffb1d76, 0xff76fb1d, 0xffa21cfb};//цвета атомов
    private final Handler mHandler;

    public GameLogic(GameView view, GameActivity activity) {
        this.view = view;
        this.activity=activity;
        mHandler=new Handler();
        //инициализируем игровые параметры (количество игроков, размер доски)
        this.COUNT_OF_PLAYERS=2;
        this.BOARD_HEIGHT=10;
        this.BOARD_WIDTH=10;
        cells=new Cell[BOARD_WIDTH][BOARD_HEIGHT];
        for(int x=0; x<BOARD_WIDTH; x++){
            for(int y=0; y<BOARD_HEIGHT; y++){
                if((x==0 || x==BOARD_WIDTH-1) && (y==0 || y==BOARD_HEIGHT-1)){
                    cells[x][y]=new Cell(2);//угловые ячейки имеют емкость 2
                }else
                if((x==0 || x==BOARD_WIDTH-1) || (y==0 || y==BOARD_HEIGHT-1)){
                    cells[x][y]=new Cell(3);//краевые, но не угловые - 3
                }else{
                    cells[x][y]=new Cell(4);//остальные - 4
                }
            }
        }
    }


    //вызывается из вьюхи по одиночному тапу
    public void addAtom(final int cellX, final int cellY) {
        //получаем ячейку, в которую добавляем атом, если ее нет в массиве - выходим из функции.
        final Cell currentCell;
        try{
            currentCell=cells[cellX][cellY];
        }catch (IndexOutOfBoundsException ex){
            return;
        }
        //если в ячейке уже есть атомы этого игрока
        if(currentCell.getPlayer()==currentPlayer){
            currentCell.addAtom();
            view.drawAtoms(cellX, cellY, colors[currentPlayer], currentCell.getCountOfAtoms());
            //если ячейка заполнена
            if(currentCell.isFilled()){
                final List<Cell> nearby=new ArrayList<Cell>(4);//лист соседних ячеек
                selfAddCell(cellX, cellY-1, nearby);
                selfAddCell(cellX, cellY+1, nearby);
                selfAddCell(cellX-1, cellY, nearby);
                selfAddCell(cellX+1, cellY, nearby);
                for(Cell nearbyCell:nearby){
                    nearbyCell.setPlayer(currentPlayer);//соседним ячейкам устанавливаем нового владельца
                }
                delayedAddAtom(cellX, cellY-1);
                delayedAddAtom(cellX, cellY+1);
                delayedAddAtom(cellX-1, cellY);
                delayedAddAtom(cellX+1, cellY);
                //через секунду произойдет вызов метода run()
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //текущая ячейка становится нейтральной
                        currentCell.setPlayer(-1);
                        //и пустой
                        currentCell.resetCount();
                        view.drawAtoms(cellX, cellY, 0x000000, 0);
                    }
                }, 1000);
                return;
            }
        }else if(currentCell.getPlayer()==-1){
            currentCell.addAtom();
            view.drawAtoms(cellX, cellY, colors[currentPlayer], currentCell.getCountOfAtoms());
            currentCell.setPlayer(currentPlayer);
        }else{
            return;
        }
    }

    //через секунду соседние ячйки получат по атому
    private void delayedAddAtom(final int cellX, final int cellY){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addAtom(cellX, cellY);
            }
        }, 1000);
    }


    //добавляем в лист target существующие ячейки
    private void selfAddCell(int cellX, int cellY, List<Cell> target){
        try{
            target.add(cells[cellX][cellY]);
        }catch (IndexOutOfBoundsException ignore){}
    }

    private class Cell{
        int player=-1, countOfAtoms=0;
        final int maxCountOfAtoms;

        Cell(int maxCountOfAtoms){
            this.maxCountOfAtoms=maxCountOfAtoms;
        }

        public int getCountOfAtoms() {
            return countOfAtoms;
        }

        public int getPlayer() {
            return player;
        }

        public void setPlayer(int player) {
            this.player = player;
        }

        public void resetCount() {
            this.countOfAtoms = 0;
        }

        public void addAtom(){
            this.countOfAtoms++;
        }

        boolean isFilled(){
            return this.countOfAtoms == this.maxCountOfAtoms;
        }
    }
}
