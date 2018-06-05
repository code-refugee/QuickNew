package com.quicknew.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.quicknew.android.db.MyDatabaseHelper;
import com.quicknew.android.other.RadomNum;

import java.util.ArrayList;

public class Register extends AppCompatActivity {
    private EditText repassword;
    private EditText repasswordagain;
    private RadioGroup sex;
    private RadioButton checked;
    private Button register;
    //用于存储账号
    private ArrayList<Integer> data;
    private MyDatabaseHelper dbHelper;
    private RadomNum rn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        repassword=(EditText)findViewById(R.id.repassword);
        repasswordagain=(EditText)findViewById(R.id.repasswordagain);
        sex=(RadioGroup)findViewById(R.id.sexcheck);
        register=(Button)findViewById(R.id.register);

        //得到被选中的RadioButton的id根据id来寻找控件
        int id=sex.getCheckedRadioButtonId();
        checked=(RadioButton)findViewById(id);

        data=new ArrayList<>();
        rn=new RadomNum();
        //设置按钮的监听
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=repassword.getText().toString().trim();
                String passagain=repasswordagain.getText().toString().trim();
                String checksex=checked.getText().toString();
                //用于存放插入表的值
                ContentValues values=new ContentValues();
                //若输入为空或两次密码不一致则给出提示
                if("".equals(pass)||"".equals(passagain))
                    Toast.makeText(Register.this,"输入不能为空",
                            Toast.LENGTH_SHORT).show();
                else if(!pass.equals(passagain))
                    Toast.makeText(Register.this,"两次密码不一致",
                            Toast.LENGTH_SHORT).show();
                else{
                    int useacount;//用户名

                    //第一次执时，创建数据库数据表，若已存在数据库数据表时，不会再次创建，只会与其创建连接
                    dbHelper=new MyDatabaseHelper(Register.this,"User.db",null,1);
                    //自动执行onCreate()方法创建数据库
                    SQLiteDatabase db=dbHelper.getReadableDatabase();

                    //查询user表中所有的数据
                    Cursor cursor=db.query("User",null,null,null,null,null,null);
                    if(cursor.moveToFirst()){
                        do{
                            //将取出来的账号放入list中
                            int ac=cursor.getColumnIndex("account");
                            data.add(ac);
                        }while(cursor.moveToNext());
                        int[] nums=new int[data.size()];
                        for(int i=0;i<data.size();i++)
                            nums[i]=data.get(i);
                        //随机产生账号
                        useacount=rn.account(nums);
                    }else{
                        useacount=(int)((Math.random()*9+1)*100000);
                    }
                    cursor.close();
                    values.put("account",useacount);
                    values.put("password",pass);
                    values.put("sex",checksex);
                    //执行插入操作，对User表插入values
                    db.insert("User",null,values);
                    values.clear();
                    Toast.makeText(Register.this,"注册成功，你的用户名为"
                    +useacount,Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
}
