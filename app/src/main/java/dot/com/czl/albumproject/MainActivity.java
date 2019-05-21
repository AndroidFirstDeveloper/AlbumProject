package dot.com.czl.albumproject;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import dot.com.czl.albumlibrary.album.AlbumActivity;
import dot.com.czl.albumlibrary.album.AlbumFile;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findVIew();
        initSystemUI();
    }


    private void findVIew() {
        Button activity_main_select_btn = findViewById(R.id.activity_main_select_btn);
        activity_main_select_btn.setOnClickListener(this);

        recyclerView = findViewById(R.id.activity_main_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    private void initSystemUI() {
        View activity_main_toolbar = findViewById(R.id.activity_main_toolbar);
        View title_bar_layout_status_view = activity_main_toolbar.findViewById(R.id.title_bar_layout_status_view);
        ImmersionBar.with(this).statusBarView(title_bar_layout_status_view).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
    }

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<AlbumFile> list = new ArrayList<>();

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_main_select_btn) {
            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
            intent.putExtra(AlbumActivity.PICTURE_MAX_SELECTED_SIZE, 20);
            intent.putParcelableArrayListExtra(AlbumActivity.PICTURE_SELECTED_LIST, (ArrayList<? extends Parcelable>) list);
            intent.putExtra(AlbumActivity.REQUEST_CLASS_NAME, "dot.com.czl.albumproject.MainActivity");
            startActivityForResult(intent, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        ImmersionBar.with(this).destroy();
        super.onDestroy();
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AlbumActivity.PICTURE_SELECTED_RESULT_CODE) {
            list.clear();
            adapter.notifyDataSetChanged();
            list.addAll((ArrayList) data.getParcelableArrayListExtra(AlbumActivity.PICTURE_SELECTED_LIST));
            adapter.notifyDataSetChanged();
            if (list != null) {
                Log.e("MainActivity", new Gson().toJson(list));
            }
        }
    }


    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        private Context context;
        private List<AlbumFile> list;

        public MyAdapter(Context context, List<AlbumFile> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.picture_item_layout, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            Glide.with(context)
                    .load(list.get(position).getPath())
                    .into(holder.picture_item_layout_iv);
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        static class MyHolder extends RecyclerView.ViewHolder {

            public ImageView picture_item_layout_iv;

            public MyHolder(View itemView) {
                super(itemView);
                picture_item_layout_iv = itemView.findViewById(R.id.picture_item_layout_iv);
            }
        }
    }
}
