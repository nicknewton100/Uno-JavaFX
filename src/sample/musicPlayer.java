//simple java program that plays sounds and music

package sample;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class musicPlayer
{
	Clip clip;
	AudioInputStream audioInputStream;

	//plays the music and make sure it loops over and over
	public musicPlayer(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		InputStream file = getClass().getResourceAsStream(filePath);
		InputStream bufferedFile = new BufferedInputStream(file);
		audioInputStream = AudioSystem.getAudioInputStream(bufferedFile);
		
		clip = AudioSystem.getClip();
		
		clip.open(audioInputStream);
		
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	//plays clip
	public void play()
	{
		clip.start();
	}

	public static void main(String[] args)
	{
      /*
		try
		{
			musicPlayer audioPlayer = new musicPlayer("music/casino.wav");
			
			audioPlayer.play();
		}
		
		catch (Exception ex)
		{
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
      */
	}

}