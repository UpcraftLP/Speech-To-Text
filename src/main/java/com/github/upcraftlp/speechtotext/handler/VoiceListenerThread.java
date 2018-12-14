package com.github.upcraftlp.speechtotext.handler;

import com.github.upcraftlp.speechtotext.STT;
import edu.cmu.sphinx.api.SpeechResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class VoiceListenerThread extends Thread {

    private static final BooleanSupplier CHECK_CHAT_OPEN = () -> Minecraft.getMinecraft().currentScreen instanceof GuiChat;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public VoiceListenerThread() {
        this.setName("STT Voice Recorder");
        this.setDaemon(true);
    }

    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void run() {
        STT.voiceRecognizer.startRecognition(true);
        STT.getLogger().trace("voice recognizer started");
        notifyPlayerOffThreadSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER, CHECK_CHAT_OPEN);
        running.set(true);
        try {
            while(Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
                SpeechResult result = STT.voiceRecognizer.getResult();
                if(result != null) {
                    String text = result.getHypothesis();
                    STT.getLogger().trace("User Voice Input: {}", text);
                    if(text.startsWith("slash")) text = text.replaceFirst("slash", "/"); //TODO more replacements
                    final String msg = text;
                    notifyPlayerOffThread(() -> Minecraft.getMinecraft().currentScreen.sendChatMessage(msg), CHECK_CHAT_OPEN);
                }
            }
        }
        finally {
            notifyPlayerOffThreadSound(SoundEvents.BLOCK_DISPENSER_FAIL, () -> true);
            STT.voiceRecognizer.stopRecognition();
            running.set(false);
            STT.getLogger().trace("voice recognizer stopped");
        }
    }

    private static void notifyPlayerOffThreadSound(SoundEvent sound, BooleanSupplier shouldExecute) {
        notifyPlayerOffThread(() -> Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(sound, 1.0F, 1.0F)), shouldExecute);
    }

    private static void notifyPlayerOffThread(Runnable task, BooleanSupplier shouldExecute) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if(shouldExecute.getAsBoolean()) task.run();
        });
    }
}
