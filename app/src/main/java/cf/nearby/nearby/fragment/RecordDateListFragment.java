package cf.nearby.nearby.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.InquiryDetailActivity;
import cf.nearby.nearby.adapter.InquiryDateListCustomAdapter;
import cf.nearby.nearby.obj.MainRecord;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.ParsePHP;

/**
 * Created by tw on 2017-10-28.
 */
public class RecordDateListFragment extends BaseFragment implements OnAdapterSupport {

    // UI
    private View view;
    private Context context;

    private Patient selectedPatient;


    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_MARK_SUCCESS = 506;
    private final int MSG_MESSAGE_MARK_FAIL = 507;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;

    private int page = 0;
    private ArrayList<MainRecord> tempList;
    private ArrayList<MainRecord> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private InquiryDateListCustomAdapter adapter;
    private boolean isLoadFinish;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        if(getArguments() != null) {
            selectedPatient = (Patient)getArguments().getSerializable("patient");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // UI
        view = inflater.inflate(R.layout.fragment_inquiry_date_list, container, false);
        context = container.getContext();

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        init();

        getPatientMainRecordList();

        return view;

    }


    public void init(){

        tv_msg = (TextView)view.findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv = (RecyclerView)view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLinearLayoutManager);
//        rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));

        loading = (AVLoadingIndicatorView)view.findViewById(R.id.loading);


    }

    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void getPatientMainRecordList(){

        if(!isLoadFinish) {
//            loading.show();
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getMainRecordList");
            map.put("patient_id", selectedPatient.getId());
            map.put("page", Integer.toString(page));

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = MainRecord.getMainRecordGroupingList(MainRecord.getMainRecordList(data));

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();

                        tempList = MainRecord.getMainRecordGroupingList(MainRecord.getMainRecordList(data));

                        if (tempList.size() < 30) {
                            isLoadFinish = true;
                        }
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_ENDLESS_LIST));

                    }

                }
            }.start();
        }else{
            if(adapter != null){
                adapter.setLoaded();
            }
        }

    }

    public void makeList(){

        if(list.size() > 0){
            tv_msg.setVisibility(View.GONE);
        }else{
            tv_msg.setVisibility(View.VISIBLE);
            setFadeInAnimation(tv_msg);
        }


        adapter = new InquiryDateListCustomAdapter(context, list, rv, this);

        rv.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page+=1;
                getPatientMainRecordList();
            }
        });

        adapter.notifyDataSetChanged();

    }

    private void addList(){

        for(int i=0; i<tempList.size(); i++){
            list.add(tempList.get(i));
            adapter.notifyItemInserted(list.size());
        }

        adapter.setLoaded();

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_MAKE_LIST:
//                    progressDialog.hide();
                    loading.hide();
                    makeList();
                    break;
                case MSG_MESSAGE_MAKE_ENDLESS_LIST:
//                    progressDialog.hide();
                    loading.hide();
                    addList();
                    break;
                case MSG_MESSAGE_PROGRESS_HIDE:
//                    progressDialog.hide();
                    loading.hide();
                    break;
                case MSG_MESSAGE_MARK_SUCCESS:
//                    progressDialog.hide();
                    initLoadValue();
                    getPatientMainRecordList();
                    break;
                case MSG_MESSAGE_MARK_FAIL:
//                    progressDialog.hide();
                    new MaterialDialog.Builder(context)
                            .title(R.string.fail_srt)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void showView() {
//        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void hideView() {
//        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void redirectActivityForResult(Intent intent) {
        startActivityForResult(intent, 0);
    }

    @Override
    public void redirectActivity(Intent intent) {
        intent.putExtra("patient", selectedPatient);
        startActivity(intent);
    }


}
