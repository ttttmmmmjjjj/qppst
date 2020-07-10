package com.hsic.tmj.qppst;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hsic.camera.CameraManager;
import com.hsic.decoding.CaptureActivityHandler;
import com.hsic.decoding.InactivityTimer;
import com.hsic.utils.DESCrypt;
import com.hsic.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

public class ScanQRCodeActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final int REQUEST_CODE_SCAN_GALLERY = 100;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private ImageButton back;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Button cancelScanButton;
    private String Tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();//上门配送：1   安检：2  (区分二维码扫描时使用)
        Tag = i.getStringExtra("RequestMode");
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.scanner_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        //quit the scan view
        cancelScanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                if (Tag.equals("sale")) {
                    data.putExtra("SaleID","");
                    setResult(6,data);
                    ScanQRCodeActivity.this.finish();
                }else{
                    data.putExtra("userID","");
                    setResult(6,data);
                    ScanQRCodeActivity.this.finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent data = new Intent();
            if (Tag.equals("sale")) {
                data.putExtra("SaleID","");
                setResult(6,data);
                ScanQRCodeActivity.this.finish();
            }else{
                data.putExtra("userID","");
                setResult(6,data);
                ScanQRCodeActivity.this.finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        try {
            inactivityTimer.onActivity();
            playBeepSoundAndVibrate();
            String resultString = result.getText();
            String ScanReulst = "";
            ScanReulst = resultString;
            if (Tag.equals("sale")) {
                Intent data = new Intent();
                data.putExtra("SaleID", resultString);
                setResult(6, data);
            } else {
                //用户卡解密方式
                String allData = "";
                String code = "";// 要解密的字符串
                String password = "54033336";
                DESCrypt descrypt = new DESCrypt();
                String userID = "";
                String company = "";
                String xjCode = "";

                String[] str = resultString.split("[?]");
                resultString = str[1];
                // 1.先判断字符串是否符合标准
                if (resultString != null && ScanReulst.contains(str[0])) {
                    try {
                        allData = descrypt.decrypt(resultString, password);


                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (!allData.contains("&")) {
                        Toast.makeText(ScanQRCodeActivity.this, "非奉贤液化气用户卡",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] str_all = allData.split("&");
                    company = str_all[0];
                    userID = str_all[1];
                    xjCode = str_all[2];
                    ;
                    if (xjCode.equals("jy=hsic8888")) {
                        if (company.equals("company=312005")) {
                            //"CustomerID=00000194"
                            String[] s2 = userID.split("=");
                            userID = s2[1];
                            int RESULT_CODE = 0;
                            Intent intent = new Intent();
                            intent.putExtra("userID", userID);
                            setResult(RESULT_CODE, intent);
                        } else {
                            Toast.makeText(ScanQRCodeActivity.this, "非奉贤液化气用户卡",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ScanQRCodeActivity.this, "非华申授权!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ScanQRCodeActivity.this, "非用户卡!",
                            Toast.LENGTH_SHORT).show();
                }
            }
            ScanQRCodeActivity.this.finish();
        } catch (Exception ex) {
            ScanQRCodeActivity.this.finish();
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(surfaceHolder);
        }
    }
}
