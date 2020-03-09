package bensalcie.likesyou.org.textdetector

import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.ctx
import org.jetbrains.anko.runOnUiThread


class MainActivity : AppCompatActivity() {

    val detector=FirebaseVision.getInstance().visionTextDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       etUrl.setOnEditorActionListener { _, action, _ ->

           if (action==EditorInfo.IME_ACTION_DONE)
           {
               Picasso.with(ctx).load(etUrl.text.toString()).into(image_holder)
               btnRecognizeText.setOnClickListener {

                   val textImage=FirebaseVisionImage.fromBitmap((image_holder.drawable as BitmapDrawable).bitmap)


                   detector.detectInImage(textImage).addOnCompleteListener{
                       var detectedText=""
                       it!!.result!!.blocks!!.forEach {
                           detectedText+=it.text+"\n"
                           ctx!!.runOnUiThread {
                               alert (detectedText,"DTETECTED TEXT").show()
                           }
                       }

                   }.addOnFailureListener {
                       ctx!!.runOnUiThread {
                           alert ("an error occured","DTETECTED TEXT").show()
                       }
                   }

                   true
               }
           }




           false }

    }
}
