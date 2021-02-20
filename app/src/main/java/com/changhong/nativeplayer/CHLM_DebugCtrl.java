package com.changhong.nativeplayer;

import android.util.Log;

public class CHLM_DebugCtrl {
	private final static String LOGI = "info";
	private final static String LOGW= "warning";
	private final static String LOGE = "error";
	public String DEBUG_TAG;	
	public boolean DEBUG_FLAG;
	
	public CHLM_DebugCtrl(String debug_tag, boolean debug_flag)
	{
		this.DEBUG_TAG = debug_tag;
		this.DEBUG_FLAG = debug_flag;
		
	}
	public void CHLM_DEBUG_FUN(String LogClass, String LogContent)
	{
		if(DEBUG_FLAG == true)
		{
			if(LogClass.equals(LOGI))
			{
				Log.i(DEBUG_TAG, LogContent);
			}else if(LogClass.equals(LOGW))
			{
				Log.w(DEBUG_TAG, LogContent);
			}else if(LogClass.equals(LOGE))
			{
				Log.e(DEBUG_TAG, LogContent);
			}
		}
	}
}
