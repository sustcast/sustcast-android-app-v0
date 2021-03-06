package com.chameleon.sustcast.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chameleon.sustcast.authentication.ApiLogin;
import com.chameleon.sustcast.chatHandler.ChatActvity;
import com.chameleon.sustcast.fontOverride.FontsOverride;
import com.chameleon.streammusic.R;
import com.chameleon.sustcast.utils.BottomNavigationViewHelper;
import com.chameleon.sustcast.credit.credit_page;
import com.chameleon.sustcast.data.model.logoutResponse;
import com.chameleon.sustcast.data.remote.ApiUtils;
import com.chameleon.sustcast.data.remote.UserClient;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;

    private AdView mAdView;
    static final String serverName = "103.84.159.230";
    static final int port = 50002;
    static OutputStream outToServer;
    public static DataOutputStream out;
    static InputStream inFromServer;
    public static DataInputStream in;
    static Socket socket;
    final String token = "siojdioajs21839712987391872ahsdhkjshkjdh21983912doiasoidoias";
    final String userName = "shuhan";
    private static final String TAG = "Home";
    private static final int ACTIVITY_NUM = 0;
    String token2;

    private Context mContext = Home.this;
    private Button mLogout;
    private Button mChat;
    private UserClient mAPIService;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    CoordinatorLayout coordinatorLayout;


    //new code added from radiomeowv2
    int buttonFlag;
    String url = "http://103.84.159.230:8000/sustcast";

    static int mediaGenjam = 50;

    public static String curSong;
    public static String curArtist;
    public static String curGenre;
    public static String curLyric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLogout = findViewById(R.id.buttonLogout);
        mChat = findViewById(R.id.buttonChat);
        mAPIService = ApiUtils.getAPIService();
        token2 = getIntent().getStringExtra("token");
        buttonFlag = 0;
        setupBottomNavigationView();
        setupViewPager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_close, R.string.navigation_drawer_open);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat();
            }
        });
        FontsOverride.setDefaultFont(this, "MONOSPACE", "doppio_one.ttf");
        setupBottomNavigationView();
        drawer = findViewById(R.id.drawer_layout);

    }

    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LiveFragment());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new TrendingFragment());

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Live");
        tabLayout.getTabAt(1).setText("Home");
        tabLayout.getTabAt(2).setText("Trending");
    }

    private void startChat() {
        Intent intent = new Intent(Home.this, ChatActvity.class);
        startActivity(intent);
    }

    private void logout() {
        mAPIService.signout("Bearer " + token2).enqueue(new Callback<logoutResponse>() {
            @Override
            public void onResponse(Call<logoutResponse> call, Response<logoutResponse> response) {
                System.out.println("Response code =>" + response.code());
                Log.i("MY: ", "LOGOUT CLICKED");
                if (response.isSuccessful()) {
                    Toast.makeText(Home.this, "Response Successful!! in general page", Toast.LENGTH_SHORT).show();
                    //spinner.setVisibility(View.GONE);
                    Intent intent = new Intent(Home.this, ApiLogin.class);
                    startActivity(intent);
                } else {
                    //spinner.setVisibility(View.GONE);
                    Toast.makeText(Home.this, "Response Unsuccessful", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<logoutResponse> call, Throwable t) {
                // spinner.setVisibility(View.GONE);
                Toast.makeText(Home.this, "No Internet.", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (item.isChecked()) {
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
        if (id == R.id.nav_credits) {
            startActivity(new Intent(getApplicationContext(), credit_page.class));
        } else if (id == R.id.nav_rateus) {
            startActivity(new Intent(getApplicationContext(), ApiLogin.class));

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
