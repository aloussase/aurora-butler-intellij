package io.github.aloussase.aurorabutler

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class AppSettingsComponent {

    val mMainPanel: JPanel
    val mAuthTokenText: JBTextField = JBTextField()
    val mApiBaseUrlTextField: JBTextField = JBTextField()
    val mBluePrintNameText: JBTextField = JBTextField()
    val mDeploymentIdText: JBTextField = JBTextField()
    val mEnvironmentText: JBTextField = JBTextField()

    fun panel() = mMainPanel

    fun getApiBaseUrl(): String = mApiBaseUrlTextField.text

    fun setApiBaseUrl(value: String) {
        mApiBaseUrlTextField.text = value
    }

    fun getAuthTokenText() = mAuthTokenText.text

    fun setAuthToken(token: String) {
        mAuthTokenText.text = token
    }

    fun getBluePrintNameText() = mBluePrintNameText.text

    fun setBluePrintName(value: String) {
        mBluePrintNameText.text = value
    }

    fun getDeploymentIdText() = mDeploymentIdText.text

    fun setDeploymentId(value: String) {
        mDeploymentIdText.text = value
    }

    fun getEnvironmentText() = mEnvironmentText.text

    fun setEnvironment(value: String) {
        mEnvironmentText.text = value
    }

    init {
        mMainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("API base URL:"), mApiBaseUrlTextField, 1, false)
            .addLabeledComponent(JBLabel("Auth token:"), mAuthTokenText, 1, false)
            .addLabeledComponent(JBLabel("Blueprint ID:"), mBluePrintNameText, 1, false)
            .addLabeledComponent(JBLabel("Deployment ID:"), mDeploymentIdText, 1, false)
            .addLabeledComponent(JBLabel("Environment:"), mEnvironmentText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }


}
