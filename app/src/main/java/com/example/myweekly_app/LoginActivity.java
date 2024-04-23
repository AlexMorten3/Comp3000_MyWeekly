package com.example.myweekly_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.functions.Functions;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonSignUp;

    private String appID = "application-0-csvmn";
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Realm.init(this);
        app = new App(new AppConfiguration.Builder(appID).build());

        editTextEmail = findViewById(R.id.email_entry);
        editTextPassword = findViewById(R.id.password_entry);
        buttonLogin = findViewById(R.id.login_button);
        buttonSignUp = findViewById(R.id.create_account_button);

        buttonLogin.setOnClickListener(v -> {
            String username = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            loginUser(username, password);
        });

        buttonSignUp.setOnClickListener(v -> {
            String username = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            signupUser(username, password);
        });
    }

    private void loginUser(String email, String password) {
        Credentials emailPasswordCredentials = Credentials.emailPassword(email, password);
        AtomicReference<User> user = new AtomicReference<>();
        app.loginAsync(emailPasswordCredentials, it -> {
            if (it.isSuccess()) {
                Log.v("AUTH", "Successfully authenticated using an email and password.");
                user.set(app.currentUser());
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            } else {
                Log.e("AUTH", it.getError().toString());
                Toast.makeText(LoginActivity.this, "Login failed: " + it.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signupUser(String username, String password) {
        AtomicReference<User> user = new AtomicReference<>();
        app.getEmailPassword().registerUserAsync(username, password, it -> {
            if (it.isSuccess()) {
                Log.v("AUTH", "Successfully registered a new user.");
                Toast.makeText(LoginActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                user.set(app.currentUser());
                callRegisterUserFunction(username,user);
            } else {
                Log.e("AUTH", it.getError().toString());
                Toast.makeText(LoginActivity.this, "Sign-up failed: " + it.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callRegisterUserFunction(String username, AtomicReference<User> userRef) {
        User currentUser = userRef.get();
        if (currentUser != null) {
            Functions functionsClient = app.getFunctions(currentUser);

            functionsClient.callFunctionAsync("saveuser", Arrays.asList(username),String.class, result ->{
                if(result.isSuccess()){
                    Log.d("FUNCTION_CALL", "Function executed successfully: ");
                }
                else{
                    Log.e("FUNCTION_CALL", "Function execution failed: ");
                }
            });
        } else {
            Log.e("CALL_FUNCTION", "User is null");
        }
    }
}