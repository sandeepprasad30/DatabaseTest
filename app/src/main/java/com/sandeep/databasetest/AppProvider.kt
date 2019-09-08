package com.sandeep.databasetest

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import java.lang.IllegalArgumentException

private const val TAG ="AppProvider"
const val CONTENT_AUTHORITY = "com.sandeep.databasetest.provider"

private const val REC = 100
private const val REC_ID = 101

val CONTENT_AUTHORITY_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY" )

class AppProvider: ContentProvider() {

    private val uriMatcher by lazy { buildUriMatcher() }
    private fun buildUriMatcher(): UriMatcher {
        Log.d(TAG, "buildUriMatcher: starts")

        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        matcher.addURI(CONTENT_AUTHORITY, TestData.TABLE_NAME, REC)

        matcher.addURI(CONTENT_AUTHORITY, "${TestData.TABLE_NAME}/#", REC_ID)

        return matcher
    }

    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate: starts")
        return true
    }

    override fun getType(uri: Uri): String? {
        val match = uriMatcher.match(uri)
        return when (match) {
            REC -> TestData.CONTENT_TYPE
            REC_ID -> TestData.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("unknown uri $uri")
        }
    }

    @Throws(IllegalArgumentException::class)
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "query starts with uri $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "match is $match")

        val queryBuilder = SQLiteQueryBuilder()

        when (match) {
            REC -> queryBuilder.tables = TestData.TABLE_NAME
            REC_ID -> {
                queryBuilder.tables = TestData.TABLE_NAME
                val recId = TestData.getId(uri)
                queryBuilder.appendWhere("${TestData.Columns.ID} = ")
                queryBuilder.appendWhereEscapeString("$recId")
            }
            else -> throw IllegalArgumentException("unknown uri $uri")
        }

        val db = context?.let { AppDatabase.getInstance(it).readableDatabase }
        val cursor = queryBuilder.query(db , projection, selection, selectionArgs, null, null, sortOrder)
        Log.d(TAG, "query rows returned = ${cursor.count}")

        return cursor
    }

    @Throws(IllegalArgumentException::class)
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "insert starts with uri $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "match is $match")

        val recId: Long
        val returnUri: Uri

        when(match) {
            REC -> {
                val db = context?.let { AppDatabase.getInstance(it).writableDatabase }
                recId = db!!.insert(TestData.TABLE_NAME, null, values)
                if (recId != -1L) {
                    returnUri = TestData.buildUriFromId(recId)
                } else {
                    throw SQLException("failed to insert uri $uri")
                }
            }
            else -> throw  IllegalArgumentException ("unknown uri at update $uri")
        }

        return returnUri

    }





    @Throws(IllegalArgumentException::class)
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "update starts with uri $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "match is $match")

        val count: Int
        var selectionCriteria: String

        when(match) {
            REC -> {
                val db = context?.let { AppDatabase.getInstance(it).writableDatabase }
                count = db!!.update(TestData.TABLE_NAME, values, selection, selectionArgs)
            }

            REC_ID -> {
                val db = context?.let { AppDatabase.getInstance(it).writableDatabase }
                val id = TestData.getId(uri)
                selectionCriteria = "${TestData.Columns.ID} =  $id"
                if (selection != null && selection.isNotEmpty()){
                    selectionCriteria += " AND ($selection)"
                }
                count = db!!.update(TestData.TABLE_NAME, values, selectionCriteria, selectionArgs)
            }
            else -> throw  IllegalArgumentException ("unknown uri at update $uri")
        }

        Log.d(TAG, "existing update, affected rows $count")
        return count
    }

    @Throws(IllegalArgumentException::class)
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "delete starts with uri $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "match is $match")

        val count: Int
        var selectionCriteria: String

        when(match) {
            REC -> {
                val db = context?.let { AppDatabase.getInstance(it).writableDatabase }
                count = db!!.delete(TestData.TABLE_NAME, selection, selectionArgs)
            }

            REC_ID -> {
                val db = context?.let { AppDatabase.getInstance(it).writableDatabase }
                val id = TestData.getId(uri)
                selectionCriteria = "${TestData.Columns.ID} =  $id"
                if (selection != null && selection.isNotEmpty()){
                    selectionCriteria += " AND ($selection)"
                }
                count = db!!.delete(TestData.TABLE_NAME, selectionCriteria, selectionArgs)
            }
            else -> throw  IllegalArgumentException ("unknown uri at update $uri")
        }

        Log.d(TAG, "exiting delete, affected rows $count")
        return count
    }


}