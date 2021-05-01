package korneluk.serhij.chemlabfuel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// the fragment initialization parameters
private const val GROUP_POSITION = "groupPosition"
private const val CHILD_POSITION = "childPosition"
private const val UNIT = "unit"
private const val CONSUMPTION = "consumption"
private const val JOURNAL = "journal"
private const val USER = "user"

/**
 * A simple [DialogFragment] subclass.
 * Use the [JournalDialog.getInstance] factory method to
 * get (create) an instance of this fragment.
 */
class JournalDialog : DialogFragment() {
    private lateinit var alert: AlertDialog
    private lateinit var journals: ArrayList<ArrayList<String>>
    private lateinit var listAdapter: ArrayAdapter<ArrayList<String>>
    fun updateConsumptionJournal(position: Int, t0: String, t1: String, t2: String, t3: String,
                                 t4: String, t5: String) {
        journals[position][0] = t0
        journals[position][1] = t1
        journals[position][2] = t2
        journals[position][3] = t3
        journals[position][4] = t4
        journals[position][5] = t5
        listAdapter.notifyDataSetChanged()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { activity ->
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
            journals = gson.fromJson<ArrayList<ArrayList<String>>>(arguments?.getString("journal")?: "", type)
            val builder = AlertDialog.Builder(activity)
            val linearLayout = LinearLayout(activity)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewTitle = TextView(activity)
            textViewTitle.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            textViewTitle.setPadding(10, 10, 10, 10)
            textViewTitle.text = getString(R.string.consumption_journal)
            textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textViewTitle.setTypeface(null, Typeface.BOLD)
            textViewTitle.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
            linearLayout.addView(textViewTitle)
            val listView = ListView(activity)
            listAdapter = ListAdapter(activity)
            listView.adapter = listAdapter
            listView.onItemClickListener =
                    AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?,
                                                      position: Int, _: Long ->
                val journal = gson.toJson(journals)
                val consumption: Dialog_reagent_consumption = Dialog_reagent_consumption.getInstance(
                        arguments?.getInt(GROUP_POSITION)?: 0, arguments?.getInt(CHILD_POSITION)?: 0,
                        arguments?.getInt(UNIT)?: 0, journals[position][5], journal, position
                )
                fragmentManager?.let {
                    consumption.show(it, CONSUMPTION)
                }
            }
            linearLayout.addView(listView)
            builder.setView(linearLayout)
            builder.setPositiveButton(getString(R.string.good)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            }
        }
        return alert
    }

    private inner class ListAdapter internal constructor(context: Activity) :
            ArrayAdapter<ArrayList<String>>(context, R.layout.simple_list_item2, journals) {
        private val chemLabFuel: SharedPreferences = context.getSharedPreferences("ChemLabFuel", Context.MODE_PRIVATE)
        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val root: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                root = vi.inflate(R.layout.simple_list_item2, parent, false)
                viewHolder = ViewHolder()
                root.tag = viewHolder
                viewHolder.textView = root.findViewById(R.id.label)
            } else {
                root = convertView
                viewHolder = root.tag as ViewHolder
            }
            val createBy = journals[position][5]
            var fnG = ""
            var lnG = ""
            for (i2 in ChemLabFuel.users.indices) {
                if (ChemLabFuel.users[i2][0].contains(createBy)) {
                    fnG = ChemLabFuel.users[i2][1]
                    lnG = ChemLabFuel.users[i2][2]
                    break
                }
            }
            viewHolder.textView?.text = journals[position][0] + "\n" + journals[position][1] + " " +
                    journals[2] + "\n" + getString(R.string.density) + ": " + journals[position][3] +
                    "\n" + getString(R.string.for_purpose) + ": " + journals[position][4] + "\n" +
                    getString(R.string.entry_made) + ": " + fnG + " " + lnG
            viewHolder.textView?.textSize = chemLabFuel.getInt("fontSize", 18).toFloat()
            return root
        }
    }

    private class ViewHolder {
        var textView: TextView? = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param groupPosition Group Position.
         * @param childPosition Child Position.
         * @param unit Unit.
         * @param journalText Journal Text.
         * @param user User.
         * @return A new instance of fragment JournalDialog.
         */
        @JvmStatic
        fun getInstance(groupPosition: Int, childPosition: Int, unit: Int, journalText: String?,
                        user: String?) =
            JournalDialog().apply {
                arguments = Bundle().apply {
                    putInt(GROUP_POSITION, groupPosition)
                    putInt(CHILD_POSITION, childPosition)
                    putInt(UNIT, unit)
                    putString(JOURNAL, journalText)
                    putString(USER, user)
                }
            }
    }
}