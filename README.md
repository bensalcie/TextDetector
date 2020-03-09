# Any Image Text Detector
This is a simple app that will help developers learn how to detect and extract text from images using Android-Kotlin and Firebase Machine Learning Kit via Google Cloud Platforms (GCP)

#Text Detection on Images.
Requirements:
1.Android Studio (Fully Setup)
2.Basic Knowledge of Kotlin
3.Basic Interaction with Firebase
4.Google cloud platform account (Billed)


SETUP FIREBASE AND GOOGLE CLOUD PLATFORM:
1.Visit https://firebase.google.com/ and create an account (if you don’t have one.)->Go to get started →Add project (Give it a desired name, i gave mine:my-project)
create a new project then give it any desired name
2. Access the console of your Firebase account, locate and navigate to ML-KIT →get started. From the following interface you will see a list of available ML Apis available from google, like Text Detection,Face recognition e.t.c. We will however just tackle one. (Text Detection)
3. To enable text detection, we need to enable the vision API from google cloud platform console here https://console.cloud.google.com/ (Make sure to click on console).
On the interface that appears, click on search , then type ‘Cloud Vision Api’ as shown.

Search for cloud vision api
Once its loaded, click on ‘Enable API’.




#ANDROID STUDIO SETUP.
In order for us to use machine learning in our android studio, we need a couple of libraries to help us to achieve our first ML Project.
So open up your android studio, Create a new project, Can be a blank activity or any other that you decide on. Make sure to select API version 21 or higher for ML support. Make sure to tick Kotlin as the Language as well, as we will be doing this tutorial in Kotlin.

Select Kotlin as Language and API 21 or Higher
First things first:
Open up your manifest folder from the project tree structure and add these permissions and a meta tag. This meta tag will help us to download the models when the user first installs the application, which will make our work easier. The permissions will allow our app to access to the internet via the users phone.
#//These ones will come before the application tags
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

#//add the meta tags before initiating your activity
<meta-data
    android:name="com.google.firebase.ml.vision.DEPENDENCIES"
    android:value="text,face,label" />

#Your manifest file editted.
To utilize firebase features in our app , we will need to connect our app to your already created project on firebase by navigating to :
Tools →Firebase →(Side Menu appears) →Go to Analytics →Log an analysis event →Connect to Firebase(Wait for it to sync) →Add analytics to your app (Let it sync as well)
We will also need Picasso library (To load our images),Anko library (To help us to structure our Kotlin Syntax with simplicity).So navigate to build.gradle (Module app) and add the following lines. These lines will import all the above mentioned libraries.
implementation 'com.google.firebase:firebase-ml-vision:16.0.0'
implementation 'com.squareup.picasso:picasso:2.5.2'
implementation 'org.jetbrains.anko:anko-commons:0.10.5'
Make sure your android studio syncs correctly after adding the above.

#Your dependancies should look like this
#How your gradle files should look like.
In android, we will need one buttion, and imageview and an edit text on which we will be interacting with our app. The edit text will play a key role in getting us the images and the button will help us detect the text once the image is loaded. Open your activity_main.xml and edit with the following code. (Customize as per your wish)
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
    <EditText
        android:layout_width="match_parent"
        android:imeOptions="actionDone"
        android:id="@+id/etUrl"
        android:inputType="textUri"
        android:hint="Enter url"
        android:layout_marginLeft="10dp"
        android:layout_marginRight="10dp"
        android:layout_alignParentTop="true"
        android:layout_marginTop="20dp"
        android:layout_height="wrap_content"/>
    <ImageView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="10dp"
        android:scaleType="centerCrop"
        android:layout_below="@+id/etUrl"
        android:id="@+id/image_holde"
        />

    <Button
        android:layout_width="match_parent"
        android:id="@+id/btnDetect"
        android:text="DETECT TEXT"
        android:textColor="#ffffff"
        android:layout_margin="20dp"
        android:backgroundTint="@color/colorAccent"
        android:background="@drawable/button_background"
        android:layout_alignParentBottom="true"
        android:layout_height="wrap_content"/>

</RelativeLayout>

#Visual representation
For the above, to make the button little rounded, i created a drawable resource file button_background and played around with the shape and edges as shown:


<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">

    <corners android:radius="16dp" android:topLeftRadius="25dp" android:bottomRightRadius="25dp"/>
</shape>


Now that we are done with the layout parts, lets mavigate to the package and locate: java →…MainActivity.kt, Here is where we will give all the functionality to do the text detection.
We start it off my creating an instance of the FirebaseVision with the functionality to take text from image as shows, above the onCreate() function:
val detector=FirebaseVision.getInstance().visionTextDetector
Next we have to allow the user enter some image Url into the input field, and we will allow the user to press done button of the virtual keyboad, and listen to every time this user presses it, once this user presses done, our app will load the desired image from the url entered and display it using Picasso Library we earlier on added. (We gave it an id of etUrl) and an attribute of action done, and input type of textUrl
etUrl.setOnEditorActionListener { _, action, _ ->

    if (action==EditorInfo.IME_ACTION_DONE)
    {
        Picasso.with(ctx).load(etUrl.text.toString()).into(image_holde)
Once the image is loaded into our image view, we then ask our detector, earlier on declared to try detect any readable text from the image, and by this we will loop through this image to get as much data as possible as shown below. Remember, we have to convert this image from picasso to a bitmap so that they can communicate correctly with Firebase Image conversion process as shown.
btnDetect.setOnClickListener {

  val textImage=FirebaseVisionImage.fromBitmap((image_holde.drawable as BitmapDrawable).bitmap)
Once we get the required bitmap, for this case textImage, we then tell the detector to detect the text in the image and request for a listener which will respond by giving us either a success of a failure depending on the prevailing conditions. We will then loop through the responce we reciece and for every moment we get the data, we assign it to a variable: detected text.
detector.detectInImage(textImage).addOnCompleteListener {

                    var detectedText = ""
                    it!!.result!!.blocks!!.forEach {
                        detectedText += it.text + "\n"

                        ctx!!.runOnUiThread {
                            alert(detectedText, "DETECTED TEXT").show()
                        }
                    }
                }.addOnFailureListener {
                    ctx!!.runOnUiThread {

                        alert("Unable to detect Text", "ERROR DURING DETECTION").show()

                    }
                }
            }

            detector.close()


            true
        }




        false }
}
Of course we have used Anko library to create the alerts to show our detected text, which runs on the UI thread.
#The full code for our MainActivity.kt will be as shown below:
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
                Picasso.with(ctx).load(etUrl.text.toString()).into(image_holde)

                btnDetect.setOnClickListener {

                    val textImage=FirebaseVisionImage.fromBitmap((image_holde.drawable as BitmapDrawable).bitmap)

                    detector.detectInImage(textImage).addOnCompleteListener {

                        var detectedText = ""
                        it!!.result!!.blocks!!.forEach {
                            detectedText += it.text + "\n"

                            ctx!!.runOnUiThread {
                                alert(detectedText, "DETECTED TEXT").show()
                            }
                        }
                    }.addOnFailureListener {
                        ctx!!.runOnUiThread {

                            alert("Unable to detect Text", "ERROR DURING DETECTION").show()

                        }
                    }
                }

                detector.close()


                true
            }




            false }
    }
}
Having gone though all this, when you run your application, you should be able to detect any text from an image from any URL.
Remember, when using Firebase ML Kit , detection is done either on the device or on Googles cloud Platform, for our case, we have been using on device detection, which is convenient for faster loading data models. You could also train your own model and upload it on firebase ML kit and use it just as we have used the text detection one.
Below is an illustration of the app we have just made:


#First Demo of Detected Text


Minute Maid Bottle text detected.

You definatley need a big clap for reaching this end, Hope you learnt something.
If you had any problem during this tutorial please write back to me:
Twitter:@ibensalcie
Email:bensalcie@gmail.co,
WhatsApp number:+254704808070
