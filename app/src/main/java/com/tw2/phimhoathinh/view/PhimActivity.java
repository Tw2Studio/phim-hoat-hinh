package com.tw2.phimhoathinh.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.glomadrian.loadingballs.BallView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.tw2.phimhoathinh.R;
import com.tw2.phimhoathinh.adapter.VideoAdapter;
import com.tw2.phimhoathinh.model.MyVideo;
import com.tw2.phimhoathinh.model.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhimActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<MyVideo> list;
    private VideoAdapter adapter;
    private String ID_PLAYLIST = "PLYLXC-cAzgsnKy105XaSlnS_C7xPYFHKJ";
    private String URL_PLAYLIST = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="
            +ID_PLAYLIST+"&key="+ Utils.KEY_YOUTUBE;
    private Intent intent;
    private String namePlaylist;
    private BallView ballView;
    private AdView banner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phim);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStatusBar));
        }
        intent = getIntent();
        namePlaylist = intent.getStringExtra("NAME_PLAYLIST");
        initToolbar();
        requestAds();
        initView();
        initData();
    }

    private void requestAds() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2328589623882503~5777206290");
        banner = (AdView) findViewById(R.id.banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void initToolbar() {
        ((TextView) findViewById(R.id.tv_toolbar)).setText(namePlaylist);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initData() {
        ID_PLAYLIST = intent.getStringExtra("ID_PLAYLIST");
        URL_PLAYLIST = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="
                +ID_PLAYLIST+"&key="+ Utils.KEY_YOUTUBE;
        getJsonYouTube(URL_PLAYLIST);
    }

    private void getJsonYouTube(final String url){
        RequestQueue requestQueue = Volley.newRequestQueue(PhimActivity.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(PhimActivity.this);
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

                                MyVideo video = new MyVideo(name, image, idVideo, ID_PLAYLIST);
                                list.add(video);
                            }

                            adapter = new VideoAdapter(list, PhimActivity.this);
                            recyclerView.setAdapter(adapter);
                            ballView.setVisibility(View.GONE);

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
        recyclerView = (RecyclerView) findViewById(R.id.recycle_video);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PhimActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        ballView = (BallView) findViewById(R.id.loading);
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
