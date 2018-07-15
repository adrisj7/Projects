package org.usfirst.frc.team694.client;

/**
 * This is the CLIENT-SIDE app, to be run on the laptop controlling the robot
 * 
 * This laptop grabs data from the robot so that external processes may use it
 */

public class Client {

	public enum Mode {
		NORMAL, // Normal
		GASGASGAS, // Speed up really fast
		DEJAVU // Skrrrrrrrr
	}

	enum Music {
		DEJAVU, GASGASGAS, SKRRT
	}

	static final int DEJA_VU_FADE_TIME = 4000;
	static final int SKRT_FADE_TIME = 1000;

	Mode mode;
	AudioPlayer<Music> player;

	int dejavuTimer = DEJA_VU_FADE_TIME + 1;
	int skrtTimer = SKRT_FADE_TIME + 1; // Start high so we don't skrtt

	int test = 0;

	public Client() {
		mode = Mode.NORMAL;
		player = new AudioPlayer<Music>();

		// System.out.println("f: " + new
		// File("music/dejavu.mp3").getAbsoluteFile().getAbsolutePath());

		player.addAudioClip(Music.DEJAVU, "dejavu.wav");
		player.addAudioClip(Music.GASGASGAS, "gasgasgas.wav");
		player.addAudioClip(Music.SKRRT, "skrrt.wav");

		mode = Mode.NORMAL;
	}

	public void loop() {

		while (true) {

			/* TEST */
//			System.out.print("status: " + player.getStatusString(Music.SKRRT) + "\t");
//
//			if ((int) (test / 2000) % 2 == 0) {
//				System.out.print("Switch");
//				mode = Mode.NORMAL;
//			} else {
//				System.out.print("");
//				mode = Mode.DEJAVU;
//			}
//
//			System.out.println();
//
//			test++;

			switch (mode) {
				case NORMAL:
					player.stopAudio(Music.GASGASGAS);
					break;
				case GASGASGAS:
					if (dejavuTimer > DEJA_VU_FADE_TIME) {
						player.startAudio(Music.GASGASGAS);
						player.stopAudio(Music.DEJAVU);
					}
					break;
				case DEJAVU:
					skrtTimer = 0;
					dejavuTimer = 0;
					player.stopAudio(Music.GASGASGAS);
					break;
			}

			if (skrtTimer > SKRT_FADE_TIME)
				player.pauseAudio(Music.SKRRT);
			else
				player.startAudio(Music.SKRRT);

			if (dejavuTimer > DEJA_VU_FADE_TIME)
				player.pauseAudio(Music.DEJAVU);
			else
				player.startAudio(Music.DEJAVU);

			skrtTimer++;
			dejavuTimer++;

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new Client().loop();
	}

}
