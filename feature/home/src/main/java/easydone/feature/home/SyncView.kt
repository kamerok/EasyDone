package easydone.feature.home

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.kamer.home.R
import kotlinx.android.synthetic.main.view_sync.view.*


class SyncView(context: Context) : FrameLayout(context) {

    var listener: (() -> Unit)? = null
        set(value) {
            field = value
            setOnClickListener { value?.invoke() }
        }
    var isSyncing: Boolean = false
        set(value) {
            field = value
            updateSyncingState()
        }
    var hasChanges: Boolean = false
        set(value) {
            field = value
            updateChangesIndicator()
        }

    private val rotateAnimator: Animator = ValueAnimator.ofFloat(180f, 0f).apply {
        duration = 500
        interpolator = LinearInterpolator()
        addUpdateListener { iconView.rotation = it.animatedValue as Float }
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                if (isSyncing) animation.start()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationStart(animation: Animator) {}
        })
    }
    private var changesAnimator: Animator? = null

    init {
        View.inflate(context, R.layout.view_sync, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateSyncingState()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        rotateAnimator.cancel()
    }

    private fun updateSyncingState() {
        if (isSyncing) {
            rotateAnimator.run {
                if (!isRunning) {
                    start()
                }
            }
        }
    }

    private fun updateChangesIndicator() {
        changesAnimator?.removeAllListeners()
        changesAnimator?.cancel()
        changesAnimator = null
        changesAnimator = ValueAnimator.ofFloat(badgeView.scaleX, if (hasChanges) 1f else 0f)
            .apply {
                val currentFraction = if (hasChanges) {
                    badgeView.scaleX
                } else {
                    1 - badgeView.scaleX
                }
                duration = 200
                currentPlayTime = (200 * currentFraction).toLong()
                addUpdateListener {
                    val scale = it.animatedValue as Float
                    badgeView.scaleX = scale
                    badgeView.scaleY = scale
                }
                start()
            }
    }

}
