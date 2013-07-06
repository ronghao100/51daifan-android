package com.daifan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daifan.R;


/**
 * Created by ronghao on 6/22/13.
 */
public class LoginActivity extends Activity {

    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Importing all assets like buttons, text fields
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);

        // Login button Click Event
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                String email = inputEmail.getText().toString();
//                String password = inputPassword.getText().toString();
//                UserService userService = new UserService();
//                Log.d("Button", "Login");
//                User user = userService.login(email, password);
//
//                // check for login response
//                if (user != null) {
//                    loginErrorMsg.setText("");
//                    // user successfully logged in
//                    // Store user details in SQLite Database
//                    UserDao userDao = new UserDao(getApplicationContext());
//                    userService.logoutUser(getApplicationContext());
//                    userDao.addUser(user);
//                    Intent main = new Intent(getApplicationContext(), RegisterActivity.class);
//
//                    // Close all views before launching Dashboard
//                    main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(main);
//
//                    // Close Login Screen
//                    finish();
//                } else {
//                    // Error in login
//                    loginErrorMsg.setText("Incorrect username/password");
//                }
//            }
//        });
//
//        // Link to Register Screen
//        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),
//                        RegisterActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        SubMenu sub = menu.addSubMenu("Theme");
//        sub.add(0, R.style.Theme_Sherlock, 0, "Default");
//        sub.add(0, R.style.Theme_Sherlock_Light, 0, "Light");
//        sub.add(0, R.style.Theme_Sherlock_Light_DarkActionBar, 0, "Light (Dark Action Bar)");
//        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        return true;
//    }
}
