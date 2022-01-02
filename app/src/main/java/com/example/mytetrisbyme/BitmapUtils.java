package com.example.mytetrisbyme;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BitmapUtils {

    static String TargetPath = new String();
    static List<Uri> UriList = new ArrayList<Uri>();

    static void saveBitmap(String name, Bitmap bm, Context mContext) {
        Log.d("Save Bitmap", "Ready to save picture");
        //指定我们想要存储文件的地址
        TargetPath =  Environment.getExternalStorageDirectory().getPath() + "/MytetrisImages/";
        Log.d("Save Bitmap", "Save Path=" + TargetPath);
        //判断指定文件夹的路径是否存在
        if (!fileIsExist(TargetPath)) {
            Log.d("Uri", "TargetPath isn't exist");
        } else {
            //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
            File saveFile = new File(TargetPath, name);

            try {
                FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                // compress - 压缩的意思
                bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                //存储完成后需要清除相关的进程
                saveImgOut.flush();
                saveImgOut.close();
                Log.d("Save Bitmap", "The picture is save to your phone!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    static boolean fileIsExist(String fileName)
    {
        //传入指定的路径，然后判断路径是否存在
        File file=new File(fileName);
        if (file.exists())
            return true;
        else{
            //file.mkdirs() 创建文件夹的意思
            return file.mkdirs();
        }
    }

    /*遍历文件夹，获取图片地址*/
    static void getPicturesUri(){
        File   TargetDirectory = new File(TargetPath);
        if (TargetDirectory.isDirectory()) {
            for (File file : TargetDirectory.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")) {
                        UriList.add(Uri.fromFile(new File(path)));

                }
            }
        }
        for (int i = 0; i < UriList.size(); i++) {
            Log.d("Uri","URI是"+ UriList.get(i));
        }
    }

    /*根据uri创建Bitmap*/
    public static Bitmap createBitmapByUri(Uri uri,Context context){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
