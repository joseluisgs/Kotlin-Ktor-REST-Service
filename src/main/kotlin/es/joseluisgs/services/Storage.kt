package es.joseluisgs.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.*

object Storage {
    suspend fun saveFile(
        pathName: String,
        fileName: String,
        fileBytes: ByteArray,
    ): Map<String, String> {
        val fileExtension = fileName.substringAfterLast(".")
        val fileUpload = UUID.randomUUID().toString() + "." + fileExtension
        try {
            coroutineScope {
                val file = async(Dispatchers.IO) { File("$pathName/$fileUpload").writeBytes(fileBytes) }
                file.await()
            }
            return mapOf(
                "originalName" to fileName,
                "uploadName" to fileUpload
            )
        } catch (e: Exception) {
            throw Exception("Error saving file")
        }
    }

    fun getFile(pathName: String, fileName: String): File {
        val file = File("$pathName/$fileName")
        if (!file.exists()) {
            throw Exception("File not found")
        } else {
            return file
        }
    }

    fun deleteFile(pathName: String, fileName: String) {
        val file = File("$pathName/$fileName")
        if (!file.exists()) {
            throw Exception("File not found")
        } else {
            file.delete()
        }
    }
}