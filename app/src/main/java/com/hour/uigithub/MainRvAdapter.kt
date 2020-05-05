package com.hour.uigithub

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hour.uigithub.goalMain.TimerActivity
import java.util.*
import kotlin.collections.ArrayList

class MainRvAdapter(val context: Context, val categoryList: ArrayList<Category>, val itemClick: (Category) -> Unit) :
    RecyclerView.Adapter<MainRvAdapter.Holder>() {

    // <> 안에 MainRvAdapter.Holder 를 입력한 이유는 inner class로 만들었기 때문이다.
    inner class Holder(itemView: View?,itemClick: (Category) -> Unit) : RecyclerView.ViewHolder(itemView!!){
        val categoryPhoto = itemView?.findViewById<ImageView>(R.id.category_image)
        val categoryTitle = itemView?.findViewById<TextView>(R.id.category_title)


        // ViewHolder 와 클래스의 각 변수를 연동하는 역할
        // 쉽게 말해 이쪽 TextView엔 이 String을 넣어라, 라고 지정하는 함수
        fun bind(category:Category, context: Context){

            categoryPhoto?.setImageResource(category.photo)
            /* 나머지 TextView와 String 데이터를 연결한다.*/
            categoryTitle?.text = category.title


            itemView.setOnClickListener {
                if(position == 0){
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }
                //TODO: MainActivity 가 아니고 다른 액티비티 띄우기
                if(position == 1){
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }
                if(position == 2){
                    val intent = Intent(context, TimerActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        // 화면을 최초 로딩하여 만들어진 View 가 없는 경우,
        // xml 파일을 inflate 하여 ViewHolder 를 생성한다.
        val view = LayoutInflater.from(context).inflate(R.layout.main_rv_item,parent,false)
        return Holder(view,itemClick)
    }

    override fun getItemCount(): Int {
        //RecyclerView 로 만들어지는 item 의 총 개수를 반환한다.
        return categoryList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // 위의 onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터를 연결한다.
        holder.bind(categoryList[position],context)

    }

}