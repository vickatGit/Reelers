package com.example.spotify_clone.ViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.reelers.Models.UserModel
import com.example.reelers.Repositories.DataRepository


class LoginViewModel : ViewModel() {

  private lateinit var repo: DataRepository
  init {
    repo= DataRepository.getInstance()
  }
  fun isUserExist(email: String, password: String): MutableLiveData<UserModel> {
    return repo.isUserExist(email, password)
  }


}