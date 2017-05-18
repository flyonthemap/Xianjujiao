package xidian.xianjujiao.com.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.piasy.audioprocessor.AudioProcessor;
import com.github.piasy.rxandroidaudio.StreamAudioPlayer;
import com.github.piasy.rxandroidaudio.StreamAudioRecorder;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.adapter.GridImageAdapter;
import xidian.xianjujiao.com.manager.FullyGridLayoutManager;
import xidian.xianjujiao.com.utils.UiUtils;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by flyonthemap on 2017/5/8.
 */

public class FactFragment extends Fragment {
    public static final String TAG = "FactFragment";
    @Bind(R.id.rl_image)
    RecyclerView rlImage;

    @Bind(R.id.rl_video)
    RecyclerView rlVideo;
    @Bind(R.id.et_fact_content)
    EditText etFactContent;
    @Bind(R.id.iv_listen)
    ImageView ivListen;
    @Bind(R.id.iv_start)
    ImageView ivStart;
    @Bind(R.id.iv_delete)
    ImageView ivDelete;
    @Bind(R.id.iv_upload)
    ImageView ivUpload;
    @Bind(R.id.tv_hint)
    TextView tvHint;

    private GridImageAdapter imageAdapter;
    private GridImageAdapter videoAdapter;
    private int maxSelectNum = 9;// 图片最大可选数量
    private int selectType = FunctionConfig.TYPE_IMAGE;
    private int maxB = 0;
    private int compressW = 0;
    private int compressH = 0;
    private boolean isCheckNumMode = false;
    private List<LocalMedia> imageSelectMedia = new ArrayList<>();
    private List<LocalMedia> videoListMedia = new ArrayList<>();
    private int completeColor;
    private boolean mode = false;// 启动相册模式


    private String rootDir;
    static final int BUFFER_SIZE = 2048;

    private int count = 0;
    private int maxTime = 180;
    private Runnable timerRunnable;// 定时器
    private static final int TIME_CHANGE = 2000;
    private static final int TIMER_COMPLETE = 3000;



    private StreamAudioRecorder mStreamAudioRecorder;
    private StreamAudioPlayer mStreamAudioPlayer;
    private AudioProcessor mAudioProcessor;
    private FileOutputStream mFileOutputStream;
    private File mOutputFile;
    private byte[] mBuffer;
    private boolean mIsRecording = false;
    private RxPermissions mPermissions;
    private float mRatio = 1;
    private boolean isDelete = true;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TIME_CHANGE:
                    Bundle data = msg.getData();
                    int time = data.getInt("time");
                    tvHint.setText(getString(R.string.remaining_time,time));
                    break;
                case TIMER_COMPLETE:
                    tvHint.setText("录音完成");
                    mHandler.removeCallbacks(timerRunnable);
                    timerRunnable = null;
                    // 设置按钮的点击状态
                    isDelete = false;
                    ivDelete.setClickable(true);
                    ivListen.setClickable(true);
                    ivUpload.setClickable(true);
                    count = 0;
                    break;
            }

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fact, null);
        ButterKnife.bind(this, view);
        initImageRecyclerView();
        initVideoRecyclerView();
        initRecord();
        return view;
    }

    // 开始录音
    @OnClick(R.id.iv_start)
    void start(){
        if(isDelete){
            if (mIsRecording) {
                stopRecord();

                mIsRecording = false;
            } else {
                boolean isPermissionsGranted = getRxPermissions().isGranted(WRITE_EXTERNAL_STORAGE)
                        && getRxPermissions().isGranted(RECORD_AUDIO);
                if (!isPermissionsGranted) {
                    getRxPermissions()
                            .request(WRITE_EXTERNAL_STORAGE,
                                    RECORD_AUDIO)
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean granted) {
                                    // not record first time to request permission
                                    if (granted) {
                                        Toast.makeText(UiUtils.getContext(), "授权成功",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(UiUtils.getContext(),
                                                "授权失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
                } else {
                    startRecord();
                    mIsRecording = true;
                }
            }
        }else {
            Toast.makeText(UiUtils.getContext(),"重录前请删除之前的录音",Toast.LENGTH_SHORT).show();
        }

    }
    private void startRecord() {
        try {
            // 设置定时器
            if (timerRunnable != null) {
                mHandler.removeCallbacks(timerRunnable);
                timerRunnable = null;
            }
            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    if (count < maxTime) {
                        count++;
                        Message msg = mHandler.obtainMessage();
                        msg.what = TIME_CHANGE;
                        Bundle bundle = new Bundle();
                        bundle.putInt("time", count);
                        msg.setData(bundle);
                        msg.sendToTarget();
                        mHandler.postDelayed(timerRunnable, 1000);
                    } else {
                        mHandler.sendEmptyMessage(TIMER_COMPLETE);
                    }
                }
            };
            mHandler.post(timerRunnable);


            String path = rootDir
                    + File.separator
                    + new SimpleDateFormat(
                    "yyyyMMddHHmmss").format(System
                    .currentTimeMillis())
                    + ".wma";
            mOutputFile = new File(path);
            mOutputFile.createNewFile();
            mFileOutputStream = new FileOutputStream(mOutputFile);
            mStreamAudioRecorder.start(new StreamAudioRecorder.AudioDataCallback() {
                @Override
                public void onAudioData(byte[] data, int size) {
                    if (mFileOutputStream != null) {
                        try {
                            mFileOutputStream.write(data, 0, size);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError() {
                    ivStart.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UiUtils.getContext(), "录音失败",
                                    Toast.LENGTH_SHORT).show();
                            tvHint.setText("点击录音");
                            mIsRecording = false;
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopRecord() {
        mStreamAudioRecorder.stop();
        try {
            mFileOutputStream.close();
            mFileOutputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHandler.sendEmptyMessage(TIMER_COMPLETE);
    }
    @OnClick(R.id.iv_delete)
    void deleteRecord(){
        if(mOutputFile.exists()){
            mOutputFile.delete();
            isDelete = true;
            ivDelete.setClickable(false);
            ivListen.setClickable(false);
            ivUpload.setClickable(false);
        }

    }
    @OnClick(R.id.iv_listen)
    void playRecord(){
        Observable.just(mOutputFile)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        try {
                            mStreamAudioPlayer.init();
                            FileInputStream inputStream = new FileInputStream(file);
                            int read;
                            while ((read = inputStream.read(mBuffer)) > 0) {
                                mStreamAudioPlayer.play(mBuffer, read);
                            }
                            inputStream.close();
                            mStreamAudioPlayer.release();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void initRecord() {
        mStreamAudioRecorder = StreamAudioRecorder.getInstance();
        mStreamAudioPlayer = StreamAudioPlayer.getInstance();
        mAudioProcessor = new AudioProcessor(BUFFER_SIZE);
        mBuffer = new byte[BUFFER_SIZE];
        // 创建保存录音的根目录
        try {
            rootDir = Environment.getExternalStorageDirectory().getCanonicalPath().toString()
                    +File.separator+"JuJiaoXiAn";
            File file = new File(rootDir);
            if(!file.exists()){
                file.mkdir();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 刚开始的时候设置按钮均为不可点击的状态
        ivListen.setClickable(false);
        ivUpload.setClickable(false);
        ivDelete.setClickable(false);

    }


    private void initVideoRecyclerView() {
        selectType = FunctionConfig.TYPE_VIDEO;
        FullyGridLayoutManager videoLayout = new FullyGridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false);
        rlVideo.setLayoutManager(videoLayout);
        videoAdapter = new GridImageAdapter(UiUtils.getContext(), videoOnAddPicClickListener);
        videoAdapter.setList(videoListMedia);
        videoAdapter.setSelectMax(maxSelectNum);
        rlVideo.setAdapter(videoAdapter);



        videoAdapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                if (videoListMedia.size() > 0) {
                    PictureConfig.getInstance().externalPictureVideo(getActivity(), videoListMedia.get(position).getPath());
                }

            }
        });
    }


    private void initImageRecyclerView() {
        selectType = FunctionConfig.TYPE_IMAGE;
        FullyGridLayoutManager imageLayoutManager = new FullyGridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false);
        rlImage.setLayoutManager(imageLayoutManager);
        imageAdapter = new GridImageAdapter(UiUtils.getContext(), imageOnAddPicClickListener);
        imageAdapter.setList(imageSelectMedia);
        imageAdapter.setSelectMax(maxSelectNum);
        rlImage.setAdapter(imageAdapter);
        imageAdapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // 预览图片 可长按保存 也可自定义保存路径
                PictureConfig.getInstance().externalPicturePreview(getActivity(), position, imageSelectMedia);

            }
        });
    }



    /**
     * 删除图片回调接口
     */

    private GridImageAdapter.onAddPicClickListener videoOnAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:

                    maxB = 102400;


                    int themeStyle = ContextCompat.getColor(UiUtils.getContext(), R.color.fab_material_white);


                    boolean selectImageType = false;
                    int checkedBoxDrawable;
                    if (selectImageType) {
                        checkedBoxDrawable = R.drawable.select_cb;
                    } else {
                        checkedBoxDrawable = 0;
                    }


                    int previewColor = ContextCompat.getColor(UiUtils.getContext(), R.color.blue);
                    completeColor = ContextCompat.getColor(UiUtils.getContext(), R.color.blue);
                    int previewBottomBgColor = ContextCompat.getColor(UiUtils.getContext(), R.color.fab_material_white);
                    int previewTopBgColor = ContextCompat.getColor(UiUtils.getContext(), R.color.fab_material_white);


                    FunctionOptions options = new FunctionOptions.Builder()
                            .setType(FunctionConfig.TYPE_VIDEO) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                            .setCompress(true) //是否压缩
                            .setEnablePixelCompress(true) //是否启用像素压缩
                            .setEnableQualityCompress(true) //是否启质量压缩
                            .setMaxSelectNum(maxSelectNum) // 可选择图片的数量
                            .setMinSelectNum(0)// 图片或视频最低选择数量，默认代表无限制
                            .setSelectMode(FunctionConfig.MODE_MULTIPLE) // 单选 or 多选
                            .setShowCamera(true) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
                            .setEnablePreview(true) // 是否打开预览选项
                            .setPreviewVideo(true) // 是否预览视频(播放) mode or 多选有效
                            .setCheckedBoxDrawable(checkedBoxDrawable)
                            .setRecordVideoDefinition(FunctionConfig.HIGH) // 视频清晰度
                            .setCustomQQ_theme(R.drawable.custom_qq_cb)// 可自定义QQ数字风格，不传就默认是蓝色风格
                            .setGif(false)// 是否显示gif图片，默认不显示
                            .setMaxB(maxB) // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb
                            .setPreviewColor(previewColor) //预览字体颜色
                            .setCompleteColor(completeColor) //已完成字体颜色
                            .setPreviewBottomBgColor(previewBottomBgColor) //预览图片底部背景色
                            .setPreviewTopBgColor(previewTopBgColor)//预览图片标题背景色
                            .setGrade(Luban.THIRD_GEAR) // 压缩档次 默认三档
                            .setCheckNumMode(isCheckNumMode)
                            .setCompressQuality(100) // 图片裁剪质量,默认无损
                            .setImageSpanCount(4) // 每行个数
                            .setSelectMedia(videoListMedia) // 已选图片，传入在次进去可选中，不能传入网络图片
                            .setCompressFlag(2) // 1 系统自带压缩 2 luban压缩
                            .setCompressW(compressW) // 压缩宽 如果值大于图片原始宽高无效
                            .setCompressH(compressH) // 压缩高 如果值大于图片原始宽高无效
                            .setThemeStyle(themeStyle) // 设置主题样式
                            .setNumComplete(false) // 0/9 完成  样式
                            .setClickVideo(false)// 开启点击声音
                            .setPicture_title_color(ContextCompat.getColor(UiUtils.getContext(), R.color.black)) // 设置标题字体颜色
                            .setPicture_right_color(ContextCompat.getColor(UiUtils.getContext(), R.color.black)) // 设置标题右边字体颜色
                            .setStatusBar(ContextCompat.getColor(UiUtils.getContext(), R.color.fab_material_white)) // 设置状态栏颜色，默认是和标题栏一致
                            .setImmersive(true)// 是否改变状态栏字体颜色(黑色)
                            .create();

                    if (mode) {
                        // 只拍照
                        PictureConfig.getInstance().init(options).startOpenCamera(getActivity(), videoResultCallback);
                    } else {
                        // 先初始化参数配置，在启动相册
                        PictureConfig.getInstance().init(options).openPhoto(getActivity(), videoResultCallback);
                    }
                    break;
                case 1:
                    // 删除图片
                    videoListMedia.remove(position);
                    videoAdapter.notifyItemRemoved(position);
                    break;
            }
        }
    };
    // 设置视频的监听器
    private GridImageAdapter.onAddPicClickListener imageOnAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    maxB = 102400;
                    int themeStyle = ContextCompat.getColor(UiUtils.getContext(), R.color.fab_material_white);
                    boolean selectImageType = false;
                    int checkedBoxDrawable;
                    if (selectImageType) {
                        checkedBoxDrawable = R.drawable.select_cb;
                    } else {
                        checkedBoxDrawable = 0;
                    }
                    int previewColor = ContextCompat.getColor(UiUtils.getContext(), R.color.blue);
                    completeColor = ContextCompat.getColor(UiUtils.getContext(), R.color.blue);
                    int previewBottomBgColor = ContextCompat.getColor(UiUtils.getContext(), R.color.fab_material_white);
                    int previewTopBgColor = ContextCompat.getColor(UiUtils.getContext(), R.color.fab_material_white);
                    FunctionOptions options = new FunctionOptions.Builder()
                            .setType(FunctionConfig.TYPE_IMAGE) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                            .setCompress(true) //是否压缩
                            .setEnablePixelCompress(true) //是否启用像素压缩
                            .setEnableQualityCompress(true) //是否启质量压缩
                            .setMaxSelectNum(maxSelectNum) // 可选择图片的数量
                            .setMinSelectNum(0)// 图片或视频最低选择数量，默认代表无限制
                            .setSelectMode(FunctionConfig.MODE_MULTIPLE) // 单选 or 多选
                            .setShowCamera(true) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
                            .setEnablePreview(true) // 是否打开预览选项
                            .setPreviewVideo(true) // 是否预览视频(播放) mode or 多选有效
                            .setCheckedBoxDrawable(checkedBoxDrawable)
                            .setRecordVideoDefinition(FunctionConfig.HIGH) // 视频清晰度
                            .setRecordVideoSecond(60) // 视频秒数
                            .setVideoS(0)// 查询多少秒内的视频 单位:秒
                            .setCustomQQ_theme(R.drawable.custom_qq_cb)// 可自定义QQ数字风格，不传就默认是蓝色风格
                            .setGif(false)// 是否显示gif图片，默认不显示
                            .setMaxB(maxB) // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb
                            .setPreviewColor(previewColor) //预览字体颜色
                            .setCompleteColor(completeColor) //已完成字体颜色
                            .setPreviewBottomBgColor(previewBottomBgColor) //预览图片底部背景色
                            .setPreviewTopBgColor(previewTopBgColor)//预览图片标题背景色
                            .setGrade(Luban.THIRD_GEAR) // 压缩档次 默认三档
                            .setCheckNumMode(isCheckNumMode)
                            .setCompressQuality(100) // 图片裁剪质量,默认无损
                            .setImageSpanCount(4) // 每行个数
                            .setSelectMedia(imageSelectMedia) // 已选图片，传入在次进去可选中，不能传入网络图片
                            .setCompressFlag(2) // 1 系统自带压缩 2 luban压缩
                            .setCompressW(compressW) // 压缩宽 如果值大于图片原始宽高无效
                            .setCompressH(compressH) // 压缩高 如果值大于图片原始宽高无效
                            .setThemeStyle(themeStyle) // 设置主题样式
                            .setNumComplete(false) // 0/9 完成  样式
                            .setClickVideo(false)// 开启点击声音
                            .setPicture_title_color(ContextCompat.getColor(UiUtils.getContext(), R.color.black)) // 设置标题字体颜色
                            .setPicture_right_color(ContextCompat.getColor(UiUtils.getContext(), R.color.black)) // 设置标题右边字体颜色
                            .setStatusBar(ContextCompat.getColor(UiUtils.getContext(), R.color.fab_material_white)) // 设置状态栏颜色，默认是和标题栏一致
                            .setImmersive(true)// 是否改变状态栏字体颜色(黑色)
                            .create();
                    if (mode) {
                        // 只拍照
                        PictureConfig.getInstance().init(options).startOpenCamera(getActivity(), imageResultCallback);
                    } else {
                        // 先初始化参数配置，在启动相册
                        PictureConfig.getInstance().init(options).openPhoto(getActivity(), imageResultCallback);
                    }
                    break;
                case 1:
                    // 删除图片
                    imageSelectMedia.remove(position);
                    imageAdapter.notifyItemRemoved(position);
                    break;
            }
        }
    };

    /**
     * 图片回调方法
     */
    private PictureConfig.OnSelectResultCallback imageResultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            imageSelectMedia = resultList;
            Log.i("callBack_result", imageSelectMedia.size() + "");
            LocalMedia media = resultList.get(0);
            if (media.isCompressed()) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                String path = media.getCompressPath();
                Toast.makeText(UiUtils.getContext(),path,Toast.LENGTH_SHORT).show();
            } else {
                // 原图地址
                String path = media.getPath();
                Toast.makeText(UiUtils.getContext(),path,Toast.LENGTH_SHORT).show();
            }
            if (imageSelectMedia != null) {
                imageAdapter.setList(imageSelectMedia);
                imageAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            imageSelectMedia.add(media);
            imageAdapter.setList(imageSelectMedia);
            imageAdapter.notifyDataSetChanged();
        }
    };

    // 视频回调接口
    private PictureConfig.OnSelectResultCallback videoResultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            videoListMedia = resultList;
            Log.i("callBack_result", imageSelectMedia.size() + "");
            LocalMedia media = resultList.get(0);
            if (media.isCompressed()) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                String path = media.getCompressPath();
                Toast.makeText(UiUtils.getContext(),path,Toast.LENGTH_SHORT).show();
            } else {
                // 原图地址
                String path = media.getPath();
                Toast.makeText(UiUtils.getContext(),path,Toast.LENGTH_SHORT).show();
            }
            if (videoListMedia != null) {
                videoAdapter.setList(videoListMedia);
                videoAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            videoListMedia.add(media);
            videoAdapter.setList(videoListMedia);
            videoAdapter.notifyDataSetChanged();
        }
    };
    private RxPermissions getRxPermissions() {
        if (mPermissions == null) {
            mPermissions = new RxPermissions(getActivity());
        }
        return mPermissions;
    }



}
