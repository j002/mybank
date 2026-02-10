package org.djibril.mybank.ui

sealed interface Screen {
    data object Banks : Screen
    data class Operations(
        val bankName: String,
        val accountLabel: String
    ) : Screen
}