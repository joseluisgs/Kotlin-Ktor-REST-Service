package es.joseluisgs.services

import java.io.File
import java.util.*

object Storage {
    fun saveFile(
        pathName: String,
        fileName: String,
        fileBytes: ByteArray,
    ): Map<String, String> {
        val fileExtension = fileName.substringAfterLast(".")
        val fileUpload = UUID.randomUUID().toString() + "." + fileExtension
        try {
            File("$pathName/$fileUpload").writeBytes(fileBytes)
            return mapOf(
                "originalName" to fileName,
                "uploadName" to fileUpload,
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