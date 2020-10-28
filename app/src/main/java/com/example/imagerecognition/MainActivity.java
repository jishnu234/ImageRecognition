package com.example.imagerecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button process;
    private TextView description;
    private ImageView image;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        process=findViewById(R.id.buttonProcess);
        description=findViewById(R.id.description);
        image=findViewById(R.id.imageView);


        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");

                    startActivityForResult(Intent.createChooser(i,"select images"),121);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==121)
        {
                image.setImageURI(data.getData());

            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());

//                FirebaseVisionCloudImageLabelerOptions options =
//                new FirebaseVisionCloudImageLabelerOptions.Builder()
//                .setConfidenceThreshold(0.7f)
//                    .build();
                FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                        .getOnDeviceImageLabeler();

                labeler.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                description.setText("");
                                for (FirebaseVisionImageLabel label: labels) {
                                    String text = label.getText();
                                    String entityId = label.getEntityId();
                                    float confidence = label.getConfidence();
                                    description.append(text+"   "+confidence+"\n");
                                }


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onBackPressed() {

        if(doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        },2000);

        doubleBackToExitPressedOnce=true;
        Toast.makeText(this, "click again to exit", Toast.LENGTH_SHORT).show();

    }
}