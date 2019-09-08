package com.sandeep.databasetest

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.IllegalArgumentException

private const val TAG = "AppDatabase"
private const val DATABASE_NAME = "TestDb.db"
private const val DATABASE_VERSION = 3

internal class AppDatabase private constructor(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    init {
        Log.d(TAG, "AppDatabase initialized")
    }
    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "onCreate starts")
        val sSql = """CREATE TABLE ${TestData.TABLE_NAME} (
            |${TestData.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            |${TestData.Columns.REC_NAME} TEXT,
            |${TestData.Columns.REC_DESC} TEXT);
        """.replaceIndent(" ").trimMargin()
        Log.d(TAG, sSql)
        db?.execSQL(sSql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade starts")
        when (oldVersion) {
            1 -> {

            }
            else -> throw IllegalArgumentException("onupgrade with unknown new version")
        }
    }

    companion object : SingletonHolder<AppDatabase, Context>(::AppDatabase)
}