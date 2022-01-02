package com.example.mytetrisbyme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ImagePiece extends AppCompatActivity implements View.OnClickListener {
    //格子数量

    int viewWidth,viewHeight;
    int boxSize;
    int witdhNum =10;
    int heighNum =20;

    //图片资源数组
   int[] ImageArray;

    //经过拉伸的原图片
   public static Bitmap sourceBitmap;

    //记录当前放入的Tetris的顺序
    int indexNow =0;
   public static Bitmap[][] bitmapBoxs;
    //是否有碰撞
    boolean[][] ifColid;
     //用list存Tetris,每个tetris是4x2的坐标
    public static List<int[][]> TetrisList = new ArrayList<int[][]>();
    //表示下标对应的Tetris是否可用
    public static List<Boolean> usableTetris = new ArrayList<Boolean>();

    Button startGame,setting,about;
    public static String[] TetrisArray = {"I","J","L","O","S","T","Z"};

    List<Uri> UriList = new ArrayList<Uri>();

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagepiece);
        /*遍历图片文件夹，获取图片uri*/
//        getPicturesUri();
//        Log.d("Uri","没有LOG信息吗？");

        saveDefaultPicture();

        String TargetPath =  Environment.getExternalStorageDirectory().getPath() + "/MytetrisImages/";
//        BitmapUtils.getPicturesUri();

        File TargetDirectory = new File(TargetPath);
        Boolean Directory = TargetDirectory.isDirectory();
        Log.d("Uri","oncreate中是不是文件夹"+ Directory);
        //判断指定文件夹的路径是否存在
        if (!BitmapUtils.fileIsExist(TargetPath)) {
            Log.d("Uri", "TargetPath isn't exist");
        } else {
            //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
//            File saveFile = new File(TargetPath, name);
            for (File file : TargetDirectory.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")) {
//                    UriList.add(Uri.parse(path));
                    BitmapUtils.UriList.add(Uri.fromFile(new File(path)));
                }
            }
        }
        for (int i = 0; i < BitmapUtils.UriList.size(); i++) {
            Log.d("Uri","URI是"+ BitmapUtils.UriList.get(i));
        }

        WindowManager windowManager = getWindow().getWindowManager();
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        //屏幕实际宽度（像素个数）
        viewWidth = point.x * 2/3;
        //绘制游戏区域

        //屏幕实际高度（像素个数）
        viewHeight = viewWidth * (20/10);
        initButton();

    }

    /*创建文件夹，并把drawable中的默认图片存入其中*/
    public void saveDefaultPicture(){
        String TargetPath =  Environment.getExternalStorageDirectory().getPath() + "/MytetrisImages/";
        /*第一次打开时*/
        //判断指定文件夹的路径是否存在
        //传入指定的路径，然后判断路径是否存在
        File filenew=new File(TargetPath);
        /*如果文件夹不存在*/
        if (!filenew.exists()){
            BitmapUtils.fileIsExist(TargetPath);
            Resources res = this.getResources();
            Log.d("Uri","这个文件夹不存在啊");
            /*获取图片数组*/
            TypedArray typedArray = getResources().obtainTypedArray(R.array.Images);
            ImageArray = new int[typedArray.length()];
            for (int i = 0; i < typedArray.length(); i++) {
                @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable d = (BitmapDrawable) res.getDrawable(typedArray.getResourceId(i,0));
                /*根据资源ID创建Bitmap，再将Bitmap存入文件夹中*/
                Bitmap bitmap = d.getBitmap();
                String name = "bgimage"+Integer.toString(i)+".jpg";
                File saveFile = new File(TargetPath, name);

                try {
                    FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                    // compress - 压缩的意思
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                    //存储完成后需要清除相关的进程
                    saveImgOut.flush();
                    saveImgOut.close();
                    Log.d("Save Bitmap", "The picture is save to your phone!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    void getPicturesUri(){
        File TargetDirectory = new File(BitmapUtils.TargetPath);
        Boolean Directory = TargetDirectory.isDirectory();
        Log.d("Uri","是不是目录呢"+Directory);
        if (TargetDirectory.isDirectory()) {
            for (File file : TargetDirectory.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")) {
//                    UriList.add(Uri.parse(path));
                    UriList.add(Uri.fromFile(new File(path)));
                }
            }
        }
        for (int i = 0; i < UriList.size(); i++) {
            Log.d("Uri","URI是"+ UriList.get(i));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onResume() {
        super.onResume();
        BitmapUtils.getPicturesUri();
        /*设置被选中的图片为图片源*/
       setSelecteImage();
        boxSize = sourceBitmap.getWidth()/10;


        //切分
        PieceToBoxs(sourceBitmap);


        /*随机取一个点判断，一共取10x20个*/
        shuffleTetris();

        for (int i = 0; i < TetrisList.size(); i++) {
            for (int j = 0; j < 4; j++) {
                Log.i("Tetris", "TetrisList的所有值,当前" + i + ":" +  TetrisList.get(i)[j][0] + TetrisList.get(i)[j][1]);
            }
        }
    }

    public void initButton(){
       startGame = findViewById(R.id.startGame);
       setting=findViewById(R.id.Setting);
        about = findViewById(R.id.About);
        startGame.setOnClickListener(this);
        setting.setOnClickListener(this);
        about.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.startGame:

                intent.setClass( ImagePiece.this,ChooseDifficulty.class);
                startActivity(intent);
                break;
            case R.id.Setting:
                intent.setClass( ImagePiece.this,Setting.class);
                startActivity(intent);
                break;
            case R.id.About:
                ShowAboutDialog();
                break;
        }
    }

    /*将源图切成与地图个数一样的小方格*/
  public void PieceToBoxs(Bitmap sourceBitmap){
      bitmapBoxs = new Bitmap[10][20];
      ifColid = new boolean[10][20];
      for (int x = 0; x < bitmapBoxs.length; x++) {
          for (int y =  bitmapBoxs[0].length-1; y >=0; y--) {
              bitmapBoxs[x][y] = Bitmap.createBitmap(sourceBitmap, x*boxSize, y*boxSize, boxSize,boxSize,null,true );
              ifColid[x][y] = false;
          }
      }
  }

/*从200个bitmap切分组合成一个个Tetris,最多50个*/
 public boolean combinationToTetris(int x,int y) {
     /*确定一个起始点，已起点为依据，循环生成7中形状下不同方向的一种，
      * 检查碰撞，若无，组合成果，存入Tetris数组，并将对应Boxs碰撞状态设为true*/
     //随机生成一个数，以选定一种形状最为循环的开始，保证了随机性
     Random random = new Random();
     int TetrisType = random.nextInt(7);

     /*两层循环，每次组合后判断点是否有碰撞*/
     for (int num = TetrisType; num < 7; num++) {
         Tetris.pieceTetris(x, y, num);
         for (int direction = 0; direction < 4; direction++) {
             //确定实际坐标
             /*检查四个方块是否有碰撞，如有，下一轮循环，没有，成功退出*/
             for (int i = 0; i < 4; i++) {
                 if (checkColid(Tetris.TetrisCoordinates[i][0], Tetris.TetrisCoordinates[i][1])) {
                     break;
                 }
                 if (i == 3) {
                     /*应该存bitmap的下标啊”*/
                     /*没有碰撞，说明该Tetris可行，存入数组中*/
                     //必须新建一个便来用于向LIST中add，否则会覆盖LIST中之前的所有数据
                     int[][] thisTetrisCoordinate = new int[4][2];

//                     thisTetrisCoordinate = Tetris.TetrisCoordinates;
                     for (int k = 0; k < 4; k++) {
                         thisTetrisCoordinate[k][0] = Tetris.TetrisCoordinates[k][0];
                         thisTetrisCoordinate[k][1] = Tetris.TetrisCoordinates[k][1];
                     }
                     TetrisList.add(thisTetrisCoordinate);
                     usableTetris.add(true);
                     for (int z = 0; z < 4; z++) {
                         Log.i("Tetris","当前放入LIST的Tetris的顺序："+indexNow+"坐标"+ Tetris.TetrisCoordinates[z][0]+":"+Tetris.TetrisCoordinates[z][1]);
                     }
                     indexNow++;
                     //将该Tetris占去的boxs的碰撞状态变为true
                     changeColid(Tetris.TetrisCoordinates);
                     Log.i("Tetris","当前旋转的次数:"+ direction);
                     Log.i("Tetris","当前的形状数字是："+num);
                     return true;
                 }
             }
             /*旋转坐标*/
             Tetris.RotateCoordinate(num);
         }
     }

     for (int num = 0; num < TetrisType; num++) {
         Tetris.pieceTetris(x, y, num);
         for (int direction = 0; direction < 4; direction++) {

             /*检查四个方块是否有碰撞，如有，下一轮循环，没有，成功退出*/
             for (int i = 0; i < 4; i++) {
                 if (checkColid(Tetris.TetrisCoordinates[i][0], Tetris.TetrisCoordinates[i][1])) {
                     break;
                 }
                 if (i == 3) {
                     /*没有碰撞，说明该Tetris可行，存入数组中，结束循环*/
                     int[][]  thisTetrisCoordinate = new int[4][2];
                     for (int k = 0; k < 4; k++) {
                         thisTetrisCoordinate[k][0] = Tetris.TetrisCoordinates[k][0];
                         thisTetrisCoordinate[k][1] = Tetris.TetrisCoordinates[k][1];
                     }
//                     thisTetrisCoordinate = Tetris.TetrisCoordinates;
                     TetrisList.add(thisTetrisCoordinate);
                     usableTetris.add(true);
                     for (int z = 0; z < 4; z++) {
                         Log.i("Tetris","当前放入LIST的Tetris的顺序："+indexNow+"坐标"+ Tetris.TetrisCoordinates[z][0]+":"+Tetris.TetrisCoordinates[z][1]);
                         Log.i("Tetris","当前放入LIST内的坐标："+indexNow+"坐标"+ TetrisList.get(indexNow)[z][0] +":"+ TetrisList.get(indexNow)[z][1]);

                     }
                     indexNow++;
                     //修改用去的boxs的碰撞状态
                     changeColid(Tetris.TetrisCoordinates);
                     Log.i("Tetris","当前旋转的次数："+ direction);
                     Log.i("Tetris","当前的形状数字是："+num);
                     return true;
                 }
             }
             /*旋转坐标*/
             Tetris.RotateCoordinate(num);
         }

     }

 return false;
 }

 /*检查是否有碰撞，与边界，与被选择的方块*/
 public  boolean checkColid(int x, int y){
     return (x<0||y<0||x>=bitmapBoxs.length||y>=bitmapBoxs[0].length||ifColid[x][y]);
 }

/*将生成的Tetris所用去的Boxs的碰撞状态变为ture*/
    public void changeColid(int[][] coordinates){
        for (int i = 0; i < 4; i++) {
            ifColid[coordinates[i][0]][coordinates[i][1]] = true;
        }
    }

    /*打乱顺序,每次取一个随机点判断是否能生成Tetris*/
    public void shuffleTetris(){
        List<Integer> weith = new ArrayList<Integer>();
        List<Integer> heigh = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            weith.add(i);
        }
        for (int i = 0; i < 20; i++) {
            heigh.add(i);
        }
        for (int i = 0; i < 10; i++) {
            Log.i("num","随机LIST："+weith.get(i));
        }
        /*打乱LIST*/
        Collections.shuffle(weith);
        Collections.shuffle(heigh);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                //尝试合成俄罗斯方块
                combinationToTetris(weith.get(i),heigh.get(j));
            }

        }

    }

    /*设置被选中的图片作为背景*/
    public void setSelecteImage(){
        TypedArray typedArray = getResources().obtainTypedArray(R.array.Images);
        ImageArray = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            ImageArray[i] = typedArray.getResourceId(i,0);
        }
        /*读取选择图片的下标*/
        /*获取当前选择图片的下标*/
        SharedPreferences myPreferences = getSharedPreferences("selectedImage", MODE_PRIVATE);
        int selectedImage = myPreferences.getInt("index" , 0);

//        Resources res = getResources();
//        Bitmap bmp = BitmapFactory.decodeResource(res,ImageArray[selectedImage]);
         /*根据uri创建Bitmap*/
//        Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),BitmapUtils.UriList.get(selectedImage));
//          getPicturesUri();

        if (BitmapUtils.UriList != null) {
            Bitmap bmp = BitmapUtils.createBitmapByUri(BitmapUtils.UriList. get(selectedImage), this);
            sourceBitmap = Bitmap.createScaledBitmap(bmp, viewWidth, viewHeight, true);
        }
    }

    /*显示关于的对话框*/
    public void ShowAboutDialog(){
        String message = "该游戏是基于俄罗斯方块形式的拼图游戏，在下落中将方块固定到正确的位置即可得分。高难预警（手动狗头）！！！\n作者:Yt Z\ngithub：https://github.com/sixsixla/Android_MyTetris-.git";
        new AlertDialog.Builder(ImagePiece.this).setTitle("俄罗斯拼图").setMessage(message).show();
    }
}
