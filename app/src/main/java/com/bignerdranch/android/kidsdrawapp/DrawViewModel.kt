package com.bignerdranch.android.kidsdrawapp

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class DrawViewModel(application: Application) : AndroidViewModel(application) { //класс может использовать ресурсы андроид.
    //класс, который позволяет изменять значение, хранящееся в нем, и уведомлять наблюдателей о таких изменениях.
    val drawingView = MutableLiveData<DrawingView>()

    fun setDrawingView(view: DrawingView) {
        drawingView.value = view
    }

    fun setBackGround(bitmap: Bitmap) {
        drawingView.value?.setBackGround(bitmap)
    }
// принимает битмап и вызывает метод setBackGround на объекте DrawingView, если он существует.
    fun setBrushSize(size: Float) {
        drawingView.value?.setBrushSize(size)
    }
    //принимает размер и вызывает метод setBrushSize на объекте DrawingView, если он существует.

    fun setColor(color: Int) {
        drawingView.value?.setColor(color)
    }//принимает цвет

    fun getBitMap() : Bitmap? {
        return drawingView.value?.getBitMap()
    }//принимает битмап.
    //класс DrawViewModel служит для управления и обмена данными между приложением и объектом DrawingView.
// Он обеспечивает связь между приложением и DrawingView, позволяя изменять и получать данные из DrawingView из приложения.
}