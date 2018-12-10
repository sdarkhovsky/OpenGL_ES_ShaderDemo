package com.example.openglshaderdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    public void StartDemo1(View view) {
        Intent intent = new Intent(this, Demo1Activity.class);
        startActivity(intent);
    }

    public void StartDemo2(View view) {
        Intent intent = new Intent(this, Demo2Activity.class);
        startActivity(intent);
    }

    public void StartDemo3(View view) {
        Intent intent = new Intent(this, Demo3Activity.class);
        startActivity(intent);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
