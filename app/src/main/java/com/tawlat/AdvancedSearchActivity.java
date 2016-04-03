package com.tawlat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.tawlat.core.ApiListeners;
import com.tawlat.core.Broadcasting;
import com.tawlat.customViews.MultiSpinner;
import com.tawlat.models.City;
import com.tawlat.models.Cuisine;
import com.tawlat.models.GoodFor;
import com.tawlat.models.Location;
import com.tawlat.models.Model;
import com.tawlat.models.serializables.AdvancedSearchRequest;
import com.tawlat.services.Result;
import com.tawlat.services.SupplementaryApi;
import com.tawlat.utils.MiscUtils;
import com.tawlat.utils.Notifications;
import com.tawlat.utils.TawlatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdvancedSearchActivity extends AppCompatActivity {

    private MultiSpinner mLocationsSpinner;
    private MultiSpinner mCuisinesSpinner;
    private MultiSpinner mGoodForSpinner;
    private MultiSpinner mPricesSpinner;
    private Spinner mTimesSpinner;
    private Spinner mPeopleSpinner;
    private TextView mDate;
    private Switch mTawlatPartners;
    private Button mSearchButton;


    private ArrayList<String> mCityNames = new ArrayList<>();
    private ArrayList<String> mCityIds = new ArrayList<>();

    private ArrayList<String> mLocationsIds = new ArrayList<>();
    private ArrayList<String> mLocationsNames = new ArrayList<>();

    private ArrayList<String> mGoodForIds = new ArrayList<>();
    private ArrayList<String> mGoodForNames = new ArrayList<>();

    private ArrayList<String> mCuisinesIds = new ArrayList<>();
    private ArrayList<String> mCuisinesNames = new ArrayList<>();

    private BroadcastReceiver mBroadcastReceiver;

    private AlertDialog mProgressDialog;
    private AlertDialog mChangeCityDialog;
    private int mCurrentlySelectedCityIdx;

    private boolean mIsLoaded = false;
    private Date mSelectedDate = Calendar.getInstance().getTime();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advnaced_search);
        setupBroadcastReceiver();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.mipmap.back);
        }
        setTitle(R.string.advanced_search);
        loadCities();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.advanced_search_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                resetFields();
                return true;
            case R.id.location:
                showChangeCityDialog();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                loadGoodFor();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Broadcasting.CITY_CHANGED));
    }

    private void init() {
        loadCities();
        initReferences();
        fillFields();
        initEvents();
    }

    private void initEvents() {

        /**
         * the search button
         */
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AdvancedSearchRequest advancedSearchRequest = new AdvancedSearchRequest();

                    List<String> selection;
                    boolean[] selected;
                    int counter;

                    if (!everyThingIsOk())
                        return;

                    /**
                     * locations
                     */
                    advancedSearchRequest.locationIds = null;
                    selection = mLocationsSpinner.getSelectedItems();
                    counter = 0;
                    selected = mLocationsSpinner.getSelected();
                    if (selection.size() != selected.length) {
                        advancedSearchRequest.locationIds = new String[selection.size()];
                        for (int i = 0; i < selected.length; i++)
                            if (selected[i])
                                advancedSearchRequest.locationIds[counter++] = mLocationsIds.get(i);
                    }


                    /**
                     * cuisines
                     */
                    advancedSearchRequest.cuisineIds = null;
                    selection = mCuisinesSpinner.getSelectedItems();
                    counter = 0;
                    selected = mCuisinesSpinner.getSelected();
                    if (selection.size() != selected.length) {
                        advancedSearchRequest.cuisineIds = new String[selection.size()];
                        for (int i = 0; i < selected.length; i++)
                            if (selected[i])
                                advancedSearchRequest.cuisineIds[counter++] = mCuisinesIds.get(i);
                    }

                    /**
                     * good for
                     */
                    advancedSearchRequest.goodForIds = null;
                    selection = mGoodForSpinner.getSelectedItems();
                    counter = 0;
                    selected = mGoodForSpinner.getSelected();
                    if (selection.size() != selected.length) {
                        advancedSearchRequest.goodForIds = new String[selection.size()];
                        for (int i = 0; i < selected.length; i++)
                            if (selected[i])
                                advancedSearchRequest.goodForIds[counter++] = mGoodForIds.get(i);
                    }

                    /**
                     * price
                     */
                    advancedSearchRequest.prices = null;
                    selection = mPricesSpinner.getSelectedItems();
                    selected = mPricesSpinner.getSelected();
                    if (selection.size() != selected.length) {
                        advancedSearchRequest.prices = new String[selection.size()];
                        for (int i = 0; i < advancedSearchRequest.prices.length; i++)
                            advancedSearchRequest.prices[i] = selection.get(i);
                    }

                    /**
                     * time
                     */
                    advancedSearchRequest.timeOfDay = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US).parse("1970-01-01 " + mTimesSpinner.getSelectedItem());


                    /**
                     * date
                     */
                    Calendar c = Calendar.getInstance();
                    c.setTime(mSelectedDate);
                    advancedSearchRequest.dayOfWeek = MiscUtils.getDayOfWeek(c.getTime());

                    /**
                     * people
                     */
                    advancedSearchRequest.people = (mPeopleSpinner.getSelectedItemPosition() + 1);


                    advancedSearchRequest.isDirectoryIncluded = !mTawlatPartners.isChecked();

                    advancedSearchRequest.cityId = TawlatUtils.getCurrentCityId();

                    /**
                     * navigate to the search result
                     * activity
                     */
                    Bundle b = new Bundle();
                    b.putSerializable(SearchResultActivity.REQUEST, advancedSearchRequest);

                    Intent i = new Intent(AdvancedSearchActivity.this, SearchResultActivity.class);
                    i.putExtra(SearchResultActivity.TYPE, 1);
                    i.putExtras(b);

                    startActivity(i);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean everyThingIsOk() {
        if (mLocationsSpinner.getSelectedItems().size() == 0) {
            showMessage(getString(R.string.you_have_to_choose_location));
            return false;
        }

        if (mCuisinesSpinner.getSelectedItems().size() == 0) {
            showMessage(getString(R.string.you_have_to_choose_cuisine));
            return false;
        }

        if (mGoodForSpinner.getSelectedItems().size() == 0) {
            showMessage(getString(R.string.you_have_to_choose_good_for));
            return false;
        }

        if (mPricesSpinner.getSelectedItems().size() == 0) {
            showMessage(getString(R.string.you_have_to_choose_price_range));
            return false;
        }


        return true;
    }

    private void showMessage(String message) {
        Notifications.showSnackBar(this, message);
    }

    private void fillFields() {
        fillSpinners();
        /**
         * prices spinner
         */
        mPricesSpinner.setItems(Arrays.asList("Any Price Range", "$", "$$", "$$$", "$$$$", "$$$$$"), "Any Price Range", new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {

            }

            @Override
            public void onItemSelectionChanged(int position, boolean isChecked) {
                if (position == 0)
                    mPricesSpinner.selectAll(isChecked);
                else
                    mPricesSpinner.setSelected(0, mPricesSpinner.isAllSelected());
            }
        });

        /**
         * times spinner
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_italic, getResources().getStringArray(R.array.reservation_time_options));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimesSpinner.setAdapter(adapter);
        mTimesSpinner.setSelection(22); //07:00 pm

        /**
         * People spinner
         */
        ArrayAdapter<String> peopleAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_italic, getResources().getStringArray(R.array.people_number_options));
        peopleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPeopleSpinner.setAdapter(peopleAdapter);
        mPeopleSpinner.setSelection(1); //2 People

        /**
         * date
         */
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth);
                        mSelectedDate = c.getTime();
                        mDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(c.getTime()));
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 2);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        /**
         * show the dialog when the
         * date is clicked
         */
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        mDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Calendar.getInstance().getTime()));
    }

    private void initReferences() {
        mLocationsSpinner = ((MultiSpinner) findViewById(R.id.locations));
        mCuisinesSpinner = ((MultiSpinner) findViewById(R.id.cuisine));
        mGoodForSpinner = ((MultiSpinner) findViewById(R.id.good_for));
        mPricesSpinner = ((MultiSpinner) findViewById(R.id.price));
        mTimesSpinner = ((Spinner) findViewById(R.id.time));
        mPeopleSpinner = ((Spinner) findViewById(R.id.people));
        mDate = ((TextView) findViewById(R.id.date));
        mSearchButton = ((Button) findViewById(R.id.search_button));
        mTawlatPartners = ((Switch) findViewById(R.id.tawlat_partner));
    }

    private void resetFields() {
        mLocationsSpinner.selectAll(true);
        mCuisinesSpinner.selectAll(true);
        mGoodForSpinner.selectAll(true);
        mPricesSpinner.selectAll(true);
        mTimesSpinner.setSelection(22);
        mDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Calendar.getInstance().getTime()));
        mPeopleSpinner.setSelection(1);

    }

    private void fillSpinners() {
        refreshLocationSpinner();
        refreshCuisinesSpinner();
        refreshGoodForSpinner();
    }

    private void refreshLocationSpinner() {
        final MultiSpinner.MultiSpinnerListener multiSpinnerListener = new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {

            }

            @Override
            public void onItemSelectionChanged(int position, boolean isChecked) {
                if (position == 0)
                    mLocationsSpinner.selectAll(isChecked);
                else
                    mLocationsSpinner.setSelected(0, mLocationsSpinner.isAllSelected());
            }
        };
        mLocationsSpinner.clear();
        mLocationsNames.add(0, getString(R.string.all_locations));
        mLocationsIds.add(0, "-1");
        mLocationsSpinner.setItems(mLocationsNames, getString(R.string.all_locations), multiSpinnerListener);
    }

    private void refreshGoodForSpinner() {
        final MultiSpinner.MultiSpinnerListener multiSpinnerListener = new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
            }

            @Override
            public void onItemSelectionChanged(int position, boolean isChecked) {
                if (position == 0)
                    mGoodForSpinner.selectAll(isChecked);
                else
                    mGoodForSpinner.setSelected(0, mGoodForSpinner.isAllSelected());
            }
        };
        mGoodForSpinner.clear();
        mGoodForNames.add(0, getString(R.string.all_good_for));
        mGoodForIds.add(0, "-1");
        mGoodForSpinner.setItems(mGoodForNames, getString(R.string.all_good_for), multiSpinnerListener);
    }

    private void refreshCuisinesSpinner() {
        final MultiSpinner.MultiSpinnerListener multiSpinnerListener = new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
            }

            @Override
            public void onItemSelectionChanged(int position, boolean isChecked) {
                if (position == 0)
                    mCuisinesSpinner.selectAll(isChecked);
                else
                    mCuisinesSpinner.setSelected(0, mCuisinesSpinner.isAllSelected());
            }
        };
        mCuisinesSpinner.clear();
        mCuisinesNames.add(0, getString(R.string.all_cuisines));
        mCuisinesIds.add(0, "-1");
        mCuisinesSpinner.setItems(mCuisinesNames, getString(R.string.all_cuisines), multiSpinnerListener);
    }

    private void loadCities() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.cities("1", new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    mCityIds.clear();
                    mCityNames.clear();
                    for (int i = 0; i < items.size(); i++) {
                        City city = ((City) items.get(i));
                        mCityIds.add(city.getId());
                        mCityNames.add(city.getName());
                    }
                    loadGoodFor();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(AdvancedSearchActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    private void loadGoodFor() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.goodFor(TawlatUtils.getCurrentCityId(), new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    mGoodForIds.clear();
                    mGoodForNames.clear();
                    for (int i = 0; i < items.size(); i++) {
                        GoodFor goodFor = ((GoodFor) items.get(i));
                        mGoodForIds.add(goodFor.getId());
                        mGoodForNames.add(goodFor.getName());
                    }
                    loadCuisines();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(AdvancedSearchActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadGoodFor();
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

    private void loadCuisines() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.cuisines(TawlatUtils.getCurrentCityId(), new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    mCuisinesIds.clear();
                    mCuisinesNames.clear();
                    for (int i = 0; i < items.size(); i++) {
                        Cuisine cuisine = ((Cuisine) items.get(i));
                        mCuisinesIds.add(cuisine.getId());
                        mCuisinesNames.add(cuisine.getName());
                    }
                    loadLocations();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(AdvancedSearchActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadCuisines();
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

    private void loadLocations() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.locations(TawlatUtils.getCurrentCityId(), new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (mProgressDialog != null) mProgressDialog.dismiss();
                mProgressDialog = null;
                if (result.isSucceeded() && items != null) {
                    mLocationsIds.clear();
                    mLocationsNames.clear();
                    for (int i = 0; i < items.size(); i++) {
                        Location location = ((Location) items.get(i));
                        mLocationsIds.add(location.getId());
                        mLocationsNames.add(location.getName());
                    }
                    if (!mIsLoaded) {
                        mIsLoaded = true;
                        init();
                    }
                    fillSpinners();
                } else {

                    mProgressDialog = null;
                    Notifications.showYesNoDialog(AdvancedSearchActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadLocations();
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

    private void showChangeCityDialog() {
        if (mChangeCityDialog == null) {
            mChangeCityDialog = Notifications.showListWithRadioButton(AdvancedSearchActivity.this, getString(R.string.change_city), mCityNames.toArray(new String[mCityNames.size()]), mCurrentlySelectedCityIdx, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    TawlatUtils.setCurrentCityId(mCityIds.get(i));
                    mCurrentlySelectedCityIdx = i;
                    Broadcasting.sendCityChanged(AdvancedSearchActivity.this, mCityIds.get(i));
                    mChangeCityDialog.dismiss();
                }
            });
        }

        mChangeCityDialog.show();
    }
}
