package com.example.work.calcsample;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Logcat用タグ文字列（クラス名）
    private final static String TAG = MainActivity.class.getSimpleName();

    /*
    private String[] memoList = {"memo1", "memo2", "memo3"};
    private String[] calcResult = {"10+20=30", "200-100=100", "30*40=1200"};
    private List<Map<String, String>> calcList = new ArrayList<Map<String, String>>();
    private SimpleAdapter adapter = null;
    private ListView listView = (ListView) findViewById(R.id.calcList);
    */
    /*
    String[] memoList = {"memo1", "memo2", "memo3"};
    String[] calcResult = {"10+20=30", "200-100=100", "30*40=1200"};
    */
    String[] memoList = {};
    String[] calcResult = {};
    List<Map<String, String>> calcList = new ArrayList<Map<String, String>>();
    SimpleAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*リストビュー生成*/
        //TODO 本処理は既存データを読み込むとき用なので、後で切り出す
        //forで回してMap(Memo,calcResultを持つ)をListに詰めていく
        for(int i=0; i<memoList.length; i++){
            Map<String, String> calcMap = new HashMap<String, String>();
            calcMap.put("Memo", memoList[i]);
            calcMap.put("CalcResult", calcResult[i]);
            this.calcList.add(calcMap);
        }

        adapter = new SimpleAdapter(this, calcList,
                android.R.layout.simple_list_item_2,
                new String[]{"Memo", "CalcResult"},
                new int[]{android.R.id.text1, android.R.id.text2});

        ListView listView = (ListView) findViewById(R.id.calcList);
        listView.setAdapter(adapter);

        //listViewクリックリスナー
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                Log.d(TAG, "Listener is clicked!");
                ListView listView = (ListView)parent;
                Map<String, String> calcMap = (Map<String, String>) listView.getItemAtPosition(position);
                String memo = calcMap.get("Memo");
                String calcResult = calcMap.get("CalcResult");
                Log.d(TAG, "Memo:" +memo +", CalcResult:" +calcResult);

                //取得した計算式の文字列から、入力数値と演算子をパースする
                HashMap<String, ArrayList<String>> parseMap = parseCalcResult(calcResult);
                ArrayList<String> numbers = parseMap.get("numbers");
                ArrayList<String> simbols = parseMap.get("simbols");

                for(String s : numbers){
                    Log.d(TAG, s);
                }
                for(String s : simbols){
                    Log.d(TAG, s);
                }
            }
        });

        //Newボタンクリックリスナー
        findViewById(R.id.newButton)
                .setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //後からAdapterに要素を追加
                        //addCalcListView("memoAdd", "resultAdd");

                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        int requestCode = 1001;
                        startActivityForResult(intent, requestCode);
                    }
        });
    }

    /**
     * 他のActivityからMainActivityに処理が戻ってきた際の処理
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        //DetailActivityから戻ってきた場合の処理
        if(requestCode == 1001){
            if(resultCode == Activity.RESULT_OK){
                String firstInputString = intent.getStringExtra("firstInputString");
                String secondInputString = intent.getStringExtra("secondInputString");
                String simbol = intent.getStringExtra("simbol");
                String outputValue = intent.getStringExtra("outputValue");
                Log.d(TAG, "firstInput:" +firstInputString
                        +", simbol:" +simbol
                        +", secondInput:" +secondInputString
                        +", outputValue:" +outputValue);
                String calcResult = firstInputString +simbol +secondInputString +"=" +outputValue;
                addCalcListView("memoAdd", calcResult);
            }
        }
    }

    /**
     * 後から計算要素を画面に追加する
     * @param memo
     * @param calcResult
     */
    private void addCalcListView(String memo, String calcResult){
        Map<String, String> m = new HashMap<String, String>();
        m.put("Memo", memo);
        m.put("CalcResult", calcResult);
        this.calcList.add(m);
        this.adapter = new SimpleAdapter(MainActivity.this, this.calcList,
                android.R.layout.simple_list_item_2,
                new String[]{"Memo", "CalcResult"},
                new int[]{android.R.id.text1, android.R.id.text2});
        ListView listView = (ListView) findViewById(R.id.calcList);
        listView.setAdapter(this.adapter);
    }

    /**
     * 文字列の計算式を入力数値と演算子に正規表現で分離する
     * TODO:数式の左辺に小数が使われると正しく分離できない
     * @param calcResultStr　計算式の文字列
     * @return "numbers"と"simbols"をキーにもつ2つのArrayListを持つHashMap
     */
    private HashMap<String, ArrayList<String>> parseCalcResult(String calcResultStr){
        //calcResultStr = "100×22＋33－1000=2200.0"; //テスト用Input
        HashMap<String, ArrayList<String>> parseMap = new HashMap<String, ArrayList<String>>();

        //イコールより左を取得
        String calcLeft = calcResultStr.split("=", 0)[0];
        Log.d(TAG, calcLeft);

        //数字以外で分割して、入力数値の配列を取得
        String[] numbers = calcLeft.split("\\D");
        ArrayList<String> numbersArray = new ArrayList<String>(Arrays.<String>asList(numbers));

        //数字で分割して、演算子の配列を取得
        String[] simbols = calcLeft.split("\\d");
        ArrayList<String> simbolsArray = new ArrayList<String>();
        for(int i=0; i<simbols.length; i++){
            if(!simbols[i].isEmpty()){
                simbolsArray.add(simbols[i]);
            }
        }
        parseMap.put("numbers", numbersArray);
        parseMap.put("simbols", simbolsArray);
        return parseMap;
    }

}
