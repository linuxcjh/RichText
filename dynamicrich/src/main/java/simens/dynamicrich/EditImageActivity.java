package simens.dynamicrich;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import simens.dynamicrich.view.BitmapUtils;
import simens.dynamicrich.view.CropImageView;
import simens.dynamicrich.view.Matrix3;
import simens.dynamicrich.view.RotateImageView;
import simens.dynamicrich.zoom.ImageViewTouch;
import simens.dynamicrich.zoom.ImageViewTouchBase;


/**
 * 图片编辑 主页面
 *
 * @author panyi
 *         <p/>
 *         包含 1.贴图 2.滤镜 3.剪裁 4.底图旋转 功能
 */
public class EditImageActivity extends Activity  implements View.OnClickListener{
    public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";

    public String filePath;// 需要编辑图片路径
    public String saveFilePath;// 生成的新图片路径
    private int imageWidth, imageHeight;// 展示图片控件 宽 高
    private LoadImageTask mLoadImageTask;

    public Bitmap mainBitmap;// 底层显示Bitmap
    public ImageViewTouch mainImage;
    private ImageView backBtn;

    private ImageView saveBtn;// 保存按钮

    public CropImageView mCropPanel;// 剪切操作控件
    public RotateImageView mRotatePanel;// 旋转操作控件


    private Button corp_button,rote_button;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        initView();
        getData();
    }

    private void getData() {
        filePath = getIntent().getStringExtra(FILE_PATH);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);// 保存图片路径
        loadImage(filePath);
    }

    private void initView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = (int) ((float) metrics.widthPixels / 1.5);
        imageHeight = (int) ((float) metrics.heightPixels / 1.5);

        mainImage = (ImageViewTouch) findViewById(R.id.main_image);
        mCropPanel = (CropImageView) findViewById(R.id.crop_panel);
        mRotatePanel = (RotateImageView) findViewById(R.id.rotate_panel);


        corp_button=(Button)findViewById(R.id.corp_button);
        corp_button.setOnClickListener(this);
        rote_button=(Button)findViewById(R.id.rote_button);
        rote_button.setOnClickListener(this);

        backBtn=(ImageView)findViewById(R.id.backn);
        backBtn.setOnClickListener(this);
        saveBtn=(ImageView)findViewById(R.id.save);
        saveBtn.setOnClickListener(this);


    }


    /**
     * 异步载入编辑图片
     *
     * @param filepath
     */
    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.corp_button:
                mCropPanel.setRatioCropRect(mainImage.getBitmapRect(),
                        -1f);
                mCropPanel.setVisibility(View.VISIBLE);
                mRotatePanel.setVisibility(View.GONE);
                break;

            case R.id.backn:
                finish();
                break;
            case R.id.save:

                break;
            case R.id.rote_button:

                this.mainImage.setImageBitmap(mainBitmap);
                this.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
                this.mainImage.setVisibility(View.GONE);

                this.mRotatePanel.addBit(this.mainBitmap,
                        this.mainImage.getBitmapRect());
                this.mRotatePanel.reset();
                this.mRotatePanel.setVisibility(View.VISIBLE);
                this.mRotatePanel.rotateImage(90);

                break;
        }
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.loadImageByPath(params[0], imageWidth,
                    imageHeight);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mainBitmap != null) {
                mainBitmap.recycle();
                mainBitmap = null;
                System.gc();
            }
            mainBitmap = result;
            mainImage.setImageBitmap(result);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        }
    }// end inner class


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
    }

    /**
     * 保存剪切图片
     */
    public void saveCropImage() {
        RectF cropRect = mCropPanel.getCropRect();// 剪切区域矩形
        Matrix touchMatrix = mainImage.getImageViewMatrix();
        // Canvas canvas = new Canvas(resultBit);
        float[] data = new float[9];
        touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
        Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
        Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
        Matrix m = new Matrix();
        m.setValues(inverseMatrix.getValues());
        m.mapRect(cropRect);// 变化剪切矩形

        Bitmap resultBit = Bitmap.createBitmap(mainBitmap,
                (int) cropRect.left, (int) cropRect.top,
                (int) cropRect.width(), (int) cropRect.height());

        saveBitmap(resultBit, saveFilePath);



        if (mainBitmap != null
                && !mainBitmap.isRecycled()) {
            mainBitmap.recycle();
        }
        mainBitmap = resultBit;
        mainImage.setImageBitmap(mainBitmap);
        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        mCropPanel.setCropRect(mainImage.getBitmapRect());


    }


    /**
     * 保存Bitmap图片到指定文件
     *
     * @param bm
     */
    public static void saveBitmap(Bitmap bm, String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}// end class
