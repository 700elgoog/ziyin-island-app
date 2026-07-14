package com.ziyin.island.speech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TencentPinyinReferenceMapperTest {
    @Test
    public void mapsCurrentLessonTonesToTencentNumericReferences() {
        assertEquals("a1", TencentPinyinReferenceMapper.toNumeric("ā"));
        assertEquals("a2", TencentPinyinReferenceMapper.toNumeric("á"));
        assertEquals("a3", TencentPinyinReferenceMapper.toNumeric("ǎ"));
        assertEquals("a4", TencentPinyinReferenceMapper.toNumeric("à"));
        assertEquals("o3", TencentPinyinReferenceMapper.toNumeric("ǒ"));
        assertEquals("e4", TencentPinyinReferenceMapper.toNumeric("è"));
    }

    @Test
    public void supportsFutureSingleVowelsAndRejectsUnknownReferences() {
        assertEquals("i2", TencentPinyinReferenceMapper.toNumeric("í"));
        assertEquals("u4", TencentPinyinReferenceMapper.toNumeric("ù"));
        assertEquals("v3", TencentPinyinReferenceMapper.toNumeric("ǚ"));
        assertNull(TencentPinyinReferenceMapper.toNumeric("a"));
        assertNull(TencentPinyinReferenceMapper.toNumeric("hao3"));
    }
}
