package com.example.fzy.retrofitdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonReg;
    private Button buttonPic;
    private Button buttonFile;
    private ImageView imageView;
    EditText name;
    EditText password;
    private Call<ResponseBody> call;
    private Call<ResponseBody> imageCall;
    private Call<ResponseBody> fileCall;
    private InputStream is;
    //图片保存的路径
    private final static String ALBUM_PATH
            = Environment.getExternalStorageDirectory() + "/download_test/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        buttonReg = (Button) findViewById(R.id.button_get_reg);
        name = (EditText) findViewById(R.id.editText_username);
        password = (EditText) findViewById(R.id.editText_pwd);
        buttonPic = (Button) findViewById(R.id.button_get_pic);
        buttonFile = (Button) findViewById(R.id.button_get_file);
        imageView = (ImageView) findViewById(R.id.imageView_login);
        buttonReg.setOnClickListener(this);
        buttonPic.setOnClickListener(this);
        buttonFile.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_get_reg:
                String qq = name.getText().toString();
                String pwd = password.getText().toString();
                call = ApiHome.getInstance().getData(qq, pwd);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String result = response.body().string();
                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });

                break;
            case R.id.button_get_pic:
                imageCall = ApiHome.getInstance().getImage();
                imageCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            byte[] be = response.body().bytes();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(be, 0, be.length);
                            imageView.setImageBitmap(bitmap);
                            saveBitmapToSDCard(bitmap, response.body().string());
                            Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                break;
            case R.id.button_get_file:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            fileCall = ApiHome.getInstance().getMp4();
                            Response<ResponseBody> response = fileCall.execute();
                            is = response.body().byteStream();
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), "1.mp4");
                            if (writtenToDisk) {
                                //动态注册广播
                                Intent intent = new Intent();
                                intent.setAction("com.example.fzy.retrofitdemo");
                                //之所以用Bundle,是为了让MyDownloadReceiver类中调用系统播放器,能根据传过来的路径进行播放
                                Bundle bundle = new Bundle();
                                String filePath = getExternalFilesDir(null) + File.separator + "1.mp4";
                                //传输数据,1是接收的关键键 ,2是文件路径
                                bundle.putString("path", filePath);
                                intent.putExtras(bundle);
                                sendBroadcast(intent);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

              /*  fileCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.e("Download", "server contacted and has file");
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), "1.mp4");
                            Log.e("Download", "file download was a success? " + writtenToDisk);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }).start();

                        } else {
                            Log.e("Download", "server contact failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Log.e("Download", "error :" + t.getMessage());
                    }
                });*/
                break;
        }
    }

    //文件保存到本地
    private boolean writeResponseBodyToDisk(ResponseBody body, String savaName) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + savaName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[100000];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.w("saveFile", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 保存bitmap到SD卡
     *
     * @param bitmap
     * @param imagename
     */
    public static String saveBitmapToSDCard(Bitmap bitmap, String imagename) {
        String path = "/sdcard/" + "img-" + imagename + ".jpg";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }

            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }
    }
}