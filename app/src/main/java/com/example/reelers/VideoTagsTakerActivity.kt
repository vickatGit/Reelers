package com.example.reelers

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class VideoTagsTakerActivity : AppCompatActivity() {
    private lateinit var uploadVideo:Button
    private lateinit var tagsSource:EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_tags_taker)
        initialise()

        uploadVideo.setOnClickListener {
            if(!tagsSource.text.isEmpty()){
                val tags=tagsSource.text.toString()
                val allTags=tags.split(" ")
                val intent= Intent()
                intent.putStringArrayListExtra("Tags", ArrayList(allTags))
                setResult(Activity.RESULT_OK,intent)
                finish()
            }else{
                Toast.makeText(this,"Tags Field is Empty",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun initialise(){
        uploadVideo=findViewById(R.id.upload_video)
        tagsSource=findViewById(R.id.tags)
    }
}