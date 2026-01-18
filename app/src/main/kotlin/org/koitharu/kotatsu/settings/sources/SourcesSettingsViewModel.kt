package org.koitharu.kotatsu.settings.sources

import kotlinx.coroutines.launch
import org.koitharu.kotatsu.core.model.CustomMangaSource
import org.koitharu.kotatsu.parsers.model.ContentType
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.core.ui.BaseViewModel
import org.koitharu.kotatsu.explore.data.MangaSourcesRepository
import javax.inject.Inject

@HiltViewModel
class SourcesSettingsViewModel @Inject constructor(
	sourcesRepository: MangaSourcesRepository,
	@ApplicationContext private val context: Context,
) : BaseViewModel() {

	private val linksHandlerActivity = ComponentName(context, "org.koitharu.kotatsu.details.ui.DetailsByLinkActivity")

	val enabledSourcesCount = sourcesRepository.observeEnabledSourcesCount()
		.withErrorHandling()
		.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, -1)

	val availableSourcesCount = sourcesRepository.observeAvailableSourcesCount()
		.withErrorHandling()
		.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, -1)

	val isLinksEnabled = MutableStateFlow(isLinksEnabled())

	fun setLinksEnabled(isEnabled: Boolean) {
		context.packageManager.setComponentEnabledSetting(
			linksHandlerActivity,
			if (isEnabled) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED,
			PackageManager.DONT_KILL_APP,
		)
		isLinksEnabled.value = isLinksEnabled()
	}

	private fun isLinksEnabled(): Boolean {
		val state = context.packageManager.getComponentEnabledSetting(linksHandlerActivity)
		return state == COMPONENT_ENABLED_STATE_ENABLED || state == COMPONENT_ENABLED_STATE_DEFAULT
	}

	fun addCustomSource(url: String) {
		viewModelScope.launch {
			val name = "CUSTOM:$url|${url.substringAfter("://").substringBefore("/")}|en|MANGA"
			val source = CustomMangaSource(
				name = name,
				title = url.substringAfter("://").substringBefore("/"),
				locale = "en",
				contentType = ContentType.MANGA,
			)
			sourcesRepository.setSourcesEnabled(listOf(source), true)
		}
	}
}
