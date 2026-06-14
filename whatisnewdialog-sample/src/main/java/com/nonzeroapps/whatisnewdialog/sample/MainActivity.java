package com.nonzeroapps.whatisnewdialog.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nonzeroapps.whatisnewdialog.sample.R;
import com.nonzeroapps.whatisnewdialog.NewItemDialog;
import com.nonzeroapps.whatisnewdialog.object.NewFeatureItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create and show the dialog.

        ArrayList<NewFeatureItem> arrayList = new ArrayList<>();

        NewFeatureItem newFeatureItem = new NewFeatureItem();
        newFeatureItem.setFeatureDesc("From now on, you can search all things with keys. For searching please go to ");
        newFeatureItem.setFeatureTitle("Searching");
        newFeatureItem.setImageResource(R.drawable.androidpicture);
        arrayList.add(newFeatureItem);

        NewFeatureItem newFeatureItem2 = new NewFeatureItem();
        newFeatureItem2.setFeatureTitle("Feature 2");
        newFeatureItem2.setFeatureDesc("You waited long for this feature, we know that!!!\n\n From now on, you can follow your friend with our application. This makes our application super and cool. Don't believe my words, try and see it. If you want another features like this please contact with us via e-mail or feedback button.");
        newFeatureItem2.setImageResource("https://f1gr.hjfile.cn/pic/20170906/201709060335318628.gif");
        arrayList.add(newFeatureItem2);

        NewItemDialog
                .init(this)
                .setVersionName("1.2.0")
                .setDialogTitle("New Features of 1.2.0 Version!")
                .setPositiveButtonTitle("Close")
                .setNeutralButtonTitle("Show Me Later")
                .setCancelable(false)
                .setItems(arrayList)
                .setCancelButtonListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Close Clicked", Toast.LENGTH_LONG).show();
                    }
                })
                .setShowLaterButtonListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Remind Me Later Clicked", Toast.LENGTH_LONG).show();
                    }
                })
                .showDialogIfConditionsSuitable(this);

    }
}
