package com.example.reelers

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.spotify_clone.LocalDatabase.SqlUserEntity
import com.example.spotify_clone.LocalDatabase.UserLoginSignUpDatabase
import com.example.spotify_clone.ViewModels.SignupViewModel

class SignUpActivity : AppCompatActivity() {
  private lateinit var toolbar:androidx.appcompat.widget.Toolbar
  private lateinit var input:EditText
  private lateinit var passInput:EditText
  private lateinit var acknowledgement:TextView
  private lateinit var passAcknowledgement:TextView
  private lateinit var next:Button
  private lateinit var passNext:Button
  private lateinit var signup:LinearLayout
  private lateinit var passSignup:LinearLayout
  private lateinit var birthNext:Button
  private lateinit var birth:LinearLayout
  private lateinit var birthPicker:DatePicker
  private lateinit var viewModel:SignupViewModel
  private lateinit var username:LinearLayout
  private lateinit var usernameInput:EditText
  private lateinit var createAccount:Button
  private lateinit var emailQuestion:TextView
  private lateinit var passwordQuestion:TextView
  private lateinit var birthQuestion:TextView
  private lateinit var usernameQuestion:TextView
  private var curpos:Int=0

  enum class currentPosition{
    emial,password,birth,username
  }
  val layouts:ArrayList<Int> = ArrayList(5)


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    initialiseViews()
    layouts.addAll(listOf(R.id.signup,R.id.password,R.id.birth,R.id.username))
    viewModel=ViewModelProvider(this).get(SignupViewModel::class.java)
    setSupportActionBar(toolbar)
    setCurrentPosition(0)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setTitle("")

    next.background=ContextCompat.getDrawable(this,R.drawable.next_button_error_bg)
    passNext.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.next_button_error_bg))
    input.addTextChangedListener{
      if(!input.text.isEmpty()){
        if(Patterns.EMAIL_ADDRESS.matcher(input.text).matches()){
          next.isEnabled=true
          next.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.next_button_bg))
          input.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.input_details_background))

          input.setTextColor(Color.WHITE)
        }else{
          next.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.next_button_error_bg))
          next.isEnabled=false
          input.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.input_details_error_background))
          input.setTextColor(Color.RED)
        }
      }
      else{
        next.isEnabled=false
        next.background=ContextCompat.getDrawable(this,R.drawable.next_button_bg)
        input.setTextColor(Color.GRAY)
        input.setBackgroundColor(resources.getColor(R.color.secondaryLightColor))
      }
    }
    passInput.addTextChangedListener{
      if(passInput.text.length<8){
        passAcknowledgement.setText("password should be greater than 8 characters")
        passNext.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.next_button_error_bg))
        passNext.isEnabled=false
      }else if(passInput.text.length>=8){
        passNext.isEnabled=true
        passNext.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.next_button_bg))
        passAcknowledgement.setText("")
      }else{
        passNext.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.next_button_error_bg))
      }
    }

    next.setOnClickListener {
        Log.d("next", "onCreate: checking email")
      viewModel.isEmailAlreadyExist(input.text.toString()).observe(this, Observer {
        if(it==false) {
          viewModel.setEmail(input.text.toString())
          setCurrentPosition(1)
          Log.d("next", "onCreate: email doesn't exist")
          proceedToPassword()
        }else{
          Log.d("next", "onCreate: email already exist")
          acknowledgement.text="This email already exists, Try using login"
        }
      })
    }
    passNext.setOnClickListener {
      setCurrentPosition(2)
      proceedToBirth()
    }
    birthNext.setOnClickListener {
      setCurrentPosition(3)
      viewModel.setBirthDate(birthPicker.dayOfMonth,birthPicker.month,birthPicker.year)
//      proccedToGenderSelection()
      proceedToAccountCreation()

    }


    createAccount.setOnClickListener {
      viewModel.setUsername(usernameInput.text.toString())

        viewModel.createUser().observe(this@SignUpActivity, Observer {
          Log.d("TAG", "onCreate: $it")
          Log.d("TAG", "onCreate: observer"+it)
          if(it!=null){
            val bundle=Bundle()
            val intent=Intent(this@SignUpActivity,HomeActivity::class.java)
            bundle.putParcelable(LoginActivity.USER,it)
            intent.putExtra(LoginActivity.USER,bundle)
            startActivity(intent)
            UserLoginSignUpDatabase.getInstance(this@SignUpActivity)?.getUserLoginDao()?.insertUser(SqlUserEntity(null,it.username,it.userRef!!))
          }
        })

    }
  }


  private fun proceedToAccountCreation() {
    animationHandler(birth,username)
  }

  private fun proceedToBirth() {
    viewModel.setPassword(passInput.text.toString())
    animationHandler(passSignup,birth)
  }

  private fun proceedToPassword() {
    animationHandler(signup,passSignup)
  }
  private fun animationHandler(remLayout:LinearLayout,newLayout: LinearLayout){

    newLayout.visibility=View.VISIBLE
    val slideOut=AnimationUtils.loadAnimation(this,R.anim.old_slide_in_animation)
    val slideIn=AnimationUtils.loadAnimation(this,R.anim.slide_in_animation)
    remLayout.startAnimation(slideOut)
    slideOut.setAnimationListener(object:Animation.AnimationListener{
      override fun onAnimationStart(animation: Animation?) { newLayout.startAnimation(slideIn) }

      override fun onAnimationEnd(animation: Animation?) { remLayout.visibility= View.GONE }

      override fun onAnimationRepeat(animation: Animation?) { }
    })
  }

  private fun setCurrentPosition(pos:Int) {
    this.curpos=pos
  }
  private fun reverseAnimation(remLayout:LinearLayout,newLayout: LinearLayout){

    val oldSlideOut=AnimationUtils.loadAnimation(this,R.anim.old_slide_out_animation)
    val slideOut=AnimationUtils.loadAnimation(this,R.anim.slide_out_animation)
    remLayout.startAnimation(slideOut)
    slideOut.setAnimationListener(object:Animation.AnimationListener{
      override fun onAnimationStart(animation: Animation?) {
        newLayout.visibility=View.VISIBLE
        newLayout.startAnimation(oldSlideOut)
      }

      override fun onAnimationEnd(animation: Animation?) { remLayout.visibility= View.GONE }

      override fun onAnimationRepeat(animation: Animation?) { }
    })
  }
  private fun reverseAnimationHandler(): Boolean {
    var def=true
    if(curpos>0){
      reverseAnimation(findViewById(layouts.get(curpos)), findViewById(layouts.get(curpos-1)))
      curpos--
      def=false
    }
    return def
  }


  override fun onBackPressed() {
    if(reverseAnimationHandler()){
      super.onBackPressed()
    }
  }
  private fun initialiseViews() {
    toolbar=findViewById(R.id.signup_toolbar)

    signup=findViewById(R.id.signup)
    input=findViewById(R.id.email)
    acknowledgement=findViewById(R.id.acknowledgement)
    next=findViewById(R.id.next)
    emailQuestion=findViewById(R.id.question)
    next.isEnabled=false
    next.setTypeface(ResourcesCompat.getFont(this,R.font.circular_std_medium))
    emailQuestion.setTypeface(ResourcesCompat.getFont(this,R.font.circular_std_bold))

    passSignup=findViewById(R.id.password)
    passInput=findViewById(R.id.pass_take_details)
    passAcknowledgement=findViewById(R.id.pass_acknowledgement)
    passNext=findViewById(R.id.pass_next)
    passwordQuestion=findViewById(R.id.pass_question)
    passNext.isEnabled=false
    passNext.setTypeface(ResourcesCompat.getFont(this,R.font.circular_std_medium))
    passNext.setTypeface(ResourcesCompat.getFont(this,R.font.circular_std_bold))

    birth=findViewById(R.id.birth)
    birthPicker=findViewById(R.id.birth_picker)
    birthNext=findViewById(R.id.birth_next)
    birthQuestion=findViewById(R.id.birth_question)
    birthNext.setTypeface(ResourcesCompat.getFont(this,R.font.circular_std_medium))
    birthQuestion.setTypeface(ResourcesCompat.getFont(this,R.font.circular_std_bold))


    username=findViewById(R.id.username)
    usernameInput=findViewById(R.id.username_take_details)
    createAccount=findViewById(R.id.create_account)
    usernameQuestion=findViewById(R.id.username_question)
    usernameQuestion.setTypeface(ResourcesCompat.getFont(this,R.font.circular_std_bold))
    createAccount.setTypeface(ResourcesCompat.getFont(this,R.font.circular_std_medium))


  }
}