package org.usfirst.frc.team694.client;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer<T> {
	enum ClipStatus {
		PLAYING, PAUSED, STOPPED // Basically reset
	}

	private HashMap<T, AudioGroup> groupmap;

	public AudioPlayer() {
		groupmap = new HashMap<T, AudioGroup>();
	}

	public void addAudioClip(T key, String fpath) {
		try {
			groupmap.put(key, new AudioGroup(fpath));
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			System.out.println("You done goofed when adding the audio file:");
			e.printStackTrace();
		}
	}

	public void startAudio(T key) {
		try {
			groupmap.get(key).play();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			System.out.println("Bad time starting the audio");
			e.printStackTrace();
		}
	}

	public void pauseAudio(T key) {
		groupmap.get(key).pause();
	}

	public void resumeAudio(T key) {
		try {
			groupmap.get(key).resume();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			System.out.println("Bad time pausing audio");
			e.printStackTrace();
		}
	}

	public void stopAudio(T key) {
		groupmap.get(key).stop();
	}

	public ClipStatus getStatus(T key) {
		return groupmap.get(key).getStatus();
	}

	public String getStatusString(T key) {
		return groupmap.get(key).getStatusString();
	}

	private class AudioGroup {
		long currentFrame;

		Clip clip;

		ClipStatus status;

		AudioInputStream audioInputStream;
		String filePath;

		public AudioGroup(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			this.filePath = filePath;

			System.out.println("fpath: " + filePath);
			status = ClipStatus.STOPPED;

			audioInputStream = buildAudioInputStream(filePath);
			clip = AudioSystem.getClip();

			clip.open(audioInputStream);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}

		public void play() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			if (status == ClipStatus.PLAYING)
				return;

			if (status == ClipStatus.PAUSED) {
				resume();
				return;
			}
			clip.setMicrosecondPosition(0);
			clip.start();
			status = ClipStatus.PLAYING;
		}

		public void pause() {
			if (status != ClipStatus.PAUSED) {
				clip.stop();
				currentFrame = clip.getMicrosecondPosition();
				status = ClipStatus.PAUSED;
			}
		}

		public void resume() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			if (status == ClipStatus.PLAYING) {
				System.out.println("Clip already playing...");
				return;
			}
			status = ClipStatus.PLAYING;
			clip.close();
			resetAudioStream();
			clip.setMicrosecondPosition(currentFrame);
			play();

		}

		public void stop() {
			currentFrame = 0L;
			clip.stop();
			status = ClipStatus.STOPPED;
		}

		public ClipStatus getStatus() {
			return status;
		}

		public String getStatusString() {
			switch (status) {
				case PLAYING:
					return "PLAYING";
				case PAUSED:
					return "PAUSED";
				case STOPPED:
					return "STOPPED";
				default:
					return "unknown";
			}
		}

		private AudioInputStream buildAudioInputStream(String fPath) throws UnsupportedAudioFileException, IOException {
			URL file = this.getClass().getClassLoader().getResource(fPath);
			return AudioSystem.getAudioInputStream(file);

		}

		private void resetAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			audioInputStream = buildAudioInputStream(filePath);
			clip.open(audioInputStream);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}

	}

}
