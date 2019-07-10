package com.example.androidtest2013.intent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidtest2013.R;

public class IntentResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_result);
        final Intent intent = getIntent();
        int one = intent.getIntExtra("one", 0);
        int two = intent.getIntExtra("two", 0);
        TextView tv = findViewById(R.id.show_add);
        tv.setText(one + " + " + two +" = ?" );
        final EditText re = findViewById(R.id.show_add_result);

        findViewById(R.id.add_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                int result = Integer.parseInt(re.getText().toString());
                intent1.putExtra("result", result);
                setResult(2, intent1);
                finish();
            }
        });
    }
}
