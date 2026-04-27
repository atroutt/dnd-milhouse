package com.audreytroutt.milhouse.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatDate(millis: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))
