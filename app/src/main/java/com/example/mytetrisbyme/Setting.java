package com.example.mytetrisbyme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class Setting extends AppCompatActivity implements View.OnClickListener {

    ImageButton preImage, nextImage, backMenu, updateImage;
    CheckBox checkBox;
    ImageView showimage;
    int[] ImageArray;
    int selectedImage = 0;
    int nowShow;
    Uri uri;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        /*获取图片数组*/
        TypedArray typedArray = getResources().obtainTypedArray(R.array.Images);
        ImageArray = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            ImageArray[i] = typedArray.getResourceId(i, 0);
        }

        /*获取当前选择图片的下标*/
        SharedPreferences myPreferences = getSharedPreferences("selectedImage", MODE_PRIVATE);
        selectedImage = myPreferences.getInt("index", 0);
        nowShow = selectedImage;
        Log.i("num", "nowshow是" + nowShow);
        initView();
    }

    /*获取控件，监听事件*/
    public void initView() {
        preImage = (ImageButton) findViewById(R.id.preImage);
        nextImage = (ImageButton) findViewById(R.id.nextImage);
        backMenu = (ImageButton) findViewById(R.id.backMenu);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        showimage = (ImageView) findViewById(R.id.showImage);
        updateImage = (ImageButton) findViewById(R.id.updateImage);
        preImage.setOnClickListener(this);
        nextImage.setOnClickListener(this);
        backMenu.setOnClickListener(this);
        checkBox.setOnClickListener(this);
        updateImage.setOnClickListener(this);
//        showimage.setBackgroundResource(ImageArray[nowShow]);
        setShowimageBackground(nowShow);
        checkBox.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preImage:
                changeImage(-1);
                break;
            case R.id.nextImage:
                changeImage(1);
                break;
            case R.id.checkBox:
                /*点击后，修改selectImage*/
                selectedImage = nowShow;
                break;
            case R.id.backMenu:
                /*返回主菜单*/
                finish();
                break;
            case R.id.updateImage:
                /*上传图片*/
                updateImageFromGallery();
                break;
        }
    }
//    /*按下按钮后的操作*/
//    public void changeImage(int x){
//        if (nowShow+x >= ImageArray.length)
//            nowShow = 0;
//        else if (nowShow+x <0)
//            nowShow = ImageArray.length -1;
//           else
//               nowShow+=x;
//        Log.i("num","nowshow是"+nowShow);
//        showimage.setBackgroundResource(ImageArray[nowShow]);
//        Toast.makeText(this, "ID"+ImageArray[nowShow], Toast.LENGTH_SHORT).show();
//        if (nowShow == selectedImage){
//            checkBox.setChecked(true);
//        }
//        else checkBox.setChecked(false);
//    }

    /*上传图片*/
    public void updateImageFromGallery() {
        //设置裁剪比例
        Toast.makeText(this, "点击了上传按钮", Toast.LENGTH_SHORT).show();
        CameraActivity.setClipRatio(1, 2);
        startActivity(new Intent(Setting.this, CameraActivity.class).putExtra(CameraActivity.ExtraType, CameraActivity.PHOTO));
        Log.d("Uri","没有打开相册啊");
    }


    /*按下按钮后的操作*/
    public void changeImage(int x) {
        Log.d("num","LIST是"+BitmapUtils.UriList.size());
        if (nowShow + x >= BitmapUtils.UriList.size())
            nowShow = 0;
        else if (nowShow + x < 0)
            nowShow = BitmapUtils.UriList.size() - 1;
        else
            nowShow += x;
        Log.i("num", "nowshow是" + nowShow);

//        showimage.setImageBitmap(BitmapUtils.createBitmapByUri(BitmapUtils.UriList.get(nowShow), this));
//        Bitmap bitmap1 = BitmapUtils.createBitmapByUri(BitmapUtils.UriList.get(nowShow),this);
//        /*将位图转为drawable资源，再设置背景*/
         setShowimageBackground(nowShow);
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap1);
//        showimage.setBackground(bitmapDrawable);

        Toast.makeText(this, "ID" + BitmapUtils.UriList.get(nowShow), Toast.LENGTH_SHORT).show();
        if (nowShow == selectedImage) {
            checkBox.setChecked(true);
        } else checkBox.setChecked(false);
    }
    public void setShowimageBackground(int nowShow){
        Bitmap bitmap1 = BitmapUtils.createBitmapByUri(BitmapUtils.UriList.get(nowShow),this);
        /*将位图转为drawable资源，再设置背景*/
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap1);
        showimage.setBackground(bitmapDrawable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("selectedImage", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("index", selectedImage);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*获得相册返回结果，图片的uri*/
        if (CameraActivity.LISTENING) {
//            Log.e("TAG", "返回的Uri结果：" + CameraActivity.IMG_URI);
//            Log.e("TAG", "返回的File结果：" + CameraActivity.IMG_File.getPath());
            CameraActivity.LISTENING = false;   //关闭获取结果
            uri = CameraActivity.IMG_URI;
            Toast.makeText(this, "URI" + uri, Toast.LENGTH_SHORT).show();
//            IMG.setImageURI(CameraActivity.IMG_URI);  //显示图片到控件
        }

        /*生成Bitmap*/
        if (uri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*将Bitmap存到本地*/
            int lastindex = BitmapUtils.UriList.size();
            String name = "picture"+Integer.toString(lastindex)+".jpg";
            BitmapUtils.saveBitmap(name, bitmap, this);
            /*更新LIST*/
            BitmapUtils.UriList.add(uri);
        }
    }
}