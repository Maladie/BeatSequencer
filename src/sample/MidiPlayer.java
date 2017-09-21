package sample;

import javafx.scene.control.SingleSelectionModel;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class MidiPlayer {
    private boolean[] kickNotes;
    private boolean[] hhNotes;
    private boolean[] snareNotes;
    private int[] pianoNotes;
    private int tempo;
    private boolean end;

    private boolean mute;
    Synthesizer midiSynth;

    public MidiPlayer() {
        kickNotes = new boolean[32];
        hhNotes = new boolean[32];
        snareNotes = new boolean[32];
        pianoNotes = new int[32];
        for (int i = 0; i < 32; i++) {
            pianoNotes[i] = -1;
        }
        tempo = 120;

        try {
            midiSynth = MidiSystem.getSynthesizer();
            if (!midiSynth.isOpen()) {
                midiSynth.open();
            }
        } catch (MidiUnavailableException e) {
            throw new RuntimeException("Midi niedostępne");
        }
    }

    public synchronized void start() {
        end = false;
        Thread midiPlayerThread = new Thread(() -> {
            MidiChannel[] mChannels = midiSynth.getChannels();
            mChannels[0].programChange(0);
            while (!end) {
                for (int i = 0; i < 32 && !end; i++) {
                    beforeEach(i, tempo);
                    if (!mute) {
                        if (pianoNotes[i] != -1) {
                            mChannels[0].allNotesOff();
                            mChannels[0].noteOn(pianoNotes[i], 100);
                        }
                        if (kickNotes[i]) {
                            mChannels[9].noteOn(35, 100);
                        }
                        if (hhNotes[i]) {
                            mChannels[9].noteOn(42, 100);
                        }
                        if (snareNotes[i]) {
                            mChannels[9].noteOn(38, 100);
                        }
                    }
                    try {
                        Thread.sleep(7500 / tempo);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        midiPlayerThread.setDaemon(true);
        midiPlayerThread.start();
    }

    public void beforeEach(int i, int tempo) {}

    public boolean[] getKickNotes() {
        return kickNotes;
    }

    public boolean[] getHhNotes() {
        return hhNotes;
    }

    public boolean[] getSnareNotes() {
        return snareNotes;
    }

    public int[] getPianoNotes() {
        return pianoNotes;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public void stop() {
        this.end = true;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public void setHhNote(int i, boolean selected) {
        hhNotes[i] = selected;
    }

    public void setKDNote(int i, boolean selected) {
        kickNotes[i] = selected;
    }

    public void setSDNote(int i, boolean selected) {
        snareNotes[i] = selected;
    }

    public void setPianoNotes(int index, int value) {
        pianoNotes[index] = value;
    }

    public boolean isMute() { return mute; }
}
