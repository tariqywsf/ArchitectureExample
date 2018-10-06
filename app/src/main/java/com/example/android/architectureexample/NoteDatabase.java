package com.example.android.architectureexample;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

/**
 * Created by Tarek on 20-Sep-18.
 */
@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    //singelton
    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    // synchronized means only one thread at the time allowed to access this method so you don't
    // accidentally  create 2 instances of this database when 2 different threads try to access this
    // at the same time.
    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            // we use database builder because instance is singelton.
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private NoteDao noteDao;

        public PopulateDbAsyncTask(NoteDatabase db) {
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title1", "Description1", 1));
            noteDao.insert(new Note("Title1", "Description2", 2));
            noteDao.insert(new Note("Title3", "Description3", 3));
            return null;
        }
    }

}
