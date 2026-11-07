// Di dalam onCreate() FloatingPanelService.kt ganti composeView-nya jadi seperti ini:
composeView?.setContent {
    var hasLicense by remember { mutableStateOf(OwnershipValidator.isLicenseValid()) }

    if (!hasLicense) {
        // Tampilkan GUI Login Kustom jika belum ada key / expired
        ModernLoginMenu(onLoginSuccess = { hasLicense = true })
    } else {
        // Jika Lisensi valid (Permanen / Timer belum habis), langsung tampilkan panel cheat
        ModernFloatingMenu(
            onHeadlockChanged = { active -> },
            onEspChanged = { active -> },
            onMinimizeClick = { stopSelf() }
        )
    }
}
