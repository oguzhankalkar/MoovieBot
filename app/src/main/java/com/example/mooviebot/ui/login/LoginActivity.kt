package com.example.mooviebot.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mooviebot.R
import com.example.mooviebot.ui.movie_search.MovieSearchActivity
import com.example.mooviebot.ui.movie_search.SplashActivity
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService

class LoginPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<View>(R.id.btnHuaweiId).setOnClickListener { signInWithId() }
        findViewById<View>(R.id.btnHuaweiAuth).setOnClickListener { signInWithAuthCode() }
        findViewById<View>(R.id.btnHuaweiSilent).setOnClickListener { silentSignIn() }
    }

    private fun signInWithId() {
        val authParams: AccountAuthParams =
            AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken()
                .createParams()
        val service: AccountAuthService =
            AccountAuthManager.getService(this, authParams)
        startActivityForResult(service.signInIntent, 8888)
    }

    private fun signInWithAuthCode() {
        val authParams: AccountAuthParams =
            AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode()
                .createParams()
        val service: AccountAuthService =
            AccountAuthManager.getService(this, authParams)
        startActivityForResult(service.signInIntent, 8887)
    }

    private fun silentSignIn() {
        val authParams: AccountAuthParams =
            AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service: AccountAuthService = AccountAuthManager.getService(this, authParams)
        val task: Task<AuthAccount> = service.silentSignIn()

        task.addOnSuccessListener { authAccount ->
            // Obtain the user's ID information.
            Log.i(TAG, "displayName:" + authAccount.displayName)
            // Obtain the **0**D type (0: HU**1**WEI ID; 1: AppTouch ID).
            Log.i(TAG, "accountFlag:" + authAccount.accountFlag);
            dealWithResultOfSignIn(authAccount)
        }

        task.addOnFailureListener { e ->
            // The sign-in failed. Your app can **getSignInIntent()**nIntent() method to explicitly display the authorization screen.
            if (e is ApiException) {
                Log.i(TAG, "sign failed status:" + e.statusCode)
            }
        }
    }

    private fun dealWithResultOfSignIn(authAccount: AuthAccount) {
        Log.i(TAG, "idToken:" + authAccount.idToken)
        if (authAccount.idToken == null) {
            Toast.makeText(this, "ID Token is not found!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, authAccount.idToken, Toast.LENGTH_SHORT).show();
        }
        val loginTypeName = "Huawei ID"
        val i = Intent(baseContext, MovieSearchActivity::class.java).also {
            it.putExtra("LOGIN TYPE NAME", loginTypeName)
            it.putExtra("LOGIN TYPE", "")
        }
        startActivity(i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 8888) {
            val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data)
            if (authAccountTask.isSuccessful) {
                val authAccount = authAccountTask.result
                Log.i(TAG, "idToken:" + authAccount.idToken)
                val loginTypeName = "Huawei ID"
                val loginType = authAccount.idToken.toString()
                val i = Intent(baseContext, MovieSearchActivity::class.java).also {
                    it.putExtra("LOGIN TYPE NAME", loginTypeName)
                    it.putExtra("LOGIN TYPE", loginType)
                }
                startActivity(i)

            } else {
                // The sign-in failed. No processing is required. Logs are recorded for fault locating.
                Log.e(
                    TAG,
                    "sign in failed : " + (authAccountTask.exception as ApiException).statusCode
                )
            }
        }
        if (requestCode == 8887) {
            val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data)
            if (authAccountTask.isSuccessful) {
                val authAccount = authAccountTask.result
                Log.i(TAG, "serverAuthCode:" + authAccount.authorizationCode)
                val loginTypeName = "Authorization Code"
                val loginType = authAccount.authorizationCode.toString()
                val i = Intent(baseContext, MovieSearchActivity::class.java).also {
                    it.putExtra("LOGIN TYPE NAME", loginTypeName)
                    it.putExtra("LOGIN TYPE", loginType)
                }
                startActivity(i)

            } else {
                Log.e(
                    TAG,
                    "sign in failed:" + (authAccountTask.exception as ApiException).statusCode
                )
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1000
        private const val TAG = "Account"
    }
}