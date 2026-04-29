package com.earn.chips.data.repository

import com.earn.chips.Supabase
import com.earn.chips.data.model.ProfileUpdate
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class UserRepository {

    suspend fun getProfile(userId: String): ProfileUpdate? {
        return try {
            Supabase.client.from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeSingleOrNull<ProfileUpdate>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateStreak(userId: String, profile: ProfileUpdate): ProfileUpdate? {
        val today = LocalDate.now()
        val lastActiveDate = profile.last_active_date?.let { LocalDate.parse(it) }

        val newStreak: Int
        val newLongestStreak: Int

        if (lastActiveDate == null) {
            // First time activity
            newStreak = 1
            newLongestStreak = maxOf(profile.longest_streak, 1)
        } else {
            val daysBetween = ChronoUnit.DAYS.between(lastActiveDate, today)
            when {
                daysBetween == 0L -> {
                    // Already active today, no change to streak
                    return profile
                }
                daysBetween == 1L -> {
                    // Consecutive day
                    newStreak = profile.current_streak + 1
                    newLongestStreak = maxOf(profile.longest_streak, newStreak)
                }
                else -> {
                    // Streak broken
                    newStreak = 1
                    newLongestStreak = profile.longest_streak
                }
            }
        }

        return try {
            val updatedProfile = profile.copy(
                current_streak = newStreak,
                longest_streak = newLongestStreak,
                last_active_date = today.toString()
            )

            Supabase.client.from("profiles").update(
                buildJsonObject {
                    put("current_streak", newStreak)
                    put("longest_streak", newLongestStreak)
                    put("last_active_date", today.toString())
                }
            ) {
                filter { eq("id", userId) }
            }
            updatedProfile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
