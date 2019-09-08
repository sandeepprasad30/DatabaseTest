package com.sandeep.databasetest

import android.content.ContentValues
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        query()
        //insert()
        //update()
        //updateTwo()
        //delete()
    }

    private fun query() {
        Log.d(TAG, "query starts")
        val projection = arrayOf(TestData.Columns.REC_NAME)
        val cursor = contentResolver.query(TestData.CONTENT_URI,
            null,
            null,
            null,
            null)
        cursor.use {
            while(it?.moveToNext()!!) {
                with(cursor){
                    val id = this?.getLong(0)
                    val name = this?.getString(1)
                    val desc = this?.getString(2)

                    val result = "ID: $id, Name: $name, Description: $desc"
                    Log.d(TAG, "on query result is $result")
                }
            }
        }
    }

    private fun insert() {
        val values = ContentValues().apply {
            put(TestData.Columns.REC_NAME, "rec1")
            put(TestData.Columns.REC_DESC, "rec_desc")
        }

        val uri = contentResolver.insert(TestData.CONTENT_URI, values)
        Log.d(TAG, "new row uri is $uri")
    }

    private fun update() {
        val values = ContentValues().apply {
            put(TestData.Columns.REC_NAME, "to_delete")
            put(TestData.Columns.REC_DESC, "some desc")
        }
        val recUri = TestData.buildUriFromId(3)

        val rowsAffected = contentResolver.update(recUri, values, null ,null)
        Log.d(TAG, "update rows affected are $rowsAffected")
    }

    private fun updateTwo() {
        val values = ContentValues().apply {
            put(TestData.Columns.REC_NAME, "my rec")
            put(TestData.Columns.REC_DESC, "my desc")
        }
        val selection = TestData.Columns.REC_DESC + " = ?"
        val selectionArgs = arrayOf("rec_desc")

        val rowsAffected = contentResolver.update(TestData.CONTENT_URI, values, selection ,selectionArgs)
        Log.d(TAG, "update rows affected are $rowsAffected")
    }

    private fun delete() {
        val selection = TestData.Columns.REC_NAME + " = 'to_delete'"
        val rowsAffected = contentResolver.delete(TestData.CONTENT_URI, selection, null)
        Log.d(TAG, "delete rows affected are $rowsAffected")
    }
}
