package org.jiwhaja

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import org.jetbrains.anko.toast
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Intro :AppCompatActivity(){

    val REQUEST_CODE_UPDATE = 205
    lateinit var appUpdateManager :AppUpdateManager
    lateinit var appUpdateInfoTask:Task<AppUpdateInfo>
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_UPDATE){
            if(resultCode != Activity.RESULT_OK){
                toast("업데이트가 취소 되었습니다.")
                finishAffinity()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if(it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                appUpdateManager.startUpdateFlowForResult(
                    it, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //화면 회전 없앰
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        var filterLocation = ""
        val pref : SharedPreferences = getSharedPreferences("prefs", 0)
        filterLocation = pref.getString("location", "지역")!!


        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateInfoTask = appUpdateManager.appUpdateInfo


        appUpdateManager?.let{
            it.appUpdateInfo.addOnSuccessListener {appUpdateInfo ->
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE
                    )
                }
            }
        }





        if(filterLocation == "지역" || filterLocation == ""){
            try{
                Thread.sleep(1000);
                val intent = Intent(this, FirstActivity::class.java)
                startActivity(intent)
                finish()
            }
            catch(e:Exception){
                return
            }
        }else{
            try{
                Thread.sleep(1000);
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            catch(e:Exception){
                return
            }
        }
    }

}