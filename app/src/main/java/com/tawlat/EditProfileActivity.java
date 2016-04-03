package com.tawlat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    public final static String EDIT_PROFILE_RESULT = "edit_profile_result";
    public final static int EDIT_PROFILE_CODE = 1;
    private final static int IMAGE_PICKER_SELECT = 0;


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
    private ImageView mUserPhoto;
    private Button mUpdateButton;

    private Date mBirthDate = null;

    private String[] mCountriesIds;
    private String[] mCountriesNames;
    private String[] mCountriesNamesCodes;
    private String[] mCountriesCodes;

    private String[] mCitiesIds;
    private String[] mCitiesNames;

    private AlertDialog mProgressDialog;
    private UserModel mUser;

    private String mPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!User.getInstance().isLoggedIn()) {
            finish();
            return;
        }

        setTitle(getString(R.string.edit_profile));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        setContentView(R.layout.edit_profile_activity);
        loadUser();
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
        mUserPhoto = (ImageView) findViewById(R.id.user_photo);
        mUpdateButton = (Button) findViewById(R.id.update_button);
    }

    private void fillFields() {
        ArrayAdapter<String> adapter;

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCitiesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCitySpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCountriesNamesCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMobileCountrySpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCountriesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNationalitySpinner.setAdapter(adapter);

        /**
         * we have to hide
         * all the unnecessary fields:
         * 1- email
         * 2- email note
         * 3- password
         */
        mEmailBox.setVisibility(View.GONE);
        mEmailNote.setVisibility(View.GONE);
        mEmailLabel.setVisibility(View.GONE);
        mPasswordBox.setVisibility(View.GONE);
        mPasswordLabel.setVisibility(View.GONE);

        // fill the fields:
        mFirstNameBox.setText(mUser.getFirstName());
        mLastNameBox.setText(mUser.getLastName());
        mMobileCountrySpinner.setSelection(getIdx(mUser.getMobileCountry(), mCountriesIds));
        mMobileNumberBox.setText(mUser.getMobileNumber());
        mCitySpinner.setSelection(getIdx(mUser.getCurrentCity(), mCitiesIds));
        mNationalitySpinner.setSelection(getIdx(mUser.getNationality(), mCountriesIds));
        if (mUser.getDateOfBirth() != null) {
            mBirthDate = mUser.getDateOfBirth();
            mBirthdayBox.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(mBirthDate));
        }
        setGender(mUser.getGender());
        mAboutMeBox.setText(mUser.getAboutMe());

        Picasso.with(this)
                .load(mUser.getImage())
                .placeholder(R.drawable.no_avatar)
                .into(mUserPhoto);
    }

    private void initEvents() {
        mBirthdayBox.setOnClickListener(new View.OnClickListener() {
            DatePickerDialog mDatePickerDialog = new DatePickerDialog(EditProfileActivity.this,
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
                    }, Calendar.YEAR, Calendar.MONDAY, Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEverythingValid()) return;
                mProgressDialog = Notifications.showLoadingDialog(EditProfileActivity.this, getString(R.string.loading));
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
                        mPhotoPath,
                        mBirthDate,
                        new ApiListeners.OnItemLoadedListener() {
                            @Override
                            public void onLoaded(Result result, @Nullable Model item) {
                                if (mProgressDialog != null)
                                    mProgressDialog.dismiss();
                                if (result.isSucceeded() && item != null) {
                                    Broadcasting.sendUserUpdated(EditProfileActivity.this);
                                    Intent i = new Intent();
                                    i.putExtra(EDIT_PROFILE_RESULT, true);
                                    setResult(EDIT_PROFILE_CODE, i);
                                    finish();
                                } else {
                                    Notifications.showSnackBar(EditProfileActivity.this, result.getMessages().get(0));
                                }
                            }
                        }
                );

            }
        });

        mUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_PICKER_SELECT);
            }
        });
    }

    private void loadUser() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        UserApi.get(User.getInstance().getUser().getId(), new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                if (result.isSucceeded() && item != null) {
                    mUser = ((UserModel) item);
                    loadCountries();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(EditProfileActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadUser();
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

    private void loadCountries() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.countries(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {

                    mCountriesIds = new String[items.size()];
                    mCountriesNames = new String[items.size()];
                    mCountriesNamesCodes = new String[items.size()];
                    mCountriesCodes = new String[items.size()];

                    for (int i = 0; i < items.size(); i++) {
                        Country country = ((Country) items.get(i));
                        mCountriesIds[i] = country.getId();
                        mCountriesNames[i] = country.getName();
                        mCountriesNamesCodes[i] = country.getName() + " (" + country.getCallingCode() + ")";
                        mCountriesCodes[i] = country.getCallingCode();
                    }

                    loadCities();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(EditProfileActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                    Notifications.showYesNoDialog(EditProfileActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

        return true;
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
            if (id.equals(arr[i]))
                return i;
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_SELECT
                && resultCode == Activity.RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (cursor == null) return;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (picturePath != null) {
                Picasso.with(this)
                        .load(new File(picturePath))
                        .into(mUserPhoto);
                mPhotoPath = picturePath;
            }
        }
    }
}
