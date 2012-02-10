package gsingh.busapp.userclient.overlay;

import gsingh.busapp.userclient.R;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BusOverlay extends MyItemizedOverlay {

	Activity activity = null;
	LinearLayout popup = null;
	TextView busName = null;
	boolean popupDisplayed = false;

	public BusOverlay(Drawable defaultMarker, Activity activity) {
		super(defaultMarker);
		this.activity = activity;
		this.popup = (LinearLayout) activity.findViewById(R.id.popupwindow);
		this.busName = (TextView) activity.findViewById(R.id.busname);

	}

	@Override
	protected boolean onTap(int index) {
		Log.d("tag", "Tapped");

		Animation animation;
		if (popupDisplayed == false) {
			animation = AnimationUtils.loadAnimation(activity, R.anim.slideup);

			busName.setText(overlays.get(index).getTitle());
			popup.setVisibility(View.VISIBLE);
			popup.startAnimation(animation);
			popupDisplayed = true;
		} else {
			animation = AnimationUtils.loadAnimation(activity, R.anim.slidedown);
			popup.setVisibility(View.INVISIBLE);
			popup.startAnimation(animation);
			popupDisplayed = false;
		}

		return true;
	}

}
