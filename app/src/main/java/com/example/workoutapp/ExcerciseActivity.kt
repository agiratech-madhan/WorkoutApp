package com.example.workoutapp

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workoutapp.databinding.ActivityExersizeBinding
import java.util.*

class ExcerSizeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var restTimer: CountDownTimer? =
        null // Variable for Rest Timer and later on we will initialize it.
    private var restProgress = 0
    private var binding: ActivityExersizeBinding? = null
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseTimerDuration:Long = 30

    private var exerciseProgress = 0
    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExersizePosition = -1
    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var exerciseAdapter: ExerciseStatusAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExersizeBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        tts = TextToSpeech(this, this)

        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        exerciseList = Constants.defaultExerciseList()


        setupRestView()
        setupExerciseStatusRecycleriew()
    }

    private fun setupExerciseStatusRecycleriew() {
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setupRestView() {
        try {
            var soundUri =
                Uri.parse("android.resource://com.example.workoutapp/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundUri)
            player?.isLooping = false
            player?.start()
        } catch (e: Exception) {
            e.printStackTrace()

        }
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.upcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        /**
         * Here firstly we will check if the timer is running the and it is not null then cancel the running timer and start the new one.
         * And set the progress to initial which is 0.
         */
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        binding?.tvUpcomingExerciseName?.text =
            exerciseList!![currentExersizePosition + 1].getName()

        // This function is used to set the progress details.
        setRestProgressBar()
    }

    private fun setRestProgressBar() {

        binding?.progressBar?.progress =
            restProgress // Sets the current progress to the specified value.
        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++ // It is increased by 1
                binding?.progressBar?.progress =
                   10 - restProgress // Indicates progress bar progress
                binding?.tvTimer?.text =
                    (10 - restProgress).toString()  // Current progress is set to text view in terms of seconds.
            }

            override fun onFinish() {
                // When the 10 seconds will complete this will be executed.
//                Toast.makeText(
//                    this@ExcerSizeActivity, "The Message timer.", Toast.LENGTH_SHORT
//                ).show()
                currentExersizePosition++
                exerciseList!![currentExersizePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()
    }

    private fun setupExerciseView() {

        // Here according to the view make it visible as this is Exercise View so exercise view is visible and rest view is not.
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE


        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExersizePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExersizePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExersizePosition].getName()
       if(currentExersizePosition<exerciseList!!.size-1){
           binding?.tvUpcomingExerciseName?.text=exerciseList!![currentExersizePosition+1].getName()
       }
        setExerciseProgressBar()
    }

    private fun setExerciseProgressBar() {

        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = exerciseTimerDuration.toInt() - exerciseProgress
                binding?.tvTimerExercise?.text = (exerciseTimerDuration.toInt() - exerciseProgress).toString()
            }

            override fun onFinish() {

               // updating in the list that this exercise is completed
                if (currentExersizePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExersizePosition].setIsSelected(false) // exercise is completed so selection is set to false
                    exerciseList!![currentExersizePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()

                    setupRestView()
                } else {
                    val intent=Intent(this@ExcerSizeActivity, FinishActivity::class.java)
                    startActivity(intent)
//                    Toast.makeText(
//                        this@ExcerSizeActivity,
//                        "Congratulations! You have Completed 7 minutes Workout",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
//
            }
        }.start()
    }

    public override fun onDestroy() {
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
        if (player != null) {
            player!!.stop()
        }
        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.CANADA)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }

        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}