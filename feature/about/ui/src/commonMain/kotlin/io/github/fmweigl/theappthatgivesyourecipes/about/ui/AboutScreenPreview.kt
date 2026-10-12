package io.github.fmweigl.theappthatgivesyourecipes.about.ui

import androidx.compose.runtime.Composable
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.preview.DevicePreviews
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.preview.MealsPreview

@DevicePreviews
@Composable
private fun AboutScreenPreview() {
    MealsPreview {
        AboutScreen(onNavigate = {}, versionName = "1.0.42", versionCode = 42)
    }
}
