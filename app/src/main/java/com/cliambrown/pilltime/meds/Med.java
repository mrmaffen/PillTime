package com.cliambrown.pilltime.meds;

import android.content.Context;

import android.graphics.Typeface;
import android.text.ParcelableSpan;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import androidx.annotation.NonNull;

import com.cliambrown.pilltime.R;
import com.cliambrown.pilltime.utilities.Utils;
import com.cliambrown.pilltime.doses.Dose;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
public class Med {

    private int id;
    private String name;
    private int maxDose;
    private int doseHours;
    private String color;
    private boolean remainingDosesTracked;
    private double remainingDosesReported;
    private long remainingDosesReportedAt;
    private final List<Dose> doses = new ArrayList<>();
    private Context context;
    private boolean hasLoadedAllDoses;

    Dose latestDose;
    Dose nextExpiringDose;
    double activeDoseCount;
    SpannableString nextExpiringDoseExpiresInStr;
    String lastTakenAtStr;
    private double remainingDosesCurrently;

    public Med(int id, String name, int maxDose, int doseHours, String color, boolean remainingDosesTracked,
               double remainingDosesReported, long remainingDosesReportedAt, Context context) {
        this.id = id;
        this.name = name;
        this.maxDose = maxDose;
        this.doseHours = doseHours;
        this.color = color;
        this.remainingDosesTracked = remainingDosesTracked;
        this.remainingDosesReported = remainingDosesReported;
        this.remainingDosesReportedAt = remainingDosesReportedAt;
        this.context = context;
        this.hasLoadedAllDoses = false;
    }

    @NonNull
    @Override
    public String toString() {
        return "Med{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxDose=" + maxDose +
                ", doseHours=" + doseHours +
                ", color=" + color +
                ", remainingDosesTracked=" + remainingDosesTracked +
                ", remainingDosesReported=" + remainingDosesReported +
                ", remainingDosesReportedAt=" + remainingDosesReportedAt +
                ", doses=" + doses +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxDose() {
        return maxDose;
    }

    public void setMaxDose(int maxDose) {
        this.maxDose = maxDose;
    }

    public int getDoseHours() {
        return doseHours;
    }

    public void setDoseHours(int doseHours) {
        this.doseHours = doseHours;
    }

    public String getColor() {
        if (color == null) return "pink";
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return boolean indicating whether the user wants the remaining doses to be tracked
     */
    public boolean isRemainingDosesTracked() {
        return remainingDosesTracked;
    }

    /**
     * @param remainingDosesTracked boolean indicating whether the user wants the remaining doses to be tracked
     */
    public void setRemainingDosesTracked(boolean remainingDosesTracked) {
        this.remainingDosesTracked = remainingDosesTracked;
    }

    /**
     * @return timestamp in seconds since epoch, which tells us when the user has last reported their remaining doses
     */
    public long getRemainingDosesReportedAt() {
        return remainingDosesReportedAt;
    }

    /**
     * @param remainingDosesReportedAt timestamp in seconds since epoch, which tells us when the user has last reported
     *                                their remaining doses
     */
    public void setRemainingDosesReportedAt(long remainingDosesReportedAt) {
        this.remainingDosesReportedAt = remainingDosesReportedAt;
    }

    /**
     * @return the amount of remaining doses reported by the user
     */
    public double getRemainingDosesReported() {
        return remainingDosesReported;
    }

    /**
     * @param remainingDosesReported the amount of remaining doses reported by the user
     */
    public void setRemainingDosesReported(double remainingDosesReported) {
        this.remainingDosesReported = remainingDosesReported;
    }

    /**
     * @return number that has been decremented by however many doses have been taken since last report of remaining
     * doses. Indicates the actually still available amount of remaining doses.
     */
    public double getCurrentlyRemainingDoses() {
        return remainingDosesCurrently;
    }

    public String getMaxDoseInfo() {
        return Utils.buildMaxDosePerHourString(context, maxDose, doseHours);
    }

    public List<Dose> getDoses() {
        return doses;
    }

    public void addDose(Dose dose) {
        int position = -1;
        int doseID = dose.getId();
        long takenAt = dose.getTakenAt();
        for (int i=0; i<doses.size(); ++i) {
            Dose listDose = doses.get(i);
            long listTakenAt = listDose.getTakenAt();
            if (takenAt > listTakenAt) {
                position = i;
                break;
            }
            if (takenAt == listTakenAt && doseID > listDose.getId()) {
                position = i;
                break;
            }
        }
        if (position > -1) {
            doses.add(position, dose);
        } else {
            doses.add(dose);
        }
    }

    public void addDoseToEnd(Dose dose) {
        doses.add(dose);
    }

    public Dose getDoseById(int doseID) {
        for (Dose dose : doses) {
            if (dose.getId() == doseID) {
                return dose;
            }
        }
        return null;
    }

    public int setDose(Dose dose) {
        int position = -1;
        for (int i=0; i<doses.size(); ++i) {
            if (doses.get(i).getId() == dose.getId()) {
                position = i;
            }
        }
        if (position > -1) {
            doses.set(position, dose);
        }
        return position;
    }

    public int repositionDose(Dose dose, int currentPosition) {
        int newPosition = -1;
        int doseID = dose.getId();
        long takenAt = dose.getTakenAt();
        doses.remove(currentPosition);
        Dose listDose;
        int listDoseID;
        long takenDiff;
        for (int i=0; i<doses.size(); ++i) {
            listDose = doses.get(i);
            listDoseID = listDose.getId();
            takenDiff = listDose.getTakenAt() - takenAt;
            if (takenDiff < 0 || (takenDiff == 0 && doseID > listDoseID)) {
                newPosition = i;
                break;
            }
        }
        if (newPosition > -1) {
            doses.add(newPosition, dose);
        } else {
            doses.add(dose);
            newPosition = doses.size() - 1;
        }
        return newPosition;
    }

    public boolean sortBefore(Med compareMed) {
        long lastTakenAt = -1;
        int lastTakenId = -1;
        Dose latestDose = getLatestDose();
        if (latestDose != null) {
            lastTakenAt = latestDose.getTakenAt();
            lastTakenId = latestDose.getId();
        }
        long compareLastTakenAt = -1;
        int compareLastTakenId = -1;
        Dose compareLatestDose = compareMed.getLatestDose();
        if (compareLatestDose != null) {
            compareLastTakenAt = compareLatestDose.getTakenAt();
            compareLastTakenId = compareLatestDose.getId();
        }
        if (lastTakenAt > compareLastTakenAt) return true;
        if (lastTakenAt < compareLastTakenAt) return false;
        if (lastTakenId > compareLastTakenId) return true;
        if (lastTakenId < compareLastTakenId) return false;
        return (id > compareMed.getId());
    }

    public Dose getLatestDose() {
        return latestDose;
    }

    public double getActiveDoseCount() {
        return activeDoseCount;
    }

    public void setActiveDoseCount(double activeDoseCount) {
        this.activeDoseCount = activeDoseCount;
    }

    public SpannableString getNextExpiringDoseExpiresInStr() {
        return nextExpiringDoseExpiresInStr;
    }

    public String getLastTakenAtStr() {
        return lastTakenAtStr;
    }

    public String getRemainingDosesStr() {
        return (int) getCurrentlyRemainingDoses() + "/" + (int) getRemainingDosesReported();
    }

    public void updateTimes() {
        double doseCount = 0.0D;
        long now = System.currentTimeMillis() / 1000L;
        long doseDuration = doseHours * 60L * 60L;
        long earliestActiveTakenAt = now - doseDuration;
        Dose loopNextExpiringDose = null;
        Dose loopLatestDose = null;
        remainingDosesCurrently = remainingDosesReported;
        for (Dose dose : doses) {
            if (dose.getTakenAt() >= remainingDosesReportedAt && remainingDosesCurrently > 0)
                remainingDosesCurrently -= dose.getCount();
            if (dose.getTakenAt() > now) continue;
            if (loopLatestDose == null) loopLatestDose = dose;
            if (dose.getTakenAt() > earliestActiveTakenAt) {
                doseCount += dose.getCount();
                loopNextExpiringDose = dose;
            } else {
                break;
            }
        }
        activeDoseCount = doseCount;
        latestDose = loopLatestDose;
        nextExpiringDose = loopNextExpiringDose;

        if (nextExpiringDose != null) {
            double nextExpiringDoseCount = nextExpiringDose.getCount();
            long expiresAtUnix = nextExpiringDose.getTakenAt() + doseDuration;
            String timeAgo = Utils.getRelativeTimeSpanString(context, expiresAtUnix);
            String countStr = Utils.getStrFromDbl(nextExpiringDoseCount);
            String unformatted = context.getResources().getQuantityString(R.plurals.expires,
                    (int) nextExpiringDoseCount);
            List<List<ParcelableSpan>> spansList = new ArrayList<>();
            List<ParcelableSpan> spans = new ArrayList<>();
            spans.add(new StyleSpan(Typeface.BOLD_ITALIC));
            spansList.add(spans);
            spans = new ArrayList<>();
            spans.add(new StyleSpan(Typeface.BOLD));
            spansList.add(spans);
            nextExpiringDoseExpiresInStr = Utils.styleString(unformatted, spansList, countStr,
                    timeAgo + " (" + Utils.simpleFutureTime(context, expiresAtUnix) + ")");
        }

        if (latestDose == null) {
            lastTakenAtStr = context.getString(R.string.never);
        } else {
            lastTakenAtStr = Utils.getRelativeTimeSpanString(context, latestDose.getTakenAt());
        }
    }

    public boolean hasLoadedAllDoses() {
        return hasLoadedAllDoses;
    }

    public void setHasLoadedAllDoses(boolean hasLoadedAllDoses) {
        this.hasLoadedAllDoses = hasLoadedAllDoses;
    }

    public void removeDoseAndOlder(Dose dose) {
        int position = -1;
        for (int i=0; i<doses.size(); ++i) {
            Dose listDose = doses.get(i);
            if (listDose.getId() == dose.getId()) {
                position = i;
                break;
            }
        }
        if (position == -1) return;
        while (doses.size() > position) {
            doses.remove(doses.size() - 1);
        }
        this.hasLoadedAllDoses = true;
    }
}
