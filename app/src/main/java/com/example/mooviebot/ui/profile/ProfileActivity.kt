package com.example.mooviebot.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mooviebot.R
import com.example.mooviebot.databinding.ActivityProfileBinding
import com.huawei.agconnect.crash.AGConnectCrash
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.service.AccountAuthService

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        binding = ActivityProfileBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        val loginTypeName = intent.getStringExtra("LOGIN TYPE NAME")
        val loginType = intent.getStringExtra("LOGIN TYPE")

        binding.tvLoginField.text = loginType
        binding.tvLoginFieldTitle.text = loginTypeName

        binding.btnLogout.setOnClickListener{
            logOut()
        }
        binding.btnCrash.setOnClickListener{
            AGConnectCrash.getInstance().testIt(this)
        }
        binding.btnCustomReport.setOnClickListener {
            AGConnectCrash.getInstance().setUserId("testUser3");
            AGConnectCrash.getInstance().log(Log.DEBUG,"set debug log.");
            AGConnectCrash.getInstance().log(Log.INFO,"set info log.");
            AGConnectCrash.getInstance().log(Log.WARN,"set warning log.");
            AGConnectCrash.getInstance().log(Log.ERROR,"set error log.");
            AGConnectCrash.getInstance().setCustomKey("stringKey", "Hello world");
            AGConnectCrash.getInstance().setCustomKey("booleanKey", true);
            AGConnectCrash.getInstance().setCustomKey("doubleKey", 2.2);
            AGConnectCrash.getInstance().setCustomKey("floatKey", 2.2f);
            AGConnectCrash.getInstance().setCustomKey("intKey", 22);
            AGConnectCrash.getInstance().setCustomKey("longKey", 22L);
        }
    }

    private fun logOut(){
        val authParams: AccountAuthParams =
            AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken()
                .createParams()
        val service: AccountAuthService =
            AccountAuthManager.getService(this, authParams)

        // service indicates the AccountAuthService instance generated using the getService method during the sign-in authorization.
        val signOutTask = service.signOut()
    }
}