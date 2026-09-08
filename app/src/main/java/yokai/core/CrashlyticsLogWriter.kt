package yokai.core

import android.database.sqlite.SQLiteConstraintException
import co.touchlab.kermit.DefaultFormatter
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Message
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag
import com.google.firebase.crashlytics.FirebaseCrashlytics
import eu.kanade.tachiyomi.data.backup.create.BackupCreateException
import eu.kanade.tachiyomi.data.download.InvalidDownloadLocationException
import eu.kanade.tachiyomi.data.download.NoPagesException
import eu.kanade.tachiyomi.data.track.myanimelist.MALTokenExpired
import eu.kanade.tachiyomi.network.HttpException
import eu.kanade.tachiyomi.network.isAuthError
import eu.kanade.tachiyomi.network.isServerError
import eu.kanade.tachiyomi.source.SourceNotFoundException
import eu.kanade.tachiyomi.ui.reader.loader.MissingDownloadedPageException
import eu.kanade.tachiyomi.ui.reader.loader.SourceNotInstalledException
import eu.kanade.tachiyomi.ui.source.browse.NoResultsException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import org.jsoup.HttpStatusException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class CrashlyticsLogWriter : LogWriter() {
    override fun isLoggable(tag: String, severity: Severity): Boolean = severity >= Severity.Info

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        try {
            FirebaseCrashlytics.getInstance().log(DefaultFormatter.formatMessage(severity, Tag(tag), Message(message)))
            if (throwable != null && severity >= Severity.Error && !throwable.isIgnoredForCrashlytics()) {
                FirebaseCrashlytics.getInstance().recordException(throwable)
            }
        } catch (_: Exception) {
            // Probably crashlytics not yet initialized or disabled
        }
    }

    /**
     * Skips exceptions that are never actionable from app code: coroutine cancellation
     * (normal control flow), pure device/network connectivity failures (DNS, timeout,
     * TLS handshake, connection reset), a source's own server erroring out (5xx, whether
     * raised via our own HttpException or a source/extension's own HTTP client),
     * rejecting auth (401/403), or answering 404 for a resource that isn't there (a
     * dead or misconfigured extension repo URL, or a legacy repo with no repo.json -
     * ExtensionApi and ExtensionRepoService both fall back cleanly), and the user's
     * downloads folder, a downloaded page file, or the backup destination
     * (BackupCreateException) becoming inaccessible (permission revoked, file/folder
     * moved/deleted, storage removed - the backup failure is still shown to the user as a
     * notification) - none of these reflect a Rokku bug, and recording them buries real
     * non-fatals in noise. Walks the whole cause chain since some call sites (e.g.
     * MangaCoverFetcher) wrap these in a generic IOException before it reaches here.
     *
     * Also skips two more transient, self-healing cases: an extension's dex not being
     * readable yet (empty DexPathList) when ExtensionManager's startup scan races an
     * in-progress install/update - ExtensionInstallReceiver reloads that package on its
     * own once the real PACKAGE_ADDED/PACKAGE_REPLACED broadcast lands - and a downloaded
     * page file disappearing between the post-download success check and CBZ archiving
     * (external storage/media scanner interference), which Downloader already surfaces
     * to the user as a failed download.
     *
     * Also skips a MyAnimeList tracker session expiring (MyAnimeListInterceptor already
     * surfaces a clear re-login message to the user instead) and a source extension's own
     * auth interceptor rejecting a request as unauthorized (a 401 the extension raises as
     * a plain IOException rather than through our HttpException, so it doesn't reach the
     * isAuthError check above) - neither reflects a Rokku bug.
     *
     * Also skips Hikka/MangaBaka trying to JSON-decode their saved OAuth token preference
     * on first use when the user never logged in (empty preference, decoding fails as EOF) -
     * Hikka.loadOAuth and MangaBaka.restoreToken already catch this and return null, so the
     * tracker just starts logged out.
     *
     * Also skips AniList's tracker refresh not finding the manga in the user's list anymore
     * (e.g. removed/unlinked directly on AniList's site) - not a Rokku bug.
     *
     * Also skips the OS refusing to promote a library update job to a foreground service
     * (android.app.ForegroundServiceStartNotAllowedException, API 31+, matched by class name
     * since it doesn't extend IllegalStateException) - tryToSetForeground() already treats
     * this as best-effort and lets the update proceed without the progress notification.
     *
     * Also skips a history upsert hitting a FOREIGN KEY constraint failure - the chapter the
     * reader/recents/backup was recording progress for was deleted from the database in the
     * meantime (chapter list refreshed, manga removed). HistoryRepositoryImpl already catches
     * this and skips the row; there's simply no chapter left to attach the history to.
     *
     * Also skips a batch of cover/reader/browse conditions that only reflect a source or the
     * network misbehaving, never a Rokku bug: MangaCoverFetcher raising a source's HTTP error
     * status ("HTTP 4xx/5xx …"), a missing/invalid cover URL in the source's metadata
     * ("Invalid image", "No cover specified"), an HTTP/2 stream reset ("stream was reset: …",
     * including covers cancelled mid-scroll), a redirect loop ("Too many follow-up requests"),
     * a source's own Cloudflare challenge not clearing ("Failed to bypass Cloudflare"), a
     * source returning an empty page list (NoPagesException) or a locked chapter
     * ("Chapter locked"), a browse query with no matches (NoResultsException), the related-
     * manga / deep-link path resolving to an uninstalled source (SourceNotFoundException), and
     * an online reader page's disk-cache entry being evicted between listing and reading it
     * (FileNotFoundException under chapter_disk_cache - the page just reloads).
     *
     * Also skips: any InterruptedIOException (an okhttp call timeout or cancellation - covers
     * the former SocketTimeoutException too), an okhttp connection dropped mid-request
     * (ConnectionShutdownException, "Canceled"), a local cover file that can't be opened
     * ("Can't open InputStream" - moved/permission revoked), a Cloudflare/WebView challenge
     * that never resolved ("Timed out waiting for WebView"), and a local-library entry whose
     * folder is gone or holds a non-archive file ("… is not a valid directory",
     * "Unrecognized archive format").
     */
    internal fun Throwable.isIgnoredForCrashlytics(): Boolean {
        var current: Throwable? = this
        while (current != null) {
            when (current) {
                is CancellationException,
                is UnknownHostException,
                is InterruptedIOException, // also covers SocketTimeoutException (a call timeout or cancellation, never a bug)
                is ConnectException,
                is SocketException,
                is SSLException,
                is InvalidDownloadLocationException,
                is BackupCreateException,
                is MissingDownloadedPageException,
                is SourceNotInstalledException,
                is SourceNotFoundException,
                is MALTokenExpired,
                is NoResultsException,
                is NoPagesException,
                -> return true

                is HttpException -> if (current.isAuthError || current.isServerError || current.code == 404) return true

                is HttpStatusException -> if (current.statusCode in 500..599 || current.statusCode == 404) return true

                is SQLiteConstraintException -> if (
                    current.message?.contains("FOREIGN KEY constraint failed") == true
                ) {
                    return true
                }

                is FileNotFoundException -> if (current.message?.contains("chapter_disk_cache") == true) return true

                is IOException -> if (
                    current.message?.contains("SETTINGS preface") == true ||
                    current.message == "Unauthorized" ||
                    current.message == "Canceled" ||
                    current.message == "Chapter locked" ||
                    current.message == "Can't open InputStream" ||
                    current.message == "Failed to bypass Cloudflare" ||
                    current.message?.startsWith("stream was reset: ") == true ||
                    current.message?.startsWith("Too many follow-up requests") == true ||
                    current.message?.matches(HTTP_STATUS_MESSAGE) == true
                ) {
                    return true
                }

                is IllegalStateException -> if (
                    current.message == "Invalid image" ||
                    current.message == "No cover specified"
                ) {
                    return true
                }

                is SerializationException -> if (current.message?.contains("had 'EOF' instead") == true) return true

                is ClassNotFoundException -> if (current.message?.contains("DexPathList[[]") == true) return true

                is IllegalArgumentException -> if (
                    current.message?.contains("is child of") == true &&
                    current.message?.contains("FileNotFoundException") == true
                ) {
                    return true
                }
            }

            if (current.message == "Refresh Chapter List" || current.message == "Could not find manga") return true
            // A source's Cloudflare/WebView challenge not resolving in time, or a local-library
            // folder that was renamed/deleted or has a non-archive file in it - not a Rokku bug.
            if (current.message?.startsWith("Timed out waiting for WebView") == true) return true
            if (current.message?.endsWith("is not a valid directory") == true) return true
            if (current.message == "Unrecognized archive format") return true
            if (current.javaClass.simpleName == "ForegroundServiceStartNotAllowedException") return true
            if (current.javaClass.simpleName == "ConnectionShutdownException") return true

            current = current.cause?.takeIf { it !== current }
        }
        return false
    }

    private companion object {
        /** Matches the "HTTP 404 …" message MangaCoverFetcher raises as a plain IOException. */
        val HTTP_STATUS_MESSAGE = Regex("""HTTP \d{3}\b.*""")
    }
}
