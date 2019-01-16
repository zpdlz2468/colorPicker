package project.lellon.colorpicker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {
    Button btn;
    private BluetoothSPP bt; //블루투스 객체.
    String rgb;
    public static Context mContext; //acitivity 리소스를 얻어서 외부 Acitivity가 MainActivity를 참조하기 위함.
    boolean check; //블루투스와 연결 시도 중인지 알수 있도록 flag 설정.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//activity_main Layout 리소스 사용하여 배치.
        mContext = this;
        btn = findViewById(R.id.start); //레이아웃 내 선언된 각 뷰에 대한 인스턴스를 받아옴.
        bt = new BluetoothSPP(this); //블루투스 초기화

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "블루투스 사용 불가."
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) { //이 메소드로 데이터 받아옴.
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show(); //받아온 데이터를 toast로 표현.
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                check = false;
                Toast.makeText(getApplicationContext()
                        , name + "\n" + address + "(이)가 연결되었습니다."
                        , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ColorActivity.class); //ColorActivity로 화면 전환할 객체 생성
                startActivity(intent);//화면 전환
            }

            public void onDeviceDisconnected() { //연결해제
                check = false;
                Toast.makeText(getApplicationContext()
                        , "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                check = false;
                Toast.makeText(getApplicationContext()
                        , "연결이 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() { //시작하기 버튼을 눌렀을 떄
            public void onClick(View v) { //호출 되는 메소드
                if (!check) { //블루투스 연결 시도 중이 아닐 때
                    if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) { //블루투스 연결중이라면
                        bt.disconnect(); //해제
                    } else {
                        check = true;
                        Intent intent = new Intent(getApplicationContext(), DeviceList.class); //블루투스 라이브러리 안에 있는 DeviceList Acitivity로 화면전환할 객체 생성
                        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE); //객체 실행함과 동시에 파라미터로 요청코드 전달.
                    }
                }
            }
        });
    }

    public void DataReceivedListener(String color) { //ColorActivity에서 받은 파라미터 값을 블루투스 연결된 기기로 전송.
        bt.send(String.valueOf(color), true);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (check) { //연결 시도 중이라면
            Toast.makeText(MainActivity.this, "잠시 기다려 주세요.", Toast.LENGTH_SHORT).show();
        }
        if (!bt.isBluetoothEnabled()) { //블루투스가 활성화 상태가 아닐떄
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //블루투스 활성화 할수 있도록 화면 전환 객체 생성
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT); //화면 전환 객체 실행
        } else {
            if (!bt.isServiceAvailable()) {//활성화 되지 않았을 때
                bt.setupService();//블루투스 서비스 실행
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {//DeviceList Acitivity에서 리턴 받았을때 호출.
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) { //요첰코드가 블루투스 기기와 연결 가능하다면
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data); //그 기기와 연결
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) { //요청코드가 블루투스 사용 가능하다면
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService(); //블루투스 서비스 실행
                bt.startService(BluetoothState.DEVICE_OTHER);//DEVICE_ANDROID는 안드로이드 기기 끼리
            } else { //활성화 되지 않았을 때
                Toast.makeText(getApplicationContext()
                        , "블루투스가 활성화되지 않았습니다."
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }
}
