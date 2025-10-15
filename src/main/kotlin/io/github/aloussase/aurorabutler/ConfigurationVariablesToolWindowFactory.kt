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

    private var configurationVariables: List<ConfigurationVariable> = emptyList()
    private var visibleConfigurationVariables = emptyList<ConfigurationVariable>()

    private lateinit var table: JBTable
    private lateinit var tableModel: DefaultTableModel

    private fun applySearchFilter(filter: String) {
        visibleConfigurationVariables = configurationVariables
            .filter {
                it.name.contains(filter, ignoreCase = true)
                        || it.value.contains(filter, ignoreCase = true)

            }

        invokeLater {
            tableModel.rowCount = 0
            for (variable in visibleConfigurationVariables) {
                tableModel.addRow(arrayOf(variable.name, variable.value))
            }
        }
    }

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        tableModel = object : DefaultTableModel(
            arrayOf("Name", "Value"),
            0
        ) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false
            }
        }

        table = JBTable(tableModel)

        val panel: DialogPanel = panel {
            row {
                textField()
                    .applyToComponent { emptyText.text = "Search for variables..." }
                    .align(Align.FILL)
                    .resizableColumn()
                    .onChanged { applySearchFilter(it.text) }

                button("Refresh") {}
            }

            separator()

            row {
                scrollCell(table)
                    .resizableColumn()
                    .align(Align.FILL)
            }
                .resizableRow()
        }

        val content = ContentFactory.getInstance().createContent(panel, "Aurora Configuration Variables", false)

        toolWindow.also {
            it.contentManager.addContent(content)
        }

        invokeLater {
            table.requestFocusInWindow()
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Loading configuration variables") {
            override fun run(p0: ProgressIndicator) {
                val auroraService = service<AuroraService>()

                configurationVariables = auroraService.fetchConfigurationVariables()
                visibleConfigurationVariables = configurationVariables

                invokeLater {
                    for (variable in configurationVariables) {
                        tableModel.addRow(arrayOf(variable.name, variable.value))
                    }
                }

            }
        })
    }
}
