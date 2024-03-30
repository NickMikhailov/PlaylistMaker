package com.example.playlistmaker.main.domain.models

class GrammarUtil {
    companion object{
        fun getEnding(count: Int, nominative: String, genitiveSingle: String, genitivePlural: String): String {
            return when {
                (count % 100) in 10..20 || (count % 10) in 5..9 || (count % 10) == 0 -> " $genitivePlural"
                (count % 10) == 1 -> " $nominative"
                else -> " $genitiveSingle"
            }
        }
        fun getEnding(count: Long, nominative: String, genitiveSingle: String, genitivePlural: String): String {
            return when {
                (count % 100) in 10..20 || (count % 10) in 5..9 || (count % 10) == 0L -> " $genitivePlural"
                (count % 10) == 1L -> " $nominative"
                else -> " $genitiveSingle"
            }
        }
    }

}