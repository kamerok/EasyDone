package easydone.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
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
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import easydone.coreui.design.important
import easydone.coreui.design.urgent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import easydone.core.strings.R as stringsR

private val SMALL_BOX = DpSize(48.dp, 48.dp)
private val ROW = DpSize(200.dp, 48.dp)
private val BIG_BOX = DpSize(200.dp, 215.dp)

private val mainActivityComponent = ComponentName(
    "com.kamer.easydone",
    "com.kamer.builder.MainActivity"
)
private val transparentActivityComponent = ComponentName(
    "com.kamer.easydone",
    "com.kamer.builder.TransparentActivity"
)

class AppWidgetGlance(private val state: State<WidgetState>) : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SMALL_BOX, ROW, BIG_BOX)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            when (LocalSize.current) {
                SMALL_BOX -> SmallWidget(state.value)
                ROW -> RowWidget(state.value)
                BIG_BOX -> BigWidget(state.value)
            }
        }
    }
}

@Composable
private fun SmallWidget(state: WidgetState) {
    val context = LocalContext.current
    val colorBackground by remember {
        derivedStateOf {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
            Color(typedValue.data)
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = GlanceModifier
            .size(SMALL_BOX.width, SMALL_BOX.height)
            .background(colorBackground)
            .appWidgetBackground()
            .clickable(actionStartActivity(mainActivityComponent))
    ) {
        InboxIcon(count = state.inboxCount)
    }
}

@Composable
private fun RowWidget(state: WidgetState) {
    val context = LocalContext.current
    val colorBackground by remember {
        derivedStateOf {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
            Color(typedValue.data)
        }
    }
    TopRow(
        state = state,
        modifier = GlanceModifier
            .size(ROW.width, ROW.height)
            .background(colorBackground)
            .appWidgetBackground()
            .clickable(actionStartActivity(mainActivityComponent))
    )
}

@Composable
private fun BigWidget(state: WidgetState) {
    val context = LocalContext.current
    val colorBackground by remember {
        derivedStateOf {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
            Color(typedValue.data)
        }
    }
    Column(
        modifier = GlanceModifier
            .size(BIG_BOX.width, BIG_BOX.height)
            .background(colorBackground)
            .appWidgetBackground()
            .clickable(actionStartActivity(mainActivityComponent))
    ) {
        val topRowHeight = 48.dp
        TopRow(
            state,
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(topRowHeight)
        )
        Box {
            val bitmap = remember {
                Bitmap.createBitmap(
                    BIG_BOX.width.toPx.toInt(),
                    (BIG_BOX.height - topRowHeight).toPx.toInt(),
                    Bitmap.Config.ARGB_8888
                ).also {
                    Canvas(it).apply {
                        val padding = 16.dp.toPx
                        val lineWidth = 2.dp.toPx
                        val guidelineTextMargin = 4.dp.toPx
                        val arrowheadLength = 10.dp.toPx
                        val arrowheadWidth = 8.dp.toPx
                        val boxSize = 48.dp.toPx
                        val boxCorners = 8.dp.toPx
                        val guidelinePaint = Color.Gray.copy(alpha = 0.6f).toArgb()
                        val linePaint = Paint().apply {
                            color = guidelinePaint
                            strokeWidth = lineWidth
                        }
                        val guidelineTextPaint = Paint().apply {
                            color = guidelinePaint
                            textSize = 35f
                            textAlign = Paint.Align.CENTER
                        }
                        val numberTextPaint = Paint().apply {
                            color = Color.DarkGray.copy(alpha = 0.7f).toArgb()
                            textSize = 90f
                            textAlign = Paint.Align.CENTER
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        }
                        val boxPaintDefault = Paint().apply {
                            color = Color.LightGray.copy(alpha = 0.7f).toArgb()
                        }
                        val boxPaintMediumPriority = Paint().apply {
                            color = urgent.copy(alpha = 0.7f).toArgb()
                        }
                        val boxPaintHighPriority = Paint().apply {
                            color = important.copy(alpha = 0.7f).toArgb()
                        }

                        val boxHeight =
                            height.toFloat() - padding * 2 - guidelineTextPaint.textSize - guidelineTextMargin
                        val startOffset =
                            padding + guidelineTextPaint.textSize + guidelineTextMargin

                        val contentBox = RectF(
                            startOffset,
                            padding,
                            width - padding - guidelineTextPaint.textSize,
                            padding + boxHeight
                        )

                        run {
                            val text = "URGENCY"
                            save()
                            translate(contentBox.left, contentBox.bottom)
                            drawLine(0f, -contentBox.height() + arrowheadLength, 0f, 0f, linePaint)
                            rotate(-90f, 0f, 0f)
                            val arrowhead = Path().apply {
                                moveTo(contentBox.height(), 0f)
                                lineTo(contentBox.height() - arrowheadLength, -arrowheadWidth / 2)
                                lineTo(contentBox.height() - arrowheadLength, arrowheadWidth / 2)
                                lineTo(contentBox.height(), 0f)
                            }
                            drawPath(arrowhead, linePaint)
                            drawText(
                                text,
                                contentBox.height() / 2f,
                                -guidelineTextMargin,
                                guidelineTextPaint
                            )
                            restore()
                        }

                        run {
                            val text = "IMPORTANCE"
                            val textBounds = Rect().apply {
                                guidelineTextPaint.getTextBounds(text, 0, text.length, this)
                            }
                            save()
                            translate(contentBox.left, contentBox.bottom)
                            drawLine(0f, 0f, contentBox.width() - arrowheadLength, 0f, linePaint)
                            val arrowhead = Path().apply {
                                moveTo(contentBox.width(), 0f)
                                lineTo(contentBox.width() - arrowheadLength, -arrowheadWidth / 2)
                                lineTo(contentBox.width() - arrowheadLength, arrowheadWidth / 2)
                                lineTo(contentBox.width(), 0f)
                            }
                            drawPath(arrowhead, linePaint)
                            drawText(
                                text,
                                contentBox.width() / 2f,
                                guidelineTextMargin + textBounds.height(),
                                guidelineTextPaint
                            )
                            restore()
                        }

                        val distanceFromCenter = 4.dp.toPx + boxSize / 2
                        val innerBox = RectF(
                            contentBox.centerX() - distanceFromCenter,
                            contentBox.centerY() - distanceFromCenter,
                            contentBox.centerX() + distanceFromCenter,
                            contentBox.centerY() + distanceFromCenter
                        )

                        fun drawNumber(number: Int, x: Float, y: Float, boxPaint: Paint) {
                            val text = "$number"
                            val textBounds = Rect().apply {
                                numberTextPaint.getTextBounds(text, 0, text.length, this)
                            }
                            drawRoundRect(
                                x - boxSize / 2,
                                y - boxSize / 2,
                                x + boxSize / 2,
                                y + boxSize / 2,
                                boxCorners,
                                boxCorners,
                                boxPaint
                            )
                            drawText(
                                text,
                                x,
                                y + textBounds.height() / 2,
                                numberTextPaint
                            )
                        }

                        drawNumber(
                            number = state.urgentCount,
                            x = innerBox.left,
                            y = innerBox.top,
                            boxPaint = if (state.urgentCount > 0) {
                                boxPaintMediumPriority
                            } else {
                                boxPaintDefault
                            }
                        )
                        drawNumber(
                            number = state.importantCount,
                            x = innerBox.right,
                            y = innerBox.bottom,
                            boxPaint = if (state.importantCount > 0) {
                                boxPaintMediumPriority
                            } else {
                                boxPaintDefault
                            }
                        )
                        drawNumber(
                            number = state.noFlagsCount,
                            x = innerBox.left,
                            y = innerBox.bottom,
                            boxPaint = boxPaintDefault
                        )
                        drawNumber(
                            number = state.urgentImportantCount,
                            x = innerBox.right,
                            y = innerBox.top,
                            boxPaint = if (state.urgentImportantCount > 0) {
                                boxPaintHighPriority
                            } else {
                                boxPaintDefault
                            }
                        )
                    }
                }
            }
            Image(provider = ImageProvider(bitmap), contentDescription = null)
        }
    }
}

private val Dp.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.value,
        Resources.getSystem().displayMetrics
    )

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
        val isInboxFilled = count > 0
        Image(
            ImageProvider(if (isInboxFilled) R.drawable.ic_inbox_filled else R.drawable.ic_inbox),
            ""
        )
        if (isInboxFilled) {
            Text("$count")
        }
    }
}

@Composable
private fun TopRow(
    state: WidgetState,
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current
    Row(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        InboxIcon(
            state.inboxCount,
            GlanceModifier
                .defaultWeight()
                .fillMaxHeight()
                .clickable(
                    actionStartActivity(
                        mainActivityComponent,
                        actionParametersOf(ActionParameters.Key<Boolean>("inbox") to true)
                    )
                )
        )
        Text(
            text = context.getString(stringsR.string.app_name),
            style = TextStyle(
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxHeight()
                .clickable(actionStartActivity(transparentActivityComponent))
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_create),
                contentDescription = "",
                modifier = GlanceModifier.size(24.dp)
            )
        }
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
                urgentCount = tasks.count { it.markers.isUrgent && !it.markers.isImportant },
                importantCount = tasks.count { it.markers.isImportant && !it.markers.isUrgent },
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
