import javax.sound.sampled.*;
import java.net.URL;

public abstract class Sound{
    private static Clip soundBackground;//Фоновый звук, проигрывающийся пока, игрок ничего не выбрал
    private static Clip sound;//Обычный звук
    public static void playBackgroundSound(String selectedSound, byte number){
        try{soundBackground.close();}
        catch (NullPointerException ignored){}
        if(number > 5)
            try{
                URL file = Sound.class.getResource("sounds/" + selectedSound + ".wav");
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
                soundBackground = AudioSystem.getClip();
                soundBackground.open(audioInput);
                soundBackground.start();
                soundBackground.loop(Clip.LOOP_CONTINUOUSLY);
            }
            catch (Exception ignored){}
    }
    public static void stopBackgroundSound(){
        try{soundBackground.close();}
        catch (NullPointerException ignored){}
    }
    public static void restartBackgroundSound(){
        try{
            soundBackground.stop();
            soundBackground.setMicrosecondPosition(0);
            soundBackground.start();
        }
        catch (NullPointerException ignored){}
    }
    public static void playSound(String selectedSound){
        try{
            URL file = Sound.class.getResource("sounds/" + selectedSound + ".wav");
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
            sound = AudioSystem.getClip();
            sound.open(audioInput);
            sound.start();
        }catch (Exception ignored){}
    }
    public static void trueAnswer(byte number, byte checkpoint){
        try{soundBackground.close();}
        catch (NullPointerException ignored){}
        if(number != checkpoint)
            switch ((number - 1) / 5){
                case 0:
                    playSound("answer.true.1-5");
                    break;
                case 1:
                    playSound("answer.true.6-10");
                    break;
                default:
                    playSound("answer.true.11-14");
            }
        else
            playSound("answer.true.checkpoint");
    }
    public static void falseAnswer(byte number){
        if (number <= 5)
            playSound("answer.false.1-5");
        if (number >= 6 && number <= 14)
            playSound("answer.false.6-14");
        if (number == 15)
            playSound("answer.false.15");
    }
    public static void stopSound(){
        try{sound.close();}
        catch (NullPointerException ignored){}
    }
}
