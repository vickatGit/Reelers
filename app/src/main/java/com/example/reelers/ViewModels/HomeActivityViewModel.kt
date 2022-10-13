package com.example.reelers.ViewModels

import androidx.lifecycle.ViewModel
import com.example.reelers.Repositories.DataRepository
import java.util.ArrayList

class HomeActivityViewModel : ViewModel() {

    private lateinit var repo: DataRepository
    init {
        repo= DataRepository.getInstance()
    }
    fun uploadReel(videoPath: String?, userRef: String?, tags: ArrayList<String>?) {
        return repo.uploadReel(videoPath,userRef,tags)
    }
}