package com.example.mytetrisbyme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ChooseDifficulty extends AppCompatActivity implements View.OnClickListener {

    Button normalMode,difficultyMode,harMode;
    ImageButton backMenu;
    Intent intent = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosedifficulty);
        initButton();


    }

    public void initButton(){
        normalMode = findViewById(R.id.normalMode);
        difficultyMode = findViewById(R.id.difficultyMode);
        harMode = findViewById(R.id.hardMode);
        backMenu = findViewById(R.id.backMenu);
        normalMode.setOnClickListener(this);
        difficultyMode.setOnClickListener(this);
        harMode.setOnClickListener(this);
        backMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.normalMode:
                intent.putExtra("Mode","normal");
                break;
            case R.id.difficultyMode:
                intent.putExtra("Mode","difficulty");
                Toast.makeText(this, "点击了difficult", Toast.LENGTH_SHORT).show();
                break;
            case R.id.hardMode:
                intent.putExtra("Mode","hard");
                Toast.makeText(this, "点击了hard", Toast.LENGTH_SHORT).show();
                break;
            case  R.id.backMenu:
                intent.setClass(this,ImagePiece.class);
                startActivity(intent);
                finish();
                break;
        }
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}