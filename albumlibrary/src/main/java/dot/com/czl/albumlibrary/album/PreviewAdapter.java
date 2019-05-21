package dot.com.czl.albumlibrary.album;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class PreviewAdapter extends PagerAdapter {

    private final String TAG = "PreviewAdapter";
    private Context context;
    private List<AlbumFile> list;
    private List<LoadStatus> loadList = new ArrayList<>();

    public class LoadStatus {
        private boolean isLoading;

        public boolean isLoading() {
            return isLoading;
        }

        public void setLoading(boolean loading) {
            isLoading = loading;
        }
    }

    public PreviewAdapter(Context context, List<AlbumFile> list) {
        this.context = context;
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            LoadStatus loadStatus = new LoadStatus();
            loadStatus.setLoading(true);
            loadList.add(loadStatus);
        }
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final FrameLayout frameLayout = new FrameLayout(context);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        ZoomImageView zoomImageView = new ZoomImageView(context);
//        frameLayout.addView(zoomImageView, 0, params1);

//        TouchImageView touchImageView = new TouchImageView(context);
//        frameLayout.addView(touchImageView, 0, params1);

//        ImageView imageView = new ImageView(context);
//        frameLayout.addView(imageView, 0, params1);

        GestureImageView gestureImageView = new GestureImageView(context);
        frameLayout.addView(gestureImageView, 0, params1);

        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.CENTER;
        ProgressBar progressBar = new ProgressBar(context);
        frameLayout.addView(progressBar, 1, params2);

        container.addView(frameLayout);
        return frameLayout;
    }


    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, final int position, @NonNull Object object) {
//        super.setPrimaryItem(container, position, object);
        //Log.e("PreviewAdapter", "setPrimaryItem" + "\t\tposition " + position);
        if (loadList.get(position).isLoading()) {
            loadList.get(position).setLoading(false);
            final FrameLayout frameLayout = (FrameLayout) object;
            final ProgressBar progressBar = (ProgressBar) frameLayout.getChildAt(1);
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(list.get(position).getPath())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                           progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(((GestureImageView) (frameLayout.getChildAt(0))));
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        /*super.destroyItem(container, position, object);*/
        container.removeView((View) object);
        loadList.get(position).setLoading(true);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
