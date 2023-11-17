package com.atilsamancioglu.yemektariflerisqlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListeRecyclerAdapter(val yemekListesi: ArrayList<String>, val idListesi : ArrayList<Int>) : RecyclerView.Adapter<ListeRecyclerAdapter.YemekHolder>() {
//adapter sınıfı oluturuldu, 
    class YemekHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false) //recycler_row xmlden çağırıldı ve ilgili görünrtü sınıfı olarak döndürüldü unutma ekledğin grüntü sınıfı custmo olan xml de
        return YemekHolder(view)
    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) { //customdaki doldurulacak yerlere erişim sağlayaıp verileri eklyioruz
        holder.itemView.recycler_row_text.text = yemekListesi[position]  //row_text ile custom viewer a ulaşıp oralara gelecek olan veriler işleniyor gönderiliyor  listwiew de gösterilmesi için
        holder.itemView.setOnClickListener {     // arrayliste e ilgili position a yerleştiriliyorlar listwiev de
            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment("recyclerdangeldim",idListesi[position])
            Navigation.findNavController(it).navigate(action)
        }
    }
}
