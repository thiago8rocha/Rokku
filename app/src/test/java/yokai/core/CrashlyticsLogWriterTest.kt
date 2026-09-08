package yokai.core

import eu.kanade.tachiyomi.data.backup.create.BackupCreateException
import eu.kanade.tachiyomi.network.HttpException
import eu.kanade.tachiyomi.source.SourceNotFoundException
import eu.kanade.tachiyomi.ui.source.browse.NoResultsException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ProtocolException
import java.net.SocketTimeoutException

class CrashlyticsLogWriterTest {

    private val writer = CrashlyticsLogWriter()

    private fun ignored(t: Throwable) = with(writer) { t.isIgnoredForCrashlytics() }

    @Test
    fun `cover fetcher HTTP status wrapped as a plain IOException is ignored`() {
        ignored(IOException("HTTP 404 Not Found (url=https://example.org/cover.jpg)")) shouldBe true
        ignored(IOException("HTTP 503  (url=https://example.org/cover.jpg)")) shouldBe true
    }

    @Test
    fun `transient network conditions surfaced as IOException messages are ignored`() {
        ignored(IOException("stream was reset: PROTOCOL_ERROR")) shouldBe true
        ignored(IOException("stream was reset: CANCEL")) shouldBe true
        ignored(IOException("unexpected end of stream on https://example.org/...")) shouldBe true
        ignored(ProtocolException("Too many follow-up requests: 21")) shouldBe true
        ignored(IOException("Failed to bypass Cloudflare")) shouldBe true
        ignored(IOException("Chapter locked")) shouldBe true
    }

    @Test
    fun `missing or invalid cover metadata is ignored`() {
        ignored(IllegalStateException("Invalid image")) shouldBe true
        ignored(IllegalStateException("No cover specified")) shouldBe true
    }

    @Test
    fun `an evicted online-reader disk cache entry is ignored`() {
        val path = "/data/user/0/app.rokku/cache/chapter_disk_cache/9e90b9fb.0"
        ignored(FileNotFoundException("$path: open failed: ENOENT (No such file or directory)")) shouldBe true
    }

    @Test
    fun `expected source and browse outcomes are ignored by type`() {
        ignored(NoResultsException()) shouldBe true
        ignored(SourceNotFoundException("Source not installed", 123L)) shouldBe true
        ignored(BackupCreateException("Backup location is no longer accessible")) shouldBe true
    }

    @Test
    fun `HttpException is ignored only for auth, server, and not-found codes`() {
        ignored(HttpException(404)) shouldBe true
        ignored(HttpException(403)) shouldBe true
        ignored(HttpException(500)) shouldBe true
        ignored(HttpException(418)) shouldBe false
    }

    @Test
    fun `call timeouts and cancellations are ignored`() {
        ignored(SocketTimeoutException("timeout")) shouldBe true
        ignored(InterruptedIOException("timeout")) shouldBe true
        ignored(IOException("Canceled")) shouldBe true
    }

    @Test
    fun `source, cover file and local library conditions are ignored`() {
        ignored(IOException("Can't open InputStream")) shouldBe true
        ignored(Exception("Timed out waiting for WebView after 30s")) shouldBe true
        ignored(Exception("The Amazing Spider-Man (1962 - 1995) is not a valid directory")) shouldBe true
        ignored(Exception("Unrecognized archive format")) shouldBe true
    }

    @Test
    fun `the whole cause chain is walked`() {
        val wrapped = IOException("failed to load cover", IOException("stream was reset: INTERNAL_ERROR"))
        ignored(wrapped) shouldBe true
    }

    @Test
    fun `real bugs are still reported`() {
        ignored(IOException("write failed: ENOSPC (No space left on device)")) shouldBe false
        ignored(IllegalStateException("ViewModel was cleared")) shouldBe false
        ignored(NullPointerException("Attempt to read from null array")) shouldBe false
        ignored(IndexOutOfBoundsException("Index -1 out of bounds for length 17")) shouldBe false
    }
}
