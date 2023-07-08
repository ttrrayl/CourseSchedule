package com.dicoding.courseschedule.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.CalendarContract.Reminders
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.ui.home.HomeActivity
import com.dicoding.courseschedule.util.*
import java.util.*

class DailyReminder : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        executeThread {
            val repository = DataRepository.getInstance(context)
            val courses = repository?.getTodaySchedule()

            courses?.let {
                if (it.isNotEmpty()) showNotification(context, it)
            }
        }
    }

    //TODO 12 : Implement daily reminder for every 06.00 a.m using AlarmManager
    fun setDailyReminder(context: Context) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderIntent = Intent(context, DailyReminder::class.java)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, reminderIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, "Set up Repeating Alarm", Toast.LENGTH_SHORT).show()


//        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            PendingIntent.getBroadcast(context, ID_REPEATING, reminderIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
//        } else {
//            PendingIntent.getBroadcast(context, ID_REPEATING, reminderIntent, PendingIntent.FLAG_CANCEL_CURRENT)
//        }
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderIntent = Intent(context, DailyReminder::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(context, ID_REPEATING, reminderIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(context, ID_REPEATING, reminderIntent, 0)
        }
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
    }

    private fun showNotification(context: Context, content: List<Course>) {
        //TODO 13 : Show today schedules in inbox style notification & open HomeActivity when notification tapped
        Log.d("Reminder", "NOTIFICATION START")
        val notificationStyle = NotificationCompat.InboxStyle()
        val timeString = context.resources.getString(R.string.notification_message_format)
        content.forEach {
            val courseData = String.format(timeString, it.startTime, it.endTime, it.courseName)
            notificationStyle.addLine(courseData)
        }
        val intent = Intent(context, HomeActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Today's Course Reminder"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                vibrationPattern =
                    longArrayOf(100, 200, 300, 400, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.title_activity_list))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notifications)
            .setStyle(notificationStyle)
            .build()

        notificationManager.notify(NOTIFICATION_ID, builder)


        Log.d("Reminder", "NOTIFICATION EXECUTED")


//        val notificationStyle = NotificationCompat.InboxStyle()
//        val timeString = context.resources.getString(R.string.notification_message_format)
//        content.forEach {
//            val courseData = String.format(timeString, it.startTime, it.endTime, it.courseName)
//            notificationStyle.addLine(courseData)
//        }
//
//        val intent = Intent(context, HomeActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE)
////        val pendingIntent = TaskStackBuilder.create(context)
////            .addParentStack(HomeActivity::class.java)
////            .addNextIntent(intent)
////            .getPendingIntent(ID_REPEATING, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
//            .setContentIntent(pendingIntent)
//            .setSmallIcon(R.drawable.ic_notifications)
//            .setContentTitle(context.resources.getString(R.string.today_schedule))
//            .setContentText(context.resources.getString(R.string.notification_message_format))
//            .setStyle(notificationStyle)
//            .setAutoCancel(true)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
//            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
//            notificationManager.createNotificationChannel(channel)
//        }
//        val notification = builder.build()
//        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}