package edu.reaction;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class GameView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint paint, mBitmapPaint;
    private float canvasSize;
    private final int horizontalCountOfCells, verticalCountOfCells;

    private final ScaleGestureDetector scaleGestureDetector;
    private final int viewSize;
    private float mScaleFactor;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //размер игрового поля
        horizontalCountOfCells =10;
        verticalCountOfCells =10;
        //в xml разметке позднее пропишем размер вьюхи равный 300dp
        viewSize=(int)convertDpToPixel(300, context);
        mScaleFactor=1f;//значение зума по умолчанию
        canvasSize=(int)(viewSize*mScaleFactor);//определяем размер канваса

        mBitmap = Bitmap.createBitmap((int) canvasSize, (int) canvasSize, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        //определяем параметры кисти, которой будем рисовать сетку и атомы
        paint =new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xffff0505);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        //рисуем сетку
        for(int x=0;x< horizontalCountOfCells +1;x++)
            mCanvas.drawLine((float)x* canvasSize / horizontalCountOfCells, 0, (float)x* canvasSize / horizontalCountOfCells, canvasSize, paint);
        for(int y=0;y< verticalCountOfCells +1;y++)
            mCanvas.drawLine(0, (float)y* canvasSize / verticalCountOfCells, canvasSize, (float)y* canvasSize / verticalCountOfCells, paint);

        scaleGestureDetector=new ScaleGestureDetector(context, new MyScaleGestureListener());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);//зумируем канвас
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    //в случае касания пальем передаем управление MyScaleGestureListener
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    //переводим dp в пиксели
    public float convertDpToPixel(float dp,Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi/160f);
    }

    //унаследовались от ScaleGestureDetector.SimpleOnScaleGestureListener, чтобы не писать пустую реализацию ненужных методов интерфейса OnScaleGestureListener
    private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        //обрабатываем "щипок" пальцами
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor=scaleGestureDetector.getScaleFactor();//получаем значение зума относительно предыдущего состояния
            //получаем координаты фокальной точки - точки между пальцами
            float focusX=scaleGestureDetector.getFocusX();
            float focusY=scaleGestureDetector.getFocusY();
            //следим чтобы канвас не уменьшили меньше исходного размера и не допускаем увеличения больше чем в 2 раза
            if(mScaleFactor*scaleFactor>1 && mScaleFactor*scaleFactor<2){
                mScaleFactor *= scaleGestureDetector.getScaleFactor();
                canvasSize =viewSize*mScaleFactor;//изменяем хранимое в памяти значение размера канваса
                //используется при расчетах
                //по умолчанию после зума канвас отскролит в левый верхний угол. Скролим канвас так, чтобы на экране оставалась обасть канваса, над которой был
                //жест зума
                //Для получения данной формулы достаточно школьных знаний математики (декартовы координаты).
                int scrollX=(int)((getScrollX()+focusX)*scaleFactor-focusX);
                scrollX=Math.min( Math.max(scrollX, 0), (int) canvasSize -viewSize);
                int scrollY=(int)((getScrollY()+focusY)*scaleFactor-focusY);
                scrollY=Math.min( Math.max(scrollY, 0), (int) canvasSize -viewSize);
                scrollTo(scrollX, scrollY);
            }
            //вызываем перерисовку принудительно
            invalidate();
            return true;
        }
    }
}
