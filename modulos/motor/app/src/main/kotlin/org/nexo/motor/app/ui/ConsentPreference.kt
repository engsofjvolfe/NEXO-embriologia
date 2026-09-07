package org.nexo.motor.app.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Escolha de consentimento lembrada entre sessões (decisions/0045, item 5) -- nunca um dado que
 * identifique a pessoa (nome, papel), só se a última tela de Consentimento foi aceita e se essa
 * decisão deve ser lembrada.
 */
data class RememberedConsent(val given: Boolean)

private val Context.consentPreferenceDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "consent_preference",
)
private val CONSENT_GIVEN_KEY = booleanPreferencesKey("consent_given")

/** `choice = null` apaga a escolha lembrada -- mesmo caminho da revogação (LGPD, art. 8º, §5º). */
suspend fun saveConsentChoice(context: Context, choice: RememberedConsent?) {
    context.consentPreferenceDataStore.edit { preferences ->
        if (choice == null) {
            preferences.remove(CONSENT_GIVEN_KEY)
        } else {
            preferences[CONSENT_GIVEN_KEY] = choice.given
        }
    }
}

fun consentChoiceFlow(context: Context): Flow<RememberedConsent?> =
    context.consentPreferenceDataStore.data.map { preferences ->
        preferences[CONSENT_GIVEN_KEY]?.let { RememberedConsent(given = it) }
    }
