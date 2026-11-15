package eu.kanade.tachiyomi.extension.ja.kisslove

/**
 * Picks the Japanese title out of the site's `other_name`, a comma-separated list that mixes
 * Japanese, Chinese, Korean and English names ("The Saint of the Drugstore, 薬屋の聖女, 药屋的圣女").
 * A name with kana comes first. Failing that, a short all-kanji name that Shift_JIS can write
 * (呪術廻戦, but not the simplified 咒术回战); longer all-kanji names are mostly Chinese.
 */
internal object JapaneseTitle {
    private val trailingLatinNote = Regex("""\s*[(（][\p{IsLatin}\p{IsDigit}\s.,'&-]*[)）]\s*$""")
    private val rawSuffix = Regex("""\s*[-–]\s*生肉\s*$|\s*[(（]生肉[)）]\s*$""")

    private val shiftJis = charset("Shift_JIS")

    fun of(otherName: String): String? {
        val names = otherName.split(',', '，').map { it.trim() }
        return names
            .asSequence()
            .filter { hasKana(it) }
            .map { clean(it) }
            .firstOrNull { it.isNotEmpty() }
            ?: names.firstOrNull { isJapaneseKanji(it) }
    }

    private fun isJapaneseKanji(name: String) = name.length in 2..MAX_KANJI_TITLE &&
        name.none { it.isHangul() } &&
        name.all { Character.UnicodeScript.of(it.code) == Character.UnicodeScript.HAN } &&
        shiftJis.newEncoder().canEncode(name)

    private const val MAX_KANJI_TITLE = 8

    /**
     * "Kusuriya no Hitorigoto 薬屋のひとりごと (KURATA Minoji)" → "薬屋のひとりごと";
     * "… Якусоку но Нэба:рандо 約束のネバーランド 约定的梦幻岛 약속의 네버랜드" → "約束のネバーランド".
     * Some entries put every name in one run separated by spaces, so start at the first word with
     * kana and stop at the first word in another language (all kanji, hangul, Cyrillic, ...).
     */
    private fun clean(name: String): String {
        val words = name.split(' ', '　').filter { it.isNotEmpty() }
        val start = words.indexOfFirst { hasKana(it) }
        if (start < 0) return ""
        val run = words.drop(start).takeWhile { word -> hasKana(word) || word.all { it.isLatinOrCommon() } }
        // Latin words count only inside the title (ラブライブ！flowers* -蓮ノ空…-), not after it.
        val end = run.indexOfLast { hasKana(it) }
        return run
            .take(end + 1)
            .filterIndexed { i, word -> i == 0 || word != run[i - 1] }
            .joinToString(" ")
            .replace(trailingLatinNote, "")
            .replace(rawSuffix, "")
            .trim()
    }

    private fun Char.isLatinOrCommon(): Boolean {
        val script = Character.UnicodeScript.of(code)
        return script == Character.UnicodeScript.LATIN || script == Character.UnicodeScript.COMMON
    }

    private fun hasKana(text: String) = text.any { it in 'ぁ'..'ヿ' && it != '・' }

    private fun Char.isHangul() = this in '가'..'힣' || this in 'ᄀ'..'ᇿ' || this in '㄰'..'㆏'
}
