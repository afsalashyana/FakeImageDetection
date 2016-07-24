package com.qburst.ai.fake_image_detection.neural_network.thread_sync;

public interface ThreadCompleteListener {
    void notifyOfThreadComplete(final Thread thread);
}
