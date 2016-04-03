package com.tawlat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.Broadcasting;
import com.tawlat.core.User;
import com.tawlat.models.Model;
import com.tawlat.models.UserModel;
import com.tawlat.services.Result;
import com.tawlat.services.UserApi;
import com.tawlat.utils.Notifications;
import com.tawlat.utils.TawlatUtils;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    public static final String SUCCESS = "success";

    private EditText mEmail;
    private EditText mPassword;
    private FloatingActionButton mLoginButton;
    private FloatingActionButton mFacebookLogin;
    private TextView mForgotPassword;
    private TextView mRegister;

    private AlertDialog mProgressDialog;
    private AlertDialog mForgotPasswordDialog;

    private CallbackManager mCallbackManager;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBroadcastReceiver();
        setContentView(R.layout.login_activity);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private void setupBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Broadcasting.LOGIN))
                    finish();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Broadcasting.LOGIN));
    }

    private void init() {
        initReferences();
        initFacebook();
        initEvents();
    }

    private void initEvents() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString();

                /**
                 * validate email
                 */
                if (!TawlatUtils.validateEmail(email)) {
                    Notifications.showSnackBar(LoginActivity.this, getString(R.string.the_email_is_not_valid));
                    return;
                }

                /**
                 * validate password
                 */
                if (password.isEmpty()) {
                    Notifications.showSnackBar(LoginActivity.this, getString(R.string.password_cannot_be_empty));
                    return;
                }

                /**
                 * perform the login
                 */
                if (mProgressDialog != null) mProgressDialog.dismiss();
                mProgressDialog = Notifications.showLoadingDialog(LoginActivity.this, getString(R.string.loading));
                UserApi.login(email, password, new ApiListeners.OnItemLoadedListener() {
                    @Override
                    public void onLoaded(Result result, @Nullable Model item) {
                        if (mProgressDialog != null) mProgressDialog.dismiss();
                        mProgressDialog = null;

                        if (result.isSucceeded() && item != null) {
                            UserModel user = ((UserModel) item);
                            // check if the user is verified if he/she is not a facebook user:
                            if (!user.isVerified()) {
                                Bundle b = new Bundle();
                                b.putSerializable(VerificationActivity.USER, user);
                                Intent i = new Intent(LoginActivity.this, VerificationActivity.class);
                                i.putExtras(b);
                                startActivity(i);
                            } else {
                                User.getInstance().login(((UserModel) item));
                                Broadcasting.sendLogin(LoginActivity.this);
                                Intent intent = new Intent();
                                intent.putExtra(SUCCESS, true);
                                setResult(Activity.RESULT_OK, intent);
                            }
                        } else
                            Notifications.showSnackBar(LoginActivity.this, getString(R.string.invaild_email_or_password));
                    }
                });

            }
        });

        mFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends", "user_birthday", "user_about_me", "email"));
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void initReferences() {
        mEmail = ((EditText) findViewById(R.id.email));
        mPassword = ((EditText) findViewById(R.id.password));
        mLoginButton = ((FloatingActionButton) findViewById(R.id.login_button));
        mFacebookLogin = ((FloatingActionButton) findViewById(R.id.facebook_button));
        mForgotPassword = ((TextView) findViewById(R.id.forgot_password));
        mRegister = ((TextView) findViewById(R.id.register));
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Login", loginResult.getAccessToken().getToken());
                if (mProgressDialog != null) mProgressDialog.dismiss();
                mProgressDialog = Notifications.showLoadingDialog(LoginActivity.this, getString(R.string.loading));

                UserApi.facebookLogin(loginResult.getAccessToken().getToken(), new ApiListeners.OnItemLoadedListener() {
                    @Override
                    public void onLoaded(Result result, @Nullable Model item) {
                        if (mProgressDialog != null) mProgressDialog.dismiss();
                        mProgressDialog = null;
                        if (result.isSucceeded() && item != null) {
                            UserModel user = ((UserModel) item);
                            if (user.getMobileNumber() == null || user.getMobileNumber().isEmpty()) {
                                Bundle b = new Bundle();
                                b.putSerializable(RegisterActivity.USER, user);
                                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                                i.putExtra(RegisterActivity.MODE, RegisterActivity.FACEBOOK);
                                i.putExtras(b);
                                startActivity(i);
                            } else {
                                User.getInstance().login(user);
                                Broadcasting.sendLogin(LoginActivity.this);
                                Intent intent = new Intent();
                                intent.putExtra(SUCCESS, true);
                                setResult(Activity.RESULT_OK, intent);
                            }
                        } else {
                            String error = result.getMessages().get(0);
                            if (error.equals("null"))
                                error = getString(R.string.a_user_with_this_email_already_exists);
                            Notifications.showSnackBar(LoginActivity.this, error);
                        }
                    }
                });

                LoginManager.getInstance().logOut();
            }

            @Override
            public void onCancel() {
                Log.d("LoginFragment", "Canceled");
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("LoginFragment", "Error");
                e.printStackTrace();
                LoginManager.getInstance().logOut();
            }
        });
    }

    private void showForgotPasswordDialog() {
        if (mForgotPasswordDialog == null)
            mForgotPasswordDialog = TawlatUtils.showForgotPassword(this);
        else
            mForgotPasswordDialog.show();
    }
}
