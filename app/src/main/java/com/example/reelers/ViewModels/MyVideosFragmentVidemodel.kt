package com.example.reelers.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reelers.Models.ReelModel
import com.example.reelers.Repositories.DataRepository

class MyVideosFragmentVidemodel: ViewModel() {
    private lateinit var repo: DataRepository
    init {
        repo= DataRepository.getInstance()
    }
    fun getUserVideos(userRef:String): MutableLiveData<List<ReelModel>> {
        return repo?.getUserVideos(userRef)
    }
}