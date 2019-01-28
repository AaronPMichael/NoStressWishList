package rose.michaeap.nostresswishlist

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_item__add_.view.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Item_Add_Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Item_Add_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Item_Add_Fragment : Fragment() {
    lateinit var itemSource:ItemSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var ogi = arguments?.getParcelable<Item>(ARG_ITEM)?:null
        var view = inflater.inflate(R.layout.fragment_item__add_, container, false)
        if (ogi!=null){
            view.editName.setText(ogi.name)
            if (ogi.price!=0.0)
                view.editPrice.setText(ogi.price.toString())
            view.mult_box.isChecked = ogi.mult
            view.priority_box.isChecked = ogi.priority
            view.online_box.isChecked = ogi.online
            view.comments_field.setText(ogi.comments)
        }
        view.positive_button.setOnClickListener {
            val item = createItem(view)
            if (ogi!=null)
                item.id = ogi.id
            itemSource.closeInspect(item,ogi)
        }
        view.negative_button.setOnClickListener {
            itemSource.closeInspect(null,null)
        }
        return view
    }


    override fun onAttach(context: Context) {
        if (context is ItemSource) {
            itemSource = context as ItemSource
        }
        super.onAttach(context)

    }

    fun createItem(view:View):Item{
        val name = view.editName.text.toString()
        var price = view.editPrice.text.toString().toDoubleOrNull()
        if (price==null)
            price = 0.0
        val priority = view.priority_box.isChecked
        val mult = view.mult_box.isChecked
        val online = view.online_box.isChecked
        val comment = view.comments_field.text.toString()
        return Item(name,price,priority,mult,online,comment)
    }

    companion object {

        val ARG_ITEM = "item"

        @JvmStatic
        fun newInstance(item:Item) =
            Item_Add_Fragment().apply {
                arguments = Bundle().apply {
                    if (item!=null)
                        putParcelable(ARG_ITEM,item)
                }
            }
    }



}
