package com.example.authapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.authapp.model.Image_Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val imageList = MutableLiveData<List<Image_Item>>()
    val imageListAll = ArrayList<Image_Item>()
    var selectedStartDate = MutableLiveData<String>()
    var selectedEndDate = MutableLiveData<String>()
    var mProgressDialog: ProgressDialog? = null

    fun getImageList() {
        val context = getApplication<Application>().applicationContext
        val jsonFileString = getJsonDataFromAsset(context, "image_data.json")
        Log.i("data_data", jsonFileString.toString())
        val gson = Gson()
        val listPersonType = object : TypeToken<List<Image_Item>>() {}.type
        var images: List<Image_Item> = gson.fromJson(jsonFileString, listPersonType)
        imageList.postValue(images)
        imageListAll.addAll(images)
    }

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    @SuppressLint("SimpleDateFormat")
    fun filterData(startDate: Date, endDate: Date) {
        val imageListTemp = ArrayList<Image_Item>()
        for (i in 0 until imageListAll.size) {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val strs = imageListAll.get(i).date?.split(" ")?.toTypedArray()
            var date = LocalDate.parse(strs?.get(0))
            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var formattedDate = date.format(formatter)
            val cal = Calendar.getInstance()
            cal.time = sdf.parse(formattedDate) as Date
            val dateToValidate: Date = cal.time
            if (dateToValidate.after(startDate) && dateToValidate.before(endDate))
                imageListTemp.add(imageListAll.get(i))
        }
        imageList.postValue(imageListTemp)
    }

    fun openDatePickerDialog(startDate: String, context: Context) {
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                if (startDate == "")
                    selectedStartDate.value =
                        year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
                else
                    selectedEndDate.value =
                        year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = cal.getTimeInMillis()
        if (startDate != "") {
            /* val cal1 = Calendar.getInstance()
             val strs = binding.tvFrom.text.split("/").toTypedArray()
             cal1.set(strs.get(2).toInt(), strs.get(1).toInt(), strs.get(0).toInt())
             datePickerDialog.datePicker.minDate = cal1.getTimeInMillis()*/
        }
        datePickerDialog.show()
    }

    fun downloadImages(urlList: ArrayList<String>, context: Context) {
        mProgressDialog = ProgressDialog(context)
        mProgressDialog!!.setMessage("Download Images...")
        mProgressDialog!!.show()
        for (i in urlList.indices) {
            Glide.with(context)
                .asBitmap()
                .load(urlList.get(i))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        mSaveMediaToStorage(resource, context)
                    }
                })
        }
        var duration: Long = 3000;
        if (urlList.size > 10) {
            duration = 7000
        }
        Handler(Looper.getMainLooper()).postDelayed({
            Toast.makeText(context, "Image download Successfully", Toast.LENGTH_LONG)
                .show()
            mProgressDialog!!.dismiss()
        }, duration)
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?, context: Context) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Log.e("saved :- ", filename + "Gallery")
        }
    }
}
