//package com.example.teorifirebase;
//
//import android.app.Application;
//import android.os.AsyncTask;
//
//import androidx.lifecycle.LiveData;
//
//import java.util.List;
//
//public class ContactRepository {
//    private ContactDao contactDao;
//    private LiveData<List<ContactModel>> allContacts;
//
//    public ContactRepository(Application application) {
//        ContactDatabase database = ContactDatabase.getInstance(application);
//        contactDao = database.contactDao();
//        allContacts = contactDao.getAllContacts();
//    }
//
//    public void insert(ContactModel contact) {
//        new InsertContactAsyncTask(contactDao).execute(contact);
//    }
//
//    public void update(ContactModel contact) {
//        new UpdateContactAsyncTask(contactDao).execute(contact);
//    }
//
//    public void delete(ContactModel contact) {
//        new DeleteContactAsyncTask(contactDao).execute(contact);
//    }
//
//    public void deleteAllContacts() {
//        new DeleteAllContactsAsyncTask(contactDao).execute();
//    }
//
//    public LiveData<List<ContactModel>> getAllContacts() {
//        return allContacts;
//    }
//
//    private static class InsertContactAsyncTask extends AsyncTask<ContactModel, Void, Void> {
//        private ContactDao contactDao;
//
//        private InsertContactAsyncTask(ContactDao contactDao) {
//            this.contactDao = contactDao;
//        }
//
//        @Override
//        protected Void doInBackground(ContactModel... contacts) {
//            contactDao.insert(contacts[0]);
//            return null;
//        }
//    }
//
//    private static class UpdateContactAsyncTask extends AsyncTask<ContactModel, Void, Void> {
//        private ContactDao contactDao;
//
//        private UpdateContactAsyncTask(ContactDao contactDao) {
//            this.contactDao = contactDao;
//        }
//
//        @Override
//        protected Void doInBackground(ContactModel... contacts) {
//            contactDao.update(contacts[0]);
//            return null;
//        }
//    }
//
//    private static class DeleteContactAsyncTask extends AsyncTask<ContactModel, Void, Void> {
//        private ContactDao contactDao;
//
//        private DeleteContactAsyncTask(ContactDao contactDao) {
//            this.contactDao = contactDao;
//        }
//
//        @Override
//        protected Void doInBackground(ContactModel... contacts) {
//            contactDao.delete(contacts[0]);
//            return null;
//        }
//    }
//
//    private static class DeleteAllContactsAsyncTask extends AsyncTask<Void, Void, Void> {
//        private ContactDao contactDao;
//
//        private DeleteAllContactsAsyncTask(ContactDao contactDao) {
//            this.contactDao = contactDao;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            contactDao.deleteAllContacts();
//            return null;
//        }
//    }
//}