//package com.jm.voicerec.recordUtils
//
//import android.content.ContentResolver
//import android.content.ContentValues
//import android.content.Context
//import android.net.Uri
//import android.os.Build
//import android.provider.MediaStore
//import java.io.FileNotFoundException
//import java.io.FileOutputStream
//import java.io.IOException
//
//class RecordUtils(_context: Context) {
//    private var context: Context = _context
//
//    private fun saveFile() {
//        val values = ContentValues()
//        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "video_1024.mp3")
//        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            values.put(MediaStore.Audio.Media.IS_PENDING, 1)
//        }
//        val contentResolver: ContentResolver = context.contentResolver
//        val item: Uri? = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
//        try {
//            val pdf = contentResolver.openFileDescriptor(item, "w", null)
//            if (pdf == null) {
//                Log.d("asdf", "null")
//            } else {
//                val str = "heloo"
//                val strToByte = str.toByteArray()
//                val fos = FileOutputStream(pdf.fileDescriptor)
//                fos.write(strToByte)
//                fos.close()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    values.clear()
//                    values.put(MediaStore.Audio.Media.IS_PENDING, 0)
//                    contentResolver.update(item, values, null, null)
//                }
//            }
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//}