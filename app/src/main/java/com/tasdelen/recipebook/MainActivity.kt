package com.tasdelen.recipebook

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createOrOpenDb()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipe_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.addRecipeItem &&
            findNavController(R.id.fragmentContainerView).currentDestination?.id == R.id.listFragment)
        {
            Navigation.findNavController(this, R.id.fragmentContainerView).navigate(
                ListFragmentDirections.actionListFragmentToAddFragment(-1)
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createOrOpenDb() {
        val db : SQLiteDatabase = this.openOrCreateDatabase(
            "food",
            Context.MODE_PRIVATE,
            null
        )

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS food (" +
                    "id INTEGER PRIMARY KEY," +
                    " name VARCHAR," +
                    " ingredients VARCHAR," +
                    " image BLOB" +
                    " )"
        )
    }







}