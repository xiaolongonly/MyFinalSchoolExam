/*
 * 系统: Android_LaiDianYi
 * 文件名: MagnifyImageSaveTool.java
 * 版权: U1CITY Corporation 2015
 * 描述: 图片放大保存(放大多张图片PAGE滚动)
 * 创建人: Tianbf
 * 创建时间: 2015-12-22 上午9:41:00
 */
package com.u1city.module.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.u1city.module.R;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.common.Debug;
import com.u1city.module.model.BaseModel;
import com.u1city.module.util.SimpleImageOption;
import com.u1city.module.util.StringUtils;
import com.u1city.module.widget.ShopGuideSaveDialog.DialogShopGuideSave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** 注意：如果MODEL不是BaseModel请转换成BaseModel再使用,类似分享那样 */
public class MagnifyImageSaveTool implements OnClickListener, OnPageChangeListener, DialogShopGuideSave
{
    private static final String TAG = "MagnifyImageSaveTool";
    private Context context;
    private View viewlayout;
    private LayoutInflater inflater;
    private ArrayList<View> views = new ArrayList<View>();
    protected List<BaseModel> datas;
    private TextView numTv;
    private String photoUrl;
    private int position = 0;
    private DisplayImageOptions imagesOption = SimpleImageOption.create(R.drawable.list_loading_goods2);
    private AllPopupWindows allPopupWindows;

    Handler handler = new Handler()
    {
        private LoadingDialog dialog;

        public void handleMessage(android.os.Message msg)
        {
            switch (msg.arg1)
            {
                case 0:
                    Debug.e(TAG, "----------------handler-------1------------->" + msg.arg1);
                    dialog = new LoadingDialog((BaseActivity) context);
                    dialog.show();
                    break;
                case 1:
                    dialog.dismiss();
                    Debug.e(TAG, "----------------handler-------2------------->" + msg.arg1);
                    break;

                default:
                    break;
            }
        }
    };

    public MagnifyImageSaveTool()
    {
    }

    public void setDatas(Context context, List<BaseModel> datas, int position)
    {
        this.context = context;
        this.datas = datas;
        this.position = position;
        inflater = LayoutInflater.from(context);

        initPage();
        initView();
    }

    public void setShowDown(View v)
    {
        if (allPopupWindows != null)
        {
            allPopupWindows.showDown(v);
        }
    }

    private void initView()
    {
        allPopupWindows = new AllPopupWindows(context, viewlayout);

    }

    private void initPage()
    {
        views.clear();
        if (viewlayout == null)
        {
            viewlayout = inflater.inflate(R.layout.activity_photon, null);
        }
        numTv = (TextView) viewlayout.findViewById(R.id.tv_num);
        Button photo_bt_exit = (Button) viewlayout.findViewById(R.id.photo_bt_exit);
        photo_bt_exit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                allPopupWindows.dismiss();
            }
        });

        // RelativeLayout container = (RelativeLayout) viewlayout.findViewById(R.id.container);
        // container.setOnClickListener(new OnClickListener()
        // {
        // @Override
        // public void onClick(View v)
        // {
        // allPopupWindows.dismiss();
        // }
        // });

        ViewPager viewPager = (ViewPager) viewlayout.findViewById(R.id.viewpager);
        // 每张照片都保存在一个新的VIEW对象里,如果共用就不能正常显示了，可以试一下哦
        for (int i = 1; i <= datas.size(); i++)
        {
            View view = inflater.inflate(R.layout.guide_item, null);
            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    allPopupWindows.dismiss();
                }
            });
            view.findViewById(R.id.photos_settings_iv).setOnClickListener(this);
            ScaleImageView ivIcon = (ScaleImageView) view.findViewById(R.id.icon);
            String url = datas.get(i - 1).getPicUrl();
            ivIcon.setOnLongClickListener(new OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    setSaveDialog(context, photoUrl);
                    return false;
                }

            });
            ivIcon.setOnClickListener(this);
            ImageLoader.getInstance().displayImage(url, ivIcon, imagesOption);
            views.add(view);
        }
        // 1.设置幕后item的缓存数目
        viewPager.setOffscreenPageLimit(3);
        // 2.设置页与页之间的间距
        viewPager.setPageMargin(10);
        PhotosPagerAdapter photosPagerAdapter = new PhotosPagerAdapter(viewPager, views);
        viewPager.setAdapter(photosPagerAdapter); // 为viewpager设置adapter
        viewPager.setOnPageChangeListener(this);// 设置监听器
        viewPager.setCurrentItem(position);
        photoUrl = datas.get(position).getPicUrl();
        numTv.setText((position + 1) + "/" + datas.size());

    }

    /**
     * 保存图片Dialog
     */
    public void setSaveDialog(Context context,final String url)
    {
        if(this.context == null){
            this.context = context;
        }
        new ShopGuideSaveDialog((BaseActivity) context, "photo").setShopGuideSaveDialog(new DialogShopGuideSave()
        {

            @Override
            public void settings(ShopGuideSaveDialog dialog, String tag)
            {
                if (!StringUtils.isEmpty(url))
                {
                    new DownloadNetworkerImageTask().execute(url);
                }
                dialog.dismiss();
            }
        }).show();
    }

    // 下载并保存网络图片
    private class DownloadNetworkerImageTask extends AsyncTask<String, Void, Bitmap>
    {
        protected Bitmap doInBackground(String... urls)
        {
            Message msg = handler.obtainMessage();
            msg.arg1 = 0;
            handler.sendMessage(msg);

            Debug.e(TAG, "save-urls>" + urls[0]);
            Bitmap bitmap = loadImageFromNetwork(urls[0]);
            Debug.e(TAG, "save-scusse>" + bitmap);
            saveImageToGallery(context, bitmap);
            return bitmap;
        }

        private void saveImageToGallery(Context context, Bitmap bmp)
        {
            // 首先保存图片
            File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
            if (!appDir.exists())
            {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);
            try
            {
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // Bitmap image = bmp;
            // Resources r = context.getResources();
            //
            // try {
            // // Create screenshot directory if it doesn't exist
            // appDir.mkdirs();
            //
            // // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
            // // for DATE_TAKEN
            // long dateSeconds = System.currentTimeMillis() / 1000;
            //
            // // Save the screenshot to the MediaStore
            // ContentValues values = new ContentValues();
            // ContentResolver resolver = context.getContentResolver();
            // values.put(MediaStore.Images.ImageColumns.DATA, appDir.toString());
            // values.put(MediaStore.Images.ImageColumns.TITLE, fileName);
            // values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName);
            // values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis());
            // values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
            // values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
            // values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
            // values.put(MediaStore.Images.ImageColumns.WIDTH, 100);
            // values.put(MediaStore.Images.ImageColumns.HEIGHT, 100);
            // Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            //
            // OutputStream out = resolver.openOutputStream(uri);
            // image.compress(Bitmap.CompressFormat.PNG, 100, out);
            // out.flush();
            // out.close();
            //
            // // update file size in the database
            // values.clear();
            // values.put(MediaStore.Images.ImageColumns.SIZE, new File(appDir.toString()).length());
            // resolver.update(uri, values, null, null);
            //
            //
            // } catch (Exception e) {
            //
            // }

            // 其次把文件插入到系统图库
            // try {
            Debug.e(TAG, "------>" + context.getContentResolver() + ";file.getAbsolutePath()=" + file.getAbsolutePath() + ";fileName=" + fileName);
            // MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(), fileName, null);
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "title", "description");
            // }
            // catch (FileNotFoundException e) {
            // e.printStackTrace();
            // }
            File path = new File("/sdcard/Boohee/image.jpg");
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        }

        @SuppressLint("NewApi")
        protected void onPostExecute(Bitmap result)
        {
            Message msg = handler.obtainMessage();
            msg.arg1 = 1;
            handler.sendMessage(msg);

            Debug.e(TAG, "----------------save-urls-------1------result------->" + result);
            if (result != null)
            {
                Debug.e(TAG, "----------------save-urls-------2------result------->" + result);
                // saveImageToGallery(getApplicationContext(), result);
                new CustomToast(context).showToast("已保存到手机相册");
                if (!result.isRecycled())
                {
                    result.recycle();
                    System.gc();
                }
            }

        }

        private Bitmap loadImageFromNetwork(String imageUrl)
        {
            Bitmap bitmap = null;
            try
            {
                // 可以在这里通过文件名来判断，是否本地有此图片
                bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
            }
            catch (IOException e)
            {
            }
            return bitmap;
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position)
    {
        photoUrl = datas.get(position).getPicUrl();
        numTv.setText((position + 1) + "/" + datas.size());
    }

    // 底部保存菜单回调
    @Override
    public void settings(ShopGuideSaveDialog dialog, String tag)
    {
        if (tag.equals("photo"))
        {
            if (!StringUtils.isEmpty(photoUrl))
            {
                new DownloadNetworkerImageTask().execute(photoUrl);
            }
        }
        else
        {
            if (!StringUtils.isEmpty(photoUrl))
            {
                new DownloadNetworkerImageTask().execute(photoUrl);
            }
        }
        dialog.dismiss();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.photos_settings_iv)
        {
            new ShopGuideSaveDialog((BaseActivity) context, "photo").setShopGuideSaveDialog(this).show();
        }
        else if (id == R.id.icon)
        {
            allPopupWindows.dismiss();
        }
    }

}
