package gsingh.busapp.userclient;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay {
	
	ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();

	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	public void addOverlay(OverlayItem overlay) {
		overlays.add(overlay);
		populate();
	}
	
	@Override
	public int size() {
		return overlays.size();
	}

}
