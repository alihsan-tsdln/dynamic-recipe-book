package com.tasdelen.recipebook

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.scale
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.recycler_row.view.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class AddFragment : Fragment() {

    var photoUri : Uri? = null
    var photoBitmap : Bitmap? = null

    private var mediaTrig = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.data != null && it.resultCode == Activity.RESULT_OK) {
            photoUri = it.data!!.data
            try {
                context?.let {
                    if(photoUri != null) {
                        if(Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(it.contentResolver, photoUri!!)
                            photoBitmap = ImageDecoder.decodeBitmap(source)
                        } else {
                            photoBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, photoUri)
                        }
                    }
                }
                photoBitmap?.let {
                    photoBitmap = scaleBitmap(it, 100)
                }
                addImageButton.setImageBitmap(photoBitmap)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    private var requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if(it){
            activity?.let{
                val mediaIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                mediaTrig.launch(mediaIntent)
            }
        }else {

            if(!shouldShowRequestPermissionRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )) {
                //Snackbar learn
                view?.let{
                    Snackbar.make(it, "Permission Required for App Functionality", Snackbar.LENGTH_SHORT)
                        .setAction("Settings") {
                            val intent = Intent()
                            activity?.let {
                                intent.data = Uri.parse("package:" + it.packageName)
                            }
                            intent.action = Uri.decode(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            startActivity(intent)
                        }.show()
                }
            }
        }
    }

    private fun requestStoragePermission() {
        requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controlRecycler()

        addImageButton.setOnClickListener {
            addImage(it)
        }

        saveButton.setOnClickListener {
            saveRecipe(it)
        }
    }

    private fun controlRecycler(){
        arguments?.let {
            val id : Int = AddFragmentArgs.fromBundle(it).recipeId
            if(id == -1) {
                return
            }
            context?.let {
                val db : SQLiteDatabase = it.openOrCreateDatabase(
                    "food",
                    Context.MODE_PRIVATE,
                    null
                )
                val cursor = db.rawQuery("SELECT name, ingredients, image FROM food WHERE id = $id", null)
                val nameList = cursor.getColumnIndex("name")
                val ingList = cursor.getColumnIndex("ingredients")
                val imageList = cursor.getColumnIndex("image")

                if(cursor.moveToNext())
                {
                    view?.let {
                        it.recipeNameEditText.setText(cursor.getString(nameList))
                        it.ingredientsEditText.setText(cursor.getString(ingList))
                        val img : ByteArray = cursor.getBlob(imageList)
                        it.addImageButton.setImageBitmap(BitmapFactory.decodeByteArray(
                            img, 0, img.size
                        ))
                    }

                }
            }
        }
    }


    fun saveRecipe(view: View) {
        if (photoBitmap != null && recipeNameEditText.text.isNotEmpty() && ingredientsEditText.text.isNotEmpty())  {
            val outputStream = ByteArrayOutputStream()
            photoBitmap!!.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val imgsource : ByteArray = outputStream.toByteArray()

            try {
                context?.let {
                    val db : SQLiteDatabase = it.openOrCreateDatabase(
                        "food",
                        Context.MODE_PRIVATE,
                        null
                    )

                    val query = db.compileStatement("INSERT INTO food (name, ingredients, image) VALUES (?, ?, ?)")
                    query.bindString(1, recipeNameEditText.text.toString())
                    query.bindString(2, ingredientsEditText.text.toString())
                    query.bindBlob(3, imgsource)
                    query.execute()
                }

                view?.let {
                    Navigation.findNavController(it).navigate(AddFragmentDirections.actionAddFragmentToListFragment())
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addImage(view : View) {
        requestStoragePermission()
    }

    private fun scaleBitmap(img : Bitmap, max : Int) : Bitmap {
        var height = img.height
        var width = img.width
        val ratio : Double = height.toDouble() / width.toDouble()
        if(ratio > 1) {
            height = max
            width = (max / ratio).toInt()
        } else {
            width = max
            height = (max * ratio).toInt()
        }
        return img.scale(height, width)
    }
}














