package com.cliambrown.pilltime.meds;

import android.graphics.Typeface;
import android.text.ParcelableSpan;
import android.text.style.StyleSpan;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.cliambrown.pilltime.utilities.SimpleMenuActivity;
import com.cliambrown.pilltime.PillTimeApplication;
import com.cliambrown.pilltime.R;
import com.cliambrown.pilltime.utilities.ThemeHelper;
import com.cliambrown.pilltime.utilities.Utils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.*;

public class EditMedActivity extends SimpleMenuActivity {

    ConstraintLayout cl_editMed_parent;
    EditText et_editMed_name;
    NumberPicker np_editMed_maxDose;
    NumberPicker np_editMed_doseHoursDays;
    AppCompatSpinner sp_dayshours_picker;
    SwitchCompat switch_editMed_trackRemainingDoses;
    LinearLayout ll_editMed_trackRemainingDoses;
    NumberPicker np_editMed_remainingDoses;
    TextView tv_editMed_remainingReportedAt;
    Flow flow_editMed_colors;
    ExtendedFloatingActionButton btn_editMed_save;
    PillTimeApplication mApp;
    int medID;
    String[] colors;
    String selectedColor;
    List<ImageButton> colorButtons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_med);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        cl_editMed_parent = findViewById(R.id.cl_editMed_parent);
        et_editMed_name = findViewById(R.id.et_editMed_name);
        flow_editMed_colors = findViewById(R.id.flow_editMed_colors);
        btn_editMed_save = findViewById(R.id.btn_editMed_save);

        np_editMed_maxDose = findViewById(R.id.np_editMed_maxDose);
        np_editMed_maxDose.setMinValue(1);
        np_editMed_maxDose.setMaxValue(100);
        np_editMed_maxDose.setWrapSelectorWheel(false);

        np_editMed_doseHoursDays = findViewById(R.id.np_editMed_doseHoursDays);
        np_editMed_doseHoursDays.setMinValue(1);
        np_editMed_doseHoursDays.setMaxValue(100);
        np_editMed_doseHoursDays.setWrapSelectorWheel(false);

        sp_dayshours_picker = findViewById(R.id.sp_dayshourspicker);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        String[] spinnerItems = {getString(R.string.hours), getString(R.string.days)};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        sp_dayshours_picker.setAdapter(adapter);

        np_editMed_remainingDoses = findViewById(R.id.np_editMed_remainingDoses);
        np_editMed_remainingDoses.setMinValue(1);
        np_editMed_remainingDoses.setMaxValue(1000);
        np_editMed_remainingDoses.setWrapSelectorWheel(false);

        Intent intent = getIntent();
        medID = intent.getIntExtra("id", -1);
        mApp = (PillTimeApplication) this.getApplication();
        Med med = mApp.getMed(medID);

        tv_editMed_remainingReportedAt = findViewById(R.id.tv_editMed_remainingReportedAt);
        ll_editMed_trackRemainingDoses = findViewById(R.id.ll_editMed_trackRemainingDoses);
        switch_editMed_trackRemainingDoses = findViewById(R.id.switch_editMed_trackRemainingDoses);
        switch_editMed_trackRemainingDoses.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            ll_editMed_trackRemainingDoses.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            tv_editMed_remainingReportedAt.setVisibility(isChecked && med.getRemainingDosesReportedAt() > 0 ?
                    View.VISIBLE : View.GONE);
        });

        colors = getResources().getStringArray(R.array.color_options);

        if (med != null) {
            et_editMed_name.setText(med.getName());
            np_editMed_maxDose.setValue(med.getMaxDose());
            if (med.getDoseHours() % 24 == 0) {
                np_editMed_doseHoursDays.setValue(med.getDoseHours() / 24);
                sp_dayshours_picker.setSelection(1, false);
            } else {
                np_editMed_doseHoursDays.setValue(med.getDoseHours());
            }
            if (med.isRemainingDosesTracked()) {
                switch_editMed_trackRemainingDoses.setChecked(true);
                ll_editMed_trackRemainingDoses.setVisibility(View.VISIBLE);
                np_editMed_remainingDoses.setValue((int) med.getCurrentlyRemainingDoses());
                if (med.getRemainingDosesReportedAt() > 0) {
                    tv_editMed_remainingReportedAt.setVisibility(View.VISIBLE);
                    List<List<ParcelableSpan>> spansList = new ArrayList<>();
                    List<ParcelableSpan> spans = new ArrayList<>();
                    spans.add(new StyleSpan(Typeface.BOLD));
                    spansList.add(spans);
                    String unformatted = getString(R.string.remaining_doses_reported_at);
                    String timeSpanString = Utils.getRelativeTimeSpanString(this, med.getRemainingDosesReportedAt());
                    tv_editMed_remainingReportedAt.setText(Utils.styleString(unformatted, spansList, timeSpanString));
                } else {
                    tv_editMed_remainingReportedAt.setVisibility(View.GONE);
                }
            } else {
                switch_editMed_trackRemainingDoses.setChecked(false);
                ll_editMed_trackRemainingDoses.setVisibility(View.GONE);
                tv_editMed_remainingReportedAt.setVisibility(View.GONE);
            }
            setTitle(getString(R.string.edit_med_title, "\"" + med.getName() +"\""));
            selectedColor = med.getColor();
        } else {
            int rnd = new Random().nextInt(colors.length);
            selectedColor = colors[rnd];
            setTitle(getString(R.string.new_med));
        }

        colorButtons = new ArrayList<>();
        ImageButton imageButton;

        int drawableID;
        int dp48 = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                48,
                getResources().getDisplayMetrics()
        );
        int btnText = ThemeHelper.getThemeAttr(R.attr.buttonText, this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dp48, dp48);

        for (String colorName : colors) {
            imageButton = new ImageButton(this);
            imageButton.setId(View.generateViewId());
            imageButton.setLayoutParams(params);
            imageButton.setColorFilter(btnText);
            setColorBtnState(imageButton, selectedColor.equals(colorName));
            drawableID = Utils.getResourceIdentifier(this, "round_button_" + colorName, "drawable");
            imageButton.setBackgroundResource(drawableID);
            imageButton.setFocusable(true);
            colorButtons.add(imageButton);

            imageButton.setOnClickListener(view -> {
                for (ImageButton listButton : colorButtons) {
                    setColorBtnState(listButton, false);
                }
                setColorBtnState((ImageButton) view, true);
                selectedColor = colorName;
            });

            cl_editMed_parent.addView(imageButton);
            flow_editMed_colors.addView(imageButton);
        }

        btn_editMed_save.setOnClickListener(view -> {

            Med med1;
            String medName;
            int maxDose;
            int doseHours;

            try {
                medName = et_editMed_name.getText().toString();
                maxDose = np_editMed_maxDose.getValue();
                doseHours = np_editMed_doseHoursDays.getValue();
                boolean isDays = sp_dayshours_picker.getSelectedItemPosition() == 1;
                if (isDays) {
                    doseHours *= 24;
                }
                boolean remainingDosesTracked = switch_editMed_trackRemainingDoses.isChecked();
                int remainingDosesReported = -1;
                long remainingDosesReportedAt = -1;
                if (remainingDosesTracked) {
                    remainingDosesReported = np_editMed_remainingDoses.getValue();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    remainingDosesReportedAt = calendar.getTimeInMillis() / 1000;
                }
                med1 = new Med(medID, medName, maxDose, doseHours, selectedColor, remainingDosesTracked,
                        remainingDosesReported, remainingDosesReportedAt, EditMedActivity.this);
            } catch (Exception e) {
                Toast.makeText(EditMedActivity.this, "Error saving med: invalid data", Toast.LENGTH_SHORT).show();
                return;
            }

            if (medID > -1) {
                boolean edited = mApp.setMed(med1);
                if (!edited) return;
            } else {
                med1.setHasLoadedAllDoses(true);
                boolean added = mApp.addMed(med1);
                if (!added) return;
            }

            Toast.makeText(EditMedActivity.this, getString(R.string.toast_med_saved),
                    Toast.LENGTH_SHORT).show();

            EditMedActivity.this.finish();
        });
    }

    private void setColorBtnState(ImageButton btn, boolean isSelected) {
        if (isSelected) {
            btn.setImageResource(R.drawable.ic_baseline_check_24);
        } else {
            btn.setImageResource(0);
        }
    }
}