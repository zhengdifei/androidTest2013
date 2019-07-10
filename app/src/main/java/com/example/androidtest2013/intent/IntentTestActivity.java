package com.example.androidtest2013.intent;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.androidtest2013.MainActivity;
import com.example.androidtest2013.R;

public class IntentTestActivity extends AppCompatActivity {

    private EditText add_one, add_two, add_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_test);

        final Intent intent = getIntent();
        String msg = intent.getStringExtra("data");
        Log.e("zdf", "show intent data:" + msg);

        findViewById(R.id.jump_intent_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntentTestActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        add_one = findViewById(R.id.add_one);
        add_two = findViewById(R.id.add_two);
        add_result = findViewById(R.id.add_result);

        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int one = Integer.parseInt(add_one.getText().toString());
                int two = Integer.parseInt(add_two.getText().toString());
                Intent intent1 = new Intent(IntentTestActivity.this, IntentResultActivity.class);
                intent1.putExtra("one", one);
                intent1.putExtra("two", two);

                startActivityForResult(intent1, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==1 && resultCode == 2){
            int result = data.getIntExtra("result", 0);
            Log.e("zdf", "cal result:" + result);
            add_result.setText(result + "");
        }

        Log.e("zdf", "requestCode：" + requestCode);
        Log.e("zdf", "resultCode：" + resultCode);
    }
}
