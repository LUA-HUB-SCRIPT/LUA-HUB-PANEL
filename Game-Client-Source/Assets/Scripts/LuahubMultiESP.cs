using UnityEngine;
using System;
using System.Runtime.InteropServices;
using System.Collections.Generic;

public class LuahubMultiESP : MonoBehaviour
{
    [Header("Aimbot Settings")]
    public bool isHeadlockActive = false;
    public float targetSmoothness = 6.5f;
    public float headHeightOffset = 1.75f; // Koreksi tinggi agar pas di kepala musuh

    [Header("ESP Settings")]
    public bool isEspActive = false;

    // Alamat offset dinamis yang akan diisi secara otomatis oleh pemindai AOB (All Version)
    private static int AUTOMATIC_HEAD_OFFSET = 0; 

    public struct PlayerTarget {
        public Vector3 headPos;
        public Vector3 basePos;
    }

    // List untuk menampung semua pointer pemain aktif di dalam room dari injector luar
    public List<IntPtr> enemyPointers = new List<IntPtr>();

    void Start()
    {
        // Jalankan pemindaian signature saat skrip pertama kali aktif untuk menentukan offset
        InitializeAllVersionOffset();
    }

    // ==========================================
    // LOGIKA ALL VERSION (AOB / PATTERN SCANNING)
    // ==========================================
    private void InitializeAllVersionOffset()
    {
        // 1. Dapatkan alamat modul dasar libil2cpp.so di RAM
        IntPtr libil2cppBase = GetModuleBaseAddress("libil2cpp.so");
        if (libil2cppBase == IntPtr.Zero)
        {
            // Jika modul belum siap, gunakan fallback versi standar dahulu
            AUTOMATIC_HEAD_OFFSET = 0x458; 
            return;
        }

        // 2. Tanda tangan byte (AOB) unik untuk fungsi posisi pemain di Free Fire
        string headSignature = "4F A0 70 D5 E0 03 00 AA ?? ?? ?? ?? F3 03 01 AA"; 
        
        // 3. Pindai memori RAM untuk mencari kecocokan tanda tangan tersebut (Batasi scan 64MB awal)
        IntPtr functionAddress = FindPattern(libil2cppBase, 0x04000000, headSignature); 
        
        if (functionAddress != IntPtr.Zero)
        {
            // 4. Ekstrak nilai offset asli langsung dari instruksi mesin RAM secara dinamis
            int extractedOffset = ReadInt32(new IntPtr(functionAddress.ToInt64() + 8)) & 0xFFF;
            AUTOMATIC_HEAD_OFFSET = extractedOffset;
        }
        else
        {
            // Fallback cadangan jika pemindaian AOB gagal
            AUTOMATIC_HEAD_OFFSET = 0x458; 
        }
    }

    void Update()
    {
        // Mengecek apakah fitur Headlock aktif, Kamera tersedia, dan Offset sudah ditemukan
        if (isHeadlockActive && Camera.main != null && AUTOMATIC_HEAD_OFFSET != 0) 
        {
            PlayerTarget closeEnemy = FindClosestEnemy();
            if (closeEnemy.headPos != Vector3.zero) 
            {
                // Hitung arah pandang dari kamera lokal ke koordinat kepala musuh
                Vector3 lookDirection = (closeEnemy.headPos - Camera.main.transform.position).normalized;
                
                // Mengunci arah kamera secara halus (Smooth Lock) berdasarkan targetSmoothness
                Camera.main.transform.forward = Vector3.Lerp(Camera.main.transform.forward, lookDirection, Time.deltaTime * targetSmoothness);
            }
        }
    }

    private PlayerTarget FindClosestEnemy()
    {
        PlayerTarget closestTarget = new PlayerTarget();
        closestTarget.headPos = Vector3.zero;
        float closestDistance = Mathf.Infinity;

        foreach (IntPtr playerPtr in enemyPointers)
        {
            if (playerPtr == IntPtr.Zero) continue;

            // 1. Membaca posisi dasar/root objek dari memori RAM
            IntPtr dataAddress = new IntPtr(playerPtr.ToInt64() + AUTOMATIC_HEAD_OFFSET);
            Vector3 enemyBasePos = ReadVector3(dataAddress);

            if (enemyBasePos != Vector3.zero)
            {
                // 2. KOREKSI LOCK KEPALA (Height Fix)
                // Menambahkan sumbu Y sebesar headHeightOffset agar crosshair naik dari kaki ke kepala
                Vector3 preciseHeadPos = new Vector3(enemyBasePos.x, enemyBasePos.y + headHeightOffset, enemyBasePos.z);

                // Hitung jarak terdekat untuk memprioritaskan musuh terdekat dari crosshair
                float distance = Vector3.Distance(Camera.main.transform.position, preciseHeadPos);
                if (distance < closestDistance)
                {
                    closestDistance = distance;
                    
                    // Mengunci target mutlak ke koordinat kepala yang sudah ditinggikan
                    closestTarget.headPos = preciseHeadPos; 
                    
                    // Estimasi posisi kaki untuk keperluan ESP Line/Box bawah
                    closestTarget.basePos = enemyBasePos; 
                }
            }
        }

        return closestTarget;
    }

    // ==========================================
    // FUNGSI UTAS PEMBANTU MEMORI (RAM READERS)
    // ==========================================
    private Vector3 ReadVector3(IntPtr address)
    {
        try {
            float x = ReadFloat(address);
            float y = ReadFloat(new IntPtr(address.ToInt64() + 4));
            float z = ReadFloat(new IntPtr(address.ToInt64() + 8));
            return new Vector3(x, y, z);
        } catch {
            return Vector3.zero;
        }
    }

    private float ReadFloat(IntPtr address)
    {
        byte[] buffer = new byte[4];
        Marshal.Copy(address, buffer, 0, 4);
        return BitConverter.ToSingle(buffer, 0);
    }

    private int ReadInt32(IntPtr address)
    {
        byte[] buffer = new byte[4];
        Marshal.Copy(address, buffer, 0, 4);
        return BitConverter.ToInt32(buffer, 0);
    }

    // ==========================================
    // NATIVE MEMORY INTERACTION INTERFACE
    // (Sesuaikan dengan library injector Android Anda)
    // ==========================================
    private IntPtr GetModuleBaseAddress(string moduleName)
    {
        // Catatan: Pasangkan fungsi ini dengan pembaca native /proc/self/maps internal mod menu Anda
        // Untuk simulasi internal engine, jika mengembalikan IntPtr.Zero, ia akan memakai offset fallback (0x458)
        return IntPtr.Zero; 
    }

    private IntPtr FindPattern(IntPtr startAddress, int size, string pattern)
    {
        // Fungsi pencari array of bytes (AOB Scan) di area RAM
        return IntPtr.Zero;
    }
}
