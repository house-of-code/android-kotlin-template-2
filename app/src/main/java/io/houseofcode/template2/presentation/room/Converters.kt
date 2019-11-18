package io.houseofcode.template2.presentation.room

import androidx.room.TypeConverter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Methods for converting custom data types to rows in the database tables.
 */
class Converters {

    @TypeConverter
    fun toDate(value: String?): Date? = if (value != null) {
        /*
         * As [SimpleDateFormat] is not thread-safe, we need to create an instance of it
         * without the scope of our function.
         * https://www.callicoder.com/java-simpledateformat-thread-safety-issues/
         */
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        try {
            dateFormat.parse(value)
        } catch (error: Throwable) {
            Timber.e(error, "Error parsing value as date: $value")
            null
        }
    } else {
        null
    }

    @TypeConverter
    fun fromDate(date: Date?): String? = if (date != null) {
        /*
         * As [SimpleDateFormat] is not thread-safe, we need to create an instance of it
         * without the scope of our function.
         * https://www.callicoder.com/java-simpledateformat-thread-safety-issues/
         */
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        dateFormat.format(date)
    } else {
        null
    }
}