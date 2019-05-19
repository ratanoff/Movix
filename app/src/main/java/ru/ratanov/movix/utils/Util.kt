package ru.ratanov.movix.utils

class Util {
    companion object {
        private val allKeywords =
            arrayOf("алиса", "найди", "сериал", "фильм", "включи", "смотреть", "посмотреть", "мне", "выбери", "выбрать", "плей")
        private val actionFindKeywords = arrayOf("найди")
        private val actionWatchKeywords = arrayOf("включи", "смотреть", "посмотреть")
        private val actionSelectKeywords = arrayOf("выбери", "выбрать")
        private val videoKeyWords = arrayOf("плей")

        val ACTION_FIND = "find"
        val ACTION_WATCH = "watch"
        val ACTION_SELECT = "select"

        fun getCorrectQuery(query: String): String {
            val words = removePunctuation(query).split(" ").toMutableList()
            var correctQuery = ""
            for (word in words) {
                if (word.toLowerCase() !in allKeywords) {
                    correctQuery += " $word"
                }
            }
            return correctQuery.trim()
        }

        // в общем случае возвращает action find
        fun getAction(query: String): String {
            for (wordWatch in actionWatchKeywords) {
                if (query.contains(wordWatch)) {
                    return ACTION_WATCH
                }
            }

            for (wordFind in actionFindKeywords) {
                if (query.contains(wordFind)) {
                    return ACTION_FIND
                }
            }

            for (wordSelect in actionSelectKeywords) {
                if (query.contains(wordSelect)) {
                    return ACTION_SELECT
                }
            }

            return ACTION_FIND
        }

        fun removePunctuation(s: String): String {
            val builder = StringBuilder()
            for (c in s.toCharArray())
                if (Character.isLetterOrDigit(c) || Character.isWhitespace(c))
                    builder.append(if (Character.isLowerCase(c)) c else Character.toLowerCase(c))
            return builder.toString().replace("\\s+".toRegex(), " ")
        }
    }
}