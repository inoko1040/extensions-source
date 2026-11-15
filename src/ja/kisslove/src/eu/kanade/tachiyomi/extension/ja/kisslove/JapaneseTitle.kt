package eu.kanade.tachiyomi.extension.ja.kisslove

/**
 * Picks the Japanese title out of the site's `other_name`, a comma-separated list that mixes
 * Japanese, Chinese, Korean and English names ("The Saint of the Drugstore, 薬屋の聖女, 药屋的圣女").
 * Only a name with kana counts: an all-kanji name is as often Chinese.
 */
internal object JapaneseTitle {
    private val trailingLatinNote = Regex("""\s*[(（][\p{IsLatin}\p{IsDigit}\s.,'&-]*[)）]\s*$""")
    private val rawSuffix = Regex("""\s*[-–]\s*生肉\s*$|\s*[(（]生肉[)）]\s*$""")

    fun of(otherName: String): String? = otherName
        .split(',', '，')
        .asSequence()
        .map { it.trim() }
        .filter { name -> hasKana(name) && name.none { it.isHangul() } }
        .map { clean(it) }
        .firstOrNull { it.isNotEmpty() }

    /** "Kusuriya no Hitorigoto 薬屋のひとりごと (KURATA Minoji)" → "薬屋のひとりごと". */
    private fun clean(name: String): String {
        // Names often follow the romaji or Chinese name in the same entry; start at the first
        // word with kana.
        val words = name.split(' ', '　')
        val start = words.indexOfFirst { hasKana(it) }
        return words
            .drop(start)
            .joinToString(" ")
            .replace(trailingLatinNote, "")
            .replace(rawSuffix, "")
            .trim()
    }

    private fun hasKana(text: String) = text.any { it in 'ぁ'..'ヿ' && it != '・' }

    private fun Char.isHangul() = this in '가'..'힣' || this in 'ᄀ'..'ᇿ' || this in '㄰'..'㆏'
}
