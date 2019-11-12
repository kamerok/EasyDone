package easydone.feature.feed

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.fragment.app.Fragment
import com.kamer.builder.DeepLinkNavigator
import com.kamer.builder.Feature
import com.kamer.builder.FeatureRegistry
import com.kamer.builder.Features
import com.kamer.builder.NavigationCommand
import org.koin.core.context.GlobalContext


class ServiceProvider : ContentProvider() {

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun onCreate(): Boolean {
        Features.registries[Feature.FEED] = object : FeatureRegistry {
            override val featureClass: Class<out Fragment> = FeedFragment::class.java

            override fun create(): Fragment =
                FeedFragment(
                    GlobalContext.get().koin.get(),
                    object : FeedNavigator {
                        override fun navigateToTask(id: String) {
                            GlobalContext.get().koin.get<DeepLinkNavigator>()
                                .execute(NavigationCommand.EditTask(id))
                        }
                    }
                )
        }
        return true
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}
