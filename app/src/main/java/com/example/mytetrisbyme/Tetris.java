package com.example.mytetrisbyme;

import android.util.Log;
import android.util.Range;

import java.util.Random;

/*记录其中俄罗斯方块的形状和各自的方向，以及一些Rand方法*/
public class Tetris {
    //起点坐标
    int x, y;
    //一个4x2的数组，存一组俄罗斯方块的相对坐标
    public static int[][] TetrisCoordinates = new int[4][2];
    //存俄罗斯方块的类型，共七个
    public static String[] Tetris = {"I","J","L","O","T","Z","S"};


    /*根据初始坐标算次其他三个坐标，用什么结构保存呢？*/
   // public static void pieceTetris(int x, int y,int TetrisType,int direction)
    public static void pieceTetris(int x, int y,int TetrisType){

        TetrisCoordinates[0][0] = x;
        TetrisCoordinates[0][1] = y;
        /*随机一个数，作为第一次开始尝试的Tetris形状*/
       String type = Tetris[TetrisType];
//       decideCoordinates(type,direction);
        decideCoordinates(type);
    }

    /*根据Tetris形状和方向确定坐标串*/
    public static void decideCoordinates(String type ) {
        int x = TetrisCoordinates[0][0];
        int y = TetrisCoordinates[0][1];

//        Log.i("Tetris","当前块的类型是"+type);

        switch (type) {
            case "I":
                        TetrisCoordinates[1][0] = x-1;
                        TetrisCoordinates[1][1] = y;
                        TetrisCoordinates[2][0] = x + 1;
                        TetrisCoordinates[2][1] = y;
                        TetrisCoordinates[3][0] = x + 2;
                        TetrisCoordinates[3][1] = y;

                        break;

            case "J":

                        TetrisCoordinates[1][0] = x-1;
                        TetrisCoordinates[1][1] = y;
                        TetrisCoordinates[2][0] = x - 1;
                        TetrisCoordinates[2][1] = y - 1;
                        TetrisCoordinates[3][0] = x + 1;
                        TetrisCoordinates[3][1] = y;
                //尝试把J换掉

                break;
            case "L":

                        TetrisCoordinates[1][0] = x - 1;
                        TetrisCoordinates[1][1] = y;
                        TetrisCoordinates[2][0] = x+1;
                        TetrisCoordinates[2][1] = y;
                        TetrisCoordinates[3][0] = x+1;
                        TetrisCoordinates[3][1] = y-1;
                        break;

            case "O":

                        TetrisCoordinates[1][0] = x -1;
                        TetrisCoordinates[1][1] = y;
                        TetrisCoordinates[2][0] = x-1;
                        TetrisCoordinates[2][1] = y - 1;
                        TetrisCoordinates[3][0] = x ;
                        TetrisCoordinates[3][1] = y - 1;

                break;
            case "S":
                        TetrisCoordinates[1][0] = x-1;
                        TetrisCoordinates[1][1] = y;
                        TetrisCoordinates[2][0] = x ;
                        TetrisCoordinates[2][1] = y - 1;
                        TetrisCoordinates[3][0] = x + 1;
                        TetrisCoordinates[3][1] = y -1;

                break;

            case "T":

                        TetrisCoordinates[1][0] = x-1;
                        TetrisCoordinates[1][1] = y;
                        TetrisCoordinates[2][0] = x+1;
                        TetrisCoordinates[2][1] = y;
                        TetrisCoordinates[3][0] = x;
                        TetrisCoordinates[3][1] = y - 1;
                        break;

            case "Z":

                        TetrisCoordinates[1][0] = x+1;
                        TetrisCoordinates[1][1] = y ;
                        TetrisCoordinates[2][0] = x ;
                        TetrisCoordinates[2][1] = y - 1;
                        TetrisCoordinates[3][0] = x - 1;
                        TetrisCoordinates[3][1] = y -1;
                        break;
        }

    }

    /*旋转坐标*/
    public static void RotateCoordinate(int type){
        if(Tetris[type].equals("O"))
            return ;
        for (int i = 0; i < 4; i++) {
            //顺时针旋转90°
            int rotateX = -TetrisCoordinates[i][1] + TetrisCoordinates[0][1] + TetrisCoordinates[0][0];
            int rotateY = TetrisCoordinates[i][0] - TetrisCoordinates[0][0] + TetrisCoordinates[0][1];
            TetrisCoordinates[i][0] = rotateX;
            TetrisCoordinates[i][1] = rotateY;
        }
    }
}
