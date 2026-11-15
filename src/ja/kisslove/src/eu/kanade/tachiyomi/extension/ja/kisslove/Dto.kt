package eu.kanade.tachiyomi.extension.ja.kisslove

import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import keiyoushi.utils.tryParse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import kotlin.time.Instant

@Serializable
class PagedManga(
    val currentPage: Long,
    val items: List<Manga>,
    val totalItems: Long,
    val totalPages: Long,
)

@Serializable
class Manga(
    val artists: String?,
    val authors: String,
    val chapters: List<Chapter> = emptyList(),
    val cover: String,
    @SerialName("daily_views") val dailyViews: Long?,
    val description: String = "",
    val genres: String?,
    @SerialName("group_uploader") val groupUploader: Long?,
    val hidden: Int?,
    val id: Long,
    @SerialName("last_chapter") val lastChapter: Double,
    @SerialName("last_update") val lastUpdate: String?,
    @SerialName("m_status") val mStatus: Int,
    val name: String,
    @SerialName("other_name") val otherName: String,
    val released: Int?,
    val showads: Int?,
    val slug: String,
    val submitter: Long?,
    val total: Long?,
    @SerialName("trans_group") val transGroup: String,
    val views: Long,
    @SerialName("vote_count") val voteCount: Long?,
) {
    /** TachiComi: "閲覧 1,960,934 ・ 評価 120 ・ 最終更新 2026-11-01". */
    private fun stats() = listOfNotNull(
        "閲覧 %,d".format(views),
        voteCount?.let { "評価 %,d".format(it) },
        lastUpdate?.take(10)?.let { "最終更新 $it" },
    ).joinToString(" ・ ")

    fun toSManga() = SManga.create().apply {
        url = this@Manga.slug
        // TachiComi: show the Japanese title when the site lists one.
        title = JapaneseTitle.of(otherName) ?: this@Manga.name
        artist = this@Manga.artists
        author = this@Manga.authors
        description = "${Jsoup.parse(this@Manga.description).text()}\n\n${this@Manga.name}, $otherName\n\n${stats()}"
        thumbnail_url = this@Manga.cover
        genre = this@Manga.genres?.split(",")?.joinToString { JapaneseGenre.of(it) }
        status = if (mStatus == 1) SManga.COMPLETED else SManga.ONGOING
    }
}

@Serializable
class ListPagedManga(
    val currentPage: Long,
    val items: List<MangaEntry>,
    val totalItems: Long,
    val totalPages: Long,
)

@Serializable
class MangaEntry(
    val artists: String?,
    val authors: String,
    val cover: String,
    val description: String = "",
    val genres: List<String>?,
    val hidden: Int?,
    val id: Long,
    @SerialName("m_status") val mStatus: Int,
    val name: String,
    @SerialName("other_name") val otherName: String,
    val slug: String,
    @SerialName("trans_group") val transGroup: String,
) {
    fun toSManga() = SManga.create().apply {
        url = this@MangaEntry.slug
        // TachiComi: show the Japanese title when the site lists one.
        title = JapaneseTitle.of(otherName) ?: this@MangaEntry.name
        artist = this@MangaEntry.artists
        author = this@MangaEntry.authors
        description = "${Jsoup.parse(this@MangaEntry.description).text()}\n\n${this@MangaEntry.name}, $otherName"
        thumbnail_url = this@MangaEntry.cover
        genre = this@MangaEntry.genres?.joinToString { JapaneseGenre.of(it) }
        status = if (mStatus == 1) SManga.COMPLETED else SManga.ONGOING
    }
}

@Serializable
class Chapter(
    val chapter: Double,
    val content: String = "",
    val hidden: Int?,
    val id: Long,
    @SerialName("last_update") val lastUpdate: String,
    val manga: String?,
    val name: String?,
    val showads: Int?,
    val submitter: Long?,
    val views: Long?,
) {
    fun toSChapter(slug: String) = SChapter.create().apply {
        url = "$id/$slug-chapter-$chapter"
        name = this@Chapter.name?.takeIf { it.isNotBlank() } ?: "Chapter $chapter"
        date_upload = Instant.tryParse(lastUpdate)
    }
}

@Serializable
class Genre(
    val name: String,
)
