package org.nexo.motor.app.report

import android.content.Intent
import android.net.Uri

fun buildReportShareIntent(csvUri: Uri, pdfUri: Uri): Intent =
    Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "*/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(csvUri, pdfUri))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
