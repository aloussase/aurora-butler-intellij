package io.github.aloussase.aurorabutler

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "com.github.aloussase.aurorbutler.AppSettings",
    storages = [Storage("AuroraButlerSettings.xml")]
)
class AppSettings : PersistentStateComponent<AppSettings.State> {
    data class State(
        var authToken: String? = null,
        var apiBaseUrl: String? = null,
        var bluePrintName: String? = null,
        var deploymentId: String? = null,
        var environment: String? = null,
    )

    companion object {
        fun getInstance(): AppSettings =
            ApplicationManager
                .getApplication()
                .getService(AppSettings::class.java)

    }

    private var state: State = State()

    override fun getState(): State? = state

    override fun loadState(state: State) {
        this.state = state
    }


}
