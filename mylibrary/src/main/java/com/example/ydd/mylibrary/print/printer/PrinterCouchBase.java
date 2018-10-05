package com.example.ydd.mylibrary.print.printer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrinterCouchBase {
    private static Database db;

    protected PrinterCouchBase(Context context)
    {
        DatabaseConfiguration config = new DatabaseConfiguration(context);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File folder = new File(String.format("%s/PrinterKitchen", Environment.getExternalStorageDirectory()));
            config.setDirectory(folder.getAbsolutePath());
        }
        try {
            db = new Database("kitchendb", config);
        } catch (CouchbaseLiteException e)
        {
            e.printStackTrace();
        }
    }

    public static Database getDb() {
        return db;
    }

    //保存打印机
    public static void setSavePinterPort(PinterPort pinterPort){
        List<Document> documentList = getAllPrinter();
        if (documentList.size() > 0) {
            for (Document doc : documentList) {
                if (!doc.getString("name").equals(pinterPort.getName())) {
                    savePrinterCouchbase(pinterPort);
                }
            }
        }else{

            savePrinterCouchbase(pinterPort);
        }


    }

    private static void savePrinterCouchbase(PinterPort pinterPort){
        MutableDocument document = new MutableDocument();
        document.setString("name", pinterPort.getName());
        document.setString("address", pinterPort.getAddress());
        document.setString("port", pinterPort.getPort());
        document.setString("type", "Printer");
        document.setInt("printerType", pinterPort.getTypePrint());
        try {
            db.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public static List<Document> getAllPrinter(){
        List<Document> documentList = new ArrayList<>();
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(PrinterCouchBase.getDb())).
                        where(Expression.property("type").equalTo(Expression.string("Printer")));
        Document document = null;
        try {
            ResultSet resultSet = query.execute();
            Result row ;
            while ((row = resultSet.next()) != null){
                document = PrinterCouchBase.getDb().getDocument(row.getString(0));
                documentList.add(document);
            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByWhere", "Exception=", e);
        }
        return documentList;
    }


    /**
     *  初始化单个打印机
     */
    public static Document getSinglePrint(String name){
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(db)).where(Expression.property("name").equalTo(Expression.string(name)));
        Document doc = null;
        try {
            ResultSet resultSet = query.execute();
            Result row  = resultSet.next();
            if (row != null) {
                doc = PrinterCouchBase.getDb().getDocument(row.getString(0));
                Log.e("---name",row.getString(0));
            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByWhere", "Exception=", e);
        }
        return doc;
    }
}
