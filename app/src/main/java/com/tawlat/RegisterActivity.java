package com.tawlat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.tawlat.core.ApiListeners;
import com.tawlat.core.Broadcasting;
import com.tawlat.core.User;
import com.tawlat.models.City;
import com.tawlat.models.Country;
import com.tawlat.models.Model;
import com.tawlat.models.UserModel;
import com.tawlat.services.Result;
import com.tawlat.services.SupplementaryApi;
import com.tawlat.services.UserApi;
import com.tawlat.utils.Notifications;
import com.tawlat.utils.TawlatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    public final static String MODE = "mode";
    public final static String USER = "user";
    public final static int NORMAL = 0;
    public final static int FACEBOOK = 1;

    private EditText mFirstNameBox;
    private EditText mLastNameBox;
    private EditText mEmailBox;
    private TextView mEmailLabel;
    private TextView mEmailNote;
    private EditText mPasswordBox;
    private TextView mPasswordLabel;
    private Spinner mMobileCountrySpinner;
    private EditText mMobileNumberBox;
    private Spinner mCitySpinner;
    private Spinner mNationalitySpinner;
    private TextView mBirthdayBox;
    private RadioGroup mGenderRadioGroup;
    private EditText mAboutMeBox;
    private Button mRegisterButton;
    private CheckBox mTermsAndConditions;
    private TextView mTermsAndConditionsText;


    private Date mBirthDate = null;

    private String[] mCountriesIds;
    private String[] mCountriesNames;
    private String[] mCountriesNamesCodes;
    private String[] mCountriesCodes;

    private String[] mCitiesIds;
    private String[] mCitiesNames;

    private AlertDialog mProgressDialog;

    private int mMode;
    private UserModel mUser; // in case facebook registration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (User.getInstance().isLoggedIn()) {
            finish();
            return;
        }

        mMode = getIntent().getIntExtra(MODE, NORMAL);
        if (mMode == FACEBOOK) {
            mUser = ((UserModel) getIntent().getExtras().getSerializable(USER));
            if (mUser == null) {
                finish();
                return;
            }
        }

        setTitle(getString(R.string.register));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        setContentView(R.layout.register_activity);
        loadCountries();
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
        initReferences();
        fillFields();
        initEvents();
    }

    private void initReferences() {
        mFirstNameBox = (EditText) findViewById(R.id.first_name);
        mLastNameBox = (EditText) findViewById(R.id.last_name);
        mEmailBox = (EditText) findViewById(R.id.email);
        mEmailLabel = (TextView) findViewById(R.id.email_label);
        mEmailNote = (TextView) findViewById(R.id.email_note);
        mPasswordBox = (EditText) findViewById(R.id.password);
        mPasswordLabel = (TextView) findViewById(R.id.password_label);
        mMobileCountrySpinner = (Spinner) findViewById(R.id.mobile_code);
        mMobileNumberBox = (EditText) findViewById(R.id.mobile_number);
        mCitySpinner = (Spinner) findViewById(R.id.city);
        mNationalitySpinner = (Spinner) findViewById(R.id.nationality);
        mBirthdayBox = (TextView) findViewById(R.id.birthday);
        mGenderRadioGroup = (RadioGroup) findViewById(R.id.gender);
        mAboutMeBox = (EditText) findViewById(R.id.about_me);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mTermsAndConditions = (CheckBox) findViewById(R.id.terms_and_conditions);
        mTermsAndConditionsText = (TextView) findViewById(R.id.register_terms);
    }

    private void fillFields() {
        mTermsAndConditionsText.setMovementMethod(LinkMovementMethod.getInstance());

        ArrayAdapter<String> adapter;

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCitiesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCitySpinner.setAdapter(adapter);
        mCitySpinner.setSelection(getIdx("1", mCitiesIds));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCountriesNamesCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMobileCountrySpinner.setAdapter(adapter);
        mMobileCountrySpinner.setSelection(getIdx("1", mCountriesIds));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCountriesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNationalitySpinner.setAdapter(adapter);

        /**
         * if the mode is facebook, we have to hide
         * all the unnecessary fields:
         * 1- email
         * 2- email note
         * 3- password
         */
        if (mMode == FACEBOOK) {
            mEmailBox.setVisibility(View.GONE);
            mEmailNote.setVisibility(View.GONE);
            mEmailLabel.setVisibility(View.GONE);
            mPasswordBox.setVisibility(View.GONE);
            mPasswordLabel.setVisibility(View.GONE);

            // fill the fields:
            mFirstNameBox.setText(mUser.getFirstName());
            mLastNameBox.setText(mUser.getLastName());
            mLastNameBox.setText(mUser.getLastName());
            setGender(mUser.getGender());
            if (mUser.getDateOfBirth() != null) {
                mBirthDate = mUser.getDateOfBirth();
                mBirthdayBox.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(mBirthDate));
            }
        }
    }

    private void initEvents() {
        mBirthdayBox.setOnClickListener(new View.OnClickListener() {
            DatePickerDialog mDatePickerDialog = new DatePickerDialog(RegisterActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mBirthdayBox.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/"
                                    + year);
                            Calendar c = Calendar.getInstance();
                            c.set(year, monthOfYear, dayOfMonth);
                            mBirthDate = c.getTime();

                        }
                    }, 1980, 0, 1);

            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEverythingValid()) return;
                mProgressDialog = Notifications.showLoadingDialog(RegisterActivity.this, getString(R.string.loading));
                if (mMode == NORMAL) {
                    UserApi.register(
                            mFirstNameBox.getText().toString().trim(),
                            mLastNameBox.getText().toString().trim(),
                            mEmailBox.getText().toString().trim(),
                            mPasswordBox.getText().toString(),
                            mAboutMeBox.getText().toString().trim(),
                            getGender(),
                            mCitiesIds[mCitySpinner.getSelectedItemPosition()],
                            mNationalitySpinner.getSelectedItemPosition() == 0 ? "0" : mCountriesIds[mNationalitySpinner.getSelectedItemPosition() - 1], // because the nationality spinner has a none value not just the countries
                            mCountriesIds[mMobileCountrySpinner.getSelectedItemPosition()],
                            mCountriesCodes[mMobileCountrySpinner.getSelectedItemPosition()] + "-" + mMobileNumberBox.getText().toString().trim(),
                            mBirthDate,
                            new ApiListeners.OnItemLoadedListener() {
                                @Override
                                public void onLoaded(Result result, @Nullable Model item) {
                                    if (mProgressDialog != null)
                                        mProgressDialog.dismiss();
                                    if (result.isSucceeded() && item != null) {
                                        UserModel user = ((UserModel) item);
                                        Intent i = new Intent(RegisterActivity.this, VerificationActivity.class);
                                        Bundle b = new Bundle();
                                        b.putSerializable(VerificationActivity.USER, user);
                                        i.putExtras(b);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        String error = result.getMessages().get(0);
                                        if (error.equals("\"Code:AlreadyExists\""))
                                            error = getString(R.string.a_user_with_this_email_already_exists);
                                        else
                                            error = getString(R.string.unknown_error_has_occurred);
                                        Notifications.showSnackBar(RegisterActivity.this, error);
                                    }
                                }
                            }
                    );
                } else {
                    UserApi.update(
                            mUser.getId(),
                            mFirstNameBox.getText().toString().trim(),
                            mLastNameBox.getText().toString().trim(),
                            mUser.getEmail(),
                            mAboutMeBox.getText().toString().trim(),
                            getGender(),
                            mCitiesIds[mCitySpinner.getSelectedItemPosition()],
                            mCountriesIds[mNationalitySpinner.getSelectedItemPosition()],
                            mCountriesIds[mMobileCountrySpinner.getSelectedItemPosition()],
                            mCountriesCodes[mMobileCountrySpinner.getSelectedItemPosition()] + "-" + mMobileNumberBox.getText().toString().trim(),
                            null,
                            mBirthDate,
                            new ApiListeners.OnItemLoadedListener() {
                                @Override
                                public void onLoaded(Result result, @Nullable Model item) {
                                    if (mProgressDialog != null)
                                        mProgressDialog.dismiss();
                                    if (result.isSucceeded() && item != null) {
                                        // login the user, no  verification needed
                                        User.getInstance().login(((UserModel) item));
                                        Broadcasting.sendLogin(RegisterActivity.this);
                                        finish();
                                    } else {
                                        Notifications.showSnackBar(RegisterActivity.this, result.getMessages().get(0));
                                    }
                                }
                            }
                    );
                }
            }
        });

    }

    private boolean isEverythingValid() {
        String val;

        // check first name
        val = mFirstNameBox.getText().toString();
        if (val.equals("") || val.length() < 2) {
            Notifications.showSnackBar(this, getString(R.string.first_name_should_be_filled));
            return false;
        }

        // check last name
        val = mLastNameBox.getText().toString();
        if (val.equals("") || val.length() < 2) {
            Notifications.showSnackBar(this, getString(R.string.last_name_should_be_filled));
            return false;
        }


        if (mMode != FACEBOOK) {
            // check email
            val = mEmailBox.getText().toString();
            if (val.equals("")) {
                Notifications.showSnackBar(this, getString(R.string.email_cannot_be_empty));
                return false;
            }
            if (!TawlatUtils.validateEmail(val)) {
                Notifications.showSnackBar(this, getString(R.string.the_email_is_not_valid));
                return false;
            }

            // check password
            val = mPasswordBox.getText().toString();
            if (val.equals("") || val.length() < 6) {
                Notifications.showSnackBar(this, getString(R.string.password_should_be_filled));
                return false;
            }
        }


        // check mobile number
        val = mMobileNumberBox.getText().toString();
        if (val.equals("")) {
            Notifications.showSnackBar(this, getString(R.string.phone_number_should_be_filled));
            return false;
        }


        val = mMobileNumberBox.getText().toString();
        if (!TawlatUtils.validatePhoneNumber(val)) {
            Notifications.showSnackBar(this, getString(R.string.invalid_phone_number));
            return false;
        }

        // t&c
        if (!mTermsAndConditions.isChecked()) {
            Notifications.showSnackBar(this, getString(R.string.you_must_agree_to_the_terms_and_coditions));
            return false;
        }


        return true;
    }

    private void loadCountries() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.countries(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {

                    mCountriesIds = new String[items.size()];
                    mCountriesNames = new String[items.size() + 1];
                    mCountriesNamesCodes = new String[items.size()];
                    mCountriesCodes = new String[items.size()];

                    mCountriesNames[0] = getString(R.string.none);

                    for (int i = 0; i < items.size(); i++) {
                        Country country = ((Country) items.get(i));
                        mCountriesIds[i] = country.getId();
                        mCountriesNames[i + 1] = country.getName();
                        mCountriesNamesCodes[i] = country.getName() + " (" + country.getCallingCode() + ")";
                        mCountriesCodes[i] = country.getCallingCode();
                    }

                    loadCities();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(RegisterActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadCountries();
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

    private void loadCities() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.cities("1", new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (mProgressDialog != null) mProgressDialog.dismiss();
                mProgressDialog = null;

                if (result.isSucceeded() && items != null) {

                    mCitiesIds = new String[items.size()];
                    mCitiesNames = new String[items.size()];

                    for (int i = 0; i < items.size(); i++) {
                        City city = ((City) items.get(i));
                        mCitiesIds[i] = city.getId();
                        mCitiesNames[i] = city.getName();
                    }

                    init();

                } else {
                    Notifications.showYesNoDialog(RegisterActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    private String getGender() {
        int radioButtonID = mGenderRadioGroup.getCheckedRadioButtonId();

        switch (mGenderRadioGroup.indexOfChild(findViewById(radioButtonID))) {
            case 0:
                return "male";
            case 1:
                return "female";
            default:
                return "";

        }
    }

    private void setGender(String gender) {
        switch (gender.toLowerCase()) {
            case "male":
                ((RadioButton) findViewById(R.id.male)).setChecked(true);
                break;
            case "female":
                ((RadioButton) findViewById(R.id.female)).setChecked(true);
                break;
        }
    }

    private int getIdx(String id, String[] arr) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i].equals(id))
                return i;
        return 0;
    }
}
