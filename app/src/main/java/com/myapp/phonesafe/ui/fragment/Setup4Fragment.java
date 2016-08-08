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

public class Setup4Fragment extends Fragment {
	private View rootView;
	private Context context;
	
	@ViewInject(R.id.next)
	private Button nextBtn;
	
	@ViewInject(R.id.pre)
	private Button preBtn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=getActivity();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 rootView= inflater.inflate(R.layout.activity_defind_setup4, container, false);
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
				Fragment fragment=new Setup5Fragment();
				FragmentTransaction bt = getActivity().getFragmentManager().beginTransaction();
				bt.replace(R.id.content, fragment).commit();
			}
		});
		//上一步
		preBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment=new Setup3Fragment();
				FragmentTransaction bt = getActivity().getFragmentManager().beginTransaction();
				bt.replace(R.id.content, fragment).commit();
			}
		});
	}

}
