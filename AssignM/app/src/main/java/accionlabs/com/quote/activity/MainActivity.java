package accionlabs.com.quote.activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import accionlabs.com.quote.R;
import accionlabs.com.quote.model.QuotesResponse;
import accionlabs.com.quote.network.ApiInterface;
import accionlabs.com.quote.network.ApiClient;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    TextView mProfileNameTextView, mProfileEmail;
    TextView mQuoteTextView;
   CircleImageView mPicProfile;
    ProgressDialog progressDoalog;
    Button mRefreshButton;
    String profileName, profilePic,profileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intialiseView();
        callApi();
    }

    private void intialiseView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        mProfileEmail = (TextView) header.findViewById(R.id.email_textview);
        mProfileNameTextView = (TextView) header.findViewById(R.id.profile_name_textview);
        mQuoteTextView = (TextView) findViewById(R.id.quote_textview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRefreshButton = (Button) findViewById(R.id.refresh_button);
        mPicProfile = (CircleImageView) header.findViewById(R.id.profile_imageview);
        mRefreshButton.setOnClickListener(this);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        callProgressBar();
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            profileName = bundle.getString("ACCOUNT_NAME");
            profilePic = bundle.getString("ACCOUNT_PIC");
            profileEmail = bundle.getString("ACCOUNT_EMAIL");
            Log.v(":MainActivity", profileName + profilePic);
            mProfileNameTextView.setText(profileName);
            mProfileEmail.setText(profileEmail);
            if (profilePic!=null){
                Picasso.get().load(profilePic).into(mPicProfile);
            }
            else {

                    mPicProfile.setImageResource(R.mipmap.profiledefault);
            }


        }
    }

    private void callProgressBar() {
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Its loading....");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDoalog.show();
    }

    private void callApi() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<QuotesResponse> call = apiService.getTopRatedMovies();
        call.enqueue(new Callback<QuotesResponse>() {
            @Override
            public void onResponse(Call<QuotesResponse> call, Response<QuotesResponse> response) {

                Log.d("MAinAct", "Number of Quote received: " + response.toString());
                if (response.code() != 429) {
                    for (int i = 0; i < response.body().getContents().getQuotes().size(); i++) {
                        mQuoteTextView.setText(response.body().getContents().getQuotes().get(i).getQuote());
                    }
                    progressDoalog.dismiss();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Too Many Requests! Try some other time ");
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDoalog.dismiss();
                            //finish();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<QuotesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("MainAct Throw", t.toString());
                progressDoalog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_send) {
            LoginActivity mainActivity=new LoginActivity();
            mainActivity.signOut();
         //   LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("logoutLocalBroadCast"));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refresh_button:
                callProgressBar();
                callApi();
                break;
        }
    }
}
