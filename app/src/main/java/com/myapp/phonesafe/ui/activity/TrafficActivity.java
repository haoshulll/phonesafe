package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.business.TrafficProvider;
import com.myapp.phonesafe.entity.TrafficInfo;

import java.util.List;

public class TrafficActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		List<TrafficInfo> traffics = TrafficProvider.getTraffics(this);
		for (TrafficInfo trafficInfo : traffics) {
			System.out.println(trafficInfo.toString());
		}
		
	}

}
