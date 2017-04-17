package lshankarrao.travelatease1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

public class TripDbHelper extends SQLiteOpenHelper {

    static private final int VERSION = 1;
    static private final String DB_NAME = "YourTripDatabase.db";//"TripDatabase.db";

    Context context;

    public TripDbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
        initHotelSubKinds();
        initEventResSubKinds();
        initTransportSubKinds();
    }

    static private final String SQL_CREATE_TRIP_TABLE =
            "CREATE TABLE tripInfo (" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  title TEXT," +
                    "  placeName TEXT," +
                    "  placeAddress TEXT," +
                    "  stTimeMillis INTEGER, " +
                    "  endTimeMillis INTEGER, " +
                    "  themes TEXT," +
                    "  notes TEXT," +
                    "  planningDone INTEGER);";

    static private final String SQL_CREATE_RESERVATIONS_TABLE =
            "CREATE TABLE reservationInfo (" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  kind TEXT," +
                    "  titleOrName TEXT," +
                    "  stTimeMillis INTEGER," +
                    "  startPlaceName TEXT," +
                    "  startPlaceAddress TEXT," +
                    "  endTimeMillis INTEGER," +
                    "  endPlaceName TEXT," +
                    "  endPlaceAddress TEXT," +
                    "  confNo TEXT," +
                    "  notes TEXT," +
                    "  attachmentFilePath TEXT," +
                    "  contacts TEXT," +
                    "  tripId INTEGER);";

    static private final String SQL_CREATE_CONTACTS_TABLE =
            "CREATE TABLE contactInfo (" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  name TEXT," +
                    "  email TEXT," +
                    "  favourite INTEGER);";

    static private final String SQL_CREATE_THINGS_TO_PACK_TABLE =
            "CREATE TABLE thingsToPack (" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  item TEXT," +
                    "  category TEXT);";


    static private final String SQL_CREATE_TRIP_SPECIFIC_PACKING_LISTS_TABLE =
            "CREATE TABLE tripSpecificPackingLists (" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  itemsList TEXT," +
                    "  tripId INTEGER);";

    static private final String SQL_CREATE_BUCKET_lIST_TABLE =
            "CREATE TABLE bucketListInfo (" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  theme TEXT," +
                    "  placeName TEXT," +
                    "  placeAddress TEXT," +
                    "  startMonth TEXT," +
                    "  endMonth TEXT," +
                    "  tripId INTEGER," +
                    "  checked INTEGER);";

    static private final String SQL_CREATE_TODO_LISTS_TABLE =
            "CREATE TABLE todoList (" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  itemsList TEXT," +
                    "  tripId INTEGER);";

    static private final String SQL_CREATE_SETTINGS_TABLE =
            "CREATE TABLE settings (" +
                    "  disablePlanRem INTEGER," +
                    "  disableLocRem INTEGER);";


    static private final String k_id = "_id";
    static private final String kKind = "kind";
    static private final String kTitleOrName = "titleOrName";
    static private final String kStTimeMillis = "stTimeMillis";
    static private final String kStartPlaceName = "startPlaceName";
    static private final String kStartPlaceAddress = "startPlaceAddress";
    static private final String kEndTimeMillis = "endTimeMillis";
    static private final String kEndPlaceName = "endPlaceName";
    static private final String kEndPlaceAddress = "endPlaceAddress";
    static private final String kConfNo = "confNo";
    static private final String kNotes = "notes";
    static private final String kAttachmentFilePath = "attachmentFilePath";
    static private final String kTripId = "tripId";

    static private final String SQL_DROP_TABLE = "DROP TABLE tripInfo";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TripDbHelper onCreate()","called"+"");
        db.execSQL(SQL_CREATE_TRIP_TABLE);
        db.execSQL(SQL_CREATE_RESERVATIONS_TABLE);
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
        db.execSQL(SQL_CREATE_THINGS_TO_PACK_TABLE);
        db.execSQL(SQL_CREATE_TRIP_SPECIFIC_PACKING_LISTS_TABLE);
        db.execSQL(SQL_CREATE_BUCKET_lIST_TABLE);
        db.execSQL(SQL_CREATE_TODO_LISTS_TABLE);
        db.execSQL(SQL_CREATE_SETTINGS_TABLE);
        setInitialSettings(db);
        updateThingsToPack(db);
    }

    private void setInitialSettings(SQLiteDatabase db) {
        SettingsInfo settings = new SettingsInfo(0, 0);
        addInitialSettings(settings, db);
    }

    public void addInitialSettings(SettingsInfo settingsInfo, SQLiteDatabase db) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("disablePlanRem", settingsInfo.disablePlanRem);
        contentValues.put("disableLocRem", settingsInfo.disableLocRem);
        db.insert("settings", null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        db.execSQL(SQL_CREATE_TRIP_TABLE);
        db.execSQL(SQL_CREATE_RESERVATIONS_TABLE);
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
        db.execSQL(SQL_CREATE_THINGS_TO_PACK_TABLE);
        db.execSQL(SQL_CREATE_TRIP_SPECIFIC_PACKING_LISTS_TABLE);
        db.execSQL(SQL_CREATE_BUCKET_lIST_TABLE);
        db.execSQL(SQL_CREATE_TODO_LISTS_TABLE);
        db.execSQL(SQL_CREATE_SETTINGS_TABLE);
    }

    public void printAllThingsToPack() {
        Cursor c = fetchAllThingsToPack();
        List<StdPackingItemsInfo> allPackingItems = getPackingItemsForCursor(c);
        for (StdPackingItemsInfo p : allPackingItems) {
            Log.d(p.id + " ", p.category + " --> " + p.item);
        }
    }

    public List<StdPackingItemsInfo> getPackingItemsForCursor(Cursor c) {

        if (c == null || c.getCount() < 1) {
            Log.d("cursor null", "packing tripDb");
            return null;
        }
        List<StdPackingItemsInfo> allPackingItems = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            StdPackingItemsInfo packingItem = new StdPackingItemsInfo(c);
            allPackingItems.add(packingItem);
            c.moveToNext();
        }
        return allPackingItems;
    }

    private void updateThingsToPack(SQLiteDatabase db) {
        String[] labels = {"Party", "Camping", "Medical Kit", "Medical Kit", "Medical Kit", "Medical Kit", "Electronics", "Child Care", "Child Care", "Child Care", "Medical Kit", "Electronics", "Accessories", "Child Care", "Outdoor Activities", "Medical Kit", "Makeup Kit", "Indoor Activities", "Camping", "Party", "Electronics", "Outdoor Activities", "Electronics", "Electronics", "Electronics", "Electronics", "Electronics", "Camping", "Accessories", "Electronics", "Indoor Activities", "Electronics", "Electronics", "Camping", "Child Care", "Child Care", "Party", "Clothes", "Camping", "Personal Care", "Outdoor Activities", "Electronics", "Personal Care", "Camping", "Camping", "Medical Kit", "Water Activities", "Water Activities", "Business/Educational", "Camping", "Outdoor Activities", "Outdoor Activities", "Personal Care", "Personal Care", "Child Care", "Medical Kit", "Business/Educational", "Party", "Clothes", "Camping", "Indoor Activities", "Documents", "Camping", "Electronics", "Electronics", "Personal Care", "Electronics", "Outdoor Activities", "Outdoor Activities", "Documents", "Party", "Water Activities", "Water Activities", "Makeup Kit", "Makeup Kit", "Makeup Kit", "Snow Activities", "Personal Care", "Medical Kit", "Business/Educational", "Camping", "Electronics", "Electronics", "Camping", "Documents", "Camping", "Indoor Activities", "Camping", "Documents", "Business/Educational", "Business/Educational", "Business/Educational", "Business/Educational", "Makeup Kit", "Camping", "Camping", "Camping", "Indoor Activities", "Party", "Clothes", "Outdoor Activities", "Electronics", "Electronics", "Camping", "Accessories", "Accessories", "Personal Care", "Personal Care", "Accessories", "Personal Care", "Personal Care", "Accessories", "Personal Care", "Accessories", "Snow Activities", "Camping", "Electronics", "Electronics", "Party", "Snow Activities", "Outdoor Activities", "Documents", "Camping", "Outdoor Activities", "Documents", "Clothes", "Clothes", "Accessories", "Indoor Activities", "Clothes", "Party", "Camping", "Camping", "Business/Educational", "Business/Educational", "Electronics", "Business/Educational", "Clothes", "Personal Care", "Makeup Kit", "Makeup Kit", "Documents", "Party", "Makeup Kit", "Makeup Kit", "Makeup Kit", "Camping", "Outdoor Activities", "Camping", "Makeup Kit", "Camping", "Makeup Kit", "Snow Activities", "Personal Care", "Medical Kit", "Personal Care", "Indoor Activities", "Electronics", "Camping", "Camping", "Indoor Activities", "Makeup Kit", "Makeup Kit", "Makeup Kit", "Makeup Kit", "Snow Activities", "Clothes", "Business/Educational", "Child Care", "Camping", "Medical Kit", "Clothes", "Clothes", "Camping", "Party", "Documents", "Business/Educational", "Personal Care", "Documents", "Camping", "Camping", "Indoor Activities", "Child Care", "Camping", "Camping", "Electronics", "Camping", "Camping", "Medical Kit", "Indoor Activities", "Camping", "Water Activities", "Water Activities", "Personal Care", "Electronics", "Camping", "Camping", "Makeup Kit", "Accessories", "Clothes", "Accessories", "Personal Care", "Personal Care", "Personal Care", "Clothes", "Clothes", "Clothes", "Personal Care", "Camping", "Camping", "Outdoor Activities", "Snow Activities", "Snow Activities", "Snow Activities", "Personal Care", "Clothes", "Camping", "Indoor Activities", "Party", "Camping", "Business/Educational", "Accessories", "Personal Care", "Snow Activities", "Water Activities", "Water Activities", "Water Activities", "Camping", "Electronics", "Clothes", "Snow Activities", "Snow Activities", "Documents", "Accessories", "Business/Educational", "Personal Care", "Personal Care", "Personal Care", "Personal Care", "Clothes", "Personal Care", "Child Care", "Camping", "Personal Care", "Outdoor Activities", "Documents", "Party", "Makeup Kit", "Outdoor Activities", "Clothes", "Camping", "Accessories", "Electronics", "Electronics", "Electronics", "Electronics", "Electronics", "Personal Care", "Accessories", "Outdoor Activities", "Water Activities", "Water Activities", "Camping", "Camping", "Snow Activities", "Water Activities"};
        String[] items = {"accessories", "air mattresses", "allergy meds", "antacid meds", "antibacterial wipes", "antibiotic cream", "aux cable", "baby bottles", "baby carriage", "baby food", "bandages", "batteries (AAA,AA)", "belt", "bibs", "binoculars", "birth control", "blush", "board games", "bottle opener/corkscrew", "bow tie", "camera", "camera and accessories", "camera bag", "camera batteries, charger & cables", "camera lenses", "camera memory cards", "camera tripod", "can opener", "cap", "car charger", "card games", "cell phone", "cell phone chargers", "charcoal (with fire starter)", "children's books", "clothes", "clutches", "coat (seasonal)", "collapsible water container(s)", "comb (brush)", "compass", "computer charger", "conditioner", "cook pots", "coolers", "cotton", "covers for wet clothes", "crocs (waterproof shoes)", "cufflinks", "cutting board or cutting surface", "cycle", "cycle accessories", "dental floss", "deodorant", "diapers", "dietary supplements and Vitamins", "documents", "dress shoes", "dresses", "drinking water suppy ", "drinks", "driver's license/other photo ID", "dutch oven", "DVDs (CDs)", "ear phones", "ear plugs", "ebook reader (Kindle)", "energy bars", "energy drink", "entry visa (or required paperwork)", "evening gown", "extra clothing", "extra towels", "eye liner", "eye shadow", "eyebrow pencil", "face mask", "facial cleanser and mask", "feminine hygiene (sanitary napkins)", "file", "firewood (if allowed, plus saw or axe)", "flash drives (USB)", "flashlight (small LED)", "flashlights (with extra batteries, bulbs)", "flight confirmation", "folding chairs", "food", "food-storage containers", "foreign currency and credit card", "formal pants", "formal shirt", "formal shoes", "formal skirt", "foundation", "frying pan", "fuel", "fuel bottle(s) with fuel funnel", "game units", "gift cards/ presents", "gloves", "gps", "GPS charger", "GPS unit", "grill rack", "hair bun", "hair clips", "hair dryer", "hair oil", "hair pins", "hair remover", "hair styling gel/ spray", "hand bag/purse", "hand sanitizer", "hat (wide brim, sun)", "headband", "headlamps (with extra batteries)", "headphones", "headset (Bluetooth for handsfree)", "heels", "helmet", "hiking shoes", "hotel confirmation", "ice", "insect repellent", "international health insurance", "jacket", "jeans", "jewelry (rings, earring, necklace, bracelet)", "karaoke units", "kerchief", "knitted gloves", "lantern fuel or batteries", "lanterns (with mantles, if needed)", "laptop", "laptop bag", "laptop computer", "laser pointer", "lingerie", "lip balm", "lip liner", "lipstick/lip gloss", "list of key phone numbers", "make-up kit", "makeup brushes", "makeup remover", "makeup sponges", "mallet or hammer", "maps", "marshmallow/wiener roasting sticks", "mascara", "matches/lighter", "mirror (small hand size)", "mittens", "moisturizer (face, body)", "motion sickness medications", "mouthwash", "movies collection", "MP3 Player", "mugs/cups", "multi-tool or knife", "music units", "nail clippers", "nail file", "nail polish", "nail polish remover", "neck warmer", "nightgown", "notebook", "pacifiers", "pad/mattress repair kit", "pain reliever medications", "pajamas", "pants", "paring knife", "party wear", "passport", "pen", "perfume", "photocopy of first page of passport", "pillows", "plates, bowls, mixing bowls", "pleasure readings", "portable changing pad", "portable coffee/espresso maker", "portable or standing camp sink", "portable solar charger", "pot grabber", "pot scrubber/sponge(s)", "prescription medications", "projector set up", "pump for air mattresses", "quick dry pants", "rain jacket (poncho)", "razor (electric & charger)", "rechargeable battery charger", "recipes", "resealable storage bags", "rubber bands", "safety pins", "sandals", "scarf", "scissors", "shampoo", "shaving cream", "shirts", "shoes", "shorts", "sleep mask", "sleeping bags (with optional liners)", "sleeping pads", "snacks", "snow boots", "snow cap", "snowboard gloves", "soap (body wash)", "socks", "spatula", "stage, mic and speaker set up", "stockings", "stove", "suit", "sunglasses", "sunscreen", "sweater", "swim coverup (sarong)", "swim goggles", "swim suit", "tablecloth and clips (or tape)", "tablet device", "tees", "thermals", "thick jacket", "tickets/ passes", "tie", "ties", "tissues (facial, packs)", "toilet paper", "toothbrush", "toothpaste", "tops", "towel", "toys", "trash bags", "travel pillow", "trekking poles", "trip itinerary", "tuxedo", "tweezers", "umbrella", "underwear", "utensils", "vallet", "video camera", "video camera bag", "video camera batteries, charger & cables", "video camera memory sticks", "wall plug adapters", "washcloth", "watch", "water bottles", "waterproof sunscreen", "waterproof watch", "whisk", "windscreen", "woolen socks", "ziplock covers for securing electronics"};
        for (int i = 0; i < items.length; ++i) {
            StdPackingItemsInfo stdPackingItemsInfo = new StdPackingItemsInfo(items[i], labels[i], 1);
            long id = addPackingItemInfo(stdPackingItemsInfo, db);
            Log.d("item, label", items[i] + ", " + labels[i]);
            Log.d("packingTBL entry id:", id + "");
        }
    }

    public Cursor fetchAllThingsToPack() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM thingsToPack;", null);
    }

    public Cursor getCursorForPackingItemsCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM thingsToPack WHERE category='" + category + "';", null);
    }


    public int getMaxRecID() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(_id) FROM contact;", null);

        if (cursor.getCount() == 0) {
            return 0;
        } else {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
    }

    public Cursor fetchAllTrips() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tripInfo ORDER BY stTimeMillis;", null);
    }

    public Cursor fetchAllReservationsForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM reservationInfo WHERE tripId=" + tripId + " ORDER BY stTimeMillis;", null);
    }

    public Cursor fetchUpcomingTrips() {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM tripInfo WHERE stTimeMillis>=" + currentTime + " ORDER BY stTimeMillis;";
        return db.rawQuery(q, null);
    }

    public List<TripInfo> fetchAllTripsPlaceName() {
        Cursor c = fetchAllTrips();
        Log.d("no. of trips= ", c.getCount() + "");
        List<TripInfo> tripInfos = new ArrayList<>();
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "fetchAllTripsPlaceName");
            return null;
        }

        c.moveToFirst();
        while (!c.isAfterLast()) {
            int id = (c.getInt(c.getColumnIndex("_id")));
            TripInfo tripInfo = getTripInfo(id);
            tripInfos.add(tripInfo);
            c.moveToNext();
        }

        return tripInfos;
    }

    public Cursor fetchPastTrips() {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM tripInfo WHERE endTimeMillis<" + currentTime + " ORDER BY stTimeMillis;";
        return db.rawQuery(q, null);
    }

    public long addTripInfo(TripInfo ci) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", ci.title);
        contentValues.put("placeName", ci.placeName);
        contentValues.put("placeAddress", ci.placeAddress);
        contentValues.put("stTimeMillis", ci.stTimeMillis);
        contentValues.put("endTimeMillis", ci.endTimeMillis);
        contentValues.put("themes", ci.themes);
        contentValues.put("notes", ci.notes);
        return db.insert("tripInfo", null, contentValues);
    }


    public long addReservationsInfo(ReservationsInfo reservationsInfo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("kind", reservationsInfo.kind);
        if (reservationsInfo.titleOrName != null && !reservationsInfo.titleOrName.isEmpty())
            contentValues.put("titleOrName", reservationsInfo.titleOrName);
        contentValues.put("stTimeMillis", reservationsInfo.stTimeMillis);
        contentValues.put("startPlaceName", reservationsInfo.startPlaceName);
        contentValues.put("startPlaceAddress", reservationsInfo.startPlaceAddress);
        contentValues.put("endTimeMillis", reservationsInfo.endTimeMillis);
        if (reservationsInfo.endPlaceName != null && !reservationsInfo.endPlaceName.isEmpty())
            contentValues.put("endPlaceName", reservationsInfo.endPlaceName);
        if (reservationsInfo.endPlaceAddress != null && !reservationsInfo.endPlaceAddress.isEmpty())
            contentValues.put("endPlaceAddress", reservationsInfo.endPlaceAddress);
        if (reservationsInfo.confNo != null && !reservationsInfo.confNo.isEmpty())
            contentValues.put("confNo", reservationsInfo.confNo);
        if (reservationsInfo.notes != null && !reservationsInfo.notes.isEmpty())
            contentValues.put("notes", reservationsInfo.notes);
        if (reservationsInfo.attachmentFilePath != null && !reservationsInfo.attachmentFilePath.isEmpty())
            contentValues.put("attachmentFilePath", reservationsInfo.attachmentFilePath);
        contentValues.put("tripId", reservationsInfo.tripId);
        return db.insert("reservationInfo", null, contentValues);
    }

    public void delete(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("tripInfo", "_id=?", new String[]{String.valueOf(id)});

        /*
        String SQL_DELETE="DELETE FROM contact WHERE _id=" + id + ";";
        db.execSQL(SQL_DELETE);
         */
    }

    public void logAllTrips() {
        Cursor c = fetchAllTrips();
        Log.d("no. of trips= ", c.getCount() + "");
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Log.d("", c.getString(c.getColumnIndex("title")));
            Log.d("", c.getString(c.getColumnIndex("city")));
            Log.d("", c.getString(c.getColumnIndex("state")));
            Log.d("", c.getString(c.getColumnIndex("country")));
            Log.d("", c.getString(c.getColumnIndex("startDate")));
            Log.d("", c.getString(c.getColumnIndex("endDate")));
            Log.d("", c.getString(c.getColumnIndex("startTime")));
            Log.d("", c.getString(c.getColumnIndex("endTime")));
            Log.d("", c.getString(c.getColumnIndex("notes")));
            c.moveToNext();
        }
    }

    public void logAllReservations(int tripId) {
        Cursor c = fetchAllReservationsForTrip(tripId);
        List<ReservationsInfo> infos = getResInfoListForQuery(c);
        int x = 1;
        for (ReservationsInfo info : infos) {
            Log.d("Reservation # ", x + "");
            Log.d("id ", info.id + "");
            Log.d("kind ", info.kind + "");
            Log.d("titleOrName ", info.titleOrName + "");
            Log.d("stTimeMillis ", info.stTimeMillis + "");
            Log.d("startPlaceName ", info.startPlaceName + "");
            Log.d("startPlaceAddress ", info.startPlaceAddress + "");
            Log.d("endTimeMillis ", info.endTimeMillis + "");
            Log.d("endPlaceName ", info.endPlaceName + "");
            Log.d("endPlaceAddress ", info.endPlaceAddress + "");
            Log.d("confNo ", info.confNo + "");
            Log.d("notes ", info.notes + "");
            Log.d("attachmentFilePath ", info.attachmentFilePath + "");
            Log.d("tripId ", info.tripId + "");
            x++;
        }
    }


    public TripInfo getTripInfo(int id) {
        Log.d("id", id + "");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tripInfo WHERE _id=" + id + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik 1", "agide");
            return new TripInfo();
        }
        c.moveToFirst();
        Log.d("count = ", c.getCount() + "");
        Log.d("index = ", c.getColumnIndex("title") + "");


        Log.d("title TripDBhelper", c.getInt(c.getColumnIndex("_id")) + "");

        TripInfo tripInfo = new TripInfo(
                c.getString(c.getColumnIndex("title")),
                c.getString(c.getColumnIndex("placeName")),
                c.getString(c.getColumnIndex("placeAddress")),
                c.getLong(c.getColumnIndex("stTimeMillis")),
                c.getLong(c.getColumnIndex("endTimeMillis")),
                c.getString(c.getColumnIndex("themes")),
                c.getString(c.getColumnIndex("notes")),
                c.getInt(c.getColumnIndex("planningDone"))
        );


        return tripInfo;
    }


    public Cursor getCurrentTrip() {
        Calendar current = Calendar.getInstance();
        long currentTimeInMillis = current.getTimeInMillis();

        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM tripInfo WHERE stTimeMillis<=" + currentTimeInMillis + " AND endTimeMillis>=" + currentTimeInMillis + " ORDER BY endTimeMillis;";
        return db.rawQuery(q, null);
    }

    public List<ReservationsInfo> getHotelsInfo(int tripId) {
        Log.d("id", tripId + "");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM reservationInfo WHERE tripId=" + tripId + " AND kind=" + "hotel_start;", null);
        if (c == null || c.getCount() < 1) {
            Log.d("problem", "checkout");
            return null;
        }
        List<ReservationsInfo> infos = getResInfoListForQuery(c);
        return infos;
    }

    public Cursor fetchAllHotelsForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM reservationInfo WHERE tripId=" + tripId + " AND kind=hotel_start;", null);
    }

    public Cursor fetchAllTransportForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM reservationInfo WHERE tripId=" + tripId + " " +
                "AND kind=car_start AND kind=flight_start AND kind=bus_start AND " +
                "kind=train_start;", null);
    }

    public Cursor fetchAllEventResForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM reservationInfo WHERE tripId=" + tripId + ";", null);
    }



    public void updateTripInfoSanitized(TripInfo info, int tripId){
        SQLiteDatabase db = this.getWritableDatabase();
        String updateqry = "UPDATE tripInfo SET title=?, placeName=?, placeAddress=?, stTimeMillis=?," +
                " endTimeMillis=?, themes=?, notes=?, planningDone=? WHERE _id=?;";

        SQLiteStatement statement = db.compileStatement(updateqry);
        statement.bindString(1, info.title);
        statement.bindString(2, info.placeName);
        statement.bindString(3, info.placeAddress);
        statement.bindLong(4, info.stTimeMillis);
        statement.bindLong(5, info.endTimeMillis);
        statement.bindString(6, info.themes);
        statement.bindString(7, info.notes);
        statement.bindLong(8,info.planningDone );
        statement.bindLong(9, tripId);

        Log.d("statement", statement.toString()+"");
        int numberOfRowsAffected = statement.executeUpdateDelete();



    }

    public void deleteEntry(String table, int id) {
        Log.d("deleteFn"+table, id+"");
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = "DELETE FROM " + table + " WHERE _id= " + id + ";";
        if (table.equals("reservationInfo")) {
            ReservationsInfo info = getReservation(id);
            if(info != null) {
                if ((info.endPlaceName == null || info.endPlaceName.isEmpty()) &&
                        !info.kind.split("_")[0].toUpperCase().equals("HOTEL")) {
                    //do nothing
                } else {
                    String deleteMatchingRecord = "DELETE FROM " + table + " WHERE stTimeMillis= " + info.endTimeMillis + ";";
                    db.execSQL(deleteMatchingRecord);
                }
            }
        }

        db.execSQL(delete);
    }

    public List<ReservationsInfo> getAllReservationsInfo(int id) {

        Log.d("id", id + "");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM reservationInfo WHERE tripId=" + id + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "aagide");
            return null;
        }

        List<ReservationsInfo> infos = getResInfoListForQuery(c);
        return infos;

    }

    public ReservationsInfo getReservation(int resId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM reservationInfo WHERE _id=" + resId + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "aagide");
            return null;
        }
        c.moveToFirst();
        ReservationsInfo reservationsInfo = new ReservationsInfo(c);
        reservationsInfo.id = c.getInt(c.getColumnIndex("_id"));

        return reservationsInfo;
    }

    private List<ReservationsInfo> getResInfoListForQuery(Cursor c) {
        c.moveToFirst();
        Log.d("count = ", c.getCount() + "");
        List<ReservationsInfo> infos = new ArrayList<ReservationsInfo>();
        while (!c.isAfterLast()) {
            ReservationsInfo reservationsInfo = new ReservationsInfo(c);
            reservationsInfo.id = c.getInt(c.getColumnIndex("_id"));
            c.moveToNext();
            infos.add(reservationsInfo);
        }
        return infos;
    }

    public int getMatchingReservation(long stTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectMatchingRecord = "SELECT * FROM reservationInfo WHERE endTimeMillis=" + stTime + ";";
        Cursor c = db.rawQuery(selectMatchingRecord, null);
        if (c == null || c.getCount() < 1) {
            Log.d("problem", "checkout");
        }
        int matchingResId = -1;
        c.moveToFirst();
        if (!c.isAfterLast()) {
            matchingResId = c.getInt(c.getColumnIndex("_id"));
        }
        return matchingResId;
    }

    boolean ShouldAdd(String s) {
        return (s != null && !s.isEmpty());
    }

    public void updateReservation(ReservationsInfo info, int resId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String updateqry = "UPDATE reservationInfo SET " +
                "kind='" + info.kind + "'" +
                (ShouldAdd(info.titleOrName) ? ", titleOrName='" + info.titleOrName + "'" : "") +
                ", stTimeMillis=" + info.stTimeMillis +
                ", startPlaceName='" + info.startPlaceName + "'" +
                ", startPlaceAddress='" + info.startPlaceAddress + "'" +
                ", endTimeMillis=" + info.endTimeMillis +
                (ShouldAdd(info.endPlaceName) ? ", endPlaceName='" + info.endPlaceName + "'" : "") +
                (ShouldAdd(info.endPlaceAddress) ? ", endPlaceAddress='" + info.endPlaceAddress + "'" : "") +
                (ShouldAdd(info.confNo) ? ", confNo='" + info.confNo + "'" : "") +
                (ShouldAdd(info.notes) ? ", notes='" + info.notes + "'" : "") +
                (ShouldAdd(info.attachmentFilePath) ? ", attachmentFilePath='" + info.attachmentFilePath + "'" : "") +
                ", tripId=" + info.tripId +
                "  WHERE _id=" + resId + ";";
        db.execSQL(updateqry);

    }

    public void updateReservationSanitized(ReservationsInfo info, int resId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String updateqry = "UPDATE reservationInfo SET " +
                "kind=?, titleOrName=?, stTimeMillis=?, startPlaceName=?, startPlaceAddress=?, " +
                "endTimeMillis=?, endPlaceName=?, endPlaceAddress=?, confNo=?, notes=?, " +
                "attachmentFilePath=?, tripId=? WHERE _id=?;";

        SQLiteStatement statement = db.compileStatement(updateqry);
        statement.bindString(1, info.kind);

        String titleOrName = "";
        if(ShouldAdd(info.titleOrName)){
            titleOrName=info.titleOrName;
        }
        statement.bindString(2, titleOrName);

        statement.bindLong(3, info.stTimeMillis);
        statement.bindString(4, info.startPlaceName);
        statement.bindString(5, info.startPlaceAddress);
        statement.bindLong(6, info.endTimeMillis);

        String endPlaceName = "";
        if(ShouldAdd(info.endPlaceName)){
            endPlaceName=info.endPlaceName;
        }
        statement.bindString(7, endPlaceName);

        String endPlaceAddress = "";
        if(ShouldAdd(info.endPlaceAddress)){
            endPlaceAddress=info.endPlaceAddress;
        }
        statement.bindString(8, endPlaceAddress);

        String confNo = "";
        if(ShouldAdd(info.confNo)){
            confNo=info.confNo;
        }
        statement.bindString(9, confNo);

        String notes = "";
        if(ShouldAdd(info.notes)){
            notes=info.notes;
        }
        statement.bindString(10, notes);

        String attachmentFilePath = "";
        if(ShouldAdd(info.attachmentFilePath)){
            attachmentFilePath=info.attachmentFilePath;
        }
        statement.bindString(11, attachmentFilePath);

        statement.bindLong(12, info.tripId);
        statement.bindLong(13,resId );

        Log.d("statement", statement.toString()+"");
        int numberOfRowsAffected = statement.executeUpdateDelete();

    }

    HashSet transportSubKinds = new HashSet();
    HashSet eventResSubKinds = new HashSet();
    HashSet hotelSubKinds = new HashSet();

    public void initTransportSubKinds() {
        transportSubKinds.add("car_start");
        transportSubKinds.add("bus_start");
        transportSubKinds.add("train_start");
        transportSubKinds.add("flight_start");
        transportSubKinds.add("car_end");
        transportSubKinds.add("bus_end");
        transportSubKinds.add("train_end");
        transportSubKinds.add("flight_end");
    }

    public void initEventResSubKinds() {
        eventResSubKinds.add("shows");
        eventResSubKinds.add("restaurant");
        eventResSubKinds.add("cruise");
        eventResSubKinds.add("adventure");
        eventResSubKinds.add("sightseeing");
        eventResSubKinds.add("water activity");
        eventResSubKinds.add("snow activity");
        eventResSubKinds.add("art/culture");
        eventResSubKinds.add("educational/official");
        eventResSubKinds.add("party");
        eventResSubKinds.add("other");
    }

    public void initHotelSubKinds() {
        hotelSubKinds.add("hotel_start");
        hotelSubKinds.add("hotel_end");
    }


    public String[] getDatePartsFromMilli(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime()).split("/");
    }

    public String getDateFromMilli(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        Log.d("time final ", formatter.format(calendar.getTime()) + "");
        return formatter.format(calendar.getTime());
    }

    public String getTimeFromMilli(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public Calendar getCalendarForDateAndTime(String date, String time) {
        String[] dateParts = date.trim().split("/");
        Log.d("entered dt ", date);
        Log.d("Date", dateParts[0] + "/" + dateParts[1] + "/" + dateParts[2] + "/");

        String[] temp = time.trim().split(" ");
        String[] timeParts = temp[0].split(":");
        int hour = Integer.parseInt(timeParts[0]);
        if(temp[1].toUpperCase().equals("PM")){
            hour = hour + 12;
        }
        Log.d("timetemp", temp[0] + " " + temp[1]);

        Log.d("time", timeParts[0] + ":" + timeParts[1]);


        Calendar calendar = new GregorianCalendar();
        calendar.set(Integer.parseInt(dateParts[2]),
                (Integer.parseInt(dateParts[0]) - 1),
                Integer.parseInt(dateParts[1]),
                hour,
                Integer.parseInt(timeParts[1]));

        return calendar;
    }

    public String getDateAndTimeInStdFormat(long timeinMilli) {
        return getTimeFromMilli(timeinMilli, "hh:mm a") + "  " +
                getDateFromMilli(timeinMilli, "MMM dd yyyy");
    }

    public Cursor fetchAllContacts() {

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM contactInfo;", null);
    }

    public long addContactInfo(ContactInfo contactInfo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", contactInfo.name);
        contentValues.put("email", contactInfo.email);
        contentValues.put("favourite", contactInfo.favourite);
        return db.insert("contactInfo", null, contentValues);
    }

    public long addPackingItemInfo(StdPackingItemsInfo stdPackingItemsInfo, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("item", stdPackingItemsInfo.item);
        contentValues.put("category", stdPackingItemsInfo.category);
        return db.insert("thingsToPack", null, contentValues);
    }

    public void updateContactInfo(ContactInfo info, int contactId) {
        String updateqry = "UPDATE contactInfo SET " +
                "name='" + info.name +
                "', email='" + info.email +
                "', favourite='" + info.favourite +
                "'  WHERE _id=" + contactId + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(updateqry);
    }

    public List<ContactInfo> getAllContactInfo() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM contactInfo ;", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return null;
        }

        List<ContactInfo> infos = getContactsForQuery(c);
        return infos;
    }

    private List<ContactInfo> getContactsForQuery(Cursor c) {
        c.moveToFirst();
        Log.d("count = ", c.getCount() + "");
        List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
        while (!c.isAfterLast()) {
            ContactInfo contactInfo = new ContactInfo(
                    c.getString(c.getColumnIndex("name")),
                    c.getString(c.getColumnIndex("email")),
                    c.getInt(c.getColumnIndex("favourite")), 0);
            contactInfos.add(contactInfo);
            c.moveToNext();
        }
        return contactInfos;
    }

    public int getContactId(String toBeRemovedEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM contactInfo WHERE email=" + toBeRemovedEmail + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return -1;
        }

        c.moveToFirst();
        if (!c.isAfterLast()) {
            return c.getInt(c.getColumnIndex("_id"));
        }
        return -1;

    }

    public void deleleAllEntriesInTable(String table_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + table_name);
    }

    public String getPackingListStringForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tripSpecificPackingLists WHERE tripId=" + tripId + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return null;
        }

        c.moveToFirst();
        if (!c.isAfterLast()) {
            return c.getString(c.getColumnIndex("itemsList"));
        }
        return null;
    }

    public String getToDoListForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM todoList WHERE tripId=" + tripId + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return null;
        }

        c.moveToFirst();
        if (!c.isAfterLast()) {
            return c.getString(c.getColumnIndex("itemsList"));
        }
        return null;

    }


    public long getPackingListIdForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tripSpecificPackingLists WHERE tripId=" + tripId + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return -1;
        }

        c.moveToFirst();
        if (!c.isAfterLast()) {
            return c.getInt(c.getColumnIndex("_id"));
        }
        return -1;
    }

    public long getToDoListIdForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM todoList WHERE tripId=" + tripId + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return -1;
        }

        c.moveToFirst();
        if (!c.isAfterLast()) {
            return c.getInt(c.getColumnIndex("_id"));
        }
        return -1;
    }

    public void updateTripSpecificThingsToPack(String packItemsString, int tripId) {


        String updateqry = "UPDATE tripSpecificPackingLists SET " +
                "itemsList=" + packItemsString +
                "  WHERE tripId=" + tripId + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(updateqry);

    }


    public long addThingsToPackForTrip(String packItemsString, int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("itemsList", packItemsString);
        contentValues.put("tripId", tripId);
        return db.insert("tripSpecificPackingLists", null, contentValues);
    }

    public long addToDoItemsForTrip(String toDoItemsString, int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("itemsList", toDoItemsString);
        contentValues.put("tripId", tripId);
        return db.insert("todoList", null, contentValues);
    }

    public boolean entryExistsInTripSpecificPackingTable(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tripSpecificPackingLists WHERE tripId=" + tripId + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return false;
        } else {
            return true;
        }
    }

    public boolean entryExistsInToDoListTable(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM todoList WHERE tripId=" + tripId + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return false;
        } else {
            return true;
        }
    }

    public long addBucketListInfo(BucketListInfo bucketListInfo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("theme", bucketListInfo.theme);
        contentValues.put("placeName", bucketListInfo.placeName);
        contentValues.put("placeAddress", bucketListInfo.placeAddress);
        contentValues.put("startMonth", bucketListInfo.startMonth);
        contentValues.put("endMonth", bucketListInfo.endMonth);
        contentValues.put("checked", bucketListInfo.checked);
        contentValues.put("tripId", bucketListInfo.tripId);
        return db.insert("bucketListInfo", null, contentValues);

    }

    public List<BucketListInfo> getAllBucketListInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM bucketListInfo ;", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllBucketListtInfo");
            return null;
        }

        List<BucketListInfo> infos = getBucketListItemsForQuery(c);
        return infos;

    }

    private List<BucketListInfo> getBucketListItemsForQuery(Cursor c) {
        c.moveToFirst();
        Log.d("count = ", c.getCount() + "");
        List<BucketListInfo> bucketListInfos = new ArrayList<BucketListInfo>();
        while (!c.isAfterLast()) {
            BucketListInfo bucketInfo = new BucketListInfo(
                    c.getString(c.getColumnIndex("theme")),
                    c.getString(c.getColumnIndex("placeName")),
                    c.getString(c.getColumnIndex("placeAddress")),
                    c.getString(c.getColumnIndex("startMonth")),
                    c.getString(c.getColumnIndex("endMonth")),
                    c.getInt(c.getColumnIndex("tripId")),
                    c.getInt(c.getColumnIndex("checked")));
            bucketListInfos.add(bucketInfo);
            c.moveToNext();
        }
        return bucketListInfos;

    }

    public SettingsInfo getSettingsInfo() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM settings;", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik 1", "agide");
            return new SettingsInfo();
        }
        c.moveToFirst();
        Log.d("count = ", c.getCount() + "");

        SettingsInfo settings = new SettingsInfo(
                c.getInt(c.getColumnIndex("disablePlanRem")),
                c.getInt(c.getColumnIndex("disableLocRem"))
        );
        return settings;

    }

    public void updateSettings(SettingsInfo settingsInfo) {

        String updateqry = "UPDATE settings SET " +
                "disablePlanRem=" + settingsInfo.disablePlanRem +
                ", disableLocRem=" + settingsInfo.disableLocRem + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(updateqry);

    }

    public void addSettings(SettingsInfo settingsInfo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("disablePlanRem", settingsInfo.disablePlanRem);
        contentValues.put("disableLocRem", settingsInfo.disableLocRem);
        db.insert("settings", null, contentValues);
    }

    public int getTripIdForStTimeMilli(long tripStTimeMilli) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tripInfo WHERE stTimeMillis=" + tripStTimeMilli + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return -1;
        } else {

            c.moveToFirst();
            if (!c.isAfterLast()) {
                return (c.getInt(c.getColumnIndex("_id")));
            }
            return -1;
        }
    }

    public int getBucketListId(String placeAddress) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM bucketListInfo WHERE placeAddress='" + placeAddress + "';", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return -1;
        } else {

            c.moveToFirst();
            if (!c.isAfterLast()) {
                return (c.getInt(c.getColumnIndex("_id")));
            }
            return -1;
        }

    }

    public BucketListInfo getBucketListInfo(int bucketId) {

        Log.d("bucketId", bucketId + "");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM bucketListInfo WHERE _id=" + bucketId + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getBucketListInfo");
            return null;
        }
        c.moveToFirst();
        Log.d("count = ", c.getCount() + "");
        Log.d("index = ", c.getColumnIndex("title") + "");


        Log.d("bucketListId TripDB", c.getInt(c.getColumnIndex("_id")) + "");

        BucketListInfo bucketInfo = new BucketListInfo(
                c.getString(c.getColumnIndex("theme")),
                c.getString(c.getColumnIndex("placeName")),
                c.getString(c.getColumnIndex("placeAddress")),
                c.getString(c.getColumnIndex("startMonth")),
                c.getString(c.getColumnIndex("endMonth")),
                c.getInt(c.getColumnIndex("tripId")),
                c.getInt(c.getColumnIndex("checked")));


        return bucketInfo;
    }

    public void updateBucketItemInfo(BucketListInfo bucketInfo, int bucketId) {
        String updateqry = "UPDATE bucketListInfo SET " +
                "theme='" + bucketInfo.theme +
                "', placeName='" + bucketInfo.placeName +
                "', placeAddress='" + bucketInfo.placeAddress +
                "', startMonth='" + bucketInfo.startMonth +
                "', endMonth='" + bucketInfo.endMonth +
                "', tripId=" + bucketInfo.tripId +
                ", checked=" + bucketInfo.checked +
                "  WHERE _id=" + bucketId + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(updateqry);
    }

    public void updateBucketItemInfoSanitized(BucketListInfo bucketInfo, int bucketId) {
        String updateqry = "UPDATE bucketListInfo SET theme=?, placeName=?, placeAddress=?, " +
                "startMonth=?, endMonth=?, tripId=?, checked=?  WHERE _id=?;";
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement statement = db.compileStatement(updateqry);
        statement.bindString(1, bucketInfo.theme);
        statement.bindString(2, bucketInfo.placeName);
        statement.bindString(3, bucketInfo.placeAddress);
        statement.bindString(4, bucketInfo.startMonth);
        statement.bindString(5, bucketInfo.endMonth);
        statement.bindLong(6, bucketInfo.tripId);
        statement.bindLong(7, bucketInfo.checked);
        statement.bindLong(8, bucketId );

        Log.d("statement", statement.toString()+"");
        int numberOfRowsAffected = statement.executeUpdateDelete();
    }

    public boolean tripExists(int tripId) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tripInfo WHERE _id=" + tripId + ";", null);
        if (c == null || c.getCount() < 1) {
            Log.d("kirik", "getAllContactInfo");
            return false;
        } else {

            c.moveToFirst();
            if (!c.isAfterLast()) {
                return true;
            }
            return false;
        }

    }

    public String getTimeRangeInfoOfTrip(int tripId) {

        TripInfo tripInfo = getTripInfo(tripId);
        long stTimeMilli = tripInfo.getStTimeMillis();
        long endTimeMilli = tripInfo.getEndTimeMillis();
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        if (currentTime < stTimeMilli) {
            return "Upcoming";
        } else if (currentTime > endTimeMilli) {
            return "Woohoo Completed !!";
        } else if (currentTime >= stTimeMilli && currentTime <= endTimeMilli) {
            return "On-going";
        }
        return null;
    }

    public Boolean dateTimeCheck(EditText editTextDateTime, String category, Context context ){
        Boolean dateTimeOk = false;
        String dateOrTime = editTextDateTime.getText().toString();

        switch (category) {
            case "startDate":
                if ( dateOrTime == null || dateOrTime.isEmpty()) {
                    timingsNullToast("Start Date", editTextDateTime, context);
                    break;
                }
                dateTimeOk = isDateValid(dateOrTime);
                if (!dateTimeOk) {
                    timingsFormatInvalidToast("Start Date", true, editTextDateTime, context);
                }
                break;
            case "endDate":
                if ( dateOrTime == null || dateOrTime.isEmpty()) {
                    timingsNullToast("End Date", editTextDateTime, context);
                    break;
                }
                dateTimeOk = isDateValid(dateOrTime);
                if (!dateTimeOk) {
                    timingsFormatInvalidToast("End Date", true, editTextDateTime, context);
                }
                break;
            case "startTime":
                if ( dateOrTime == null || dateOrTime.isEmpty()) {
                    timingsNullToast("Start Time", editTextDateTime, context);
                    break;
                }
                dateTimeOk = isTimeValid(dateOrTime);
                if (!dateTimeOk) {
                    timingsFormatInvalidToast("Start Time", true, editTextDateTime, context);
                }
                break;

            case "endTime":
                if ( dateOrTime == null || dateOrTime.isEmpty()) {
                    timingsNullToast("End Time", editTextDateTime, context);
                    break;
                }
                dateTimeOk = isTimeValid(dateOrTime);
                if (!dateTimeOk) {
                    timingsFormatInvalidToast("End Time", true, editTextDateTime, context);
                }
                break;
            case "remDate":
                if ( dateOrTime == null || dateOrTime.isEmpty()) {
                    timingsNullToast("Reminder Date", editTextDateTime, context);
                    break;
                }
                dateTimeOk = isDateValid(dateOrTime);
                if (!dateTimeOk) {
                    timingsFormatInvalidToast("Reminder Date", true, editTextDateTime, context);
                }
                break;
            case "remTime":
                if ( dateOrTime == null || dateOrTime.isEmpty()) {
                    timingsNullToast("Reminder Time", editTextDateTime, context);
                    break;
                }
                dateTimeOk = isTimeValid(dateOrTime);
                if (!dateTimeOk) {
                    timingsFormatInvalidToast("Reminder Time", true, editTextDateTime, context);
                }
                break;
        }
        return dateTimeOk;
    }

    private Boolean isTimeValid(String dateOrTime) {
        String[] tymParts = dateOrTime.trim().split(" ");
        if(tymParts.length != 2){
            return false;
        }
        String[] time = tymParts[0].split(":");
        for(String s: time){
            if(s.length() > 2){
                return  false;
            }
        }
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        if (!tymParts[1].toUpperCase().equals("PM") && !tymParts[1].toUpperCase().equals("AM")) {
            return false;
        }else if(hour > 12 || hour < 0){
            return false;
        }else if(minute > 60 || minute < 0 ){
            return false;
        }

        return true;
    }

    final static String DATE_FORMAT = "MM/dd/yyyy";

    public boolean isDateValid(String date) {
        String[] dateParts = date.split("/");
        for(String s: dateParts){
            if(s.length()>5){
                return false;
            }
        }
        try {
            Log.d("valid", date+"");
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            Log.d("invalid", date+"");
            return false;
        }
    }

    public boolean endAndStartTimeInMilliCheck(long stTimeMillis, long endTimeMillis, Context context){
        Calendar currentCalendar = Calendar.getInstance();
        long currentTimeMillis = currentCalendar.getTimeInMillis();

        if (stTimeMillis < currentTimeMillis) {
            toast("Start Timings Invalid. Entered time already passed", context);

            return false;
        }

        if (endTimeMillis <= stTimeMillis) {
            toast("Invalid Timings. End time should be greater than Start time.", context);

            return false;
        }
        return true;
    }

    private void timingsFormatInvalidToast(String s, Boolean isDate, EditText editTextDateTime,
                                           Context context) {
        String msg;
        if (isDate) {
            msg = "Please enter " + s + " in \"mm/dd/yyyy\" format";
        } else {
            msg = "Please enter " + s + " in \"hh:mm AM / hh:mm PM\" format";
        }
        toast(msg, context);
        editTextDateTime.setBackgroundResource(R.color.inputEntryErr);

    }

    private void timingsNullToast(String s, EditText editTextDateTime, Context context) {
        toast("Please enter " + s, context);
        editTextDateTime.setBackgroundResource(R.color.inputEntryErr);
    }

    public void toast(String msg, Context context) {
        Toast.makeText(context, msg
                , Toast.LENGTH_SHORT).show();
    }

}
