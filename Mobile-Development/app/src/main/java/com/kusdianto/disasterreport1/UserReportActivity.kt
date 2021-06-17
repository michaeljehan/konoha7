package com.kusdianto.disasterreport1

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_user_report.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserReportActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    private var fileUri: Uri? = null
    private var mediaPath: String? = null
    private var mImageFileLocation = ""
    private var postPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_report)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        btn_user_send_report.setOnClickListener{
            val name : String = edt_nama_bencana.text.toString()
            val keterangan : String = edt_keterangan.text.toString()

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("NAME", name)
            editor.putString("KETERANGAN", keterangan)
            editor.apply()

            Toast.makeText(this, "Thank You for Your Report!", Toast.LENGTH_SHORT).show()

            // aku masukin ke sini upload file nya, jadi pake button paling akhir aja sekalian kirim data
            uploadFile()

            val intent   = Intent(this, InformasiActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        camBtn.setOnClickListener { //event button listener for camera
            captureImage()
        }

        chooseBtn.setOnClickListener { //event button listener for choose iamge
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO)
        }

        //karna blm ada link API jadi tombol Button Proccess nya di ilangin dulu ya
        /*buttonProcess.setOnClickListener {
            uploadFile()
        }

         */
    }
    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        //resouce variable for camera feature and make file
        const val REQUEST_TAKE_PHOTO = 0
        const val REQUEST_PICK_PHOTO = 2
        const val CAMERA_PIC_REQUEST = 1111

        private val TAG = MainActivity::class.java.simpleName

        const val MEDIA_TYPE_IMAGE = 1
        private const val IMAGE_DIRECTORY_NAME = "Android File Upload"

        private fun getOutputMediaFile(type: Int): File? {

            val mediaStorageDir = File(
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME
            )
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(
                        TAG, "Oops! Failed create "
                                + IMAGE_DIRECTORY_NAME + " directory"
                    )
                    return null
                }
            }

            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val mediaFile: File
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = File(
                    mediaStorageDir.path + File.separator
                            + "IMG_" + ".jpg"
                )
            } else {
                return null
            }

            return mediaFile
        }

    }
    //for  ask permission
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    // on activity result usefull when user has done with action take camera or choose from gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
            if (data != null) {
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = applicationContext?.contentResolver?.query(
                    selectedImage!!,
                    filePathColumn,
                    null,
                    null,
                    null
                )
                assert(cursor != null)
                cursor!!.moveToFirst()

                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                mediaPath = cursor.getString(columnIndex)
                imageAnalitics.setImageBitmap(BitmapFactory.decodeFile(mediaPath))
                cursor.close()
                postPath = mediaPath
            }


        } else if (requestCode == CAMERA_PIC_REQUEST) {
            postPath = if (Build.VERSION.SDK_INT > 21) {

                Glide.with(this).load(mImageFileLocation).into(imageAnalitics)
                mImageFileLocation

            } else {
                Glide.with(this).load(fileUri).into(imageAnalitics)
                fileUri!!.path

            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    @Throws(IOException::class)
    internal fun createImageFile(): File {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmSS").format(Date())
        val imageFileName = "IMAGE_" + timeStamp
        val storageDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app")

        if (!storageDirectory.exists()) storageDirectory.mkdir()
        val image = File(storageDirectory, "$imageFileName.jpg")

        mImageFileLocation = image.absolutePath
        return image
    }


    private fun captureImage() {
        if (Build.VERSION.SDK_INT > 21) {
            val callCameraApplicationIntent = Intent()
            callCameraApplicationIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

            var photoFile: File? = null

            try {
                photoFile = createImageFile()
            } catch (e: IOException) {

                e.printStackTrace()
            }
            val outputUri = FileProvider.getUriForFile(
                this,
                "com.kusdianto.disasterreport1" + ".provider",
                photoFile!!
            )
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)

            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivityForResult(callCameraApplicationIntent, CAMERA_PIC_REQUEST)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

            startActivityForResult(intent, CAMERA_PIC_REQUEST)
        }


    }

    private fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    private fun uploadFile() {
        if (postPath == null || postPath == "") {
            Toast.makeText(this, "image null ", Toast.LENGTH_SHORT).show()
            return
        } else {
            val map = HashMap<String, RequestBody>()
            val file = File(postPath!!)
            val requestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
            map["file\"; filename=\"" + file.name + "\""] = requestBody
            // variabel map is file ready to upload server if server has a rule that accepted image as type file

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading")
            progressDialog.show()
        }
    }
}

