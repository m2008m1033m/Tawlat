package com.tawlat;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tawlat.core.ApiListeners;
import com.tawlat.core.Broadcasting;
import com.tawlat.core.User;
import com.tawlat.models.Model;
import com.tawlat.models.UserModel;
import com.tawlat.services.Result;
import com.tawlat.services.UserApi;
import com.tawlat.utils.Notifications;

public class VerificationActivity extends AppCompatActivity {
    public final static String USER = "user";

    private UserModel mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (User.getInstance().isLoggedIn()) {
            finish();
            return;
        }

        mUser = ((UserModel) getIntent().getExtras().getSerializable(USER));
        if (mUser == null) {
            finish();
            return;
        }

        setTitle(getString(R.string.verify_account));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        setContentView(R.layout.verification_activity);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        final EditText code = ((EditText) findViewById(R.id.code));
        final Button confirm = ((Button) findViewById(R.id.button));

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code.getText().toString().trim().isEmpty()) {
                    Notifications.showSnackBar(VerificationActivity.this, getString(R.string.code_cannot_be_empty));
                    return;
                }

                final AlertDialog progressDialog = Notifications.showLoadingDialog(VerificationActivity.this, getString(R.string.loading));
                UserApi.verify(
                        mUser.getId(),
                        code.getText().toString().trim(),
                        new ApiListeners.OnItemLoadedListener() {
                            @Override
                            public void onLoaded(Result result, @Nullable Model item) {
                                progressDialog.dismiss();
                                if (result.isSucceeded() && item != null) {
                                    User.getInstance().login(((UserModel) item));
                                    Broadcasting.sendLogin(VerificationActivity.this);
                                    finish();
                                } else {
                                    Notifications.showSnackBar(VerificationActivity.this, getString(R.string.invalid_code));
                                }
                            }
                        }
                );
            }
        });
    }
}
