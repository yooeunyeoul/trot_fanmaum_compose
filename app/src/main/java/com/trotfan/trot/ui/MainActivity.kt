package com.trotfan.trot.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.integration.IntegrationHelper
import com.trotfan.trot.LoadingHelper
import com.trotfan.trot.PurchaseHelper
import com.trotfan.trot.R
import com.trotfan.trot.ui.signup.viewmodel.InvitationViewModel
import com.trotfan.trot.ui.utils.composableActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //    private val invitationViewModel: InvitationViewModel by viewModels()
    @Inject
    lateinit var purchaseHelper: PurchaseHelper

    @Inject
    lateinit var loadingHelper: LoadingHelper

    private var invitationCode: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDynamicLink()
        settingIronSource()
//        var keyHash = Utility.getKeyHash(this)
//        Timber.tag("kakaohash").d(keyHash)

//        val appSignature = AppSignatureHelper(this)
//        Log.e("HASH", "\"${appSignature.appSignatures}\"")


        setContent {
            val isProgressVisible by loadingHelper.progressbar.collectAsState()
            val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_spinner))
            val invitationViewModel: InvitationViewModel =
                composableActivityViewModel(key = "InvitationViewModel")
            if (!invitationCode.isNullOrEmpty()) {
                invitationViewModel.setInvitationCode(invitationCode)
                invitationCode = ""
            }

            Box() {
                if (isProgressVisible) {
                    Dialog(
                        onDismissRequest = { },
                        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                    ) {
                        Box(
                            contentAlignment = Center,
                            modifier = Modifier
                                .size(120.dp)
                                .background(Transparent, shape = RoundedCornerShape(8.dp))
                                .align(Center)
                        ) {
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                }
                FanwooriApp(
                    purchaseHelper = purchaseHelper
                )
            }

        }
    }


    private fun getDynamicLink() {
        Firebase.dynamicLinks.getDynamicLink(intent)
            .addOnSuccessListener {
                var deepLink: Uri? = null
                if (it != null) {
                    lifecycleScope.launch {
                        deepLink = it.link
                        deepLink?.let { deepLink ->
                            if (deepLink.getBooleanQueryParameter("insertFriendCode", false)) {
                                val code = deepLink?.getQueryParameters("insertFriendCode")
                                code?.let { codeList ->
                                    if (codeList.isNotEmpty()) {
                                        invitationCode = codeList[0].toString()
                                    }
                                }
                                Timber.e(deepLink.toString())
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.d("dynamicLinkTest", it.toString())
            }
    }

    private fun settingIronSource() {
        IronSource.init(this, "17f942e2d", IronSource.AD_UNIT.REWARDED_VIDEO)
        IntegrationHelper.validateIntegration(this)
    }

    override fun onResume() {
        super.onResume()
        IronSource.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        IronSource.onPause(this)
    }
}
