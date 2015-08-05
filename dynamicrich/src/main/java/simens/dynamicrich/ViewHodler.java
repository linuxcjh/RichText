package simens.dynamicrich;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by chen on 3/8/15.
 */
public class ViewHodler {

    private View view ;
    private EditText editText;
    private String editContent;
    private ImageView imageView;
    private String imagePath;
    private LinearLayout.LayoutParams layoutParams;


    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getEditContent() {
        return editContent;
    }

    public void setEditContent(String editContent) {
        this.editContent = editContent;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public LinearLayout.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    public void setLayoutParams(LinearLayout.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
    }
}
