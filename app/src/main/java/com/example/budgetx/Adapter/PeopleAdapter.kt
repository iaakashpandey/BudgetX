package com.example.budgetx.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetx.Person
import com.example.budgetx.R

class PeopleAdapter(private val peopleList: List<Person>) :
RecyclerView.Adapter<PeopleAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textUserName: TextView = itemView.findViewById(R.id.textUserName)
        val textUserId: TextView = itemView.findViewById(R.id.textUserId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = peopleList[position]
        holder.textUserName.text = person.userName
        holder.textUserId.text = "ID: ${person.userId}"
    }

}