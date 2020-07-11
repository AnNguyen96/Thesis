package com.example.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GalleryDetect extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    ImageView viewImage;
    Button b;
    Net tinyYolo;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detect);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Picture Detect");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);



        b=(Button)findViewById(R.id.btnSelectPhoto);
        viewImage=(ImageView)findViewById(R.id.viewImage);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        Log.d(TAG, "place wayyy outside:"+viewImage);
    }

    private void selectImage() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        final CharSequence[] options = {"Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(GalleryDetect.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
//                if (options[item].equals("Take Photo"))
////                {
////                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
////                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
////                    startActivityForResult(intent, 1);
////                }
////                else
                if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                    Log.d(TAG, "place choose:"+viewImage);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                    Log.d(TAG, "place inside:"+viewImage);
                }
            }
        });
        Log.d(TAG, "place outside:"+viewImage);

        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            if (requestCode == 1) {
//                File f = new File(Environment.getExternalStorageDirectory().toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals("temp.jpg")) {
//                        f = temp;
//                        break;
//                    }
//                }
//                try {
//                    Bitmap bitmap;
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
//                            bitmapOptions);
//                    viewImage.setImageBitmap(bitmap);
//                    String path = android.os.Environment
//                            .getExternalStorageDirectory()
//                            + File.separator
//                            + "Phoenix" + File.separator + "default";
//                    f.delete();
//                    OutputStream outFile = null;
//                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
//                    try {
//                        outFile = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
//                        outFile.flush();
//                        outFile.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else
            if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));


                Mat frame = new Mat();
                Utils.bitmapToMat(thumbnail, frame);
                String tinyYoloCfg = Environment.getExternalStorageDirectory() + "/Download/yolo-obj10.cfg" ;
                String tinyYoloWeights = Environment.getExternalStorageDirectory() + "/Download/yolo-obj10_last.weights";
                tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);

                Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);


                Mat imageBlob = Dnn.blobFromImage(frame, 1.0/255.0, new Size(416,416),new Scalar(0, 0, 0),/*swapRB*/false, /*crop*/false);


                tinyYolo.setInput(imageBlob);



                java.util.List<Mat> result = new java.util.ArrayList<Mat>(3);

                List<String> outBlobNames = new java.util.ArrayList<>();
                outBlobNames.add(0, "yolo_82");
                outBlobNames.add(1, "yolo_94");
                outBlobNames.add(2, "yolo_106");

                tinyYolo.forward(result,outBlobNames);


                float confThreshold = 0.3f;



                List<Integer> clsIds = new ArrayList<>();
                List<Float> confs = new ArrayList<>();
                List<Rect> rects = new ArrayList<>();




                for (int i = 0; i < result.size(); ++i)
                {

                    Mat level = result.get(i);

                    for (int j = 0; j < level.rows(); ++j)
                    {
                        Mat row = level.row(j);
                        Mat scores = row.colRange(5, level.cols());

                        Core.MinMaxLocResult mm = Core.minMaxLoc(scores);




                        float confidence = (float)mm.maxVal;


                        Point classIdPoint = mm.maxLoc;



                        if (confidence > confThreshold)
                        {
                            int centerX = (int)(row.get(0,0)[0] * frame.cols());
                            int centerY = (int)(row.get(0,1)[0] * frame.rows());
                            int width   = (int)(row.get(0,2)[0] * frame.cols());
                            int height  = (int)(row.get(0,3)[0] * frame.rows());


                            int left    = centerX - width  / 2;
                            int top     = centerY - height / 2;

                            clsIds.add((int)classIdPoint.x);
                            confs.add((float)confidence);




                            rects.add(new Rect(left, top, width, height));
                        }
                    }
                }
                int ArrayLength = confs.size();

                if (ArrayLength>=1) {
                    // Apply non-maximum suppression procedure.
                    float nmsThresh = 0.2f;




                    MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));


                    Rect[] boxesArray = rects.toArray(new Rect[0]);

                    MatOfRect boxes = new MatOfRect(boxesArray);

                    MatOfInt indices = new MatOfInt();



                    Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices);


                    // Draw result boxes:
                    int[] ind = indices.toArray();
                    for (int i = 0; i < ind.length; ++i) {

                        int idx = ind[i];
                        System.out.print(idx);
                        Rect box = boxesArray[idx];

                        int idGuy = clsIds.get(idx);

                        float conf = confs.get(idx);


                        List<String> cocoNames = Arrays.asList("BachKhoa","CircleK","Highland","TGDD","MiniStop","Aeon","SevenEleven",
                                "PhucLong","ShopGo","Starbucks");



                        int intConf = (int) (conf * 100);



                        Imgproc.putText(frame,cocoNames.get(idGuy) + " " + intConf + "%",box.tl(),Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255,255,0),2);



                        Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(255, 0, 0), 2);


                    }
                }







                Utils.matToBitmap(frame, thumbnail);
                viewImage.setImageBitmap(thumbnail);

                Log.d("Matrix", "place thumbnail:"+frame);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
