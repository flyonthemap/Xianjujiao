package xidian.xianjujiao.com.utils;

/**
 * Created by flyonthemap on 16/8/8.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.application.BaseApplication;


public class PicassoUtils {
    // 注意这里不能将Target对象声明为局部变量，可以避免Target的创建和销毁而产生的额外代价
    private static Target target;


    //从path中加载文件，放入ImagineView

    public static void loadImageWithSize(String path, int width, int height, ImageView imageView) {

        Picasso.with(BaseApplication.getApplication()).load(path).
                placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).resize(width, height).centerCrop().
                into(imageView);
    }

    public static void loadImageWithHolder(String path, int resID, ImageView imageView) {
        Picasso.with(BaseApplication.getApplication()).load(path).fit().placeholder(resID).into(imageView);
    }

    public static void loadImageWithCrop(String path, ImageView imageView) {
        Picasso.with(BaseApplication.getApplication()).load(path).transform(new CropSquareTransformation()).
                into(imageView);
    }

    /**
     * 实现对图片的自定义裁剪
     */
    public static class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != null){
                source.recycle();;
            }
            return result;
        }


        @Override
        public String key() {
            return "square()";
        }
    }


}
