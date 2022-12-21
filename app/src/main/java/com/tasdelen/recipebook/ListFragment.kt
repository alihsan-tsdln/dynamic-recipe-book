package com.tasdelen.recipebook

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.view.*

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.adapter = RecyclerAdapter(takeNamesDb(), takeIdsDb())
        view.recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun takeNamesDb() : ArrayList<String>{
        val names: ArrayList<String> = arrayListOf()

        try {
            context?.let {
                val db: SQLiteDatabase = it.openOrCreateDatabase(
                    "food",
                    Context.MODE_PRIVATE,
                    null
                )
                val cursor = db.rawQuery("SELECT name FROM food", null)
                val nameList = cursor.getColumnIndex("name")


                while (cursor.moveToNext()) {
                    names.add(cursor.getString(nameList))
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return names
    }

    private fun takeIdsDb() : ArrayList<Int>{
        val ids: ArrayList<Int> = arrayListOf()

        try {
            context?.let {
                val db: SQLiteDatabase = it.openOrCreateDatabase(
                    "food",
                    Context.MODE_PRIVATE,
                    null
                )
                val cursor = db.rawQuery("SELECT id FROM food", null)
                val idList = cursor.getColumnIndex("id")


                while (cursor.moveToNext()) {
                    ids.add(cursor.getInt(idList))
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return ids
    }

}