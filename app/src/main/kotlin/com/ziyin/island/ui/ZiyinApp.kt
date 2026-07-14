package com.ziyin.island.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ziyin.island.speech.AudioPracticeState
import com.ziyin.island.speech.GuideNarrator
import com.ziyin.island.speech.StandardAudioState

@Composable
fun ZiyinApp(viewModel: ZiyinViewModel = viewModel()) {
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val audioPracticeState by viewModel.audioPracticeState.collectAsStateWithLifecycle()
    val audioLevel by viewModel.audioLevel.collectAsStateWithLifecycle()
    val standardAudioState by viewModel.standardAudioState.collectAsStateWithLifecycle()
    val pinyinLesson by viewModel.selectedPinyinLesson.collectAsStateWithLifecycle()
    val characterLesson by viewModel.selectedCharacter.collectAsStateWithLifecycle()
    val story by viewModel.selectedStory.collectAsStateWithLifecycle()
    val tracingSymbol by viewModel.tracingSymbol.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val narrator = remember(context) { GuideNarrator(context.applicationContext) }
    val guideState by narrator.state.collectAsStateWithLifecycle()
    val guideSpeaker = remember(narrator) {
        object : GuideSpeaker {
            override fun speak(text: String, key: String) {
                viewModel.stopStandardAudio()
                narrator.speak(text, key)
            }
            override fun replay() {
                viewModel.stopStandardAudio()
                narrator.replay()
            }
            override fun stop() = narrator.stop()
        }
    }
    val pagePrompt = GuideCatalog.forScreen(screen, pinyinLesson, characterLesson, story, tracingSymbol)

    DisposableEffect(narrator) { onDispose { narrator.release() } }

    LaunchedEffect(screen, pagePrompt?.key) {
        pagePrompt?.let { narrator.speak(it.text, it.key) }
    }

    LaunchedEffect(audioPracticeState, standardAudioState) {
        val practiceBusy = audioPracticeState is AudioPracticeState.Recording ||
            audioPracticeState is AudioPracticeState.Playing ||
            audioPracticeState is AudioPracticeState.PreparingPlayback
        val standardBusy = standardAudioState is StandardAudioState.Loading ||
            standardAudioState is StandardAudioState.Playing
        if (practiceBusy || standardBusy) narrator.stop()
    }

    LaunchedEffect(message) {
        val current = message ?: return@LaunchedEffect
        narrator.speak(current, "message-${current.hashCode()}")
        snackbarHostState.showSnackbar(current)
        viewModel.clearMessage()
    }

    LaunchedEffect(guideState.error) {
        val error = guideState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(error)
    }

    CompositionLocalProvider(LocalGuideSpeaker provides guideSpeaker) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                when (screen) {
                AppScreen.ONBOARDING -> OnboardingScreen(viewModel::finishOnboarding)
                AppScreen.HOME -> HomeScreen(
                    progress = progress,
                    onOpenPinyin = viewModel::openPinyinMap,
                    onOpenSound = viewModel::openSoundForest,
                    onOpenLiteracy = viewModel::openLiteracyTown,
                    onOpenStories = viewModel::openStoryTheater,
                    onOpenReview = viewModel::openReviewGames,
                    onOpenAchievements = viewModel::openAchievements,
                    onOpenParent = viewModel::openParentCenter,
                )
                AppScreen.SOUND_FOREST -> SoundForestScreen(viewModel::goHome)
                AppScreen.PINYIN_MAP -> CourseMapScreen(
                    progress = progress,
                    onBack = viewModel::goHome,
                    onOpenLesson = viewModel::openPinyinLesson,
                    onOpenReview = viewModel::openReviewGames,
                )
                AppScreen.PINYIN_LESSON -> PinyinLearningScreen(
                    lesson = pinyinLesson,
                    audioState = audioPracticeState,
                    audioLevel = audioLevel,
                    standardAudioState = standardAudioState,
                    onBack = viewModel::goBackFromLesson,
                    onToneGame = viewModel::openToneGame,
                    onTracing = viewModel::openTracing,
                    onComplete = viewModel::completeCurrentPinyinLesson,
                    onStartRecording = { narrator.stop(); viewModel.startRecording() },
                    onStopRecording = viewModel::stopRecording,
                    onPlayRecording = { narrator.stop(); viewModel.playLatestRecording() },
                    onResetRecording = viewModel::resetRecording,
                    onPermissionDenied = viewModel::reportMicrophonePermissionDenied,
                    onPlayStandardAudio = { narrator.stop(); viewModel.playStandardAudio(it) },
                )
                AppScreen.TONE_GAME -> ToneGameScreen(
                    lesson = pinyinLesson,
                    onBack = viewModel::goBackToPinyinLesson,
                    onTracing = viewModel::openTracing,
                )
                AppScreen.TRACING -> TracingPracticeScreen(
                    symbol = tracingSymbol,
                    onBack = viewModel::finishTracing,
                    onDone = viewModel::finishTracing,
                )
                AppScreen.LITERACY_TOWN -> LiteracyTownScreen(
                    progress = progress,
                    onBack = viewModel::goHome,
                    onOpenCharacter = viewModel::openCharacterLesson,
                )
                AppScreen.CHARACTER_LESSON -> CharacterLearningScreen(
                    lesson = characterLesson,
                    onBack = viewModel::goBackFromCharacter,
                    onTracing = viewModel::openCharacterTracing,
                    onComplete = viewModel::completeCurrentCharacter,
                )
                AppScreen.REVIEW_GAMES -> ReviewGamesScreen(progress, viewModel::goHome)
                AppScreen.STORY_THEATER -> StoryTheaterScreen(progress, viewModel::goHome, viewModel::openStory)
                AppScreen.STORY_READER -> StoryReaderScreen(story, viewModel::goBackFromStory, viewModel::completeCurrentStory)
                AppScreen.ACHIEVEMENTS -> AchievementsScreen(progress, viewModel::goHome)
                AppScreen.PARENT -> ParentScreen(progress, viewModel::goHome, viewModel::setDailyLimit)
                }
            }
        }
    }
}
