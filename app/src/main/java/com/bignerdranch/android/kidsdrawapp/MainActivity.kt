package com.bignerdranch.android.kidsdrawapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var buttonAdd: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonBlack: Button
    private lateinit var buttonBlue: Button
    private lateinit var buttonRed: Button
    private lateinit var buttonGreen: Button
    private lateinit var buttonSend: Button
    private lateinit var viewModel: DrawViewModel
    private lateinit var editText: EditText
    private var sizePaint : Array<Float> = arrayOf<Float>(20f, 10f, 5f)
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawView)
        buttonAdd = findViewById(R.id.buttonAddPhoto)
        buttonSave = findViewById(R.id.buttonSave)
        editText = findViewById(R.id.editTextText)

        buttonBlack = findViewById(R.id.buttonBlack)
        buttonBlue = findViewById(R.id.buttonBlue)
        buttonRed = findViewById(R.id.buttonRed)
        buttonGreen = findViewById(R.id.buttonGreen)

        buttonSend = findViewById(R.id.buttonShare)

        val arrayAdapter = ArrayAdapter<Float>(this, android.R.layout.simple_spinner_item, sizePaint)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //метод устанавливает ресурс макета ввыпадающего списка

        spinner = findViewById(R.id.spinner)
        spinner.adapter = arrayAdapter

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener { //устанавливает слушателя (работает при нажатии).
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int, //какую позицию выбрали.
                id: Long //индефикатор.
            ) {
                val size = parent.getItemAtPosition(position).toString().toFloat()
                //выбранный элемент из Spinner, преобразуется в строку и затем в Float.  представляет размер кисти.

                viewModel.setBrushSize(size)
                //установка размера кисти.
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })//Этот метод вызывается, если ничего не выбрано в Spinner. В
        // данном случае, он оставлен пустым, так как нет необходимости в дополнительных действиях при отсутствии выбора.

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[DrawViewModel::class.java]

        viewModel.setDrawingView(drawingView)
            // код создает экземпляр ViewModel для класса DrawViewModel и устанавливает ссылку на DrawingView для использования в приложении.

        buttonAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // Создается объект Intent, который будет использоваться для открытия галереи изображений.
            //Intent.ACTION_PICK: Это действие, которое указывает, что пользователь хочет выбрать изображение из галереи.
                //MediaStore.Images.Media.EXTERNAL_CONTENT_URI: Это URI, указывающий на внешнее хранилище устройства, где находятся изображения.

            //resultForActivity.launch(). resultForActivity -
            // это объект, который позволяет получить результат от запущенной активности (в данном случае, галереи изображений).
            resultForActivity.launch(intent)
        }

        buttonSave.setOnClickListener { //при нажатии
            val bitmap = viewModel.getBitMap() //получается битмап из ViewModel с помощью метода getBitMap().

            if (editText.text.toString() != "") { //роверяется, не пустой ли текст в текстовом поле editText.
                // текст не пустой, то выполняются следующие действия.
                if (bitmap != null) {
                    saveTheImageLegacyStyle(bitmap, editText.text.toString())
                }
            }
        }//: Вызывается метод saveTheImageLegacyStyle() с двумя параметрами: битмапом и текстом из текстового поля editText.
        // Метод saveTheImageLegacyStyle() вероятно, сохраняет битмап с указанным текстом в файл или на сервер.

        buttonSend.setOnClickListener() {
            sendImage(viewModel.getBitMap())
        }

        buttonBlack.setOnClickListener() {
            viewModel.setColor(Color.BLACK)
        }

        buttonRed.setOnClickListener() {
            viewModel.setColor(Color.RED)
        }

        buttonBlue.setOnClickListener() {
            viewModel.setColor(Color.BLUE)
        }

        buttonGreen.setOnClickListener() {
            viewModel.setColor(Color.GREEN)
        }

        viewModel.drawingView.observe(this, Observer {
            drawingView = it
        })
    }//тот код позволяет реагировать на изменения в drawingView в ViewModel и обновлять соответствующую переменную drawingView

    private val resultForActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, result.data?.data)

        viewModel.setBackGround(bitmap)
    }//Этот код использует registerForActivityResult для обработки результатов запуска активности и выбора изображения из галереи

    fun saveTheImageLegacyStyle(bitmap: Bitmap, fileName: String){ //принимает битмап и имя файла для сохранения.
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)// ссылка на папку "Изображения" на внешнем хранилище устройства, где будет сохранено изображение.

        if (!folder.exists()) {
            folder.mkdir()//есть ли папка? если нет создается.
        }

        val cachePath = File(folder, "$fileName.png")//создается путь файла.

        try {
            val ostream = FileOutputStream(cachePath)//создается поток для записи.
            bitmap.compress(CompressFormat.PNG, 100, ostream)//сжимается в пнг и уходит в поток.
            ostream.close()//поток закрывается.

            Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_LONG).show()//отображение сообщения.
        } catch (e: IOException) {
            e.printStackTrace()//если проблемы, кидает в лог.
        }
    }

    fun sendImage(bitmap: Bitmap?) {//принимает битап для отправки.
        val filePath = "${externalCacheDir?.absolutePath}/temp_image.png"//путь к папке.

        try {
            val fileOutputStream = FileOutputStream(filePath)//поток
            bitmap?.compress(CompressFormat.PNG, 100, fileOutputStream)//сжимается и кидается в поток.

            val file = File(filePath)// Создается объект File, представляющий файл, который был записан в поток.
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)//Получается URI для файла, который был записан в поток.

            val intent = Intent(Intent.ACTION_SEND)//Создается Intent для отправки файла.
            intent.type = "image/*"//какой тип файла.
            intent.putExtra(Intent.EXTRA_STREAM, uri)//обавляется URI файла в Intent для отправки.
            startActivity(Intent.createChooser(intent, "Поделиться рисунком"))//Открывается диалог для выбора приложения для поделки,
        // используя Intent для отправки файла.
        } catch (e: IOException) {
            e.printStackTrace()
        }//исключение ввода-вывода, оно перехватывается и выводится в лог с помощью e.printStackTrace().
    }
}



