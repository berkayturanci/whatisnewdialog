package com.nonzeroapps.whatisnewdialog.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nonzeroapps.whatisnewdialog.NewItemDialog
import com.nonzeroapps.whatisnewdialog.model.NewFeatureItem
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val arrayList = ArrayList<NewFeatureItem>()

        val newFeatureItem = NewFeatureItem()
        newFeatureItem.featureDesc = "From now on, you can search all things with keys. For searching please go to "
        newFeatureItem.featureTitle = "Searching"
        newFeatureItem.setImageResource(R.drawable.androidpicture)
        arrayList.add(newFeatureItem)

        val newFeatureItem2 = NewFeatureItem()
        newFeatureItem2.featureTitle = "Feature 2"
        newFeatureItem2.featureDesc = "You waited long for this feature, we know that!!!\n\n From now on, you can follow your friend with our application. This makes our application super and cool. Don't believe my words, try and see it. If you want another features like this please contact with us via e-mail or feedback button."
        newFeatureItem2.imageResource = "https://f1gr.hjfile.cn/pic/20170906/201709060335318628.gif"
        arrayList.add(newFeatureItem2)

        NewItemDialog.init(this)
            .setVersionName("1.2.0")
            .setDialogTitle("New Features of 1.2.0 Version!")
            .setPositiveButtonTitle("Close")
            .setNeutralButtonTitle("Show Me Later")
            .setUsePaletteForDescBackground(false)
            .setUsePaletteForImageBackground(false)
            .setCancelable(false)
            .setItems(arrayList)
            .setCancelButtonListener { _, _ ->
                Toast.makeText(this@MainActivity, "Close Clicked", Toast.LENGTH_LONG).show()
            }
            .setShowLaterButtonListener { _, _ ->
                Toast.makeText(this@MainActivity, "Remind Me Later Clicked", Toast.LENGTH_LONG).show()
            }
            .showDialogIfConditionsSuitable(this)
    }
}
