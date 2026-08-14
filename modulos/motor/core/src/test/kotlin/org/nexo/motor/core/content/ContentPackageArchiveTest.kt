package org.nexo.motor.core.content

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ContentPackageArchiveTest {

    private fun buildZip(entries: Map<String, ByteArray>): File {
        val file = File.createTempFile("content-package", ".zip")
        file.deleteOnExit()
        ZipOutputStream(file.outputStream()).use { zip ->
            entries.forEach { (name, bytes) ->
                zip.putNextEntry(ZipEntry(name))
                zip.write(bytes)
                zip.closeEntry()
            }
        }
        return file
    }

    @Test
    fun `readManifest le o conteudo de content json na raiz do pacote`() {
        val manifestText = """{"schema_version": "1.0.0"}"""
        val zipFile = buildZip(mapOf("content.json" to manifestText.toByteArray()))

        ContentPackageArchive(zipFile).use { archive ->
            assertEquals(manifestText, archive.readManifest())
        }
    }

    @Test
    fun `readManifest lanca ContentPackageException quando content json nao existe no pacote`() {
        val zipFile = buildZip(mapOf("outro-arquivo.json" to "{}".toByteArray()))

        ContentPackageArchive(zipFile).use { archive ->
            assertFailsWith<ContentPackageException> { archive.readManifest() }
        }
    }

    @Test
    fun `readImage le os bytes da imagem referenciada por caminho relativo`() {
        val imageBytes = byteArrayOf(1, 2, 3, 4)
        val zipFile = buildZip(mapOf("images/frame01.png" to imageBytes))

        ContentPackageArchive(zipFile).use { archive ->
            assertEquals(imageBytes.toList(), archive.readImage("images/frame01.png").toList())
        }
    }

    @Test
    fun `readImage lanca ContentPackageException quando a imagem referenciada nao existe no pacote`() {
        val zipFile = buildZip(mapOf("content.json" to "{}".toByteArray()))

        ContentPackageArchive(zipFile).use { archive ->
            assertFailsWith<ContentPackageException> { archive.readImage("nao-existe.png") }
        }
    }
}
