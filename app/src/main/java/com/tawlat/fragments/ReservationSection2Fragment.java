package com.tawlat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.tawlat.R;
import com.tawlat.models.Country;
import com.tawlat.models.UserModel;
import com.tawlat.models.Voucher;
import com.tawlat.utils.Notifications;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReservationSection2Fragment extends Fragment {

    public enum Mode {
        FULL,
        PARTIAL
    }

    private enum Type {
        NONE,
        POINTS,
        VOUCHERS,
    }

    private View mView;
    private LinearLayout mRedemptionContainer;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mMobilePhone;
    private EditText mSpecialRequests;
    private Spinner mMobileCodes;
    private Spinner mPoints;
    private Spinner mVouchersSpinner;
    private RadioButton mNothing;
    private RadioButton mPointsRadio;
    private RadioButton mVouchersRadio;
    private TextView mShowSpecialRequest;


    private UserModel mUser; //this has to be set in the onCreate()
    private String[] mCountryCodes; //this has to be set in the onCreate()
    private String[] mCountryCodesIds; //this has to be set in the onCreate()
    private String[] mCountryNamesCodes; //this has to be set in the onCreate()
    private String[] mVouchersTexts; //this has to be set in the onCreate()
    private String[] mVouchersIds; //this has to be set in the onCreate()

    private Type mType = Type.NONE;
    private Mode mMode = Mode.FULL;

    public ReservationSection2Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.reservation_section_2, container, false);
        init();
        return mView;
    }

    private void init() {
        initReferences();
        fillFields();

        if (mMode == Mode.FULL)
            initEvents();
    }

    private void initReferences() {
        mRedemptionContainer = ((LinearLayout) mView.findViewById(R.id.redemption_container));
        mFirstName = ((EditText) mView.findViewById(R.id.first_name));
        mLastName = ((EditText) mView.findViewById(R.id.last_name));
        mMobilePhone = ((EditText) mView.findViewById(R.id.mobile_number));
        mSpecialRequests = ((EditText) mView.findViewById(R.id.special_requests));
        mMobileCodes = ((Spinner) mView.findViewById(R.id.mobile_code));
        mPoints = ((Spinner) mView.findViewById(R.id.points_spinner));
        mVouchersSpinner = ((Spinner) mView.findViewById(R.id.vouchers_spinner));
        mNothing = ((RadioButton) mView.findViewById(R.id.none));
        mPointsRadio = ((RadioButton) mView.findViewById(R.id.points));
        mVouchersRadio = ((RadioButton) mView.findViewById(R.id.vouchers));
        mShowSpecialRequest = ((TextView) mView.findViewById(R.id.special_request_question));
    }

    private void fillFields() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mCountryNamesCodes);
        adapter.setDropDownViewResource(R.layout.reservation_spinner_item);
        mMobileCodes.setAdapter(adapter);

        if (mUser != null) {
            mFirstName.setText(mUser.getFirstName());
            mLastName.setText(mUser.getLastName());
            mMobilePhone.setText(mUser.getMobileNumber());
            mMobileCodes.setSelection(getIdx(mUser.getMobileCountry(), mCountryCodesIds));
        }

        if (mMode == Mode.FULL) {
            initPointsSpinner();
            initVouchersSpinners();

        } else {
            mRedemptionContainer.setVisibility(View.GONE);
            mShowSpecialRequest.setVisibility(View.GONE);
            mSpecialRequests.setVisibility(View.VISIBLE);
        }


    }

    private void initEvents() {
        mShowSpecialRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShowSpecialRequest.setVisibility(View.GONE);
                mSpecialRequests.setVisibility(View.VISIBLE);
            }
        });

        mNothing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    radioChanges(true, false, false);
            }
        });

        mPointsRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    radioChanges(false, true, false);
            }
        });

        mVouchersRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    radioChanges(false, false, true);
            }
        });
    }

    private void radioChanges(boolean none, boolean points, boolean vouchers) {
        if (none) {
            mPoints.setEnabled(false);
            mVouchersSpinner.setEnabled(false);
            mType = Type.NONE;
            mPointsRadio.setChecked(false);
            mVouchersRadio.setChecked(false);
        } else if (points) {
            mPoints.setEnabled(true);
            mVouchersSpinner.setEnabled(false);
            mType = Type.POINTS;
            mNothing.setChecked(false);
            mVouchersRadio.setChecked(false);
        } else if (vouchers) {
            mPoints.setEnabled(false);
            mVouchersSpinner.setEnabled(true);
            mType = Type.VOUCHERS;
            mNothing.setChecked(false);
            mPointsRadio.setChecked(false);
        }
    }

    private void initPointsSpinner() {
        final int points = mUser.getTotalPoint();
        final int len = points / 1000;
        final String[] pointsArray;

        pointsArray = new String[11];
        pointsArray[0] = getString(R.string.select_from_balance);

        for (int i = 1; i < 11; i++)
            pointsArray[i] = getActivity().getString(R.string.get_aed_d_off_my_bill_use_d_pts, i * 50, i * 1000);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                pointsArray) {


            @Override
            public boolean isEnabled(int position) {
                return (position <= len);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.reservation_spinner_item, null);
                }
                TextView tv = (TextView) v.findViewById(R.id.spinner_item_text);
                tv.setText(pointsArray[position]);

                if (position <= len)
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                else
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));
                return v;

            }
        };
        adapter.setDropDownViewResource(R.layout.reservation_spinner_item);
        mPoints.setAdapter(adapter);

        if (len == 0) {
            mPointsRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mType = Type.NONE;
                    Notifications.showAlertDialog(getActivity(), getString(R.string.info), getActivity().getString(R.string.you_can_only_redeem_points_when_you_reach_1000, points));
                }
            });
        }

        mPoints.setEnabled(false);
    }

    private void initVouchersSpinners() {
        int len = mVouchersTexts.length;
        String[] items = new String[len + 1];
        System.arraycopy(mVouchersTexts, 0, items, 1, len);

        if (len == 0) {
            items[0] = getString(R.string.you_dont_have_any_voucher);
            mVouchersRadio.setEnabled(false);
        } else
            items[0] = getString(R.string.select_voucher);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mVouchersSpinner.setAdapter(adapter);
        mVouchersSpinner.setEnabled(false);
    }

    private int getIdx(String id, String[] ids) {
        for (int i = 0; i < ids.length; i++)
            if (ids[i].equals(id))
                return i;

        return 0;
    }

    public void setCountries(ArrayList<Country> countries) {
        mCountryCodes = new String[countries.size()];
        mCountryCodesIds = new String[countries.size()];
        mCountryNamesCodes = new String[countries.size()];

        for (int i = 0; i < countries.size(); i++) {
            mCountryCodesIds[i] = countries.get(i).getId();
            mCountryCodes[i] = countries.get(i).getCallingCode();
            mCountryNamesCodes[i] = countries.get(i).getName() + " (" + countries.get(i).getCallingCode() + ")";
        }
    }

    public void setVouchers(ArrayList<Voucher> vouchers) {
        mVouchersIds = new String[vouchers.size()];
        mVouchersTexts = new String[vouchers.size()];

        for (int i = 0; i < vouchers.size(); i++) {
            mVouchersIds[i] = vouchers.get(i).getId();
            mVouchersTexts[i] = vouchers.get(i).getAmount() + " AED (" + vouchers.get(i).getVoucherCode() + ")";
        }
    }

    public void setUser(UserModel user) {
        mUser = user;
    }

    public void setMode(Mode mode) {
        mMode = mode;
    }

    public String getErrors() {
        if (mFirstName.getText().toString().trim().isEmpty() || mLastName.getText().toString().trim().isEmpty())
            return "First and last names cannot be empty.";
        else if (mMobilePhone.getText().toString().trim().isEmpty())
            return "Mobile number cannot be empty";
        else if (!TawlatUtils.validatePhoneNumber(mMobilePhone.getText().toString().trim()))
            return "Invalid mobile phone.";

        return null;
    }

    public String getFirstName() {
        return mFirstName.getText().toString().trim();
    }

    public String getLastName() {
        return mLastName.getText().toString().trim();
    }

    public String getMobileCountry() {
        return mCountryCodes[mMobileCodes.getSelectedItemPosition()];
    }

    public String getMobileNumber() {
        return mMobilePhone.getText().toString().trim();
    }

    public String getSpecialRequests() {
        return mSpecialRequests.getText().toString().trim();
    }

    public String getType() {
        if (mType == Type.POINTS && mPoints.getSelectedItemPosition() != 0) {
            return "Points";
        } else if (mType == Type.VOUCHERS && mVouchersSpinner.getSelectedItemPosition() != 0) {
            return "Voucher";
        } else {
            return "Normal";
        }
    }

    public int getPointsToRedeem() {
        if (mType != Type.POINTS || mPoints.getSelectedItemPosition() == 0) return 0;
        return mPoints.getSelectedItemPosition() * 1000;
    }

    public int getAmountToRedeem() {
        if (mType == Type.POINTS)
            return mPoints.getSelectedItemPosition() * 1000 / 20;
        if (mType == Type.VOUCHERS && mVouchersSpinner.getSelectedItemPosition() != 0)
            try {
                return Integer.parseInt(((String) mVouchersSpinner.getSelectedItem()).split(" ")[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 0;
            }
        else
            return 0;
    }


    public String getVoucherId() {
        if (mType != Type.VOUCHERS || mVouchersSpinner.getSelectedItemPosition() == 0) return "0";
        return mVouchersIds[mVouchersSpinner.getSelectedItemPosition() - 1];
    }

    public String getVoucherNumber() {
        if (mType != Type.VOUCHERS || mVouchersSpinner.getSelectedItemPosition() == 0) return "";
        Pattern pattern = Pattern.compile("\\((.+?)\\)");
        Matcher matcher = pattern.matcher(((String) mVouchersSpinner.getSelectedItem()));
        if (matcher.find())
            return matcher.group(1);

        return "";
    }


}
