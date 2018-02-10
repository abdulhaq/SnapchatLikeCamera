package life.haq.it.android.snapchatlikecamera.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

import life.haq.it.android.snapchatlikecamera.R;

/**
 * Created by Abdul Haq (it.haq.life) on 11-07-2017.
 */

public class MyCanvas extends Activity {

    private static final String TAG = MyCanvas.class.getSimpleName();
    public static final int PERM_RQST_CODE = 110;
    public StickerView stickerView;
    private TextSticker sticker;
    private EditText editText;
    private LinearLayout editTextLayout;
    private int textColor;
    public InputMethodManager keyboard;
    public LinearLayout selectSticker;
    private StickerAdapter stickerAdapter;
    private boolean loadSticker = true;
    private GridView stickersGrid;
    public File dir;
    public String defaultVideo;
    private Integer[]  sticker_images = {
            R.drawable.sticker_1, R.drawable.sticker_2, R.drawable.sticker_3, R.drawable.sticker_4,
            R.drawable.sticker_5, R.drawable.sticker_6, R.drawable.sticker_7, R.drawable.sticker_8,
            R.drawable.sticker_9, R.drawable.sticker_10, R.drawable.sticker_11, R.drawable.sticker_12,
            R.drawable.sticker_13, R.drawable.sticker_14, R.drawable.sticker_15, R.drawable.sticker_16,
            R.drawable.sticker_17
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);

        editText = (EditText) findViewById(R.id.editText);
        editTextLayout = (LinearLayout) findViewById(R.id.editTextLayout);
        keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        selectSticker  = (LinearLayout) findViewById(R.id.select_sticker);
        stickersGrid = (GridView) findViewById(R.id.sticker_grid);
        textColor = Color.WHITE;
        stickerAdapter = new StickerAdapter(this);

        LinearGradient test = new LinearGradient(0.f, 0.f, 700.f, 0.0f,
                new int[] {0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
                        0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF}, null, Shader.TileMode.CLAMP);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        shapeDrawable.getPaint().setShader(test);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar_font);
        final View colorSelected = (View) findViewById(R.id.colorSelected);
        seekBar.setProgressDrawable((Drawable)shapeDrawable);

        seekBar.setMax(256*7-1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int r = 0;
                    int g = 0;
                    int b = 0;

                    if(progress < 256) {
                        b = progress;
                    } else if (progress < 256*2) {
                        g = progress%256;
                        b = 256 - progress%256;
                    } else if (progress < 256 * 4) {
                        r = progress%256;
                        g = 256 - progress%256;
                        b = 256 - progress%256;
                    } else if (progress < 256 * 5) {
                        r = 255;
                        g = 0;
                        b = progress%256;
                    } else if (progress < 256 * 6) {
                        r = 255;
                        g = progress%256;
                        b = 256 - progress%256;
                    } else if (progress < 256 * 7) {
                        r = 255;
                        g = 255;
                        b = progress%256;
                    }
                    colorSelected.setBackgroundColor(Color.argb(255, r, g, b));
                    textColor = Color.argb(255, r, g, b);
                    editText.setTextColor(textColor);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        stickersGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String sticker_bitmap = sticker_links[position];
                ImageView iv = (ImageView) view.findViewById(R.id.sticker_grid_item);
                Drawable drawable = iv.getDrawable();
                if (drawable != null) {
                    stickerView.addSticker(new DrawableSticker(drawable));
                    stickerOptions();
                }
            }
        });
    }

    public void setStickerView(int stickerV) {
        Log.i("setStickerView","Called");
        if (stickerV == 0) {
            stickerView = (StickerView) findViewById(R.id.sticker_view);
            StickerView SvVideo = (StickerView) findViewById(R.id.sticker_view1);
            SvVideo.setBackgroundColor(0);
        } else if (stickerV == 1) {
            stickerView = (StickerView) findViewById(R.id.sticker_view1);
            StickerView SvVideo = (StickerView) findViewById(R.id.sticker_view1);
            SvVideo.setBackgroundColor(0);
        }

        //currently you can config your own icons and icon event. the event you can custom
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon));

        stickerView.setBackgroundColor(Color.WHITE);
        stickerView.setLocked(false);
        stickerView.setConstrained(true);

        sticker = new TextSticker(this);

        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerAdded");
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                //stickerView.removeAllSticker();
                if (sticker instanceof TextSticker) {
                    //((TextSticker) sticker).setTextColor(Color.RED);
                    //stickerView.replace(sticker);
                    //stickerView.invalidate();
                    String stext = ((TextSticker) sticker).getText();
                    editText.setText(stext);
                    stickerView.removeCurrentSticker();
                    editTextLayout.setVisibility(View.VISIBLE);
                    showKeyboard(true);
                    editText.requestFocus();
                }
                Log.d(TAG, "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDragFinished");
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                Log.d(TAG, "onDoubleTapped: double tap will be with two click");
            }
        });
    }

    public void showHideEditText() {
        int getEditTextVisibility = editTextLayout.getVisibility();
        if (getEditTextVisibility == View.VISIBLE) {
            String sText = editText.getText().toString();
            addText(sText, textColor);
            showKeyboard(false);
            editTextLayout.setVisibility(View.GONE);
        } else {
            editText.setText("");
            editText.setTextColor(Color.WHITE);
            editTextLayout.setVisibility(View.VISIBLE);
            showKeyboard(true);
            editText.requestFocus();

        }
    }

    public void showKeyboard(boolean show) {
        if (show) {
            keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);
        } else {
            keyboard.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        Log.i("Keyboard function","triggered");
    }

    public void addText(String stickerText, int color) {
        if (!stickerText.equals("")) {
            final TextSticker sticker = new TextSticker(this);
            sticker.setText(stickerText);
            sticker.setTextColor(color);
            sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
            sticker.resizeText();

            stickerView.addSticker(sticker);
        }
    }

    public void stickerOptions() {
        if (selectSticker.getVisibility() == View.VISIBLE) {
            selectSticker.setVisibility(View.GONE);
        } else {
            if (loadSticker) {}
            stickersGrid.setAdapter(stickerAdapter);
            selectSticker.setVisibility(View.VISIBLE);
        }
    }

    public class StickerAdapter extends BaseAdapter {

        private Activity activity;
        private LayoutInflater inflater;

        private StickerAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return sticker_images.length;
        }

        @Override
        public Object getItem(int location) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.item_sticker_gridview, null);

            ImageView girdPhoto = (ImageView) convertView.findViewById(R.id.sticker_grid_item);

            // set gird Photos
            girdPhoto.setImageResource(sticker_images[position]);
            return convertView;
        }
    }

    public void testRemove(View view) {
        if (stickerView.removeCurrentSticker()) {
            Toast.makeText(this, "Remove current Sticker successfully!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, "Remove current Sticker failed!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void removeAllStickers() {
        stickerView.removeAllStickers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_RQST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //loadSticker();
        }
    }
}
