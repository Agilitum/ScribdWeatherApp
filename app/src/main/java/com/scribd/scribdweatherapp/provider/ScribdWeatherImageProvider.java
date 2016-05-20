package com.scribd.scribdweatherapp.provider;

import android.graphics.Bitmap;

import com.android.volley.RequestQueue;

/**
 * Created by ludwig on 20/05/16.
 */
public interface ScribdWeatherImageProvider {

	public Bitmap getImage(int code,RequestQueue requestQueue, WeatherImageListener listener);

	public static interface WeatherImageListener {
		public void onImageReady(Bitmap image);
	}
}
