package com.example.reelers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.reelers.Adapters.ReelAdapter
import com.example.reelers.Models.ReelListenerItem
import com.example.reelers.Models.ReelModel
import com.example.reelers.ViewModels.ReelsActivityViewModel

class ReelsActivity : AppCompatActivity() {
    private lateinit var viewModel: ReelsActivityViewModel
    private lateinit var viewPager:ViewPager2
    private lateinit var searchReelsByTag:SearchView
    private var reelsList=ArrayList<ReelModel>(1)
    private lateinit var reelsAdapter:ReelAdapter
    private var allReelPlayers=ArrayList<ReelListenerItem>(1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reels)
        initialise()
        searchReelsByTag.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.getReels(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        reelsAdapter= ReelAdapter(this,reelsList,object :ReelAdapter.ReelListener{
            override fun OnReelListener(reelListenerItem: ReelListenerItem) {
                allReelPlayers.add(reelListenerItem)
            }

        })
        viewPager.adapter=reelsAdapter

        viewModel=ViewModelProvider(this).get(ReelsActivityViewModel::class.java)
        viewModel.getReels(null).observe(this, Observer {
            Log.d("TAG", "onCreate: size in observer is "+it.size)

            if(it.size>0) {
                reelsList.clear()
                reelsList.addAll(it)
                playThis(allReelPlayers.size)
                allReelPlayers.clear()
                reelsAdapter.notifyDataSetChanged()
            }else{
                Toast.makeText(this,"No videos for this Tag",Toast.LENGTH_SHORT).show()
            }
        })
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val previousIndex = allReelPlayers.indexOfFirst { it.reelPlayer.isPlaying }
                if (previousIndex != -1) {
                    val player = allReelPlayers[previousIndex].reelPlayer
                    player.pause()
                    player.playWhenReady = false
                }
                val newIndex = allReelPlayers.indexOfFirst { it.reelPosition == position }
                if (newIndex != -1) {
                    val player = allReelPlayers[newIndex].reelPlayer
                    player.playWhenReady = true
                    player.play()
                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        val index = allReelPlayers.indexOfFirst { it.reelPosition == viewPager.currentItem }
        if (index != -1) {
            val player = allReelPlayers[index].reelPlayer
            player.playWhenReady = true
            player.play()
        }
    }
    override fun onPause() {
        super.onPause()
        val index = allReelPlayers.indexOfFirst { it.reelPosition == viewPager.currentItem }
        if (index != -1) {
            val player = allReelPlayers[index].reelPlayer
            player.pause()
            player.playWhenReady = false
        }


    }

    private fun playThis(position:Int){
        for (i in  0..allReelPlayers.size-1){
            if(position==i){
                val player = allReelPlayers[i].reelPlayer
                player.play()
                player.playWhenReady = true
            }else{
                val player = allReelPlayers[i].reelPlayer
                player.pause()
                player.playWhenReady = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (allReelPlayers.isNotEmpty()) {
            var index=0
            for (item in allReelPlayers) {
                val player = item.reelPlayer
                player.release()
                player.stop()
                player.clearMediaItems()
            }
        }
    }

    private fun initialise() {
        viewPager=findViewById(R.id.reels_container)
        searchReelsByTag=findViewById(R.id.search_by_tag)
    }
}