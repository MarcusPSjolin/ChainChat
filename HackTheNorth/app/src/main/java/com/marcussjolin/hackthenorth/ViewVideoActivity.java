package com.marcussjolin.hackthenorth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileInputStream;

public class ViewVideoActivity extends Activity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    public static VideoView mVideoView;
    Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        if (getIntent().hasExtra("video")) {
            Uri vidUri = Uri.parse(getIntent().getExtras().get("video").toString());
            mVideoView.setVideoURI(vidUri);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.setVisibility(View.VISIBLE);
            mVideoView.start();
        } else {
            dispatchTakeVideoIntent();
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            mVideoView.setVideoURI(videoUri);
            String path = getRealPathFromURI(this, videoUri);
            File file = new File(path);
            byte[] bytes = new byte[(int) file.length()];
            try {
                new FileInputStream(file).read(bytes);
            } catch (Exception e) {
                Log.d("Exception", e.getMessage());
            }
            ParseFile parseFile = new ParseFile("temp.mp4", bytes);
            try {
                parseFile.saveInBackground();
            } catch (Exception e) {
                Log.d("Parse Exception", e.getMessage());
            }
            ParseObject parseObject = new ParseObject("video");
            parseObject.put("video", parseFile);
            parseObject.put("recipient", "testtestU");
            parseObject.put("sender", ParseUser.getCurrentUser().getUsername());
            parseObject.saveInBackground();
            mVideoView.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
