package com.Yogify.birthdayreminder.backup

import android.content.pm.Capability
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.Yogify.birthdayreminder.R
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.File.Capabilities
import com.google.api.services.drive.model.FileList
import com.google.api.services.drive.model.Permission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class DriveHelper(var drive: Drive) {



    fun createFolder(foldername: String): String {

        var folderid = folderId(foldername)
        if (folderid.isNullOrEmpty()) {
            val fileMetadata = File()
            fileMetadata.name = foldername
            fileMetadata.mimeType = "application/vnd.google-apps.folder"
            var file: File? = null
            try {
                file = drive.files().create(fileMetadata).setFields("id").execute()
                folderid = file.id
                Log.d("DRIVEFOLDERId", folderid)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return folderid!!
    }

    fun folderId(foldername: String): String? {
        var folderid = ""
        var pageToken: String? = null
        do {
            var result: FileList? = null
            try {
                result = drive.files().list().setQ("mimeType='application/vnd.google-apps.folder'")
                    .setSpaces("drive").setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken).execute()
                for (file in result.files) {
                    System.out.printf("Total Folder : " + result.size)
                    for (i in 0 until result.size) {
                        if (file.name == foldername) {
                            folderid = file.id
                            Log.d("DRIVEFOLDERId", folderid)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            pageToken = result!!.nextPageToken
        } while (pageToken != null)
        return folderid
    }

    fun createSubFolder(parentfolderid: String, foldername: String): String {
        var folderid = ""
        val fileMetadata = File()
        fileMetadata.parents = listOf(parentfolderid)
        fileMetadata.name = foldername
        fileMetadata.mimeType = "application/vnd.google-apps.folder"
        var file: File? = null
        try {
            file = drive.files().create(fileMetadata).setFields("id").execute()
            folderid = file.id
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return folderid
    }

    fun UploadCsvFile(folderid: String, filepath: String?, filename: String?): String? {
        var fileid = ""
        if (!folderid.isEmpty()) {
            val fileMetadata2 = File()
            fileMetadata2.name = filename
            fileMetadata2.parents = listOf(folderid)
            val filePath = java.io.File(filepath)
            val mediaContent2 = FileContent("text/csv", filePath)
            var file2: File? = null
            try {
                file2 = drive.files().create(fileMetadata2, mediaContent2).setFields("id").execute()
                fileid = file2.id
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return fileid
    }


    fun uploadImageFile(folderid: String, filepath: String): String {
        var weburl = ""
        if (!folderid.isEmpty()) {
            val fileMetadata = File()
            fileMetadata.name = Uri.parse(filepath).lastPathSegment.toString()
            fileMetadata.parents = listOf(folderid)
            fileMetadata.webViewLink
            val filePath = java.io.File(filepath)
            filePath.name
            val mediaContent = FileContent("image/jpeg", filePath)
            var file: File? = null
            try {
                file = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id,name,webContentLink,webViewLink").execute()
                weburl = file.webContentLink
                Log.d("FILEURL",file.webContentLink)
                Log.d("FILEURL",file.webViewLink)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return weburl
    }


    fun fileId(foldername: String): String {
        var folderid = ""
        var pageToken: String? = null
        do {
            var result: FileList? = null
            try {
                result = drive.files().list().setQ("mimeType='text/csv'").setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)").setPageToken(pageToken).execute()
                for (file in result.files) {
                    System.out.printf("Found file: %s (%s)\n", file.name, file.id)
                    for (i in 0 until result.size) {
                        if (file.name == foldername) {
                            folderid = file.id
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            pageToken = result!!.nextPageToken
        } while (pageToken != null)
        return folderid
    }

    fun imageFileId(foldername: String): String? {
        var folderid = ""
        var pageToken: String? = null
        do {
            var result: FileList? = null
            try {
                result = drive.files().list().setQ("mimeType='image/jpeg'").setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)").setPageToken(pageToken).execute()
                for (file in result.files) {
                    System.out.printf("Found file: %s (%s)\n", file.name, file.id)
                    for (i in 0 until result.size) {
                        if (file.name == foldername) {
                            folderid = file.id
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            pageToken = result!!.nextPageToken
        } while (pageToken != null)
        return folderid
    }

    fun uploadFile(folderid: String, filepath: String, filename: String): Boolean {
        val updated = false
        val fileidtodelete = fileId(filename)
        if (fileidtodelete != "") {
            deleteFile(drive, fileidtodelete)
            UploadCsvFile(folderid, filepath, filename)
        }
        return updated
    }

    fun updateImageFile(folderid: String, filepath: String, filename: String): Boolean {
        val updated = false
        val fileidtodelete = fileId(filename)
        if (fileidtodelete != "") {
            deleteFile(drive, fileidtodelete)
            uploadImageFile(folderid, filepath)
        }
        return updated
    }

    fun deleteFile(service: Drive, fileId: String) {
        try {
            service.files().delete(fileId).execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}