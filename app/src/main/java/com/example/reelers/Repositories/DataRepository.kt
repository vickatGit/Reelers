package com.example.reelers.Repositories

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.reelers.Models.ReelModel
import com.example.reelers.Models.UserModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class DataRepository {

    val db = FirebaseFirestore.getInstance()
    val remoteStorage=FirebaseStorage.getInstance()
    var user: UserModel? = null
    var some: DocumentSnapshot? = null
    private var isEmailExist:MutableLiveData<Boolean> = MutableLiveData()
    private var isUserExist:MutableLiveData<UserModel> = MutableLiveData()
    private var getReels:MutableLiveData<List<ReelModel>> = MutableLiveData()
    private var getUserReels:MutableLiveData<List<ReelModel>> = MutableLiveData()

    companion object{
        private val TAG = "tag"

        private var dataRepository = DataRepository()
        fun getInstance(): DataRepository {
            return dataRepository
        }

    }
    fun isUserExist(email: String, password: String): MutableLiveData<UserModel> {
        CoroutineScope(Dispatchers.IO).async {
            db.collection("Users").whereEqualTo("email", email).limit(1).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result.documents.forEach {
                            some = it
                            if (some != null) {
                                user = UserModel(
                                    some?.get("email").toString(),
                                    some?.get("password").toString(),
                                    user?.birth,
                                    some?.get("username").toString(),
                                    some?.getString("userRef")
                                )
                                Log.d(TAG, "isUserExist:user is  $user")
                                Log.d(TAG, "isUserExist: dynamic" +BCrypt.verifyer().verify(password.toCharArray(),user?.password).verified)
                                Log.d(TAG, "isUserExist: static" +BCrypt.verifyer().verify("12345678".toCharArray(),user?.password).verified)
                                val isUser = BCrypt.verifyer()
                                    .verify(password.toCharArray(), user!!.password)
                                Log.d(TAG, "isUserExist: ${isUser.verified}")
                                if (isUser.verified)
                                    isUserExist.postValue(user)
                                else
                                    isUserExist.postValue(null)
                            }
                        }
                    }
                }
        }

        return isUserExist
    }
    fun createUser(user: UserModel): MutableLiveData<UserModel> {

        Log.d(TAG, "createUser: repo")
        val password=user.password
        CoroutineScope(Dispatchers.IO).launch {
            val hashedPassword:String=BCrypt.withDefaults().hashToString(6,user.password.toCharArray())
            user.password=hashedPassword
            val docRef = db.collection("Users").document().id
            user.setuserReference(docRef.toString())
            db.collection("Users").document(docRef.toString()).set(user).addOnCompleteListener {
                if (it.isSuccessful) {
                    isUserExist(user.email, user.password)
                    Log.d(TAG, "createUser: user account is successfully created")
                }
            }.addOnFailureListener {
                Log.d(TAG, "createUser: cant create user")
            }
        }
        return isUserExist(user.email, password)
    }
    fun isEmailAlreadyExist(email: String): MutableLiveData<Boolean> {


        db.collection("Users").whereEqualTo("email", email).get(Source.SERVER)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.size() == 0) {
                        Log.d(TAG, "isEmailAlreadyExist: email false")
                        isEmailExist.postValue(false)
                    }else{
                        Log.d(TAG, "isEmailAlreadyExist: email true")
                        isEmailExist.postValue(true)
                    }
                }else{
                    Log.d(TAG, "isEmailAlreadyExist: email false")
                    isEmailExist.postValue(false)
                }
            }
        return isEmailExist

    }

    fun uploadReel(videoPath: String?, userRef: String?, tags: java.util.ArrayList<String>?) {

        try {
            val file=File(videoPath)
            Log.d(TAG, "uploadReel: uri is ${file.toUri()}")
            val docRef=db.collection("Reels").document().id
            remoteStorage.reference.child("Reels/${file.name}").putFile(file.toUri()).addOnSuccessListener{
                remoteStorage.reference.child("Reels/${file.name}").downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "uploadReel: $it")
                    val reelObj=ReelModel(userRef!!, it.toString(),tags!!)
                    db.collection("Reels").document(docRef).set(reelObj).addOnSuccessListener {
                        Log.d(TAG, "uploadReel: success in firstore")
                    }.addOnFailureListener {
                        Log.d(TAG, "uploadReel: failure in firestore")
                    }
                }

            }.addOnFailureListener{
                Log.d(TAG, "uploadReel: failure finding "+it.localizedMessage)
            }
        }catch (e:Exception){
            Log.d(TAG, "uploadReel:${e.localizedMessage}")
        }

    }

    fun getSampleReels(query: String?): MutableLiveData<List<ReelModel>> {
        val reels=ArrayList<ReelModel>(1)
        var finalQuery="trending"
        Log.d(TAG, "getSampleReels: query is $query")
        if(query!=null){
            finalQuery=query
        }

        db.collection("Reels").get().addOnSuccessListener {
            Log.d(TAG, "getSampleReels: success"+it.documents.size)
            it.documents.forEach {
                val reel=ReelModel(it.get("belongsTo").toString(),it.get("reelDownloadUrl").toString(), it.get("tags") as ArrayList<String>)
                if(reel.tags.contains(finalQuery)) {
                    reels.add(reel)
                    Log.d(TAG, "getSampleReels: reel is $reel")
                }
            }
            getReels.postValue(reels)
        }.addOnFailureListener {
            Log.d(TAG, "getSampleReels: failure"+it.localizedMessage)
        }
        return getReels
    }

    fun getUserVideos(userRef: String): MutableLiveData<List<ReelModel>> {
        val userReels=ArrayList<ReelModel>(1)
        db.collection("Reels").whereEqualTo("belongsTo",userRef).get().addOnSuccessListener {
            Log.d(TAG, "getSampleReels: success"+it.documents.size)
            it.documents.forEach {
                val reel=ReelModel(it.get("belongsTo").toString(),it.get("reelDownloadUrl").toString(), it.get("tags") as ArrayList<String>)
//                if(reel.tags.contains("trending")) {
                    userReels.add(reel)
                    Log.d(TAG, "getSampleReels: reel is $reel")
//                }
            }
            getUserReels.postValue(userReels)
        }.addOnFailureListener {
            Log.d(TAG, "getSampleReels: failure"+it.localizedMessage)
        }
        return getUserReels
    }
}