package io.github.aloussase.aurorabutler

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {
    private var mAppSettingsComponent: AppSettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): @NlsContexts.ConfigurableName String? {
        return "Aurora Butler"
    }

    override fun createComponent(): JComponent? {
        mAppSettingsComponent = AppSettingsComponent()
        return mAppSettingsComponent?.panel()
    }

    override fun isModified(): Boolean {
        return AppSettings.getInstance().state?.let {
            it.authToken != mAppSettingsComponent?.getAuthTokenText()
                    || it.apiBaseUrl != mAppSettingsComponent?.getApiBaseUrl()
                    || it.bluePrintName != mAppSettingsComponent?.getBluePrintNameText()
                    || it.deploymentId != mAppSettingsComponent?.getDeploymentIdText()
                    || it.environment != mAppSettingsComponent?.getEnvironmentText()
        } ?: false
    }

    override fun apply() {
        AppSettings.getInstance().state?.let {
            it.apiBaseUrl = mAppSettingsComponent?.getApiBaseUrl()
            it.authToken = mAppSettingsComponent?.getAuthTokenText()
            it.bluePrintName = mAppSettingsComponent?.getBluePrintNameText()
            it.deploymentId = mAppSettingsComponent?.getDeploymentIdText()
            it.environment = mAppSettingsComponent?.getEnvironmentText()
        }
    }

    override fun reset() {
        AppSettings.getInstance().state?.let {
            mAppSettingsComponent?.setApiBaseUrl(it.apiBaseUrl ?: "")
            mAppSettingsComponent?.setAuthToken(it.authToken ?: "")
            mAppSettingsComponent?.setBluePrintName(it.bluePrintName ?: "")
            mAppSettingsComponent?.setDeploymentId(it.deploymentId ?: "")
            mAppSettingsComponent?.setEnvironment(it.environment ?: "")
        }
    }

    override fun disposeUIResources() {
        mAppSettingsComponent = null
    }
}
