package com.atilsamancioglu.yemektariflerisqlite

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_tarif.*
import java.io.ByteArrayOutputStream
import java.lang.Exception


class TarifFragment : Fragment() {

    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            kaydet(it)
        }

        imageView.setOnClickListener {
            gorselSec(it)
        }

        arguments?.let { //ayarladığımız menudengeldim vb boolen verileri argumentsleri kontrol ediyoruz

            var gelenBilgi = TarifFragmentArgs.fromBundle(it).bilgi

            if (gelenBilgi.equals("menudengeldim")){//equals yerine == diye kontrol edebilrisnin
                //yeni bir yemek eklemeye geldi - - soru isteme işlemlerini yapacak şekilde görüntüle
                yemekIsmiText.setText("")
                yemekMalzemeText.setText("")
                button.visibility = View.VISIBLE

                val gorselSecmeArkaPlani = BitmapFactory.decodeResource(context?.resources,R.drawable.gorselsecimi)
                imageView.setImageBitmap(gorselSecmeArkaPlani) // görsel seç png yi gösteriyor şuan

            } else {
                //daha önce oluşturulan yemeği görmeye geldi
                button.visibility = View.INVISIBLE

                val secilenId = TarifFragmentArgs.fromBundle(it).id

                context?.let {

                    try {

                        val db = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM yemekler WHERE id = ?", arrayOf(secilenId.toString()))  //seçilen listview id si string yapıldı ve ona ait veriler çekildi

                        val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex = cursor.getColumnIndex("yemekmalzemesi")
                        val yemekGorseli = cursor.getColumnIndex("gorsel")

                        while(cursor.moveToNext()){
                            yemekIsmiText.setText(cursor.getString(yemekIsmiIndex))
                            yemekMalzemeText.setText(cursor.getString(yemekMalzemeIndex))

                            val byteDizisi = cursor.getBlob(yemekGorseli) //görsel byte dizisi veri olarak geldi tekrar png jpg hale getiriyoruz burda.
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            imageView.setImageBitmap(bitmap)  // veri imageVİew e verildi
                        }

                        cursor.close()

                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    fun kaydet(view: View){
        //SQLite'a Kaydetme
        val yemekIsmi = yemekIsmiText.text.toString()
        val yemekMalzemeleri = yemekMalzemeText.text.toString()

        if (secilenBitmap != null) {

            val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)

            val outputStream = ByteArrayOutputStream()         // resim veri haline getirildi ve kaydedilmeye hazır artık
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()

            try{
                context?.let {
                    val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler (id INTEGER PRIMARY KEY, yemekismi VARCHAR, yemekmalzemesi VARCHAR, gorsel BLOB)")
                    //database sqlLit da bu datrıla oluşturuldu ilgili veri satırları girildi
                    
                    val sqlString = "INSERT INTO yemekler (yemekismi, yemekmalzemesi, gorsel) VALUES (?, ?, ?)" // buaradki soru işareti olan yerlere yazılacak veriler girilecek 
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,yemekIsmi) //1.soru işaretine denk gelen veri
                    statement.bindString(2,yemekMalzemeleri) //2.soruişaretine denk gelen veri
                    statement.bindBlob(3,byteDizisi)  // 3 . soru işaretine denk gelen veri
                    statement.execute()  //işlem bitti yazıldı veri taabınna

                }

            } catch (e: Exception){
                e.printStackTrace()
            }

            val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action) // işlem bitti diğer fragmente geri dönüldü

        }

    }

    fun gorselSec(view: View){

        activity?.let {
            if (ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin verilmedi, izin istememiz gerekiyor
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            } else {
                //izin zaten verilmiş, tekrar istemeden galeriye git
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,  //array olarak izin istendikullanıcıdan
        grantResults: IntArray
    ) {
        if (requestCode == 1){

            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //izni aldık
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){

            secilenGorsel = data.data

            try {

                context?.let {
                    if(secilenGorsel != null) {
                        if( Build.VERSION.SDK_INT >= 28){  //SDK28 yani android 9 üzeri için alttki fonk uygulanırken 
                            val source = ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                        } else {   // API SDk 28 altı için bu fonk uygulanır kullanıcı telefonununun API değrine göre işke yapılır
                            secilenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)
                        }

                    }
                }


            } catch (e: Exception){
                e.printStackTrace()
            }


        }



        super.onActivityResult(requestCode, resultCode, data)
    }

    fun kucukBitmapOlustur(kullanicininSectigiBitmap: Bitmap, maximumBoyut: Int) : Bitmap {

        var width = kullanicininSectigiBitmap.width
        var height = kullanicininSectigiBitmap.height

        val bitmapOrani : Double = width.toDouble() / height.toDouble()

        if (bitmapOrani > 1) {
            // görselimiz yatay
            width = maximumBoyut
            val kisaltilmisHeight = width / bitmapOrani
            height = kisaltilmisHeight.toInt()
        } else {
            //görselimiz dikey
            height = maximumBoyut
            val kisaltilmisWidth = height * bitmapOrani
            width = kisaltilmisWidth.toInt()

        }

        //görsel boyutu 1mb altına düşüldü
        return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)
    }


}
