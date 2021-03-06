package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.activity.SearchPatientActivity;

public class NurseMainActivity extends BaseActivity {

    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_main);

        init();
    }

    private void init(){

        logoutBtn = (Button)findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(NurseMainActivity.this);
            }
        });

        showSnackbar(String.format(getString(R.string.print_hello_msg), StartActivity.employee.getName()));


        findViewById(R.id.cv_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NurseMainActivity.this, SearchPatientActivity.class);
                intent.putExtra("nextActivity", Information.NURSE_RECORD_MENU);
                startActivity(intent);
            }
        });
        findViewById(R.id.cv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NurseMainActivity.this,NurseRegisterActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cv_inquiry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NurseMainActivity.this, NurseInquiryActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.cv_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NurseMainActivity.this, NurseManageActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout li_menu = (LinearLayout)findViewById(R.id.li_menu);

    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title(R.string.ok)
                .content(R.string.are_you_finish_app)
                .positiveText(R.string.finish)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}
