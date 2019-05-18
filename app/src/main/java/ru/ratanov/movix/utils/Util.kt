package ru.ratanov.movix.utils

class Util {
    companion object {
        val allKeywords = arrayOf("алиса", "найди", "сериал", "фильм", "включи", "смотреть", "посмотреть", "мне")
        val actionFindKeywords = arrayOf("найди")
        val actionWatchKeywords = arrayOf("включи", "смотреть", "посмотреть")

        val actionFind = "find"
        val actionWatch = "watch"

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
                    return actionWatch
                }
            }

            for (wordFind in actionFindKeywords) {
                if (query.contains(wordFind)) {
                    return actionFind
                }
            }

            return actionFind
        }

        private fun removePunctuation(s: String): String {
            val builder = StringBuilder()
            for (c in s.toCharArray())
                if (Character.isLetterOrDigit(c) || Character.isWhitespace(c))
                    builder.append(if (Character.isLowerCase(c)) c else Character.toLowerCase(c))
            return builder.toString()
        }
    }
}