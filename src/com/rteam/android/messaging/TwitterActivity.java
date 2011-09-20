package com.rteam.android.messaging;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.common.RTeamLog;
import com.rteam.android.common.Simple3LineAdapater;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.teams.common.TeamCache;
import com.rteam.android.teams.common.TeamSelectDialog;
import com.rteam.api.ActivitiesResource;
import com.rteam.api.ActivitiesResource.CreateActivityResponse;
import com.rteam.api.ActivitiesResource.GetActivitiesResponse;
import com.rteam.api.ActivitiesResource.GetActivitiesResponseHandler;
import com.rteam.api.ActivitiesResource.GetActivityPhotoResponse;
import com.rteam.api.ActivitiesResource.GetActivityVideoResponse;
import com.rteam.api.base.ResponseStatus;
import com.rteam.api.business.Activity;
import com.rteam.api.business.Team;
import com.rteam.api.common.DateUtils;
import com.rteam.api.common.StringUtils;
import com.rteam.api.common.VideoUtils;

public class TwitterActivity extends RTeamActivity {
			
	////////////////////////////////////////////////////////////////
	///// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - activity"; }
	
	private static Team _team;
	private static boolean _teamOnly;
	private static void setTeam(Team team) { _team = team; }
	public static void setForTeamOnly(Team team) {
		_teamOnly = true;
		setTeam(team);
	}
	public static boolean hasTeam() { return _team != null; }
	public static void clear() { setTeam(null); _teamOnly = false; }
	
	
	private ListView _listActivity;
	private EditText _txtMessage;
	private Button _btnPost;
	private TextView _lblCharactersLeft;
	private EditText _txtPostTeam;
	private ImageButton _btnTakePicture;
	private ImageButton _btnTakeVideo;
	
	private LinearLayout _mainLayout;
	private LinearLayout _detailLayout;
	
	private VideoView _videoView;
	private ImageView _imageView;
	
	private TextView _txtDetailTitle;
	private Button _btnDetailDelete;
	private Button _btnDetailClose;
	
	private ArrayList<Activity> _activities = new ArrayList<Activity>();
	
	private Bitmap _postPhoto;
	private String _postVideoPath;
	private Bitmap _postVideoThumb;
	
	private final int MAX_LENGTH = 140;
	private final int ACTIVITY_RANGE = 30;
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows a current feed of activities.  This inclused posts to twitter if applicable."));
	}
	
	////////////////////////////////////////////////////////////////
	///// Initialization
	
	@Override
	protected void initialize() {
		initializeView();
		loadActivity();
	}
	
	private void initializeView() {
		setContentView(R.layout.messaging_activity);
		
		_listActivity = (ListView) findViewById(R.id.listActivity);
		_txtMessage = (EditText) findViewById(R.id.txtMessage);
		_btnPost = (Button) findViewById(R.id.btnPost);
		_lblCharactersLeft = (TextView) findViewById(R.id.lblCharactersLeft);
		_txtPostTeam = (EditText) findViewById(R.id.txtPostTeam);
		
		_btnTakePicture = (ImageButton) findViewById(R.id.btnTakePicture);
		_btnTakeVideo = (ImageButton) findViewById(R.id.btnTakeVideo);
		
		_mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		_detailLayout = (LinearLayout) findViewById(R.id.detailLayout);
		
		_txtDetailTitle = (TextView) findViewById(R.id.txtDetailTitle);
		_videoView = (VideoView) findViewById(R.id.videoView);
		_imageView = (ImageView) findViewById(R.id.imageView);
		_btnDetailDelete = (Button) findViewById(R.id.btnDetailDelete);
		_btnDetailClose = (Button) findViewById(R.id.btnDetailClose);
		
		_txtMessage.clearFocus();
		
		_txtMessage.addTextChangedListener(new TextWatcher() {
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) { bindPostLength(); }
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override public void afterTextChanged(Editable s) { }
		});
		
		_txtPostTeam.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setPostTeam(); }
		});
		
		_btnPost.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { postMessageClicked(); }
		});
		
		_listActivity.setOnItemClickListener(new OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> view, View convertView, int index, long id) { 
				activityClicked(index);
			}
		});
		
		_btnTakePicture.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { clickTakePicture(); }
		});
		
		_btnTakeVideo.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { clickTakeVideo(); }			
		});
		
		_btnDetailClose.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setDetailView(false); }});
		
		bindView();
	}

	private static final int TAKE_PICTURE = 1;
	private static final int TAKE_VIDEO = 2;
	
	private void clickTakePicture() {
		if (_postPhoto != null) {
			showPhoto(_postPhoto, _txtMessage.getText().toString(), _txtPostTeam.getText().toString(), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					_postPhoto = null;
					bindPhotos();
				}
			});
		}
		else {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			takePictureIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1000000);
			startActivityForResult(takePictureIntent, TAKE_PICTURE);
		}
	}
	
	private void clickTakeVideo() {
		if (_postVideoPath != null) {
			showVideo(_postVideoPath, _txtMessage.getText().toString(), _txtPostTeam.getText().toString(), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					_postVideoPath = null;
					_postVideoThumb = null;
					bindPhotos();
				}
			});
		}
		else {
			Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1000000);
			takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
			startActivityForResult(takeVideoIntent, TAKE_VIDEO);
		}
	}
	
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE) {			
			if (data != null && data.hasExtra("data")) {
				_postPhoto = data.getParcelableExtra("data");
				bindPhotos();
			}
		}
		else if(requestCode == TAKE_VIDEO) {
			RTeamLog.i("Data: %s", data != null ? data.toString() : "NULL DATA");
			if (data != null && data.getData() != null) {
				
				try {
					_postVideoPath = data.getData().toString();
					_postVideoThumb = createVideoThumbnail(data.getData());
				}
				catch(Exception ex) {}
				bindPhotos();
			}
		}
	}
	
	private Bitmap createVideoThumbnail(Uri origPath) {
		Cursor cursor = MediaStore.Video.query(getContentResolver(), origPath, new String[] { MediaStore.Video.Media._ID });
		cursor.moveToFirst();
		long fileid = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
		ContentResolver crThumb = getContentResolver();
		return MediaStore.Video.Thumbnails.getThumbnail(crThumb, fileid, MediaStore.Video.Thumbnails.MICRO_KIND, null);
	}
	
	
	
	
	
	private void bindView() {
		bindActivities();
		bindPostLength();
		bindPhotos();
		bindTeam();
	}
	
	private void bindActivities() {
		if (_activities != null) {
			ArrayList<Simple3LineAdapater.Data> data = new ArrayList<Simple3LineAdapater.Data>();
			for(Activity activity : _activities) {
				if (activity.teamId() != null) {
					activity.bindTeam(TeamCache.get(activity.teamId()));
				}
				if (activity.text().equalsIgnoreCase("Jen W post: a new friend for Bella!")) {
					RTeamLog.i("Has Thumbnail: " + (activity.thumbNail() != null));
					RTeamLog.i("Blah: " + activity.isVideo());
				}
				
				data.add(new Simple3LineAdapater.Data(activity.text(), 
													  DateUtils.toShortString(activity.createdDate()), 
													  _teamOnly ? "" : "Team: " + activity.teamName(),
													  activity.thumbNail()));
			}
			_listActivity.setAdapter(new Simple3LineAdapater(this, data));
		}
	}
	
	private void bindPhotos() {
		if (_postPhoto != null) {
			_btnTakePicture.setImageBitmap(_postPhoto);
			_btnTakeVideo.setEnabled(false);
		}
		else {
			_btnTakePicture.setImageResource(android.R.drawable.ic_menu_gallery);
			_btnTakeVideo.setEnabled(hasTeam());
		}
		
		
		if (_postVideoThumb != null) {
			_btnTakeVideo.setImageBitmap(_postVideoThumb);
			_btnTakePicture.setEnabled(false);
		}
		else {
			_btnTakeVideo.setImageResource(android.R.drawable.ic_menu_upload_you_tube);
			_btnTakePicture.setEnabled(hasTeam());
		}
	}
	
	private void bindPostLength() {
		_lblCharactersLeft.setText(Integer.toString(MAX_LENGTH - _txtMessage.getText().length()));
	}
	
	private void bindTeam() {
		_txtPostTeam.setText(hasTeam() ? _team.teamName() : "");
		_txtPostTeam.setEnabled(!hasTeam() || !_teamOnly);
		_btnPost.setEnabled(hasTeam());
		_txtMessage.setEnabled(hasTeam());
		_txtMessage.setSelected(hasTeam());
		_btnPost.setCompoundDrawablesWithIntrinsicBounds(hasTeam() && _team.usesTwitter() ? this.getResources().getDrawable(R.drawable.twitter_32_blue) : null, null, null, null);
		_btnTakePicture.setEnabled(hasTeam());
		_btnTakeVideo.setEnabled(hasTeam());
	}

	////////////////////////////////////////////////////////////////
	///// Loading activity
	
	private void loadActivity() {
		Calendar c = Calendar.getInstance();
		c.roll(Calendar.DATE, -ACTIVITY_RANGE);
		Activity.ActivityFilter filters = new Activity.RangedActivityFilter(c.getTime(), ACTIVITY_RANGE);
		//filters.refreshFirst(true);
		if (hasTeam() && _teamOnly) filters.teamId(_team.teamId()); 
		
		CustomTitle.setLoading(true, "Loading activity...");
		new ActivitiesResource().getActivities(filters, new GetActivitiesResponseHandler() {			
			@Override public void finish(GetActivitiesResponse response) { loadActivityFinished(response); }
		});
	}
	
	private void loadActivityFinished(GetActivitiesResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_activities = addAll(response.activities());
			//for (Activity activity : response.activities()) {
				  //_activities.add(activity);
				//};
			bindActivities();
		}
	}
	
	
	private ArrayList<Activity> addAll(ArrayList<Activity> activities) {
		// TODO Auto-generated method stub
		return null;
	}
	private void setPostTeam() {
		if (_teamOnly) return;
		new TeamSelectDialog(this, new TeamSelectDialog.TeamSelectHandler() {
			@Override
			public void teamSelected(Team team) {
				if (team != null) {
					setTeam(team);
					bindTeam();
				}
			}
		});
	}
	
	private void postMessageClicked() {
		CustomTitle.setLoading(true, "Posting message...");
		final Activity activity = getActivity();
		new ActivitiesResource().create(activity, new ActivitiesResource.CreateActivityResponseHandler() {
			@Override public void finish(CreateActivityResponse response) { tweetCreated(response, activity); }
		});
	}
	
	private Activity getActivity() {
		Activity activity = new Activity(_team, _txtMessage.getText().toString());
		if (_postPhoto != null) {
			activity.photo(_postPhoto);
			activity.thumbNail(_postPhoto);
		}
		if (_postVideoPath != null && _postVideoThumb != null) {
			activity.rawVideo(VideoUtils.getEncodedVideoFrom(getContentResolver(), _postVideoPath));
			activity.photo(_postVideoThumb);
			activity.thumbNail(_postVideoThumb);
		}
		return activity;
	}
	
	private void tweetCreated(CreateActivityResponse response, Activity activity) {
		CustomTitle.setLoading(false);
		if (response.getStatus() == ResponseStatus.Success) {
			Toast.makeText(this, "Successfully posted message", Toast.LENGTH_SHORT).show();
			_activities.add(0, activity);
			bindActivities();
			_txtMessage.setText("");
			_postPhoto = null;
			
			_postVideoPath = null;
			_postVideoThumb = null;

			bindPhotos();
		}
		else {
			Toast.makeText(this, String.format("Post failed : %s", response.getStatus().toString()), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void activityClicked(int index) {
		final Activity activity = _activities.get(index);
		if (activity.thumbNail() != null) {
			if (activity.isVideo()) {
				if (activity.rawVideo() == null) {
					CustomTitle.setLoading(true);
					new ActivitiesResource().getVideo(activity, new ActivitiesResource.GetActivityVideoResponseHandler() {
						@Override
						public void finish(GetActivityVideoResponse response) {
							CustomTitle.setLoading(false);
							showVideoFor(activity);
						}
					});
				}
				else {
					showVideoFor(activity);
				}
			}
			else {
				if (activity.photo() == null) {
					CustomTitle.setLoading(true);
					new ActivitiesResource().getPhoto(activity, new ActivitiesResource.GetActivityPhotoResponseHandler() {
						@Override public void finish(GetActivityPhotoResponse response) {
							CustomTitle.setLoading(false);
							showPhotoFor(activity);
						}
					});
				}
				else {
					showPhotoFor(activity);
				}
			}
		}
	}
	
	private void showPhotoFor(Activity activity) {
		showPhoto(activity.photo(), activity.text(), activity.teamName(), null);
	}
	
	private void showPhoto(Bitmap photo, String message, String team, View.OnClickListener neutralListener) {
		
		String title = 	StringUtils.valueOr(message, 
			  	 			String.format("Post Message%s%s", 
			  	 					StringUtils.isNullOrEmpty(team) ? "" : " for ",
			  	 					StringUtils.isNullOrEmpty(team) ? "" : team));
		
		_txtDetailTitle.setText(title);
		_imageView.setImageBitmap(photo);
		
		if (neutralListener != null) {
			_btnDetailDelete.setVisibility(View.VISIBLE);
			_btnDetailDelete.setOnClickListener(neutralListener);
		}
		else {
			_btnDetailDelete.setVisibility(View.INVISIBLE);
			_btnDetailDelete.setOnClickListener(null);
		}
		
		setVideoVisible(false);
		setDetailView(true);
	}
	
	private void showVideoFor(Activity activity) {
		if (activity.videoPath() == null) {
			activity.videoPath(VideoUtils.writeEncodedVideo(activity.rawVideo(), activity.activityId()));
		}
		showVideo(activity.videoPath(), activity.text(), activity.teamName(), null);
	}
	
	private void showVideo(String videoPath, String message, String team, View.OnClickListener neutralListener) {
	    
	    String title = 	StringUtils.valueOr(message, 
  	 			String.format("Post Message%s%s", 
  	 					StringUtils.isNullOrEmpty(team) ? "" : " for ",
  	 					StringUtils.isNullOrEmpty(team) ? "" : team));
	    
		_txtDetailTitle.setText(title);
		_videoView.setVideoPath(videoPath);
		_videoView.setMediaController(new MediaController(this));
		
		if (neutralListener != null) {
			_btnDetailDelete.setVisibility(View.VISIBLE);
			_btnDetailDelete.setOnClickListener(neutralListener);
		}
		else {
			_btnDetailDelete.setVisibility(View.INVISIBLE);
			_btnDetailDelete.setOnClickListener(null);
		}
		
		setVideoVisible(true);
		setDetailView(true);
		
		_videoView.start();
	}
	
	private void setVideoVisible(boolean visible) {
		if (visible) {
			_imageView.setVisibility(View.GONE);
			_videoView.setVisibility(View.VISIBLE);
		}
		else {
			_videoView.setVisibility(View.GONE);
			_imageView.setVisibility(View.VISIBLE);
		}
	}
	
	private void setDetailView(boolean visible) {
		_mainLayout.setVisibility(visible ? View.GONE : View.VISIBLE);
		_detailLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
}
