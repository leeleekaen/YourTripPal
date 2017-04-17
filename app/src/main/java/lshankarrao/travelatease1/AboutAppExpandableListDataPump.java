package lshankarrao.travelatease1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class AboutAppExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> tripInfoOnePlace = new ArrayList<String>();
        tripInfoOnePlace.add("Add Trip.");
        tripInfoOnePlace.add("Add Reservations(Transport, Stay and Events) pertaining to the trip here.");
        tripInfoOnePlace.add("Helps you keep all info about Trip handy.");

        List<String> reminderNotifications = new ArrayList<String>();
        reminderNotifications.add("You can always set reminders for anything you wish using the “Set Reminder” option.");
        reminderNotifications.add("Other than that you will receive timely planning reminders for your upcoming trip.");
        reminderNotifications.add("And before 2 days of the start of the trip, you will also get a reminder to share your itinerary with your friends.");
        reminderNotifications.add("You can disable/enable the auto-reminders in \"Settings\".");


        List<String> packingHelp = new ArrayList<String>();
        packingHelp.add("Create and manage Packing Lists.");
        packingHelp.add("Choose from many packing templates(ex: Personal Care, Outdoor Activities packing templates) to create the list.");
        packingHelp.add("Set Reminder for packing.");

        List<String> sayHello = new ArrayList<String>();
        sayHello.add("When you reach your trip destination, you will get a reminder notification to say hello to your friends and family to let them know you have reached safely. " +
                "You can say hello with a pic and a message in one click.");
        sayHello.add("You can disable/enable this feature in \"Settings\".");


        List<String> tripTodo = new ArrayList<String>();
        tripTodo.add("Create and manage trip To-do activity items list for your trip.");
        tripTodo.add("Set Reminders for To do activities.");
        tripTodo.add("If trip planning is done you will have an option of sharing the trip itinerary with your friends.");

        List<String> bucketList = new ArrayList<String>();
        bucketList.add("Create and maintain bucket list of places to visit and the best times to visit.");
        bucketList.add("Status of completion of each trip in the bucket list will be displayed.");
        bucketList.add("Depending on the status of the trip, by clicking on “Ongoing” or “Upcoming” you can view the itinerary of the trip and by clicking on “Start Planning” you can  start planning for your favourite vacation destination right away.");


        List<String> deletion = new ArrayList<String>();
        deletion.add("For Trip/Reservation, go to Manage Trip/Manage Reservation Page respectively. You can find the delete icon(Reservation)/click the 3 dots in the top right corner to find the delete option(Trip).");
        deletion.add("Packing List Items, Contact Entries and To-do Activities can be deleted by just swiping(from right to left or left to right). Long press on the item to rearrange its position. ");

        expandableListDetail.put("Trip To-do", tripTodo);
        expandableListDetail.put("Reminders", reminderNotifications);
        expandableListDetail.put("Bucket List", bucketList);
        expandableListDetail.put("All your Trip Info in one Place", tripInfoOnePlace);
        expandableListDetail.put("Say Hello", sayHello);
        expandableListDetail.put("Packing", packingHelp);
        expandableListDetail.put("Deletion", deletion);
        return expandableListDetail;
    }
}