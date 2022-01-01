package com.example.mytetrisbyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //游戏区域控件
    View view;
    //得分
    int score = 0;
    //游戏区域的宽和高，像素
    public static int viewWidth,viewHeight;

    //地图
    boolean[][] map;
    //记录块是否正确
    boolean[][] ifShowPicture;
    //辅助线画笔
    Paint linePaint;
    //方块画笔
    Paint boxPaint;
    //地图画笔
    Paint mapPaint;
    //状态画笔，画暂停和游戏介绍的提示
    Paint statePaint;
    //地图格子数
    int mapWidth = 10;
    int mapHeigh = 20;
    //存构成Tetris的bitmap的下标
    int[][] coordinateTwo = new int[][]{new int[]{0,1},new int[]{0,1},new int[]{0,1},new int[]{0,1}};
    //方块
    Point[] boxs = new Point[]{new Point(3,0),new Point(4,0),new Point(5,0),new Point(6,0) };
    //在map中产生Tetris的初始坐标
    Point generatePoint = new Point(3,1);
    //方块类型
    int boxType = 7;
    //所选择的Tetris的index
    int TetrisIndex;
    //两个list,按顺序存地图上不可用的点和该点对应的bitmap
    List<Point> unusableBoxs = new ArrayList<Point>();
    List<Point> withBitMap = new ArrayList<Point>();

    //方块大小
    int boxSize;
    //是否暂停
    boolean ifPause;
    //是否游戏结束
    boolean ifGameOver;
    //各个button按钮
    Button leftMove,Rotate,rightMove,downMove,toBottom,upMove,Pause,ToFix,reStart;
    ImageButton backMenu;
    TextView scoreView,mostScoreView;

    ImageButton musicPlay;
    boolean isPlaying = false;
    //开一个线程，用于方块自动下落
    public Thread downThread;
    //音乐播放类
    MediaMusci mediaMusci = new MediaMusci();
    //游戏难度，默认为困难
    String Mode = "difficulty";
    //游戏是否通关
    boolean ifGmaeOK = false;
    //当前难度下的最高分
    int Mostscore = 0;
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message message){
            view.invalidate();
            setGmaeOver();
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamemain2);
        leftMove = findViewById(R.id.leftMove);
        Rotate =  findViewById(R.id.Rotate);
        rightMove = findViewById(R.id.rightMove);
        upMove = findViewById(R.id.upMove);

        downMove =  findViewById(R.id.downMove);
//        toBottom = findViewById(R.id.toBottom);
        Pause =  findViewById(R.id.Pause);

        ToFix = findViewById(R.id.toFix);
        scoreView = findViewById(R.id.score);
        musicPlay = findViewById(R.id.musicPlay);
        reStart = findViewById(R.id.reStart);
        backMenu = findViewById(R.id.backMenu);
        mostScoreView = findViewById(R.id.mostscore);
        initView();
        initListener();
        upMove.setEnabled(false);

        Intent intent = getIntent();
        Mode = intent.getStringExtra("Mode");
        /*根据Mode设置最高分*/
        GetMostScore(Mode);
        if (Mode.equals("difficulty")||Mode.equals("hard")){
            initThread();
        }
        else if (Mode.equals("normal")){
            upMove.setEnabled(true);
        }

        musicPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying){
                    isPlaying = false;
                    mediaMusci.stopVoice();
                    musicPlay.setBackgroundResource(R.drawable.musicoff);
                }
                else {
                    isPlaying = true;
                    mediaMusci.startVoice(MainActivity.this);
                    musicPlay.setBackgroundResource(R.drawable.musicon);
                }
            }
        });
        musicPlay.callOnClick();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaMusci.stopVoice();
    }

    /*初始化游戏视图*/
    public void initView() {
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
        //状态画笔,
        statePaint = new Paint();
        statePaint.setTextSize(300);
        statePaint.setColor(0xff000000);
        statePaint.setAntiAlias(true);

        FrameLayout gameView = (FrameLayout) findViewById(R.id.Gameview);
        //实例化
        view = new View(this) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                /*绘制底层背景*/
                if (Mode.equals("normal")||Mode.equals("difficulty")) {
                    DrawBackground(canvas);
                }
                DrawBoxs(boxs, canvas);
                DrawLines(canvas);

                /*绘制地图,即固定后不正确的块*/
                for (int x = 0; x < map.length; x++) {
                    for (int y = 0; y < map[x].length; y++) {
                        if (map[x][y]) {
                            DrawUnableBoxsInMap(x, y, canvas);
                            /*画辅助线*/
                            DrawLines(canvas);
                        }
                    }
                }
                /*绘制要显示原图的块,即正确的块*/
                for (int x = 0; x < map.length; x++) {
                    for (int y = 0; y < map[x].length; y++) {
                        Log.i("show","当前的x,y,boolen:"+x+y+ifShowPicture[x][y]);
                        if (ifShowPicture[x][y]) {
                            canvas.drawBitmap(ImagePiece.bitmapBoxs[x][y], x * boxSize, y * boxSize, new Paint());
                        }
                    }
                }
                /*绘制游戏暂停提示*/
                DrawWhenPause(canvas);
                DrawWhenGameOver(canvas);
                DrawWhenGameOK(canvas);
            }
        };
                /*获取屏幕大小，根据大小设置游戏区域大小*/
                WindowManager windowManager = getWindow().getWindowManager();
                Point point = new Point();
                windowManager.getDefaultDisplay().getRealSize(point);
                //屏幕实际宽度（像素个数）
                viewWidth = point.x * 2 / 3;
                //绘制游戏区域

                //屏幕实际高度（像素个数）
                viewHeight = viewWidth * (mapHeigh / mapWidth);
                map = new boolean[mapWidth][mapHeigh];
                ifShowPicture = new boolean[mapWidth][mapHeigh];
//        boxs = new Point[]{new Point(4,1),new Point(3,0),new Point(3,1),new Point(5,1)};
                //方块大小
                boxSize = viewWidth / map.length;
                //创建一个随机的俄罗斯方块Tetris
//        newTetris();
                copyTetrisToMap(generatePoint);
                Toast.makeText(this, "被重新绘制了", Toast.LENGTH_SHORT).show();

                view.setLayoutParams(new FrameLayout.LayoutParams(viewWidth, viewHeight));
                //设置背景颜色
                view.setBackgroundColor(0x10000000);
                //添加到父容器中
                gameView.addView(view);
                //设置背景颜色
            }


            public void setPause() {

                if (ifPause) {
                    ifPause = false;
                    Pause.setText("暂停");
                    leftMove.setEnabled(true);
                    Rotate.setEnabled(true);
                    rightMove.setEnabled(true);
                    downMove.setEnabled(true);
//                    toBottom.setEnabled(true);
                    Pause.setEnabled(true);
                    mediaMusci.resumeVoic();


                } else {
                    ifPause = true;
                    Pause.setText("继续");
                    leftMove.setEnabled(false);
                    Rotate.setEnabled(false);
                    rightMove.setEnabled(false);
                    downMove.setEnabled(false);
//                    toBottom.setEnabled(false);
                    mediaMusci.pauseVoic();
                }
            }
            /*游戏结束时，禁用其他按钮*/
            public void setGmaeOver(){
                if(ifGameOver){

                    Pause.setEnabled(false);
                    leftMove.setEnabled(false);
                    Rotate.setEnabled(false);
                    rightMove.setEnabled(false);
                    downMove.setEnabled(false);
                    upMove.setEnabled(false);
//                    toBottom.setEnabled(false);
                    mediaMusci.stopVoice();
                }
            }

            /*处理消行*/
            public void cleanLine() {
                boolean ifclean;
                for (int y = map[0].length - 1; y > 0; y--) {
                    ifclean = true;
                    for (int x = 0; x < map.length; x++) {
                        //如果有一个块都是true,消行
                        if (!map[x][y]) {
                            ifclean = false;
                        }
                    }
                    //执行消行
                    if (ifclean) {
                        //执行消行,把要消掉的行以上的内容都下移一行
                        for (int nowy = y; nowy > 0; nowy--) {
                            for (int x = 0; x < map.length; x++) {
                                map[x][nowy] = map[x][nowy - 1];
                            }
                        }
                        y++;
                    }
                }

            }

            /*判断游戏是否结束*/
            public boolean checkIfGameOver() {
                for (int i = 0; i < boxs.length; i++) {
                    if (map[boxs[i].x][boxs[i].y])
                        return true;
                }
                return false;
            }

            public void initListener() {
                leftMove.setOnClickListener(this);
                Rotate.setOnClickListener(this);
                rightMove.setOnClickListener(this);
                downMove.setOnClickListener(this);
                upMove.setOnClickListener(this);
//                toBottom.setOnClickListener(this);
                Pause.setOnClickListener(this);
                ToFix.setOnClickListener(this);
                musicPlay.setOnClickListener(this);
                reStart.setOnClickListener(this);
                backMenu.setOnClickListener(this);
            }

            public void initThread() {
                downThread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        while (true) {
                            //延迟0，5秒后执行一次下落
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (ifPause || ifGameOver)
                                continue;
                            downMove();
                            //使用handler，让线程循环起来
                            handler.sendEmptyMessage(0);
                        }
                    }
                };
                downThread.start();
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.leftMove:
                        Toast.makeText(this, "点击了左", Toast.LENGTH_SHORT).show();
                        move(-1, 0);
                        break;
                    case R.id.Rotate:
                        rotate();
                        break;
                    case R.id.rightMove:
                        Toast.makeText(this, "右移", Toast.LENGTH_SHORT).show();
                        move(1, 0);
                        break;
                    case R.id.downMove:
//                move(0,1);
                        downMove();
                        break;
                    case R.id.upMove:
                        move(0,-1);
                        break;
                    case R.id.toBottom:
//                Toast.makeText(this, "快速下落", Toast.LENGTH_SHORT).show();
                        while (true) {
                            if (!downMove()) {
                                break;
                            }
                        }
                        break;
                    case R.id.Pause:
                        setPause();
                        break;
                    case R.id.toFix:
                        /*固定Tetris*/
                        fixAndCheckTetris();
                        Toast.makeText(this, "点了固定", Toast.LENGTH_SHORT).show();
                        break;
                        /*重新加载activity*/
                    case R.id.reStart:
                        //刷新当前activity
                        Intent intentRe = getIntent();
                        finish();
                        startActivity(intentRe);
                        break;
                        /*返回主菜单*/
                    case R.id.backMenu:
                        finish();
                        break;
                }
                //点击按钮后，要重新绘制控件
                view.invalidate();
            }

            /*判断是否正确*/
            public boolean fixAndCheckTetris() {
                for (int i = 0; i < 4; i++) {
                    if (boxs[i].x != coordinateTwo[i][0] || boxs[i].y != coordinateTwo[i][1]) {
                        for (int j = 0; j < 4; j++) {
                            //将对应的maps块置为true
                            map[boxs[j].x][boxs[j].y] = true;
                            unusableBoxs.add(new Point(boxs[j].x, boxs[j].y));
                            withBitMap.add(new Point(coordinateTwo[j][0], coordinateTwo[j][1]));
                        }
                        /*生成新块*/
                        copyTetrisToMap(generatePoint);
                        return false;
                    }
                }
                /*正确，不固定，修改对应块的标记*/
                for (int i = 0; i < 4; i++) {
                    ifShowPicture[boxs[i].x][boxs[i].y] = true;
                    /*加分*/
                    score++;
                    scoreView.setText(Integer.toString(score));
                    /*设置最高分*/
                    SetMostScore(Mode);

                }

                copyTetrisToMap(generatePoint);
                return true;
            }

            /*正确，不固定快，画上该区域对应的全部图片*/
            public void DrawRightPicture(int x, int y, Canvas canvas) {
                //要绘制的区域
                Rect src = new Rect(0, 0, ImagePiece.sourceBitmap.getWidth(), ImagePiece.sourceBitmap.getHeight());
                Rect dst = new Rect(x * boxSize, y * boxSize, x * boxSize + boxSize, y * boxSize + boxSize);
                canvas.drawBitmap(ImagePiece.sourceBitmap, src, dst, new Paint());

                /*得分加一*/
                score++;
                scoreView.setText(Integer.toString(score));
            }

            public boolean move(int x, int y) {
                for (int i = 0; i < boxs.length; i++) {
                    //判断移动后是否会出界
                    if (checkBorder(boxs[i].x + x, boxs[i].y + y)) {
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
            public boolean rotate() {
                //正方形方块无需旋转
                if (boxType == 0)
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
            public boolean downMove() {
                //移动
                if (move(0, 1)) {
                    return true;
                }
                //把方块所在区域置为true
                for (int i = 0; i < boxs.length; i++) {
                    map[boxs[i].x][boxs[i].y] = true;
//            unusableBoxs.add(boxs[i]);
                    unusableBoxs.add(new Point(boxs[i].x, boxs[i].y));
                    withBitMap.add(new Point(coordinateTwo[i][0], coordinateTwo[i][1]));
                }
                //判断是否消行
//                cleanLine();
                //生成新方块
                copyTetrisToMap(generatePoint);
                //判断游戏是否结束
                ifGameOver = checkIfGameOver();
                return false;
            }

            /*边界判断，防止出界和触底重合*/
            public boolean checkBorder(int x, int y) {
                return (x < 0 || y < 0 || x >= map.length || y >= map[0].length || map[x][y]);
            }


            /*随机选一个TetrisList中的Tetris，把其内容绘制在地图顶部*/
            public boolean copyTetrisToMap(Point generatePoint) {
                Random random = new Random();
                int indexAll = ImagePiece.TetrisList.size();
                Log.i("index","indexALL:"+indexAll);
                TetrisIndex = random.nextInt(indexAll);

                int index = TetrisIndex;
                int indexflag = index;
                int cycle = 0;
                //判断该Tetris是否可用,可用，跳出，不可用，加1继续判断
                while (true) {
                    if (ImagePiece.usableTetris.get(index)) {
                        ImagePiece.usableTetris.set(index, false);
                        break;
                    }
                    if (index < indexAll)
                        index++;
                    else {
                        index = 0;
                    }
                    //循环indexALL次数后，直接退出,说明当前没有能用的Tetris了，游戏通关！;
                    cycle++;
                    if (cycle == indexAll) {
                        ifGmaeOK = true;
                        return true;
                    }
                }
                int[][] coordinate = new int[][]{new int[]{0, 1}, new int[]{0, 1}, new int[]{0, 1}, new int[]{0, 1}};
                Log.i("num", "当前Tetris数:" + index);
                for (int i = 0; i < 4; i++) {
                    coordinate[i][0] = ImagePiece.TetrisList.get(index)[i][0];
                    coordinate[i][1] = ImagePiece.TetrisList.get(index)[i][1];
                    coordinateTwo[i][0] = coordinate[i][0];
                    coordinateTwo[i][1] = coordinate[i][1];

                }
                int DeltX = coordinate[0][0] - generatePoint.x;
                int DeltY = coordinate[0][1] - generatePoint.y;
                //将Tetris的坐标平移到地图中待生成的位置
                for (int i = 0; i < 4; i++) {
                    coordinate[i][0] -= DeltX;
                    coordinate[i][1] -= DeltY;
                    boxs[i].x = coordinate[i][0];
                    boxs[i].y = coordinate[i][1];
                }
                /*随机转一下*/
                RotateWhenGenerate(boxs);
                return false;
            }
            /*将生成的块随机选择几次，增加难度和随机性*/
    public void RotateWhenGenerate(Point[] boxs){
        Random random = new Random();
        int num = random.nextInt(4);
        for (int j = 0; j < num; j++) {
            for (int i = 0; i < 4; i++) {
                //顺时针旋转90°
                int rotateX = -boxs[i].y + boxs[0].y + boxs[0].x;
                int rotateY = boxs[i].x - boxs[0].x + boxs[0].y;
                boxs[i].x = rotateX;
                boxs[i].y = rotateY;
            }
        }

    }
            /*获取当前难度下的最高分*/
    public void GetMostScore(String Mode){
        SharedPreferences myPreferences = getSharedPreferences("MostScore", MODE_PRIVATE);
         Mostscore = myPreferences.getInt(Mode , 0);
         mostScoreView.setText(Integer.toString(Mostscore));
    }
    /*如果最高分刷新了，要重新写入*/
    public void SetMostScore(String Mode){
        if (score > Mostscore){
            Mostscore = score;
            SharedPreferences sharedPreferences = getSharedPreferences("MostScore", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Mode,Mostscore );
            editor.commit();
            mostScoreView.setText(Integer.toString(Mostscore));
        }
    }

            /*绘制辅助线*/
            public void DrawLines(Canvas canvas){
                //绘制辅助线
                for (int x = 0; x < map.length; x++) {
                    canvas.drawLine(x * boxSize, 0, x * boxSize, view.getHeight(), linePaint);
                }
                for (int y = 0; y < map[0].length; y++) {
                    canvas.drawLine(0, y * boxSize, view.getWidth(), y * boxSize, linePaint);
                }
            }

            /*绘制地图中的Tetris*/
            public void DrawBoxs(Point[] boxs, Canvas canvas) {
                //绘制图案，
                for (int i = 0; i < 4; i++) {
                    canvas.drawBitmap(ImagePiece.bitmapBoxs[coordinateTwo[i][0]][coordinateTwo[i][1]], boxs[i].x * boxSize, boxs[i].y * boxSize, new Paint());
                    Log.i("coordinateTwo", "draw时的coordinateTWO" + coordinateTwo[i][0] + coordinateTwo[i][1]);
                }
            }

            /*绘制底层背景*/
            public void DrawBackground(Canvas canvas) {
                Paint paint = new Paint();
                paint.setAlpha(70);
                canvas.drawBitmap(ImagePiece.sourceBitmap, 0, 0, paint);
            }

            /*绘制地图，目的是保留触底后的背景图片内容,根据当前下标，找出该不可用块在List中的位置，根据位置找到对应bitmap坐标，然后绘制*/
            public void DrawUnableBoxsInMap(int x, int y, Canvas canvas) {
                int index = 2;
                for (int i = 0; i < unusableBoxs.size(); i++) {
                    if (unusableBoxs.get(i).x == x && unusableBoxs.get(i).y == y) {
                        index = i;
                    }
                }
                int bitmapX = withBitMap.get(index).x;
                int bitmapY = withBitMap.get(index).y;
                canvas.drawBitmap(ImagePiece.bitmapBoxs[bitmapX][bitmapY], x * boxSize, y * boxSize,new Paint());
            }

            /*绘制游戏暂停提示*/
           public void DrawWhenPause(Canvas canvas){
              statePaint.setColor(0xFF27D600);

               if(ifPause)
                   canvas.drawText("暂停",viewWidth/2-statePaint.measureText("暂停")/2,viewHeight/2,statePaint);
           }

           /*绘制游戏结束提示*/
           public void DrawWhenGameOver(Canvas canvas){
               statePaint.setTextSize(200);
               statePaint.setColor(0xFF254900);
               if(ifGameOver)
                   canvas.drawText("游戏结束",viewWidth/2-statePaint.measureText("游戏结束")/2,viewHeight/2,statePaint);
           }
           /*绘制游戏通关*/
          public void DrawWhenGameOK(Canvas canvas){

        if(ifGmaeOK) {
            statePaint.setTextSize(200);
            statePaint.setColor(0x3C00FF00);
            canvas.drawText("恭喜通关！", viewWidth / 2 - statePaint.measureText("恭喜通关！") / 2, viewHeight / 2, statePaint);
        }
    }


    }