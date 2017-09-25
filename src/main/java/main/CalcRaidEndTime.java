package main;

import java.time.LocalTime;

public class CalcRaidEndTime {
    public static void main(String[] args) {
        System.out.println(LocalTime.of(15, 6) // Time of egg hatch report
                .plusMinutes(7) // Until egg hatches
                .plusMinutes(60)); // 1 hour raid time after egg hatches
    }
}
