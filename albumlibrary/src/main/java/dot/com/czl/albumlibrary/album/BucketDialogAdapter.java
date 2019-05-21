package dot.com.czl.albumlibrary.album;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.List;

import dot.com.czl.albumlibrary.R;

public class BucketDialogAdapter extends RecyclerView.Adapter<BucketDialogAdapter.BucketViewHolder> {

    private Context context;
    private List<AlbumFolder> albumFolders;
    private LayoutInflater layoutInflater;
    private int pre_select_index = 0;

    public BucketDialogAdapter(Context context, List<AlbumFolder> albumFolders) {
        this.context = context;
        this.albumFolders = albumFolders;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BucketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BucketViewHolder(layoutInflater.inflate(R.layout.bucket_dialog_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BucketViewHolder holder, final int position) {
        Glide.with(context)
                .load(albumFolders.get(position).getAlbumFiles().get(0).getPath())
                .into(holder.bucket_dialog_item_layout_picture_iv);
        holder.bucket_dialog_item_layout_bucket_size_tv.setText("(" + albumFolders.get(position).getAlbumFiles().size() + ")" + " " + albumFolders.get(position).getName());

        if (albumFolders.get(position).isChecked()) {
            holder.bucket_dialog_item_layout_checkbox_iv.setImageResource(R.drawable.distinct_select);
        } else {
            holder.bucket_dialog_item_layout_checkbox_iv.setImageResource(R.drawable.distinct_not_select);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumFolders.get(pre_select_index).setChecked(false);
                pre_select_index = position;
                albumFolders.get(pre_select_index).setChecked(true);
                notifyDataSetChanged();

                if (onItemChoiceListener != null) {
                    onItemChoiceListener.onItemChoice(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFolders == null ? 0 : albumFolders.size();
    }

    public class BucketViewHolder extends RecyclerView.ViewHolder {
        public ImageView bucket_dialog_item_layout_picture_iv, bucket_dialog_item_layout_checkbox_iv;
        public TextView bucket_dialog_item_layout_bucket_size_tv;

        public BucketViewHolder(View itemView) {
            super(itemView);
            bucket_dialog_item_layout_picture_iv = itemView.findViewById(R.id.bucket_dialog_item_layout_picture_iv);
            bucket_dialog_item_layout_checkbox_iv = itemView.findViewById(R.id.bucket_dialog_item_layout_checkbox_iv);
            bucket_dialog_item_layout_bucket_size_tv = itemView.findViewById(R.id.bucket_dialog_item_layout_bucket_size_tv);
        }
    }

    private OnItemChoiceListener onItemChoiceListener;

    public void setOnItemChoiceListener(OnItemChoiceListener onItemChoiceListener) {
        this.onItemChoiceListener = onItemChoiceListener;
    }

    public interface OnItemChoiceListener{
        void onItemChoice(View v, int position);
    }
}
