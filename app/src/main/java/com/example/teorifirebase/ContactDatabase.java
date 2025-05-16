//package com.example.teorifirebase;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import androidx.annotation.NonNull;
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import androidx.sqlite.db.SupportSQLiteDatabase;
//
//@Database(entities = {ContactModel.class}, version = 1, exportSchema = false)
//public abstract class ContactDatabase extends RoomDatabase {
//    private static ContactDatabase instance;
//    public abstract ContactDao contactDao();
//
//    public static synchronized ContactDatabase getInstance(Context context) {
//        if (instance == null) {
//            instance = Room.databaseBuilder(context.getApplicationContext(),
//                            ContactDatabase.class, "contact_database")
//                    .fallbackToDestructiveMigration()
//                    .addCallback(roomCallback)
//                    .build();
//        }
//        return instance;
//    }
//
//    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//            new PopulateDbAsyncTask(instance).execute();
//        }
//    };
//
//    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
//        private ContactDao contactDao;
//
//        private PopulateDbAsyncTask(ContactDatabase db) {
//            contactDao = db.contactDao();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            contactDao.insert(new ContactModel("John Doe", "john@example.com", "081234567890"));
//            contactDao.insert(new ContactModel("Jane Smith", "jane@example.com", "089876543210"));
//            return null;
//        }
//    }
//}