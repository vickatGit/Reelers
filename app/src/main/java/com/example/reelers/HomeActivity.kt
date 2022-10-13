package com.example.reelers

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.reelers.Fragments.HomeBottomsheetFragment
import com.example.reelers.Fragments.MyVideosFragment
import com.example.reelers.ViewModels.HomeActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class HomeActivity : AppCompatActivity() , HomeBottomsheetFragment.videoPathSender {

    private lateinit var addVideo:FloatingActionButton
    private lateinit var bottomNavigationView:BottomNavigationView
    private lateinit var fragContainer:FrameLayout
    private lateinit var viewModel:HomeActivityViewModel
    private var userRef:String?=null
    private lateinit var tagsTaker:ActivityResultLauncher<Intent>
    private var currentRealPath:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        userRef=intent.getStringExtra(SignUpLoginActivity.USER_REF)
        initialise()
        tagsTaker=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val tags=it.data?.getStringArrayListExtra("Tags")
                Log.d("TAG", "onCreate: upload tags are taken$tags")
                viewModel.uploadReel(currentRealPath,userRef,tags)
            }
        }
        viewModel=ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        addVideo.setOnClickListener {
            val addVideoSheet=HomeBottomsheetFragment()
            addVideoSheet.show(supportFragmentManager,"add_video")
        }
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.reels -> {
                    startActivity(Intent(this,ReelsActivity::class.java))
                }
                R.id.home -> {
                    val bundle=Bundle()
                    bundle.putString(SignUpLoginActivity.USER_REF,userRef)
                    val myVidFrag=MyVideosFragment()
                    myVidFrag.arguments=bundle
                    supportFragmentManager.beginTransaction().replace(R.id.frag_container,myVidFrag).addToBackStack(null).commit()
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun onStart() {
        super.onStart()
        bottomNavigationView.selectedItemId=R.id.home
    }

    private fun initialise() {
        addVideo=findViewById(R.id.add_video)
        bottomNavigationView=findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.background=null
        fragContainer=findViewById(R.id.frag_container)
    }

    override fun getVideoPath(videoPath: Intent?) {
        currentRealPath=getRealPathFromURI(videoPath?.data)
        val time=isVideoInLimit(currentRealPath)
        if (time != null) {
            if((time/1000)<=60){
                val intent=Intent(this,VideoTagsTakerActivity::class.java)
                tagsTaker.launch(intent)
            }else{
                Toast.makeText(this,"Duration is greater than 60 seconds",Toast.LENGTH_SHORT).show()
            }

        }


    }
    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }
    fun isVideoInLimit(currentRealPath: String?): Long? {
        val retriever:MediaMetadataRetriever =MediaMetadataRetriever()
            //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this, Uri.fromFile(File(currentRealPath)));
        val time  = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillisec: Long? = time?.toLong()
        retriever.release()
        return timeInMillisec
    }
}