package com.hour.uigithub.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hour.uigithub.Category
import com.hour.uigithub.DetailActivity
import com.hour.uigithub.MainRvAdapter
import com.hour.uigithub.R
import kotlinx.android.synthetic.main.content_list.*

class ListFragment : Fragment() {

    companion object{
        val INTENT_PARCELABLE = "OBJECT_INTENT"
    }

    var categoryList = arrayListOf<Category>(
        Category(R.drawable.workout, "운동"),
        Category(R.drawable.study, "공부"),
        Category(R.drawable.music, "음악" )


    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mAdapter = MainRvAdapter(this.requireContext(), categoryList) {
                dog ->

        }
        recycler_view.adapter = mAdapter

        val lm = GridLayoutManager(this.requireContext(),1)
        //LayoutManager는 RecyclerView의 각 item들을 배치하고,
        // item이 더이상 보이지 않을 때 재사용할 것인지 결정하는 역할을 한다
        recycler_view.layoutManager = lm
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = MainRvAdapter(this.requireContext(), categoryList){
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(INTENT_PARCELABLE,it)
                startActivity(intent)
        }

    }

}
