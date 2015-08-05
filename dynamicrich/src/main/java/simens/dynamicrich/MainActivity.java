package simens.dynamicrich;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import simens.dynamicrich.view.FileUtils;

public class MainActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {


    private Button picButton;
    private LinearLayout rootLinearLayout;

    private List<ViewHodler> viewHoldlers = new ArrayList<>();
    private int position;
    int focusPo = 0;
    private Point  outPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager wm=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(outPoint);

        picButton = (Button) findViewById(R.id.picbt);
        picButton.setOnClickListener(this);

        rootLinearLayout = (LinearLayout) findViewById(R.id.root_layout);
        addEditText("Test", 0);
        refreshViews();
    }

    //刷新Views
    private void refreshViews() {

        rootLinearLayout.removeAllViews();

        for (int i = 0; i < viewHoldlers.size(); i++) { //合并相连的EditTExt
            if (viewHoldlers.get(i).getEditText() != null) {
                if (i != 0 && viewHoldlers.get(i - 1).getEditText() != null) {
                    viewHoldlers.get(i - 1).getEditText().setText(viewHoldlers.get(i - 1).getEditText().getText().toString() + viewHoldlers.get(i).getEditText().getText().toString());
                    viewHoldlers.remove(i);
                    i--;
                }
            }
            if (viewHoldlers.get(i).getView() != null && viewHoldlers.get(i).getView().getVisibility() == View.GONE) {//删除标记为GONE的viewImage
                viewHoldlers.remove(i);
                i--;
            }
        }


        for (int i = 0; i < viewHoldlers.size(); i++) { //重新排序

            if (viewHoldlers.get(i).getEditText() != null) {

                rootLinearLayout.addView(viewHoldlers.get(i).getEditText(), viewHoldlers.get(i).getLayoutParams());

            } else if (viewHoldlers.get(i).getView() != null) {
                rootLinearLayout.addView(viewHoldlers.get(i).getView());
            }
        }

    }

    //添加EditTExt
    private void addEditText(String temp, int position) {

        final ViewHodler holder = new ViewHodler();
        holder.setEditContent(temp);

        EditText editText = new EditText(this);
        editText.setText(temp);
        editText.setSelection(editText.getText().toString().length());
        editText.setBackgroundColor(Color.WHITE);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setOnFocusChangeListener(this);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                holder.setEditContent(s.toString());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.setEditContent(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                holder.setEditContent(s.toString());


            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 30;
        holder.setEditText(editText);
        holder.setLayoutParams(lp);
        viewHoldlers.add(position, holder);
    }


    //添加ImageView布局
    private void addImageLayoutBitmap(final Bitmap bitmap, final int position) {

        ViewHodler holder = new ViewHodler();

        final View view = getLayoutInflater().from(this).inflate(R.layout.image_operat_layout, null);

        final RelativeLayout blurV = (RelativeLayout) view.findViewById(R.id.blur_view);
        final EditText editTv = (EditText) view.findViewById(R.id.title_et);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        ImageView deleteBt = (ImageView) view.findViewById(R.id.delete_bt);
        ImageView corpBt = (ImageView) view.findViewById(R.id.corp_bt);
        final ImageView editBt = (ImageView) view.findViewById(R.id.edit_bt);

        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.setVisibility(View.GONE);
                refreshViews();
            }
        });
        corpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        editBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(editTv.getVisibility()== View.VISIBLE){
                   editTv.setVisibility(View.GONE);
               }else {
                   editTv.setVisibility(View.VISIBLE);
               }
                blurV.setVisibility(View.GONE);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if (blurV.getVisibility() == View.VISIBLE) {
                    blurV.setVisibility(View.GONE);
                } else {
                    blurV.setVisibility(View.VISIBLE);
                    AppTools.blurJava(bitmap, blurV, MainActivity.this, 80);
                }
            }
        });
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(bitmap);

        holder.setView(view);

        viewHoldlers.add(position, holder);
    }

    //获取获得焦点的EditTExt光标的位置
    private void getFocusPosition() {

        for (int i = 0; i < viewHoldlers.size(); i++) {
            if (viewHoldlers.get(i).getEditText() != null && viewHoldlers.get(i).getEditText().isFocused()) {
                position = i;
                focusPo = viewHoldlers.get(i).getEditText().getSelectionStart();
                Toast.makeText(this, focusPo + "", Toast.LENGTH_SHORT).show();

                break;
            }
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            getFocusPosition();
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.picbt:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0x11);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }
        String picPath = AppTools.getAbsolutePath(this, data.getData());
        String toPath = AppTools.getImageCompresPath(this) + "/"
                + Math.random() * 10000 + ".jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(AppTools.compressImage(
                toPath, 970, picPath));

//      jump(picPath);
//      insertPic(bitmap);
        insertPic(AppTools.getimage(picPath));

    }


    private void jump(String path) {

        Intent it = new Intent(MainActivity.this, EditImageActivity.class);
        it.putExtra(EditImageActivity.FILE_PATH, path);
        File outputFile = FileUtils.getEmptyFile("tietu"
                + System.currentTimeMillis() + ".jpg");
        it.putExtra(EditImageActivity.EXTRA_OUTPUT,
                outputFile.getAbsolutePath());
        MainActivity.this.startActivity(it);

    }


    //插入图片
    private void insertPic(Bitmap bitmap) {

        int cursorPosition = viewHoldlers.get(position).getEditText().getSelectionStart();//当前光标所在的位置
        String cutPre = viewHoldlers.get(position).getEditText().getText().toString().substring(0, cursorPosition);//光标之前的String
        String cutBih = viewHoldlers.get(position).getEditText().getText().toString().substring(cursorPosition, viewHoldlers.get(position).getEditText().length());//光标之后的String


        /**
         * 1、下面有控件并且是EditText
         * 2、下面有控件并且是ImageView
         * 3、下面没控件
         */

        if (viewHoldlers.size() > position + 1 && viewHoldlers.get(position + 1).getEditText() != null) {//下面还有EditText并进行合并

            String temp = viewHoldlers.get(position + 1).getEditText().getText().toString() + cutBih;

            addImageLayoutBitmap(bitmap, position + 1);
            addEditText(temp, position + 2);

        } else if (viewHoldlers.size() > position + 1 && viewHoldlers.get(position + 1).getImageView() != null) {//下面有控件并且是ImageView

            addImageLayoutBitmap(bitmap, position + 1);

        } else {//下面没控件

            addImageLayoutBitmap(bitmap, position + 1);

            addEditText(cutBih, position + 2);

        }

        if (TextUtils.isEmpty(cutPre)) {
            viewHoldlers.remove(position);//为空删除控件
        } else {
            viewHoldlers.get(position).getEditText().setText(cutPre);//保留之前的String
        }
        refreshViews();

    }


}
