package BabysitterCalculator;

import java.text.NumberFormat;
import java.util.HashMap;

import static BabysitterCalculator.Keys.*;

/**
 * Created by jacobmenke on 4/27/17.
 */


public class BabySitter {
    private String name;
    private BabysittingJob babysittingJob;

    public BabySitter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BabysittingJob getBabysittingJob() {
        return babysittingJob;
    }

    public String proposeJob(BabysittingJob babysittingJob, HashMap<String, String> timesData) {

        String reason = "";

        if (babysittingJob.getStartingTime() >= 17){

            if (babysittingJob.getEndingTime() <= 4) {

                babysittingJob.compensateFor24Hours();

                if (babysittingJob.getStartingTime()  <= babysittingJob.getBedTime() && babysittingJob.getBedTime() <= babysittingJob.getEndingTime()){
                    this.babysittingJob = babysittingJob;
                    return "yes";
                } else {
                    return "an inappropriate bedtime at " + timesData.get(BED_TIME) + ".";
                }


            } else {
                return "the late ending time at " + timesData.get(ENDING_TIME) + ".";

            }


        } else {
            reason += "the early starting time at " + timesData.get(STARTING_TIME);
            if (babysittingJob.getEndingTime() <= 4){
                reason += ".";

            } else {
                reason += ", not to mention the late ending hours at " + timesData.get(ENDING_TIME) + ".";

            }
        }

        return reason;


    }

    public void says(String message) {

        System.out.println(name + " says, \"" + message + "\"");
    }

    public HashMap<String, Integer> calculateHours(BabysittingJob babysittingJob) {



        if (babysittingJob != null) {


            Double hoursStartingToBedtime = babysittingJob.getBedTime() - babysittingJob.getStartingTime();


            //midnight is at 24 hours
            Double hoursBedtimeToMidnight = 24 - babysittingJob.getBedTime();

            if (hoursBedtimeToMidnight < 0) hoursBedtimeToMidnight = 0.0;

            //dont need to subtract because calculated from 0 or midnight
            Double hoursMidnighttoEnd = babysittingJob.getEndingTime();

            if (hoursMidnighttoEnd >= 24){
                hoursMidnighttoEnd -=24;
            }


            Integer hoursStartingToBedtimeFloored = removeFractional(hoursStartingToBedtime);
            Integer hoursBedtimeToMidnightFloored = removeFractional(hoursBedtimeToMidnight);
            Integer hoursMidnighttoEndFloored = removeFractional(hoursMidnighttoEnd);


            Integer hours = removeFractional(hoursStartingToBedtime) + removeFractional(hoursBedtimeToMidnight) + removeFractional(hoursMidnighttoEnd);


            HashMap<String, Integer> hourMap = new HashMap<>();
            hourMap.put("hoursStartingToBedtimeFloored", hoursStartingToBedtimeFloored);
            hourMap.put("hoursBedtimeToMidnightFloored", hoursBedtimeToMidnightFloored);
            hourMap.put("hoursMidnighttoEndFloored", hoursMidnighttoEndFloored);
            hourMap.put("totalHours", hours);

            babysittingJob.setHoursMap(hourMap);

            return hourMap;
        }
        return null;
    }

    public String calculatePay(BabysittingJob babysittingJob) {

        if (babysittingJob != null) {

            HashMap<String, Integer> hoursMap = babysittingJob.getHoursMap();

            Double pay = (double) calculatePay(hoursMap.get("hoursStartingToBedtimeFloored"), hoursMap.get("hoursBedtimeToMidnightFloored"), hoursMap.get("hoursMidnighttoEndFloored"));

            //no fractional hours
            return formatMoney(pay);
        }

        return null;
    }

    private long calculatePay(long hoursStartingToBedtimeLong, long hoursBedtimeToMidnightLong, long hoursMidnighttoEndLong) {
        return hoursStartingToBedtimeLong * HourlyRates.HOURLY_RATE_FROM_START_TO_BEDTIME
                + hoursBedtimeToMidnightLong * HourlyRates.HOURLY_RATE_FROM_BEDTIME_TO_MIDNIGHT
                + hoursMidnighttoEndLong * HourlyRates.HOURLY_RATE_FROM_MIDNIGHT_TO_END;
    }

    private Integer removeFractional(Double hoursStartingToBedtime) {
        return (int) Math.floor(hoursStartingToBedtime);

    }

    public String formatMoney(Double money) {

        return NumberFormat.getCurrencyInstance().format(money);
    }
}
