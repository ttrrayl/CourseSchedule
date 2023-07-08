package com.dicoding.courseschedule.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.ui.list.ListViewModelFactory
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {
    private lateinit var addCourseViewModel: AddCourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.title = getString(R.string.add_course)

        setupTimePicker()
        setupViewModel()
    }

    private fun setupViewModel() {
        val factory = ListViewModelFactory.createFactory(this)
        addCourseViewModel = ViewModelProvider(this, factory)[AddCourseViewModel::class.java]

        addCourseViewModel.saved.observe(this) {
            if (it.getContentIfNotHandled() == true){
                onBackPressed()
            } else {
                Toast.makeText(this, getString(R.string.input_empty_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_insert -> {
                val edCourse = findViewById<TextInputEditText?>(R.id.add_ed_name).text?.trim().toString()
                val spinDay = findViewById<Spinner>(R.id.add_spinner_day).selectedItemPosition
                val startTime = findViewById<TextView>(R.id.add_text_start_time).text.toString()
                val endTime = findViewById<TextView>(R.id.add_text_end_time).text.toString()
                val edLecturer = findViewById<TextInputEditText?>(R.id.add_name_lecturer).text?.trim().toString()
                val edNote = findViewById<TextInputEditText?>(R.id.add_note).text?.trim().toString()

                addCourseViewModel.insertCourse(edCourse, spinDay, startTime, endTime, edLecturer, edNote)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupTimePicker(){
        val startTime = findViewById<ImageButton>(R.id.add_start_time)
        startTime.setOnClickListener{
            val dialogTimePicker = TimePickerFragment()
            dialogTimePicker.show(supportFragmentManager, " startTime")
        }

        val endTime = findViewById<ImageButton>(R.id.add_end_time)
        endTime.setOnClickListener{
            TimePickerFragment().show(supportFragmentManager, " endTime")
        }
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        if (tag == " startTime"){
            val startTime = findViewById<TextView>(R.id.add_text_start_time)
            startTime.text = dateFormat.format(calendar.time)
        } else{
            val endTime = findViewById<TextView>(R.id.add_text_end_time)
            endTime.text = dateFormat.format(calendar.time)
        }
    }
}