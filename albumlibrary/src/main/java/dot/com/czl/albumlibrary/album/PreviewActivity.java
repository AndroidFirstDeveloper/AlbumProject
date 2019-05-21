package dot.com.czl.albumlibrary.album;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

import java.util.List;

import dot.com.czl.albumlibrary.R;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        findView();
//        setListener();
        initSystemUI();
    }

    //    private Toolbar activity_preview_toolbar;
    private ViewPager activity_preview_viewpager;
//    private ImageView activity_preview_back_iv;
//    private TextView activity_preview_title_tv;


    private void findView() {
//        activity_preview_toolbar = findViewById(R.id.activity_preview_toolbar);
//        activity_preview_back_iv = findViewById(R.id.activity_preview_back_iv);
//        activity_preview_title_tv = findViewById(R.id.activity_preview_title_tv);
//        activity_preview_toolbar.setTitle("");
//        setSupportActionBar(activity_preview_toolbar);

//        activity_preview_back_iv.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));

        activity_preview_viewpager = findViewById(R.id.activity_preview_viewpager);
        List<AlbumFile> list = getIntent().getParcelableArrayListExtra(AlbumActivity.PICTURE_SELECTED_LIST);
        int currentSelectedIndex = getIntent().getIntExtra(AlbumActivity.PICTURE_CURRENT_SELECTED_INDEX, 0);
        activity_preview_viewpager.setAdapter(new PreviewAdapter(this, list));
        activity_preview_viewpager.setCurrentItem(currentSelectedIndex);
    }

    /*private void setListener() {
        activity_preview_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewActivity.this.finish();
            }
        });
    }*/


    private void initSystemUI() {
        View activity_preview_toolbar = findViewById(R.id.activity_preview_toolbar);
        View title_bar_layout_status_view = activity_preview_toolbar.findViewById(R.id.title_bar_layout_status_view);
        View title_bar_layout_divider = activity_preview_toolbar.findViewById(R.id.title_bar_layout_divider);
        title_bar_layout_divider.setVisibility(View.GONE);
        View title_bar_layout_title_layout = activity_preview_toolbar.findViewById(R.id.title_bar_layout_title_layout);
        title_bar_layout_title_layout.setBackgroundColor(Color.TRANSPARENT);
        title_bar_layout_status_view.setBackgroundColor(Color.TRANSPARENT);

//        TextView title_bar_layout_title_tv = activity_preview_toolbar.findViewById(R.id.title_bar_layout_title_tv);
//        title_bar_layout_title_tv.setText("图片选择");
        ImmersionBar.with(this).statusBarView(title_bar_layout_status_view).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
    }

   /* @Override
    protected void onHandleMessage(Message msg) {

    }

    @Override
    protected void initDataOnSuccess(String result) {

    }

    @Override
    protected void initDataOnFailure() {

    }

    @Override
    protected void initNetWorkFailure() {

    }

    private Toolbar activity_preview_toolbar;
    private ViewPager activity_preview_viewpager;
    private ImageView activity_preview_back_iv;
    private TextView activity_preview_title_tv;

    @Override
    protected void createContent() {
        setContentView(R.layout.activity_preview);
    }

    @Override
    protected void findViews() {
        View activity_preview_toolbar = findViewById(R.id.activity_preview_toolbar);
        AlwaysMarqueeTextView rb_book_rack_title = (AlwaysMarqueeTextView) activity_preview_toolbar.findViewById(R.id.title);
        rb_book_rack_title.setText("预览");
        activity_preview_toolbar.setBackgroundColor(Color.TRANSPARENT);
        mImmersionBar.titleBar(activity_preview_toolbar).init();
        hideBack();

        activity_preview_viewpager = findViewById(R.id.activity_preview_viewpager);
        List<AlbumFile> list = getIntent().getParcelableArrayListExtra(AlbumActivity.PICTURE_SELECTED_LIST);
        int currentSelectedIndex = getIntent().getIntExtra(AlbumActivity.PICTURE_CURRENT_SELECTED_INDEX, 0);
        activity_preview_viewpager.setAdapter(new PreviewAdapter(this, list));
        activity_preview_viewpager.setCurrentItem(currentSelectedIndex);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
       *//* activity_preview_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewActivity.this.finish();
            }
        });*//*
    }

    @Override
    public void free() {

    }*/

    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        ImmersionBar.with(this).destroy();
        super.onDestroy();
    }
}
