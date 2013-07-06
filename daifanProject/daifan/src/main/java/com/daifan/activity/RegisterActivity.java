package com.daifan.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daifan.R;

/**
 * Created by ronghao on 6/22/13.
 */
public class RegisterActivity extends Activity {

    Button btnRegister;
    Button btnLinkToLogin;
    EditText inputName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Importing all assets like buttons, text fields
        inputName = (EditText) findViewById(R.id.registerName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);

        // Register Button Click event
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                String name = inputName.getText().toString();
//                String email = inputEmail.getText().toString();
//                String password = inputPassword.getText().toString();
//                UserService userService = new UserService();
//                User user = userService.register(name, email, password);
//
//                if (user != null) {
//                    registerErrorMsg.setText("");
//                    // user successfully logged in
//                    // Store user details in SQLite Database
//                    UserDao userDao = new UserDao(getApplicationContext());
//                    userService.logoutUser(getApplicationContext());
//                    userDao.addUser(user);
//                    Intent main = new Intent(getApplicationContext(), LoginActivity.class);
//
//                    // Close all views before launching Dashboard
//                    main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(main);
//
//                    // Close Login Screen
//                    finish();
//                } else {
//                    // Error in login
//                    registerErrorMsg.setText("Email already exist");
//                }
//            }
//        });
//
//        // Link to Login Screen
//        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),
//                        LoginActivity.class);
//                startActivity(i);
//                // Close Registration View
//                finish();
//            }
//        });
    }
}
