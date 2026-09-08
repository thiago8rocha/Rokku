package eu.kanade.tachiyomi.source

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.SMangaUpdate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import rx.Observable

/**
 * A basic interface for creating a source. It could be an online source, a local source, etc.
 */
interface Source {

    /**
     * ID for the source. Must be unique.
     */
    val id: Long

    /**
     * Name of the source.
     */
    val name: String

    val lang: String
        get() = ""

    /**
     * Whether the source has support for latest updates.
     */
    val supportsLatest: Boolean

    /**
     * Returns the list of filters for the source.
     */
    fun getFilterList(): FilterList = FilterList()

    /**
     * Get a page with a list of manga.
     *
     * @since tachiyomix 1.6
     * @param page the page number to retrieve.
     */
    suspend fun getPopularManga(page: Int): MangasPage

    /**
     * Get a page with a list of latest manga updates.
     *
     * @since tachiyomix 1.6
     * @param page the page number to retrieve.
     */
    suspend fun getLatestUpdates(page: Int): MangasPage

    /**
     * Get a page with a list of manga.
     *
     * @since tachiyomix 1.6
     * @param page the page number to retrieve.
     * @param query the search query.
     * @param filters the list of filters to apply.
     */
    suspend fun getSearchManga(page: Int, query: String, filters: FilterList): MangasPage

    /**
     * Fetches updated information for a manga.
     *
     * Depending on the provided flags or source availability, this may include
     * updated manga metadata, available chapters, or both.
     *
     * If a value is not requested, the existing provided value can be returned as-is.
     * The host app may apply any returned updates regardless of the flags,
     * so care should be taken to only return accurate and intentional changes.
     *
     * @since tachiyomix 1.6
     * @param manga The manga to fetch updates for.
     * @param chapters Existing chapters of the manga
     * @param fetchDetails Whether to fetch updated manga details.
     * @param fetchChapters Whether to fetch available chapters.
     */
    suspend fun getMangaUpdate(
        manga: SManga,
        chapters: List<SChapter>,
        fetchDetails: Boolean,
        fetchChapters: Boolean,
    ): SMangaUpdate

    /**
     * Get the list of pages a chapter has. Pages should be returned
     * in the expected order; the index is ignored.
     *
     * @since tachiyomix 1.6
     * @param chapter the chapter.
     * @return the pages for the chapter.
     */
    suspend fun getPageList(chapter: SChapter): List<Page>

    /**
     * Whether this source provides its own related manga lookup (via [fetchRelatedMangaList])
     * rather than relying on the default title-keyword search fallback.
     *
     * @default false
     * @since Rokku 1.1.7
     */
    val supportsRelatedMangas: Boolean get() = false

    /**
     * Opt out of the default title-keyword search fallback for related manga, e.g. if it
     * tends to produce noisy/irrelevant results for this source.
     *
     * @default false
     * @since Rokku 1.1.7
     */
    val disableRelatedMangasBySearch: Boolean get() = false

    /**
     * Opt out of related manga lookups entirely for this source.
     *
     * @default false
     * @since Rokku 1.1.7
     */
    val disableRelatedMangas: Boolean get() = false

    /**
     * Get all the available related manga for a manga. Normally not needed to override -
     * override [fetchRelatedMangaList] and set [supportsRelatedMangas] instead.
     *
     * @since Rokku 1.1.7
     * @param manga the manga to get related manga for.
     * @param pushResults called with each batch of results as they come in; keyed by the
     * keyword/label they came from, with a completion flag.
     */
    suspend fun getRelatedMangaList(
        manga: SManga,
        exceptionHandler: (Throwable) -> Unit,
        pushResults: suspend (relatedManga: Pair<String, List<SManga>>, completed: Boolean) -> Unit,
    ) {
        val handler = CoroutineExceptionHandler { _, e -> exceptionHandler(e) }
        if (!disableRelatedMangas) {
            supervisorScope {
                if (supportsRelatedMangas) {
                    launch(handler) {
                        getRelatedMangaListByExtension(manga, exceptionHandler, pushResults)
                    }
                }
                if (!disableRelatedMangasBySearch) {
                    launch(handler) {
                        getRelatedMangaListBySearch(manga, exceptionHandler, pushResults)
                    }
                }
            }
        }
    }

    /**
     * Get related manga provided by the source itself (see [fetchRelatedMangaList]).
     *
     * @since Rokku 1.1.7
     */
    suspend fun getRelatedMangaListByExtension(
        manga: SManga,
        exceptionHandler: (Throwable) -> Unit = {},
        pushResults: suspend (relatedManga: Pair<String, List<SManga>>, completed: Boolean) -> Unit,
    ) {
        runCatching { fetchRelatedMangaList(manga) }
            .onSuccess { if (it.isNotEmpty()) pushResults(Pair("", it), false) }
            .onFailure { exceptionHandler(it) }
    }

    /**
     * Fetch related manga for a manga directly from the source/site. Only called if
     * [supportsRelatedMangas] is overridden to return true.
     *
     * @since Rokku 1.1.7
     * @throws UnsupportedOperationException if a source doesn't support related manga.
     */
    suspend fun fetchRelatedMangaList(manga: SManga): List<SManga> =
        throw UnsupportedOperationException("Unsupported!")

    /**
     * Split & strip a manga title into separate searchable keywords, used to look up related
     * manga via [getRelatedMangaListBySearch].
     *
     * @since Rokku 1.1.7
     */
    private fun String.stripKeywordForRelatedMangas(): List<String> {
        val regexWhitespace = Regex("\\s+")
        val regexSpecialCharacters =
            Regex("([!~#$%^&*+_|/\\\\,?:;'“”‘’\"<>(){}\\[\\]。・～：—！？、―«»《》〘〙【】「」｜]|\\s-|-\\s|\\s\\.|\\.\\s)")
        val regexNumberOnly = Regex("^\\d+$")

        return replace(regexSpecialCharacters, " ")
            .split(regexWhitespace)
            .map { it.replace(regexNumberOnly, "").lowercase() }
            // Many sources reject searches shorter than 3 characters outright
            .filter { it.length > 2 }
    }

    /**
     * Get related manga by searching for each keyword extracted from the manga's title.
     * Works with any source, since it only relies on [getSearchManga].
     *
     * @since Rokku 1.1.7
     */
    suspend fun getRelatedMangaListBySearch(
        manga: SManga,
        exceptionHandler: (Throwable) -> Unit = {},
        pushResults: suspend (relatedManga: Pair<String, List<SManga>>, completed: Boolean) -> Unit,
    ) {
        val keywords = LinkedHashSet<String>()
        keywords.add(manga.title)
        manga.title.stripKeywordForRelatedMangas()
            .filterNot { word -> keywords.any { it.equals(word, ignoreCase = true) } }
            .forEach { keywords.add(it) }
        if (keywords.isEmpty()) return

        coroutineScope {
            val filterList = getFilterList()
            keywords.map { keyword ->
                launch {
                    runCatching { getSearchManga(1, keyword, filterList).mangas }
                        .onSuccess { if (it.isNotEmpty()) pushResults(Pair(keyword, it), false) }
                        .onFailure { exceptionHandler(it) }
                }
            }
        }
    }

    @Deprecated("Use the combined suspend API instead", ReplaceWith("getMangaUpdate"))
    fun fetchMangaDetails(manga: SManga): Observable<SManga> =
        throw IllegalStateException("Not used")

    @Deprecated("Use the combined suspend API instead", ReplaceWith("getMangaUpdate"))
    fun fetchChapterList(manga: SManga): Observable<List<SChapter>> =
        throw IllegalStateException("Not used")

    @Deprecated("Use the non-RxJava API instead", ReplaceWith("getPageList"))
    fun fetchPageList(chapter: SChapter): Observable<List<Page>> =
        throw IllegalStateException("Not used")
}

fun Source.preferenceKey(): String = "source_$id"
