package com.example.reelers.Adapters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reelers.Models.ReelListenerItem
import com.example.reelers.Models.ReelModel
import com.example.reelers.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource

class ReelAdapter(val context: Context, val reelsList: ArrayList<ReelModel>, val reelListener:ReelListener): RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.reel_layout,parent,false)
        return ReelViewHolder(view,reelListener)

    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        holder.setVideoPath(reelsList.get(position).reelDownloadUrl,position)
//        holder.setVideoPath("https://firebasestorage.googleapis.com/v0/b/reelers-f9386.appspot.com/o/Reels%2Fy2mate.com%20-%20revenge%20timell%20mother%20of%20dragons%20ll%20fed%20up%20ll%20game%20of%20thones%20ll%20deepesh%20sai%20ll%20shorts_v240P.mp4?alt=media&token=4c80e14b-4035-43fe-aae2-0257dba96e67",position)
    }

    override fun getItemCount(): Int {
        return reelsList.size
    }
    inner class ReelViewHolder(itemView: View, reelListener: ReelListener): RecyclerView.ViewHolder(itemView){
        private lateinit var reelPlayer:ExoPlayer
        private lateinit var reelPlayerview:StyledPlayerView
        private lateinit var mediaSource:MediaSource
        private lateinit var bufferer:ProgressBar

        fun setVideoPath(reelUrl: String, position: Int){

            bufferer=itemView.findViewById(R.id.video_bufferer)
            reelPlayerview=itemView.findViewById(R.id.reel_player)
            reelPlayer=ExoPlayer.Builder(context).build()
            reelPlayer?.addListener(object:Player.Listener{
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(context,"$error", Toast.LENGTH_SHORT).show()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if(playbackState==Player.STATE_BUFFERING){
                        bufferer.visibility=View.VISIBLE
                    }else{
                        bufferer.visibility=View.INVISIBLE
                    }
                }
            })

            reelPlayerview.player=reelPlayer
            reelPlayer.seekTo(0)
            reelPlayer?.repeatMode=Player.REPEAT_MODE_ONE
            val dataSourceFactory= DefaultDataSource.Factory(context)
            mediaSource=ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(reelUrl)))
            reelPlayer?.setMediaSource(mediaSource)
            reelPlayer?.prepare()
            if (absoluteAdapterPosition == 0) {
                reelPlayer.playWhenReady = true
                reelPlayer.play()
            }
            reelListener.OnReelListener(ReelListenerItem(reelPlayer!!,absoluteAdapterPosition))
        }
    }
    interface ReelListener{
        fun OnReelListener(reelListenerItem:ReelListenerItem)
    }
}