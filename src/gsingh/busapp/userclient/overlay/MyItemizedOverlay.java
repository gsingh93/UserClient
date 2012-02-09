package gsingh.busapp.userclient.overlay;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Wrapper for list of {@link OverlayItem}s
 * 
 * @author Gulshan
 * 
 */
public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	protected ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();


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

	public void clear() {
		overlays.clear();
	}

}
