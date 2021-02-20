package com.changhong.nativeplayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.changhong.nativeplayer.explorer.activity.CHLM_Other;
import com.changhong.nativeplayer.explorer.activity.MainExplorerActivity;
import com.changhong.nativeplayer.explorer.common.FileUtil;
import com.changhong.nativeplayer.explorer.common.GroupInfo;
import com.changhong.nativeplayer.explorer.common.MountInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CHLMMainUI extends Activity {

    /*for DEBUG start*/
    private final String DEBUG_TAG = "CHLMMainUI";
    private boolean DEBUG_FLAG = true;
    private final static String LOGI = "info";
    private final static String LOGW = "warning";
    private final static String LOGE = "error";
    private CHLM_DebugCtrl CHLMDebug;
    /*for DEBUG end*/
    /*CHLM Class CTRL ImageButton*/
    private ImageButton CHLM_FileManager;
    private ImageButton CHLM_Picture;
    private ImageButton CHLM_Video;
    private ImageButton CHLM_Audio;
    /*Delete DOC and OTHER 05-03*/
    //private ImageButton CHLM_DOC;
    //private ImageButton CHLM_ARA;
    /*Add Media Center*/
    private ImageButton CHLM_MediaCenter;
    private ImageView CHLM_ClaasFocus;
    /*class member Animation*/
    private Animation aniScaleBigImg, aniScaleSmall, aniScaleBigSel;
    /*storage device list*/
    private ListView StorageDev_List;
    private List<CHLMSD_MountInfo> StorageDev_Contentlist;
    // sub notes set of equipment
    private List<Map<String, String>> childList;
    // Device type node-set
    private List<GroupInfo> groupList;
    final static String MOUNT_LABLE = "mountLable";
    final static String MOUNT_TYPE = "mountType";
    final static String MOUNT_PATH = "mountPath";
    final static String MOUNT_NAME = "mountName";
    final static String MUSIC_PATH = "music_path";
    final static String ISO_PATH = "/mnt/iso";
    final static String CDROM_PATH = "/mnt/sr0/sr01";
    private int StorageDevSel_Index = 0;
    /*handler message for refresh UI*/
    final static int HANDLER_MSG_MOUNT_CHANGE = 10001;
    final static int HANDLER_MSG_SELECTED_CHANGE = 10002;
    /*show device info text view and ProgressBar*/
    TextView mStoDevName_TextView;
    TextView mStoDevInfo_TextView;
    ProgressBar mStoDevInfo_ProgressBar;
    final static int CHLM_CLASS_SHOW_ALL = 0;
    final static int CHLM_CLASS_SHOW_PICTURE = 1;
    final static int CHLM_CLASS_SHOW_AUDIO = 2;
    final static int CHLM_CLASS_SHOW_VIDEO = 3;

    /*Delete DOC and OTHER 05-03*/
    //final static int CHLM_CLASS_SHOW_DOC = 4;
    //final static int CHLM_CLASS_SHOW_OTHER = 5;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        /*Delete DOC and OTHER 05-03*/
        //setContentView(R.layout.layout_main_ui);
        setContentView(R.layout.layout_mian_newui);
        /*debug print CTRL*/
        CHLMDebug = new CHLM_DebugCtrl(DEBUG_TAG, DEBUG_FLAG);
        CHLMDebug.CHLM_DEBUG_FUN(LOGI, "Welcom to CHLM!");
        /*Find Views*/
        CHLM_GetViews();
        /*get mounts devices*/
        CHLM_MountEquipmentList();
        /*Show storage device list*/
        CHLM_MapMountDevice();
        /*register USB mount information*/
        CHLM_RegUSBAction();

    }

    private void CHLM_GetViews() {

        CHLM_ClaasFocus = (ImageView) findViewById(R.id.chlm_focus_id);
        aniScaleSmall = AnimationUtils.loadAnimation(this, R.anim.scale_small);
        aniScaleBigImg = AnimationUtils.loadAnimation(this, R.anim.scale_big_img);
        aniScaleBigSel = AnimationUtils.loadAnimation(this, R.anim.scale_big_sel);

        StorageDev_List = (ListView) findViewById(R.id.chlm_storagedevice_list_id);

        CHLM_FileManager = (ImageButton) findViewById(R.id.chlm_filemanager_id);
        CHLM_Picture = (ImageButton) findViewById(R.id.chlm_picture_id);
        CHLM_Video = (ImageButton) findViewById(R.id.chlm_video_id);
        CHLM_Audio = (ImageButton) findViewById(R.id.chlm_audio_id);
        /*Delete DOC and OTHER 05-03*/
        //CHLM_DOC		 = (ImageButton)findViewById(R.id.chlm_doc_id);
        //CHLM_ARA		 = (ImageButton)findViewById(R.id.chlm_rar_id);
        CHLM_MediaCenter = (ImageButton) findViewById(R.id.chlm_mediacenter_id);

        mStoDevName_TextView = (TextView) findViewById(R.id.chlm_sdevicename_id);
        mStoDevInfo_TextView = (TextView) findViewById(R.id.chlm_sdevicinfo_id);
        mStoDevInfo_ProgressBar = (ProgressBar) findViewById(R.id.chlm_sdevicePbar_id);

        StorageDev_List.setOnItemSelectedListener(StorageDevItem_Selected);
        StorageDev_List.setOnFocusChangeListener(StorageDevList_FocusChange);

        CHLM_FileManager.setOnFocusChangeListener(CHLM_FocusChangeLis);
        CHLM_Picture.setOnFocusChangeListener(CHLM_FocusChangeLis);
        CHLM_Video.setOnFocusChangeListener(CHLM_FocusChangeLis);
        CHLM_Audio.setOnFocusChangeListener(CHLM_FocusChangeLis);
        CHLM_MediaCenter.setOnFocusChangeListener(CHLM_FocusChangeLis);
        /*Delete DOC and OTHER 05-03*/
        //CHLM_DOC.setOnFocusChangeListener(CHLM_FocusChangeLis);
        //CHLM_ARA.setOnFocusChangeListener(CHLM_FocusChangeLis);

        CHLM_FileManager.setOnClickListener(CHLM_ClickLis);
        CHLM_Picture.setOnClickListener(CHLM_ClickLis);
        CHLM_Video.setOnClickListener(CHLM_ClickLis);
        CHLM_Audio.setOnClickListener(CHLM_ClickLis);
        CHLM_MediaCenter.setOnClickListener(CHLM_ClickLis);
        /*Delete DOC and OTHER 05-03*/
        //CHLM_DOC.setOnClickListener(CHLM_ClickLis);
        //CHLM_ARA.setOnClickListener(CHLM_ClickLis);
    }

    /*CHLM Storage device item selected listener*/
    OnItemSelectedListener StorageDevItem_Selected = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1, int arg2,
                                   long arg3) {
            StorageDevSel_Index = arg2;
            mHandler.removeMessages(0);
            Message msg = new Message();
            msg.what = HANDLER_MSG_SELECTED_CHANGE;
            mHandler.sendMessage(msg);
            //mHandler.sendMessageDelayed(msg, 1000);
            CHLMDebug.CHLM_DEBUG_FUN(LOGI, "StorageDevItem_Selected == " + StorageDevSel_Index);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };
    /**/
    OnFocusChangeListener StorageDevList_FocusChange = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View mView, boolean hasFocus) {

            if (hasFocus == true) {
                CHLMDebug.CHLM_DEBUG_FUN(LOGI, "StorageDevList_FocusChange true== " + StorageDevSel_Index);
            } else {
                CHLMDebug.CHLM_DEBUG_FUN(LOGI, "StorageDevList_FocusChange  false== " + StorageDevSel_Index);
            }

        }

    };
    /*CHLM Focus Change Listener*/
    OnFocusChangeListener CHLM_FocusChangeLis = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View ImageBnView, boolean HasFocus) {

            if (HasFocus) {
                FrameLayout.LayoutParams mlayout = new FrameLayout.LayoutParams(100, 100);
                FrameLayout.LayoutParams tmplayout = (FrameLayout.LayoutParams) ImageBnView.getLayoutParams();
                mlayout.leftMargin = tmplayout.leftMargin - 40 - tmplayout.width / 10;
                mlayout.topMargin = tmplayout.topMargin - 32 - tmplayout.height / 10;
                mlayout.width = tmplayout.width + 80 + (tmplayout.width / 5);
                mlayout.height = tmplayout.height + 86 + (tmplayout.height / 5);
                CHLMDebug.CHLM_DEBUG_FUN(LOGI, "CHLM_FocusChangeLis - >" + mlayout.leftMargin + "." + mlayout.topMargin
                        + "." + mlayout.width + "." + mlayout.height);

                CHLM_ClaasFocus.setBackgroundResource(R.drawable.ss01);
                CHLM_ClaasFocus.setLayoutParams(mlayout);
                CHLM_ClaasFocus.setVisibility(View.VISIBLE);

                ImageBnView.startAnimation(aniScaleBigImg);
                CHLM_ClaasFocus.startAnimation(aniScaleBigSel);
                ImageBnView.bringToFront();
                CHLM_ClaasFocus.bringToFront();
            } else {
                ImageBnView.startAnimation(aniScaleSmall);
                CHLM_ClaasFocus.startAnimation(aniScaleSmall);
                ImageBnView.clearAnimation();
                CHLM_ClaasFocus.clearAnimation();
                CHLM_ClaasFocus.setVisibility(View.INVISIBLE);
            }
        }
    };

    /*CHL Click Listener*/
    OnClickListener CHLM_ClickLis = new OnClickListener() {
        @Override
        public void onClick(View ImageBnview) {
            /*add by libeibei for Media Center*/
            if (ImageBnview.getId() == R.id.chlm_mediacenter_id) {
                Intent startIntent = new Intent();
                startIntent.setClassName("com.explorer",
                        "com.explorer.activity.TabBarExample");
                try {
                    startActivity(startIntent);
                } catch (Exception e) {
                    CHLMDebug.CHLM_DEBUG_FUN(LOGE, "startActivity Failed!--");
                }

            } else {
                Intent mintent = new Intent();
                mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CHLMSD_MountInfo mCHLMSD_MInfo = new CHLMSD_MountInfo();
                mCHLMSD_MInfo = StorageDev_Contentlist.get(StorageDevSel_Index);
                CHLMDebug.CHLM_DEBUG_FUN(LOGI, mCHLMSD_MInfo.mountShowName);
                Bundle mbundle = new Bundle();
                mbundle.putParcelable("mountinfo", mCHLMSD_MInfo);
                mintent.putExtras(mbundle);
                switch (ImageBnview.getId()) {
                    case R.id.chlm_filemanager_id:
                        mintent.putExtra("ClassShow", CHLM_CLASS_SHOW_ALL);
                        break;
                    case R.id.chlm_picture_id:
                        mintent.putExtra("ClassShow", CHLM_CLASS_SHOW_PICTURE);
                        break;
                    case R.id.chlm_video_id:
                        mintent.putExtra("ClassShow", CHLM_CLASS_SHOW_VIDEO);
                        break;
                    case R.id.chlm_audio_id:
                        mintent.putExtra("ClassShow", CHLM_CLASS_SHOW_AUDIO);
                        break;
                    default:
                        break;
                    /*Delete DOC and OTHER 05-03*/
                    //case R.id.chlm_doc_id:
                    //mintent.putExtra("ClassShow", CHLM_CLASS_SHOW_DOC);
                    //	break;
                    //case R.id.chlm_rar_id:
                    //mintent.putExtra("ClassShow", CHLM_CLASS_SHOW_OTHER);

                    //try {
                    //	mintent.setClass(CHLMMainUI.this,CHLM_Other.class);
                    //	startActivity(mintent);
                    //} catch (Exception e) {
                    //	CHLMDebug.CHLM_DEBUG_FUN(LOGE, "startActivity Failed!--"+mintent.getPackage());
                    //}
                    //return;

                    //break;
                }
                try {
                    mintent.setClass(CHLMMainUI.this, MainExplorerActivity.class);
                    startActivity(mintent);
                } catch (Exception e) {
                    CHLMDebug.CHLM_DEBUG_FUN(LOGE, "startActivity Failed!--" + mintent.getPackage());
                }
            }
        }
    };

    /*USB broadcast Receiver*/
    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED) ||
                    action.equals(Intent.ACTION_MEDIA_REMOVED) ||
                    action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                mHandler.removeMessages(0);
                Message msg = new Message();
                msg.what = HANDLER_MSG_MOUNT_CHANGE;
                mHandler.sendMessageDelayed(msg, 1000);
                if (action.equals(Intent.ACTION_MEDIA_REMOVED) ||
                        action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                    FileUtil.showToast(context, getString(R.string.uninstall_equi));
                } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    FileUtil.showToast(context, getString(R.string.install_equi));
                }
            }
        }
    };

    /*Handler for refresh UI*/
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HANDLER_MSG_MOUNT_CHANGE: {
                    CHLMDebug.CHLM_DEBUG_FUN(LOGI, "USB Mount change!");
                    CHLM_MountEquipmentList();
                    CHLM_MapMountDevice();
                }
                break;
                case HANDLER_MSG_SELECTED_CHANGE: {
                    CHLMSD_MountInfo mCHLMSD_mInfo = StorageDev_Contentlist.get(StorageDevSel_Index);
                    String VolumeStr = CHLM_CalcSDeviceVolume(mCHLMSD_mInfo);
                    //mStoDevName_TextView.setText(mCHLMSD_mInfo.mountShowName);
                    mStoDevInfo_ProgressBar.setProgress((int) (Math.round((mCHLMSD_mInfo.BlockSizeCount - mCHLMSD_mInfo.BlockSizeAvailable) * 100 / mCHLMSD_mInfo.BlockSizeCount)));
                    mStoDevInfo_TextView.setText(VolumeStr);
                }
                break;
            }
            super.handleMessage(msg);
        }
    };

    private String CHLM_CalcSDeviceVolume(CHLMSD_MountInfo mCHLMSD_mInfo) {
        String StrVolume = new String();
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        long allVolumeM = mCHLMSD_mInfo.BlockSize * (mCHLMSD_mInfo.BlockSizeCount / 1024) / 1024;
        long avaVolumeM = mCHLMSD_mInfo.BlockSize * mCHLMSD_mInfo.BlockSizeAvailable / 1024 / 1024;
        if (allVolumeM > 10000) {
            float AvaVf = (float) avaVolumeM / 1024;
            float AllVf = (float) allVolumeM / 1024;
            StrVolume = decimalFormat.format(AvaVf) + "GB/" +
                    decimalFormat.format(AllVf) + "GB";
        } else {
            StrVolume = String.valueOf(avaVolumeM) + "MB/" + String.valueOf(allVolumeM) + "MB";
        }
        return StrVolume;
    }

    private void CHLM_MountEquipmentList() {
        String[] mountType = getResources().getStringArray(R.array.mountType);
        MountInfo info = new MountInfo(this);
        groupList = new ArrayList<GroupInfo>();
        childList = new ArrayList<Map<String, String>>();
        GroupInfo group = null;
        for (int j = 0; j < mountType.length; j++) {
            group = new GroupInfo();
            childList = new ArrayList<Map<String, String>>();
            for (int i = 0; i < info.index; i++) {
                if (info.type[i] == j) {
                    if (info.path[i] != null && (info.path[i].contains("/mnt") || info.path[i].contains("/storage"))) {
                        Map<String, String> map = new HashMap<String, String>();
                        //map.put(MOUNT_DEV, info.dev[i]);
                        map.put(MOUNT_TYPE, String.valueOf(info.type[i]));
                        CHLMDebug.CHLM_DEBUG_FUN(LOGI, "mount type=" + String.valueOf(info.type[i]));
                        map.put(MOUNT_PATH, info.path[i]);
                        CHLMDebug.CHLM_DEBUG_FUN(LOGI, "mount path=" + info.path[i]);
                        map.put(MOUNT_LABLE, info.label[i]);
                        map.put(MOUNT_NAME, info.partition[i]);
                        Log.e("libeibei", "lable = " + info.label[i] + " , partition = " + info.partition[i]);
                        CHLMDebug.CHLM_DEBUG_FUN(LOGI, "mount name=" + info.partition[i]);
                        if (CDROM_PATH.equals(info.path[i])) {
                            CHLMDebug.CHLM_DEBUG_FUN(LOGI, "------CDROM-----");
                            //hasCDROM = true;
                        }
                        childList.add(map);
                    }
                }
            }
            if (childList.size() > 0) {
                group.setChildList(childList);
                group.setName(mountType[j]);
                groupList.add(group);
            }
        }
    }

    private void CHLM_MapMountDevice() {
        int index = 0;
        int j = 0;
        if (groupList == null) {
            CHLMDebug.CHLM_DEBUG_FUN(LOGE, "groupList == null!");
            return;
        }
        StorageDev_Contentlist = new ArrayList<CHLMSD_MountInfo>();
        //StorageDev_Contentlist.add(getResources().getString(R.string.sd_storage));
        try {
            for (index = 0; index < groupList.size(); index++) {
                List<Map<String, String>> mChildList = new ArrayList<Map<String, String>>();
                mChildList = groupList.get(index).getChildList();
                for (j = 0; j < mChildList.size(); j++) {
                    CHLMSD_MountInfo mCHLMSD_mInfo = new CHLMSD_MountInfo();
                    mCHLMSD_mInfo.GroupPosition = index;
                    mCHLMSD_mInfo.ChildPosition = j;
                    mCHLMSD_mInfo.mountPath = mChildList.get(j).get(MOUNT_PATH);
                    mCHLMSD_mInfo.mountName = mChildList.get(j).get(MOUNT_NAME);
                    mCHLMSD_mInfo.mountLable = mChildList.get(j).get(MOUNT_LABLE);
                    /**/
                    StatFs sta = new StatFs(mCHLMSD_mInfo.mountPath);
                    mCHLMSD_mInfo.BlockSize = sta.getBlockSizeLong();
                    mCHLMSD_mInfo.BlockSizeCount = sta.getBlockCountLong();
                    mCHLMSD_mInfo.BlockSizeAvailable = sta.getAvailableBlocksLong();
                    int mountType = Integer.parseInt(mChildList.get(j).get(MOUNT_TYPE));
                    mCHLMSD_mInfo.mountType = mountType;
                    switch (mountType) {
                        case 0://USB2.0
                        case 1://USB3.0
                            /*STB only two USB interface*/
                            mCHLMSD_mInfo.mountShowName =
                                    getResources().getString(R.string.usb) + mChildList.get(j).get(MOUNT_NAME);
                            CHLMDebug.CHLM_DEBUG_FUN(LOGI, "mount type = " + "mountType" + "--- path=" + mChildList.get(j).get(MOUNT_PATH));
                            break;
                        case 2://SATA
                            CHLMDebug.CHLM_DEBUG_FUN(LOGI, "mount type = 2 --- path=" + mChildList.get(j).get(MOUNT_PATH));
                            mCHLMSD_mInfo.mountShowName =
                                    getResources().getString(R.string.sata_storage) + mChildList.get(j).get(MOUNT_NAME);

                            break;
                        case 3://SDCARD or /mnt/nand
                            if (mChildList.get(j).get(MOUNT_PATH).equals("/storage/emulated/0")) {

                                mCHLMSD_mInfo.mountShowName =
                                        getResources().getString(R.string.local_storage) + mChildList.get(j).get(MOUNT_NAME);
                            } else if (mChildList.get(j).get(MOUNT_PATH).equals("/mnt/mmcblk1/mmcblk1p1")) {
                                mCHLMSD_mInfo.mountShowName =
                                        getResources().getString(R.string.sd_storage) + mChildList.get(j).get(MOUNT_NAME);
                            } else {
                                mCHLMSD_mInfo.mountShowName =
                                        getResources().getString(R.string.other_storage) + mChildList.get(j).get(MOUNT_NAME);
                            }
                            break;
                        case 4://UNKOWN
                            mCHLMSD_mInfo.mountShowName =
                                    getResources().getString(R.string.other_storage) + mChildList.get(j).get(MOUNT_NAME);
                            break;
                        case 5://CD-ROM
                            mCHLMSD_mInfo.mountShowName =
                                    getResources().getString(R.string.cdrom_storage) + mChildList.get(j).get(MOUNT_NAME);
                            break;
                    }
                    StorageDev_Contentlist.add(mCHLMSD_mInfo);
                }
            }
            CHLMSD_Adapter SDList_data = new CHLMSD_Adapter(this, StorageDev_Contentlist);
            StorageDev_List.setAdapter(SDList_data);
            StorageDev_List.setFocusable(true);
            StorageDev_List.setSelected(true);
            StorageDev_List.setSelection(0);
            StorageDev_List.setFocusableInTouchMode(false);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "----- error:" + e.toString());
            //FileUtil.showToast(this, getString(R.string.install_equi));
        }
    }

    private void CHLM_RegUSBAction() {
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_UMS_DISCONNECTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbFilter.addDataScheme("file");
        registerReceiver(usbReceiver, usbFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(usbReceiver);
        super.onDestroy();
    }

    /*storage device mount information*/
    public static class CHLMSD_MountInfo implements Parcelable {

        public String mountPath;
        public int mountType;
        public String mountName;
        public String mountShowName;
        public String mountLable;
        public int GroupPosition;
        public int ChildPosition;
        public long BlockSize;
        public long BlockSizeCount;
        public long BlockSizeAvailable;

        public CHLMSD_MountInfo() {
        }


        public final static Creator<CHLMSD_MountInfo> CREATOR = new Creator<CHLMSD_MountInfo>() {
            public CHLMSD_MountInfo createFromParcel(Parcel source) {
                return new CHLMSD_MountInfo(source);
            }

            public CHLMSD_MountInfo[] newArray(int size) {
                return new CHLMSD_MountInfo[size];
            }
        };

        public CHLMSD_MountInfo(Parcel source) {
            readFromParcel(source);
        }

        public void readFromParcel(Parcel source) {
            if (null == source) {
                Log.e("Parcelable", "CH_DTV_Date>>readFromParcel>>param in null");
                return;
            }
            mountPath = source.readString();
            mountType = source.readInt();
            mountName = source.readString();
            mountShowName = source.readString();
            mountLable = source.readString();
            GroupPosition = source.readInt();
            ChildPosition = source.readInt();
            BlockSize = source.readLong();
            BlockSizeCount = source.readLong();
            BlockSizeAvailable = source.readLong();

        }

        @Override
        public int describeContents() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int arg1) {
            // TODO Auto-generated method stub
            parcel.writeString(mountPath);
            parcel.writeInt(mountType);
            parcel.writeString(mountName);
            parcel.writeString(mountShowName);
            parcel.writeString(mountLable);
            parcel.writeInt(GroupPosition);
            parcel.writeInt(ChildPosition);
            parcel.writeLong(BlockSize);
            parcel.writeLong(BlockSizeCount);
            parcel.writeLong(BlockSizeAvailable);
        }

    }

    ;

    /*CHLM storage device adapter*/
    public class CHLMSD_Adapter extends BaseAdapter {

        private Context mContext;
        private List<CHLMSD_MountInfo> Contentlist;
        private LayoutInflater inflater;
        //private  int[] i_positionX = {100, 210, 263, 286, 270, 215, 100};
        //private  int[] i_positionY = {15, 50, 60, 70, 76, 70, 60};
        private int[] i_positionX = new int[64];
        private int[] i_positionY = new int[64];

        public CHLMSD_Adapter(Context context, List<CHLMSD_MountInfo> list) {
            this.mContext = context;
            this.Contentlist = list;
            inflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return Contentlist.size();
        }

        public Object getItem(int position) {

            return Contentlist.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView Textbookcontent;
            ImageView Imagebookcontent;
            convertView = inflater.inflate(R.layout.chlm_storagedevice_item, null);
            Textbookcontent = (TextView) convertView.findViewById(R.id.storagedevice_item_id);
            Imagebookcontent = (ImageView) convertView.findViewById(R.id.storagedevice_item_image_id);
            Textbookcontent.setText(Contentlist.get(position).mountShowName);
            /*set storage device icon*/
            Drawable drawable;
            String mDevContent = Contentlist.get(position).mountShowName;
            /*fix input Unknown device lead to APK dead*/
            if (mDevContent == null || mDevContent.length() == 0) {
                return convertView;
            }
            if (mDevContent.contains(mContext.getResources().getString(R.string.local_storage))) {
                drawable = mContext.getResources().getDrawable(R.drawable.local_icon);
            } else if (mDevContent.contains(mContext.getResources().getString(R.string.sd_storage))) {
                drawable = mContext.getResources().getDrawable(R.drawable.sdcard_icon);
            } else {
                drawable = mContext.getResources().getDrawable(R.drawable.usb_icon);
            }
            Imagebookcontent.setBackground(drawable);
            convertView.setTag(Imagebookcontent);
            return convertView;
        }
    }

    ;
}
