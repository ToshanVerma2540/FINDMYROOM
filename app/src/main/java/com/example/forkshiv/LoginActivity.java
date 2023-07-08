package com.example.forkshiv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText mobileNumEt;
    String mobilNumLength,oneet,twoet,threeet,fouret,fiveet,sixet,phoneNumber,otpid;
    TextView sendOtp;
    int count =0;
    String otps="";
    EditText one,two,three,four,five,six;
   Button loginButton;
   FirebaseAuth mAuth;
   TextView skipTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
         mAuth = FirebaseAuth.getInstance();
         skipTextView = findViewById(R.id.skip_textview);

         skipTextView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
             }
         });
// Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, so navigate to main activity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        mobileNumEt = findViewById(R.id.mobile_num_editText);
        sendOtp = findViewById(R.id.send_otp_button);
        one = findViewById(R.id.otp_et1);
        two = findViewById(R.id.otp_et2);
        three = findViewById(R.id.otp_et3);
        four = findViewById(R.id.otp_et4);
        five = findViewById(R.id.otp_et5);
        six = findViewById(R.id.otp_et6);
        loginButton = findViewById(R.id.login_button);
        mobileNumEt.addTextChangedListener(otp);
        one.addTextChangedListener(otp); two.addTextChangedListener(otp); three.addTextChangedListener(otp); four.addTextChangedListener(otp);
        five.addTextChangedListener(otp);six.addTextChangedListener(otp);
        //if(mobilNumLength.length()==12){

            sendOtp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    phoneNumber = mobileNumEt.getText().toString().trim();
                    if (phoneNumber.length() == 10&&(count%2) == 0) {
                        count = count+1;
                        //sendOtp.setTextColor(getResources().getColor(R.color.primary_color_50));
                        sendOtp.setText(R.string.change_num);
                        one.setEnabled(true);
                        one.setBackground(getDrawable(R.drawable.outline_bg));
                        one.requestFocus();
                        initiateotp();
                    }else if(phoneNumber.length()==10){
                        count = count+1;
                        sendOtp.setText(R.string.send_otp);
                        mobileNumEt.setText("");
                        one.setText("");
                        two.setText("");
                        three.setText(""); four.setText(""); five.setText(""); six.setText("");
                        mobileNumEt.requestFocus();
                    }
                }
            });


        //}
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otps = otps+oneet+twoet+threeet+fouret+fiveet+sixet;
                if(otpid!=null) {
                    //loging the user in
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid, otps);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }


    public TextWatcher otp = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mobilNumLength = mobileNumEt.getText().toString().trim();
            if(mobilNumLength.length()==10){
                sendOtp.setTextColor(getResources().getColor(R.color.primary_color));
            }else{
                sendOtp.setTextColor(getResources().getColor(R.color.primary_color_50));
            }
            oneet = one.getText().toString();
            twoet = two.getText().toString();
            threeet = three.getText().toString();
            fouret = four.getText().toString();
            fiveet = five.getText().toString();
            sixet = six.getText().toString();
            if(oneet.length() == 1){
                two.setEnabled(true);
                two.setBackground(getDrawable(R.drawable.outline_bg));
                two.requestFocus();
            } if(twoet.length() == 1){
                three.setEnabled(true);
                three.setBackground(getDrawable(R.drawable.outline_bg));
                three.requestFocus();
            } if(threeet.length() ==1){
                four.setEnabled(true);
                four.setBackground(getDrawable(R.drawable.outline_bg));
                four.requestFocus();
            }if(fouret.length() == 1){
                five.setEnabled(true);
                five.setBackground(getDrawable(R.drawable.outline_bg));
                 five.requestFocus();
            }if(fiveet.length() == 1){
                six.setEnabled(true);
                six.setBackground(getDrawable(R.drawable.outline_bg));
                six.requestFocus();
            }
            if(mobilNumLength.length()<10||oneet.isEmpty()||twoet.isEmpty()||threeet.isEmpty()||fouret.isEmpty()||fiveet.isEmpty()||sixet.isEmpty()){
                loginButton.setEnabled(false);
                loginButton.setBackgroundColor(getResources().getColor(R.color.primary_color_50));
            }else{
                loginButton.setEnabled(true);
                loginButton.setBackgroundColor(getResources().getColor(R.color.primary_color));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            
        }
    };

    //sending opt to the given phone number
    public void initiateotp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber( "+91" +phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s,  PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpid = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                       //signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_LONG).show();
                    }
                });
    }

    //signing the user in using his credentials and moving to homeactivity
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}