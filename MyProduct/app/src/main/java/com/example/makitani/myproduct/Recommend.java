package com.example.makitani.myproduct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Recommend extends AppCompatActivity {

    private Button LogIn;
    private Button LogIn2;

    private View.OnClickListener LogIn_ClickListener = new View.OnClickListener(){
        public void onClick(View v) {
            LogIn_Click();
        }
    };

    private View.OnClickListener LogIn2_ClickListener = new View.OnClickListener(){
        public void onClick(View v) {
            LogIn2_Click();
        }
    };

    private void setViewObject () {
        LogIn = (Button) findViewById(R.id.rec1);
    }

    private void setViewObject2 () {
        LogIn2 = (Button) findViewById(R.id.rec1);
    }

    private void setListner() {
        LogIn.setOnClickListener(LogIn_ClickListener);
    }

    private void setListner2() {
        LogIn2.setOnClickListener(LogIn2_ClickListener);
    }

    private void LogIn_Click() {
        Intent intent = new Intent(this, TopActivity.class);
        startActivity(intent);
    }

    private void LogIn2_Click() {
        Intent intent = new Intent(this, TopActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        setViewObject();
        setListner();
        setViewObject2();
        setListner2();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recommend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
