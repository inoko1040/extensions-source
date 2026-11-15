package eu.kanade.tachiyomi.extension.ja.kisslove

/** TachiComi: Japanese names for the site's genres (api/genres); unknown ones stay as they are. */
internal object JapaneseGenre {
    private val names = mapOf(
        "Action" to "アクション",
        "Adult" to "アダルト",
        "Adventure" to "冒険",
        "Comedy" to "コメディ",
        "Demons" to "悪魔",
        "Drama" to "ドラマ",
        "Ecchi" to "エッチ",
        "Fantasy" to "ファンタジー",
        "Gender Bender" to "性転換",
        "Harem" to "ハーレム",
        "Historical" to "歴史",
        "Horror" to "ホラー",
        "Isekai" to "異世界",
        "Josei" to "女性向け",
        "Magic" to "魔法",
        "Mature" to "大人向け",
        "Mecha" to "メカ",
        "Mystery" to "ミステリー",
        "Psychological" to "心理",
        "Romance" to "恋愛",
        "School Life" to "学園",
        "Sci-fi" to "SF",
        "Seinen" to "青年",
        "Shoujo" to "少女",
        "Shounen" to "少年",
        "Shounen Ai" to "少年愛",
        "Slice of Life" to "日常",
        "Smut" to "官能",
        "Sports" to "スポーツ",
        "Super Power" to "超能力",
        "Supernatural" to "超常",
        "Tragedy" to "悲劇",
        "Yaoi" to "BL",
        "Yuri" to "百合",
    ).mapKeys { it.key.lowercase() }

    fun of(genre: String): String = names[genre.trim().lowercase()] ?: genre.trim()
}
