package korneluk.serhij.chemlabfuel

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

private const val GROUP_POSITION = "groupPosition"
private const val CHILD_POSITION = "childPosition"
private const val NAME = "name"

/**
 * A simple [DialogFragment] subclass.
 * Use the [ReagentsContextMenuDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReagentsContextMenuDialog : DialogFragment() {
    private lateinit var alertDialog: AlertDialog

    interface ReagentsContextMenuDialogListener {
        fun onAddLotDialog(groupPosition: Int)
        fun onConsumptionDialog(groupPosition: Int, childPosition: Int)
        fun onJournalDialog(groupPosition: Int, childPosition: Int)
        fun onEditDialog(groupPosition: Int, childPosition: Int)
        fun onRemoveDialog(groupPosition: Int, childPosition: Int)
    }

    private var mListener: ReagentsContextMenuDialogListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as ReagentsContextMenuDialogListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement ReagentsContextMenuDialogListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val builder = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewTitle = TextView(it)
            textViewTitle.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            textViewTitle.setPadding(10, 10, 10, 10)
            textViewTitle.text = arguments?.getString(NAME, "")?: ""
            textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textViewTitle.setTypeface(null, Typeface.BOLD)
            textViewTitle.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewTitle)
            val textView = TextView(it)
            textView.setPadding(10, 20, 10, 20)
            textView.setText(R.string.add_lot)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            textView.setOnClickListener {
                dialog?.cancel()
                mListener?.onAddLotDialog(arguments?.getInt(GROUP_POSITION, 0)?: 0)
            }
            linearLayout.addView(textView)
            val textView2 = TextView(it)
            textView2.setPadding(10, 20, 10, 20)
            textView2.setText(R.string.consumption)
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textView2.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            textView2.setOnClickListener {
                dialog?.cancel()
                mListener?.onConsumptionDialog(arguments?.getInt(GROUP_POSITION, 0)?: 0,
                        arguments?.getInt(CHILD_POSITION, 0)?: 0)
            }
            linearLayout.addView(textView2)
            val textView3 = TextView(it)
            textView3.setPadding(10, 20, 10, 20)
            textView3.setText(R.string.consumption_journal)
            textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textView3.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            textView3.setOnClickListener {
                dialog?.cancel()
                mListener?.onJournalDialog(arguments?.getInt(GROUP_POSITION, 0)?: 0,
                        arguments?.getInt(CHILD_POSITION, 0)?: 0)
            }
            linearLayout.addView(textView3)
            val textView4 = TextView(it)
            textView4.setPadding(10, 20, 10, 20)
            textView4.setText(R.string.edit)
            textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textView4.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            textView4.setOnClickListener {
                dialog?.cancel()
                mListener?.onEditDialog(arguments?.getInt(GROUP_POSITION, 0)?: 0,
                        arguments?.getInt(CHILD_POSITION, 0)?: 0)
            }
            linearLayout.addView(textView4)
            val textView5 = TextView(it)
            textView5.setPadding(10, 20, 10, 20)
            textView5.setText(R.string.delete)
            textView5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textView5.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            textView5.setOnClickListener {
                dialog?.cancel()
                mListener?.onRemoveDialog(arguments?.getInt(GROUP_POSITION, 0)?: 0,
                        arguments?.getInt(CHILD_POSITION, 0)?: 0)
            }
            linearLayout.addView(textView5)
            builder.setView(linearLayout)
            alertDialog = builder.create()
        }
        return alertDialog
    }

    companion object {
        /**
         * Use this factory method to get a new instance of
         * this fragment using the provided parameters.
         *
         * @param groupPosition Parameter 1.
         * @param childPosition Parameter 2.
         * @param name Parameter 3.
         * @return A new instance of fragment ReagentsContextMenuDialog.
         */
        @JvmStatic
        fun getInstance(groupPosition: Int, childPosition: Int, name: String?): ReagentsContextMenuDialog {
            val instance = ReagentsContextMenuDialog()
            val args = Bundle()
            args.putInt(GROUP_POSITION, groupPosition)
            args.putInt(CHILD_POSITION, childPosition)
            args.putString(NAME, name)
            instance.arguments = args
            return instance
        }
    }
}