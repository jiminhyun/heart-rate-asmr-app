package com.example.moveair5.relatedmusic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moveair5.CustomToast;
import com.example.moveair5.MainActivity;
import com.example.moveair5.R;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StabilizeActivity extends AppCompatActivity {
    Button btnSearch;
    Switch bluetoothswitchOn, statusswitchon;

    BluetoothAdapter bluetoothAdapter;

    static int LowBpm = 0;
    static int HighBpm = 0;

    private LineChart chart;
    Set<BluetoothDevice> devices;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket = null;
    OutputStream outputStream = null;
    InputStream inputStream = null;
    Thread workerThread = null;
    byte[] readBuffer;
    int readBufferPosition;

    ArrayList<Integer> bpmsetup = new ArrayList<Integer>();
    ArrayList<Integer> copybpmsetup = new ArrayList<Integer>();
    int averagebpm = 0, sum = 0;
    int check = 0, connectcheck = 0, status = 0, healthstatus = 1; //status ????????????, healthstatus ??????????????????
    int checkage, checklow = 0, checkhigh = 0;

    String data;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth; // ?????????????????? ??????

    private RecyclerView recyclerView;
    private StabilizeAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Stabilize> arrayList, arrayListcopy2;
    ImageButton music, next, previous;
    TextView statusname, songname, averageview;
    Spinner statuscheck;
    LinearLayout linearLayout;
    LineChart datachart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stabilize);
        stopService(new Intent(this, PlaySong.class));
        btnSearch = findViewById(R.id.buttonSearch);
        bluetoothswitchOn = findViewById(R.id.BluetoothswitchOn);
        statusswitchon = findViewById(R.id.StatuswitchOn);
        music = findViewById(R.id.stabilize_music);
        previous = findViewById(R.id.stabilize_previous);
        next = findViewById(R.id.stabilize_next);
        statusname = findViewById(R.id.stabilizenameView);
        songname = findViewById(R.id.stabilizenameView2);
        averageview = findViewById(R.id.averagebpmView);
        linearLayout = findViewById(R.id.stabilizebar);
        statuscheck = findViewById(R.id.statuscheck);
        datachart = findViewById(R.id.LineChart);

        recyclerView = findViewById(R.id.stabilizeview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(StabilizeActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        @SuppressLint({"ResourceType", "MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.menu.stabilize_toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("song/genre");
// ???????????? ????????????

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                data = profile.getDisplayName();
            }
        }
        String[] check_age = data.split(",");
        int[] get_age = new int[]{Integer.parseInt(check_age[0]), Integer.parseInt(check_age[1]), Integer.parseInt(check_age[2])};
        checkage = getAge(get_age[0], get_age[1], get_age[2]);
        if (checkage >= 20 && checkage <= 25) {
            HighBpm = 85;
            LowBpm = 56;
        } else if (checkage > 25 && checkage <= 35) {
            HighBpm = 83;
            LowBpm = 55;
        } else if (checkage > 35 && checkage <= 45) {
            HighBpm = 85;
            LowBpm = 57;
        } else if (checkage > 45 && checkage <= 55) {
            HighBpm = 84;
            LowBpm = 58;
        } else if (checkage > 55 && checkage <= 65) {
            HighBpm = 84;
            LowBpm = 57;
        } else if (checkage > 65) {
            HighBpm = 84;
            LowBpm = 56;
        }// ????????? ????????? ?????? ??????bpm ?????????

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.status)){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView age_item = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    age_item.setTextColor(Color.GRAY);
                }
                else {
                    age_item.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        statuscheck.setAdapter(spinnerArrayAdapter);

        statuscheck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    healthstatus = Integer.parseInt(String.valueOf(adapterView.getItemAtPosition(i)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });//???????????? ??????

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StabilizeSong.preset_response(StabilizeActivity.this);
                init_play();
            }
        });//play, stop ??????

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StabilizeSong.previous_response(StabilizeActivity.this);
                init_previous_next();
            }
        });//previous ??????

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StabilizeSong.next_response(StabilizeActivity.this);
                init_previous_next();
            }
        });//next ??????

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {//?????? ?????? ?????????, ??????, ?????? ????????? ?????? ??????
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });//?????????????

        statusswitchon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    status = 1;
                    statuscheck.setVisibility(View.VISIBLE);
                } else {
                    status = 0;
                    statuscheck.setVisibility(View.INVISIBLE);
                }
            }
        });//??????????????????

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    CustomToast.showToast(getApplicationContext(), "?????? ???????????? ????????? ???????????? ????????????.");
                } else if (bluetoothAdapter.isEnabled()) { //???????????? ??????
                    searchbuilder();
                } else {
                    CustomToast.showToast(getApplicationContext(), "??????????????? ????????????.");
                }
            }
        });//???????????? ??????

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initOnOff();


        chart = (LineChart) findViewById(R.id.LineChart);

        chart.setDrawGridBackground(true);
        chart.setBackgroundColor(Color.WHITE);
        chart.setGridBackgroundColor(Color.rgb(124, 150, 201));
        chart.setBorderColor(Color.BLACK);

// description text
        chart.getDescription().setEnabled(false);

// touch gestures (false-????????????)
        chart.setTouchEnabled(false);

// scaling and dragging (false-????????????)
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

//auto scale
        chart.setAutoScaleMinMaxEnabled(true);

// if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

//X???
        XAxis bottomAxis = chart.getXAxis();
        bottomAxis.setTextColor(Color.BLACK);
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setDrawGridLines(false);

        bottomAxis.setEnabled(false);
        bottomAxis.setAxisMinimum(0);
        bottomAxis.setAxisMaximum(29);
        bottomAxis.setLabelCount(30, false);
        bottomAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                String sValue = String.valueOf((int) value + 1);

                return sValue;
            }
        });

//Y???
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);

        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(240);//200?????? ????????? ????????? ?????? ??????

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

//??????
        chart.setRenderer(new CenteredTextLineChartRenderer(chart, chart.getAnimator(), chart.getViewPortHandler())); // ??? ?????? ??? ??????

// don't forget to refresh the drawing
        chart.invalidate();

    }//oncreate

    private void addEntry(double num) {// chart data ?????? ???(????????? ?????????)

        if (status == 0) {
            if (checkage >= 20 && checkage <= 25) {
                HighBpm = 85;
                LowBpm = 56;
            } else if (checkage > 25 && checkage <= 35) {
                HighBpm = 83;
                LowBpm = 55;
            } else if (checkage > 35 && checkage <= 45) {
                HighBpm = 85;
                LowBpm = 57;
            } else if (checkage > 45 && checkage <= 55) {
                HighBpm = 84;
                LowBpm = 58;
            } else if (checkage > 55 && checkage <= 65) {
                HighBpm = 84;
                LowBpm = 57;
            } else if (checkage > 65) {
                HighBpm = 84;
                LowBpm = 56;
            }
        } else if (status == 1) {
            if (healthstatus == 1) {
                HighBpm = 119 - (int) ((35.0f / 45.0f * (checkage - 20)));
                LowBpm = 80;
            } else if (healthstatus == 2) {
                HighBpm = 129 - (int) ((33.0f / 45.0f * (checkage - 20)));
                LowBpm = 119 - (int) ((35.0f / 45.0f * (checkage - 20)));
            } else if (healthstatus == 3) {
                HighBpm = 169 - (int) ((44.0f / 45.0f * (checkage - 20)));
                LowBpm = 129 - (int) ((33.0f / 45.0f * (checkage - 20)));
            } else if (healthstatus == 4) {
                HighBpm = 200 - (int) ((51.0f / 45.0f * (checkage - 20)));
                LowBpm = 169 - (int) ((44.0f / 45.0f * (checkage - 20)));
            }
        } else {

        }// ????????? ????????? ?????? ??????bpm ?????????
        LimitLine lowlimitLine = new LimitLine((float) LowBpm, "?????? ?????????(" + LowBpm + ")");
        lowlimitLine.setLineWidth(2);
        lowlimitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        lowlimitLine.setLineColor(Color.BLACK);
        lowlimitLine.setTextColor(Color.BLACK);
        lowlimitLine.setTextSize(10f);

        LimitLine highlimitLine = new LimitLine((float) HighBpm, "?????? ?????????(" + HighBpm + ")");
        highlimitLine.setLineWidth(2);
        highlimitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        highlimitLine.setLineColor(Color.BLACK);
        highlimitLine.setTextColor(Color.BLACK);
        highlimitLine.setTextSize(10f);

        chart.getAxisLeft().removeAllLimitLines();
        chart.getAxisLeft().addLimitLine(lowlimitLine);
        chart.getAxisLeft().addLimitLine(highlimitLine);

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }

        data.addEntry(new Entry((float) set.getEntryCount(), (float) num), 0); //data ??????
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        if (bpmsetup.size() < 30) {
            datachart.setVisibility(View.VISIBLE);
            if (StabilizeSong.mediaPlayer != null) {
                StabilizeSong.mediaPlayer.release();
                StabilizeSong.mediaPlayer = null;
            }
            StabilizeSong.setArrayList(null);
            statusname.setText("????????? ??????????????????...");
        } else {
            music.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            previous.setVisibility(View.VISIBLE);
            averageview.setText("?????? BPM: " + String.valueOf(averagebpm));
            if (averagebpm != 0 && averagebpm < LowBpm) {
                if(checklow == 0) {
                    databaseReference.child("low").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            recyclerView.setVisibility(View.VISIBLE);
                            arrayList.clear();
                            arrayListcopy2 = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Stabilize stabilize = dataSnapshot.getValue(Stabilize.class);
                                arrayList.add(stabilize);
                                arrayListcopy2 = arrayList;
                            }
                            StabilizeActivity.this.onArrayListObtained(arrayListcopy2);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });// ?????? ????????? ????????????
                    checkhigh = 0;
                    checklow = 1;
                    adapter = new StabilizeAdapter(arrayList, StabilizeActivity.this);
                    recyclerView.setAdapter(adapter);//?????????
                    adapter.setOnItemClickListener(new StabilizeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int pos) {
                            StabilizeSong.itemcount = adapter.getItemCount() - 1;
                            StabilizeSong.position = pos;
                            StabilizeSong.init_response(StabilizeActivity.this);
                            init_play();
                        }
                    });// ???????????? ?????? ?????????
                }
                statusname.setText("?????? ??????: BPM ??????!");
                auto_play();
            } else if (averagebpm != 0 && averagebpm > HighBpm) {
                if(checkhigh == 0) {
                    databaseReference.child("high").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            recyclerView.setVisibility(View.VISIBLE);
                            arrayList.clear();
                            arrayListcopy2 = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Stabilize stabilize = dataSnapshot.getValue(Stabilize.class);
                                arrayList.add(stabilize);
                                arrayListcopy2 = arrayList;
                            }
                            StabilizeActivity.this.onArrayListObtained(arrayListcopy2);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });// ?????? ????????? ????????????
                    checkhigh = 1;
                    checklow = 0;
                    adapter = new StabilizeAdapter(arrayList, StabilizeActivity.this);
                    recyclerView.setAdapter(adapter);//?????????
                    adapter.setOnItemClickListener(new StabilizeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int pos) {
                            StabilizeSong.itemcount = adapter.getItemCount() - 1;
                            StabilizeSong.position = pos;
                            StabilizeSong.init_response(StabilizeActivity.this);
                            init_play();
                        }
                    });// ???????????? ?????? ?????????
                }
                statusname.setText("?????? ??????: BPM ??????!");
                auto_play();
            } else {
                recyclerView.setVisibility(View.INVISIBLE);
                statusname.setText("?????? ??????: ??????!");
                songname.setText("");
                music.setImageResource(R.drawable.play);
                StabilizeSong.setArrayList(null);
                checkhigh = 0;
                checklow = 0;
                if (StabilizeSong.mediaPlayer != null) {
                    StabilizeSong.mediaPlayer.release();
                    StabilizeSong.mediaPlayer = null;
                }
            }
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "REALTIME-BPM Data");
        set.setColors(Color.GRAY);
        set.setLineWidth(3.0f);
        set.setCircleRadius(5.0f);
        set.setDrawValues(true);
        set.setCircleColor(Color.WHITE);
        set.setDrawHighlightIndicators(false);
        set.setDrawFilled(false);
        set.setFillAlpha(20);
        set.setHighlightLineWidth(50f);
        set.setValueTextSize(10f);
        return set;
    }

    public class CenteredTextLineChartRenderer extends LineChartRenderer {//?????? ????????? ?????????

        public CenteredTextLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
            super(chart, animator, viewPortHandler);
        }

//Modified drawValues Method
// Center label on coordinate instead of applying a valOffset

        @Override
        public void drawValues(Canvas c) {

            if (isDrawingValuesAllowed(mChart)) {

                List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();

                for (int i = 0; i < dataSets.size(); i++) {

                    ILineDataSet dataSet = dataSets.get(i);

                    float textHeight = dataSet.getValueTextSize();

                    if (!shouldDrawValues(dataSet) || dataSet.getEntryCount() < 1)
                        continue;

                    // apply the text-styling defined by the DataSet
                    applyValueTextStyle(dataSet);

                    Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                    // make sure the values do not interfear with the circles
                    int valOffset = (int) (dataSet.getCircleRadius() * 1.75f);

                    if (!dataSet.isDrawCirclesEnabled())
                        valOffset = valOffset / 2;

                    mXBounds.set(mChart, dataSet);

                    float[] positions = trans.generateTransformedValuesLine(dataSet, mAnimator.getPhaseX(), mAnimator
                            .getPhaseY(), mXBounds.min, mXBounds.max);
                    ValueFormatter formatter = dataSet.getValueFormatter();

                    MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                    iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                    iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                    for (int j = 0; j < positions.length; j += 2) {

                        float x = positions[j];
                        float y = positions[j + 1];

                        if (!mViewPortHandler.isInBoundsRight(x))
                            break;

                        if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                            continue;

                        Entry entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min);

                        if (dataSet.isDrawValuesEnabled()) {
                            //drawValue(c, formatter.getPointLabel(entry), x, y - valOffset, dataSet.getValueTextColor(j / 2));
                            drawValue(c, formatter.getPointLabel(entry), x, y + textHeight * 0.35f, dataSet.getValueTextColor(j / 2));
                        }

                        if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                            Drawable icon = entry.getIcon();

                            Utils.drawImage(
                                    c,
                                    icon,
                                    (int) (x + iconsOffset.x),
                                    (int) (y + iconsOffset.y),
                                    icon.getIntrinsicWidth(),
                                    icon.getIntrinsicHeight());
                        }
                    }

                    MPPointF.recycleInstance(iconsOffset);
                }
            }
        }
    }// class

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(//???????????? ?????? ??????
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    if (resultCode == RESULT_OK) {
                        CustomToast.showToast(getApplicationContext(), "??????????????? ????????????????????????.");
                    } else if (resultCode == RESULT_CANCELED) {
                        bluetoothswitchOn.setChecked(false);
                    }
                }
            });

    private void initOnOff() {//???????????? switch ?????????
        bluetoothswitchOn.setChecked(bluetoothAdapter.isEnabled());
        bluetoothswitchOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    launcher.launch(intent);
                } else
                    bluetoothAdapter.disable();
            }
        });
    }//initOnOff

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 100)
            return;
        if (grantResults.length >= 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            /*searchbuilder();*/
        }
    }

    public void searchbuilder() {//?????? ??????
        devices = bluetoothAdapter.getBondedDevices();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????? ???????????? ???????????? ???????????? ??????");
        List<String> list = new ArrayList<>();
        for (BluetoothDevice bluetoothDevice : devices) {
            list.add(bluetoothDevice.getName());
        }

        final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
        list.toArray(new CharSequence[list.size()]);

        builder.setItems(charSequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                connectDevice(charSequences[i].toString());
            }
        });

        builder.setCancelable(false);

        builder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void connectDevice(String deviceName) {
        for (BluetoothDevice tempDevice : devices) {
            if (deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            connectcheck = 1;
            receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler bluetoothHandler = new Handler(Looper.getMainLooper()) {//?????? ????????? ?????? ??? ????????? ????????????2
        public void handleMessage(Message msg) {
            String readMessage = null;
            try {
                readMessage = new String((byte[]) msg.obj, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            readMessage = readMessage.trim();

            int change_readMessage = Integer.parseInt(readMessage);
            chart.clear();
            copybpmsetup = bpmsetup;
            if (check < 30) {
                bpmsetup.add(change_readMessage);
                check++;
            } else {
                for (int i = 0; i <= 28; i++) {
                    bpmsetup.set(i, copybpmsetup.get(i + 1));
                }
                bpmsetup.set(29, change_readMessage);
            }
            sum = 0;
            for (int i = 0; i < bpmsetup.size(); i++) {
                sum += bpmsetup.get(i);
            }
            init_chart();
            averagebpm = (int) (sum / bpmsetup.size());
        }
    };

    public void receiveData() {//?????? ????????? ?????? ??? ????????? ????????????1
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;

                while (true) {
                    try {
                        bytes = inputStream.available();
                        if (bytes != 0) {
                            SystemClock.sleep(100);
                            bytes = inputStream.available();
                            bytes = inputStream.read(buffer, 0, bytes);
                            bluetoothHandler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                        }
                    } catch (IOException e) {
                        String sMsg = "????????? ?????? ??? ????????? ??????????????????.";
                        byte[] buf = sMsg.getBytes();
                        break;
                    }
                }
            }
        });
        workerThread.start();
        CustomToast.showToast(getApplicationContext(), "?????? ??????");
    }

    @Override
    public void onDestroy() {//?????????????????? ????????? ??????
        try {
            if (bluetoothSocket != null) {
                outputStream.close();
                inputStream.close();
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            Log.i("error", "error ??????");
        }
        checklow = 0;
        checkhigh = 0;
        StabilizeSong.setArrayList(null);
        if (workerThread != null)
            workerThread.interrupt();
        if (StabilizeSong.mediaPlayer != null) {
            StabilizeSong.mediaPlayer.release();
            StabilizeSong.mediaPlayer = null;
        }
        super.onDestroy();
    }

    public int getAge(int birthYear, int birthMonth, int birthDay)//????????? ?????????
    {
        Calendar current = Calendar.getInstance();

        int currentYear = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH) + 1;
        int currentDay = current.get(Calendar.DAY_OF_MONTH);

        // ??? ?????? ????????? 2022-1995=27 (?????????-????????????)
        int age = currentYear - birthYear;
        // ?????? ????????? ????????? ???????????? -1
        if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay)
            age--;
        // 5??? 26??? ?????? 526
        // ???????????? 5??? 25?????? 525
        // ??? ?????? ?????? ?????? ??? ????????? ??? ??? ?????? ????????? ????????? ?????? ?????????.
        return age;
    }

    public void init_chart() {//?????? ?????????
        for (int i = 0; i < bpmsetup.size(); i++) {
            addEntry(bpmsetup.get(i));
        }
    }

    public void init_play() {
        if (StabilizeSong.arrayList == null) {

        } else {
            if (StabilizeSong.mediaPlayer != null) {
                songname.setText("?????? ???????????? ??????: " + StabilizeSong.getName());
                music.setImageResource(R.drawable.pause);
            } else {
                songname.setText("?????? ???????????? ??????: " + StabilizeSong.getName());
                music.setImageResource(R.drawable.play);
            }
        }
    }

    public void init_previous_next() {
        if (StabilizeSong.arrayList == null) {

        } else {
            songname.setText("?????? ???????????? ??????: " + StabilizeSong.getName());
            music.setImageResource(R.drawable.pause);
        }
    }

    public boolean isConnected(BluetoothDevice device) {// ???????????? ?????? ??????
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {// ?????????????????? ???????????? ????????? ?????? ??????(?)
        if (connectcheck == 1) {
            if (isConnected(bluetoothDevice)) {

            } else {
                initOnOff();
                datachart.setVisibility(View.INVISIBLE);
                music.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
                previous.setVisibility(View.INVISIBLE);
                connectcheck = 0;
                CustomToast.showToast(getApplicationContext(), "???????????? ????????? ???????????????.");
                try {
                    if (bluetoothSocket != null) {
                        outputStream.close();
                        inputStream.close();
                        bluetoothSocket.close();
                    }
                } catch (IOException e) {
                    Log.i("error", "error ??????");
                }
                bpmsetup = null;
                copybpmsetup = null;
                averagebpm = 0;
                sum = 0;
                check = 0;
                recyclerView.setVisibility(View.INVISIBLE);
                averageview.setText("");
                statusname.setText("");
                songname.setText("");
                checklow = 0;
                checkhigh = 0;
                StabilizeSong.setArrayList(null);
                if (workerThread != null)
                    workerThread.interrupt();
                if (StabilizeSong.mediaPlayer != null) {
                    StabilizeSong.mediaPlayer.release();
                    StabilizeSong.mediaPlayer = null;
                }
                bpmsetup = new ArrayList<Integer>();
                chart.invalidate();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stabilize_toolbar, menu);
        return true;
    }

    public void info(MenuItem item) {
        AlertDialog.Builder info = new AlertDialog.Builder(StabilizeActivity.this);
        info.setTitle("???????????? ?????? ?????????")
                .setMessage("???????????? ?????? ????????? ???????????????.\n1: ????????? ???????????? / ??????????????? ???????????? : ????????? 50% ??????" +
                        "\n2: ????????? ??????????????? ????????? ???????????? : ???.????????? 50-65%" + "\n3: ?????????????????? ?????? ???????????? ????????? ???????????? : ????????? 65-85%"
                        + "\n4: ??????????????? ?????? ???????????? ????????? ????????????(?????????/?????????): ????????? 85-100%" + "\n???????????? ?????? ????????? ??????????????? 1?????????.");
        info.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        info.show();
    }

    public void setting(MenuItem item) {
        final Switch SettingSwitch = new Switch(StabilizeActivity.this);
        AlertDialog.Builder setting = new AlertDialog.Builder(StabilizeActivity.this);
        setting.setTitle("???????????? ??????")
                .setView(SettingSwitch);

        if (AutoPlay.getSet() == 1) {
            SettingSwitch.setChecked(true);
        } else {
            SettingSwitch.setChecked(false);
        }

        setting.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (SettingSwitch.isChecked()) {
                    AutoPlay.setSet(1);
                } else {
                    AutoPlay.setSet(0);
                }
            }
        });

        setting.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        setting.show();
    }

    public void onArrayListObtained(ArrayList<Stabilize> items) {
        StabilizeSong.setArrayList(items);

        if (StabilizeSong.mediaPlayer == null && AutoPlay.getSet() == 1 && StabilizeSong.getArrayList() != null) {
            int randomdata = (int) (Math.random() * (adapter.getItemCount() - 1));
            StabilizeSong.itemcount = adapter.getItemCount() - 1;
            StabilizeSong.position = randomdata;
            StabilizeSong.setName(StabilizeSong.arrayList.get(randomdata).getName());
            StabilizeSong.setGenre(StabilizeSong.arrayList.get(randomdata).getGenre());
            StabilizeSong.setRoute(StabilizeSong.arrayList.get(randomdata).getRoute());
            StabilizeSong.mediaPlayer = MediaPlayer.create(this, Song.resid[StabilizeSong.getGenre()][StabilizeSong.getRoute()]);
            StabilizeSong.mediaPlayer.setLooping(true);
            StabilizeSong.mediaPlayer.start();
            music.setImageResource(R.drawable.pause);
            songname.setText("?????? ???????????? ??????: " + StabilizeSong.getName());
        }
    }

    public void auto_play() {
        if (StabilizeSong.mediaPlayer == null && AutoPlay.getSet() == 1 && StabilizeSong.getArrayList() != null) {
            int randomdata = (int) (Math.random() * (adapter.getItemCount() - 1));
            StabilizeSong.itemcount = adapter.getItemCount() - 1;
            StabilizeSong.position = randomdata;
            StabilizeSong.setName(StabilizeSong.arrayList.get(randomdata).getName());
            StabilizeSong.setGenre(StabilizeSong.arrayList.get(randomdata).getGenre());
            StabilizeSong.setRoute(StabilizeSong.arrayList.get(randomdata).getRoute());
            StabilizeSong.mediaPlayer = MediaPlayer.create(this, Song.resid[StabilizeSong.getGenre()][StabilizeSong.getRoute()]);
            StabilizeSong.mediaPlayer.setLooping(true);
            StabilizeSong.mediaPlayer.start();
            music.setImageResource(R.drawable.pause);
            songname.setText("?????? ???????????? ??????: " + StabilizeSong.getName());
        }
    }
}