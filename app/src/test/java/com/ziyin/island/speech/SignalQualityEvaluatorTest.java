package com.ziyin.island.speech;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SignalQualityEvaluatorTest {
    private final SignalQualityEvaluator evaluator = new SignalQualityEvaluator();

    @Test
    public void shortRecordingGetsRetryGuidance() {
        PracticeFeedback feedback = evaluator.evaluate(300, 5000);
        assertEquals(PracticeFeedbackKind.TOO_SHORT, feedback.getKind());
    }

    @Test
    public void softRecordingGetsVolumeGuidance() {
        PracticeFeedback feedback = evaluator.evaluate(1000, 200);
        assertEquals(PracticeFeedbackKind.TOO_SOFT, feedback.getKind());
    }

    @Test
    public void clearRecordingIsReadyForListening() {
        PracticeFeedback feedback = evaluator.evaluate(1000, 5000);
        assertEquals(PracticeFeedbackKind.READY, feedback.getKind());
    }
}

