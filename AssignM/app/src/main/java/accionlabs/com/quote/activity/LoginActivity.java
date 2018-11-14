package accionlabs.com.quote.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import accionlabs.com.quote.R;
//adada
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private SignInButton mSignInButton;
    private static GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        intialiseView();
       // LocalBroadcastManager.getInstance(this).registerReceiver(act2InitReceiver, new IntentFilter("logoutLocalBroadCast"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
    }

    private void intialiseView() {
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("ACCOUNT_NAME", account.getDisplayName());
            intent.putExtra("ACCOUNT_PIC", account.getPhotoUrl());
            intent.putExtra("ACCOUNT_EMAIL", account.getEmail());
            startActivity(intent);
            Log.v("LoginActivity", account.getDisplayName());
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Main", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    public void signOut(){
        if(mGoogleSignInClient != null)
            mGoogleSignInClient.signOut();
    }


    //Using Local Broadcast Logout
  /*  BroadcastReceiver act2InitReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            mGoogleSignInClient.signOut();


            // do your listener event stuff
        }
    };*/
//LocalBroadcastManager.getInstance(this).registerReceiver(act2InitReceiver, new IntentFilter("activity-2-initialized"));
}
