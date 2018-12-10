package com.example.wayne.usernew;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class chartMain extends Activity {
    private LineChart mChart;
    String h="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        h = bundle.getString("c" );
        setContentView(R.layout.chartlayout);
        mChart = (LineChart) findViewById(R.id.chart);
        mChart.setDescription("Zhang Phil @ http://blog.csdn.net/zhangphil");
        mChart.setNoDataTextDescription("暫時尚無資料");
        mChart.setTouchEnabled(true);
// 可拖曳
        mChart.setDragEnabled(true);
// 可縮放
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
// 設定圖表的背景顏色
        mChart.setBackgroundColor(Color.LTGRAY);
        LineData data = new LineData();
// 資料顯示的顏色
        data.setValueTextColor(Color.WHITE);
// 先增加一個空的資料，隨後往裡面動態新增
        mChart.setData(data);
// 圖表的註解(只有當資料集存在時候才生效)
        Legend l = mChart.getLegend();
// 可以修改圖表註解部分的位置
// l.setPosition(LegendPosition.LEFT_OF_CHART);
// 線性，也可是圓
        l.setForm(LegendForm.LINE);
// 顏色
        l.setTextColor(Color.WHITE);
// x座標軸
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
// 幾個x座標軸之間才繪製？
        xl.setSpaceBetweenLabels(10);
// 如果false，那麼x座標軸將不可見
        xl.setEnabled(true);
// 將X座標軸放置在底部，預設是在頂部。
        xl.setPosition(XAxisPosition.BOTTOM);
// 圖表左邊的y座標軸線
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
// 最大值
        leftAxis.setAxisMaxValue(40f);
// 最小值
        leftAxis.setAxisMinValue(0f);
// 不一定要從0開始
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = mChart.getAxisRight();
// 不顯示圖表的右邊y座標軸線
        rightAxis.setEnabled(false);
// 每點選一次按鈕，增加一個點
        Button addButton = (Button) findViewById(R.id.button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<10;i++) {
                    addEntry(i*2);
                }
            }
        });
    }
    // 新增進去一個座標點
    private void addEntry(int i) {

        LineData data = mChart.getData();
// 每一個LineDataSet代表一條線，每張統計圖表可以同時存在若干個統計折線，這些折線像陣列一樣從0開始下標。
// 本例只有一個，那麼就是第0條折線
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
// 如果該統計折線圖還沒有資料集，則建立一條出來，如果有則跳過此處程式碼。
        if (set == null) {
            set = createLineDataSet();
            data.addDataSet(set);
        }
// 先新增一個x座標軸的值
// 因為是從0開始，data.getXValCount()每次返回的總是全部x座標軸上總數量，所以不必多此一舉的加1
        data.addXValue((data.getXValCount())+ "");
// 生成隨機測試數
        String n = h.substring(i,i+2);
        float f = Float.parseFloat(n);
// set.getEntryCount()獲得的是所有統計圖表上的資料點總量，
// 如從0開始一樣的陣列下標，那麼不必多次一舉的加1
        Entry entry = new Entry(f, set.getEntryCount());
// 往linedata裡面新增點。注意：addentry的第二個引數即代表折線的下標索引。
// 因為本例只有一個統計折線，那麼就是第一個，其下標為0.
// 如果同一張統計圖表中存在若干條統計折線，那麼必須分清是針對哪一條（依據下標索引）統計折線新增。
        data.addEntry(entry, 0);
// 像ListView那樣的通知資料更新
        mChart.notifyDataSetChanged();
// 當前統計圖表中最多在x軸座標線上顯示的總量
        mChart.setVisibleXRangeMaximum(10);
// y座標軸線最大值
// mChart.setVisibleYRange(30, AxisDependency.LEFT);
// 將座標移動到最新
// 此程式碼將重新整理圖表的繪圖
        mChart.moveViewToX(data.getXValCount() - 5);
// mChart.moveViewTo(data.getXValCount()-7, 55f,
// AxisDependency.LEFT);
    }
    // 初始化資料集，新增一條統計折線，可以簡單的理解是初始化y座標軸線上點的表徵
    private LineDataSet createLineDataSet() {
        LineDataSet set = new LineDataSet(null, "動態新增的資料");
        set.setAxisDependency(AxisDependency.LEFT);
// 折線的顏色
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(10f);
        set.setCircleSize(5f);
        set.setFillAlpha(128);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        return set;
    }
}
