package org.nexo.motor.core.content

import java.io.Closeable
import java.io.File
import java.util.zip.ZipFile

private const val MANIFEST_ENTRY_NAME = "content.json"

class ContentPackageException(message: String) : Exception(message)

class ContentPackageArchive(file: File) : Closeable {
    private val zip = ZipFile(file)

    fun readManifest(): String {
        val entry = zip.getEntry(MANIFEST_ENTRY_NAME)
            ?: throw ContentPackageException("arquivo \"$MANIFEST_ENTRY_NAME\" não encontrado na raiz do pacote de conteúdo")
        return zip.getInputStream(entry).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    fun readImage(path: String): ByteArray {
        val entry = zip.getEntry(path)
            ?: throw ContentPackageException("imagem \"$path\" referenciada não encontrada no pacote de conteúdo")
        return zip.getInputStream(entry).use { it.readBytes() }
    }

    override fun close() = zip.close()
}
