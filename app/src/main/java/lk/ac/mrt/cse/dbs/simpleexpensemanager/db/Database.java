package lk.ac.mrt.cse.dbs.simpleexpensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public Database(Context context) {
        super(context, "200272L.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create Table userList(accountNo TEXT primary key,bankName TEXT, accountHolderName TEXT,balance TEXT)");
        database.execSQL("create Table transactionList(transactionID INTEGER primary key autoincrement,date Date,accountNo TEXT,expenseType TEXT,amount TEXT,FOREIGN KEY(accountNo) REFERENCES userList(accountNo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL("drop Table if exists userList");
        database.execSQL("drop Table if exists transactionList");
    }
}
