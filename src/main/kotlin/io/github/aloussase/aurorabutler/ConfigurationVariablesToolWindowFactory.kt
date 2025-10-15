package io.github.aloussase.aurorabutler

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import javax.swing.table.DefaultTableModel

class ConfigurationVariablesToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        val tableModel = DefaultTableModel(
            arrayOf("Name", "Value"),
            0
        )

        val table = JBTable(tableModel)


        val panel: DialogPanel = panel {
            row {
                scrollCell(table)
                    .resizableColumn()
                    .align(Align.FILL)
            }.resizableRow()
        }

        val content = ContentFactory.getInstance().createContent(panel, "Aurora Configuration Variables", false)

        toolWindow.also {
            it.contentManager.addContent(content)
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Loading configuration variables") {
            override fun run(p0: ProgressIndicator) {
                val auroraService = service<AuroraService>()
                val configurationVariables = auroraService.fetchConfigurationVariables()

                invokeLater {
                    for (variable in configurationVariables) {
                        tableModel.addRow(arrayOf(variable.name, variable.value))
                    }
                }

            }
        })
    }
}
