package application.chandra.covidtracker.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import application.chandra.covidtracker.R
import com.airbnb.lottie.LottieAnimationView

class SplashScreen : AppCompatActivity() {

    //first
    lateinit var relativeOne : RelativeLayout
    lateinit var forward : LottieAnimationView
    lateinit var mask : LottieAnimationView
    lateinit var txtCovid : TextView
    lateinit var txtSecond : TextView
    lateinit var txtGetting : TextView
    lateinit var llName : LinearLayout
    lateinit var getName : EditText

    //second
    lateinit var relativeSecond : RelativeLayout
    lateinit var welcome : TextView
    lateinit var hope : TextView
    lateinit var imageWelcome : LottieAnimationView
    lateinit var txtCovid2 : TextView
    lateinit var txtSecond2 : TextView
    lateinit var firstImage : ImageView
    lateinit var secondImage : ImageView
    lateinit var thirdImage : ImageView
    lateinit var firstText : TextView
    lateinit var secondText : TextView
    lateinit var thirdText : TextView

    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences(getString(R.string.file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)

        //first
        relativeOne = findViewById(R.id.relativeOne)
        mask = findViewById(R.id.mask)
        txtCovid = findViewById(R.id.txtCovid)
        txtSecond = findViewById(R.id.txtSecond)
        txtGetting = findViewById(R.id.gettingStarted)
        llName = findViewById(R.id.llName)
        getName = findViewById(R.id.getName)


        //second
        relativeSecond = findViewById(R.id.relativeSecond)
        welcome = findViewById(R.id.welcome)
        hope = findViewById(R.id.hope)
        imageWelcome = findViewById(R.id.imageWelcome)
        txtCovid2 = findViewById(R.id.txtCovid2)
        txtSecond2 = findViewById(R.id.txtSecond2)
        firstImage = findViewById(R.id.firstImage)
        secondImage = findViewById(R.id.secondImage)
        thirdImage = findViewById(R.id.thirdImage)
        firstText = findViewById(R.id.firstText)
        secondText = findViewById(R.id.secondText)
        thirdText = findViewById(R.id.thirdText)
        relativeSecond.visibility = View.GONE


        if(isLoggedIn){
            splashMain()
        }
        else {
            relativeOne.animation = AnimationUtils.loadAnimation(this, R.anim.down_n_settle)
            forward = findViewById(R.id.forward)

            mask.translationY = -100f
            txtCovid.translationY = -50f
            txtSecond.translationY = -50f
            txtGetting.translationY = -80f
            llName.translationY = -80f

            mask.alpha = 0f
            txtCovid.alpha = 0f
            txtSecond.alpha = 0f
            txtGetting.alpha = 0f
            llName.alpha = 0f

            mask.animate().translationY(0f).alpha(1f).setDuration(1300).setStartDelay(1000).start()
            txtCovid.animate().translationY(0f).alpha(1f).setDuration(1500).setStartDelay(1400).start()
            txtSecond.animate().translationY(0f).alpha(1f).setDuration(1600).setStartDelay(1500).start()
            txtGetting.animate().translationY(0f).alpha(1f).setDuration(2200).setStartDelay(2000).start()
            llName.animate().translationY(0f).alpha(1f).setDuration(2200).setStartDelay(2000).start()


            forward.setOnClickListener {

                if(getName.text.toString().isEmpty()){
                    Toast.makeText(this@SplashScreen,"Please Enter Name",Toast.LENGTH_SHORT).show()
                }

                else{
                    hideKeyboard()
                    val userName = getName.text.toString()
                    relativeOne.animation = AnimationUtils.loadAnimation(this,R.anim.up_n_disappear)
                    relativeOne.visibility = View.GONE
                    relativeSecond.visibility = View.VISIBLE
                    relativeSecond.animation = AnimationUtils.loadAnimation(this,R.anim.down_n_settle)

                    mask.translationY = -100f
                    txtCovid.translationY = -50f
                    txtSecond.translationY = -50f
                    txtGetting.translationY = -80f
                    llName.translationY = -80f

                    mask.alpha = 1f
                    txtCovid.alpha = 1f
                    txtSecond.alpha = 1f
                    txtGetting.alpha = 1f
                    llName.alpha = 1f

                    mask.animate().translationY(-100f).alpha(0f).setDuration(1300).setStartDelay(1000).start()
                    txtCovid.animate().translationY(-50f).alpha(0f).setDuration(1500).setStartDelay(1400).start()
                    txtSecond.animate().translationY(-50f).alpha(0f).setDuration(1600).setStartDelay(1500).start()
                    txtGetting.animate().translationY(-80f).alpha(0f).setDuration(2200).setStartDelay(2000).start()
                    llName.animate().translationY(-80f).alpha(0f).setDuration(2200).setStartDelay(2000).start()

                    savePreferences(userName)

                    splashMain()
                }
            }

        }

    }
    override fun onPause() {
        super.onPause()
        finish()
    }

    fun savePreferences(
        name: String
    ) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("Name", name).apply()
    }

    fun splashMain(){

        val text = "Welcome ${sharedPreferences.getString("Name","User")} ,"
        welcome.text = text
        relativeOne.visibility = View.GONE
        relativeSecond.visibility = View.VISIBLE
        relativeSecond.animation = AnimationUtils.loadAnimation(this,R.anim.down_n_settle)

        welcome.translationY = -100f
        hope.translationY = -80f
        txtCovid2.translationY = -80f
        txtSecond2.translationY = -50f

        welcome.alpha = 0f
        hope.alpha = 0f
        imageWelcome.alpha = 0f
        txtCovid2.alpha = 0f
        txtSecond2.alpha = 0f
        firstImage.alpha = 0f
        secondImage.alpha = 0f
        thirdImage.alpha = 0f
        firstText.alpha = 0f
        secondText.alpha = 0f
        thirdText.alpha = 0f

        welcome.animate().translationY(0f).alpha(1f).setDuration(1200).setStartDelay(1000).start()
        hope.animate().translationY(0f).alpha(1f).setDuration(1300).setStartDelay(1100).start()
        imageWelcome.animate().translationX(0f).alpha(1f).setDuration(1500).setStartDelay(1300).start()
        txtCovid2.animate().translationY(0f).alpha(1f).setDuration(2000).setStartDelay(1800).start()
        txtSecond2.animate().translationY(0f).alpha(1f).setDuration(2200).setStartDelay(2000).start()
        firstImage.animate().translationY(0f).alpha(1f).setDuration(2600).setStartDelay(2500).start()
        firstText.animate().translationY(0f).alpha(1f).setDuration(2700).setStartDelay(2600).start()
        secondImage.animate().translationY(0f).alpha(1f).setDuration(2900).setStartDelay(2800).start()
        secondText.animate().translationY(0f).alpha(1f).setDuration(3000).setStartDelay(2900).start()
        thirdImage.animate().translationY(0f).alpha(1f).setDuration(3200).setStartDelay(3100).start()
        thirdText.animate().translationY(0f).alpha(1f).setDuration(3300).setStartDelay(3200).start()

        Handler().postDelayed({
            val startAct = Intent(
                this@SplashScreen,
                MainActivity::class.java
            )
            startActivity(startAct)
            overridePendingTransition(R.anim.zoom,R.anim.static_animation)
        }, 10000)
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}