package com.ziyin.island.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.learningDataStore by preferencesDataStore(name = "learning_progress")

data class ProgressSnapshot(
    val exploredLetters: Set<String> = emptySet(),
    val completedLessons: Set<String> = emptySet(),
    val dailyLimitMinutes: Int = 15,
    val knownCharacters: Set<String> = emptySet(),
    val completedStories: Set<String> = emptySet(),
    val onboardingCompleted: Boolean = false,
) {
    constructor(
        exploredLetters: Set<String>,
        completedLessons: Set<String>,
        dailyLimitMinutes: Int,
    ) : this(exploredLetters, completedLessons, dailyLimitMinutes, emptySet(), emptySet(), false)

    val stars: Int get() = exploredLetters.size + completedLessons.size * 2 + knownCharacters.size + completedStories.size * 2
}

class ProgressRepository(private val context: Context) {
    private object Keys {
        val exploredLetters = stringSetPreferencesKey("explored_letters")
        val completedLessons = stringSetPreferencesKey("completed_lessons")
        val dailyLimitMinutes = intPreferencesKey("daily_limit_minutes")
        val knownCharacters = stringSetPreferencesKey("known_characters")
        val completedStories = stringSetPreferencesKey("completed_stories")
        val onboardingCompleted = booleanPreferencesKey("onboarding_completed")
    }

    val progress: Flow<ProgressSnapshot> = context.learningDataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map(::toSnapshot)

    suspend fun markLetterExplored(letter: String) {
        context.learningDataStore.edit { preferences ->
            val updated = preferences[Keys.exploredLetters].orEmpty() + letter
            preferences[Keys.exploredLetters] = updated
        }
    }

    suspend fun markLessonComplete(lessonId: String) {
        context.learningDataStore.edit { preferences ->
            val updated = preferences[Keys.completedLessons].orEmpty() + lessonId
            preferences[Keys.completedLessons] = updated
        }
    }

    suspend fun setDailyLimit(minutes: Int) {
        context.learningDataStore.edit { preferences ->
            preferences[Keys.dailyLimitMinutes] = minutes.coerceIn(10, 30)
        }
    }

    suspend fun markCharacterKnown(character: String) {
        context.learningDataStore.edit { preferences ->
            preferences[Keys.knownCharacters] = preferences[Keys.knownCharacters].orEmpty() + character
        }
    }

    suspend fun markStoryComplete(storyId: String) {
        context.learningDataStore.edit { preferences ->
            preferences[Keys.completedStories] = preferences[Keys.completedStories].orEmpty() + storyId
        }
    }

    suspend fun completeOnboarding() {
        context.learningDataStore.edit { preferences -> preferences[Keys.onboardingCompleted] = true }
    }

    private fun toSnapshot(preferences: Preferences): ProgressSnapshot = ProgressSnapshot(
        exploredLetters = preferences[Keys.exploredLetters].orEmpty(),
        completedLessons = preferences[Keys.completedLessons].orEmpty(),
        dailyLimitMinutes = preferences[Keys.dailyLimitMinutes] ?: 15,
        knownCharacters = preferences[Keys.knownCharacters].orEmpty(),
        completedStories = preferences[Keys.completedStories].orEmpty(),
        onboardingCompleted = preferences[Keys.onboardingCompleted] ?: false,
    )
}
