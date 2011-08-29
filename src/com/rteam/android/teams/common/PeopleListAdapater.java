package com.rteam.android.teams.common;

import java.util.ArrayList;

import com.rteam.android.R;
import com.rteam.api.MembersResource;
import com.rteam.api.MembersResource.GetMemberResponse;
import com.rteam.api.business.Member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PeopleListAdapater extends BaseExpandableListAdapter {
	
	//////////////////////////////////////////////////////////////////
	//// Members
	
	private LayoutInflater _inflater;
	private ArrayList<ArrayList<Member>> _allMembers;
		
	//////////////////////////////////////////////////////////////////
	//// .ctor
	
	public PeopleListAdapater(Context context, ArrayList<Member> members, ArrayList<Member> fans) {
		_inflater = LayoutInflater.from(context);
		_allMembers = new ArrayList<ArrayList<Member>>();
		_allMembers.add(members);
		_allMembers.add(fans);
	}
	
	//////////////////////////////////////////////////////////////////
	//// Overrides

	@Override
	public Object getChild(int groupPos, int itemPos) { return _allMembers.get(groupPos).get(itemPos); }
	@Override
	public Object getGroup(int groupPosition) { return _allMembers.get(groupPosition); }
	@Override
	public long getChildId(int groupPosition, int childPosition) { return groupPosition * 100 + childPosition; }
	@Override
	public long getGroupId(int groupPosition) { return groupPosition; }
	@Override
	public int getChildrenCount(int groupPosition) { return _allMembers.get(groupPosition).size(); }
	@Override	
	public int getGroupCount() { return _allMembers.size(); }
	@Override
	public boolean hasStableIds() { return false; }
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Views
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String title = groupPosition == 0 ? "Members" : "Fans";
		convertView = _inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
		((TextView) convertView.findViewById(android.R.id.text1)).setText(title);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		Member member = (Member) getChild(groupPosition, childPosition);
		final ViewHolder holder = new ViewHolder();
		convertView = _inflater.inflate(R.layout.list_item_member, null);
		holder.imageMain = (ImageView) convertView.findViewById(R.id.imageMain);
		holder.txtMain = (TextView) convertView.findViewById(R.id.txtMain);
		convertView.setTag(holder);
			
		if (member.memberImageThumb() != null) {
			holder.imageMain.setImageBitmap(member.memberImageThumb());
		}
		else {
			new MembersResource().getFullMember(member, false, new MembersResource.GetMemberResponseHandler() {
				@Override public void finish(GetMemberResponse response) {
					if (response.member().memberImageThumb() != null) {
						holder.imageMain.setImageBitmap(response.member().memberImageThumb());
					}
				}
			});

		}
		holder.txtMain.setText(member.memberName());
		return convertView;
	}
	
	static class ViewHolder
	{
		ImageView imageMain;
		TextView txtMain;
	}
}
