import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetx.Person
import com.example.budgetx.R

class SplitTransactionAdapter(private var users: MutableList<Person>) :
    RecyclerView.Adapter<SplitTransactionAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.textUserName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.userName.text = users[position].userName // Use userName from Person
    }

    override fun getItemCount(): Int = users.size

    fun updateList(newUsers: MutableList<Person>) {
        users = newUsers
        notifyDataSetChanged()
    }
}





