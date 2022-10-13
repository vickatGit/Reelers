package com.example.reelers.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.reelers.HomeActivity
import com.example.reelers.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import androidx.core.app.ActivityCompat.startActivityForResult





class HomeBottomsheetFragment : BottomSheetDialogFragment() {

    private lateinit var createVideo: LinearLayoutCompat
    private lateinit var uploadVideo: LinearLayoutCompat
    private val CAPTURE_REEL = 232
    private lateinit var videoTaker: ActivityResultLauncher<Intent>
    private var parent:HomeActivity?=null
    companion object{
        val TAG="HomeBottomsheetFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is videoPathSender){
            parent= context as HomeActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoTaker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val videoPath = it.data
                parent?.getVideoPath(videoPath)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_bottomsheet, container, false)
        initialise(view)
        createVideo.setOnClickListener {
            val intent = Intent()
            intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
            videoTaker.launch(intent)


        }
        uploadVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            videoTaker.launch(intent)
        }
        return view
    }

    private fun initialise(view: View?) {
        uploadVideo = view?.findViewById(R.id.upload)!!
        createVideo = view?.findViewById(R.id.Create)!!

    }


    interface videoPathSender{
        fun getVideoPath(videoPath: Intent?)
    }

}


