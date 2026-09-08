package yokai.domain.extension.repo.service

import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.awaitSuccess
import eu.kanade.tachiyomi.network.parseAs
import eu.kanade.tachiyomi.util.system.withIOContext
import okhttp3.OkHttpClient
import yokai.domain.extension.repo.model.ExtensionRepo

class ExtensionRepoService(
    private val client: OkHttpClient,
) {

    suspend fun fetchRepoDetails(
        repo: String,
    ): ExtensionRepo? {
        return fetchRepoMeta(repo)?.toExtensionRepo(baseUrl = repo)
    }

    /**
     * Returns the `index_v2` URL advertised by the repo's `repo.json`, if any. Repos that have
     * migrated to the newer ExtensionStore index (protobuf or JSON object) point to it from here;
     * repos that haven't return null, in which case the legacy `index.min.json` should be used.
     */
    suspend fun fetchIndexV2Url(repo: String): String? {
        return fetchRepoMeta(repo)?.indexV2
    }

    private suspend fun fetchRepoMeta(repo: String): ExtensionRepoMetaDto? {
        return withIOContext {
            val url = "$repo/repo.json".toUri()

            try {
                client.newCall(GET(url.toString()))
                    .awaitSuccess()
                    .parseAs<ExtensionRepoMetaDto>()
            } catch (e: Exception) {
                // Handled: caller falls back to the legacy index. A dead/misconfigured repo
                // (404, HTML instead of JSON, timeout) is not a Rokku bug - log, don't report.
                Logger.w(e) { "Failed to fetch repo details" }
                null
            }
        }
    }
}
