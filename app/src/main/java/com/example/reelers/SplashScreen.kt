package com.example.reelers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.spotify_clone.LocalDatabase.SqlUserEntity
import com.example.spotify_clone.LocalDatabase.UserLoginSignUpDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private lateinit var splashScreenIcon:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        splashScreenIcon=findViewById(R.id.spalsh_icon)
        val splashAnim=AnimationUtils.loadAnimation(this,R.anim.fade_out)
        splashScreenIcon.startAnimation(splashAnim)

        Log.d("tag", "onCreate: checking")
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            val user: SqlUserEntity? = UserLoginSignUpDatabase.getInstance(this@SplashScreen)?.getUserLoginDao()?.getUser()
            if(user?.userReference!=null){
                Log.d("TAG", "onCreate: the user is $user")
                val intent= Intent(this@SplashScreen,HomeActivity::class.java)
                intent.putExtra(SignUpLoginActivity.USER_REF,user?.userReference)
                startActivity(intent)
                finish()
            }else{
                val intent=Intent(this@SplashScreen,SignUpLoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}