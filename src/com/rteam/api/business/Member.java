package com.rteam.api.business;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rteam.api.common.BitmapUtils;
import com.rteam.api.common.EnumUtils;

import android.graphics.Bitmap;

public class Member {
	
	//////////////////////////////////////////////////////////////
	/// Enums
	
	public enum Role {
		Coordinator, Member, Fan, Creator, Unknown;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
		
		public boolean atLeast(Member.Role other) {
			if (other == Role.Fan) return this == Role.Fan || this.atLeast(Role.Member);
			else if (other == Role.Member) return this == Role.Member || this.atLeast(Role.Coordinator);
			else if (other == Role.Coordinator) return this == Role.Coordinator || this.atLeast(Role.Creator);
			else if (other == Role.Creator) return this == Role.Creator;
			return false;
		}
	}
	
	public enum RoleTag {
		Friend, Coach, Manager, Player, Parent, Sponsor, Family, Trainer, Organizer, Fan;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
	}
	
	public enum Gender {
		Male, Female;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
	}
		
	
	public static class Guardian {
		private String _key;
		public String key() { return this._key; }
		
		private String _firstName;
		public void firstName(String _firstName) {this._firstName = _firstName;}
		public String firstName() {return _firstName;}
		
		private String _lastName;
		public void lastName(String _lastName) {this._lastName = _lastName;}
		public String lastName() {return _lastName;}
		
		private String _emailAddress;	
		public void emailAddress(String _emailAddress) {this._emailAddress = _emailAddress;}
		public String emailAddress() {return _emailAddress;}
		
		public Guardian() {}
		public Guardian(JSONObject json) {
			firstName(json.optString("firstName"));
			lastName(json.optString("lastName"));
			emailAddress(json.optString("emailAddress"));
			_key = json.optString("key");
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			
			json.putOpt("key", key());
			json.putOpt("firstName", firstName());
			json.putOpt("lastName", lastName());
			json.putOpt("emailAddress", emailAddress());
			
			return json;
		}
	}

	//////////////////////////////////////////////////////////////
	/// Members
	
	private String _memberId;
	public String memberId() { return _memberId; }
	public void memberId(String value) { _memberId = value; }
	
	private String _memberName;
	public String memberName() { return _memberName; }
	public void memberName(String value) { _memberName = value; }

			
	private String _emailAddress;
	public String emailAddress() { return _emailAddress; }
	public void emailAddress(String value) { _emailAddress = value; }
	
	private Role _participantRole;
	public Role participantRole() { return _participantRole; }
	public void participantRole(Role value) { _participantRole = value; }
	
	private boolean _isNetworkAuthenticated;
	public boolean isNetworkAuthenticated() { return _isNetworkAuthenticated; }
	public void isNetworkAuthenticated(boolean value) { _isNetworkAuthenticated = value; }

	private boolean _isUser;
	public boolean isUser() { return _isUser; }
	public void isUser(boolean value) { _isUser = value; }
	
	///// Non Writable -- Optional Members
	private String _teamId;
	public String teamId() { return _teamId; }
	public void teamId(String value) { _teamId = value; }
	
	
	public void bindTeam(Team team) {
		teamId(team.teamId());
	}
	
	//// Writable Members -- Optional
	
	private String _firstName;
	public void firstName(String _firstName) {this._firstName = _firstName;}
	public String firstName() {return _firstName;}
	
	private String _lastName;
	public void lastName(String _lastName) {this._lastName = _lastName;}
	public String lastName() {return _lastName;}
	
	
	private String _jerseyNumber;
	public void jerseyNumber(String _jerseyNumber) {this._jerseyNumber = _jerseyNumber;	}
	public String jerseyNumber() {return _jerseyNumber;	}
	
	private String _phoneNumber;
	public void phoneNumber(String phoneNumber) {this._phoneNumber = phoneNumber; }
	public String phoneNumber() { return _phoneNumber; }
	
	private ArrayList<Guardian> _guardians;
	public void guardians(ArrayList<Guardian> _guardians) {this._guardians = _guardians;}
	public ArrayList<Guardian> guardians() {return _guardians;}
		
	private ArrayList<RoleTag> _roles;
	public void roles(ArrayList<RoleTag> _roles) {this._roles = _roles;}
	public ArrayList<RoleTag> roles() {return _roles;}
	
	private Gender _gender;
	public void gender(Gender _gender) {this._gender = _gender;}
	public Gender gender() {return _gender;}
	
	private String _age;
	public void age(String _age) {this._age = _age;}
	public String age() {return _age;}

	private String _streetAddress;
	public void streetAddress(String _streetAddress) {this._streetAddress = _streetAddress;}
	public String streetAddress() {return _streetAddress;}
	
	private String _city;
	public void city(String _city) {this._city = _city;	}
	public String city() {return _city;}
	
	private String _state;
	public void state(String _state) {this._state = _state;}
	public String state() {return _state;}
	
	private String _zipcode;
	public void zipcode(String _zipcode) {this._zipcode = _zipcode;}
	public String zipcode() {return _zipcode;}
	
	private Bitmap _memberImage;
	public void memberImage(Bitmap memberImage) { _memberImage = memberImage; }
	public Bitmap memberImage() { return _memberImage; }
	
	private Bitmap _memberImageThumb;
	public void memberImageThumb(Bitmap memberImageThumb) { _memberImageThumb = memberImageThumb; }
	public Bitmap memberImageThumb() { return _memberImageThumb; }	
	
	//////////////////////////////////////////////////////////////////
	/// .ctor
	
	public Member(String teamId, String firstName, String lastName, String emailAddress) {
		_teamId = teamId;
		_firstName = firstName;
		_lastName = lastName;
		_emailAddress = emailAddress;
		_memberName = firstName + " " + lastName;
	}
	
	public Member(String memberId, String memberName) {
		memberId(memberId);
		memberName(memberName);
	}
	
	public Member(JSONObject json) {
		memberId(json.optString("memberId"));
		memberName(json.optString("memberName"));
		emailAddress(json.optString("emailAddress"));
		participantRole(EnumUtils.fromString(Role.class, json.optString("participantRole")));
		isNetworkAuthenticated(json.optBoolean("isNetworkAuthenticated", false));
		isUser(json.optBoolean("isUser", false));
	}
	
	public Member(Member other) {
		memberId(other.memberId());
		memberName(other.memberName());
		emailAddress(other.emailAddress());
		participantRole(other.participantRole());
		isNetworkAuthenticated(other.isNetworkAuthenticated());
		isUser(other.isUser());

		teamId(other.teamId());

		firstName(other.firstName());
		lastName(other.lastName());
		jerseyNumber(other.jerseyNumber());
		phoneNumber(other.phoneNumber());
		guardians(other.guardians());
		roles(other.roles());
		gender(other.gender());
		age(other.age());
		streetAddress(other.streetAddress());
		city(other.city());
		state(other.state());
		zipcode(other.zipcode());
		memberImage(other.memberImage());
		memberImageThumb(other.memberImageThumb());
	}
	
	public void mergeFullMemberData(JSONObject json) {
		firstName(json.optString("firstName"));
		lastName(json.optString("lastName"));
		emailAddress(json.optString("emailAddress"));
		jerseyNumber(json.optString("jerseyNumber"));
		isNetworkAuthenticated(json.optBoolean("isNetworkAuthenticated", false));
		participantRole(EnumUtils.fromString(Role.class, json.optString("participantRole")));
		gender(EnumUtils.fromString(Gender.class, json.optString("gender")));
		age(json.optString("age"));
		streetAddress(json.optString("streetAddress"));
		city(json.optString("city"));
		state(json.optString("state"));
		zipcode(json.optString("zipcode"));
		
		JSONArray rolesArray = json.optJSONArray("roles");
		if (rolesArray != null) {
			ArrayList<RoleTag> roles = new ArrayList<RoleTag>();
			for(int i=0; i<rolesArray.length(); i++)
				roles.add(EnumUtils.fromString(RoleTag.class, rolesArray.optString(i)));
			this.roles(roles);
		}
		
		JSONArray guardiansArray = json.optJSONArray("guardians");
		if (guardiansArray != null) {
			ArrayList<Guardian> guardians = new ArrayList<Guardian>();
			for(int i=0; i<guardiansArray.length(); i++)
				guardians.add(new Guardian(guardiansArray.optJSONObject(i)));
			this.guardians(guardians);
		}
		
		memberImage(BitmapUtils.getBitmapFrom(json.optString("photo")));
		memberImageThumb(BitmapUtils.getBitmapFrom(json.optString("thumbNail")));
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.putOpt("firstName", firstName());
			json.putOpt("lastName", lastName());
			json.putOpt("emailAddress", emailAddress());
			json.putOpt("jerseyNumber", jerseyNumber());
			json.putOpt("phoneNumber", phoneNumber());
			
			JSONArray guardians = new JSONArray();
			if (guardians() != null) {
				for(Guardian g : guardians()) {
					guardians.put(g.toJSON());
				}
			}
			json.putOpt("guardians", guardians);
			json.putOpt("participantRole", participantRole());
			
			JSONArray roles = new JSONArray();
			if (roles() != null) {
				for(RoleTag role : roles()) {
					roles.put(role.toString());
				}
			}
			json.putOpt("roles", roles);
			
			json.putOpt("gender", gender());
			json.putOpt("age", age());
			json.putOpt("streetAddress", streetAddress());
			json.putOpt("city", city());
			json.putOpt("state", state());
			json.putOpt("zipcode", zipcode());
			
			if (memberImage() != null){
				String encodedImage = BitmapUtils.getEncodedStringFrom(memberImage());
				json.putOpt("photo", encodedImage);
				json.putOpt("isPortrait", memberImage().getWidth() > memberImage().getHeight());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;	
	}
}
