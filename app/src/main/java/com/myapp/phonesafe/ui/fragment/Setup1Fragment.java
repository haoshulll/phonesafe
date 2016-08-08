package com.myapp.phonesafe.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myapp.phonesafe.R;

//import android.support.v4.app.Fragment;


public class Setup1Fragment extends Fragment {
	private View rootView;
	private Context context;
	
	
	@ViewInject(R.id.next)
	private Button nextBtn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=getActivity();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 rootView= inflater.inflate(R.layout.activity_defind_setup1, container, false);
		ViewUtils.inject(this, rootView);
		 return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//下一步
		nextBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Fragment fragment=new Setup2Fragment();
				FragmentTransaction bt = getActivity().getFragmentManager().beginTransaction();
				//替换
				bt.replace(R.id.content, fragment).commit();
			}
		});
	}


}
