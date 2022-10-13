package com.example.reelers.Models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
  val email:String, var password: String,
  val birth: Timestamp?,
  val username:String,
  var userRef:String?) : Parcelable{
  fun setuserReference(userRef: String) {
    this.userRef=userRef
  }
}