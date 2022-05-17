package com.example.test30

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.chat_item_watson.view.*


class ChatAdapter(private val messageArrayList: ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    protected var activity: Activity? = null
    private val SELF = 100
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_self, parent, false)
        } else {
            // WatBot message
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_watson, parent, false)
        }
        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageArrayList[position]
        return if (message.id != null && message.id.equals("1")) {
            SELF
        } else position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageArrayList[position]
        when (message.type) {
            Message.Type.TEXT -> (holder as ViewHolder).message.setText(message.message)
            Message.Type.IMAGE -> {
                (holder as ViewHolder).message.visibility = View.GONE
                val iv = (holder as ViewHolder).itemView.image
                Glide
                    .with(iv.context)
                    .load(message.url)
                    .into(iv)
            }
        }
    }

//    override fun getItemCount(){
//        //TODO("Not yet implemented")
//        val itemCount: Int = messageArrayList.size
//    }
    override fun getItemCount() = messageArrayList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var message: TextView
//        internal var image: ImageView

        init {
            message = itemView.findViewById(R.id.message)
//            image = itemView.findViewById(R.id.image)!!

            //TODO: Uncomment this if you want to use a custom Font
            /*String customFont = "Montserrat-Regular.ttf";
            Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), customFont);
            message.setTypeface(typeface);*/
        }
    }
}