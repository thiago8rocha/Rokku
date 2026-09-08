# Changelog

All notable changes to this project will be documented in this file.

The format is simplified version of [Keep a Changelog](https://keepachangelog.com/en/1.1.0/):
- `Additions` - New features
- `Changes` - Behaviour/visual changes
- `Fixes` - Bugfixes
- `Other` - Technical changes/updates

## [Unreleased]

### Changes
- An automatic backup that fails because its saved location is no longer accessible (folder deleted, permission revoked, storage removed) now shows a notification telling you to pick a new one, instead of failing silently

### Fixes
- Fixed a crash when updating all extensions with many updates pending (the work request's input data exceeded its size limit)
- Fixed a rare crash in Recents ("Two different ViewHolders have the same stable ID") caused by a section header's id colliding with a chapter row's
- Fixed the library update job silently failing (and spamming Crashlytics) when the OS refused to promote it to a foreground service
- Fixed a crash opening a chapter in the webtoon reader when its saved resume position was out of range
- Fixed a crash sharing a reader page when its cached image had already been evicted from disk
- Fixed a crash when saving reading history for a chapter that was removed from the library in the meantime (chapter list refreshed, manga removed)
- Fixed an extension install wrongly reporting a failure (and never installing) when Android's DownloadManager returned no content URI for a download that actually completed

### Other
- Reduced Crashlytics noise by no longer reporting a dead or misconfigured extension repo (HTTP 404 on its `repo.json` or index) as a non-fatal error
- Reduced Crashlytics noise by no longer reporting cover-loading, reader, browse, and backup failures that only reflect a source, the network, or the device misbehaving rather than a Rokku bug

## [1.7.1]

### Fixes
- Fixed laggy, stuttering scrolling and covers intermittently failing to appear in the library, updates, and history
- Fixed checked checkboxes rendering with an invisible checkmark on the Yin Yang theme
- Fixed extension loading blocking the main thread during startup, causing ANRs with many extensions installed
- Fixed the file logger blocking the app, including the UI, during slow disk I/O
- Fixed a crash opening a manga's chapter list when a "missing chapters" gap indicator was shown
- Fixed a tracker refresh/removal failure showing a raw "HTTP error 401" toast instead of an expired-session message
- Fixed the manga details screen still theming the follow/download buttons from the cover after disabling "Theme buttons based on cover"
- Fixed a download crashing and getting stuck instead of erroring out when its temporary chapter folder failed to be created
- Fixed the full cover viewer's buttons and image being misaligned with the display cutout/system bars on Android 15+ ([@Hiirbaf](https://github.com/Hiirbaf))

### Other
- Added support for Android 16's Live Updates: the library update progress notification can now be promoted to a status bar chip
- Reduced Crashlytics noise by no longer reporting non-fatal errors that reflect expected conditions rather than real bugs (network/server issues, expired sessions, self-healing races, and similar)

## [1.7.0]

### Additions
- Added a per-extension incognito toggle (Extension details) to pause history/tracking for just that extension's sources
- Added a "Cover theme style" option (Settings > Appearance) for the Material You palette used for cover-derived accent colors, including a "Legacy" choice for the pre-Material You look ([@Hiirbaf](https://github.com/Hiirbaf))
- Added a "Load suggestions automatically" option (Settings > Browse) to fetch related-manga suggestions on demand instead of automatically
- Added a "Show 'Duplicate' badge" option (Settings > Browse) to flag library entries already added from a different extension
- Added a button to open an available extension's website in a WebView before installing it

### Changes
- Related-manga suggestions are now cached instead of re-fetched every time they're shown
- Renamed "Security" settings to "Security and Privacy"
- The cover-derived accent color now defaults to the "Legacy" style (the pre-Material You look) instead of Material You ([@Hiirbaf](https://github.com/Hiirbaf))

### Fixes
- Fixed a possible race condition in download notifications updated from multiple threads at once
- Fixed download/error notifications collapsing under a generic icon instead of Rokku's when grouped in the shade
- Fixed a restored backup's theme not applying until the app was manually restarted
- Fixed a crash in the source filter spinner when its entries shrink past a stale selected position
- Fixed a possible crash from duplicate extension entries available from more than one repo
- Fixed extension installs hanging indefinitely when started offline or the network drops mid-download
- Fixed the manga description opening expanded when navigating from browse instead of staying collapsed like from the library
- Fixed "Check for updates" failing on nightly builds when the beta-releases preference was off
- Fixed the "Missing N chapters" summary and divider not counting chapters missing before the earliest available one
- Fixed scanlator filtering causing a full library scan on every chapter list/backup/recents query
- Fixed the library still flickering to the wrong covers while scrolling during an active backup restore
- Fixed manga covers never loading, then flickering to the wrong one, in the migration source list
- Fixed a crash when updating a single library category whose queued manga list came back empty
- Fixed a settings crash when a stored preference value had been overwritten with an incompatible type
- Fixed a rare crash when switching chapters while a local archive page was still loading
- Fixed a recycled source filter spinner briefly showing the wrong item, left over from its previous binding
- Fixed an empty notification staying in the shade after chapter downloads finished
- Fixed the library update skipped notification's "open log" action opening the help page instead
- Fixed a crash opening a source's browse page (e.g. a shortcut) when its extension was no longer installed
- Fixed adjacent chapters not preloading when the current chapter has only a single page
- Fixed the library grid/list losing its row count and collapsing to a single column after toggling uniform grid
- Fixed library swipe gestures silently breaking when a view they check against wasn't laid out yet
- Fixed the manga details cover and its blurred backdrop loading independently, showing different covers momentarily
- Fixed the manga details palette request caching an oversized cover bitmap under the library grid's cache key
- Fixed reading a chapter silently reverting AniList custom list membership to stale local data, including changes made directly on AniList (see #115)
- Fixed fetching an AniList entry's tracking info failing with a JSON parsing error when it belonged to no custom list
- Fixed a manga's tracking info never actually refreshing when reopening its details page

### Other
- Bumped Voyager to 2.2.21-1.10.3
- Moved cover ratio/color decoding to a background-priority thread to compete less with the UI thread

## [1.6.1]

### Fixes
- Fixed the "Missing N chapters" chapter-count summary staying hidden when a manga has decimal-numbered chapters
- Fixed manga covers flickering to the wrong cover, or reloading repeatedly, while scrolling the library or Recents
- Fixed covers in Recents disappearing when leaving the tab and coming back
- Fixed manga covers staying blank in Recents when opening the tab, until scrolling
- Fixed MangaBaka scores being recorded against the wrong scale when using a score step size other than 1 ([@Hiirbaf](https://github.com/Hiirbaf))

## [1.6.0]

### Additions
- Added a "Missing N chapters" separator in the chapter list, plus a summary below the chapter count (Settings > Library > Behavior)
- Added a "Select all" option to a manga's genre/tag chips, to search by every tag at once
- Added MangaBaka and Hikka tracker support, including OAuth login ([@Hiirbaf](https://github.com/Hiirbaf))
- Added private tracking: mark a tracked entry as private on services that support it (AniList, Bangumi, Kitsu, MangaBaka)
- Added a "Flash the screen on page turn" reader option, for e-ink displays
- Added a "Custom" option to Settings > Library > Global updates, to anchor updates to a specific time of day
- Added inline editing for extension repo URLs; long-press a repo to copy its URL
- Added a configurable free-space floor (Settings > Downloads) that pauses downloads instead of erroring when storage runs low
- Added a "With 'Cancelled' status" library update restriction (Settings > Library)
- Added AniList custom list support to the tracking sheet

### Fixes
- Fixed manga covers staying blank in list view (library and source browse) on release/nightly builds
- Fixed source browse covers sometimes staying blank in list view after scrolling
- Fixed MangaBaka and Hikka tracker sync IDs being swapped, breaking MangaBaka tracking on backup restore
- Fixed library updates re-downloading a chapter already marked read via "mark duplicate read chapters as read"
- Fixed started/finished reading dates never being set on trackers
- Fixed memory leaks on theme/night-mode/side-nav changes and app shortcut refreshes
- Fixed nested filter groups (e.g. Publisher/Genre) not rendering
- Fixed genre/publisher filters staying empty for sources that fetch filters in the background
- Fixed the filter sheet flickering when expanding a group with many sub-groups
- Fixed MyAnimeList erroring on list entries with partial start/finish reading dates
- Fixed the reader getting stuck loading indefinitely in some scenarios
- Fixed a manga's memo not being saved to or restored from backups
- Fixed free-space checks never triggering when the download directory is a SAF tree/document URI
- Fixed pasting a manga URL into Global Search failing for some sources
- Fixed a source's toggle in extension settings sometimes staying visually off after enabling its language
- Fixed extension icons in the browse list showing the wrong icon, staying blank, or stuck mid-fade ([@Hiirbaf](https://github.com/Hiirbaf))

### Other
- Synced with upstream (yokai): added proguard rules to keep `Serializable` `writeReplace`/`readResolve`, and bumped `okio` to 3.18.1

## [1.5.0]

### Additions
- Add configurable download concurrency (Settings > Downloads): number of simultaneous chapter downloads and simultaneous page downloads per chapter are no longer hardcoded
- Overhaul library search: support `&&`/`||` boolean operators, `-term` negation, `title:`/`author:`/`artist:`/`genre:` field prefixes (in addition to the existing `src:`), and numeric comparators (`>`, `<`, `=`) on `chapters`, `unread`, and `read`
- Pasting a manga URL into Global Search now opens that manga directly if the URL matches an already-installed source, instead of running a full text search across every source
- Add category filtering to the Updates tab (Recents > Updates > display options)

### Changes
- Debounce library search input (250ms) so refiltering no longer runs on every keystroke
- Spoof `Sec-CH-UA` client hints in WebView to match the user agent
- Update default user agent to Chrome 149

### Fixes
- Fix slow backup restore with large libraries by batching manga restoration into chunked database transactions instead of committing every manga individually
- Fix backup restore inserting duplicate chapters when a backup contains the same chapter URL more than once
- Fix `X-Requested-With` spoofing leaking to unrelated callers
- Fix backup restore posting a system notification update on every single manga (throttled by Android and further slowing large restores); it now only updates once per batch
- Fix backup restore re-querying the full category list from the database once per manga instead of once for the whole restore
- Fix backup restore querying the database once per reading-history entry instead of once per manga, which made restoring a backup with a lot of reading history disproportionately slow
- Fix the same one-query-per-history-entry issue when *creating* a backup, which made backing up a library with a lot of reading history disproportionately slow
- Fix backup creation dispatching every manga's queries individually (one dispatcher context-switch each) instead of batching them into chunked transactions, which was the dominant cost for large libraries
- Fix backup creation's chapter queries needlessly joining the internal scanlator-filter view (a recursive query with no per-manga predicate pushdown, so it re-scanned the whole library on every call) even when the scanlator filter isn't used, which was the actual dominant cost for large libraries
- Harden the mitigation for a rare crash when bulk-removing/migrating library entries (a sqldelight race between a library query and a concurrent write): retry with a short delay until it clears instead of giving up after a single immediate retry
- Fix "Check for updates" never finding new nightly builds: it was querying the stable release repo with a stale GitHub username, so nightly checks silently failed and update links pointed to the wrong repo

## [1.4.0]

### Additions
- Add a `src:` search prefix to filter the library by source name or ID (e.g. `src:MangaDex` or `src:2499283573021220255`)
- Add options to immediately fetch metadata and/or the chapter list from the source when adding a manga to the library, instead of waiting for the next library update (Settings > Library, both off by default)
- Add a "Use legacy decoder for long strip reader" option (Settings > Advanced) to force the older SSIV decoder for webtoon pages when lowering the hardware bitmap threshold doesn't fix blank images (contributed by [@Hiirbaf](https://github.com/Hiirbaf))

### Fixes
- Fix a manga's cover sometimes staying blank in the library grid after adding it, until the item was rebound (e.g. by long-pressing it)
- Fix covers never loading in Global Search and Migration, caused by the list rebinding/resorting fast enough to keep cancelling each cover's own in-flight load before it could finish; both now render covers the same Coil/Compose way as the Library and Browse screens
- Fix a broken-image icon showing while a Global Search/Migration cover was still loading or searching, instead of a neutral placeholder
- Fix the migration screen's toolbar title staying stuck at "(0/0)" instead of showing the real manga count
- Fix "Migrate All"'s progress dialog not showing the final count for a moment before closing, most noticeable when migrating a single manga
- Fix "Migrate All"'s progress counting every manga it looked at instead of only the ones actually migrated (e.g. migrating 3 of 4 manga showed "4/4" instead of "3/4")
- Fix automatic migration matching only trying the manga's exact full title against a source's search, missing manga a manual search could still find; it now also tries shorter word combinations as a fallback, with a stricter similarity requirement so a coincidentally shared generic word can't match a completely unrelated manga

### Other
- Enable and fix Kotlin style checks (`kotlinter`) project-wide; it was declared but never actually wired up, so it had never run
- Add CI workflows for Kotlin style checks and CodeQL security analysis
- Document the commit message convention in CONTRIBUTING.md
- Publish nightly build releases to a separate `rokku-nightly` repo instead of the main one, keeping its tags/releases list to actual versions

## [1.3.2]

### Fixes
- Fix the app icon showing as a generic system icon instead of Rokku's own artwork in condensed/bundled notification stacks on some OEM launchers (e.g. MIUI lock screen), caused by the themed/monochrome launcher icon never being wired up since the Yokai fork
- Fix the app's own self-update getting stuck showing "Installing" forever if the install confirmation notification was missed, the same issue fixed for extension updates in 1.3.1

## [1.3.1]

### Additions
- Support the `mihon://extension-store` deep link for adding extension repos, alongside the existing `tachiyomi://add-repo` one (contributed by [@Hiirbaf](https://github.com/Hiirbaf))

### Fixes
- Fix extension updates getting stuck showing "Installing" forever if the OS silently blocked the install confirmation dialog (common when updating an already-installed extension), leaving no way out except uninstalling and reinstalling the extension
- Fix the reader not exiting fullscreen/immersive mode correctly in split-screen or multi-window mode
- Fix the app bar's toolbar mode not resyncing after entering/exiting split screen
- Fix the floating browse toolbar recalculating its bottom margin (including keyboard insets) on every scroll on Android 10 and below
- Fix the favorite button's long-press category picker letting a stray tap open a menu underneath it
- Fix a crash when exiting the reader back to manga details with no shared element to animate

### Other
- Upgrade to Android Gradle Plugin 9.3.1 and Gradle 9.6.1, matching Mihon

## [1.3.0]

### Additions
- Add a persistent "Library update errors" screen listing manga that failed to update, with select-all/individual selection and bulk migration to another source (ported from Komikku)

### Fixes
- Fix a "ghost" download group summary notification surviving if the app process was killed while downloads were still queued (e.g. swiping the app away from recents)

## [1.2.5]

### Fixes
- Fix a "ghost" download notification staying behind after all downloads finished

## [1.2.4]

### Fixes
- Fix library update discarding a source's updated manga details (cover, description, status, etc.) whenever it returned them together with the chapter list, instead of only when explicitly requested, and doing a redundant extra request for details it already had
- Fix the download icon in the chapter list getting stuck instead of animating while a chapter downloads, if the row scrolled off-screen and back mid-download
- Fix the "New chapters found" notification sometimes showing no content when only one manga had an update, and its "Skipped" counterpart opening the help page instead of the skip log when tapped
- Restore the animated download icon (matching upstream) instead of a static one, and stop the download notification's group summary from re-posting on every single downloaded page, which could make it visibly flicker/reshuffle in the notification shade

## [1.2.3]

### Fixes
- Fix "Data and storage" opening the older, less polished settings screen; it's now the primary screen reached from Settings
- Fix "Data and storage" (and every other Settings screen using the large collapsible app bar) freezing mid-scroll until the screen was reopened
- Fix the "In library" badge not showing on the comfortable grid layout when browsing a source or viewing related manga
- Fix extension installs/updates sometimes getting stuck showing "Downloading" or the install button after finishing, until the extensions sheet was reopened
- Fix the manga's own source being dropped from the migration search when more than one source was available
- Fix the app version showing a redundant "Release" prefix in About
- Fix grouped download notifications sometimes showing a generic icon instead of Rokku's

## [1.2.2]

### Additions
- Add an option to pick what to restore from a backup (library, categories, app settings, source settings, extension repos), instead of it being all-or-nothing
- Back up and restore custom extension repos along with the rest of your data

### Fixes
- Fix "Data and storage" defaulting to the experimental settings screen instead of the stable one (single tap now opens the stable screen; long-press still opens the experimental one, matching upstream)
- Fix in-app updates sometimes getting stuck on "Installing" forever, by falling back to a tap-to-confirm notification if the install dialog is blocked by the OS
- Fix "Help translate" linking to Yokai's translation project instead of Rokku's

## [1.2.1]

### Fixes
- Fix chapters from some sources (e.g. Asura Scans, Hive Scans, Kayn, Vortex) failing to open with a "Refresh Chapter List" error, caused by the local database silently discarding the `memo` metadata some sources rely on to validate a chapter before loading it

## [1.2.0]

### Additions
- Add a "Suggestions" section to the manga details page and a dedicated full-list screen, showing related titles from the same source (Settings → Browse, off by default)

### Fixes
- Fix the webtoon reader visibly resizing/jumping when transitioning between pages split from a long strip image
- Fix source search/browse results not reflecting a manga's library status if it was favorited a few screens deeper in the navigation (e.g. via a suggested manga)

### Other
- Standardize Settings descriptions to not end with a trailing period

## [1.1.8]

### Fixes
- Fix manga details sometimes only loading info or chapters (never both) until a manual reload, caused by two concurrent calls to the same source request racing against its concurrency guard (contributed by [@Hiirbaf](https://github.com/Hiirbaf))
- Fix in-app updates sometimes getting stuck on "Installing" forever on some OEM devices, by falling back to a manual install prompt if the install confirmation never appears

## [1.1.7]

### Fixes
- Fix migration screen crashing (double-unlock) and getting progressively slower with each migrated manga
- Fix manga description showing blank when opening from the library until a manual refresh
- Fix library selection being cleared every time a background chapter download completed
- Fix chapter list stutter and a stuck download-completion animation while other manga download in the background
- Fix a ~1s freeze and missed taps when marking a single chapter as read/bookmarked
- Fix the per-category library update spinner not animating, or getting stuck off
- Fix a UI freeze when starting a category update while another one was already running
- Add progress feedback to Migrate All / Copy All

## [1.1.6]

### Fixes
- Fix editing an extension's settings (e.g. a custom URL) crashing with a `ClassCastException`
- Fix extensions targeting the current `tachiyomix 1.6` protocol failing to load correctly: the `SManga`/`SChapter` interfaces were missing the `memo` field some extensions rely on (contributed by [@Sacha1016](https://github.com/Sacha1016))
- Fix `SManga.copy()` silently dropping the `update_strategy` field
- Fix the About screen's Discord/website links pointing at Mihon's own community, which this fork isn't affiliated with
- Fix download notifications using generic Android system icons instead of this app's own

### Other
- CI: docs-only pushes (README/CHANGELOG) no longer trigger a nightly build/release

## [1.1.5]

### Fixes
- Fix a data-loss bug where toggling "Download with ID" could cause already-downloaded chapters to be misidentified as orphaned and deleted during download cleanup
- Fix a slight shake/readjustment while scrolling down in the webtoon (continuous scroll) reader, caused by pages reflowing right as they finish decoding into view
- Fix remaining links/identifiers still pointing at the original Yokai maintainer: the About screen's GitHub link, the debug build's "view source" link, and the User-Agent sent to tracker services (Anilist, Bangumi, Kavita, Kitsu, MangaUpdates, Shikimori)
- Manga details FAB now shrinks/extends on scroll, and the chapter list no longer sits behind it (contributed by [@Hiirbaf](https://github.com/Hiirbaf))

### Changes
- Removed the BETA tag from "Use staggered grid", "Download with ID", and "Scan external storages for entries" after testing and, where needed, fixing the underlying issues that justified the tag

### Other
- CI: release/beta builds now fail early with a clear error if `CHANGELOG.md` has no matching version section, instead of silently publishing with an empty body
- CI: PR builds now boot the release APK on an emulator and fail if it crashes on launch, to catch R8-shrinking issues (like the one fixed in 1.1.4) before they reach a release
- Removed `.github/FUNDING.yml`, which pointed GitHub's Sponsor button at the original Yokai maintainer's accounts

## [1.1.4]

### Fixes
- Fix extensions that decompress zstd/brotli-encoded responses crashing the app outright (native abort, no error/log shown) in release/nightly builds; R8 was stripping the native decoder classes since they're only reachable via JNI

## [1.1.3]

### Other
- Raise `minSdk` to 26 (matching Mihon) to fix `AbstractMethodError` crashes in extensions (e.g. MangaDotNet) compiled assuming native Java 8 default-interface-method dispatch, which Android doesn't support below API 24; this drops support for Android 6.0/7.0/7.1

## [1.1.2]

### Fixes
- Fix extensions that decompress zstd-encoded responses crashing with `NoClassDefFoundError: okhttp3.zstd.Zstd` (the fix in 1.1.1 restored the brotli dependency but missed zstd)

## [1.1.1]

### Fixes
- Fix extensions that reference `okhttp3.brotli.Brotli` or `okhttp3.zstd.Zstd` directly (e.g. WeebCentral, MangaDotNet) crashing with `NoClassDefFoundError` since 1.1.0

### Other
- CI: make the manual release/beta build actually set the app's version (previously it only renamed the GitHub tag/release, the installed app kept showing an unrelated version number)
- CI: normalize and quote the version input so a stray space no longer breaks the whole build
- CI: push the version tag before creating the GitHub release, avoiding a race that could leave the release "untagged"
- CI: grant `GITHUB_TOKEN` write permissions for releases and test check runs (newer repos default to read-only)

## [1.1.0]

### Fixes
- Fix in-app update checker pointing at the original Yokai's GitHub repo instead of this fork's (was permanently showing a fake "update available")
- Removed forced Brotli/gzip-bypass network interceptors (fixed some sources but broke others that treat them as a bot-detection signal; see the known issue below, fixed in 1.1.1)

### Changes
- Replaced the Yokai notification icon with a Rokku one

### Known issues (fixed in 1.1.1)
- Some extensions that reference `okhttp3.brotli.Brotli` directly crash on load

## [1.0.1]

### Changes
- Own `applicationId` (`app.rokku`) instead of reusing the original Yokai's, so this fork can be installed alongside it without conflicting
- Removed Firebase Crashlytics/Analytics (was still pointing at the original Yokai maintainer's project)

## [1.0.0]

First release under the Rokku name. Everything below was inherited as broken from Yokai/pre-fork and fixed here; see the README for full fork history and credits.

### Fixes
- Fix Keiyoushi extension compatibility: extension lib version window was capped below what current extensions ship as, rejecting all of them
- Fix `Source`/`CatalogueSource` interface to match the current extension API (`tachiyomix 1.6`), including the combined `getMangaUpdate` call
- Fix "add extension repository" only accepting a URL ending in `/index.min.json`, rejecting the `repo.json`/`index.pb` URLs Keiyoushi now points users to
- Add support for the newer protobuf/JSON "ExtensionStore" repository index format, with the legacy `index.min.json` array kept as a fallback
- Fix Shikimori tracker using a decommissioned domain (`shikimori.one` → `shikimori.io`)
- Fix downloader always restarting interrupted page downloads from scratch instead of resuming them
- Fix download notifications not respecting the Android 13+ notification permission
- Fix Shizuku extension installer relying on a private API and shelling out to `pm`, a source of silent install failures; now uses the proper (if hidden) `PackageInstaller` session APIs
- Fix extension install broadcast receiver being unnecessarily exported

### Changes
- "Source repos" setting no longer marked as beta

### Other
- Updated OkHttp, Kotlin, Compose, AndroidX, coroutines/serialization, and other core dependencies
- Completed pt-BR translations

## [1.9.7.5]

### Fixes
- Add missing ProtoBuf singleton definition to the DI for extensions

## [1.9.7.4]

### Other
- Prioritize extension classpath over app
- Update kotlin monorepo to v2.3.10
- Update dependency gradle to v8.14.4

## [1.9.7.3]

### Fixes
- More `Comparison method violates its general contract!` crash prevention

## [1.9.7.2]

### Fixes
- Fix MyAnimeList timeout issue

## [1.9.7.1]

### Fixes
- Prevent `Comparison method violates its general contract!` crashes

## [1.9.7]

### Changes
- Adjust log file to only log important information by default

### Fixes
- Fix sorting by latest chapter is not working properly
- Prevent some NPE crashes
- Fix some flickering issues when browsing sources
- Fix download count is not updating

### Translation
- Update Korean translation (@Meokjeng)

### Other
- Update NDK to v27.2.12479018

## [1.9.6]

### Fixes
- Fix some crashes

## [1.9.5]

### Changes
- Entries from local source now behaves similar to entries from online sources

### Fixes
- Fix new chapters not showing up in `Recents > Grouped`
- Add potential workarounds for duplicate chapter bug
- Fix favorite state is not being updated when browsing source

### Other
- Update dependency androidx.compose:compose-bom to v2024.12.01
- Update plugin kotlinter to v5
- Update plugin gradle-versions to v0.51.0
- Update kotlin monorepo to v2.1.0

## [1.9.4]

### Fixes
- Fix chapter date fetch always null causing it to not appear on Updates tab

## [1.9.3]

### Fixes
- Fix slow chapter load
- Fix chapter bookmark state is not persistent

### Other
- Refactor downloader
  - Replace RxJava usage with Kotlin coroutines
  - Replace DownloadQueue with Flow to hopefully fix ConcurrentModificationException entirely

## [1.9.2]

### Changes
- Adjust chapter title-details contrast
- Make app updater notification consistent with other notifications

### Fixes
- Fix "Remove from read" not working properly

## [1.9.1]

### Fixes
- Fix chapters cannot be opened from `Recents > Grouped` and `Recents > All`
- Fix crashes caused by malformed XML
- Fix potential memory leak

### Other
- Update dependency io.github.kevinnzou:compose-webview to v0.33.6
- Update dependency org.jsoup:jsoup to v1.18.3
- Update voyager to v1.1.0-beta03
- Update dependency androidx.annotation:annotation to v1.9.1
- Update dependency androidx.constraintlayout:constraintlayout to v2.2.0
- Update dependency androidx.glance:glance-appwidget to v1.1.1
- Update dependency com.google.firebase:firebase-bom to v33.7.0
- Update fast.adapter to v5.7.0
- Downgrade dependency org.conscrypt:conscrypt-android to v2.5.2

## [1.9.0]

### Additions
- Sync DoH provider list with upstream (added Mullvad, Control D, Njalla, and Shecan)
- Add option to enable verbose logging
- Add category hopper long-press action to open random series from **any** category
- Add option to enable reader debug mode
- Add option to adjust reader's hardware bitmap threshold (@AntsyLich)
  - Always use software bitmap on certain devices (@MajorTanya)
- Add option to scan local entries from `/storage/(sdcard|emulated/0)/Android/data/<yokai>/files/local`

### Changes
- Enable 'Split Tall Images' by default (@Smol-Ame)
- Minor visual adjustments
- Tell user to restart the app when User-Agent is changed (@NGB-Was-Taken)
- Re-enable fetching licensed manga (@Animeboynz)
- Bangumi search now shows the score and summary of a search result (@MajorTanya)
- Logs are now written to a file for easier debugging
- Bump default user agent (@AntsyLich)
- Custom cover is now compressed to WebP to prevent OOM crashes

### Fixes
- Fix only few DoH provider is actually being used (Cloudflare, Google, AdGuard, and Quad9)
- Fix "Group by Ungrouped" showing duplicate entries
- Fix reader sometimes won't load images
- Handle some uncaught crashes
- Fix crashes due to GestureDetector's firstEvent is sometimes null on some devices
- Fix download failed due to invalid XML 1.0 character
- Fix issues with shizuku in a multi-user setup (@Redjard)
- Fix some regional/variant languages is not listed in app language option
- Fix browser not opening in some cases in Honor devices (@MajorTanya)
- Fix "ConcurrentModificationException" crashes
- Fix Komga unread badge, again
- Fix default category can't be updated manually
- Fix crashes trying to load Library caused by cover being too large

### Other
- Simplify network helper code
- Fully migrated from StorIO to SQLDelight
- Update dependency com.android.tools:desugar_jdk_libs to v2.1.3
- Update moko to v0.24.4
- Refactor trackers to use DTOs (@MajorTanya)
  - Fix AniList `ALSearchItem.status` nullibility (@Secozzi)
- Replace Injekt with Koin
- Remove unnecessary permission added by Firebase
- Remove unnecessary features added by Firebase
- Replace BOM dev.chrisbanes.compose:compose-bom with JetPack's BOM
- Update dependency androidx.compose:compose-bom to v2024.11.00
- Update dependency com.google.firebase:firebase-bom to v33.6.0
- Update dependency com.squareup.okio:okio to v3.9.1
- Update activity to v1.9.3
- Update lifecycle to v2.8.7
- Update dependency me.zhanghai.android.libarchive:library to v1.1.4
- Update agp to v8.7.3
- Update junit5 monorepo to v5.11.3
- Update dependency androidx.test.ext:junit to v1.2.1
- Update dependency org.jetbrains.kotlinx:kotlinx-collections-immutable to v0.3.8
- Update dependency org.jsoup:jsoup to v1.18.1
- Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-bom to v1.9.0
- Update serialization to v1.7.3
- Update dependency gradle to v8.11.1
- Update dependency androidx.webkit:webkit to v1.12.0
- Update dependency io.mockk:mockk to v1.13.13
- Update shizuku to v13.1.5
  - Use reflection to fix shizuku breaking changes (@Jobobby04)
- Bump compile sdk to 35
  - Handle Android SDK 35 API collision (@AntsyLich)
- Update kotlin monorepo to v2.0.21
- Update dependency androidx.work:work-runtime-ktx to v2.10.0
- Update dependency androidx.core:core-ktx to v1.15.0
- Update dependency io.coil-kt.coil3:coil-bom to v3.0.4
- Update xml.serialization to v0.90.3
- Update dependency co.touchlab:kermit to v2.0.5
- Replace WebView to use Compose (@arkon)
  - Fixed Keyboard is covering web page inputs
- Increased `tryToSetForeground` delay to fix potential crashes (@nonproto)
- Update dependency org.conscrypt:conscrypt-android to v2.5.3
- Port upstream's download cache system

## [1.8.5.13]

### Fixed
- Fix version checker

## [1.8.5.12]

### Fixed
- Fixed scanlator data sometimes disappear

## [1.8.5.11]

### Fixed
- Fixed crashes caused by Bangumi invalid status

## [1.8.5.10]

### Fixes
- Fixed scanlator filter not working properly

## [1.8.5.9]

### Changes
- Revert create backup to use file picker

## [1.8.5.8]

### Other
- Separate backup error log when destination is null or not a file
- Replace com.github.inorichi.injekt with com.github.null2264.injekt

## [1.8.5.7]

### Fixes
- Fixed more NPE crashes

## [1.8.5.6]

### Fixes
- Fixed NPE crash on tablets

## [1.8.5.5]

### Fixes
- Fixed crashes caused by certain extension implementation
- Fixed "Theme buttons based on cover" doesn't work properly
- Fixed library cover images looks blurry then become sharp after going to
  entry's detail screen

### Other
- More StorIO to SQLDelight migration effort
- Update dependency dev.chrisbanes.compose:compose-bom to v2024.08.00-alpha02
- Update kotlin monorepo to v2.0.20
- Update aboutlibraries to v11.2.3
- Remove dependency com.github.leandroBorgesFerreira:LoadingButtonAndroid

## [1.8.5.4]

### Fixes
- Fixed custom cover set from reader didn't show up on manga details

## [1.8.5.3]

### Additions
- Add toggle to enable/disable chapter swipe action(s)
- Add toggle to enable/disable webtoon double tap to zoom

### Changes
- Custom cover now shown globally

### Fixes
- Fixed chapter number parsing (@Naputt1)
- Reduced library flickering (still happened in some cases when the cached image size is too different from the original image size, but should be reduced quite a bit)
- Fixed entry details header didn't update when being removed from library

### Other
- Refactor chapter recognition (@stevenyomi)
- (Re)added unit test for chapter recognition
- More StorIO to SQLDelight migration effort
- Target Android 15
- Adjust manga cover cache key
- Refactor manga cover fetcher (@ivaniskandar, @AntsyLich, @null2264)

## [1.8.5.2]

### Fixes
- Fixed some preference not being saved properly

### Other
- Update dependency co.touchlab:kermit to v2.0.4
- Update lifecycle to v2.8.4

## [1.8.5.1]

### Fixes
- Fixed library showing duplicate entry when using dynamic category

## [1.8.5]

### Additions
- Add missing "Max automatic backups" option on experimental Data and Storage setting menu
- Add information on when was the last time backup automatically created to experimental Data and Storage setting menu
- Add monochrome icon

### Changes
- Add more info to WorkerInfo page
  - Added "next scheduled run"
  - Added attempt count
- `english` tag no longer cause reading mode to switch to LTR (@mangkoran)
- `chinese` tag no longer cause reading mode to switch to LTR
- `manhua` tag no longer cause reading mode to switch to LTR
- Local source manga's cover now being invalidated on refresh
- It is now possible to create a backup without any entries using experimental Data and Storage setting menu
- Increased default maximum automatic backup files to 5
- It is now possible to edit a local source entry without adding it to library
- Long Strip and Continuous Vertical background color now respect user setting
- Display Color Profile setting no longer limited to Android 8 or newer
- Increased long strip cache size to 4 for Android 8 or newer (@FooIbar)
- Use Coil pipeline to handle HEIF images

### Fixes
- Fixed auto backup, auto extension update, and app update checker stop working
  if it crash/failed
- Fixed crashes when trying to reload extension repo due to connection issue
- Fixed tap controls not working properly after zoom (@arkon, @Paloys, @FooIbar)
- Fixed (sorta, more like workaround) ANR issues when running background tasks, such as updating extensions (@ivaniskandar)
- Fixed split (downloaded) tall images sometimes doesn't work
- Fixed status bar stuck in dark mode when app is following system theme
- Fixed splash screen state only getting updates if library is empty (Should slightly reduce splash screen duration)
- Fixed kitsu tracker issue due to domain change
- Fixed entry custom cover won't load if entry doesn't have cover from source
- Fixed unread badge doesn't work properly for some sources (notably Komga)
- Fixed MAL start date parsing (@MajorTanya)

### Translation
- Update Japanese translation (@akir45)
- Update Brazilian Portuguese translation (@AshbornXS)
- Update Filipino translation (@infyProductions)

### Other
- Re-added several social media links to Mihon
- Some code refactors
  - Simplify some messy code
  - Rewrite version checker
  - Rewrite Migrator (@ghostbear)
  - Split the project into several modules
  - Migrated i18n to use Moko Resources
  - Removed unnecessary dependencies (@null2264, @nonproto)
- Update firebase bom to v33.1.0
- Replace com.google.android.gms:play-services-oss-licenses with com.mikepenz:aboutlibraries
- Update dependency com.google.gms:google-services to v4.4.2
- Add crashlytics integration for Kermit
- Replace ProgressBar with ProgressIndicator from Material3 to improve UI consistency
- More StorIO to SQLDelight migrations
  - Merge lastFetch and lastRead query into library_view VIEW
  - Migrated a few more chapter related queries
  - Migrated most of the manga related queries
- Bump dependency com.github.tachiyomiorg:unifile revision to a9de196cc7
- Update project to Kotlin 2.0 (v2.0.10)
- Update compose bom to v2024.08.00-alpha01
- Refactor archive support to use `libarchive` (@FooIbar)
- Use version catalog for gradle plugins
- Update dependency org.jsoup:jsoup to v1.7.1
- Bump dependency com.github.tachiyomiorg:image-decoder revision to 41c059e540
- Update dependency io.coil-kt.coil3 to v3.0.0-alpha10
- Update Android Gradle Plugin to v8.5.2
- Update gradle to v8.9
- Start using Voyager for navigation
- Update dependency androidx.work:work-runtime-ktx to v2.9.1
- Update dependency androidx.annotation:annotation to v1.8.2

## [1.8.4.6]

### Fixes
- Fixed scanlator filter not working properly if it contains " & "

### Other
- Removed dependency com.dmitrymalkovich.android:material-design-dimens
- Replace dependency br.com.simplepass:loading-button-android with
  com.github.leandroBorgesFerreira:LoadingButtonAndroid
- Replace dependency com.github.florent37:viewtooltip with
  com.github.CarlosEsco:ViewTooltip

## [1.8.4.5]

### Fixes
- Fixed incorrect library entry chapter count

## [1.8.4.4]

### Fixes
- Fixed incompatibility issue with J2K backup file

## [1.8.4.3]

### Fixes
- Fixed "Open source repo" icon's colour

## [1.8.4.2]

### Changes
- Changed "Open source repo" icon to prevent confusion

## [1.8.4.1]

### Fixes
- Fixed saving combined pages not doing anything

## [1.8.4]

### Additions
- Added option to change long tap browse and recents nav behaviour
  - Added browse long tap behaviour to open global search (@AshbornXS)
  - Added recents long tap behaviour to open last read chapter (@AshbornXS)
- Added option to backup sensitive settings (such as tracker login tokens)
- Added beta version of "Data and storage" settings (can be accessed by long tapping "Data and storage")

### Changes
- Remove download location redirection from `Settings > Downloads`
- Moved cache related stuff from `Settings > Advanced` to `Settings > Data and storage`
- Improve webview (@AshbornXS)
  - Show url as subtitle
  - Add option to clear cookies
  - Allow zoom
- Handle urls on global search (@AshbornXS)
- Improve download queue (@AshbornXS)
  - Download badge now show download queue count
  - Add option to move series to bottom
- Only show "open repo url" button when repo url is not empty

### Fixes
- Fix potential crashes for some custom Android rom
- Allow MultipartBody.Builder for extensions
- Refresh extension repo now actually refresh extension(s) trust status
- Custom manga info now relink properly upon migration
- Fixed extension repo list did not update when a repo is added via deep link
- Fixed download unread trying to download filtered (by scanlator) chapters
- Fixed extensions not retaining their repo url
- Fixed more NullPointerException crashes
- Fixed split layout caused non-split images to not load

### Other
- Migrate some StorIO queries to SQLDelight, should improve stability
- Migrate from Timber to Kermit
- Update okhttp monorepo to v5.0.0-alpha.14
- Refactor backup code
  - Migrate backup flags to not use bitwise
  - Split it to several smaller classes
- Update androidx.compose.material3:material3 to v1.3.0-beta02

## [1.8.3.4]

### Fixes
- Fixed crashes caused by invalid ComicInfo XML

  If this caused your custom manga info to stop working, try resetting it by deleting `ComicInfoEdits.xml` file located in `Android/data/eu.kanade.tachiyomi.yokai`

- Fixed crashes caused by the app trying to round NaN value

## [1.8.3.3]

### Changes
- Crash report can now actually be disabled

### Other
- Loading GlobalExceptionHandler before Crashlytics

## [1.8.3.2]

### Other
- Some more NullPointerException prevention that I missed

## [1.8.3.1]

### Other
- A bunch of NullPointerException prevention

## [1.8.3]

### Additions
- Extensions now can be trusted by repo

### Changes
- Extensions now required to have `repo.json`

### Other
- Migrate to SQLDelight
- Custom manga info is now stored in the database

## [1.8.2]

### Additions
- Downloaded chapters now include ComicInfo file
- (LocalSource) entry chapters' info can be edited using ComicInfo

### Fixes
- Fixed smart background colour by page failing causing the image to not load
- Fixed downloaded chapter can't be opened if it's too large
- Downloaded page won't auto append chapter ID even tho the option is enabled

### Other
- Re-route nightly to use its own repo, should fix "What's new" page

## [1.8.1.2]

### Additions
- Added a couple new tags to set entry as SFW (`sfw` and `non-erotic`)

### Fixes
- Fixed smart background colour by page failing causing the image to not load

### Other
- Re-route nightly to use its own repo, should fix "What's new" page

## [1.8.1.1]

### Fixes
- Fixed crashes when user try to edit an entry

## [1.8.1]

### Additions
- (Experimental) Option to append chapter ID to download filename to avoid conflict

### Changes
- Changed notification icon to use Yōkai's logo instead
- Yōkai is now ComicInfo compliant. [Click here to learn more](https://anansi-project.github.io/docs/comicinfo/intro)
- Removed "Couldn't split downloaded image" notification to reduce confusion. It has nothing to do with unsuccessful split, it just think it shouldn't split the image

### Fixes
- Fixed not being able to open different chapter when a chapter is already opened
- Fixed not being able to read chapters from local source
- Fixed local source can't detect archives

### Other
- Wrap SplashState to singleton factory, might fix issue where splash screen shown multiple times
- Use Okio instead of `java.io`, should improve reader stability (especially long strip)

## [1.8.0.2]

### Fixes
- Fixed app crashes when backup directory is null
- Fixed app asking for All Files access permission when it's no longer needed

## [1.8.0.1]

### Additions
- Added CrashScreen

### Fixes
- Fixed version checker for nightly against hotfix patch version
- Fixed download cache causes the app to crash

## [1.8.0]

### Additions
- Added cutout support for some pre-Android P devices
- Added option to add custom colour profile
- Added onboarding screen

### Changes
- Permanently enable 32-bit colour mode
- Unified Storage™ ([Click here](https://mihon.app/docs/faq/storage#migrating-from-tachiyomi-v0-14-x-or-earlier) to learn more about it)

### Fixes
- Fixed cutout behaviour for Android P
- Fixed some extensions doesn't detect "added to library" entries properly ([GH-40](https://github.com/null2264/yokai/issues/40))
- Fixed nightly and debug variant doesn't include their respective prefix on their app name
- Fixed nightly version checker

### Other
- Update dependency com.github.tachiyomiorg:image-decoder to e08e9be535
- Update dependency com.github.null2264:subsampling-scale-image-view to 338caedb5f
- Added Unit Test for version checker
- Use Coil pipeline instead of SSIV for image decode whenever possible, might improve webtoon performance
- Migrated from Coil2 to Coil3
- Update compose compiler to v1.5.14
- Update dependency androidx.compose.animation:animation to v1.6.7
- Update dependency androidx.compose.foundation:foundation to v1.6.7
- Update dependency androidx.compose.material:material to v1.6.7
- Update dependency androidx.compose.ui:ui to v1.6.7
- Update dependency androidx.compose.ui:ui-tooling to v1.6.7
- Update dependency androidx.compose.ui:ui-tooling-preview to v1.6.7
- Update dependency androidx.compose.material:material-icons-extended to v1.6.7
- Update dependency androidx.lifecycle:lifecycle-viewmodel-compose to v2.8.0
- Update dependency androidx.activity:activity-ktx to v1.9.0
- Update dependency androidx.activity:activity-compose to v1.9.0
- Update dependency androidx.annotation:annotation to v1.8.0
- Update dependency androidx.browser:browser to v1.8.0
- Update dependency androidx.core:core-ktx to v1.13.1
- Update dependency androidx.lifecycle:lifecycle-viewmodel-ktx to v2.8.0
- Update dependency androidx.lifecycle:lifecycle-livedata-ktx to v2.8.0
- Update dependency androidx.lifecycle:lifecycle-common to v2.8.0
- Update dependency androidx.lifecycle:lifecycle-process to v2.8.0
- Update dependency androidx.lifecycle:lifecycle-runtime-ktx to v2.8.0
- Update dependency androidx.recyclerview:recyclerview to v1.3.2
- Update dependency androidx.sqlite:sqlite to v2.4.0
- Update dependency androidx.webkit:webkit to v1.11.0
- Update dependency androidx.work:work-runtime-ktx to v2.9.0
- Update dependency androidx.window:window to v1.2.0
- Update dependency com.google.firebase:firebase-crashlytics-gradle to v3.0.1
- Update dependency com.google.gms:google-services to v4.4.1
- Update dependency com.google.android.material:material to v1.12.0
- Update dependency com.squareup.okio:okio to v3.8.0
- Update dependency com.google.firebase:firebase-bom to v33.0.0
- Update dependency org.jetbrains.kotlin:kotlin-gradle-plugin to v1.9.24
- Update dependency org.jetbrains.kotlin:kotlin-serialization to v1.9.24
- Update dependency org.jetbrains.kotlinx:kotlinx-serialization-json to v1.6.2
- Update dependency org.jetbrains.kotlinx:kotlinx-serialization-json-okio to v1.6.2
- Update dependency org.jetbrains.kotlinx:kotlinx-serialization-protobuf to v1.6.2
- Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-android to v1.8.0
- Update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.8.0
- Resolved some compile warnings
- Update dependency com.github.tachiyomiorg:unifile to 7c257e1c64

## [1.7.14]

### Changes
- Added splash to reader (in case it being opened from shortcut)
- Increased long strip split height
- Use normalized app name by default as folder name

### Fixes
- Fixed cutout support being broken

### Other
- Move AppState from DI to Application class to reduce race condition

## [1.7.13]

### Additions
- Ported Tachi's cutout option
- Added Doki theme (dark only)

### Changes
- Repositioned cutout options in settings
- Splash icon now uses coloured variant of the icon
- Removed deep link for sources, this should be handled by extensions
- Removed braces from nightly (and debug) app name

### Fixes
- Fixed preference summary not updating after being changed once
- Fixed legacy appbar is visible on compose when being launched from deeplink
- Fixed some app icon not generated properly
- Fixed splash icon doesn't fit properly on Android 12+

### Other
- Migrate to using Android 12's SplashScreen API
- Clean up unused variables from ExtensionInstaller

## [1.7.12]

### Additions
- Scanlator filter is now being backed up (@jobobby04)

### Fixes
- Fixed error handling for MAL tracking (@AntsyLich)
- Fixed extension installer preference incompatibility with modern Tachi

### Other
- Split PreferencesHelper even more
- Simplify extension install issue fix (@AwkwardPeak7)
- Update dependency com.github.tachiyomiorg:image-decoder to fbd6601290
- Replace dependency com.github.jays2kings:subsampling-scale-image-view with com.github.null2264:subsampling-scale-image-view
- Update dependency com.github.null2264:subsampling-scale-image-view to e3cffd59c5

## [1.7.11]

### Fixes
- Fixed MAL tracker issue (@AntsyLich)
- Fixed trusting extension caused it to appear twice

### Other
- Change Shikimori client from Tachi's to Yōkai's
- Move TrackPreferences to PreferenceModule

## [1.7.10]

### Addition
- Content type filter to hide SFW/NSFW entries
- Confirmation before revoking all trusted extension

### Changes
- Revert Webcomic -> Webtoon

### Fixes
- Fix app bar disappearing on (scrolled) migration page
- Fix installed extensions stuck in "installable" state
- Fix untrusted extensions not having an icon

### Other
- Changed (most) trackers' client id and secret
- Add or changed user-agent for trackers

## [1.7.9]

### Other
- Sync project with J2K [v1.7.4](https://github.com/Jays2Kings/tachiyomiJ2K/releases/tag/v1.7.4)

## [1.7.8]

### Changes
- Local source now try to find entries not only in `Yōkai/` but also in `Yokai/` and `TachiyomiJ2K/` for easier migration

### Other
- Changed AniList and MAL clientId, you may need to logout and re-login

## [1.7.7]

### Changes
- Hopper icon now changes depending on currently active group type (J2K)

### Fixes
- Fixed bookmarked entries not being detected as bookmarked on certain extensions

## [1.7.6]

### Additions
- Shortcut to Extension Repos from Browser -> Extensions page
- Added confirmation before extension repo deletion

### Changes
- Adjusted dialogs background colour to be more consistent with app theme

### Fixes
- Fixed visual glitch where page sometime empty on launch
- Fixed extension interceptors receiving compressed responses (T)

### Other
- Newly added strings from v1.7.5 is now translatable

## [1.7.5]

### Additions
- Ported custom extension repo from upstream

### Changes
- Removed built-in extension repo
- Removed links related to Tachiyomi
- Ported upstream's trust extension logic
- Rebrand to Yōkai

### Other
- Start migrating to Compose

## [1.7.4]

### Changes
- Rename project to Yōkai (Z)
- Replace Tachiyomi's purged extensions with Keiyoushi extensions (Temporary solution until I ported custom extension repo feature) (Z)
- Unread count now respect scanlator filter (J2K)

### Fixes
- Fixed visual glitch on certain page (J2K)
