package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.Question
import kotlinx.android.synthetic.main.choice_numbers_layout.view.*

class ChoicesAdapter(private val listener:ChoiceClickListener) : RecyclerView.Adapter<ChoicesAdapter.MyViewHolder>() {

    lateinit var context : Context
    private var numbers = ArrayList<Int>()
    private var question = ArrayList<Question>()

    fun setdata(question: ArrayList<Question>, numbers:ArrayList<Int>){
        this.numbers = numbers
        this.question = question
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.choice_numbers_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return question.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(position: Int){
            itemView.numberbtn.text = (position+1).toString()

            itemView.numberbtn.setOnClickListener {
                listener.onChoiceClicked(position)
            }

            for (i in numbers){
                if (position == i){
                    itemView.numberbtn.background = context.getDrawable(R.drawable.button_rounded_green_background)
                }
            }
        }
    }

    interface ChoiceClickListener{
        fun onChoiceClicked(pos: Int)
    }

}
