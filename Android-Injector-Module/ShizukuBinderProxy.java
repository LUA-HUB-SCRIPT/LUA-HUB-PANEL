package com.luahub.injector;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import moe.shizuku.api.Shizuku;

public class ShizukuBinderProxy {

    public static boolean checkAndRequestShizuku(Context context) {
        // 1. Validasi Kepemilikan Aplikasi Terlebih Dahulu
        if (!OwnershipValidator.verifyAppIntegrity(context)) {
            return false; // Akses Ditolak total karena aplikasi di-copy
        }

        // 2. Cek status Shizuku Binder
        if (!Shizuku.pingBinder()) {
            // Jika Shizuku belum aktif, arahkan pengguna aktifkan Debug Nirkabel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            return false;
        }
        return true;
    }

    // Eksekusi pemindahan data aman ke com.dts.freefireth (Bypass Scoped Storage)
    public static boolean safeInject(String sourcePath, String fileName) {
        if (!Shizuku.pingBinder()) return false;

        String targetPath = "/sdcard/Android/data/com.dts.freefireth/files/contentcache/Optional/";
        // Command bypass via ADB Shell Shizuku
        String command = "mkdir -p " + targetPath + " && cp " + sourcePath + " " + targetPath + fileName;

        try {
            Process process = Shizuku.newProcess(new String[]{"sh", "-c", command}, null, null);
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
