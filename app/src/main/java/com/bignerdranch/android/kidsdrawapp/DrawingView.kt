package com.bignerdranch.android.kidsdrawapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
   //Конструктор класса DrawingView принимает два параметра: Параметр context представляет контекст,
    // в котором будет использоваться DrawingView, а attrs представляет набор атрибутов

    private lateinit var mDrawPath : CustomPath // переменная, для траектории класса (отображение пути)
    private lateinit var mCanvasBitmap : Bitmap // Побитовая разметка области рисования
    private lateinit var mDrawPaint : Paint // Отвечает за хранение характеристик рисунка.
    private lateinit var mCanvasPaint : Paint // Отвечает за хранение характеристик области рисования
    private var mBrushSize : Float // отвечает за размер маркера
    private var color : Int // цвет маркера
    private lateinit var canvas : Canvas // отвечает за инициализацию графической области рисования
    private var mBackgroundBitMap : Bitmap? = null // проверка значения? Оно пустое или нет. (еще пустое).
    private var mPath = ArrayList<CustomPath>() // список нарисованных штучек.

    init { //выполняется при создании экзмелпяра класса.
        mBrushSize = 20f
        color = Color.BLACK
        setUpDrawing() //для подготовки к рисованию.
    }

    internal class CustomPath(color: Int, brushThickness : Float) : Path() { }
    //внутренний класс , от графического модуля Path, создали цвета и толщину линии.

    fun setBackGround(bitmap: Bitmap) { //установка фонового изображения.
        mBackgroundBitMap = Bitmap.createScaledBitmap(bitmap, canvas.width, canvas.height, true) //подгон bitman под рамки изображения.
        invalidate()
    }

    fun setColor(colorOut: Int) {
        color = colorOut
        mDrawPaint.color = color
    }

    fun setBrushSize(size: Float) {
        mBrushSize = size
        mDrawPaint.strokeWidth = mBrushSize
    }

    fun getBitMap() : Bitmap {
        val mergedBitmap = Bitmap.createBitmap( //создает bitmap bиз данных.
            mCanvasBitmap.getWidth(),
            mCanvasBitmap.getHeight(),
            mCanvasBitmap.getConfig()
        )

        val canvasBitMap = Canvas(mergedBitmap)
        canvasBitMap.drawBitmap(mCanvasBitmap, Matrix(), null)//рисуется mCanvasBitmap на CanvasBitmap
            //матрица нужна для отображения данных.

        canvasBitMap.drawBitmap(mBackgroundBitMap!!, matrix, null)
        canvasBitMap.drawBitmap(mCanvasBitmap, Matrix(), null)

        return mergedBitmap
            //происходит обединение Bitmapов на одном холсте.
    }

    fun setUpDrawing() {
        //функция, отвечающая за настройку параметров рисования
        mDrawPath = CustomPath(color, mBrushSize) //тип Path и состоит из траектории и цвета.
        mDrawPaint = Paint() //тип Paint используется для рисования.
        mDrawPaint.style = Paint.Style.STROKE //устанавливает стиль . Stroke - рисование по контуру фигур.
        mDrawPaint.color = color //цвет рисования
        mDrawPaint.strokeJoin = Paint.Join.ROUND //делает в рисовании закругленные углы везде
        mDrawPaint.strokeCap = Paint.Cap.ROUND // закгругленные углы в конце.
        mDrawPaint.strokeWidth = mBrushSize //толщина линии
        mCanvasPaint = Paint()
        mCanvasPaint.flags = Paint.DITHER_FLAG //уcтанавливается флаг DITHER_FLAG, который улучшает качество рисования путем сглаживания цветов при переходе между ними.
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //Это объявление метода onSizeChanged(), который переопределяет (override) метод из родительского класса.
        //Метод принимает четыре параметра:
        //w: новая ширина представления
        //h: новая высота представления
        //oldw: предыдущая ширина представления
        //oldh: предыдущая высота представления
        super.onSizeChanged(w, h, oldw, oldh)
        //Вызывается реализация метода onSizeChanged().

        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        //Создается новый объект Bitmap с размерами, соответствующими новому размеру представления (w и h).
        //Битмап создается с конфигурацией ARGB_8888, что означает, чтоый пиксель представлен 32-битным значением, включающим 8 бит для каждого из четырех компонентов: альфа, красный, зеленый и сини
        // каждй.

        canvas = Canvas(mCanvasBitmap)
        //код обновляет размер битмапа и связанного с ним холста (Canvas)
    // при изменении размера представления DrawingView. Это необходимо, чтобы обеспечить корректное отображение рисунка на новом размере экрана.
    }

    override fun onDraw(canvas: Canvas) { //Метод принимает один параметр canvas типа Canvas, который представляет собой холст, на котором будет происходить рисование
        super.onDraw(canvas)
//Вызывается реализация метода onDraw() из родительского класса
        canvas.save()

        if (mBackgroundBitMap != null) {
            canvas.drawBitmap(mBackgroundBitMap!!, 0f, 0f, mCanvasPaint)
        }
//Если переменная mBackgroundBitMap не равна null, то на холст canvas рисуется битмап mBackgroundBitMap с помощью метода drawBitmap().
// Битмап рисуется в позиции (0, 0) с использованием объекта mCanvasPaint.

        for (path in mPath) {//Для каждого элемента в списке mPath (который, вероятно,
            // содержит траектории рисования) вызывается метод drawPath() холста canvas, чтобы нарисовать эти траектории с использованием объекта mDrawPaint.
            canvas.drawPath(path, mDrawPaint)
            //На холст canvas рисуется битмап mCanvasBitmap в позиции (0, 0) с использованием объекта mCanvasPaint.
            //Восстанавливается предыдущее состояние холста canvas, которое было сохранено ранее с помощью canvas.save().
        }
        canvas.drawBitmap(mCanvasBitmap, 0f, 0f, mCanvasPaint)
        canvas.restore() //Восстанавливается предыдущее состояние холста canvas, которое было сохранено ранее с помощью canvas.save().
    }

    override fun onTouchEvent(event: MotionEvent): Boolean { //обработчик событий касания экрана.
        val current = PointF(event.x, event.y) //Создается объект PointF, который содержит координаты текущей точки касания.

        when (event.action) {  //для обработки различных типов событий касания.
            MotionEvent.ACTION_DOWN -> { //Это событие происходит, когда пользователь начинает касаться экрана,
                // то есть когда палец или стилус первого касания сенсорной поверхности.
                mDrawPath = CustomPath(color, mBrushSize) //Создается новый объект CustomPath с заданным цветом (color) и размером кисти

                mDrawPath.moveTo(current.x, current.y)// Перемещает начальную точку пути в текущую точку касания
                // (current.x и current.y). Это означает, что рисование начинается с этой точки.
                mPath.add(mDrawPath)//Добавляет созданный путь (mDrawPath) в список всех путей (mPath).
            // Это означает, что путь будет сохранен и может быть использован для рисования.
            }
            MotionEvent.ACTION_MOVE -> {//перемещение пальца.
                mDrawPath.lineTo(current.x, current.y)//: Эта строка добавляет линию от предыдущей точки до
            // текущей точки касания (current.x, current.y) в текущий путь рисования (mDrawPath).

            }
            MotionEvent.ACTION_UP -> { //Когда пользователь поднимает палец, событие ACTION_UP срабатывает, и в этом блоке кода происходит обработка этого события.
                canvas.drawPath(mDrawPath, mDrawPaint)// Здесь текущий путь mDrawPath рисуется на холсте (canvas) с использованием заданной краски
                mDrawPath.reset()//После отрисовки пути на холсте, текущий путь сбрасывается, чтобы быть готовым для нового рисования.
            }
            else -> {
                return false
            }//В случае, если событие не является ACTION_DOWN, ACTION_MOVE или ACTION_UP, возвращается false, чтобы указать, что событие не было обработано.
        }

        invalidate()// очистка памяти приложения от рисования
        return true// метод onTouchEvent() успешно обработал событие и необходимо предотвратить дальнейшую обработку этого события другими обработчиками.
    }//Таким образом, эти строки завершают обработку события касания, обновляют экран для отображения изменений и сообщают системе, что событие было успешно обработано.
}

