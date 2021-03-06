package cf.nearby.nearby.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gigamole.navigationtabbar.ntb.NavigationTabBar;

import java.util.ArrayList;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.fragment.ManageDetailFragment;
import cf.nearby.nearby.fragment.RecordDateListFragment;
import cf.nearby.nearby.fragment.RecordItemListFragment;
import cf.nearby.nearby.obj.Patient;

public class InquiryMainActivity extends BaseActivity {


    private ViewPager viewPager;
    private NavigationAdapter mPagerAdapter;
    private String[] toolbarTitleList = new String[3];
    private static final int startPage = 0;

    private Patient selectedPatient;
    private boolean isSupporter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_main);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        isSupporter = getIntent().getBooleanExtra("isSupporter", false);

        if(isSupporter){
            toolbarTitleList = new String[3];
            toolbarTitleList[0] = getString(R.string.manage_srt);
            toolbarTitleList[1] = getResources().getString(R.string.view_by_item);
            toolbarTitleList[2] = getResources().getString(R.string.view_by_date);
        }else{
            toolbarTitleList = new String[2];
            toolbarTitleList[0] = getResources().getString(R.string.view_by_item);
            toolbarTitleList[1] = getResources().getString(R.string.view_by_date);
        }

        init();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(toolbarTitleList.length);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager(), selectedPatient, isSupporter);
        viewPager.setAdapter(mPagerAdapter);

        final String[] colors = getResources().getStringArray(R.array.default_preview);
        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        navigationTabBar.setTitleSize(30);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        if(isSupporter) {
            models.add(
                    new NavigationTabBar.Model.Builder(
                            getResources().getDrawable(R.drawable.ic_file_document_white_36dp),
                            getColorId(R.color.colorAccent))
                            .build()
            );
        }
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_format_list_bulleted_type_white_36dp),
                        getColorId(R.color.colorAccent))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_calendar_today_white_36dp),
                        getColorId(R.color.colorAccent))
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, startPage);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);

    }



    private static class NavigationAdapter extends FragmentStatePagerAdapter{

        Patient patient;
        boolean isSupporter;

        public NavigationAdapter(FragmentManager fm, Patient patient, boolean isSupporter){
            super(fm);
            this.patient = patient;
            this.isSupporter = isSupporter;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            final int pattern;
            if(isSupporter)
                pattern = position%3;
            else
                pattern = position%2;
            Bundle bdl = new Bundle(1);
            bdl.putSerializable("patient", patient);
            if(isSupporter) {
                switch (pattern) {
                    case 0: {
                        f = new ManageDetailFragment();
                        f.setArguments(bdl);
                        break;
                    }
                    case 1: {
                        f = new RecordItemListFragment();
                        f.setArguments(bdl);
                        break;
                    }
                    case 2: {
                        f = new RecordDateListFragment();
                        f.setArguments(bdl);
                        break;
                    }
                    default: {
                        f = new Fragment();
                        break;
                    }
                }
            }else{
                switch (pattern) {
                    case 0: {
                        f = new RecordItemListFragment();
                        f.setArguments(bdl);
                        break;
                    }
                    case 1: {
                        f = new RecordDateListFragment();
                        f.setArguments(bdl);
                        break;
                    }
                    default: {
                        f = new Fragment();
                        break;
                    }
                }
            }
            return f;
        }

        @Override
        public int getCount(){
            if(isSupporter)
                return 3;
            else
                return 2;
        }

    }
}
