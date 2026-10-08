package io.github.fmweigl.theappthatgivesyourecipes

import androidx.compose.ui.window.ComposeUIViewController

// Named like the view controller it returns; Swift calls it as MainViewControllerKt.MainViewController().
@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController { App() }
