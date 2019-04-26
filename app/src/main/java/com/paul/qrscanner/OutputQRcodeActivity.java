package com.paul.qrscanner;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class OutputQRcodeActivity extends AppCompatActivity {

    private EditText editText1,editText2;
    private Button button;
    private ImageView imageView;
    private RSAutill rsa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_qrcode);
        init();
    }
    void init()
    {
        rsa=new RSAutill(OutputQRcodeActivity.this);
        editText1=findViewById(R.id.et_output_user);
        editText2=findViewById(R.id.et_output_name);
        imageView=findViewById(R.id.iv_output);
        button=findViewById(R.id.btn_output_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String count = editText1.getText().toString().trim()+";"+editText2.getText().toString().trim();
                if (TextUtils.isEmpty(count)) {
                    Toast.makeText(OutputQRcodeActivity.this, "请输入内容", Toast.LENGTH_LONG).show();
                    return;
                }
                //生成二维码显示在imageView上
                try {
                    imageView.setImageBitmap(generateBitmap(rsa.RSAEncrypt(count), 600, 600));
                    //imageView.setImageBitmap(generateBitmap(count, 600, 600));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 生成固定大小的二维码(不需网络权限)
     *
     * @param content 需要生成的内容
     * @param width   二维码宽度
     * @param height  二维码高度
     * @return
     */
    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
