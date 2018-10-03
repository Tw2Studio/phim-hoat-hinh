package com.tw2.phimhoathinh.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tw2.phimhoathinh.R;
import com.tw2.phimhoathinh.adapter.PlayVideoAdapter;
import com.tw2.phimhoathinh.adapter.VideoAdapter;
import com.tw2.phimhoathinh.model.MyVideo;
import com.tw2.phimhoathinh.model.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private YouTubePlayerView youTubePlayerView;
    private int REQUEST_CODE = 123;
    private String idVideo;
    private String nameVideo;
    private TextView textView;
    private String idPlaylist;
    private String URL_PLAYLIST;
    private List<MyVideo> list;
    private RecyclerView recyclerView;
    private PlayVideoAdapter adapter;
    private AdView banner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStatusBar));
        }
        Intent intent = getIntent();
        idVideo = intent.getStringExtra("ID_VIDEO");
        nameVideo = intent.getStringExtra("NAME_VIDEO");
        idPlaylist = intent.getStringExtra("ID_PLAY_LIST");
        URL_PLAYLIST = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="
                + idPlaylist + "&key=" + Utils.KEY_YOUTUBE;
        requestAds();
        initView();
        getJsonYouTube(URL_PLAYLIST);
    }

    private void requestAds() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2328589623882503~5777206290");
        banner = (AdView) findViewById(R.id.banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void getJsonYouTube(final String url){
        RequestQueue requestQueue = Volley.newRequestQueue(PlayVideoActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject jsonMax = response.getJSONObject("pageInfo");
                            int maxItem = jsonMax.getInt("totalResults");
                            getDataYouTube(url+"&maxResults="+50);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void getDataYouTube(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(PlayVideoActivity.this);
        list = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonItems = response.getJSONArray("items");
                            String name = "";
                            String image = "";
                            String idVideo = "";

                            for (int i=0; i<jsonItems.length();i++){
                                JSONObject jsonItem = jsonItems.getJSONObject(i);
                                JSONObject jsonSnippet  = jsonItem.getJSONObject("snippet");
                                JSONObject jsonThumbnail = jsonSnippet.getJSONObject("thumbnails");
                                JSONObject jsonMedium = jsonThumbnail.getJSONObject("medium");
                                name = jsonSnippet.getString("title");
                                image = jsonMedium.getString("url");

                                JSONObject jsonIdVideo = jsonSnippet.getJSONObject("resourceId");
                                idVideo = jsonIdVideo.getString("videoId");

                                MyVideo video = new MyVideo(name, image, idVideo, idPlaylist);
                                list.add(video);
                            }

                            List<MyVideo> myVideoList = new ArrayList<>();
                            int size = list.size();
                            for (int i = 0; i < size; i++) {
                                Random rd = new Random();
                                int index = rd.nextInt(size);
                                if (!myVideoList.contains(list.get(index))) {
                                    if (!list.get(index).getIdVideo().equals(idVideo)) {
                                        myVideoList.add(list.get(index));
                                    }
                                }

                            }

                            adapter = new PlayVideoAdapter(myVideoList, PlayVideoActivity.this);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String name = "";
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void initView() {
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);

        youTubePlayerView.initialize(Utils.KEY_YOUTUBE, this);

        textView = (TextView) findViewById(R.id.tv_name_playvideo);
        textView.setText(nameVideo);

        recyclerView = (RecyclerView) findViewById(R.id.recycle_play_video);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PlayVideoActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo(idVideo);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(PlayVideoActivity.this, REQUEST_CODE);
        } else {
            Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            youTubePlayerView.initialize(Utils.KEY_YOUTUBE, PlayVideoActivity.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        if (banner != null) {
            banner.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (banner != null) {
            banner.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (banner != null) {
            banner.destroy();
        }
        super.onDestroy();
    }
}
