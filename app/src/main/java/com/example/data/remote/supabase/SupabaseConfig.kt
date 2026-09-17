package com.example.data.remote.supabase

import com.example.BuildConfig

object SupabaseConfig {
    val url: String = BuildConfig.SUPABASE_URL.let {
        if (it.isBlank() || it.contains("YOUR_SUPABASE")) "https://dxhkqadlmnibckdqsppl.supabase.co" else it
    }
    val anonKey: String = BuildConfig.SUPABASE_ANON_KEY.let {
        if (it.isBlank() || it.contains("YOUR_SUPABASE")) "sb_publishable_vd_A0oH4m3QQwzEP7z_TIg_ekI3Mh_D" else it
    }

    val isConfigured: Boolean
        get() = url.isNotBlank() && anonKey.isNotBlank() && !anonKey.contains("YOUR_SUPABASE")
}
