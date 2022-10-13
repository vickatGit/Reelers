package com.example.reelers.Fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.reelers.Adapters.HomeReelThumbAdapter
import com.example.reelers.Adapters.ReelAdapter
import com.example.reelers.Models.ReelListenerItem
import com.example.reelers.Models.ReelModel
import com.example.reelers.R
import com.example.reelers.SignUpLoginActivity
import com.example.reelers.StaggeredLayout.StaggeredLayout
import com.example.reelers.ViewModels.MyVideosFragmentVidemodel


class MyVideosFragment : Fragment() {

    private lateinit var myVideosRecycler:RecyclerView
    private lateinit var viewModel:MyVideosFragmentVidemodel
    private lateinit var USER_REF:String
    private var userReels=ArrayList<ReelModel>(1)
    private lateinit var myVideosAdapter:HomeReelThumbAdapter
    private var allReelPlayers=ArrayList<ReelListenerItem>(1)
    private var currentPlayerPosition:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myVideosAdapter= HomeReelThumbAdapter(this.requireContext(),userReels,object :HomeReelThumbAdapter.HomeReelListener{
            override fun OnReelListener(reelListenerItem: ReelListenerItem) {
                allReelPlayers.add(reelListenerItem)
                currentPlayerPosition=reelListenerItem.reelPosition
            }

            override fun OnFocusChanged(position: Int) {
                playThis(position)
                currentPlayerPosition=position
            }


        })
        USER_REF=arguments?.getString(SignUpLoginActivity.USER_REF)!!
        viewModel=ViewModelProvider(this).get(MyVideosFragmentVidemodel::class.java)
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_my_videos, container, false)
        initialise(view)
//        val layout=StaggeredLayout(this.requireContext(),100,99)
        val layout=GridLayoutManager(this.requireContext(),2)
        myVideosRecycler.layoutManager=layout
        myVideosRecycler.adapter=myVideosAdapter
        viewModel.getUserVideos(USER_REF).observe(this.viewLifecycleOwner, Observer {
            Log.d("TAG", "onCreateView: uservideos ${it.size}")
            if(it!=null){
                userReels.clear()
                userReels.addAll(it)
                myVideosAdapter.notifyDataSetChanged()
            }
        })
        return view
    }

    private fun initialise(view: View?) {
        myVideosRecycler=view?.findViewById(R.id.user_videos)!!

    }
    override fun onResume() {
        super.onResume()
        val index = allReelPlayers.indexOfFirst { it.reelPosition == currentPlayerPosition }
        if (index != -1) {
            val player = allReelPlayers[index].reelPlayer
            player.playWhenReady = true
            player.play()
        }
    }
    override fun onPause() {
        super.onPause()
        val index = allReelPlayers.indexOfFirst { it.reelPosition == currentPlayerPosition }
        Log.d("TAG", "onPause: frag $index")
        if (index != -1) {
            val player = allReelPlayers[index].reelPlayer
            player.pause()
            player.playWhenReady = false
        }

    }


}