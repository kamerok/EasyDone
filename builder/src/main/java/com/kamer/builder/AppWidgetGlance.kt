package com.kamer.builder

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext

private val SMALL_BOX = DpSize(48.dp, 48.dp)
private val ROW = DpSize(200.dp, 48.dp)
private val BIG_BOX = DpSize(200.dp, 200.dp)

class AppWidgetGlance(private val state: State<WidgetState>) : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SMALL_BOX, ROW, BIG_BOX)
    )

    @Composable
    override fun Content() {
        when (LocalSize.current) {
            SMALL_BOX -> SmallWidget(state)
            ROW -> RowWidget(state)
            BIG_BOX -> BigWidget(state)
        }
    }
}

@Composable
private fun SmallWidget(state: State<WidgetState>) {
    val context = LocalContext.current
    val colorBackground by derivedStateOf {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
        Color(typedValue.data)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = GlanceModifier
            .size(SMALL_BOX.width, SMALL_BOX.height)
            .background(colorBackground)
            .appWidgetBackground()
            .clickable(actionStartActivity<MainActivity>())
    ) {
        InboxIcon(count = state.value.inboxCount)
    }
}

@Composable
private fun RowWidget(state: State<WidgetState>) {
    val context = LocalContext.current
    val colorBackground by derivedStateOf {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
        Color(typedValue.data)
    }
    TopRow(
        state = state,
        modifier = GlanceModifier
            .size(ROW.width, ROW.height)
            .background(colorBackground)
            .appWidgetBackground()
            .clickable(actionStartActivity<MainActivity>())
    )
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun BigWidget(state: State<WidgetState>) {
    val context = LocalContext.current
    val colorBackground by derivedStateOf {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
        Color(typedValue.data)
    }
    Column(
        modifier = GlanceModifier
            .size(BIG_BOX.width, BIG_BOX.height)
            .background(colorBackground)
            .appWidgetBackground()
            .clickable(actionStartActivity<MainActivity>())
    ) {
        TopRow(
            state,
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        val rowVerticalAlignment = Alignment.CenterVertically
        val rowModifier = GlanceModifier.defaultWeight().fillMaxWidth()
        val textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = TextUnit(32f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = rowVerticalAlignment,
            modifier = rowModifier
        ) {
            Text(
                text = "${state.value.urgentImportantCount}",
                style = textStyle,
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = "${state.value.urgentCount}",
                style = textStyle,
                modifier = GlanceModifier.defaultWeight()
            )
        }
        Row(
            verticalAlignment = rowVerticalAlignment,
            modifier = rowModifier,
        ) {
            Text(
                text = "${state.value.importantCount}",
                style = textStyle,
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = "${state.value.noFlagsCount}",
                style = textStyle,
                modifier = GlanceModifier.defaultWeight()
            )
        }
    }
}

@Composable
private fun InboxIcon(
    count: Int,
    modifier: GlanceModifier = GlanceModifier
) {
    Row(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(ImageProvider(R.drawable.ic_inbox), "")
        Text("$count")
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun TopRow(
    state: State<WidgetState>,
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current
    Row(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        InboxIcon(
            state.value.inboxCount,
            GlanceModifier
                .defaultWeight()
                .clickable(
                    actionStartActivity<MainActivity>(
                        actionParametersOf(ActionParameters.Key<Boolean>("inbox") to true)
                    )
                )
        )
        Text(
            text = context.getString(R.string.app_name),
            style = TextStyle(
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        )
        Image(
            provider = ImageProvider(R.drawable.ic_create),
            contentDescription = "",
            modifier = GlanceModifier
                .defaultWeight()
                .clickable(actionStartActivity<TransparentActivity>())
        )
    }
}

class AppWidgetReceiver : GlanceAppWidgetReceiver() {
    private val repository: DomainRepository = GlobalContext.get().get()
    private val state = mutableStateOf(loadData())

    override val glanceAppWidget: GlanceAppWidget = AppWidgetGlance(state)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        state.value = loadData()
    }

    private fun loadData() =
        runBlocking {
            val tasks = repository.getTasks(Task.Type.ToDo::class).first()
            WidgetState(
                inboxCount = repository.getTasks(Task.Type.Inbox::class).first().size,
                urgentImportantCount = tasks.count { it.markers.isUrgent && it.markers.isImportant },
                urgentCount = tasks.count { it.markers.isUrgent },
                importantCount = tasks.count { it.markers.isImportant },
                noFlagsCount = tasks.count { !it.markers.isImportant && !it.markers.isUrgent }
            )

        }
}

data class WidgetState(
    val inboxCount: Int,
    val urgentImportantCount: Int,
    val urgentCount: Int,
    val importantCount: Int,
    val noFlagsCount: Int,
)
