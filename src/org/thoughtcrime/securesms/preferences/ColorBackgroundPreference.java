package org.thoughtcrime.securesms.preferences;

import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.Preference;

/**
 * Created by Sasha on 10.04.2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.thoughtcrime.securesms.R;
/**
 * An advanced copy-paste programming
 */

public class ColorBackgroundPreference extends Preference {
    private int[] mColorChoices = {};
    private int mValue = 0;
    private int mItemLayoutId = R.layout.color_bgpreference_item;
    private int mNumColumns = 5;
    private View mPreviewView;
    private int main_color = R.color.signal_primary;

    public ColorBackgroundPreference(Context context) {
        super(context);
        initAttrs(null, 0);
    }

    public ColorBackgroundPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
    }

    public ColorBackgroundPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs, defStyle);
    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorBackgroundPreference, defStyle, defStyle);

        try {
            mItemLayoutId = a.getResourceId(R.styleable.ColorBackgroundPreference_itemBgLayout, mItemLayoutId);
            mNumColumns = a.getInteger(R.styleable.ColorBackgroundPreference_bgnumColumns, mNumColumns);
        } finally {
            a.recycle();
        }

        setWidgetLayoutResource(mItemLayoutId);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mPreviewView = view.findViewById(R.id.color_bgview);
        setColorViewValue(mPreviewView, mValue, false);
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            mValue = value;
            persistInt(value);
            notifyChanged();
        }
    }

    public void setChoices(int[] values) {
        mColorChoices = values;
    }

    @Override
    protected void onClick() {
        
       // R.color.signal_primary = R.color.deep_purple_500;
        /*super.onClick();

        ColorBackgroundPreference.ColorDialogFragment fragment = ColorBackgroundPreference.ColorDialogFragment.newInstance();
        fragment.setPreference(this);

        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .add(fragment, getFragmentTag())
                .commit();*/
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();

        AppCompatActivity activity = (AppCompatActivity) getContext();
        ColorBackgroundPreference.ColorDialogFragment fragment = (ColorBackgroundPreference.ColorDialogFragment) activity
                .getSupportFragmentManager().findFragmentByTag(getFragmentTag());
        if (fragment != null) {
            // re-bind preference to fragment
            fragment.setPreference(this);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public String getFragmentTag() {
        return "color_" + getKey();
    }

    public int getValue() {
        return mValue;
    }

    public static class ColorDialogFragment extends android.support.v4.app.DialogFragment {
        private ColorBackgroundPreference mPreference;
        private GridLayout mColorGrid;

        public ColorDialogFragment() {
        }

        public static ColorBackgroundPreference.ColorDialogFragment newInstance() {
            return new ColorBackgroundPreference.ColorDialogFragment();
        }

        public void setPreference(ColorBackgroundPreference preference) {
            mPreference = preference;
            repopulateItems();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            repopulateItems();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View rootView = layoutInflater.inflate(R.layout.color_bgpreference_items, null);

            mColorGrid = (GridLayout) rootView.findViewById(R.id.color_bggrid);
            mColorGrid.setColumnCount(mPreference.mNumColumns);
            repopulateItems();

            return new AlertDialog.Builder(getActivity())
                    .setView(rootView)
                    .create();
        }

        private void repopulateItems() {
            if (mPreference == null || mColorGrid == null) {
                return;
            }

            Context context = mColorGrid.getContext();
            mColorGrid.removeAllViews();
            for (final int color : mPreference.mColorChoices) {
                View itemView = LayoutInflater.from(context)
                        .inflate(R.layout.color_bgpreference_item, mColorGrid, false);

                setColorViewValue(itemView.findViewById(R.id.color_bgview), color,
                        color == mPreference.getValue());
                itemView.setClickable(true);
                itemView.setFocusable(true);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPreference.setValue(color);
                        dismiss();
                    }
                });

                mColorGrid.addView(itemView);
            }

            sizeDialog();
        }

        @Override
        public void onStart() {
            super.onStart();
            sizeDialog();
        }

        private void sizeDialog() {}
    }

    private static void setColorViewValue(View view, int color, boolean selected) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            Resources res = imageView.getContext().getResources();

            Drawable currentDrawable = imageView.getDrawable();
            GradientDrawable colorChoiceDrawable;
            if (currentDrawable instanceof GradientDrawable) {
                // Reuse drawable
                colorChoiceDrawable = (GradientDrawable) currentDrawable;
            } else {
                colorChoiceDrawable = new GradientDrawable();
                colorChoiceDrawable.setShape(GradientDrawable.OVAL);
            }

            // Set stroke to dark version of color
//      int darkenedColor = Color.rgb(
//          Color.red(color) * 192 / 256,
//          Color.green(color) * 192 / 256,
//          Color.blue(color) * 192 / 256);

            colorChoiceDrawable.setColor(color);
//      colorChoiceDrawable.setStroke((int) TypedValue.applyDimension(
//          TypedValue.COMPLEX_UNIT_DIP, 2, res.getDisplayMetrics()), darkenedColor);

            Drawable drawable = colorChoiceDrawable;
            if (selected) {
                BitmapDrawable checkmark = (BitmapDrawable) res.getDrawable(R.drawable.check);
                checkmark.setGravity(Gravity.CENTER);
                drawable = new LayerDrawable(new Drawable[]{
                        colorChoiceDrawable,
                        checkmark});
            }

            imageView.setImageDrawable(drawable);

        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
    }
    /*private class BgColorChangeListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            final int           value         = (Integer) newValue;
            final View view = view.findViewById(R.id.color_bgview);;
            final MaterialColor selectedColor = MaterialColors.BACKGROUND_PALETTE.getByColor(getActivity(), value);
            //final MaterialColor currentColor  = recipients.getColor();
            final MaterialColor currentColor  = view.findViewById(R.id.color_bgview);

            if (selectedColor == null) return true;

            if (preference.isEnabled() && !currentColor.equals(selectedColor)) {
                recipients.setColor(selectedColor);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Context context = getActivity();
                        DatabaseFactory.getRecipientPreferenceDatabase(context)
                                .setColor(recipients, selectedColor);

                        if (DirectoryHelper.getUserCapabilities(context, recipients)
                                .getTextCapability() == DirectoryHelper.UserCapabilities.Capability.SUPPORTED)
                        {
                            ApplicationContext.getInstance(context)
                                    .getJobManager()
                                    .add(new MultiDeviceContactUpdateJob(context, recipients.getPrimaryRecipient().getRecipientId()));
                        }
                        return null;
                    }
                }.execute();
            }
            return true;
        }
    }*/
}
