package gsingh.busapp.userclient;

import gsingh.busapp.userclient.components.Route;
import gsingh.busapp.userclient.components.Stop;

import java.util.LinkedList;
import java.util.List;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

public class RouteListActivity extends ExpandableListActivity {
	ExpandableListAdapter adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ExpandableListView view = this.getExpandableListView();
		view.setGroupIndicator(this.getResources().getDrawable(
				R.drawable.crosshair));

		// Set up the adapter
		adapter = new MyExpandableListAdapter(MyLocationListener.getRouteList());
		setListAdapter(adapter);
		registerForContextMenu(getExpandableListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Sample menu");
		menu.add(0, 0, 0, "List sample Action");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();

		String title = ((TextView) info.targetView).getText().toString();

		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			int childPos = ExpandableListView
					.getPackedPositionChild(info.packedPosition);
			Toast.makeText(
					this,
					title + ": Child " + childPos + " clicked in group "
							+ groupPos, Toast.LENGTH_SHORT).show();
			return true;
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			int groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			Toast.makeText(this, title + ": Group " + groupPos + " clicked",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		return false;
	}

	/**
	 * A simple adapter which maintains an ArrayList of photo resource Ids. Each
	 * photo is displayed as an image. This adapter supports clearing the list
	 * of photos and adding a new photo.
	 * 
	 */
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		List<Route> routeList = null;
		List<List<Stop>> routeStops = new LinkedList<List<Stop>>();

		public MyExpandableListAdapter(List<Route> routeList) {
			super();
			this.routeList = routeList;

			for (Route route : routeList) {
				routeStops.add(route.getStops());
			}
		 }

		// Sample data set. children[i] contains the children (String[]) for
		// groups[i].
		// private String[] groups = { "People Names", "Dog Names", "Cat Names",
		// "Fish Names" };
		// private String[][] children = {
		// { "Arnold", "Barry", "Chuck", "David" },
		// { "Ace", "Bandit", "Cha-Cha", "Deuce" },
		// { "Fluffy", "Snuggles" }, { "Goldy", "Bubbles" } };

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// return children[groupPosition][childPosition];
			return routeStops.get(groupPosition).get(childPosition).getName();
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// return children[groupPosition].length;
			return routeStops.get(groupPosition).size();
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, 30);

			TextView textView = new TextView(RouteListActivity.this);
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(20, 0, 0, 0);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setPadding(36, 0, 0, 0);
			textView.setText(getChild(groupPosition, childPosition).toString());
			return textView;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// return groups[groupPosition];
			return routeList.get(groupPosition).getName();
		}

		@Override
		public int getGroupCount() {
			// return groups.length;
			return routeList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			TextView textView = getGenericView();
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}
}
