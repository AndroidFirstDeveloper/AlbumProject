package dot.com.czl.albumlibrary.album;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;


import java.util.List;

import dot.com.czl.albumlibrary.R;

public class BucketDialog extends Dialog implements BucketDialogAdapter.OnItemChoiceListener {

    public BucketDialog(@NonNull Context context) {
        super(context);
    }

    public BucketDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BucketDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private List<AlbumFolder> list;
    private BucketDialogAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_dialog_layout);

        findViews();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.7f);
        params.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(params);

        setCanceledOnTouchOutside(true);
        getWindow().setWindowAnimations(R.style.animTranslate);
    }

    private void findViews() {
        adapter = new BucketDialogAdapter(getContext(), list);
        adapter.setOnItemChoiceListener(this);
        recyclerView = findViewById(R.id.bucket_dialog_layout_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    public void show(List<AlbumFolder> list) {
        this.list = list;
        this.show();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemChoice(View v, int position) {
        if (onBucketSelectedListener != null)
            onBucketSelectedListener.onBucketSelected(v, position);
        dismiss();
    }

    private OnBucketSelectedListener onBucketSelectedListener;

    public void setOnBucketSelectedListener(OnBucketSelectedListener onBucketSelectedListener) {
        this.onBucketSelectedListener = onBucketSelectedListener;
    }

    public interface OnBucketSelectedListener {
        void onBucketSelected(View v, int position);
    }
}
