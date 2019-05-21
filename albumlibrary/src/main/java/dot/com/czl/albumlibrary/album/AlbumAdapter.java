package dot.com.czl.albumlibrary.album;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dot.com.czl.albumlibrary.R;

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<AlbumFile> list;

    private List<FixedInfo> headerInfo;
    private final int HEADER_ITEM_TYPE = 100;
    private final int NORMAL_ITEM_TYPE = 0;
    private int item_height = 0;

    private List<AlbumFile> checkedList;
    private int max_choice_size = Integer.MAX_VALUE;

    public AlbumAdapter(Context context, List<AlbumFile> list, int maxChoiceSize) {
        this.context = context;
        this.list = list;
        this.max_choice_size = maxChoiceSize;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        item_height = (int) ((displayMetrics.widthPixels - dip2px(2 * 2)) / 3.0f);
    }

    public void resetData(List<AlbumFile> list) {
        this.list = list;
    }

    public void resetData(List<AlbumFile> list, List<AlbumFile> checkedList) {
        this.list = list;
        this.checkedList = checkedList;
    }

    public void addHeaderView(View view) {
        if (headerInfo == null)
            headerInfo = new ArrayList<>();
        FixedInfo fixedInfo = new FixedInfo();
        fixedInfo.setView(view);
        fixedInfo.setItemType(headerInfo.size() + HEADER_ITEM_TYPE);
        headerInfo.add(fixedInfo);
    }

    private int getHeaderCount() {
        return headerInfo == null ? 0 : headerInfo.size();
    }

    private int getNormalCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderCount()) {
            return headerInfo.get(position).getItemType();
        }
        return NORMAL_ITEM_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType >= HEADER_ITEM_TYPE) {
            return new HeaderViewHolder(headerInfo.get(viewType - HEADER_ITEM_TYPE).getView());
        } else if (viewType == NORMAL_ITEM_TYPE) {
            View convertview = LayoutInflater.from(context).inflate(R.layout.album_item_layout, parent, false);
            return new AlbumViewHolder(convertview);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AlbumViewHolder) {
            //Log.e("AlbumAdapter", list.get(position - getHeaderCount()).getPath());
            final AlbumViewHolder viewHolder = (AlbumViewHolder) holder;
            Glide.with(context)
                    .load(list.get(position - getHeaderCount()).getPath())
                    .into(viewHolder.album_item_layout_picture_iv);
            if (list.get(position - getHeaderCount()).isChecked()) {
                viewHolder.album_item_layout_picture_iv.setColorFilter(Color.argb(0x7d, 0, 0, 0), PorterDuff.Mode.SRC_OVER);
                viewHolder.album_item_layout_choice_iv.setImageResource(R.drawable.icn_comic_checkbox_yes);
            } else {
                viewHolder.album_item_layout_picture_iv.clearColorFilter();
                viewHolder.album_item_layout_choice_iv.setImageResource(R.drawable.icn_comic_checkbox_no);
            }
            viewHolder.album_item_layout_choice_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position - getHeaderCount()).isChecked()) {
                        list.get(position - getHeaderCount()).setChecked(false);
                        viewHolder.album_item_layout_picture_iv.clearColorFilter();
                        ((ImageView) v).setImageResource(R.drawable.icn_comic_checkbox_no);
                        if (checkedList != null)
                            checkedList.remove(list.get(position - getHeaderCount()));
                    } else {
                        if (checkedList != null) {
                            if (checkedList.size() >= max_choice_size) {
                                Toast.makeText(context, "最多只能选择" + max_choice_size + "张图片", Toast.LENGTH_SHORT).show();
                            } else {
                                list.get(position - getHeaderCount()).setChecked(true);
                                viewHolder.album_item_layout_picture_iv.setColorFilter(Color.argb(0x7d, 0, 0, 0), PorterDuff.Mode.SRC_OVER);
                                ((ImageView) v).setImageResource(R.drawable.icn_comic_checkbox_yes);
                                checkedList.add(list.get(position - getHeaderCount()));
                            }
                        }
                    }
                    if (onPictureSelectedListener != null) {
                        onPictureSelectedListener.onPictureSelected(v, list.get(position - getHeaderCount()), checkedList.size());
                    }
                }
            });
        } else if (holder instanceof HeaderViewHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            if (onPictureSelectedListener != null)
                                onPictureSelectedListener.onCameraSelected(v);
                            break;
                        case 1:
                            if (onPictureSelectedListener != null)
                                onPictureSelectedListener.onAlbumSelected(v);
                            break;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (getHeaderCount() + getNormalCount());
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        private ImageView album_item_layout_picture_iv, album_item_layout_choice_iv;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            album_item_layout_picture_iv = itemView.findViewById(R.id.album_item_layout_picture_iv);
            album_item_layout_choice_iv = itemView.findViewById(R.id.album_item_layout_choice_iv);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) album_item_layout_picture_iv.getLayoutParams();
            params.height = item_height;
            album_item_layout_picture_iv.setLayoutParams(params);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
            params.width = GridLayoutManager.LayoutParams.MATCH_PARENT;
            params.height = item_height;
            itemView.setLayoutParams(params);
        }
    }

    private OnPictureSelectedListener onPictureSelectedListener;

    public void setOnPictureSelectedListener(OnPictureSelectedListener onPictureSelectedListener) {
        this.onPictureSelectedListener = onPictureSelectedListener;
    }

    public interface OnPictureSelectedListener {
        void onPictureSelected(View v, AlbumFile checkedList, int total);

        void onCameraSelected(View v);

        void onAlbumSelected(View v);
    }

    public float dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }
}
