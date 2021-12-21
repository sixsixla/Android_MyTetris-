package com.example.mytetrisbyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //游戏区域控件
    View view;
    //游戏区域的宽和高
    int viewWidth,viewHeight;

    //地图
    boolean[][] map;
    //辅助线画笔
    Paint linePaint;
    //方块画笔
    Paint boxPaint;
    //地图画笔
    Paint mapPaint;
    //地图大小
    int mapWidth = 10;
    int mapHeigh = 20;
    //方块
    Point[] boxs;
    //方块类型
    int boxType = 7;
    //方块大小
    int boxSize;
    //是否暂停
    boolean ifPause;
    //是否游戏结束
    boolean ifGameOver;
    //各个button按钮
    Button leftMove,Rotate,rightMove,downMove,toBottom,Pause;

    //开一个线程，用于方块自动下落
    public Thread downThread;
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message message){
            view.invalidate();
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamemain);
        leftMove = findViewById(R.id.leftMove);
        Rotate =  findViewById(R.id.Rotate);
        rightMove = findViewById(R.id.rightMove);
        downMove =  findViewById(R.id.downMove);
        toBottom = findViewById(R.id.toBottom);
        Pause =  findViewById(R.id.Pause);
        initView();
        initListener();
        initThread();
    }
    /*初始化游戏视图*/
    public void initView(){
        linePaint = new Paint();
        linePaint.setColor(0xff666666);
        //抗锯齿？
        linePaint.setAntiAlias(true);

        boxPaint = new Paint();
        boxPaint.setColor(0xff000000);
        //抗锯齿？
        boxPaint.setAntiAlias(true);

        mapPaint = new Paint();
        mapPaint.setColor(0x50000000);
        //抗锯齿？
        mapPaint.setAntiAlias(true);
        //得到父容器
        FrameLayout gameView = (FrameLayout)findViewById(R.id.Gameview);
        //实例化
        view = new View(this){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                //绘制方块
                for (int i = 0; i < boxs.length; i++) {
                    canvas.drawRect(
                            boxs[i].x*boxSize,
                            boxs[i].y*boxSize,
                            boxs[i].x*boxSize+boxSize,
                            boxs[i].y*boxSize+boxSize,
                            boxPaint);
                }
                //绘制辅助线
                for (int x = 0; x < map.length; x++) {
                    canvas.drawLine(x*boxSize,0,x*boxSize,view.getHeight(),linePaint);
                }
                for (int y = 0; y < map[0].length; y++) {
                    canvas.drawLine(0,y*boxSize,view.getWidth(),y*boxSize,linePaint);
            }

                /*绘制地图*/
                for (int x = 0; x < map.length; x++) {
                    for (int y = 0; y <map[x].length ; y++) {
                        if (map[x][y]){
                            canvas.drawRect(x*boxSize,y*boxSize,x*boxSize+boxSize,y*boxSize+boxSize,mapPaint);
                        }
                    }
                }

         }
        };


         /*获取屏幕大小，根据大小设置游戏区域大小*/
        WindowManager windowManager = getWindow().getWindowManager();
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        //屏幕实际宽度（像素个数）
        viewWidth = point.x * 2/3;
        //绘制游戏区域

        //屏幕实际高度（像素个数）
        viewHeight = viewWidth * (mapHeigh/mapWidth);
        map = new boolean[mapWidth][mapHeigh];
//        boxs = new Point[]{new Point(4,1),new Point(3,0),new Point(3,1),new Point(5,1)};
        //方块大小
        boxSize = viewWidth/map.length;
        //创建一个随机的俄罗斯方块Tetris
        newTetris();

        view.setLayoutParams(new FrameLayout.LayoutParams(viewWidth,viewHeight));
        //设置背景颜色
        view.setBackgroundColor(0x10000000);
        //添加到父容器中
        gameView.addView(view);
        //设置背景颜色
    }

    public void setPause(){

        if (ifPause){
            ifPause = false;
            Pause.setText("暂停");
            leftMove.setEnabled(true);
            Rotate.setEnabled(true);
            rightMove.setEnabled(true);
            downMove.setEnabled(true);
            toBottom. setEnabled(true);
            Pause.setEnabled(true);
        }
        else {
            ifPause = true;
            Pause.setText("继续");
            leftMove.setEnabled(false);
            Rotate.setEnabled(false);
            rightMove.setEnabled(false);
            downMove.setEnabled(false);
            toBottom. setEnabled(false);
        }
    }
    /*处理消行*/
    public void cleanLine(){
        boolean ifclean;
        for(int y=map[0].length-1;y>0;y--){
            ifclean = true;
            for (int x = 0; x < map.length; x++) {
                //如果有一个块都是true,消行
                if(!map[x][y]){
                    ifclean = false;
                }
            }
            //执行消行
            if(ifclean){
                //执行消行,把要消掉的行以上的内容都下移一行
                for(int nowy = y;nowy>0;nowy--){
                    for ( int x = 0; x< map.length; x++) {
                        map[x][nowy] = map[x][nowy-1];
                    }
                }
                y++;
            }
        }

    }

    /*判断游戏是否结束*/
    public  boolean checkIfGameOver(){
        for (int i = 0; i < boxs.length; i++) {
            if (map[boxs[i].x][boxs[i].y])
                return true;
        }
        return false;
    }
    public void initListener(){
        leftMove.setOnClickListener(this);
         Rotate.setOnClickListener(this);
      rightMove.setOnClickListener(this);
         downMove.setOnClickListener(this);
        toBottom.setOnClickListener(this);
        Pause.setOnClickListener(this);
    }

    public void initThread(){
        downThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (true){
                    //延迟0，5秒后执行一次下落
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(ifPause || ifGameOver)
                        continue;
                    downMove();
                    //给主线程传message,使其执行刷新命令
                    handler.sendEmptyMessage(0);
                }
            }
        };
        downThread.start();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.leftMove:
                Toast.makeText(this, "点击了左", Toast.LENGTH_SHORT).show();
                move(-1,0);
                break;
            case R.id.Rotate:
                 rotate();
                break;
            case R.id.rightMove:
                Toast.makeText(this, "右移", Toast.LENGTH_SHORT).show();
                move(1,0);
                break;
            case R.id.downMove:
//                move(0,1);
                downMove();
                break;
            case R.id.toBottom:
                Toast.makeText(this, "快速下落", Toast.LENGTH_SHORT).show();
                while (true) {
                    if (!downMove()) {
                        break;
                    }
                }
                break;
            case R.id.Pause:
                setPause();
                break;
        }
        //点击按钮后，要重新绘制控件
        view.invalidate();
    }

    public boolean move(int x,int y){
        for (int i = 0; i < boxs.length; i++) {
            //判断移动后是否会出界
            if(checkBorder(boxs[i].x+x,boxs[i].y+y)){
                return false;
            }
        }
        for (int i = 0; i < boxs.length; i++) {
                boxs[i].x += x;
                boxs[i].y += y;
            }
        return true;
    }
    //旋转方块
    public boolean rotate(){
        //正方形方块无需旋转
        if(boxType == 0)
            return false;
        for (int i = 0; i < boxs.length; i++) {
            //顺时针旋转90°
            int rotateX = -boxs[i].y + boxs[0].y + boxs[0].x;
            int rotateY = boxs[i].x - boxs[0].x + boxs[0].y;
            //判断是否出界
            if (checkBorder(rotateX, rotateY)) {
                return false;
            }
        }
        for (int i = 0; i < boxs.length; i++) {
                //顺时针旋转90°
                int rotateX = -boxs[i].y + boxs[0].y + boxs[0].x;
                int rotateY = boxs[i].x - boxs[0].x + boxs[0].y;
                boxs[i].x = rotateX;
                boxs[i].y = rotateY;
        }
        return true;
    }

    /*下落方块*/
    public boolean downMove(){
        //移动
        if(move(0,1)){
         return true;
        }
        for (int i = 0; i < boxs.length; i++) {
            map[boxs[i].x][boxs[i].y] = true;
        }
        //判断是否消行
        cleanLine();
        //生成新方块
        newTetris();
        //判断游戏是否结束
        ifGameOver = checkIfGameOver();
        return false;
    }

    /*边界判断，防止出界和触底重合*/
    public  boolean checkBorder(int x, int y){
        return (x<0||y<0||x>=map.length||y>=map[0].length||map[x][y]);
    }

/*创建一个新的方块*/
    public void newTetris(){
        Random random = new Random();
        boxType = random.nextInt(7);
        switch (boxType){
            //方块形状
            case 0:
                boxs = new Point[]{new Point(4,1),new Point(4,0),new Point(3,0),new Point(3,1)};
//                Toast.makeText(this, "方块行", Toast.LENGTH_SHORT).show();
                break;
                //L形状
            case 1:
                boxs = new Point[]{new Point(4,1),new Point(3,0),new Point(3,1),new Point(5,1)};
//                Toast.makeText(this, "L形状", Toast.LENGTH_SHORT).show();
                break;
               //反L
            case 2:
                boxs = new Point[]{new Point(4,1),new Point(5,0),new Point(3,1),new Point(5,1)};
//                Toast.makeText(this, "反L形状", Toast.LENGTH_SHORT).show();
                break;
            //楼梯形状
            case 3:
                boxs = new Point[]{new Point(4,1),new Point(5,1),new Point(3,0),new Point(4,0)};
//                Toast.makeText(this, "楼梯形状", Toast.LENGTH_SHORT).show();
                break;
            //反楼梯形状
            case 4:
                boxs = new Point[]{new Point(4,1),new Point(4,0),new Point(5,0),new Point(3,1)};
//                Toast.makeText(this, "反楼梯形状", Toast.LENGTH_SHORT).show();
                break;
            //长条形
            case 5:
                boxs = new Point[]{new Point(4,0),new Point(3,0),new Point(5,0),new Point(6,0)};
//                Toast.makeText(this, "长条形状", Toast.LENGTH_SHORT).show();
                break;
            //山字形状
            case 6:
                boxs = new Point[]{new Point(4,1),new Point(3,1),new Point(5,1),new Point(4,0)};
//                Toast.makeText(this, "山字形状", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
