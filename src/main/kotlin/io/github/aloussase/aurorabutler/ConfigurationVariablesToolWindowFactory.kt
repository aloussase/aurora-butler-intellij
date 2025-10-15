package io.github.aloussase.aurorabutler

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JPanel
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
    }

    private fun updateRows() {
        invokeLater {
            tableModel.rowCount = 0
            for (variable in visibleConfigurationVariables) {
                tableModel.addRow(arrayOf(variable.name, variable.value))
            }
        }
    }

    private fun doRefresh(
        project: Project,
        statusMessage: String,
        cb: () -> Unit
    ) {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, statusMessage) {
            override fun run(p0: ProgressIndicator) {
                val auroraService = service<AuroraService>()

                configurationVariables = auroraService.fetchConfigurationVariables()
                visibleConfigurationVariables = ArrayList(configurationVariables)

                cb()
            }
        })
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

        table = JBTable(tableModel).also {
            it.cellSelectionEnabled = true
            it.rowSelectionAllowed = false
            it.columnSelectionAllowed = false
            it.addKeyListener(createKeyListener(it))
        }

        val panel = buildPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "Aurora Configuration Variables", false)
        toolWindow.contentManager.addContent(content)

        invokeLater {
            table.requestFocusInWindow()
        }

        doRefresh(project, "Loading configuration variables") {
            updateRows()
        }
    }

    private fun createKeyListener(table: JBTable): KeyListener {
        return object : KeyListener {
            override fun keyPressed(e: KeyEvent?) {
                if (!(e?.keyCode == KeyEvent.VK_C && (e.isControlDown || e.isMetaDown)))
                    return

                val row = table.selectedRow
                val col = table.selectedColumn

                val text = visibleConfigurationVariables[row].run {
                    if (col == 0) {
                        name
                    } else {
                        value
                    }
                }

                Toolkit.getDefaultToolkit()
                    .systemClipboard
                    .setContents(StringSelection(text), null)

                notifyCopiedToClipboard(text)

            }

            override fun keyTyped(e: KeyEvent?) {
            }

            override fun keyReleased(e: KeyEvent?) {
            }
        }
    }

    private fun notifyCopiedToClipboard(copiedText: String) {
        Notifications.Bus.notify(
            Notification(
                "AuroraButlerNotifications",
                "Text copied to clipboard",
                copiedText,
                NotificationType.INFORMATION
            )
        )
    }

    private fun buildPanel(project: Project): JPanel {
        return panel {
            row {
                textField()
                    .applyToComponent { emptyText.text = "Search for variables..." }
                    .align(Align.FILL)
                    .resizableColumn()
                    .onChanged {
                        applySearchFilter(it.text)
                        updateRows()
                    }

                button("Refresh") {
                    doRefresh(project, "Refreshing configuration variables") {
                        updateRows()
                    }
                }
            }

            separator()

            row {
                scrollCell(table)
                    .resizableColumn()
                    .align(Align.FILL)
            }
                .resizableRow()
        }
    }
}
