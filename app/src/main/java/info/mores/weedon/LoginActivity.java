package info.mores.weedon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.progresswindow.ProgressWindow;
import com.bluehomestudio.progresswindow.ProgressWindowConfiguration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import info.mores.weedon.Util.ConnectionDetector;
import info.mores.weedon.Util.Utility;


public class LoginActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    public static final int SPLASH_TIME_OUT = 2000;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private static final int STATE_WRONG_NO = 7;
    ProgressBar progressBar;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private DatabaseReference mDatabaseRoot;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private LinearLayout mPhoneNumberViews;
    private LinearLayout mVerifyLayout;
    private LinearLayout mEditverifyLayout;
    private ViewGroup mSignedInViews;
    private TextView mStatusText;
    private TextView mDetailText;
    private EditText mPhoneNumberField;
    private EditText mUserName;
    private EditText mVerificationField;
    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    private Button mSignOutButton;
    private String mCountryCode;
    private TextView messageWithLinkTextView;
   // private ProgressWindow progressWindow ;
    LinearLayout loginRelative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        progressConfigurations();

        // For manage Keyboard or Layout
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mDatabaseRoot = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = findViewById(R.id.login_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Let's get started");
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        messageWithLinkTextView =findViewById(R.id.tvConditions);
        progressBar = findViewById(R.id.progressBar);

        // Assign views
        mPhoneNumberViews = findViewById(R.id.phone_auth_fields);
        mVerifyLayout = findViewById(R.id.verifyLayout);
        mEditverifyLayout = findViewById(R.id.EditverifyLayout);
        mPhoneNumberViews.setVisibility(View.GONE);
        mSignedInViews = findViewById(R.id.signed_in_buttons);

        mStatusText = findViewById(R.id.status);
        mDetailText = findViewById(R.id.detail);
      //  mWrongNO = findViewById(R.id.wrong_number);

        mPhoneNumberField = findViewById(R.id.field_phone_number);
        mUserName = findViewById(R.id.field_username);
        loginRelative = findViewById(R.id.login_relative);
        // Phone Number Min or Max Value

       // mPhoneNumberField.setFilters(new InputFilter[]{ new InputFilterMinMax("6", "12")});

        mVerificationField = findViewById(R.id.field_verification_code);
       // mCountryCode = findViewById(R.id.country_code);
        mUserName.requestFocus();
       // mCountryCode.setText(GetCountryZipCode());
        mCountryCode =GetCountryZipCode();

        mStartButton = findViewById(R.id.button_start_verification);
        mVerifyButton = findViewById(R.id.button_verify_phone);
        mResendButton = findViewById(R.id.button_resend);
        mSignOutButton = findViewById(R.id.sign_out_button);

        // Assign click listeners
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]


        // Country Spinner added

        // Spinner spinner = findViewById(R.id.spinner);


// For Set Layout on Correct Positon

        final View activityRootView = findViewById(R.id.login_relative);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(LoginActivity.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    // ... do something here
                }
            }
        });


        final ConnectionDetector connectionDetector = ConnectionDetector.getInstance(LoginActivity.this);
        if (!connectionDetector.isConnectionAvailable()) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("OOPS ! No Internet");
            builder.setMessage("check your network connection ...");
            builder.setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (connectionDetector.isConnectionAvailable()) {

                       dialog.dismiss();
                    }

                    else
                    {
                        builder.show();
                    }
                }
            });
            builder.create();
            builder.show();


        }


        //////////////////////////////////////////////////////////





       /* messageWithLinkTextView.setLinkTextColor(Color.BLUE); // default link color for clickable span, we can also set it in xml by android:textColorLink=""
          ClickableSpan highlightClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Highlight Link", Toast.LENGTH_SHORT).show();
               // showConditions(LoginActivity.this);
                Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(browser);
                view.invalidate(); // need put invalidate here to make text change to GREEN after clicked
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                if (messageWithLinkTextView.isPressed() && messageWithLinkTextView.getSelectionStart() != -1 && messageWithLinkTextView.getText()
                        .toString()
                        .substring(messageWithLinkTextView.getSelectionStart(), messageWithLinkTextView.getSelectionEnd())
                        .equals("Terms and Conditions")) {
                    ds.setColor(Color.parseColor("#FF6EA4BA"));// need put invalidate here to make text change to RED when pressed on Highlight Link                    textView.invalidate();
                } else {
                    ds.setColor(Color.parseColor("#FF6EA4BA"));
                }
                // dont put invalidate here because if you put invalidate here `updateDrawState` will called forever
            }
        };

      makeLinks(messageWithLinkTextView, new String[] {"Terms of services"}, new ClickableSpan[] { highlightClickSpan });*/


        SpannableString ss = new SpannableString(getString(R.string.conditions));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms)));
                startActivity(browser);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 42, 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //TextView textView = (TextView) findViewById(R.id.hello);
        messageWithLinkTextView.setText(ss);
        messageWithLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        messageWithLinkTextView.setHighlightColor(Color.TRANSPARENT);

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
        mStatusText.setVisibility(View.INVISIBLE);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            String user_id = mAuth.getCurrentUser().getUid();
                            String name = mUserName.getText().toString();
                            String phone =mPhoneNumberField.getText().toString();

                            String token_id = FirebaseInstanceId.getInstance().getToken();

                            final FirebaseUser user = task.getResult().getUser();

                            Map<String,Object> userMap = new HashMap<>();

                            userMap.put("name",name);
                            userMap.put("phone",phone);
                            userMap.put("token_id",token_id);
                            mDatabaseRoot.child("Users").child(user_id).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {



                                    // [START_EXCLUDE]
                                    updateUI(STATE_SIGNIN_SUCCESS, user);
                                    // [END_EXCLUDE]
                                }
                            });


                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    private void wrongCode() {
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(mStartButton, mPhoneNumberField,mUserName);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                mPhoneNumberViews.setVisibility(View.VISIBLE);
                mEditverifyLayout.setVisibility(View.VISIBLE);
                mVerifyLayout.setVisibility(View.VISIBLE);
                // Code sent state, show the verification field, the
                enableViews(mVerifyButton, mResendButton, mVerificationField);
                disableViews(mStartButton,mPhoneNumberField,mUserName);
                mDetailText.setText(R.string.status_code_sent);
                mDetailText.setTextColor(Color.parseColor("#43a047"));
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText(R.string.status_verification_failed);
                mDetailText.setTextColor(Color.parseColor("#dd2c00"));
                progressBar.setVisibility(View.INVISIBLE);
                break;

         /*   case STATE_WRONG_NO:
                // Verification has failed, show all options
                enableViews(mStartButton, mPhoneNumberField,mUserName);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                     mDetailText.setText(null);
                // mDetailText.setText(R.string.status_verification_failed);
                // mDetailText.setTextColor(Color.parseColor("#dd2c00"));
                 progressBar.setVisibility(View.INVISIBLE);
                break;*/
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField,mUserName);
                mDetailText.setText("Verfication Sucessfull");
                mDetailText.setTextColor(Color.parseColor("#43a047"));
                progressBar.setVisibility(View.INVISIBLE);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                        mVerificationField.setTextColor(Color.parseColor("#4bacb8"));
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                mDetailText.setText(R.string.status_sign_in_failed);
                mDetailText.setTextColor(Color.parseColor("#dd2c00"));
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                mStatusText.setText(R.string.signed_in);
                break;
        }

        if (user == null) {
            // Signed out
            mPhoneNumberViews.setVisibility(View.VISIBLE);
            mSignedInViews.setVisibility(View.GONE);

            mStatusText.setText(R.string.signed_out);
        } else {
            // Signed in
            mPhoneNumberViews.setVisibility(View.GONE);
            /*
            mSignedInViews.setVisibility(View.VISIBLE);
            enableViews(mPhoneNumberField, mVerificationField);
            mPhoneNumberField.setText(null);
            mVerificationField.setText(null);
            mStatusText.setText(R.string.signed_in);
            mDetailText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            */


            Boolean isFirstRun = getSharedPreferences("PREFERENCES", MODE_PRIVATE)
                    .getBoolean("isFirstRun", true);

            if (isFirstRun) {
                //show start activity

                startActivity(new Intent(LoginActivity.this, AllServicesActivity.class));
             //  Toast.makeText(LoginActivity.this, "First Run", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }


            getSharedPreferences("PREFERENCES", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();


        }

    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        String userName = mUserName.getText().toString();

        if (TextUtils.isEmpty(userName))
        {
            mUserName.setError("Enter Valid Name.");
            mUserName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Enter Valid Number.");
            mPhoneNumberField.requestFocus();
            //mPhoneNumberField.setTextColor(Color.parseColor("#ff1744"));
            return false;
        }

        if (phoneNumber.trim().length()<=9)
        {
            mPhoneNumberField.setError("Number Must have 10 Digits");
            mPhoneNumberField.requestFocus();
            //mPhoneNumberField.setTextColor(Color.parseColor("#ff1744"));
            return false;
        }

      return  true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    //
                    // return;
                    validatePhoneNumber();


                    ///////hide keyboard start
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    /////////hide keyboard end

                }
                else {
                    //mStatusText.setText("Authenticating....!");
                    progressBar.setVisibility(View.VISIBLE);
                    loginRelative.setVisibility(View.VISIBLE);
                    startPhoneNumberVerification(mCountryCode.trim()+ mPhoneNumberField.getText().toString());

                    ///////hide keyboard start
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    /////////hide keyboard end


                }

                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Enter valid Code");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(mCountryCode.trim() + mPhoneNumberField.getText().toString(), mResendToken);

                break;

               /* case R.id.wrong_number:

                PhoneAuthProvider.getCredential("aaa","dada");
                wrongCode();

                break;*/

            case R.id.sign_out_button:
                signOut();
                break;
        }
    }





 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
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
*/

 // for showing user Country

    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

   /* private void progressConfigurations(){
        progressWindow = ProgressWindow.getInstance(this);
        ProgressWindowConfiguration progressWindowConfiguration = new ProgressWindowConfiguration();
        progressWindowConfiguration.backgroundColor = Color.parseColor("#32000000") ;
        progressWindowConfiguration.progressColor = Color.WHITE ;
        progressWindow.setConfiguration(progressWindowConfiguration);
    }

    public void showProgress(){
        progressWindow.showProgress();
    }


    public void hideProgress(){
        progressWindow.hideProgress();
    }*/

   // For set Keyboard or Layout perfectly
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    // Show Terms and condtions to users by dialog

   /* public void showConditions(Context context) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("Terms & conditions");
        builder.setView(R.layout.conditions_layout);
        builder.setCancelable(false);
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               dialog.dismiss();
            }
        });
        builder.show();
    }

*/
    // Make Some text clickble android textView .. Terms and Conditions clickable


  /*  public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }
*/




}