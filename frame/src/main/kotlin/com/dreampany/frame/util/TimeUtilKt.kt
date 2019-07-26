package com.dreampany.frame.util

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Created by Roman-372 on 7/25/2019
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class TimeUtilKt {
    companion object {
        fun getDay(): Int {
            val date = DateTime()
            return date.dayOfMonth
        }

        fun getMonth(): Int {
            val date = DateTime()
            return date.monthOfYear
        }

        fun getYear(): Int {
            val date = DateTime()
            return date.year
        }


        fun getDay(date: String, pattern: String): Int {
            val fmt = DateTimeFormat.forPattern(pattern);
            return fmt.parseDateTime(date).dayOfMonth
        }

        fun getMonth(date: String, pattern: String): Int {
            val fmt = DateTimeFormat.forPattern(pattern);
            return fmt.parseDateTime(date).monthOfYear
        }

        fun getYear(date: String, pattern: String): Int {
            val fmt = DateTimeFormat.forPattern(pattern);
            return fmt.parseDateTime(date).year
        }
    }
}