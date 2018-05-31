package com.quicknew.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quicknew.android.db.MyDatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private TextView main_register;
    private EditText account;
    private EditText password;
    private CheckBox remember;
    private Button login;

    private MyDatabaseHelper dbHelper;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_register=(TextView)findViewById(R.id.main_register);
        account=(EditText)findViewById(R.id.account);
        password=(EditText)findViewById(R.id.password);
        remember=(CheckBox)findViewById(R.id.remember);
        login=(Button)findViewById(R.id.login);

        pref= PreferenceManager.getDefaultSharedPreferences(this);
        //用get方法从对象中读取数据
        boolean isRemember=pref.getBoolean("remember_password",false);
        if(isRemember){
            String acount=pref.getString("account","");
            String pass=pref.getString("password","");
            account.setText(acount);
            password.setText(pass);
            remember.setChecked(true);
        }
        /*给文本编辑器添加监听，当点击之后跳转到注册页面*/
        main_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建意图
                Intent intent=new Intent(MainActivity.this, Register.class);
                MainActivity.this.startActivity(intent);
            }
        });

        //给按钮添加监听
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useaccount=account.getText().toString();
                String usepass=password.getText().toString();
                dbHelper=new MyDatabaseHelper(MainActivity.this,"User.db",null,1);
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                if("".equals(useaccount)||"".equals(usepass)){
                    Toast.makeText(MainActivity.this,"用户名或账户不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Cursor cursor=db.rawQuery("select * from User where account=?",
                        new String[]{useaccount});
                if(cursor.moveToFirst()){
                    String pas=cursor.getString(cursor.getColumnIndex("password"));
                    if(pas.equals(usepass)){
                        editor=pref.edit();
                        //检查复选框是否选中
                        if(remember.isChecked()){
                            editor.putBoolean("remember_password",true);
                            editor.putString("account",useaccount);
                            editor.putString("password",usepass);
                        }else{
                            editor.clear();
                        }
                        editor.apply();
                        Intent intent=new Intent(MainActivity.this,ContentActivity.class);
                        MainActivity.this.startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this,"密码错误",
                                Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MainActivity.this,"用户不存在",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
