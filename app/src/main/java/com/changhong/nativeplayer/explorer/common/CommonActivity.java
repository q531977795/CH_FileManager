package com.changhong.nativeplayer.explorer.common;

import com.changhong.nativeplayer.R;
import com.changhong.nativeplayer.explorer.activity.FileMenu;
import com.changhong.nativeplayer.explorer.activity.TabBarExample;
import com.changhong.nativeplayer.explorer.bd.BDInfo;
import com.changhong.nativeplayer.explorer.bd.DVDInfo;
import com.changhong.nativeplayer.explorer.common.FilterType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.MimeTypeMap;
import android.os.SystemProperties;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Set;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommonActivity extends Activity {
    private final String TAG = "CommonActivity";
    /* The list of displayed data container */
    /* CNcomment:列表方式显示数据容器 */
    protected ListView listView;

    /* Thumbnail display data container */
    /* CNcomment: 缩略图方式显示数据容器 */
    protected GridView gridView;

    /* Sort array */
    /* CNcomment: 排序方式数组 */
    protected int[] sortArray;

    /* Type of filtering array */
    /* CNcomment: 过滤类型数组 */
    protected int[] filterArray;

    /* display array */
    /* CNcomment: 显示方式数组 */
    protected int[] showArray;

    /* Sort button */
    /* CNcomment: 排序按钮 */
    protected ImageButton sortBut;

    /* filtering button */
    /* CNcomment: 过滤按钮 */
    protected ImageButton filterBut;

    /* display button */
    /* CNcomment: 显示方式按钮 */
    protected ImageButton showBut;

    /* the current display */
    /* CNcomment:当前显示方式 */
    protected int showCount = 0;

    /* current sort */
    /* CNcomment: 当前排序方式 */
    protected int sortCount = 0;

    /* crrrent filtering
     * 0==显示所有文件
     * 1==所有图片
     * 2==音频
     * 3==视频*/
    /* CNcomment: 当前过滤方式 */
    protected int filterCount = 0;

    /* new creat */
    /* CNcomment: 新建 */
    protected final static int MENU_ADD = Menu.FIRST;

    /* search */
    /* CNcomment: 搜索 */
    protected final static int MENU_SEARCH = Menu.FIRST + 1;

    /* copy */
    /* CNcomment: 拷贝 */
    protected final static int MENU_COPY = Menu.FIRST + 2;
    /* cut */
    /* CNcomment: 剪切 */
    protected final static int MENU_CUT = Menu.FIRST + 3;

    /* paste */
    /* CNcomment: 粘贴 */
    protected final static int MENU_PASTE = Menu.FIRST + 4;

    /* delete */
    /* CNcomment: 删除 */
    protected final static int MENU_DELETE = Menu.FIRST + 5;

    /* rename */
    /* CNcomment:重命名 */
    protected final static int MENU_RENAME = Menu.FIRST + 6;

    /* new audio filter format */
    /* CNcomment:新增音频过滤格式 */
    protected final static int ADD_MENU_AUDIO = Menu.FIRST + 7;

    /* new video filter format */
    /* CNcomment: 新增视频过滤格式 */
    protected final static int ADD_MENU_VIDEO = Menu.FIRST + 8;

    /* add a picturn filter format */
    /* CNcomment: 新增图片过滤格式 */
    protected final static int ADD_MENU_IMAGE = Menu.FIRST + 9;

    /* remove audio filter format */
    /* CNcomment: 移除音频过滤格式 */
    protected final static int REMOVE_MENU_AUDIO = Menu.FIRST + 10;

    /* remove video filter fromat */
    /* CNcomment: 移除视频过滤格式 */
    protected final static int REMOVE_MENU_VIDEO = Menu.FIRST + 11;

    /* remove picturn filter format */
    /* CNcomment: 移除图片过滤格式 */
    protected final static int REMOVE_MENU_IMAGE = Menu.FIRST + 12;

    /* help */
    /* CNcomment: 帮助 */
    protected final static int MENU_HELP = Menu.FIRST + 13;

    /* add new NFS mount */
    /* CNcomment: 新增NFS挂载 */
    protected final static int ADD_NFS_MOUNT = Menu.FIRST + 14;

    /* label */
    /* CNcomment: 标签 */
    protected final static int MENU_TAB = Menu.FIRST + 15;

    protected final static int MENU_CD_ROM = Menu.FIRST + 16;

    /* filter file */
    /* CNcomment: 过滤 */
    protected final static int MENU_FILTER = Menu.FIRST + 17;

    /* show file */
    /* CNcomment: 文件显示 */
    protected final static int MENU_SHOW = Menu.FIRST + 18;

    public static boolean OPERATER_ENABLE = true;
    public static boolean SORT_ENABLE = true;
    public boolean FilterON_OFF = true;
    /* storage devices local mount path */
    /* CNcomment:存储设备本地挂载路径 */
    public String mountSdPath = "";

    /* access to NFS mount data */
    /* CNcomment:获取NFS挂载数据 */
    protected final static int MOUNT_DATA = 0;

    /* end of service search */
    /* CNcomment:结束服务搜索 */
    protected final static int END_SEARCH = 1;

    /* search result display */
    /* CNcomment:搜索结果显示 */
    protected final static int SEARCH_RESULT = 2;

    /* updata the list of files */
    /* CNcomment:更新文件列表 */
    protected final static int UPDATE_LIST = 3;

    /* network normal */
    /* CNcomment:网络正常 */
    protected final static int NET_NORMAL = 4;

    /* ISO mount successful */
    /* CNcomment:ISO挂载成功 */
    protected final static int ISO_MOUNT_SUCCESS = 5;

    /* network abnormal */
    /* CNcomment:网络异常 */
    protected final static int NET_ERROR = 6;

    /* add new mount */
    /* CNcomment:新增挂载 */
    protected final static int ADD_MOUNT = 7;

    /* ISO mount fail */
    /* CNcomment:ISO挂载失败 */
    protected final static int ISO_MOUNT_FAILD = 8;

    /* unmount */
    /* CNcomment:卸载挂载 */
    protected final static int DEL_MOUNT = 9;

    /* modify permissions */
    /* CNcomment:修改权限 */
    protected final static int CHMOD_FILE = 10;

    /* path display box */
    /* CNcomment: 路径显示框 */
    protected TextView pathTxt;

    /* wait for the dialog box */
    /* CNcomment: 等待对话框 */
    protected ProgressDialog progress;

    /* file list */
    /* CNcomment:文件列表 */
    protected List<File> arrayFile;

    /* directory list */
    /* CNcomment:目录列表 */
    protected List<File> arrayDir;

    /* file thumbnail list of resources */
    /* CNcomment:文件缩略图列表资源 */
    protected int gridlayout = 0;

    /* file list a list of resources */
    /* CNcomment:文件列表列表资源 */
    protected int listlayout = 0;

    /* share file read write mode */
    /* CNcomment:Share文件读写模式 */
    protected final static int SHARE_MODE = MODE_WORLD_READABLE
            + MODE_WORLD_WRITEABLE;

    /* file current path */
    /* CNcomment:当前文件路径 */
    protected String currentFileString = "";
    final static String MUSIC_PATH = "music_path";

    /* search thread of execution */
    /* CNcomment:搜索执行线程 */
    protected Timer searchTimer;

    /* previout search for the keyword */
    /* CNcomment:前一次搜索关键字 */
    protected String prevKeyword;

    /* the file is cut operation */
    /* CNcomment:文件是否剪切操作 */
    public boolean isFileCut = false;

    /* object lock */
    /* CNcomment: 对象锁 */
    protected byte[] lock = new byte[0];

    /* file data adapter */
    /* CNcomment:文件数据适配器 */
    protected FileAdapter adapter;

    /* file list collection */
    /* CNcomment:文件列表集合 */
    protected List<File> listFile;

    // begin add by qian_wei/zhou_yong 2011/10/20
    // for chmod the file
    // ISO file mount path
    protected final static String ISO_PATH = "/mnt/iso";

    protected final static String CDROM_PATH = "/mnt/sr0/sr01";

    // begin add by luoqiong 2012/12/7
    // for /mnt/iso/loopX
    protected String mISOLoopPath = "";
    // end add by luoqiong 2012/12/7


    /* want the open file */
    /* CNcomment:需要打开的文件 */
    protected File openFile;

    /* ISO file directory */
    /* CNcomment:ISO文件所在目录 */
    protected String prevPath = "";
    // end add by qian_wei/zhou_yong 2011/10/20

    // begin add by qian_wei/xiong_cuifan 2011/11/05
    // for click three buttons show toast
    // last click on the file path
    protected String preCurrentPath = "";

    /* whether to refresh the interface by returning */
    /* CNcomment: 是否通过返回刷新界面 */
    protected boolean keyBack = true;

    /* network file flag */
    /* CNcomment:网络文件标志 */
    protected boolean isNetworkFile = false;

    /* sort tips */
    /* CNcomment:排序方式提示 */
    private String[] sortMethod;

    /* type of filtering tips */
    /* CNcomment:过滤类型提示 */
    private String[] filterMethod;

    /* display prompt */
    /* CNcomment:显示方式提示 */
    private String[] showMethod;

    /* access to the file list thread base class */
    /* CNcomment:获取文件列表线程基类 */
    public MyThreadBase thread;

    // end add by qian_wei/xiong_cuifan 2011/11/08

    /* update the list of files */
    /* CNcomment:更新文件列表 */
    public abstract void updateList(boolean flag);

    /* populate the list of files to the data container */
    /* CNcomment: 填充文件列表到数据容器 */
    public abstract void fill(File file);

    /* get the message receiver object */
    /* CNcomment: 获得消息接收对象 */
    public abstract Handler getHandler();

    /* search results actions */
    /* CNcomment:搜索结果操作 */
    public abstract void operateSearch(boolean b);

    /* get list of files */
    /* CNcomment:获得文件列表 */
    public abstract void getFiles(String path);

    // 主要判断网络是否断开
    private ConnectivityManager mConnectivityManager = null;

    private BDInfo mBDInfo;

    private DVDInfo mDVDInfo;

    protected boolean mIsSupportBD = false;

    protected String mBDISOName = "";

    protected String mBDISOPath = "";

    // close Toast
    public static void cancleToast() {
        if (FileUtil.getToast() != null) {
            FileUtil.getToast().cancel();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showArray = new int[]{R.drawable.show_by_list,
                R.drawable.show_by_thum};

        sortArray = new int[]{R.drawable.sort_by_name,
                R.drawable.sort_by_size, R.drawable.sort_by_modifytime};

        filterArray = new int[]{R.drawable.filter_by_file,
                R.drawable.filter_by_picture, R.drawable.filter_by_audio,
                R.drawable.filter_by_video};

        // begin add by qian_wei/xiong_cuifan 2011/11/05
        // for click three buttons show toast
        sortMethod = getResources().getStringArray(R.array.sort_method);
        showMethod = getResources().getStringArray(R.array.show_method);
        filterMethod = getResources().getStringArray(R.array.filter_method);
        // end add by qian_wei/xiong_cuifan 2011/11/08

        arrayFile = new ArrayList<File>();

        arrayDir = new ArrayList<File>();

        listFile = new ArrayList<File>();
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        mBDInfo = new BDInfo();

        File file = new File("/system/lib/libdvdinfo_jni.so");
        if (file.exists())
            mDVDInfo = new DVDInfo();
        else
            mDVDInfo = null;

        mIsSupportBD = true;
        FilterType.filterType(CommonActivity.this);
        if (SystemProperties.get("ro.product.target").equals("telecom")) {
            //modify by libeibei for default file
            //sortCount = 0 ;
            sortCount = 2;
        }
    }

    protected BDInfo getBDInfo() {
        return mBDInfo;
    }

    protected DVDInfo getDVDInfo() {
        return mDVDInfo;
    }

    protected void launchHiBDPlayer(String path) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("bluray://"));
        intent.putExtra("path", path);
        intent.putExtra("isNetworkFile", isNetworkFile);
        int index = mBDISOName.lastIndexOf(".");
        if (index != -1) {
            intent.putExtra("BDISOName", mBDISOName.substring(0, index));
        }
        intent.putExtra("BDISOPath", mBDISOPath);
        startActivity(intent);
    }

    public boolean IsNetworkDisconnect() {
        boolean bDisconnect = false;
        if (null != mConnectivityManager) {
            NetworkInfo tmpInfo = mConnectivityManager.getActiveNetworkInfo();
            if ((null == tmpInfo) || (false == tmpInfo.isConnected())) {
                Toast.makeText(this,
                        getString(R.string.network_error_notbrowse),
                        Toast.LENGTH_LONG).show();
                bDisconnect = true;
            }
        }
        return bDisconnect;
    }

    /* open the file method */
    /* CNcomment: 打开文件方法 */

    public void openFile(CommonActivity activity, File f) {
        Intent intent = new Intent();
        String type = "*/*";
        type = FileUtil.getMIMEType(f, activity);
        if (activity.getIntent().getBooleanExtra("subFlag", false)) {
            intent.setClassName("com.hisilicon.android.videoplayer",
                    "com.hisilicon.android.videoplayer.activity.MediaFileListService");
            intent.putExtra("path", f.getPath());
            intent.putExtra("pathFlag", false);
            activity.startActivity(intent);
            activity.finish();
            return;
        } else if (type.equals("audio/*")) {

            intent.setClassName("com.hisilicon.android.music",
                    "com.hisilicon.android.music.TransitActivity");
            intent.setType(type);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(f), type);
        } else if (type.equals("image/*")) {
            /**
             * 调用HiGallery.apk的接口
             * 不支持视频层播放4K图片
             */
            intent.setClassName("com.hisilicon.android.gallery3d",
                    "com.hisilicon.android.gallery3d.list.ImageFileListService");
            SharedPreferences p = getSharedPreferences("musicPath", SHARE_MODE);
            String path = p.getString(MUSIC_PATH, "");
            intent.putExtra("path", path);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(f), "image/*");
            startService(intent);
            /**
             * 调用HiGalleryL.apk的接口
             * 支持视频层播放4K图片
             */
//            intent.setClassName("com.android.nativeimage.gallery", "com.android.nativeimage.gallery.MainActivity");
//            SharedPreferences p = getSharedPreferences("musicPath", SHARE_MODE);
//            String path = p.getString(MUSIC_PATH, "");
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.putExtra("path", path);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(Uri.fromFile(f), "image/*");
//            startActivity(intent);
            return;
        } else if (type.equals("video/*")) {
            intent.setClassName("com.hisilicon.android.videoplayer",
                    "com.hisilicon.android.videoplayer.activity.MediaFileListService");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(f), type);
            intent.putExtra("sortCount", sortCount);
            activity.startService(intent);
            return;

        } else if (type.equals("doc/*")) {//ADD by ZHONGPUJUN for DOC R160329
            Intent mintent = new Intent("android.intent.action.VIEW");
            mintent.addCategory("android.intent.category.DEFAULT");
            mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mintent.setDataAndType(Uri.fromFile(f), "application/vnd.ms-powerpoint");
            try {
                activity.startActivity(mintent);
            } catch (Exception e) {
                Toast.makeText(CommonActivity.this, getString(R.string.no_file_support), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return;
        } else if (type.equals("video/iso") || type.equals("video/dvd")) {
            intent.setClassName("com.hisilicon.android.videoplayer",
                    "com.hisilicon.android.videoplayer.activity.MediaFileListService");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            type = "video/iso";
            intent.setDataAndType(Uri.fromFile(f), type);
            intent.putExtra("sortCount", sortCount);
            activity.startService(intent);
            return;
        } else if (type.equals("apk/*")) {
          /*intent.setClassName("com.android.packageinstaller",
          "com.android.packageinstaller.PackageInstallerActivity");*/
            intent.setAction("android.intent.action.INSTALL_PACKAGE");
            intent.setDataAndType(Uri.fromFile(f),
                    "application/vnd.android.package-archive");
        } else if (f.getAbsolutePath().toLowerCase().contains(".bdmv")) {
            if (mIsSupportBD) {
                launchHiBDPlayer(f.getAbsolutePath());
            }
            return;
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("sortCount", sortCount);
            Log.w("TYPE", " = " + type);
            intent.setDataAndType(Uri.fromFile(f), type);
        }
        if (!activity.getIntent().getBooleanExtra("subFlag", false)) {
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(CommonActivity.this, getString(R.string.no_file_support), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /* button event */
    /* CNcomment: 按钮点击事件 */

    public OnClickListener clickListener = new OnClickListener() {
        public void onClick(View v) {
            Log.d(TAG, "click view = " + v.getId() + "sort but id =" + R.id.sortBut);
            if (v.equals(showBut)) {
                if (showCount == showArray.length - 1) {
                    showCount = 0;
                } else {
                    showCount++;
                }
                showBut.setImageResource(showArray[showCount]);
                FileUtil.showClickToast(CommonActivity.this,
                        showMethod[showCount]);
                if (showCount == 0) {
                    gridView.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);

                }

                /* thumbnails */
                /* CNcomment:缩略图方式 */
                else if (showCount == 1) {
                    gridView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                }
            } else if (v.equals(sortBut)) {
                if (sortCount == sortArray.length - 1) {
                    sortCount = 0;
                } else {
                    sortCount++;
                }
                sortBut.setImageResource(sortArray[sortCount]);
                // begin add by qian_wei/xiong_cuifan 2011/11/05
                // for click three buttons show toast
                FileUtil.showClickToast(CommonActivity.this, sortMethod[sortCount]);
                // end add by qian_wei/xiong_cuifan 2011/11/08
            } else if (v.equals(filterBut)) {
                if (filterCount == filterArray.length - 1) {
                    filterCount = 0;
                } else {
                    filterCount++;
                }
                filterBut.setImageResource(filterArray[filterCount]);
                FileUtil.showClickToast(CommonActivity.this,
                        filterMethod[filterCount]);
            }
            updateList(true);
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            // previous
            case KeyEvent.KEYCODE_PAGE_UP:
                if (!pathTxt.getText().toString().equals("")) {
                    if (listView.getVisibility() == View.VISIBLE) {
                        if (listView.getFirstVisiblePosition() >= 11) {
                            listView.setSelection(listView.getFirstVisiblePosition() - 11);
                        } else {
                            listView.setSelection(0);
                        }
                        return true;
                    }
                }

                // next page
            case KeyEvent.KEYCODE_PAGE_DOWN:
                if (!pathTxt.getText().toString().equals("")) {
                    if (listView.getVisibility() == View.VISIBLE) {
                        listView.setSelection(listView.getLastVisiblePosition());
                        return true;
                    }
                }
        }
        return false;
    }

    /* menu operation */
    /* CNcomment:菜单操作 */

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            /* add audio filter format */
            /* CNcomment: 添加音频过滤格式 */
            case ADD_MENU_AUDIO:
                SharedPreferences addAudio = getSharedPreferences("AUDIO",
                        SHARE_MODE);
                FileMenu.filterType(CommonActivity.this, ADD_MENU_AUDIO, addAudio);
                break;

            /* add video filter format */
            /* CNcomment:添加视频过滤格式 */
            case ADD_MENU_VIDEO:
                SharedPreferences addVideo = getSharedPreferences("VIDEO",
                        SHARE_MODE);
                FileMenu.filterType(CommonActivity.this, ADD_MENU_VIDEO, addVideo);
                break;

            /* add picturn filter format */
            /* CNcomment:添加图片过滤格式 */
            case ADD_MENU_IMAGE:
                SharedPreferences addImage = getSharedPreferences("IMAGE",
                        SHARE_MODE);
                FileMenu.filterType(CommonActivity.this, ADD_MENU_IMAGE, addImage);
                break;

            /* remove audio filter format */
            /* CNcomment:移除音频过滤格式 */
            case REMOVE_MENU_AUDIO:
                SharedPreferences removeAudio = getSharedPreferences("AUDIO",
                        SHARE_MODE);
                FileMenu.filterType(CommonActivity.this, REMOVE_MENU_AUDIO,
                        removeAudio);
                break;

            /* remove video filter format */
            /* CNcomment:移除视频过滤格式 */
            case REMOVE_MENU_VIDEO:
                SharedPreferences removeVideo = getSharedPreferences("VIDEO",
                        SHARE_MODE);
                FileMenu.filterType(CommonActivity.this, REMOVE_MENU_VIDEO,
                        removeVideo);
                break;

            /* remove picturn filter format */
            /* CNcomment: 移除图片过滤格式 */
            case REMOVE_MENU_IMAGE:
                SharedPreferences removeImage = getSharedPreferences("IMAGE",
                        SHARE_MODE);
                FileMenu.filterType(CommonActivity.this, REMOVE_MENU_IMAGE,
                        removeImage);
                break;

            /* label operation */
            /* CNcomment:标签操作 */
            case MENU_TAB:
//      if (item.getTitle().equals(
//          getResources().getString(R.string.hide_tab))) {
//        TabBarExample.getWidget().setVisibility(View.GONE);
//        item.setTitle(R.string.show_tab);
//      } else {
//        TabBarExample.getWidget().setVisibility(View.VISIBLE);
//        item.setTitle(R.string.hide_tab);
//      }
                break;
        }
        return true;
    }

    /**
     * 汉语拼音，首字母匹配
     *
     * @param keyword
     * @param filename
     * @return
     */
    private boolean fileFilter(String keyword, String filename) {
        boolean ret = false;
        String prefixString = keyword.toString().toLowerCase();
        String filePinYin = getPinYin(filename);
        PinYin4j pinyin = new PinYin4j();
        Log.e("Search", "fileFilter() -- filename = " + filename + ",FilePinYin = " + filePinYin);
        Set<String> Set = pinyin.getPinyin(filePinYin);
        Iterator iterator = Set.iterator();

        while (iterator.hasNext()) {
            final String pinyin1 = iterator.next().toString().toLowerCase();
            int len = prefixString.length();
            if (len > pinyin1.length())
                break;
            if (pinyin1.startsWith(prefixString)) {
                return true;
            }
        }
        return ret;
    }

    /**
     * 汉语拼音，关键字搜索
     *
     * @param keyword
     * @param filename
     * @return
     */
    private boolean pinyinFilter(String keyword, String filename) {
        boolean ret = false;
        String prefixString = keyword.toString().toLowerCase();
        String filePinYin = getPinYin(filename);
        Log.e(TAG, "汉语文件名称 = " + filename + ",搜索关键字 = " + prefixString);
        if (filePinYin.contains(prefixString)) ret = true;
        return ret;
    }

    /**
     * 获取汉字字符串的汉语拼音，英文字符不变
     */
    public static String getPinYin(String chines) {
        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(nameChar[i]);
            }
        }
        return sb.toString();
    }

    public static boolean isChineseChar(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }

    /* search file /folder actions */
    /* CNcomment: 搜索文件/文件夹操作 */

    /* @param keyword */
    /* CNcomment: 关键字 */
    /* @param fileRoot */
    /* CNcomment:目标目录地址 */
    /* return search files set */
    /* CNcomment: return 搜索后的文件集合 */
    public void searchFile(String keyword, String fileRoot) {
        final String TAG = "Search";
        Log.i(TAG, "searchFile()--" + "keyword = " + keyword + ",fileRoot = " + fileRoot);
        long start = System.currentTimeMillis();
        long max_stack_length = 0;
        long max_array_length = 0;
        long total_num = 0;
        int lenth = keyword.length();
        Stack<Result> stack = new Stack<Result>();
        Result result = new Result();
        result.setCurrentPath(fileRoot);
        result.setCurrentFileArray(new File(fileRoot).listFiles());
        if (max_array_length < result.getCurrentFileArray().length) {
            max_array_length = result.getCurrentFileArray().length;
        }
        result.setIndex(0);
        int i = 0;

        try {
            while (true) {
                Log.i(TAG, "result.getIndex() = " + result.getIndex() +
                        " ,  result.getCurrentFileArray().length = " + result.getCurrentFileArray().length);

                if (result.getCurrentFileArray() == null || result.getCurrentFileArray().length == 0 || i >= result.getCurrentFileArray().length) {
                    if (stack.empty()) {
                        Log.w(TAG, "aaa = "
                                + (System.currentTimeMillis() - start));
                        Log.w(TAG, "bbb = " + max_stack_length);
                        Log.w(TAG, "ccc = " + max_array_length);
                        Log.w(TAG, "ddd = " + total_num);
                        return;
                    } else {
                        /* get the top level elernent in the stack */
                        /* CNcomment:获取栈中的顶层元素 */
                        result = stack.pop();
                        result.show();
                        Log.w(TAG, " = " + stack.size());
                    }
                }

                for (i = result.getIndex(); i < result.getCurrentFileArray().length; i++) {
                    // for search the file is a link file
                    // waiting for the dialog box disappears,the search stops
                    if (progress != null && progress.isShowing()) {
                        // search results for more than 10000 records ,the
                        // search stops
                        if (numberLimmit()) {
                            File file = result.getCurrentFileArray()[i];
                            total_num++;
                            String name = file.getName();
                            String temp = name;
                            Log.e(TAG, "file name = " + name + ",file.isFile() = " + file.isFile()
                                    + ",isChineseChar(temp) =" + isChineseChar(temp)
                                    + ",isChineseChar(keyword) = " + isChineseChar(keyword));
                            if (temp.length() > lenth) {
                                temp = name.substring(0, lenth);
                            }
                            if (file.isFile()) {
                                if (isChineseChar(temp) && !isChineseChar(keyword)) {
                                    if (fileFilter(keyword, temp)) {
                                        Log.e(TAG, "中文文件，名称开头是 " + keyword);
                                        arrayFile.add(file);
                                    } else if (pinyinFilter(keyword, name)) {
                                        Log.e(TAG, "中文文件，名称包含 " + keyword);
                                        arrayFile.add(file);
                                    }
                                } else {
                                    if (name.toLowerCase().startsWith(keyword.toLowerCase())) {
                                        /* file has been added */
                                        /* CNcomment: 文件是否已添加 */
                                        Log.e(TAG, "英文文件，名称开头是 " + keyword);
                                        arrayFile.add(file);
                                    } else if (name.toLowerCase().contains(keyword.toLowerCase())) {
                                        Log.e(TAG, "英文文件，名称包含 " + keyword);
                                        arrayFile.add(file);
                                    }
                                }
                            } else {
                                if (dirNotExist(stack, file)) {
                                    if (isChineseChar(temp) && !isChineseChar(keyword)) {
                                        if (fileFilter(keyword, temp)) {
                                            Log.e(TAG, "中文文件夹，名称开头是 " + keyword);
                                            arrayDir.add(file);
                                        } else if (pinyinFilter(keyword, name)) {
                                            Log.e(TAG, "中文文件夹，名称包含 " + keyword);
                                            arrayFile.add(file);
                                        }
                                    } else {
                                        if (name.toLowerCase().startsWith(keyword.toLowerCase())) {
                                            // directory has been added
                                            // CNcomment:目录是否已添加
                                            Log.e(TAG, "英文文件夹，名称开头是 " + keyword);
                                            arrayDir.add(file);
                                        } else if (name.toLowerCase().contains(keyword.toLowerCase())) {
                                            Log.e(TAG, "英文文件夹，名称包含 " + keyword);
                                            arrayFile.add(file);
                                        }
                                    }
                                    result.setIndex(i + 1);
                                    // the parent directory is pushed onto the
                                    // stack
                                    // CNcomment: 将父目录压入栈中
                                    result.show();
                                    stack.push(result);
                                    Log.w(TAG, "eee = " + stack.size());
                                    Log.w(TAG, "fff = " + (arrayDir.size() + arrayFile.size()));
                                    if (max_stack_length < stack.size()) {
                                        max_stack_length = stack.size();
                                    }
                                    result = new Result();
                                    result.setCurrentPath(file
                                            .getCanonicalPath());
                                    result.setCurrentFileArray(file.listFiles());
                                    if (result.getCurrentFileArray() == null) {
                                        // do nothing
                                    } else if (max_array_length < result
                                            .getCurrentFileArray().length) {
                                        max_array_length = result
                                                .getCurrentFileArray().length;
                                    }
                                    result.setIndex(0);
                                    i = 0;
                                    break;
                                } else {
                                    continue;
                                }
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                } // end for
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception = " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * folder in the stack if there are
     *
     * @param stack //CNcomment:栈
     * @param file  //current file
     * @return // folder in the stack if there are
     */
    private boolean dirNotExist(Stack<Result> stack, File file) {
        try {
            for (int i = stack.size() - 1; i >= 0; i--) {
                File file1 = stack.get(i).getCurrentFileArray()[stack.get(i)
                        .getIndex() - 1];
                if (file1.getCanonicalPath().equals(file.getCanonicalPath())) {
                    return false;
                } else {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * restriction on the number of search results
     *
     * @return if the number reaches the limit
     */
    private boolean numberLimmit() {
        int size = arrayFile.size() + arrayDir.size();
        if (size >= 10000) {
            return false;
        } else {
            return true;
        }
    }

    /* pop-up search dialog */
    /* CNcomment: 弹出搜索对话框 */

    protected void searchFileDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        View myView = factory.inflate(R.layout.new_alert, null);
        TextView newTextView = (TextView) myView.findViewById(R.id.new_view);
        Button searchOkButton = (Button) myView.findViewById(R.id.button1);
        Button searchCancleButton = (Button) myView.findViewById(R.id.button2);
        newTextView.setText(getString(R.string.input_keyword));
        final EditText myEditText = (EditText) myView
                .findViewById(R.id.new_edit);
        myEditText.setText(prevKeyword);
        final AlertDialog renameDialog = new AlertDialog.Builder(this).create();
        renameDialog.setView(myView);
        renameDialog.show();

        searchOkButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                clearAdapter();
                doSearch(myEditText);
                renameDialog.dismiss();
            }
        });

        searchCancleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                renameDialog.dismiss();
            }
        });

    }

    // perform a search operation
    private void doSearch(EditText myEditText) {
        prevKeyword = myEditText.getText().toString();
        setFlag(true);
        // search the keyword is empty
        if ("".equals(prevKeyword)) {
            arrayFile.clear();
            arrayDir.clear();
            setFlag(false);
            updateList(true);
        } else {
            arrayFile.clear();
            arrayDir.clear();
            progress = new ProgressDialog(CommonActivity.this);
            progress.show();
            searchTimer = new Timer();
            searchTimer.schedule(new TimerTask() {
                public void run() {
                    synchronized (lock) {
                        long start = System.currentTimeMillis();
                        Log.w("Search", "Keyword = " + prevKeyword
                                + " ,currentFileString = " + currentFileString);

                        searchFile(prevKeyword, currentFileString);
                        long end = System.currentTimeMillis();
                        Log.w("Search", " Searched time = " + (end - start));
                        // for while is searching,quit the application , then
                        // error
                        if (!CommonActivity.this.isFinishing()) {
                            getHandler().sendEmptyMessage(SEARCH_RESULT);
                        }
                        searchTimer.cancel();
                        searchTimer.purge();
                    }
                }
            }, 500);
        }
    }

    private boolean isSearch = false;

    /**
     * synchronous setting isSearch
     *
     * @param b isSearch value
     */
    protected synchronized void setFlag(boolean b) {
        this.isSearch = b;
    }

    /**
     * synchronous setting isSearch
     *
     * @return isSearch value
     */
    protected synchronized boolean getFlag() {
        return isSearch;
    }

    /**
     * escape path
     *
     * @param path
     * @return // after escape path
     */
    public static String tranString(String path) {
        String tranPath = "";
        for (int i = 0; i < path.length(); i++) {
            tranPath += "\\" + path.substring(i, i + 1);
        }

        return tranPath;
    }

    /**
     * clear files list
     */
    public void clearList(List<File> fileDir, List<File> file) {
        if (fileDir != null) {
            fileDir.clear();
        }
        if (file != null) {
            file.clear();
        }
    }

    /* clear the value in the file adapter */
    /* CNcomment: 清除文件适配器中的值 */
    public void clearAdapter() {
        listFile.clear();
        if (listView.getVisibility() == View.VISIBLE) {
            adapter = new FileAdapter(this, listFile, listlayout);
            listView.setAdapter(adapter);
        } else if (gridView.getVisibility() == View.VISIBLE) {
            adapter = new FileAdapter(this, listFile, gridlayout);
            gridView.setAdapter(adapter);
        }
    }
    // for chmod the file

    /**
     * modify the file permissions
     *
     * @param path
     */
    public static void chmodFile(String path) {
    }
    // for broken into the directory contains many files,click again error

    public class MyThreadBase extends Thread {
        private boolean runFlag = false;

        public synchronized void setStopRun(boolean flag) {
            this.runFlag = flag;
        }

        public synchronized boolean getStopRun() {
            return runFlag;
        }

        public void run() {
        }
    }

    /**
     * whether the thread is running
     *
     * @param thrd //thread
     * @return
     */
    public boolean threadBusy(Thread thrd) {
        if (thrd == null)
            return false;

        if ((thrd.getState() != Thread.State.TERMINATED)
                && (thrd.getState() != Thread.State.NEW)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * under the program is running state decided whether to open a new thread
     *
     * @param thrd
     */
    public void waitThreadToIdle(Thread thrd) {
        while (threadBusy(thrd)) {
            try {
                Log.w("THREAD", "THREAD");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 预览
     *
     * @param file
     */
    protected void preview(File file) {
        PreviewDialog dialog = new PreviewDialog(this, file.getAbsolutePath(), this);
        dialog.show();
    }


//    protected void preview(File file) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        //add by libeibei for Thumbnail begin
//        Bitmap bitmap = revitionImageSize(file);
//        if (bitmap != null) {
//            Log.e(TAG, "Image File, add Thumbnail into preview!!");
//            ImageView view = new ImageView(this);
//            view.setImageBitmap(bitmap);
//            dialog.setView(view);
//        }
//        //add by libeibei for Thumbnail end
//        StringBuffer sb = new StringBuffer();
//        sb.append("\n" + file.getName() + "\n\n");
//        sb.append(getString(R.string.file_size_1) + FileUtil.fileSizeMsg(file) + "\n\n");
//        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(file));
//        if (type != null && !type.equals("null"))
//            sb.append(getString(R.string.file_type) + type + "\n\n");
//        else {
//            sb.append(getString(R.string.file_type) + FileUtil.getExtension(file) + "\n\n");
//        }
//        long time = file.lastModified();
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(time);
//        sb.append(getString(R.string.last_modified) + cal.getTime().toLocaleString());
//        dialog.setMessage(sb.toString());
//        dialog.create().show();
//    }

    /**
     * add by libeibei for Thumbnail
     * date:20180703
     * 根据指定的图像路径和大小来获取缩略图
     *
     * @param file 图片文件
     * @return 生成的缩略图
     */
    public Bitmap revitionImageSize(File file) {
        Bitmap bitmap = null;
        int width = 0;
        int height = 0;
        int maxWidth = 0;
        int maxHeight = 0;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            width = options.outWidth;
            height = options.outHeight;
            Log.e(TAG, "预览图片,width = " + width + " , height = " + height);
            if (width <= 0 || height <= 0) return null;
            if (width > height) {//宽图
                maxHeight = 200;
                maxWidth = width * maxHeight / height;
            } else {//高图
                maxWidth = 200;
                maxHeight = height * maxWidth / width;
            }
            Log.e(TAG, "缩略图,width = " + maxWidth + " , height = " + maxHeight);
            in.close();
            int i = 0;
            while (true) {
                if ((options.outWidth >> i <= maxWidth)
                        && (options.outHeight >> i <= maxHeight)) {
                    in = new BufferedInputStream(
                            new FileInputStream(file));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }
}
