package com.luahub.injector

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest

object OwnershipValidator {
    // Masukkan SHA-256 dari Keystore asli milikmu di sini agar anti-copy!
    private const val ORIGINAL_SIGNATURE_SHA256 = "DE:AD:BE:EF:12:34:56:78:90:AB:CD:EF"

    fun verifyAppIntegrity(context: Context): Boolean {
        try {
            val packageManager = context.packageManager
            val packageName = context.packageName
            
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                packageInfo.signatures
            }

            signatures?.forEach { signature ->
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(signature.toByteArray())
                val currentSignature = digest.joinToString(":") { String.format("%02X", it) }
                
                if (currentSignature == ORIGINAL_SIGNATURE_SHA256) {
                    return true // Aplikasi asli dan sah!
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false // Terdeteksi kloning / modifikasi pihak ketiga!
    }
}
