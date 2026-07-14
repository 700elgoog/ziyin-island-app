package com.ziyin.island.content;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

public class CurriculumCompletenessTest {
    @Test
    public void containsCompleteMvpCatalogWithUniqueIds() {
        List<PinyinLesson> pinyin = Curriculum.INSTANCE.getPinyinLessons();
        List<CharacterLesson> characters = Curriculum.INSTANCE.getCharacterLessons();
        List<StoryLesson> stories = Curriculum.INSTANCE.getStories();

        assertEquals(60, pinyin.size());
        assertEquals(300, characters.size());
        assertEquals(12, stories.size());
        assertEquals(24, Curriculum.INSTANCE.getRhymes().size());
        assertEquals(pinyin.size(), new HashSet<>(pinyin.stream().map(PinyinLesson::getId).collect(Collectors.toList())).size());
        assertEquals(characters.size(), new HashSet<>(characters.stream().map(CharacterLesson::getCharacter).collect(Collectors.toList())).size());
        assertEquals(stories.size(), new HashSet<>(stories.stream().map(StoryLesson::getId).collect(Collectors.toList())).size());
    }

    @Test
    public void everyContentItemHasChildFacingLearningMaterial() {
        for (PinyinLesson lesson : Curriculum.INSTANCE.getPinyinLessons()) {
            assertEquals(false, lesson.getTitle().isBlank());
            assertEquals(false, lesson.getSymbols().isEmpty());
            assertEquals(false, lesson.getMission().isBlank());
        }
        for (CharacterLesson lesson : Curriculum.INSTANCE.getCharacterLessons()) {
            assertEquals(false, lesson.getPinyin().isBlank());
            assertEquals(false, lesson.getMeaning().isBlank());
            assertEquals(false, lesson.getWords().isEmpty());
        }
        for (StoryLesson story : Curriculum.INSTANCE.getStories()) {
            assertEquals(3, story.getPages().size());
        }
    }
}
