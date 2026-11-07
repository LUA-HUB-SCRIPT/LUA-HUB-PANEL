package com.luahub.injector

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import moe.shizuku.api.Shizuku

object OwnershipValidator {
    private const val ORIGINAL_SIGNATURE_SHA256 = "DE:AD:BE:EF:12:34:56:78:90:AB:CD:EF"
    
    // Hardcoded Key sesuai request lo
    private const val KEY_PERMANENT = "LUAHUB098708PERMANEN"
    private const val KEY_TIMER = "FREE-KEY-1-MINGGU"
    
    // Path rahasia di dalam folder Free Fire untuk menyimpan status aktivasi
    private const val TARGET_PATH = "/sdcard/Android/data/com.dts.freefireth/files/contentcache/Optional/"
    private const val KEY_FILE_NAME = ".lh_license.txt"

    // Verifikasi Signature Aplikasi (Anti-Copy)
    fun verifyAppIntegrity(context: Context): Boolean {
        // ... (Kode signature SHA-256 yang kemarin tetap di sini)
        return true 
    }

    // Fungsi untuk memvalidasi Key yang dimasukkan user
    fun validateUserKey(inputKey: String): String {
        if (inputKey == KEY_PERMANENT) {
            saveLicenseStatus("PERMANENT")
            return "SUCCESS_PERMANENT"
        }
        
        if (inputKey == KEY_TIMER) {
            // Hitung waktu expired: Waktu sekarang + 7 hari (dalam milidetik)
            val oneWeekMillis = 7L * 24 * 60 * 60 * 1000
            val expiryTimestamp = System.currentTimeMillis() + oneWeekMillis
            saveLicenseStatus(expiryTimestamp.toString())
            return "SUCCESS_TIMER"
        }
        
        return "WRONG_KEY"
    }

    // Fungsi mengecek apakah lisensi di data game masih aktif
    fun isLicenseValid(): Boolean {
        if (!Shizuku.pingBinder()) return false
        
        // Membaca file status di folder FF menggunakan shell Shizuku
        val command = "cat $TARGET_PATH$KEY_FILE_NAME"
        return try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            val result = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()

            if (result == "PERMANENT") return true
            
            val expiryTime = result.toLongOrNull() ?: return false
            // Jika waktu sekarang belum melewati batas kedaluwarsa, berarti masih valid
            System.currentTimeMillis() < expiryTime
        } catch (e: Exception) {
            false
        }
    }

    // Menyimpan status key ke folder Free Fire menggunakan Shizuku (Bypass Scoped Storage)
    private fun saveLicenseStatus(content: String) {
        if (!Shizuku.pingBinder()) return
        val command = "mkdir -p $TARGET_PATH && echo '$content' > $TARGET_PATH$KEY_FILE_NAME"
        try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
