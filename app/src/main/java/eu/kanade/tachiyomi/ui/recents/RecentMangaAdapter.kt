package eu.kanade.tachiyomi.ui.recents

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.items.IFlexible
import eu.kanade.tachiyomi.core.preference.Preference
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.ChapterHistory
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.changesIn
import eu.kanade.tachiyomi.ui.manga.chapter.BaseChapterAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uy.kohesive.injekt.injectLazy
import yokai.domain.recents.RecentsPreferences
import yokai.domain.ui.UiPreferences
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class RecentMangaAdapter(val delegate: RecentsInterface) :
    BaseChapterAdapter<IFlexible<*>>(delegate) {

    val preferences: PreferencesHelper by injectLazy()
    val uiPreferences: UiPreferences by injectLazy()
    val recentsPreferences: RecentsPreferences by injectLazy()

    var showDownloads = recentsPreferences.showRecentsDownloads().get()
    var showRemoveHistory = recentsPreferences.showRecentsRemHistory().get()
    var showTitleFirst = recentsPreferences.showTitleFirstInRecents().get()
    var showUpdatedTime = preferences.showUpdatedTime().get()
    var uniformCovers = uiPreferences.uniformGrid().get()
    var showOutline = uiPreferences.outlineOnCovers().get()
    var sortByFetched = preferences.sortFetchedTime().get()
    var lastUpdatedTime = preferences.libraryUpdateLastTimestamp().get()
    private var collapseGroupedUpdates = preferences.collapseGroupedUpdates().get()
    private var collapseGroupedHistory = preferences.collapseGroupedHistory().get()
    val collapseGrouped: Boolean
        get() = if (viewType.isHistory) {
            collapseGroupedHistory
        } else {
            collapseGroupedUpdates
        }

    val viewType: RecentsViewType
        get() = delegate.getViewType()

    val decimalFormat = DecimalFormat(
        "#.###",
        DecimalFormatSymbols()
            .apply { decimalSeparator = '.' },
    )
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        setDisplayHeadersAtStartUp(true)
    }

    /**
     * Give headers, footers and chapter rows disjoint stable-id ranges. FlexibleAdapter's
     * default stable id is the item's hashCode(); RecentMangaHeaderItem and the footer hash
     * to small ints (0..2) that collide with chapter rows whose id hashes into the same
     * range, crashing RecyclerView with "Two different ViewHolders have the same stable ID".
     */
    override fun getItemId(position: Int): Long {
        return when (val item = getItem(position)) {
            is RecentMangaHeaderItem -> HEADER_ID_BASE - item.recentsType
            is RecentMangaItem ->
                if (item.mch.manga.id == null) {
                    FOOTER_ID_BASE - ((item.header as? RecentMangaHeaderItem)?.recentsType ?: 0)
                } else {
                    item.chapter.id ?: RecyclerView.NO_ID
                }
            else -> RecyclerView.NO_ID
        }
    }

    fun setPreferenceFlows() {
        recentsPreferences.showRecentsDownloads().register { showDownloads = it }
        recentsPreferences.showRecentsRemHistory().register { showRemoveHistory = it }
        recentsPreferences.showTitleFirstInRecents().register { showTitleFirst = it }
        preferences.showUpdatedTime().register { showUpdatedTime = it }
        uiPreferences.uniformGrid().register { uniformCovers = it }
        preferences.collapseGroupedUpdates().register { collapseGroupedUpdates = it }
        preferences.collapseGroupedHistory().register { collapseGroupedHistory = it }
        preferences.sortFetchedTime().changesIn(delegate.scope()) { sortByFetched = it }
        uiPreferences.outlineOnCovers().register(false) {
            showOutline = it
            (0 until itemCount).forEach { i ->
                (recyclerView.findViewHolderForAdapterPosition(i) as? RecentMangaHolder)?.updateCards()
            }
        }
        preferences.libraryUpdateLastTimestamp().changesIn(delegate.scope()) {
            lastUpdatedTime = it
            if (viewType.isUpdates) {
                notifyItemChanged(0)
            }
        }
    }

    fun getItemByChapterId(id: Long): RecentMangaItem? {
        return currentItems.find {
            val item = (it as? RecentMangaItem) ?: return@find false
            return@find id == item.chapter.id || id in item.mch.extraChapters.map { ch -> ch.id }
        } as? RecentMangaItem
    }

    private fun <T> Preference<T>.register(notify: Boolean = true, onChanged: (T) -> Unit) {
        changes()
            .drop(1)
            .onEach {
                onChanged(it)
                if (notify) {
                    notifyDataSetChanged()
                }
            }
            .launchIn(delegate.scope())
    }

    interface RecentsInterface : GroupedDownloadInterface {
        fun onCoverClick(position: Int)
        fun onRemoveHistoryClicked(position: Int)
        fun onSubChapterClicked(position: Int, chapter: Chapter, view: View)
        fun updateExpandedExtraChapters(position: Int, expanded: Boolean)
        fun areExtraChaptersExpanded(position: Int): Boolean
        fun markAsRead(position: Int)
        fun alwaysExpanded(): Boolean
        fun scope(): CoroutineScope
        fun getViewType(): RecentsViewType
        fun onItemLongClick(position: Int, chapter: ChapterHistory): Boolean
    }

    override fun onItemSwiped(position: Int, direction: Int) {
        super.onItemSwiped(position, direction)
        when (direction) {
            ItemTouchHelper.LEFT -> delegate.markAsRead(position)
            ItemTouchHelper.RIGHT -> delegate.markAsRead(position)
        }
    }

    enum class ShowRecentsDLs {
        None,
        OnlyUnread,
        OnlyDownloaded,
        UnreadOrDownloaded,
        All,
    }

    private companion object {
        // Chapter rows use their (always positive) chapter id; keep headers/footers well
        // clear of that range and of RecyclerView.NO_ID (-1).
        const val HEADER_ID_BASE = -1000L
        const val FOOTER_ID_BASE = -2000L
    }
}
