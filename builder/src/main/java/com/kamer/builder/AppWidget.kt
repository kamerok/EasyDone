package com.kamer.builder

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class AppWidget : AppWidgetProvider() {

    private val repository: DomainRepository = GlobalContext.get().get()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        GlobalScope.launch {
            val tasks = repository.getTasks(Task.Type.ToDo::class).first()
            val inboxCount = repository.getTasks(Task.Type.Inbox::class).first().size
            val urgentImportantCount: Int =
                tasks.count { it.markers.isUrgent && it.markers.isImportant }
            val urgentCount: Int = tasks.count { it.markers.isUrgent }
            val importantCount: Int = tasks.count { it.markers.isImportant }
            val noFlagsCount: Int = tasks.count { !it.markers.isImportant && !it.markers.isUrgent }

            val startMainActivityIntent: PendingIntent = PendingIntent.getActivity(
                /* context = */ context,
                /* requestCode = */  0,
                /* intent = */ Intent(context, MainActivity::class.java),
                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val startMainActivityInboxIntent: PendingIntent = PendingIntent.getActivity(
                /* context = */ context,
                /* requestCode = */  1,
                /* intent = */ Intent(context, MainActivity::class.java).putExtra("inbox", true),
                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val startTransparentActivityIntent: PendingIntent = PendingIntent.getActivity(
                /* context = */ context,
                /* requestCode = */  0,
                /* intent = */ Intent(context, TransparentActivity::class.java),
                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.app_widget).apply {
                    setTextViewText(R.id.inbox_text, "Inbox:$inboxCount")
                    setTextViewText(R.id.urgent_important_text, urgentImportantCount.toString())
                    setTextViewText(R.id.urgent_text, urgentCount.toString())
                    setTextViewText(R.id.important_text, importantCount.toString())
                    setTextViewText(R.id.no_flags_text, noFlagsCount.toString())

                    setOnClickPendingIntent(R.id.inbox_text, startMainActivityInboxIntent)
                    setOnClickPendingIntent(R.id.app_name_text, startMainActivityIntent)
                    setOnClickPendingIntent(R.id.urgent_important_text, startMainActivityIntent)
                    setOnClickPendingIntent(R.id.urgent_text, startMainActivityIntent)
                    setOnClickPendingIntent(R.id.important_text, startMainActivityIntent)
                    setOnClickPendingIntent(R.id.no_flags_text, startMainActivityIntent)
                    setOnClickPendingIntent(R.id.add_view, startTransparentActivityIntent)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }
}
