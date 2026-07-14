package com.ziyin.island.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ziyin.island.data.ProgressRepository
import com.ziyin.island.data.ProgressSnapshot
import com.ziyin.island.content.CharacterLesson
import com.ziyin.island.content.Curriculum
import com.ziyin.island.content.PinyinLesson
import com.ziyin.island.content.StoryLesson
import com.ziyin.island.speech.AudioPracticeManager
import com.ziyin.island.speech.AudioPracticeState
import com.ziyin.island.speech.StandardAudioPlayer
import com.ziyin.island.speech.StandardAudioState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class AppScreen {
    ONBOARDING,
    HOME,
    SOUND_FOREST,
    PINYIN_MAP,
    PINYIN_LESSON,
    TONE_GAME,
    TRACING,
    LITERACY_TOWN,
    CHARACTER_LESSON,
    REVIEW_GAMES,
    STORY_THEATER,
    STORY_READER,
    ACHIEVEMENTS,
    PARENT,
}

class ZiyinViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProgressRepository(application)
    private val audioPracticeManager = AudioPracticeManager(application)
    private val standardAudioPlayer = StandardAudioPlayer(application)

    private val _screen = MutableStateFlow(AppScreen.ONBOARDING)
    val screen: StateFlow<AppScreen> = _screen.asStateFlow()

    private val _selectedPinyinLesson = MutableStateFlow(Curriculum.pinyinLessons.first())
    val selectedPinyinLesson: StateFlow<PinyinLesson> = _selectedPinyinLesson.asStateFlow()

    private val _selectedCharacter = MutableStateFlow(Curriculum.characterLessons.first())
    val selectedCharacter: StateFlow<CharacterLesson> = _selectedCharacter.asStateFlow()

    private val _selectedStory = MutableStateFlow(Curriculum.stories.first())
    val selectedStory: StateFlow<StoryLesson> = _selectedStory.asStateFlow()

    private val _tracingSymbol = MutableStateFlow("a")
    val tracingSymbol: StateFlow<String> = _tracingSymbol.asStateFlow()
    private var tracingReturnScreen: AppScreen = AppScreen.PINYIN_LESSON

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    val progress: StateFlow<ProgressSnapshot> = repository.progress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressSnapshot(),
    )

    val audioPracticeState: StateFlow<AudioPracticeState> = audioPracticeManager.state
    val audioLevel: StateFlow<Int> = audioPracticeManager.amplitudeLevel
    val standardAudioState: StateFlow<StandardAudioState> = standardAudioPlayer.state

    init {
        viewModelScope.launch {
            _screen.value = if (repository.progress.first().onboardingCompleted) AppScreen.HOME else AppScreen.ONBOARDING
        }
    }

    fun finishOnboarding() {
        viewModelScope.launch {
            repository.completeOnboarding()
            _screen.value = AppScreen.HOME
        }
    }

    fun openLesson() {
        openPinyinMap()
    }

    fun openPinyinMap() { resetAudio(); _screen.value = AppScreen.PINYIN_MAP }
    fun openSoundForest() { resetAudio(); _screen.value = AppScreen.SOUND_FOREST }
    fun openPinyinLesson(lesson: PinyinLesson) { resetAudio(); _selectedPinyinLesson.value = lesson; _screen.value = AppScreen.PINYIN_LESSON }
    fun openToneGame() { resetAudio(); _screen.value = AppScreen.TONE_GAME }
    fun openTracing() {
        resetAudio()
        _tracingSymbol.value = _selectedPinyinLesson.value.symbols.firstOrNull().orEmpty()
        tracingReturnScreen = AppScreen.PINYIN_LESSON
        _screen.value = AppScreen.TRACING
    }
    fun openCharacterTracing() {
        resetAudio()
        _tracingSymbol.value = _selectedCharacter.value.character
        tracingReturnScreen = AppScreen.CHARACTER_LESSON
        _screen.value = AppScreen.TRACING
    }
    fun finishTracing() { _screen.value = tracingReturnScreen }
    fun openLiteracyTown() { resetAudio(); _screen.value = AppScreen.LITERACY_TOWN }
    fun openCharacterLesson(lesson: CharacterLesson) { resetAudio(); _selectedCharacter.value = lesson; _screen.value = AppScreen.CHARACTER_LESSON }
    fun openReviewGames() { resetAudio(); _screen.value = AppScreen.REVIEW_GAMES }
    fun openStoryTheater() { resetAudio(); _screen.value = AppScreen.STORY_THEATER }
    fun openStory(story: StoryLesson) { _selectedStory.value = story; _screen.value = AppScreen.STORY_READER }
    fun openAchievements() { resetAudio(); _screen.value = AppScreen.ACHIEVEMENTS }

    fun openParentCenter() {
        _screen.value = AppScreen.PARENT
    }

    fun goHome() {
        resetAudio()
        _screen.value = AppScreen.HOME
    }

    fun goBackFromLesson() { resetAudio(); _screen.value = AppScreen.PINYIN_MAP }
    fun goBackToPinyinLesson() { resetAudio(); _screen.value = AppScreen.PINYIN_LESSON }
    fun goBackFromCharacter() { resetAudio(); _screen.value = AppScreen.LITERACY_TOWN }
    fun goBackFromStory() { _screen.value = AppScreen.STORY_THEATER }

    fun showComingSoon(area: String) {
        _message.value = "${area}正在准备新内容，先去拼音工坊探索吧！"
    }

    fun clearMessage() {
        _message.value = null
    }

    fun exploreLetter(letter: String) {
        viewModelScope.launch { repository.markLetterExplored(letter) }
    }

    fun completeFirstLesson() {
        completeCurrentPinyinLesson()
    }

    fun completeCurrentPinyinLesson() {
        viewModelScope.launch {
            repository.markLessonComplete(_selectedPinyinLesson.value.id)
            resetAudio()
            _screen.value = AppScreen.PINYIN_MAP
            _message.value = "太棒了！这一课已经保存。"
        }
    }

    fun completeCurrentCharacter() {
        viewModelScope.launch {
            repository.markCharacterKnown(_selectedCharacter.value.character)
            _screen.value = AppScreen.LITERACY_TOWN
            _message.value = "你又认识了一个汉字！"
        }
    }

    fun completeCurrentStory() {
        viewModelScope.launch {
            repository.markStoryComplete(_selectedStory.value.id)
            _screen.value = AppScreen.STORY_THEATER
            _message.value = "故事读完啦，休息一下眼睛吧。"
        }
    }

    fun setDailyLimit(minutes: Int) {
        viewModelScope.launch {
            repository.setDailyLimit(minutes)
            _message.value = "每天学习时间已经设为 $minutes 分钟。"
        }
    }

    fun startRecording() {
        standardAudioPlayer.stop()
        audioPracticeManager.startRecording()
    }

    fun stopRecording() = audioPracticeManager.stopRecording()

    fun playLatestRecording() = audioPracticeManager.playLatestRecording()

    fun resetRecording() = audioPracticeManager.reset()

    fun reportMicrophonePermissionDenied() = audioPracticeManager.reportPermissionDenied()

    fun playStandardAudio(target: String) = standardAudioPlayer.play(target)

    fun stopStandardAudio() = standardAudioPlayer.stop()

    private fun resetAudio() {
        audioPracticeManager.reset()
        standardAudioPlayer.stop()
    }

    override fun onCleared() {
        audioPracticeManager.release()
        standardAudioPlayer.release()
        super.onCleared()
    }
}
