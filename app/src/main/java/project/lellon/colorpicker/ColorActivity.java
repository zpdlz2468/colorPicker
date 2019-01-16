package project.lellon.colorpicker;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import top.defaults.colorpicker.ColorPickerPopup;

public class ColorActivity extends AppCompatActivity {

    Button btn_back;
    Button btn_send;
    ImageView img_color;
    int red,blue,green;
    int rgb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color); //activity_color Layout 리소스 사용하여 배치.


        //레이아웃 내 선언된 각 뷰에 대한 인스턴스를 받아옴.
        btn_back = findViewById(R.id.back);
        btn_send = findViewById(R.id.send);
        img_color = findViewById(R.id.color);

        btn_back.setOnClickListener(new View.OnClickListener() { //뒤로가기 버튼을 클릭 했을 때
            @Override
            public void onClick(View v) { //호출되는 메소드
                finish(); //현재 액티비티만 종료.
            }
        });

        btn_send.setOnClickListener(new Button.OnClickListener() { //색상 보내기 버튼을 클릭 했을 때
            @Override
            public void onClick(View v) { //호출되는 메소드
                new ColorPickerPopup.Builder(getApplicationContext()) //ColorPickerPopup 구성 초기화 과정.
                        .initialColor(Color.RED) // 색상 초기화
                        .enableBrightness(true) // 밝기 슬라이더 화성화
                        .enableAlpha(true) // 알파값 슬라이더 활성화
                        .okTitle("") // OK 텍스트 설정
                        .cancelTitle("") //취소 텍스트 설정
                        .showIndicator(false) //색상을 보여줄건지 말건지
                        .showValue(true) //색상에 대해 값을 보여줄건지 말건지
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() { //ColorPicker를 객체 생성함
                            @Override
                            public void onColorPicked(int color) { //Color를 선택하고 OK 버튼을 눌렀을 떄
                                img_color.setColorFilter(color, PorterDuff.Mode.SRC_IN); //pallete 이미지 색상 변경
                                ((MainActivity)MainActivity.mContext).DataReceivedListener(getByteColor(color)); //MainActivity에 있는 블루투스로 색 전달.

                            }

                            @Override
                            public void onColor(int color, boolean fromUser) { //ColorPicker 위에서 Color를 선택 했을 떄
                                if(color != rgb) {
                                    rgb = color; //color값을 rgb로 저장
                                    img_color.setColorFilter(rgb, PorterDuff.Mode.SRC_IN);//pallete 이미지 색상 변경
                                    ((MainActivity)MainActivity.mContext).DataReceivedListener(getByteColor(rgb)); //MainActivity에 있는 블루투스로 색 전달.
                                }
                            }
                        });
            }
        });


    }

    public String getByteColor(int color){ //아두이노가 RGB색을 따로 구분할수 있도록 commma를 찍어서 리턴.
        red = Color.red(color);
        blue = Color.blue(color);
        green = Color.green(color);
        return red + "," + green + "," + blue;
    }

}
