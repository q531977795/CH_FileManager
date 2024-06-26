/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.changhong.nativeplayer.explorer.common;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.FloatMath;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;

public class DecodeUtils {
    private static final String TAG = "DecodeService";



    public static Bitmap requestDecode( final String filePath,
            Options options) {
        if (options == null) options = new Options();
        //jc.setCancelListener(new DecodeCanceller(options));
        return ensureGLCompatibleBitmap(
                BitmapFactory.decodeFile(filePath, options));
    }

    public static Bitmap requestDecode( FileDescriptor fd, Options options) {
        if (options == null) options = new Options();
        //jc.setCancelListener(new DecodeCanceller(options));
        return ensureGLCompatibleBitmap(
                BitmapFactory.decodeFileDescriptor(fd, null, options));
    }

    

    public static Bitmap requestDecode( byte[] bytes, int offset,
            int length, Options options) {
        if (options == null) options = new Options();
        //jc.setCancelListener(new DecodeCanceller(options));
        return ensureGLCompatibleBitmap(
                BitmapFactory.decodeByteArray(bytes, offset, length, options));
    }

    

    public static Bitmap requestDecode( FileDescriptor fd,
            Options options, int targetSize) {
        if (options == null) options = new Options();
        //jc.setCancelListener(new DecodeCanceller(options));

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
       
        options.inSampleSize = BitmapUtils.computeSampleSizeLarger(
                options.outWidth, options.outHeight, targetSize);
        options.inJustDecodeBounds = false;

        Bitmap result = BitmapFactory.decodeFileDescriptor(fd, null, options);

        if(result == null)
        {
            return null;
        }

        // We need to resize down if the decoder does not support inSampleSize.
        // (For example, GIF images.)
        result = BitmapUtils.resizeDownIfTooBig(result, targetSize, true);
        return ensureGLCompatibleBitmap(result);
    }

    public static Bitmap decodeThumbnail(
             FileDescriptor fd, Options options, int targetSize) {
        if (options == null) options = new Options();

        //jc.setCancelListener(new DecodeCanceller(options));

        BitmapRegionDecoder decoder = requestCreateBitmapRegionDecoder(fd,false);
        //BitmapRegionDecoder decoder = null;

        if (decoder == null)
        {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fd, null, options);
        }
        else
        {
            options.outWidth = decoder.getWidth();
            options.outHeight = decoder.getHeight();
        }

        
        int w = options.outWidth;
        int h = options.outHeight;
            options.inPreferredConfig = Config.ARGB_8888;
        {
            // We center-crop the original image as it's micro thumbnail. In this case,
            // we want to make sure the shorter side >= "targetSize".
            float scale = (float) targetSize / Math.min(w, h);
            options.inSampleSize = BitmapUtils.computeSampleSizeLarger(scale);

            // For an extremely wide image, e.g. 300x30000, we may got OOM when decoding
            // it for TYPE_MICROTHUMBNAIL. So we add a max number of pixels limit here.
            final int MAX_PIXEL_COUNT = 640000; // 400 x 1600
            if ((w / options.inSampleSize) * (h / options.inSampleSize) > MAX_PIXEL_COUNT) {
                options.inSampleSize = BitmapUtils.computeSampleSize(
                        FloatMath.sqrt((float) MAX_PIXEL_COUNT / (w * h)));
            }
        } 

        Bitmap result = null;
        if(decoder!=null)
        {
            result = decoder.decodeRegion(new Rect(0, 0, w, h), options);
        }
        else
        {
            options.inJustDecodeBounds = false;
            result = BitmapFactory.decodeFileDescriptor(fd, null, options);	
        }

        //Bitmap result = decoder.decodeRegion(new Rect(0, 0, w, h), options);
        if (result == null) return null;

        // We need to resize down if the decoder does not support inSampleSize
        // (For example, GIF images)
        float scale = (float) targetSize / (Math.min(result.getWidth(), result.getHeight()));

        if (scale <= 0.5) result = BitmapUtils.resizeBitmapByScale(result, scale, true);
		
        return ensureGLCompatibleBitmap(result);
    }
    /**
     * Decodes the bitmap from the given byte array if the image size is larger than the given
     * requirement.
     *
     * Note: The returned image may be resized down. However, both width and height must be
     * larger than the <code>targetSize</code>.
     */
    public static Bitmap requestDecodeIfBigEnough( byte[] data,
            Options options, int targetSize) {
        if (options == null) options = new Options();
        //jc.setCancelListener(new DecodeCanceller(options));

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        
        if (options.outWidth < targetSize || options.outHeight < targetSize) {
            return null;
        }
        options.inSampleSize = BitmapUtils.computeSampleSizeLarger(
                options.outWidth, options.outHeight, targetSize);
        options.inJustDecodeBounds = false;
        return ensureGLCompatibleBitmap(
                BitmapFactory.decodeByteArray(data, 0, data.length, options));
    }

    public static Bitmap requestDecode(
            FileDescriptor fileDescriptor, Rect paddings, Options options) {
        if (options == null) options = new Options();
        //jc.setCancelListener(new DecodeCanceller(options));
        return ensureGLCompatibleBitmap(BitmapFactory.decodeFileDescriptor
                (fileDescriptor, paddings, options));
    }

    // TODO: This function should not be called directly from
    // DecodeUtils.requestDecode(...), since we don't have the knowledge
    // if the bitmap will be uploaded to GL.
    public static Bitmap ensureGLCompatibleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.getConfig() != null) return bitmap;
        Bitmap newBitmap = bitmap.copy(Config.ARGB_8888, false);
        if(!bitmap.isRecycled()){
            bitmap.recycle();
            System.gc();
        }
        return newBitmap;
    }

    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
             byte[] bytes, int offset, int length,
            boolean shareable) {
        if (offset < 0 || length <= 0 || offset + length > bytes.length) {
            throw new IllegalArgumentException(String.format(
                    "offset = %s, length = %s, bytes = %s",
                    offset, length, bytes.length));
        }

        try {
            return BitmapRegionDecoder.newInstance(
                    bytes, offset, length, shareable);
        } catch (Throwable t)  {
            Log.w(TAG, t);
            return null;
        }
    }

    @SuppressLint("NewApi")
    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
             String filePath, boolean shareable) {
        try {
            return BitmapRegionDecoder.newInstance(filePath, shareable);
        } catch (Throwable t)  {
            Log.w(TAG, t);
            return null;
        }
    }

    @SuppressLint("NewApi")
    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
             FileDescriptor fd, boolean shareable) {
        try {
            return BitmapRegionDecoder.newInstance(fd, shareable);
        } catch (Throwable t)  {
            Log.w(TAG, t);
            return null;
        }
    }

    @SuppressLint("NewApi")
    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
             InputStream is, boolean shareable) {
        try {
            return BitmapRegionDecoder.newInstance(is, shareable);
        } catch (Throwable t)  {
            // We often cancel the creating of bitmap region decoder,
            // so just log one line.
            Log.w(TAG, "requestCreateBitmapRegionDecoder: " + t);
            return null;
        }
    }

    @SuppressLint("NewApi")
    public static BitmapRegionDecoder requestCreateBitmapRegionDecoder(
             Uri uri, ContentResolver resolver,
            boolean shareable) {
        ParcelFileDescriptor pfd = null;
        try {
            pfd = resolver.openFileDescriptor(uri, "r");
            return BitmapRegionDecoder.newInstance(
                    pfd.getFileDescriptor(), shareable);
        } catch (Throwable t) {
            Log.w(TAG, t);
            return null;
        } finally {
            Utils.closeSilently(pfd);
        }
    }
}
