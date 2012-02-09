package gsingh.busapp.userclient.overlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;

public class StopsOverlay extends MyItemizedOverlay {

	Activity activity = null;

	public StopsOverlay(Drawable defaultMarker, Activity activity) {
		super(defaultMarker);

		this.activity = activity;
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = overlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

}
