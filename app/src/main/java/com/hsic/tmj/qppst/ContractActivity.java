package com.hsic.tmj.qppst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.hsic.picture.BitmapUtilities;
import com.hsic.picture.ImageAdapter;
import com.hsic.picture.ImageShowManager;
import com.hsic.picture.PictureHelper;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.AJDB;
import com.hsic.utils.PathUtil;
import com.hsic.utils.TimeUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ContractActivity extends AppCompatActivity {
    private GridView gv_iamge;
    private Button btn_take, btn_upload;
    private String relationID, userID, deviceID;
    private GetBasicInfo getBasicInfo;
    AJDB ajdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);
        getBasicInfo=new GetBasicInfo(this);
        deviceID=getBasicInfo.getDeviceID();
        ajdb=new AJDB(this);
        userID="HS000001";
        relationID=deviceID + "u" + userID;//关联ID
        /**
         * 设置照片适配器
         */
        gv_iamge = this.findViewById(R.id.gv_image);
        btn_take = this.findViewById(R.id.btn_take);
        btn_take.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //拍照
                takePhotoes(userID);
            }
        });
        btn_upload = this.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传
            }
        });
        initGridViewData();
        gv_iamge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //预览照片
                Intent in=new Intent(ContractActivity.this,SBigPreViewActivity.class);
                in.putExtra("pathName",parent.getItemAtPosition(position).toString());
                in.putExtra("SaleID","");
                in.putExtra("RelationID",relationID);
                in.putExtra("UserID",userID);
                startActivity(in);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initGridViewData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String time= TimeUtils.getTime("yyyyMMddHHmmss");
                saveImagePath(filePath,deviceID,getBasicInfo.getOperationID(),userID,time);
                initGridViewData();
            }
        }

    }
    /**
     *
     */
    private String filePath;
    @SuppressLint("SimpleDateFormat")
    public void takePhotoes(String user) {
        filePath = Environment.getExternalStorageDirectory() + "/photoes/"
                + deviceID + "s" + ".jpg";
        File file = new File(filePath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(Environment.getExternalStorageDirectory(),
                "/photoes/");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // 保存图片的位置
        startActivityForResult(intent, 1);
    }
    public void  saveImagePath(String filePath,String deviceid,String employee, String user,String format) {
        if (user != null) {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                String path = PathUtil.getContractPath();
                File file1 = new File(path);
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                String ImageFileName=relationID+"_" +format + "_" + "contract" + ".jpg";
                File file2;
                file2 = new File(file1.getPath(), ImageFileName);
                PictureHelper.compressPicture(file.getAbsolutePath(),
                        file2.getAbsolutePath(), 720, 1280);
                if (file.exists()) {
                    file.delete();
                }
                File fileDir = new File(
                        Environment.getExternalStorageDirectory(), "/photoes/");
                if (fileDir.exists()) {
                    fileDir.delete();
                }
                String FileName=PathUtil.getContractPath();
                ajdb.InsertAJAssociation(employee, userID, ImageFileName, relationID,FileName);//将照片信息插入到数据表中
            }
        }
    }
    ArrayList<String>  mList;
    ContractImageAdapter contractImageAdapter;
    private void initGridViewData() {
        mList= new ArrayList<String>();
        String filePath= PathUtil.getContractPath();
        File file = new File(filePath);
        String[] paths = file.list();
        if (paths != null && paths.length > 0) {
            for (int i = 0; i < paths.length; i++) {
                String path = filePath+ paths[i];
                if (path.contains(relationID)) {
                    mList.add(path);
                }
            }
        }
        contractImageAdapter=new ContractImageAdapter(mList);
        gv_iamge.setAdapter(contractImageAdapter);
    }
    /**
     * 照片适配器
     */
    private class ContractImageAdapter extends BaseAdapter {
        private ArrayList<String> paths;
        private int size;
        private ImageShowManager imageManager;
        private LayoutInflater li;

        public ContractImageAdapter(ArrayList<String> paths) {
            this.paths = paths;
            size = paths.size();
            imageManager = ImageShowManager.from(ContractActivity.this);
            li = LayoutInflater.from(ContractActivity.this);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return size;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return paths.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            SurfaceHolder surfaceHolder = new SurfaceHolder();
            if (convertView != null) {
                surfaceHolder = (SurfaceHolder) convertView.getTag();
            } else {
                convertView = li.inflate(R.layout.image_item, null);
                surfaceHolder.iv = (ImageView) convertView
                        .findViewById(R.id.imageView1);

            }
            convertView.setTag(surfaceHolder);

            String path = paths.get(position);
            if (cancelPotentialLoad(path, surfaceHolder.iv)) {
                AsyncLoadImageTask task = new AsyncLoadImageTask(surfaceHolder.iv);
                surfaceHolder.iv.setImageDrawable(new LoadingDrawable(task));
                task.execute(path);
            }
            return convertView;
        }

        class SurfaceHolder {
            ImageView iv;
        }

        /**
         *
         * @param url
         * @param imageview
         * @return
         */
        private boolean cancelPotentialLoad(String url, ImageView imageview) {

            AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
            if (loadImageTask != null) {
                String bitmapUrl = loadImageTask.url;
                if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                    loadImageTask.cancel(true);
                } else {
                    return false;
                }
            }
            return true;
        }

        /**
         * @author Administrator
         */
        class AsyncLoadImageTask extends AsyncTask<String, Void, Bitmap> {

            private final WeakReference<ImageView> imageViewReference;
            private String url = null;

            public AsyncLoadImageTask(ImageView imageview) {
                super();
                imageViewReference = new WeakReference<ImageView>(imageview);
            }

            @Override
            protected Bitmap doInBackground(String... params) {

                Bitmap bitmap = null;
                this.url = params[0];

                bitmap = imageManager.getBitmapFromMemory(url);
                if (bitmap != null) {
                    return bitmap;
                }
                bitmap = imageManager.getBitmapFormDisk(url);
                if (bitmap != null) {
                    imageManager.putBitmapToMemery(url, bitmap);
                    return bitmap;
                }

                bitmap = BitmapUtilities.getBitmapThumbnail(url,
                        ImageShowManager.bitmap_width,
                        ImageShowManager.bitmap_height);
                imageManager.putBitmapToMemery(url, bitmap);
                imageManager.putBitmapToDisk(url, bitmap);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap resultBitmap) {
                if (isCancelled()) {
                    resultBitmap = null;
                }
                if (imageViewReference != null) {
                    ImageView imageview = imageViewReference.get();
                    AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
                    if (this == loadImageTask) {
                        imageview.setImageDrawable(null);
                        imageview.setImageBitmap(resultBitmap);
                    }
                }

                super.onPostExecute(resultBitmap);
            }
        }

        /**
         *
         * @param imageview
         * @return
         */
        private AsyncLoadImageTask getAsyncLoadImageTask(ImageView imageview) {
            if (imageview != null) {
                Drawable drawable = imageview.getDrawable();
                if (drawable instanceof ImageAdapter.LoadingDrawable) {
                    LoadingDrawable loadedDrawable = (LoadingDrawable) drawable;
                    return loadedDrawable.getLoadImageTask();
                }
            }
            return null;
        }

        /**
         * @author Administrator
         */
        class LoadingDrawable extends ColorDrawable {
            private final WeakReference<AsyncLoadImageTask> loadImageTaskReference;

            public LoadingDrawable(AsyncLoadImageTask loadImageTask) {
                super(Color.LTGRAY);
                loadImageTaskReference = new WeakReference<AsyncLoadImageTask>(
                        loadImageTask);
            }

            public AsyncLoadImageTask getLoadImageTask() {
                return loadImageTaskReference.get();
            }
        }

    }
}
