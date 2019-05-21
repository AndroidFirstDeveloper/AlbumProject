package dot.com.czl.albumlibrary.album;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dot.com.czl.albumlibrary.R;

public class AlbumActivity extends AppCompatActivity implements MediaReadTask.Callback, AlbumAdapter.OnPictureSelectedListener,
        BucketDialog.OnBucketSelectedListener, PathConvertTask.Callback, DialogInterface.OnDismissListener {

    private RecyclerView activity_album_recycle_view;
    private AlbumAdapter albumAdapter;
    private List<AlbumFile> displayAlbums;

    private TextView activity_album_have_select_tv, activity_album_select_number_tv, activity_album_preview_tv, activity_album_select_finish_tv;

    private BucketDialog bucketDialog;

    private View camera_header, album_header;
    public static final int PICTURE_SELECTED_RESULT_CODE = 103;
    public static String PICTURE_MAX_SELECTED_SIZE = "picture_max_selected_size";
    public static String PICTURE_SELECTED_LIST = "picture_selected_list";
    public static String PICTURE_CURRENT_SELECTED_INDEX = "picture_current_selected_index";
    public static String REQUEST_CLASS_NAME = "request_class_name";

    private int maxSelectSize = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        findViews();
        setListener();
        initData();
    }

    private String requestClassName;

    protected void findViews() {
        Intent intent = getIntent();
        requestClassName = intent.getStringExtra(REQUEST_CLASS_NAME);

        View activity_albums_toolbar = findViewById(R.id.activity_albums_toolbar);
        View title_bar_layout_status_view = activity_albums_toolbar.findViewById(R.id.title_bar_layout_status_view);
        TextView title_bar_layout_title_tv = activity_albums_toolbar.findViewById(R.id.title_bar_layout_title_tv);
        title_bar_layout_title_tv.setText("图片选择");
        ImmersionBar.with(this).statusBarView(title_bar_layout_status_view).navigationBarColor(android.R.color.white).init();

        activity_album_have_select_tv = findViewById(R.id.activity_album_have_select_tv);
        activity_album_select_number_tv = findViewById(R.id.activity_album_select_number_tv);
        activity_album_preview_tv = findViewById(R.id.activity_album_preview_tv);
        activity_album_select_finish_tv = findViewById(R.id.activity_album_select_finish_tv);
        activity_album_recycle_view = findViewById(R.id.activity_album_recycle_view);

        maxSelectSize = intent.getIntExtra(PICTURE_MAX_SELECTED_SIZE, Integer.MAX_VALUE);
        albumAdapter = new AlbumAdapter(this, displayAlbums, maxSelectSize);
        albumAdapter.setOnPictureSelectedListener(this);
        activity_album_recycle_view.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        activity_album_recycle_view.addItemDecoration(new PowerfulDecoration().setDividerWith(dip2px(2)).setDividerColor(Color.WHITE));
        camera_header = LayoutInflater.from(this).inflate(R.layout.album_item_camera_header, activity_album_recycle_view, false);
        album_header = LayoutInflater.from(this).inflate(R.layout.album_item_album_header, activity_album_recycle_view, false);
        albumAdapter.addHeaderView(camera_header);
        albumAdapter.addHeaderView(album_header);
        activity_album_recycle_view.setAdapter(albumAdapter);
    }

    protected void initData() {
        List<AlbumFile> checkedList = getIntent().getParcelableArrayListExtra(PICTURE_SELECTED_LIST);
        int mFunction = Album.FUNCTION_CHOICE_IMAGE;
        MediaReader mediaReader = new MediaReader(this, null, null, null, true);
        MediaReadTask mMediaReadTask = new MediaReadTask(mFunction, checkedList, mediaReader, this);
        mMediaReadTask.execute();
    }

    protected void setListener() {
        activity_album_preview_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(AlbumActivity.this, "预览", Toast.LENGTH_SHORT).show();
                if (checkedList.size() > 0) {
                    Intent intent = new Intent(AlbumActivity.this, PreviewActivity.class);
                    intent.putExtra(AlbumActivity.PICTURE_CURRENT_SELECTED_INDEX, 0);
                    intent.putParcelableArrayListExtra(AlbumActivity.PICTURE_SELECTED_LIST, (ArrayList<? extends Parcelable>) checkedList);
                    startActivity(intent);
                }
            }
        });

        activity_album_select_finish_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(AlbumActivity.this, "完成", Toast.LENGTH_SHORT).show();*/
                if (!TextUtils.isEmpty(requestClassName)) {
//                    Intent intent = new Intent(AlbumActivity.this,PictureActivity.class);
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName(AlbumActivity.this, requestClassName);
                    intent.setComponent(componentName);
                    intent.putParcelableArrayListExtra(PICTURE_SELECTED_LIST, (ArrayList<? extends Parcelable>) checkedList);
                    setResult(PICTURE_SELECTED_RESULT_CODE, intent);
                    AlbumActivity.this.finish();
                }
            }
        });

       /* album_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        camera_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }


    private ArrayList<AlbumFolder> allAlbumFolders;

    //操作“所有图片”相册中的图片，相应的不同的相册中的照片的选中状态也会发生变化。
    //这种“同步”现象的原因是因为“所有图片”集合中的对象跟其它相册集合
    //中的对象是一样的，查看获取图片的代码可知，图片对象在添加到不同相册集合
    //中的同时，也添加到了“所有图片”相册集合中。
    @Override
    public void onScanCallback(ArrayList<AlbumFolder> albumFolders, ArrayList<AlbumFile> checkedFiles) {//第一个参数包含：所有图片文件夹、xx文件夹、yy文件夹..
        if (albumFolders != null && albumFolders.size() > 0) {
            this.allAlbumFolders = albumFolders;
            this.checkedList.clear();
            this.checkedList.addAll(checkedFiles);
            AlbumFolder albumFolder = this.allAlbumFolders.get(0);
            if (albumFolder != null && albumFolder.getAlbumFiles() != null && albumFolder.getAlbumFiles().size() > 0) {
                albumAdapter.resetData(albumFolder.getAlbumFiles(), checkedList);
                albumAdapter.notifyDataSetChanged();
                activity_album_select_number_tv.setText(checkedList.size() + "张");
            }
        }
    }

    private List<AlbumFile> checkedList = new ArrayList<>();

    @Override
    public void onPictureSelected(View v, AlbumFile albumFile, int total) {//选择照片返回
        activity_album_select_number_tv.setText(total + "张");
    }

    @Override
    public void onCameraSelected(View v) {
        if (checkedList.size() >= maxSelectSize) {
            Toast.makeText(AlbumActivity.this, "最多只能选择" + maxSelectSize + "张图片，不能使用相机", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onAlbumSelected(View v) {
        if (allAlbumFolders != null && allAlbumFolders.size() > 0) {
            /*Toast.makeText(AlbumActivity.this, "选择相册", Toast.LENGTH_SHORT).show();*/
            showBucktDialog();
        } else {
            Toast.makeText(AlbumActivity.this, "手机里什么也没诶,赶紧拍几张照片吧", Toast.LENGTH_SHORT).show();
        }
    }


    private void showBucktDialog() {
        if (bucketDialog == null) {
            bucketDialog = new BucketDialog(AlbumActivity.this, R.style.ReplyDialog);
            bucketDialog.setOnBucketSelectedListener(AlbumActivity.this);
            bucketDialog.setOnDismissListener(this);
        }
        bucketDialog.show(allAlbumFolders);
        ImmersionBar.with(this, bucketDialog).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        if (dialog instanceof BucketDialog)
            ImmersionBar.with(this, (Dialog) dialog).destroy();
    }

    private int mCurrentFolder = 0;

    @Override
    public void onBucketSelected(View v, int position) {//选择相册返回
        mCurrentFolder = position;
        albumAdapter.resetData(allAlbumFolders.get(position).getAlbumFiles());
        albumAdapter.notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------
    private String filePath;

    private void takePicture() {
        if (mCurrentFolder == 0) {
            filePath = AlbumUtils.randomJPGPath();
        } else {
            File file = new File(allAlbumFolders.get(mCurrentFolder).getAlbumFiles().get(0).getPath());
            filePath = AlbumUtils.randomJPGPath(file.getParentFile());
        }
        AlbumUtils.takeImage(this, Album.CODE_ACTIVITY_TAKE_IMAGE, new File(filePath));
    }

    private MediaScanner mMediaScanner;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Album.CODE_ACTIVITY_TAKE_IMAGE: {
                if (resultCode == RESULT_OK) {
                    if (mMediaScanner == null) {
                        mMediaScanner = new MediaScanner(AlbumActivity.this);
                    }
                    mMediaScanner.scan(filePath);

                    PathConversion conversion = new PathConversion(null, null, null);
                    PathConvertTask task = new PathConvertTask(conversion, AlbumActivity.this);
                    task.execute(filePath);
                }
            }
            break;
        }
    }


    @Override
    public void onConvertStart() {
    }

    @Override
    public void onConvertCallback(AlbumFile albumFile) {
        albumFile.setChecked(!albumFile.isDisable());
        if (albumFile.isDisable()) {
            addFileToList(albumFile);
        } else {
            addFileToList(albumFile);
        }
    }

    private void addFileToList(AlbumFile albumFile) {
        if (mCurrentFolder != 0) {
            List<AlbumFile> albumFiles = allAlbumFolders.get(0).getAlbumFiles();
            if (albumFiles.size() > 0) albumFiles.add(0, albumFile);
            else albumFiles.add(albumFile);
        }

        AlbumFolder albumFolder = allAlbumFolders.get(mCurrentFolder);
        List<AlbumFile> albumFiles = albumFolder.getAlbumFiles();
        if (albumFiles.isEmpty()) {
            albumFiles.add(albumFile);
//            mView.bindAlbumFolder(albumFolder);
        } else {
            albumFiles.add(0, albumFile);
//            mView.notifyInsertItem(mHasCamera ? 1 : 0);
        }

        albumAdapter.notifyDataSetChanged();
        checkedList.add(albumFile);
        activity_album_select_number_tv.setText(checkedList.size() + "张");
    }

    private void requestPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> deniedPermissions = getDeniedPermissions(this, permissions);
            if (deniedPermissions.isEmpty()) {
                onPermissionGranted(Album.FUNCTION_CAMERA_IMAGE);
            } else {
                permissions = new String[deniedPermissions.size()];
                deniedPermissions.toArray(permissions);
                ActivityCompat.requestPermissions(this, permissions, 100);
            }
        } else {
            onPermissionGranted(Album.FUNCTION_CAMERA_IMAGE);
        }
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResult(grantResults)) {
                onPermissionGranted(Album.FUNCTION_CAMERA_IMAGE);
            } else {
                onPermissionDenied(Album.FUNCTION_CAMERA_IMAGE_DENIED);
            }
        }
    }

    private static List<String> getDeniedPermissions(Context context, String... permissions) {
        List<String> deniedList = new ArrayList<>(2);
        for (String permission : permissions) {
            if (PermissionChecker.checkSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
                deniedList.add(permission);
            }
        }
        return deniedList;
    }


    private boolean grantResult(int... grantResults) {
        boolean result = true;
        for (int grant : grantResults) {
            if (grant == PermissionChecker.PERMISSION_DENIED) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void onPermissionGranted(int code) {
        switch (code) {
            case Album.FUNCTION_CAMERA_IMAGE:
                takePicture();
                break;
        }
    }

    protected void onPermissionDenied(int code) {
        String messageRes;
        switch (code) {
            case Album.FUNCTION_CAMERA_IMAGE_DENIED: {
                messageRes = "请到设置中为应用添加拍照权限";
                break;
            }
            case Album.FUNCTION_CAMERA_VIDEO: {
                messageRes = "请到设置中为应用添加拍照权限";
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("权限声明")
                .setMessage(messageRes)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentUtils.gotoPermissionSetting(AlbumActivity.this);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .show();
    }

    private int dip2px(float dp) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        return (int) (displayMetrics.density * dp);
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        ImmersionBar.with(this).destroy();
        super.onDestroy();
    }
}
