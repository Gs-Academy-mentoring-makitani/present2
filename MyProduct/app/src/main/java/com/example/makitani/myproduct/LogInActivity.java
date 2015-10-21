package com.example.makitani.myproduct;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LogInActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    private Button LogIn;

    private View.OnClickListener LogIn_ClickListener = new View.OnClickListener(){
        public void onClick(View v) {
            LogIn_Click();
        }
    };


    private void setViewObject () {

        LogIn = (Button) findViewById(R.id.login_btn_facebook);
    }

    private void setListner() {
        LogIn.setOnClickListener(LogIn_ClickListener);
    }

    private void LogIn_Click() {
        Intent intent = new Intent(this, TopActivity.class);
        startActivity(intent);
    }



    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_log_in);

        setViewObject();
        setListner();

        callbackManager = CallbackManager.Factory.create();
        //LoginButtonはFacebookSDKに含まれたButtonウィジェットの拡張ウィジェット。
        loginButton = (LoginButton) findViewById(R.id.login_btn_facebook);
        //APIから取得したいユーザー情報の種類を指定し、パーミッションを許可する。
        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        loginButton.setReadPermissions(permissionNeeds);


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("");
                GraphRequest request = GraphRequest.newMeRequest
                        (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //API連携に成功し、ログイン状態になったら、下記のようなゲッター関数により、ユーザー情報を取得することが出来る。
                                try {
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    String gender = object.getString("gender");
                                    String birthday = object.getString("birthday");
                                    System.out.println(name);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.v("LoginActivity", exception.toString());
            }
        });

    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////FacebookSdkを使う場合、必須。Activityのレイアウトファイルを定義する箇所よりも前に記述する。
//        FacebookSdk.sdkInitialize(this);
//        setContentView(R.layout.activity_log_in);
//    }

    /*
        Profile prof= Profile.getCurrentProfile();
    // prof変数がnullでなければ、現在ログイン中であるということが分かる。
        if(prof != null)
        {
            v("facebook_login", "1:" + prof.getName());
            v("facebook_login", "1:" + prof.getFirstName());
            v("facebook_login", "1:" + prof.getLastName());
            v("facebook_login", "1:" + prof.getID());
        } else {
    //Prof変数がnullの場合、現在ログアウト中であることがわかる。
            v("facebook_login","2");
        }

    public class LogInActivity extends AppCompatActivity {

        private Button LogIn;

        private View.OnClickListener LogIn_ClickListener = new View.OnClickListener(){
            public void onClick(View v) {
                LogIn_Click();
            }
        };


        private void setViewObject () {

            LogIn = (Button) findViewById(R.id.LogIn);
        }

        private void setListner(){
            LogIn.setOnClickListener(LogIn_ClickListener);
        }

        private void  LogIn_Click () {
            Intent intent = new Intent(this, TopActivity.class);
            startActivity(intent);
        }






        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_log_in);

            setViewObject();
            setListner();

        }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
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
