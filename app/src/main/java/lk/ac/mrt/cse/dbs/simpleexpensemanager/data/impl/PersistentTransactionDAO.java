/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistentTransactionDAO implements TransactionDAO {
    private final List<Transaction> transactions;
    private SQLiteDatabase sqlDatabase;

    public PersistentTransactionDAO(SQLiteDatabase sqlDatabase) {
        transactions = new LinkedList<>();
        this.sqlDatabase = sqlDatabase;
        loadData();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);
        DateFormat dateFormat = new SimpleDateFormat("M-d-yyyy", Locale.ENGLISH);

        ContentValues contentValues = new ContentValues();

        contentValues.put("date", dateFormat.format(transaction.getDate()));
        contentValues.put("accountNo", transaction.getAccountNo());
        contentValues.put("expenseType", String.valueOf(transaction.getExpenseType()));
        contentValues.put("amount", transaction.getAmount());
        sqlDatabase.insert("transactionList", null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }

    public void loadData() {
        Cursor cursor = sqlDatabase.rawQuery("select * from transactionList", null);
        DateFormat dateFormat = new SimpleDateFormat("M-d-yyyy", Locale.ENGLISH);

        while (cursor.moveToNext()) {
            Date date = new Date();
            try {
                date = dateFormat.parse(cursor.getString(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Transaction transaction = new Transaction(
                    date,
                    cursor.getString(2),
                    ExpenseType.valueOf(cursor.getString(3)),
                    Double.parseDouble(cursor.getString(4))
            );
            transactions.add(transaction);
        }
    }
}

