package com.audreytroutt.milhouse.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.dateWithTimeIntervalSince1970

actual fun formatDate(millis: Long): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "MMM d, yyyy"
    val date = NSDate.dateWithTimeIntervalSince1970(millis / 1000.0)
    return formatter.stringFromDate(date)
}
