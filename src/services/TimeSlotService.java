//package services;
//
//import models.TimeSlot;
//
//import java.time.DayOfWeek;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class TimeSlotService {
//
//    // Creăm sloturi generale pentru fiecare zi a săptămânii
//    public static List<TimeSlot> createGeneralTimeSlots() {
//        List<TimeSlot> slots = new ArrayList<>();
//        LocalTime start = LocalTime.of(6, 0);
//        LocalTime end = LocalTime.of(23, 0);
//
//        for (DayOfWeek day : DayOfWeek.values()) {
//            for (LocalTime hour = start; hour.isBefore(end); hour = hour.plusHours(1)) {
//                slots.add(new TimeSlot(hour, hour.plusHours(1), day, null));
//            }
//        }
//
//        return slots;
//    }
//
//}
package services;

import dao.TimeSlotDAO;

public class TimeSlotService {

    private final TimeSlotDAO timeSlotDAO;

    public TimeSlotService(TimeSlotDAO timeSlotDAO) {
        this.timeSlotDAO = timeSlotDAO;
    }

}

