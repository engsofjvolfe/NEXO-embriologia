package org.nexo.motor.app.report

import android.graphics.Paint
import android.graphics.pdf.PdfDocument

private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MARGIN = 40f
private const val LINE_HEIGHT = 18f

fun renderReportPdf(lines: List<String>): PdfDocument {
    val document = PdfDocument()
    val paint = Paint().apply { textSize = 12f }
    val linesPerPage = ((PAGE_HEIGHT - 2 * MARGIN) / LINE_HEIGHT).toInt()

    lines.chunked(linesPerPage).forEachIndexed { pageIndex, pageLines ->
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex + 1).create()
        val page = document.startPage(pageInfo)
        var y = MARGIN + LINE_HEIGHT
        pageLines.forEach { line ->
            page.canvas.drawText(line, MARGIN, y, paint)
            y += LINE_HEIGHT
        }
        document.finishPage(page)
    }

    return document
}
