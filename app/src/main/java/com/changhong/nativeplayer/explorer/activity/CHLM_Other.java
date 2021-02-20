package com.changhong.nativeplayer.explorer.activity;

import com.changhong.nativeplayer.R;
import com.changhong.nativeplayer.explorer.common.AnimationButton;
import com.changhong.nativeplayer.explorer.common.CommonActivity;
import com.changhong.nativeplayer.explorer.common.ImageReflect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.Toast;

public class CHLM_Other extends Activity implements OnClickListener, OnKeyListener{
	
	private AnimationButton samBaButton;
  	private AnimationButton UPnPButton;
  	private AnimationButton nfsButton;
  	
  	private int focusViewId;
  	private ImageView refImageView[] = new ImageView[3];

  	private IntentFilter mIntenFilter = null;
  	private BroadcastReceiver mReceiver = null;
	    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.chlm_other);
		
		getViewInit();
		
		UPnPButton.requestFocus();

	}
	public void getViewInit(){
		samBaButton = (AnimationButton) findViewById(R.id.button_samba);
	    nfsButton = (AnimationButton) findViewById(R.id.button_nfs);
	    UPnPButton = (AnimationButton) findViewById(R.id.button_UPnP);
	    
	    samBaButton.setText(getResources().getString(R.string.lan_tab_title));
	    nfsButton.setText(getResources().getString(R.string.nfs_tab_title));
	    UPnPButton.setText(getResources().getString(R.string.upnp_tab_title));
	    
	    samBaButton.setImage(R.drawable.music_but);
	    nfsButton.setImage(R.drawable.dongman);
	    UPnPButton.setImage(R.drawable.zongyi);
	    
	    nfsButton.setImageBg(R.drawable.shadow_transverse);
	    samBaButton.setImageBg(R.drawable.shadow_square);
        UPnPButton.setImageBg(R.drawable.shadow_square);
        
        samBaButton.setOnClickListener(this);
        nfsButton.setOnClickListener(this);
        UPnPButton.setOnClickListener(this);
        
        samBaButton.setOnKeyListener(this);
        nfsButton.setOnKeyListener(this);
        UPnPButton.setOnKeyListener(this);

        refImageView[0] = (ImageView) findViewById(R.id.hot_reflected_img_0);
        refImageView[1] = (ImageView) findViewById(R.id.hot_reflected_img_1);
        refImageView[2] = (ImageView) findViewById(R.id.hot_reflected_img_2);
        ReflectedImage();
        mIntenFilter = new IntentFilter();
	    mIntenFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
	    mIntenFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
	    mIntenFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

	    mIntenFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	    mIntenFilter.addAction(ConnectivityManager.INET_CONDITION_ACTION);
	    
        mReceiver = new BroadcastReceiver() {
        
	        public void onReceive(Context context, Intent intent) {
//	            if (0 != tabHost.getCurrentTab()) {
	                boolean bIsConnect = true;
	                final String action = intent.getAction();
	                if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
	                    final NetworkInfo networkInfo = (NetworkInfo) intent
	                            .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
	                    bIsConnect = networkInfo != null
	                            && networkInfo.isConnected();
	                } else if (action
	                        .equals(ConnectivityManager.CONNECTIVITY_ACTION)
	                        || action
	                                .equals(ConnectivityManager.INET_CONDITION_ACTION)) {
	                    NetworkInfo info = (NetworkInfo) (intent
	                            .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO));
	                    bIsConnect = info.isConnected();
	                }
	                ;
	                if (false == bIsConnect) {
	                    Toast.makeText(
	                    		CHLM_Other.this,
	                            getString(R.string.network_error_exitnetbrowse),
	                            Toast.LENGTH_LONG).show();
//	                    tabHost.setCurrentTab(0);
	                }
//	            }
	        };
	    };
	    registerReceiver(mReceiver, mIntenFilter);
	}
	private void ReflectedImage() {
        {
            refImageView[0].setImageBitmap(ImageReflect
                    .createCutReflectedImage(ImageReflect.convertViewToBitmap(
                    		CHLM_Other.this, R.drawable.dongman), 0, 650));
            //refImageView[1].setImageBitmap(ImageReflect
            //        .createCutReflectedImage(ImageReflect.convertViewToBitmap(
             //       		CHLM_Other.this, R.drawable.music_but), 0, 317));
        }
    }
	@Override
    protected void onResume() {
        if ("true".equals(SystemProperties.get("ro.samba.quick.search"))) {
            Intent sambaService = new Intent(CHLM_Other.this,
                    SambaService.class);
            startService(sambaService);
        }
        super.onResume();
  }
	/**
	 * cancle Toast
	 * CNcomment:
	 */
	protected void onStop() {
		super.onStop();
		CommonActivity.cancleToast();
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
        Intent sambaService = new Intent(CHLM_Other.this, SambaService.class);
        stopService(sambaService);
	}
	@Override
	public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		
		return false;
	}

	@Override
	public void onClick(View arg0) {
		
		focusViewId = arg0.getId();
		switch (focusViewId) {

	    case R.id.button_samba:
	            Intent sambaIntent=new Intent(CHLM_Other.this, SambaActivity.class);
	            startActivity(sambaIntent);
	      break;

	    case R.id.button_nfs:
	            Intent nfsIntent=new Intent(CHLM_Other.this, NFSActivity.class);
	            startActivity(nfsIntent);
	      break;

	    case R.id.button_UPnP:
	      try {
	            Intent intent = new Intent();
	            ComponentName componentName = new ComponentName("com.hisilicon.dlna.mediacenter", "com.hisilicon.dlna.mediacenter.MediaCenterActivity");
	            intent.setComponent(componentName);
	            startActivity(intent);
	          } catch (Exception e) {
	            Toast.makeText(CHLM_Other.this, getString(R.string.no_activity_info), Toast.LENGTH_SHORT).show();
	          }
	      break;
	      default:
	    	  break;
		}
	}

}
