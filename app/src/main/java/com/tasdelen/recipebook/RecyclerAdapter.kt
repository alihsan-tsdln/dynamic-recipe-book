package com.tasdelen.recipebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class RecyclerAdapter(val recipeNames : ArrayList<String>, val recipeIds : ArrayList<Int>) :  RecyclerView.Adapter<RecyclerAdapter.RecipeVH>(){
    class RecipeVH(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return RecipeVH(itemView)
    }

    override fun onBindViewHolder(holder: RecipeVH, position: Int) {
        holder.itemView.textView.text = recipeNames.get(position)
        holder.itemView.textView.setOnClickListener {
            Navigation.findNavController(it).navigate(ListFragmentDirections.actionListFragmentToAddFragment(recipeIds.get(position)))
        }
    }

    override fun getItemCount(): Int {
        return recipeNames.size
    }
}