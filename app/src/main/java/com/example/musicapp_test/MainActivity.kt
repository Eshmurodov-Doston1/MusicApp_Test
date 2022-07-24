package com.example.musicapp_test

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.viewbinding.library.activity.viewBinding
import android.widget.SeekBar
import androidx.annotation.MainThread
import com.example.musicapp_test.databinding.ActivityMainBinding
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by viewBinding()
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying=false
    private lateinit var handler:Handler
    private lateinit var runnable:Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        handler = Handler(Looper.getMainLooper())
        binding.apply {
            mediaPlayer = MediaPlayer.create(this@MainActivity,R.raw.judolik)
            stop.setOnClickListener {
                mediaPlayer.pause()
                isPlaying = false
            }
            play.setOnClickListener {
                mediaPlayer.start()
                isPlaying = true
            }


            waveformSeekBar.setSampleFrom(R.raw.judolik)

            mediaPlayer.setOnPreparedListener {
                playerSeek.max = it.duration
                waveformSeekBar.maxProgress = it.duration.toFloat()
                updateSeek()
            }

            playerSeek.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2){
                        mediaPlayer.seekTo(p1)
                        playerSeek.progress = p1
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })




            waveformSeekBar.onProgressChanged  = object : SeekBarOnProgressChanged {
                override fun onProgressChanged(
                    waveformSeekBar: WaveformSeekBar,
                    progress: Float,
                    fromUser: Boolean
                ) {
                    if (fromUser){
                        mediaPlayer.seekTo(progress.toInt())
                        waveformSeekBar.progress = progress
                    }
                }
            }



            mediaPlayer.setOnBufferingUpdateListener { p0, p1 ->
                val d = p1 / 100.0
                val d1 = p0.duration * d
                playerSeek.secondaryProgress = d1.toInt()
                waveformSeekBar.progress = d1.toFloat()
            }







        }
    }

    private fun updateSeek() {
        val currentPosition = mediaPlayer.currentPosition
        binding.playerSeek.progress = currentPosition
        binding.waveformSeekBar.progress = currentPosition.toFloat()
        runnable = Runnable {
          updateSeek()
        }
        handler.postDelayed(runnable,1000)
    }


}