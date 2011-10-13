package com.rteam.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;

import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.android.common.RTeamApplicationVersion;
import com.rteam.api.base.ResourceBase;
import com.rteam.api.base.ResourceResponse;
import com.rteam.api.base.APIResponse;
import com.rteam.api.business.Member;
import com.rteam.api.common.UriBuilder;

public class MembersResource extends ResourceBase {

	///////////////////////////////////////////////////////////////////////////
	/// .ctor
	
	public static MembersResource instance() {
		if (_instance == null) _instance = new MembersResource();
		return _instance;
	}
	
	private static MembersResource _instance;
	
	private MembersResource() {
		super(AndroidTokenStorage.get(), RTeamApplicationVersion.get());
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Helper Classes
	
	public static class MemberListResponse extends ResourceResponse {
		private ArrayList<Member> _members;
		public ArrayList<Member> members() { return _members; }
		

		public MemberListResponse(APIResponse response) {
			super(response);
			
			_members = new ArrayList<Member>();
			
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				try {
					JSONArray members = json().getJSONArray("members");
					for(int i=0; i<members.length(); i++) {
						_members.add(new Member(members.getJSONObject(i)));
					}
				} catch(JSONException e) {}
			}
		}
		
	}
	
	public interface MemberListResponseHandler {
		public void finish(MemberListResponse response);
	}
	
	public class CreateMemberResponse extends ResourceResponse {
		protected CreateMemberResponse(APIResponse response, Member member) {
			super(response);
			initialize(member);
		}
		
		private void initialize(Member member) {
			if (isResponseGood()) {
				member.memberId(json().optString("memberId"));
			}
		}
	}
	
	public interface CreateMemberResponseHandler {
		public void finish(CreateMemberResponse response);
	}
	
	public class GetMemberResponse extends ResourceResponse {
		private Member _member;
		public Member member() { return _member; }
		
		protected GetMemberResponse(Member member, APIResponse response) {
			super(response);
			_member = member;
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_member.mergeFullMemberData(json());
			}
		}
	}
	public interface GetMemberResponseHandler {
		public void finish(GetMemberResponse response);
	}
	
	public class UpdateMemberResponse extends ResourceResponse {
		
		protected UpdateMemberResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}
	
	public interface UpdateMemberResponseHandler {
		public void finish(UpdateMemberResponse response);
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Exposed Methods
	
	public MemberListResponse getMembers(String teamId) {
		return new MemberListResponse(get(createBuilder().addPath("team").addPath(teamId).addPath("members")));
	}
	
	public void getMembers(final String teamId, final MemberListResponseHandler handler) {
		(new AsyncTask<Void, Void, MemberListResponse>() {

			@Override
			protected MemberListResponse doInBackground(Void... params) {
				return getMembers(teamId);
			}
			
			@Override
			protected void onPostExecute(MemberListResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	public MemberListResponse getMembers(String teamId, boolean includeFans) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(teamId)
							.addPath("members")
							.addParam("includeFans", Boolean.toString(includeFans));
		return new MemberListResponse(get(uri));
	}
	
	public void getMembers(final String teamId, final boolean includeFans, final MemberListResponseHandler handler) {
		(new AsyncTask<Void, Void, MemberListResponse>() {

			@Override
			protected MemberListResponse doInBackground(Void... params) {
				return getMembers(teamId, includeFans);
			}
			
			@Override
			protected void onPostExecute(MemberListResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	
	public CreateMemberResponse create(Member member) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(member.teamId())
							.addPath("members");
		return new CreateMemberResponse(post(uri, member.toJSON()), member);
	}
	
	public void create(final Member member, final CreateMemberResponseHandler handler) {
		(new AsyncTask<Void, Void, CreateMemberResponse>() {

			@Override
			protected CreateMemberResponse doInBackground(Void... arg0) {
				return create(member);
			}
			
			@Override
			protected void onPostExecute(CreateMemberResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	public GetMemberResponse getFullMember(Member memberInfo, boolean includePhoto) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(memberInfo.teamId())
							.addPath("member").addPath(memberInfo.memberId())
							.addParam("includePhoto", Boolean.toString(includePhoto).toLowerCase());
		return new GetMemberResponse(memberInfo, get(uri));
	}
	
	public void getFullMember(final Member memberInfo, final boolean includePhoto, final GetMemberResponseHandler handler) {
		(new AsyncTask<Void, Void, GetMemberResponse>() {
			@Override
			protected GetMemberResponse doInBackground(Void... arg0) {
				return getFullMember(memberInfo, includePhoto);
			}
			
			@Override
			protected void onPostExecute(GetMemberResponse response) {
				handler.finish(response);
			}
		}).execute();
	}
	
	public UpdateMemberResponse updateMember(Member member) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(member.teamId())
							.addPath("member").addPath(member.memberId());
		return new UpdateMemberResponse(put(uri, member.toJSON()));
	}
	
	public void updateMember(final Member member, final UpdateMemberResponseHandler handler) {
		(new AsyncTask<Void, Void, UpdateMemberResponse>() {
			@Override
			protected UpdateMemberResponse doInBackground(Void... arg0) {
				return updateMember(member);
			}
			
			@Override
			protected void onPostExecute(UpdateMemberResponse response) {
				handler.finish(response);
			}
		}).execute();
	}
}
