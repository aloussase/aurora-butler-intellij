package io.github.aloussase.aurorabutler

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.pom.Navigatable


class PopupDialogAction : AnAction() {

    override fun update(event: AnActionEvent) {

    }

    override fun actionPerformed(event: AnActionEvent) {
        val auroraService = service<AuroraService>()
        val configurationVariables = auroraService.fetchConfigurationVariables()

        val currentProject: Project? = event.project
        val message: StringBuilder =
            StringBuilder(event.presentation.text + " Selected!")

        val selectedElement: Navigatable? = event.getData(CommonDataKeys.NAVIGATABLE)
        if (selectedElement != null) {
            message.append("\nSelected Element: ").append(selectedElement)
        }

        val title = event.presentation.description
        Messages.showMessageDialog(
            currentProject,
            message.toString(),
            title,
            Messages.getInformationIcon()
        )
    }

}
