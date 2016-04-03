package com.tawlat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tawlat.core.ApiListeners;
import com.tawlat.core.Broadcasting;
import com.tawlat.core.User;
import com.tawlat.customViews.AppCompatPreferenceActivity;
import com.tawlat.models.City;
import com.tawlat.models.Model;
import com.tawlat.models.Text;
import com.tawlat.services.Result;
import com.tawlat.services.SupplementaryApi;
import com.tawlat.services.UserApi;
import com.tawlat.utils.Notifications;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammed on 3/24/16.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private AlertDialog mChangePasswordDialog;
    private AlertDialog mForgotPasswordDialog;
    private AlertDialog mChangeCityDialog;
    private AlertDialog mSupportDialog;
    private AlertDialog mProgressDialog;

    private String mCityNames[];
    private String mCityIds[];
    private int mCurrentlySelectedCityIdx;

    private boolean mIsEverythingLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if (mProgressDialog != null) mProgressDialog.dismiss();
        mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        /**
         * load the cities
         */
        loadCities();
    }


    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(
                User.getInstance().isLoggedIn() ? R.xml.settings_headers_logged_in : R.xml.settings_headers,
                target);

    }

    @Override
    public void onHeaderClick(Header header, int position) {
        super.onHeaderClick(header, position);
        if (!mIsEverythingLoaded) return;
        switch ((int) header.id) {
            case R.id.forgot_password:
                showForgotPasswordDialog();
                break;
            case R.id.change_password:
                showChangePasswordDialog();
                break;
            case R.id.change_city:
                showChangeCityDialog();
                break;
            case R.id.support:
                showSupportDialog();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showForgotPasswordDialog() {
        if (mForgotPasswordDialog == null)
            mForgotPasswordDialog = TawlatUtils.showForgotPassword(this);
        else
            mForgotPasswordDialog.show();
    }

    private void showChangePasswordDialog() {
        if (mChangePasswordDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View v = LayoutInflater.from(this).inflate(R.layout.change_password_dialog, null);

            final EditText newPassword = (EditText) v.findViewById(R.id.new_password);
            final EditText confirmPassword = (EditText) v.findViewById(R.id.confirm_password);

            builder
                    .setView(v)
                    .setTitle("Change Password")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mChangePasswordDialog.dismiss();
                        }
                    })
            ;

            mChangePasswordDialog = builder.create();


            final TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (
                            newPassword.getText().toString().length() < 6 ||
                                    confirmPassword.getText().toString().length() < 6)
                        mChangePasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    else
                        mChangePasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            };

            newPassword.addTextChangedListener(textWatcher);
            confirmPassword.addTextChangedListener(textWatcher);

            /**
             * listener for what happens when ok is clicked
             */
            final View.OnClickListener okOnClickedListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /**
                     * check if the new and the confirm are equal:
                     */
                    if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                        Notifications.showSnackBar(SettingsActivity.this, getString(R.string.the_new_passwords_do_not_match));
                        return;
                    }


                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = Notifications.showLoadingDialog(SettingsActivity.this, getString(R.string.loading));

                    UserApi.changePassword(User.getInstance().getUser().getId(), newPassword.getText().toString(), new ApiListeners.OnItemLoadedListener() {
                        @Override
                        public void onLoaded(Result result, @Nullable Model item) {
                            if (result.isSucceeded() && item != null)
                                Notifications.showSnackBar(SettingsActivity.this, ((Text) item).getText());
                            else
                                Notifications.showSnackBar(SettingsActivity.this, result.getMessages().get(0));
                        }
                    });

                }
            };


            mChangePasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = mChangePasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(okOnClickedListener);
                }
            });

            /**
             * what happens when dismissed
             */
            mChangePasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    newPassword.setText("");
                    confirmPassword.setText("");
                }
            });
        }


        mChangePasswordDialog.show();
        mChangePasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

    }

    private void showChangeCityDialog() {
        if (mChangeCityDialog == null) {
            mChangeCityDialog = Notifications.showListWithRadioButton(SettingsActivity.this, getString(R.string.change_city), mCityNames, mCurrentlySelectedCityIdx, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    TawlatUtils.setCurrentCityId(mCityIds[i]);
                    mCurrentlySelectedCityIdx = i;
                    Broadcasting.sendCityChanged(SettingsActivity.this, mCityIds[i]);
                    mChangeCityDialog.dismiss();
                }
            });
        }

        mChangeCityDialog.show();
    }

    private void showSupportDialog() {
        if (mSupportDialog == null) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);

            View view = LayoutInflater.from(this).inflate(R.layout.support_dialog, null);
            view.findViewById(R.id.support_dialog_about).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getString(R.string.url_about)));
                    startActivity(i);
                }
            });

            view.findViewById(R.id.support_dialog_faqs).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getString(R.string.url_faqs)));
                    startActivity(i);
                }
            });

            view.findViewById(R.id.support_dialog_terms_of_use).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getString(R.string.url_terms_of_use)));
                    startActivity(i);
                }
            });

            view.findViewById(R.id.support_dialog_privacy_policy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getString(R.string.url_privacy_policy)));
                    startActivity(i);
                }
            });

            view.findViewById(R.id.support_dialog_contact_us).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getString(R.string.url_contact_us)));
                    startActivity(i);
                }
            });

            b.setTitle(getString(R.string.support_section));
            b.setView(view);

            mSupportDialog = b.create();
            mSupportDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSupportDialog.dismiss();

                        }
                    });
        }

        mSupportDialog.show();
    }

    private void loadCities() {
        SupplementaryApi.cities("1", new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (mProgressDialog != null) mProgressDialog.dismiss();
                mProgressDialog = null;

                if (result.isSucceeded() && items != null) {
                    String currentCity = TawlatUtils.getCurrentCityId();

                    mCityIds = new String[items.size()];
                    mCityNames = new String[items.size()];

                    for (int i = 0; i < items.size(); i++) {
                        City city = ((City) items.get(i));
                        if (currentCity.equals(city.getId()))
                            mCurrentlySelectedCityIdx = i;
                        mCityIds[i] = city.getId();
                        mCityNames[i] = city.getName();
                    }
                    mIsEverythingLoaded = true;

                } else {

                    Notifications.showYesNoDialog(SettingsActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadCities();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });

                }
            }
        });
    }
}
