package com.example.yukkuri.todolistrpg;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    DBHelper helper;
    Cursor cursor;

    private int user_exp;
    private int user_exp_percent;
    private int user_level;

    private int year, month, day;

    private ListView todo_list;
    private TextView current_date;

    private TextView date_leftbtn;
    private TextView date_rightbtn;

    private TextView current_level;

    private Button morning_drug_btn;
    private Button night_drug_btn;
    private Button dawn_drug_btn;

    private Button clear_memo_btn;
    private Button save_memo_btn;
    private EditText memo_text;

    private String day_str;

    private EditText todo_text;
    private Button add_todolist_btn;
    private Button save_day_btn;

    private ProgressBar exp_bar;

    private CheckBox m_checkBox;
    private ListView m_listView;
    private MyAdapter m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Date today = new Date();
        year = today.getYear()+1900;
        month = today.getMonth()+1;
        day = today.getDate();
        day_str = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);

        //m_checkBox = (CheckBox) findViewById(R.id.totalCheckBox);
        current_date = (TextView)findViewById(R.id.date);
        date_leftbtn = (TextView)findViewById(R.id.date_left);
        date_rightbtn = (TextView)findViewById(R.id.date_right);


        current_level = (TextView)findViewById(R.id.level);

        exp_bar = (ProgressBar) findViewById(R.id.exp_bar);

        current_date.setText(day_str);


        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year_, int month_, int dayOfMonth) {
                  year = year_;
                  month = month_ + 1;
                  day = dayOfMonth;
                  day_str = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
                  current_date.setText(day_str);
                  view_todolist(day_str);
                load_memotext(day_str);
            }
        };

            current_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Dialog date_picker = new DatePickerDialog(MainActivity.this, dateSetListener, year, month-1, day);
                date_picker.show();
            }
        }
        );

            date_rightbtn.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    day += 1;
                    if ((month % 2 == 1 && month < 8) || (month % 2 == 0 && month >= 8)) {
                        if (day == 32) {
                            month += 1;
                            day = 1;
                        }
                    }
                    else if (month == 2){
                        if (day == 30){
                            month += 1;
                            day = 1;
                        }
                    }
                    else{
                        if (day == 31){
                            month += 1;
                            day = 1;
                        }
                    }

                    day_str = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
                    current_date.setText(day_str);
                    view_todolist(day_str);
                    load_memotext(day_str);
                }
            });

            date_leftbtn.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    day -= 1;

                    if (day == 0){
                        month -= 1;
                        if ((month % 2 == 1 && month < 8) || (month % 2 == 0 && month >= 8)) {
                            day = 31;
                        }
                        else if (month == 2){
                            day = 29;
                        }
                        else{
                            day = 30;
                        }
                    }

                    day_str = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
                    current_date.setText(day_str);
                    view_todolist(day_str);
                    load_memotext(day_str);
                }
            });

        todo_text = (EditText) findViewById(R.id.add_list_edittext);
        add_todolist_btn = (Button) findViewById(R.id.add_list);
        save_day_btn = (Button) findViewById(R.id.save_day);

        morning_drug_btn = (Button) findViewById(R.id.drugButton);
        night_drug_btn = (Button) findViewById(R.id.drugButton2);
        dawn_drug_btn = (Button) findViewById(R.id.drugButton3);

        memo_text = (EditText) findViewById(R.id.memo_text);
        clear_memo_btn = (Button) findViewById(R.id.clear_memo);
        save_memo_btn = (Button) findViewById(R.id.save_memo);

        //m_button = (Button) findViewById(R.id.drugButton);
        m_listView = (ListView) findViewById(R.id.totalListView);

        //m_button.setOnClickListener(this);

        // 레벨, 경험치 표시
        helper = new DBHelper(MainActivity.this, "data.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        cursor = db.rawQuery("SELECT level, exp, coupon FROM user", null);
        startManagingCursor(cursor);
        cursor.moveToFirst();

        // 경험치바 관련
        user_level = cursor.getInt(cursor.getColumnIndex("level"));
        user_exp = cursor.getInt(cursor.getColumnIndex("exp"));
        user_exp_percent = user_exp / 15;
        current_level.setText("Lv."+ Integer.toString(user_level) +" / " + "exp: " + Integer.toString(user_exp_percent) + "%");
        exp_bar.setProgress(user_exp_percent);

        // 약 표시
        // 그 날짜에 대한 데이터가 없으면 추가하고, 있으면 그냥 넘긴다
        db.execSQL("insert or ignore into drug VALUES ('" + day_str + "', 0, 0, 0)");


        cursor = db.rawQuery("SELECT morning, night, dawn FROM drug WHERE date = ?", new String[]{day_str});
        startManagingCursor(cursor);
        cursor.moveToFirst();
        int morning_drug = cursor.getInt(cursor.getColumnIndex("morning"));
        int night_drug = cursor.getInt(cursor.getColumnIndex("night"));
        int dawn_drug = cursor.getInt(cursor.getColumnIndex("dawn"));

        if (morning_drug == 1)
            morning_drug_btn.setText("먹었어요");
        if (night_drug == 1)
            night_drug_btn.setText("먹었어요");
        if (dawn_drug == 1)
            dawn_drug_btn.setText("먹었어요");

        load_memotext(day_str);

        morning_drug_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drugbtn_change(day_str, 0);
                morning_drug_btn.setText("먹었어요");
            }
        });
        night_drug_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drugbtn_change(day_str, 1);
                night_drug_btn.setText("먹었어요");
            }
        });
        dawn_drug_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drugbtn_change(day_str, 2);
                dawn_drug_btn.setText("먹었어요");
            }
        });

        view_todolist(day_str);

        clear_memo_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                memo_text.setText("");
            }
        });

        save_memo_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DBHelper helper = new DBHelper(MainActivity.this, "data.db", null, 1);
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();     // DB에 추가할 인스턴스 생성
                values.put("memo", memo_text.getText().toString());
                db.update("memo", values, "date=?", new String[]{day_str});
            }
        });

        add_todolist_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                add_todolist("todolist", todo_text.getText().toString(), day_str, 0, 0);
                todo_text.setText("");
            }
        });

        save_day_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int get_exp = 0;
                String exp_str;

                for (int i=0; i<m_adapter.getCount(); i++) {
                    if (m_adapter.get_checkbox_status(i)) {
                        exp_str = m_adapter.get_todo_string(i).split(" --- exp: ")[1];
                        get_exp += Integer.parseInt(exp_str);
                    }
                }

                user_exp += get_exp;
                user_exp_percent = user_exp / 15;

                // 레벨업 여부 체크
                if (user_exp_percent >= 100){
                    user_level += 1;
                    user_exp -= 1500;
                    user_exp_percent -= 100;
                }

                exp_bar.setProgress(user_exp_percent);
                current_level.setText("Lv."+ Integer.toString(user_level) +" / " + "exp: " + Integer.toString(user_exp_percent) + "%");
                Toast.makeText(MainActivity.this, "경험치 " + Integer.toString(get_exp) + "를 얻었습니다.", Toast.LENGTH_SHORT);

                DBHelper helper = new DBHelper(MainActivity.this, "data.db", null, 1);
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();     // DB에 추가할 인스턴스 생성
                values.put("level", user_level);
                values.put("exp", user_exp);
                db.update("user", values, null, null);
            }
        });

    }

    public void onClick(View v) {
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        CheckBox checkBox = (CheckBox) arg1;
        checkBox.setChecked(!checkBox.isChecked());
        m_adapter.notifyDataSetChanged(); // 체크박스 체크여부 실시간으로 적용하기 위해 필요

        String get_str = arg0.getItemAtPosition(arg2).toString().split(" --- ")[0];

        int checked;
        if (checkBox.isChecked())
            checked = 1;
        else checked = 0;

        update_checkbox(get_str, checked);
    }

    public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos, long id) {
        //Toast.makeText(this, "longclicklistener", Toast.LENGTH_SHORT).show();
        //Cursor text_cursor = (Cursor)
        String get_str = arg0.getItemAtPosition(pos).toString().split(" --- ")[0];
        Toast.makeText(this, get_str, Toast.LENGTH_SHORT).show();

        helper = new DBHelper(MainActivity.this, "data.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("todolist", "name=?", new String[]{get_str});

        Toast.makeText(MainActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
        view_todolist(day_str);
        return true;
    }

    void drugbtn_change(String date, int time){
        DBHelper helper = new DBHelper(MainActivity.this, "data.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();     // DB에 추가할 인스턴스 생성

        if (time==0) values.put("morning", 1);
        else if (time==1) values.put("night", 1);
        else if (time==2) values.put("dawn", 1);

        db.update("drug", values, "date=?", new String[]{date});
    }

    void add_todolist(String tbl, String context, String date, int checkflag, int insOrUpd)
    {
        DBHelper helper = new DBHelper(MainActivity.this, "data.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();     // DB에 추가할 인스턴스 생성
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
        values.put("date", date);
        values.put("name", context);
        values.put("checkflag", checkflag);
        Random rand = new Random();
        int exp = rand.nextInt(50) + 50;
        values.put("exp", Integer.toString(exp));

        if(insOrUpd==0) {
            //Toast.makeText(this, context, Toast.LENGTH_SHORT).show();
            db.insertOrThrow(tbl, null, values);
        }
        else
            db.update(tbl, values, null, null);

        Toast.makeText(this, "할 일을 추가했습니다.", Toast.LENGTH_SHORT).show();
        view_todolist(date);
    }

    void update_checkbox(String name_str, int checked)
    {
        DBHelper helper = new DBHelper(MainActivity.this, "data.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();     // DB에 추가할 인스턴스 생성
        values.put("checkflag", checked);
        db.update("todolist", values, "name=?", new String[]{name_str});
    }

    void load_memotext(String current_date)
    {
        DBHelper helper = new DBHelper(MainActivity.this, "data.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert or ignore into memo VALUES ('" + day_str + "', null)");
        cursor = db.rawQuery("SELECT memo FROM memo WHERE date = ?", new String[]{day_str});
        startManagingCursor(cursor);
        cursor.moveToFirst();
        String saved_memo = cursor.getString(cursor.getColumnIndex("memo"));
        memo_text.setText(saved_memo);
    }

    void view_todolist(String current_date)    // DB에서 저장된 목록 불러오기
    {
        helper = new DBHelper(MainActivity.this, "data.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        //Toast.makeText(this, current_date, Toast.LENGTH_SHORT).show();

        cursor = db.rawQuery("SELECT name, checkflag, exp FROM todolist WHERE date = ?", new String[] {current_date});
        startManagingCursor(cursor);

        ArrayList<String> items = new ArrayList<String>();
        ArrayList<Boolean> checkflags = new ArrayList<>();

        cursor.moveToFirst();
        for (int i=0; i<cursor.getCount(); i++){
            String todo_text = cursor.getString(cursor.getColumnIndex("name"));
            String exp_text = cursor.getString((cursor.getColumnIndex("exp")));
            todo_text = todo_text.concat(" --- exp: " + exp_text);

            int check_flag = cursor.getInt(cursor.getColumnIndex("checkflag"));

            items.add(todo_text);
            if (check_flag == 1)
                checkflags.add(true);
            else
                checkflags.add(false);

            cursor.moveToNext();
        }

        //Toast.makeText(this, items.get(0), Toast.LENGTH_SHORT).show();

        m_adapter = new MyAdapter(this,
                android.R.layout.simple_list_item_1, items);

        for (int i=0; i<items.size(); i++)
            m_adapter.set_checkbox(i, checkflags.get(i));

        m_listView.setAdapter(m_adapter);
        m_listView.setOnItemClickListener(this);
        m_listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
