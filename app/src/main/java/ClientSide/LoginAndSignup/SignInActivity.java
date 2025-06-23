package ClientSide.LoginAndSignup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.UserDatabaseHelper;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.Arrays;

import com.facebook.GraphRequest;

import ClientSide.PointsSEction.Pointsection;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In
    private UserDatabaseHelper db;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    // UI elements
    private EditText nameEt, emailEt, passEt, phoneEt, genderEt;
    private Button registerBtn;
    private TextView signInTextView;
    private LoginButton facebookLoginBtn;
    private SignInButton googleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Facebook SDK before anything else
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        db = new UserDatabaseHelper(this);

        // Initialize UI elements
        nameEt     = findViewById(R.id.nameEditText);
        emailEt    = findViewById(R.id.emailSignUpEditText);
        passEt     = findViewById(R.id.passwordSignUpEditText);
        phoneEt    = findViewById(R.id.phoneEditText);
        genderEt   = findViewById(R.id.genderEditText);
        registerBtn  = findViewById(R.id.signUpButton);
        facebookLoginBtn = findViewById(R.id.facebookLoginButton);
        signInTextView = findViewById(R.id.signInTextView);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // === Manual Sign-Up ===
        registerBtn.setOnClickListener(v -> {
            String name   = nameEt.getText().toString().trim();
            String email  = emailEt.getText().toString().trim();
            String pass   = passEt.getText().toString().trim();
            String phone  = phoneEt.getText().toString().trim();
            String gender = genderEt.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()
                    || phone.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = db.insertUser(name, email, pass, phone, gender);
            if (id > 0) {
                Toast.makeText(this, "Registered (ID=" + id + ")", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Email already used or error", Toast.LENGTH_SHORT).show();
            }
        });

        // === Facebook Sign-Up ===
        callbackManager = CallbackManager.Factory.create();
        facebookLoginBtn.setPermissions(Arrays.asList("email", "public_profile"));

        facebookLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Get user info from Facebook
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        (object, response) -> handleFacebookUser(object)
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, "Facebook sign-in canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this, "Facebook error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // === Google Sign-In ===
        googleSignInButton.setOnClickListener(v -> {
            signInWithGoogle();
        });

        // === Sign In Text Click ===
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to MainActivity (login screen)
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: Close the SignInActivity
            }
        });
    }

    private void handleFacebookUser(JSONObject object) {
        try {
            String name = object.optString("name");
            String email = object.optString("email");
            String gender = object.optString("gender", "Not specified");
            String phone = "FacebookUser"; // You can let the user edit this later

            long id = db.insertUser(name, email, "facebook_auth", phone, gender);
            if (id > 0) {
                Toast.makeText(this, "Signed in with Facebook (ID=" + id + ")", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Pointsection.class));
                finish();
            } else {
                Toast.makeText(this, "This Facebook account is already registered", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("FBProfile", "Failed to parse profile", e);
            Toast.makeText(this, "Failed to retrieve Facebook user", Toast.LENGTH_SHORT).show();
        }
    }

    // Google Sign-In methods
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from Google Sign-In.
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from GoogleApiClient.getSignInIntent() is always completed, even if the API was not available.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            // Required for Facebook login to work
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
            Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            String name = account.getDisplayName();
            String email = account.getEmail();
            String phone = "GoogleUser"; // You can let the user edit this later
            String gender = "Not specified";

            long id = db.insertUser(name, email, "google_auth", phone, gender);
            if (id > 0) {
                Toast.makeText(this, "Signed in with Google (ID=" + id + ")", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Pointsection.class));
                finish();
            } else {
                Toast.makeText(this, "This Google account is already registered", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("GoogleSignIn", "Sign-in failed");
        }
    }
}