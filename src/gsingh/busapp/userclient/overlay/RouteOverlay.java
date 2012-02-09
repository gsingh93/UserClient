package gsingh.busapp.userclient.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * An {@link Overlay} that is a single line between two {@link GeoPoint}s
 * 
 * @author Gulshan
 * 
 */
public class RouteOverlay extends Overlay {
    private GeoPoint gp1;
    private GeoPoint gp2;
 
	public RouteOverlay(GeoPoint gp1, GeoPoint gp2) {
		this.gp1 = gp1;
		this.gp2 = gp2;
	}

	private Paint initPainter() {

		Paint painter = new Paint();

		painter.setDither(true);
		painter.setColor(Color.BLUE);
		painter.setAlpha(70);
		painter.setStyle(Paint.Style.FILL_AND_STROKE);
		painter.setStrokeJoin(Paint.Join.ROUND);
		painter.setStrokeCap(Paint.Cap.ROUND);
		painter.setStrokeWidth(3);
		
		return painter;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);

		Paint painter = initPainter();

		Point p1 = new Point();
		Point p2 = new Point();
		
		Path path = new Path();
		
		// Converts the geopoint coordinate to a pixel location on the map
		Projection projection = mapView.getProjection();
		projection.toPixels(gp1, p1);
		projection.toPixels(gp2, p2);

		// Create a line from p1 to p2
		path.moveTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);

		// Paint the line
		canvas.drawPath(path, painter);
	}
}