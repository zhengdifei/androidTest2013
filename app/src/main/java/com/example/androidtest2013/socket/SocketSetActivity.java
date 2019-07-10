package com.example.androidtest2013.socket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.androidtest2013.R;

public class SocketSetActivity extends AppCompatActivity {

    private MyClick myClick = new MyClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_set);

        findViewById(R.id.sk_tcp_server).setOnClickListener(myClick);
        findViewById(R.id.sk_tcp_client).setOnClickListener(myClick);
        findViewById(R.id.sk_udp_server).setOnClickListener(myClick);
        findViewById(R.id.sk_udp_client).setOnClickListener(myClick);
    }

    public class MyClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()){
                case R.id.sk_tcp_server:
                    intent = new Intent(SocketSetActivity.this, TcpServerActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sk_tcp_client:
                    intent = new Intent(SocketSetActivity.this, TcpClientActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sk_udp_server:
                    intent = new Intent(SocketSetActivity.this, UdpServerActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sk_udp_client:
                    intent = new Intent(SocketSetActivity.this, UdpClientActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
