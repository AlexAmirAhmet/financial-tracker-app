plugins {
    alias(libs.plugins.android.application) apply false
    // Объявлен, чтобы поднять версию Kotlin до актуальной (сам Kotlin встроен в AGP 9).
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
