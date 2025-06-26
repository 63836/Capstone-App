package ClientSide.LoginAndSignup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.UserDatabaseHelper;

import ClientSide.PointsSEction.Pointsection;

public class SignInActivity extends AppCompatActivity {

    private UserDatabaseHelper db;

    // UI elements
    private EditText nameEt, emailEt, passEt, phoneEt, genderEt;
    private Button registerBtn;
    private TextView signInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        signInTextView = findViewById(R.id.signInTextView);

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
}