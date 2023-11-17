package com.atilsamancioglu.yemektariflerisqlite

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_liste.*
import java.lang.Exception


class ListeFragment : Fragment() {

    var yemekIsmiListesi = ArrayList<String>()  // gelen veri array a eklendi
    var yemekIdListtesi = ArrayList<Int>()   // gelen veri array a eklendi
    private lateinit var listeAdapter : ListeRecyclerAdapter // custom view için adapter sınıfı çağırıldı oluşturulacak şimdi burda


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter = ListeRecyclerAdapter(yemekIsmiListesi,yemekIdListtesi)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = listeAdapter


        sqlVeriAlma()
    }


    fun sqlVeriAlma(){
        try {

            activity?.let {
                val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE,null)

                val cursor = database.rawQuery("SELECT * FROM yemekler",null)
                val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")  // ilglili etiketler SQL dne okunacak şekilde ayatlandı
                val yemekIdIndex = cursor.getColumnIndex("id")

                yemekIsmiListesi.clear() // önceden kalmış veirler varsa bunların silinir ve yeni veriler için lste boş hale getirilir
                yemekIdListtesi.clear()

                while(cursor.moveToNext()){  // while ile tüm satırlar okundu ve hesi array listesine eklendi.

                    yemekIsmiListesi.add(cursor.getString(yemekIsmiIndex)) // arrya a eklekniyorlar 
                    yemekIdListtesi.add(cursor.getInt(yemekIdIndex))

                }

                listeAdapter.notifyDataSetChanged()

                cursor.close()
            }




        } catch (e: Exception){

        }
    }


}
