package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.AudioMode
import com.example.audio.AudioPlayerState
import com.example.audio.GitaAudioEngine
import com.example.data.GitaRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("॥ श्रीमद्भगवद्गीता ॥", appName)
  }

  @Test
  fun `validate gita repository dataset`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = GitaRepository(context)

    val chapters = repository.loadChapters()
    assertEquals(18, chapters.size)

    val verses = repository.loadVerses()
    assertTrue(verses.size >= 700)

    val validation = repository.validateDataset()
    assertTrue(validation.isValid)
    assertEquals(18, validation.chapterCount)
    assertEquals(0, validation.duplicateIdCount)
    assertEquals(0, validation.emptySanskritCount)
    assertEquals(0, validation.emptyHindiCount)

    // Test specific well-known verse lookup
    val karmaVerse = repository.getVerseById("BG2.47")
    assertNotNull(karmaVerse)
    assertTrue(karmaVerse!!.sanskrit.contains("कर्मण्येवाधिकारस्ते"))
  }

  @Test
  fun `test audio engine state machine without crashing`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val audioEngine = GitaAudioEngine(context)

    // 1. Initial State
    assertFalse(audioEngine.isPlaying.value)
    assertFalse(audioEngine.isPaused.value)
    assertNull(audioEngine.errorMessage.value)

    // 2. Play Sanskrit verse
    val sampleSanskrit = "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन।\nमा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि।।2.47।।"
    audioEngine.playSanskritVerse("BG2.47", sampleSanskrit)
    assertEquals(AudioMode.SANSKRIT, audioEngine.currentMode.value)

    // 3. Pause
    audioEngine.pause()
    // Pausing stops active speech safely
    assertFalse(audioEngine.isPlaying.value)

    // 4. Resume
    audioEngine.resume()

    // 5. Play Hindi meaning
    val sampleHindi = "कर्म करने में ही तुम्हारा अधिकार है, फल में कभी नहीं।"
    audioEngine.playHindiMeaning("BG2.47", 2, 47, sampleHindi)
    assertEquals(AudioMode.HINDI, audioEngine.currentMode.value)

    // 6. Voice Gender switching (Male -> Female -> Male)
    audioEngine.setGender("male")
    assertEquals("male", audioEngine.voiceGender.value)
    audioEngine.setGender("female")
    assertEquals("female", audioEngine.voiceGender.value)

    // 7. Repeated Play & Fast Switching without crash or overlap
    audioEngine.playSanskritVerse("BG1.1", "धर्मक्षेत्रे कुरुक्षेत्रे समवेता युयुत्सवः।")
    audioEngine.playSanskritVerse("BG1.2", "दृष्ट्वा तु पाण्डवानीकं व्यूढं दुर्योधनस्तदा।")
    assertEquals(AudioMode.SANSKRIT, audioEngine.currentMode.value)

    // 8. Madhurata toggle & Tanpura drone volume
    audioEngine.setMadhurataEnabled(true)
    assertTrue(audioEngine.isMadhurataEnabled.value)
    audioEngine.setTanpuraVolume(0.40f)
    assertEquals(0.40f, audioEngine.tanpuraVolume.value, 0.01f)
    audioEngine.setMadhurataEnabled(false)
    assertFalse(audioEngine.isMadhurataEnabled.value)

    // 9. Stop and reset
    audioEngine.stop()
    assertFalse(audioEngine.isPlaying.value)
    assertFalse(audioEngine.isPaused.value)
    assertNull(audioEngine.currentPlayingId.value)

    audioEngine.release()
  }
}
