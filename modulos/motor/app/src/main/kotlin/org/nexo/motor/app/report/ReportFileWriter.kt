package org.nexo.motor.app.report

import android.content.ContentValues
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun writeReportFile(
    context: Context,
    fileName: String,
    mimeType: String,
    onWritten: (Uri) -> Unit,
    write: (OutputStream) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return
        context.contentResolver.openOutputStream(uri)?.use(write)
        onWritten(uri)
    } else {
        @Suppress("DEPRECATION")
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        FileOutputStream(file).use(write)
        MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf(mimeType)) { _, scannedUri ->
            if (scannedUri != null) onWritten(scannedUri)
        }
    }
}

fun writeReportCsv(context: Context, fileName: String, csvContent: String, onWritten: (Uri) -> Unit) {
    writeReportFile(context, fileName, "text/csv", onWritten) { outputStream ->
        outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
    }
}

fun writeReportPdf(context: Context, fileName: String, document: PdfDocument, onWritten: (Uri) -> Unit) {
    writeReportFile(context, fileName, "application/pdf", onWritten) { outputStream ->
        document.writeTo(outputStream)
    }
    document.close()
}
