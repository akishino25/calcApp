package com.example.work.calcsample;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    //Logcat用タグ文字列（クラス名）
    private final static String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Spinner spinner = (Spinner) findViewById(R.id.simbol_spinner);
        final TextView outputTextView = (TextView) findViewById(R.id.outputText);
        Button confirmButton = (Button) findViewById(R.id.confirmButton);

        //演算子選択時のリスナー
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //選択された時
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "simbol_spinner is selected!");
                Spinner spinner = (Spinner) parent;
                String simbol = (String) spinner.getSelectedItem();
                outputTextView.setText(simbol);
                Log.d(TAG, "simbol_spinner is selected:" +simbol);
            }

            @Override
            //選択されなかった時
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        //確定ボタン選択時
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "confirmButton is clicked!");
                //入力1,2、演算子1の値を取得
                EditText firstInput = (EditText) findViewById(R.id.firstInput);
                String firstInputString = firstInput.getText().toString();
                EditText secondInput = (EditText) findViewById(R.id.secondInput);
                String secondInputString = secondInput.getText().toString();
                String simbol = (String) spinner.getSelectedItem();
                Log.d(TAG, "firstInput:" +firstInputString +", simbol:" +simbol +", secondInput:" +secondInputString);
                double outputValue = calcConfirm(firstInputString, secondInputString, simbol);
                Log.d(TAG, "outputValue:" +outputValue);

                //Intentを使って値をMainActivityに戻す
                Intent intent = new Intent();
                intent.putExtra("firstInputString", firstInputString);
                intent.putExtra("secondInputString", secondInputString);
                intent.putExtra("simbol", simbol);
                intent.putExtra("outputValue", String.valueOf(outputValue));

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 最終的な計算結果を求める関数
     * @param firstInput 入力1
     * @param secondInput 入力2
     * @param simbol 演算子
     * @return 計算結果
     */
    private double calcConfirm(String firstInput, String secondInput, String simbol){
        double firstInputDouble = 0;
        double secondInputDouble = 0;
        try{
            firstInputDouble = Double.parseDouble(firstInput);
            secondInputDouble = Double.parseDouble(secondInput);
        }catch(NumberFormatException e){
            e.printStackTrace();
        }

        switch (simbol){
            case "＋":
                return firstInputDouble + secondInputDouble;
            case "－":
                return firstInputDouble - secondInputDouble;
            case "×":
                return firstInputDouble * secondInputDouble;
            case "÷":
                return firstInputDouble / secondInputDouble;
            default:
                return 0;
        }
    }

}
