package io.github.aloussase.aurorabutler

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service


class PopupDialogAction : AnAction() {

    override fun update(event: AnActionEvent) {

    }

    override fun actionPerformed(event: AnActionEvent) {
        val auroraService = service<AuroraService>()
        val configurationVariables = auroraService.fetchConfigurationVariables()
    }

}
