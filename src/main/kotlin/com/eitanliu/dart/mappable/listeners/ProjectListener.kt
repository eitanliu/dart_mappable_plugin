package com.eitanliu.dart.mappable.listeners

// import com.eitanliu.compat.listeners.CompatProjectListener
// import com.eitanliu.dart.mappable.services.ProjectService
// import com.intellij.openapi.components.service
// import com.intellij.openapi.project.Project
// import com.intellij.openapi.project.ProjectManagerListener


/**
 *     <applicationListeners>
 *         <listener class="com.eitanliu.dart.mappable.listeners.ProjectListener"
 *             topic="com.intellij.openapi.project.ProjectManagerListener" />
 *     </applicationListeners>
 *
 * import com.intellij.openapi.project.ProjectManagerListener
 */
// internal class ProjectListener : ProjectManagerListener, CompatProjectListener {
//
//     override fun projectOpened(project: Project) {
//         project.service<ProjectService>()
//     }
// }

/**
 *     <extensions defaultExtensionNs="com.intellij">
 *         <postStartupActivity implementation="com.eitanliu.dart.mappable.listeners.ProjectListener" />
 *     </extensions>
 *
 * before 231
 * import com.intellij.openapi.startup.StartupActivity
 */
// internal class ProjectListener : StartupActivity {
//     override fun runActivity(project: Project) {
//         project.service<ProjectService>()
//
//     }
// }
