package org.jiwhaja

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.jiwhaja.Adapter.TabAdapterMain
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    private val adapter by lazy{ TabAdapterMain(supportFragmentManager) }
    val PERMISSIONS_REQUEST_CODE = 100

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSIONS_REQUEST_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity()
                    }else{
                        ActivityCompat.finishAffinity(this)
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    System.exit(0)
                }
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        vpMainActivity.adapter = WholeActivity@adapter

        tabLayout.setupWithViewPager(vpMainActivity)
        tabLayout.getTabAt(0)?.setCustomView(R.layout.tablayout_icon_map)
        tabLayout.getTabAt(1)?.setCustomView(R.layout.tablayout_icon_list)
        tabLayout.getTabAt(2)?.setCustomView(R.layout.tablayout_icon_help)
        tabLayout.getTabAt(3)?.setCustomView(R.layout.tablayout_icon_setting)

    }


    fun getHashKey(){
        var packageInfo : PackageInfo = PackageInfo()
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e:PackageManager.NameNotFoundException){
            e.printStackTrace()
        }

        for (signature: Signature in packageInfo.signatures){
            try{
                var md:MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KEY_HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch(e:NoSuchAlgorithmException){
                Log.e("KEY_HASH", "Unable to get MessageDigest. signature = " + signature, e)
            }
        }
    }

}
