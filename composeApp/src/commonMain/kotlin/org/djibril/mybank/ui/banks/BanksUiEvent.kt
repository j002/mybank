package org.djibril.mybank.ui.banks

sealed interface BanksUiEvent {

    /**
     * Demande de navigation vers l’écran des opérations
     * pour un compte donné.
     */
    data class OpenAccount(
        val bankName: String,
        val accountLabel: String
    ) : BanksUiEvent
}
