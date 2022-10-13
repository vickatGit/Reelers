package com.example.reelers.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
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

class HomeReelThumbAdapter(val context: Context, val reelsList: ArrayList<ReelModel>, val reelListener:HomeReelListener): RecyclerView.Adapter<HomeReelThumbAdapter.ReelViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.reel_thumb_layout,parent,false)
        return ReelViewHolder(view,reelListener)

    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        holder.setVideoPath(reelsList.get(position).reelDownloadUrl,position)
        holder.itemView.setOnLongClickListener {
            reelListener.OnFocusChanged(position)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return reelsList.size
    }
    inner class ReelViewHolder(itemView: View, reelListener: HomeReelListener): RecyclerView.ViewHolder(itemView){
        lateinit var reelPlayer: ExoPlayer
        lateinit var reelPlayerview: StyledPlayerView
        lateinit var mediaSource: MediaSource
        lateinit var bufferer: ProgressBar
        val view=itemView

        fun setVideoPath(reelUrl: String, position: Int){

            bufferer=itemView.findViewById(R.id.video_bufferer)
            reelPlayerview=itemView.findViewById(R.id.reel_player)
            reelPlayer= ExoPlayer.Builder(context).build()
            reelPlayer?.addListener(object: Player.Listener{
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(context,"Can't Play this video", Toast.LENGTH_SHORT).show()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if(playbackState== Player.STATE_BUFFERING){
                        bufferer.visibility= View.VISIBLE
                    }else{
                        bufferer.visibility= View.INVISIBLE
                    }
                }
            })

            reelPlayerview.player=reelPlayer
            reelPlayer.seekTo(0)
            reelPlayer?.repeatMode= Player.REPEAT_MODE_ONE
            val dataSourceFactory= DefaultDataSource.Factory(context)
            mediaSource= ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                MediaItem.fromUri(Uri.parse(reelUrl)))
            reelPlayer?.setMediaSource(mediaSource)
            reelPlayer?.prepare()
//            if (absoluteAdapterPosition == 0) {
//                reelPlayer.playWhenReady = true
//                reelPlayer.play()
//            }
            reelListener.OnReelListener(ReelListenerItem(reelPlayer!!,absoluteAdapterPosition))
        }
    }
    interface HomeReelListener{
        fun OnReelListener(reelListenerItem: ReelListenerItem)
        fun OnFocusChanged(position: Int)
    }
}