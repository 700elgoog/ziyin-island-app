package com.ziyin.island.speech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Map;
import org.junit.Test;

public class StandardAudioPackTest {
    @Test
    public void acceptsCompleteVersionOnePack() {
        Map<String, StandardAudioEntry> entries = Collections.singletonMap(
                "ā", new StandardAudioEntry("ā", "a1", "audio/pinyin/cmn-a1.mp3"));
        StandardAudioPack pack = new StandardAudioPack(1, "test-pack", "prototype-only", true, entries);

        assertNull(pack.validationError());
    }

    @Test
    public void rejectsUnsupportedOrEmptyPacks() {
        StandardAudioPack unsupported = new StandardAudioPack(
                2, "test-pack", "prototype-only", true, Collections.emptyMap());
        StandardAudioPack empty = new StandardAudioPack(
                1, "test-pack", "prototype-only", true, Collections.emptyMap());

        assertEquals("不支持的标准音资源版本", unsupported.validationError());
        assertEquals("标准音资源包中没有音频", empty.validationError());
    }
}
