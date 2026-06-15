package com.example.aceinc

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private var list: MutableList<ExpenseModel>,
    private val db: DatabaseHelper,
    private val onDelete: () -> Unit,              //  callback
    private val onEdit: (ExpenseModel) -> Unit     //  callback with data
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView = view.findViewById(R.id.cardAmount)
        val title: TextView = view.findViewById(R.id.cardTitle)
        val category: TextView = view.findViewById(R.id.cardCategory)
        val date: TextView = view.findViewById(R.id.cardDate)
        val deleteBtn: Button = view.findViewById(R.id.deleteBtn)
        val editBtn: Button = view.findViewById(R.id.editBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val exp = list[position]

        holder.amount.text = "R ${exp.amount}"
        holder.title.text = exp.title
        holder.category.text = exp.category
        holder.date.text = exp.date

        //  DELETE
        holder.deleteBtn.setOnClickListener {

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Expense")
                .setMessage("You sure you want to delete this?")
                .setPositiveButton("Yes") { _, _ ->

                    val success = db.deleteExpense(exp.id)

                    if (success) {
                        val pos = holder.adapterPosition
                        if (pos != RecyclerView.NO_POSITION) {
                            list.removeAt(pos)
                            notifyItemRemoved(pos)

                            onDelete() //  refresh list in activity
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }


        holder.editBtn.setOnClickListener {
            onEdit(exp) //  send full object
        }
    }
}