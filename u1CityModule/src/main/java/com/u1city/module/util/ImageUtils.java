package com.u1city.module.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.u1city.module.common.Debug;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author Linjy
 * @time 2015-5-5 10:47:00
 * @类说明 图片的处理工具类
 * 
 *      <pre>
 * 1.图片保存 
 * 2.Bitmap转换64位二进制编码 
 * 3.写图片文件到SD卡 
 * 4.根据文件路径获取bitmap
 * 5.根据文件获取bitmap 
 * 6.Bitmap转换bytes 
 * 7.图片质量压缩 
 * 8.压缩比例大小 
 * 9.使用当前时间戳拼接一个唯一的文件名
 * 10.获取照相机使用的目录 
 * 11.判断当前Url是否标准的content://样式，如果不是，则返回绝对路径 
 * 12.通过uri获取文件的绝对路径
 * 13.获取图片缩略图 只有Android2.1以上版本支持 
 * 14.获取SD卡中最新图片路径 
 * 15.计算缩放图片的宽高 
 * 16.创建缩略图
 * 17.放大缩小图片 
 * 18.(缩放)重绘图片
 * 19.将Drawable转化为Bitmap 
 * 20.获得圆角图片的方法 
 * 21.获得带倒影的图片方法
 * 22.获取原始的图片尺寸后，根据目标计算缩放比例系数 
 * 23.Bytes转化为Bimap
 * 24.在imageview中显示Resources超大图片
 * 25.毛玻璃效果
 * </pre>
 */
@SuppressWarnings("deprecation")
public class ImageUtils
{

    public final static String SDCARD_MNT = "/mnt/sdcard";
    public final static String SDCARD = "/sdcard";

    /** 请求相册 */
    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
    /** 请求相机 */
    public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
    /** 请求裁剪 */
    public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;

    /**
     * 1.写图片文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     * 
     * @throws IOException
     */
    public static void saveImage(Context context, String fileName, Bitmap bitmap) throws IOException
    {
        saveImage(context, fileName, bitmap, 100);
    }

    public static void saveImage(Context context, String fileName, Bitmap bitmap, int quality) throws IOException
    {
        if (bitmap == null || fileName == null || context == null)
            return;

        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, stream);
        byte[] bytes = stream.toByteArray();
        fos.write(bytes);
        fos.close();
    }

    /**
     * 2.Bitmap转换64位二进制编码
     */
    public static String bitmapToString(Bitmap bm)
    {
        return bitmapToString(bm, 100);
    }

    /**
     * 2.Bitmap转换64位二进制编码
     */
    public static String bitmapToString(Bitmap bm, int quality)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            baos.flush();
            baos.close();
            byte[] appicon = baos.toByteArray();// 转为byte数组
            return Base64.encodeToString(appicon, 0, appicon.length, Base64.DEFAULT);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("", "Bitmap转换64位二进制编码");
        }
        return null;
    }

    /**
     * 3.写图片文件到SD卡
     * 
     * @throws IOException
     */
    public static void saveImageToSD(String filePath, Bitmap bitmap, int quality) throws IOException
    {
        if (bitmap != null)
        {
            FileOutputStream fos = new FileOutputStream(filePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, quality, stream);
            byte[] bytes = stream.toByteArray();
            fos.write(bytes);
            fos.close();
        }
    }

    /**
     * 根据文件路径获取bitmap
     * 
     * @param context
     * @param fileName
     * @return
     */
    /*
     * public static Bitmap getBitmap(Context context, String fileName) {
     * FileInputStream fis = null; Bitmap bitmap = null; try { fis =
     * context.openFileInput(fileName); bitmap =
     * BitmapFactory.decodeStream(fis); } catch (FileNotFoundException e) {
     * e.printStackTrace(); } catch (OutOfMemoryError e) { e.printStackTrace();
     * } finally { try { fis.close(); } catch (Exception e) { } } return bitmap;
     * }
     */

    /**
     * 4.根据文件路径获取bitmap
     * 
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapByPath(String filePath)
    {
        return getBitmapByPath(filePath, null);
    }

    public static Bitmap getBitmapByPath(String filePath, BitmapFactory.Options opts)
    {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try
        {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis, null, opts);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fis.close();
            }
            catch (Exception e)
            {
            }
        }
        return bitmap;
    }

    /**
     * 5.根据文件获取bitmap
     * 
     * @param file
     * @return
     */
    public static Bitmap getBitmapByFile(File file)
    {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try
        {
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fis.close();
            }
            catch (Exception e)
            {
            }
        }
        return bitmap;
    }

    /**
     * 6.Bitmap转换bytes
     * 
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap getimage(String srcPath)
    {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww)
        {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        }
        else if (w < h && h > hh)
        {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /** 读取照片exif信息中的旋转角度
     * @param path 照片路径
     * @return 旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片旋转一定角度
     * @param img 旋转的图片
     * @param degree 旋转的角度
     * @return 旋转后的图片
     * */
    public static Bitmap rotateBitmap(Bitmap img, int degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree); /*翻转90度*/
        int width = img.getWidth();
        int height =img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    /**
     * 7.图片质量压缩
     * 
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100)
        { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 8.压缩比例大小
     * 
     * @param image
     * @return
     * 
     */
    public static Bitmap comp(Bitmap image)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        Debug.println("...............comp..........11......>" + baos.toByteArray().length / 1024);
        if (baos.toByteArray().length / 1024 > 32)
        {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.PNG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
            Debug.println("...............comp........22........>" + baos.toByteArray().length / 1024);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww)
        {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        }
        else if (w < h && h > hh)
        {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 9.使用当前时间戳拼接一个唯一的文件名
     * 
     * @return
     */
    public static String getTempFileName()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
        String fileName = format.format(new Timestamp(System.currentTimeMillis()));
        return fileName;
    }

    /**
     * 10.获取照相机使用的目录
     * 
     * @return
     */
    public static String getCamerPath()
    {
        return Environment.getExternalStorageDirectory() + File.separator + "FounderNews" + File.separator;
    }

    /**
     * 11.判断当前Url是否标准的content://样式，如果不是，则返回绝对路径
     * 
     * @param mUri
     * @return
     */
    public static String getAbsolutePathFromNoStandardUri(Uri mUri)
    {
        String filePath = null;

        String mUriString = mUri.toString();
        mUriString = Uri.decode(mUriString);

        String pre1 = "file://" + SDCARD + File.separator;
        String pre2 = "file://" + SDCARD_MNT + File.separator;

        if (mUriString.startsWith(pre1))
        {
            filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring(pre1.length());
        }
        else if (mUriString.startsWith(pre2))
        {
            filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring(pre2.length());
        }
        return filePath;
    }

    /**
     * 12.通过uri获取文件的绝对路径
     * 
     * @param uri
     * @return
     */

    public static String getAbsoluteImagePath(Activity context, Uri uri)
    {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, proj, // Which columns to
                                                        // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        if (cursor != null)
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst())
            {
                imagePath = cursor.getString(column_index);
            }
        }else{
            ToastUtil.showToastLong(context, "获取不了图片，可能此图片没有保存在本地");
        }

        return imagePath;
    }

    /**
     * 13.获取图片缩略图 只有Android2.1以上版本支持
     * 
     * @param imgName
     * @param kind
     *            MediaStore.Images.Thumbnails.MICRO_KIND
     * @return
     */
    public static Bitmap loadImgThumbnail(Activity context, String imgName, int kind)
    {
        Bitmap bitmap = null;

        String[] proj = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME};

        Cursor cursor = context.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'", null, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
        {
            // ContentResolver crThumb = context.getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            // bitmap = MethodsCompat.getThumbnail(crThumb, cursor.getInt(0),
            // kind, options);
        }
        return bitmap;
    }

    public static Bitmap loadImgThumbnail(String filePath, int w, int h)
    {
        Bitmap bitmap = getBitmapByPath(filePath);
        return zoomBitmap(bitmap, w, h);
    }

    /**
     * 14.获取SD卡中最新图片路径
     * 
     * @return
     */
    public static String getLatestImage(Activity context)
    {
        String latestImage = null;
        String[] items = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null, null, MediaStore.Images.Media._ID + " desc");

        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                latestImage = cursor.getString(1);
                break;
            }
        }

        return latestImage;
    }

    /**
     * 15.计算缩放图片的宽高
     * 
     * @param img_size
     * @param square_size
     * @return
     */
    public static int[] scaleImageSize(int[] img_size, int square_size)
    {
        if (img_size[0] <= square_size && img_size[1] <= square_size)
            return img_size;
        double ratio = square_size / (double) Math.max(img_size[0], img_size[1]);
        return new int[] {(int) (img_size[0] * ratio), (int) (img_size[1] * ratio)};
    }

    /**
     * 16.创建缩略图
     * 
     * @param context
     * @param largeImagePath
     *            原始大图路径
     * @param thumbfilePath
     *            输出缩略图路径
     * @param square_size
     *            输出图片宽度
     * @param quality
     *            输出图片质量
     * @throws IOException
     */
    public static void createImageThumbnail(Context context, String largeImagePath, String thumbfilePath, int square_size, int quality) throws IOException
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        // 原始图片bitmap
        Bitmap cur_bitmap = getBitmapByPath(largeImagePath, opts);

        if (cur_bitmap == null)
            return;

        // 原始图片的高宽
        int[] cur_img_size = new int[] {cur_bitmap.getWidth(), cur_bitmap.getHeight()};
        // 计算原始图片缩放后的宽高
        int[] new_img_size = scaleImageSize(cur_img_size, square_size);
        // 生成缩放后的bitmap
        Bitmap thb_bitmap = zoomBitmap(cur_bitmap, new_img_size[0], new_img_size[1]);
        // 生成缩放后的图片文件
        saveImageToSD(thumbfilePath, thb_bitmap, quality);
    }

    /**
     * 17.放大缩小图片
     * 
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h)
    {
        Bitmap newbmp = null;
        if (bitmap != null)
        {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidht = ((float) w / width);
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidht, scaleHeight);
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        return newbmp;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap)
    {
        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 定义预转换成的图片的宽度和高度
        int newWidth = 200;
        int newHeight = 200;
        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        // 旋转图片 动作
        // matrix.postRotate(45);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    /**
     * 18.(缩放)重绘图片
     * 
     * @param context
     *            Activity
     * @param bitmap
     * @return
     */
    public static Bitmap reDrawBitMap(Activity context, Bitmap bitmap)
    {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // int rHeight = dm.heightPixels;
        int rWidth = dm.widthPixels;
        // float rHeight=dm.heightPixels/dm.density+0.5f;
        // float rWidth=dm.widthPixels/dm.density+0.5f;
        // int height=bitmap.getScaledHeight(dm);
        // int width = bitmap.getScaledWidth(dm);
        // int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float zoomScale;
        /** 方式1 **/
        // if(rWidth/rHeight>width/height){//以高为准
        // zoomScale=((float) rHeight) / height;
        // }else{
        // //if(rWidth/rHeight<width/height)//以宽为准
        // zoomScale=((float) rWidth) / width;
        // }
        /** 方式2 **/
        // if(width*1.5 >= height) {//以宽为准
        // if(width >= rWidth)
        // zoomScale = ((float) rWidth) / width;
        // else
        // zoomScale = 1.0f;
        // }else {//以高为准
        // if(height >= rHeight)
        // zoomScale = ((float) rHeight) / height;
        // else
        // zoomScale = 1.0f;
        // }
        /** 方式3 **/
        if (width >= rWidth)
            zoomScale = ((float) rWidth) / width;
        else
            zoomScale = 1.0f;
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(zoomScale, zoomScale);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 19.将Drawable转化为Bitmap
     * 
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * 20.获得圆角图片的方法
     * 
     * @param bitmap
     * @param roundPx
     *            一般设成14
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx)
    {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 21.获得带倒影的图片方法
     * 
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap)
    {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 22.将bitmap转化为drawable
     * 
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap)
    {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    /**
     * 22.获取原始的图片尺寸后，根据目标计算缩放比例系数
     * 
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return 计算图片大小
     * 
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }

    /**
     * 
     * @param image
     * @return
     * 
     */
    public static Bitmap compressBmpFromBmp(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        Debug.println("............compressBmpFromBmp......1111.........>" + baos.toByteArray().length / 1024);
        while (baos.toByteArray().length / 1024 > 32)
        {
            baos.reset();
            options -= 10;
            Debug.println(".................options........>" + options);
            image.compress(Bitmap.CompressFormat.PNG, options, baos);
            Debug.println("............compressBmpFromBmp......2222.........>" + baos.toByteArray().length / 1024);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * 23.Bytes转化为Bimap
     * 
     * @param b
     * @return
     * 
     */
    public static Bitmap Bytes2Bimap(byte[] b)
    {
        if (b.length != 0)
        {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        else
        {
            return null;
        }
    }

    /**
     * 24.在imageview中显示Resources超大图片
     * 
     * @param res
     * @param resId
     * @param reqWidth
     *            图片宽
     * @param reqHeight图片高
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        // 例如这样使用
        // mImageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(),
        // R.id.myimage, 100, 100));
        return BitmapFactory.decodeResource(res, resId, options);
    }
    
    /**
     * 相机拍照
     * 
     * @param output
     */
    public static void startActionCamera(Activity context,Uri output, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        context.startActivityForResult(intent, requestCode);
    }
    
    /**
     * 选择图片裁剪
     * 
     * @param output
     */
    public static void startActionPickCrop(Activity context,Uri output, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra("output", output); 
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 3);// 裁剪框比例
        intent.putExtra("aspectY", 2);
        intent.putExtra("outputX", DimensUtil.getDisplayWidth(context));// 输出图片大小
        intent.putExtra("outputY", DimensUtil.getDisplayHeight(context)/3);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        context.startActivityForResult(Intent.createChooser(intent, "选择图片"), requestCode);
    }
    
    /**
     * 拍照后裁剪
     * @param data
     *            原始图片
     * @param output
     *            裁剪后图片
     */
    public static void startActionCrop(Activity context,Uri data, Uri output, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", output);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX",3);// 裁剪框比例
        intent.putExtra("aspectY", 2);
        intent.putExtra("scale",true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputX", DimensUtil.getDisplayWidth(context));// 输出图片大小
        intent.putExtra("outputY", DimensUtil.getDisplayHeight(context)/3);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 创建View的图像
     * @param view 要创建图像的View
     * @param ratio view的比例 width/height
     * @return 要创建的图像
     */
    public static Bitmap createViewBitmap(View view, double ratio)
    {
        int i = view.getWidth();
        int j = (int)(i * ratio);
        Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
        Bitmap localBitmap = Bitmap.createBitmap(i, j, localConfig);
        Canvas localCanvas = new Canvas(localBitmap);
        view.draw(localCanvas);
        return localBitmap;
    }
    
    /**
     * 
     *  设置毛玻璃效果
     * <p>
     *  详细说明
     * <p>
     * 
     * @author Administrator
     * @param context
     * @param sentBitmap
     * @param radius
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {

        
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
//        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

//        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }
}
