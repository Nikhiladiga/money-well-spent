package com.nikhil.moneywellspent.domain;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.nikhil.moneywellspent.MainActivity;
import com.nikhil.moneywellspent.entity.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadSMS {

    @SuppressLint("SimpleDateFormat")
    public static void readAllSms(String currentMonth) {
        Long latestTransactionDate = MainActivity.getInstance().transactionViewModel.getLastTransactionDate();
        StringBuilder smsBuilder = new StringBuilder();
        final String SMS_URI_INBOX = "content://sms/inbox";
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            String axisStringPattern = "%Axis%";
            String sbiStringPattern = "%SBIUPI%";
            String hdfcStringPattern = "%HDFCBK%";
            String iciciStringPattern = "%ICICIB%";
            Cursor cur = MainActivity.getInstance().getContentResolver().query(uri, projection, "address LIKE ? OR address LIKE ? OR address LIKE? OR address LIKE?", new String[]{axisStringPattern, sbiStringPattern, hdfcStringPattern, iciciStringPattern}, "date desc");
            if (cur.moveToFirst()) {
                int index_Body = cur.getColumnIndex("body");
                int dateIndex = cur.getColumnIndex("date");
                int addressIndex = cur.getColumnIndex("address");
                do {
                    String msgBody = cur.getString(index_Body);
                    long msgDate = cur.getLong(dateIndex);
                    String address = cur.getString(addressIndex);

                    Transaction transaction = null;
                    if (address.contains("Axis") || address.contains("AXIS")) {
                        if ((msgBody.contains("Debit") || msgBody.contains("credited to"))) {
                            transaction = MessageParser.parseMessage("axis", msgBody, msgDate);
                        }
                    } else if (address.contains("SBIUPI")) {
                        if ((msgBody.contains("debited by") || msgBody.contains("credited by"))) {
                            transaction = MessageParser.parseMessage("sbi", msgBody, msgDate);
                        }
                    } else if (address.contains("HDFCBK")) {
                        if (msgBody.contains("debited from") || msgBody.contains("credited to")) {
                            transaction = MessageParser.parseMessage("hdfc", msgBody, msgDate);
                        }
                    } else if (address.contains("ICICIB") || address.contains("icici")) {
                        if (msgBody.contains("debited for") || msgBody.contains("credited with")) {
                            transaction = MessageParser.parseMessage("icici", msgBody, msgDate);
                        }
                    }

                    if (transaction != null && transaction.getCreatedAt() != null
                            && transaction.getCreatedAt() > latestTransactionDate
                            && new SimpleDateFormat("MMMM").format(new Date(transaction.getCreatedAt())).equalsIgnoreCase(currentMonth)
                    ) {
                        MainActivity.getInstance().transactionViewModel.insertTransaction(transaction);
                    }
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            } // end if
        } catch (Exception ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
    }

}
