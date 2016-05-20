package com.scribd.scribdweatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.scribd.scribdweatherapp.model.CityResult;
import com.scribd.scribdweatherapp.model.Weather;
import com.scribd.scribdweatherapp.provider.ScribdWeatherImageProvider;
import com.scribd.scribdweatherapp.provider.WeatherImageProvider;
import com.scribd.scribdweatherapp.provider.YahooAPIClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

	private String city;
	private String woeid;
	private String cityName;
	private String country;

	private RequestQueue requestQueue;

	@Bind(R.id.button_get_weather_activtiy_main)
	Button weatherRequestButton;

	@Bind(R.id.autoCompleteText_city_name_activity_main)
	AutoCompleteTextView cityInput;

	@Bind(R.id.descrWeather)
	TextView textViewDescrWeather;
	@Bind(R.id.location)
	TextView textViewLocation;
	@Bind(R.id.temp)
	TextView textViewTemperature;
	@Bind(R.id.tempUnit)
	TextView textViewTempUnit;
	@Bind(R.id.tempMinMax)
	TextView textViewTempMinMax;
	@Bind(R.id.humidity)
	TextView textViewHum;
	@Bind(R.id.windSpeedAndDegree)
	TextView textViewWindSpeedAndDegree;
	@Bind(R.id.pressure)
	TextView textViewPress;
	@Bind(R.id.pressureStat)
	TextView textViewPressStatus;
	@Bind(R.id.visibility)
	TextView textViewVisibility;
	@Bind(R.id.imgWeather)
	ImageView weatherImage;
	@Bind(R.id.sunrise)
	TextView textViewSunrise;
	@Bind(R.id.sunset)
	TextView textViewSunset;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		requestQueue = Volley.newRequestQueue(getApplicationContext());

		ButterKnife.bind(this);

		CityAdapter cityAdapter = new CityAdapter(this, null);
		cityInput.setAdapter(cityAdapter);

		cityInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CityResult cityResult = (CityResult) parent.getItemAtPosition(position);
				woeid = cityResult.getWoeid();
				cityName = cityResult.getCityName();
				country = cityResult.getCountry();
			}
		});

	}

//	private void handleProgressBar(boolean visible) {
//		if (refreshItem == null)
//			return ;
//
//		if (visible)
//			refreshItem.setActionView(R.layout.progress_bar);
//		else
//			refreshItem.setActionView(null);
//	}


	@OnClick(R.id.button_get_weather_activtiy_main)
	protected void onClick(){
		//TODO:
		//handleProgressBar(true);

		//invoke Yahoo api here
		YahooAPIClient.getWeather(woeid, "c", requestQueue, new YahooAPIClient
			.WeatherClientListener() {
			@Override
			public void onWeatherResponse(Weather weather) {
				int code = weather.condition.code;

				textViewDescrWeather.setText(weather.condition.description);
				textViewLocation.setText(weather.location.name + "," + weather.location.region + "," + weather.location.country);
				// Temperature data
				textViewTemperature.setText("" + weather.condition.temp);

				int resId = getResource(weather.units.temperature, weather.condition.temp);
				((TextView) findViewById(R.id.lineTxt)).setBackgroundResource(resId);

				textViewTempUnit.setText(weather.units.temperature);
				textViewTempMinMax.setText("" + weather.forecast.tempMin + " " + weather.units.temperature +
					" - " + "" + weather.forecast.tempMax + " " + weather.units.temperature);

				// Humidity data
				textViewHum.setText("" + weather.atmosphere.humidity + " %");

				// Wind data
				textViewWindSpeedAndDegree.setText("" + weather.wind.speed + " " + weather.units.speed + " at " + "" + weather.wind.direction + "°");

				// Pressure data
				textViewPress.setText("" + weather.atmosphere.pressure + " " + weather.units.pressure);
				int status = weather.atmosphere.rising;
				String iconStat = "";
				switch (status) {
					case 0:
						iconStat = "-";
						break;
					case 1:
						iconStat = "+";
						break;
					case 2:
						iconStat = "-";
						break;
				}
				textViewPressStatus.setText(iconStat);

				// Visibility
				textViewVisibility.setText("" + weather.atmosphere.visibility + " " + weather.units.distance);

				// Astronomy
				textViewSunrise.setText(weather.astronomy.sunRise);
				textViewSunset.setText(weather.astronomy.sunSet);

				// Weather Image
				ScribdWeatherImageProvider provider = new WeatherImageProvider();
				provider.getImage(code, requestQueue, new ScribdWeatherImageProvider
					.WeatherImageListener() {
					@Override
					public void onImageReady(Bitmap image) {
						weatherImage.setImageBitmap(image);
					}
				});

			}
			//TODO:
//  handleProgressBar(false);
		});
	}

	private float convertToC(String unit, float val) {
		if (unit.equalsIgnoreCase("°C"))
			return val;

		return (float) ((val - 32) / 1.8);
	}

	private int getResource(String unit, float val) {
		float temp = convertToC(unit, val);
		Log.d("SwA", "Temp [" + temp + "]");
		int resId = 0;
		if (temp < 10)
			resId = R.drawable.line_shape_blue;
		else if (temp >= 10 && temp <= 24)
			resId = R.drawable.line_shape_green;
		else if (temp > 25)
			resId = R.drawable.line_shape_red;

		return resId;

	}


	private class CityAdapter extends ArrayAdapter<CityResult> implements Filterable {

		private Context ctx;
		private List<CityResult> cityList = new ArrayList<CityResult>();

		public CityAdapter(Context ctx, List<CityResult> cityList) {
			super(ctx, R.layout.cityresult_layout, cityList);
			this.cityList = cityList;
			this.ctx = ctx;
		}


		@Override
		public CityResult getItem(int position) {
			if (cityList != null)
				return cityList.get(position);

			return null;
		}

		@Override
		public int getCount() {
			if (cityList != null)
				return cityList.size();

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View result = convertView;

			if (result == null) {
				LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				result = inf.inflate(R.layout.cityresult_layout, parent, false);

			}

			TextView tv = (TextView) result.findViewById(R.id.textCityName);
			tv.setText(cityList.get(position).getCityName() + "," + cityList.get(position).getCountry());

			return result;
		}

		@Override
		public long getItemId(int position) {
			if (cityList != null)
				return cityList.get(position).hashCode();

			return 0;
		}

		@Override
		public Filter getFilter() {
			Filter cityFilter = new Filter() {


				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					if (constraint == null || constraint.length() < 2)
						return results;

					List<CityResult> cityResultList = YahooAPIClient.getCityList(constraint.toString());
					results.values = cityResultList;
					results.count = cityResultList.size();
					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					cityList = (List) results.values;
					notifyDataSetChanged();
				}
			};

			return cityFilter;
		}
	}
}
