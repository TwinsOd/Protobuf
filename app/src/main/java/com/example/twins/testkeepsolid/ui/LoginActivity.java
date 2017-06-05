package com.example.twins.testkeepsolid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.twins.testkeepsolid.BuildConfig;
import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.data.ApiFactory;
import com.example.twins.testkeepsolid.data.model.AuthAnswer;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.twins.testkeepsolid.Constant.ACTION;
import static com.example.twins.testkeepsolid.Constant.APP_VERSION;
import static com.example.twins.testkeepsolid.Constant.DEVICE;
import static com.example.twins.testkeepsolid.Constant.DEVICE_ID;
import static com.example.twins.testkeepsolid.Constant.KEY_SESSION_ID;
import static com.example.twins.testkeepsolid.Constant.LOCALE;
import static com.example.twins.testkeepsolid.Constant.LOGIN;
import static com.example.twins.testkeepsolid.Constant.LOGIN_VALUE;
import static com.example.twins.testkeepsolid.Constant.PASSWORD;
import static com.example.twins.testkeepsolid.Constant.PLATFORM;
import static com.example.twins.testkeepsolid.Constant.PLATFORM_VALUE;
import static com.example.twins.testkeepsolid.Constant.PLATFORM_VERSION;
import static com.example.twins.testkeepsolid.Constant.SERVICE;
import static com.example.twins.testkeepsolid.Constant.SERVICE_VALUE;
import static com.example.twins.testkeepsolid.Constant.TIME_ZONE;

public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView mLoginView, mPasswordView;
    private Button btnNext;
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginView = (AutoCompleteTextView) findViewById(R.id.login_text_view);
        mPasswordView = (AutoCompleteTextView) findViewById(R.id.password_text_view);

        btnNext = (Button) findViewById(R.id.next_button);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginView.setError(null);
                mPasswordView.setError(null);

                boolean cancel = false;
                View focusView = null;
                String login = mLoginView.getText().toString();
                String password = mPasswordView.getText().toString();

                if (TextUtils.isEmpty(password)) {
                    mPasswordView.setError(getString(R.string.error_field_required));
                    focusView = mPasswordView;
                    cancel = true;
                }
                if (TextUtils.isEmpty(login)) {
                    mLoginView.setError(getString(R.string.error_field_required));
                    focusView = mLoginView;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    btnNext.setClickable(false);
                    findViewById(R.id.login_progress_bar).setVisibility(View.VISIBLE);
                    hideKeyboard();
                    mLoginView.setError(null);
                    mPasswordView.setError(null);
                    auth(login, password);
                }
            }
        });
    }

    private void startMainActivity(String sessionId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(KEY_SESSION_ID, sessionId);
        startActivity(intent);
    }

    private void hideKeyboard() {
        InputMethodManager in = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(mPasswordView.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void auth(String login, String password) {
        ApiFactory.authAdapter().getSession(getFields(login, password)).enqueue(new Callback<AuthAnswer>() {
            @Override
            public void onResponse(Call<AuthAnswer> call, Response<AuthAnswer> response) {
                if (response.body() != null) {
                    Log.d(TAG, "getResponse = " + response.body().getResponse());
                    Log.d(TAG, "getSession = " + response.body().getSession());
                }

                if (response.body() != null && response.body().getResponse() == 200) {
                    startMainActivity(response.body().getSession());
                } else {
                    Toast.makeText(LoginActivity.this, "Error connect", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.login_progress_bar).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<AuthAnswer> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }

        });
    }

    private Map<String, String> getFields(String login, String password) {

        Map<String, String> map = new HashMap<>();
        try {
            map.put(ACTION, base64Encoded(LOGIN_VALUE));
            map.put(SERVICE, base64Encoded(SERVICE_VALUE));
            map.put(LOGIN, base64Encoded(login));
            map.put(PASSWORD, base64Encoded(password));
            map.put(DEVICE_ID, base64Encoded(Secure.getString(getContentResolver(), Secure.ANDROID_ID)));
            map.put(DEVICE, base64Encoded(android.os.Build.MODEL));
            map.put(PLATFORM, base64Encoded(PLATFORM_VALUE));
            map.put(PLATFORM_VERSION, base64Encoded(String.valueOf(android.os.Build.VERSION.SDK_INT)));
            map.put(APP_VERSION, base64Encoded(String.valueOf(BuildConfig.VERSION_CODE)));
            map.put(LOCALE, base64Encoded(Locale.getDefault().toString()));
            map.put(TIME_ZONE, base64Encoded(getTimeZone()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return map;
    }

    private String base64Encoded(String value) throws UnsupportedEncodingException {
        byte[] data = value.getBytes("UTF-8");
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    private String getTimeZone() {
        DateFormat date = new SimpleDateFormat("Z", Locale.getDefault());
        return date.format(Calendar.getInstance().getTime());
    }
}
