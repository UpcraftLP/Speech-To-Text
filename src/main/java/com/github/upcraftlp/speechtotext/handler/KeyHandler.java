package com.github.upcraftlp.speechtotext.handler;

import com.github.upcraftlp.glasspane.client.ClientUtil;
import com.github.upcraftlp.speechtotext.STT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.*;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = STT.MODID, value = Side.CLIENT)
public class KeyHandler {

    private static final KeyBinding KEY_ACTIVATE_VOICE = ClientUtil.createKeyBinding("activate", Keyboard.KEY_ADD, "voice"); //numpad +; DO NOT USE CONTROL KEYS, THEY DO NOT WORK
    private static boolean pressed = false;
    private static volatile VoiceListenerThread t;

    @SubscribeEvent
    public static synchronized void onInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if(Minecraft.getMinecraft().currentScreen instanceof GuiChat && Keyboard.getEventKey() == KEY_ACTIVATE_VOICE.getKeyCode()) {
            boolean state = Keyboard.getEventKeyState(); //true: button down
            if(state != pressed) {
                pressed = state;
            }
            event.setCanceled(true);
            if(state && (t == null || !t.isRunning())) {
                t = new VoiceListenerThread();
                t.start();
            }
        }
    }
}
