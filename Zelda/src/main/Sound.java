package main;

/*import java.applet.Applet;
import java.applet.AudioClip;*/

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
	
	public static final Sound musicBackground = new Sound("/music.wav", true);
	public static final Sound hurtEffect = new  Sound("/hurt.wav", false);
	
	InputStream raw; 
	
    AudioInputStream in;
    AudioFormat decodedFormat;
    AudioInputStream din;
    AudioFormat baseFormat;
    SourceDataLine line;
    private boolean loop;
    private BufferedInputStream stream;
    // private ByteArrayInputStream stream;

    /**
     * recreate the stream
     * 
     */
    public void reset() {
        try {
        	raw.reset();
        	//stream.reset();
            //in = AudioSystem.getAudioInputStream(stream);
            in = AudioSystem.getAudioInputStream(raw);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            line = getLine(decodedFormat);
            
            		
            				

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            line.close();
            din.close();
            in.close();
        } catch (IOException e) {
        }
    }

    Sound(String filename, boolean loop) {
        this(filename);
        this.loop = loop;
    }

    Sound(String filename) {
        this.loop = false;
        try {
            raw = Sound.class.getResourceAsStream(filename);
            
            raw.mark(raw.available()+1); 
            
            stream = new BufferedInputStream(raw);
            
            // ByteArrayOutputStream out = new ByteArrayOutputStream();
            // byte[] buffer = new byte[1024];
            // int read = raw.read(buffer);
            // while( read > 0 ) {
            // out.write(buffer, 0, read);
            // read = raw.read(buffer);
            // }
            // stream = new ByteArrayInputStream(out.toByteArray());

            in = AudioSystem.getAudioInputStream(stream);
            din = null;

            if (in != null) {
                baseFormat = in.getFormat();

                decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED, baseFormat
                                .getSampleRate(), 16, baseFormat.getChannels(),
                        baseFormat.getChannels() * 2, baseFormat
                                .getSampleRate(), false);

                din = AudioSystem.getAudioInputStream(decodedFormat, in);
                line = getLine(decodedFormat);
            }
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private SourceDataLine getLine(AudioFormat audioFormat)
            throws LineUnavailableException {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }

    public void play() {

        try {
        	new Thread() {
        		public void run() {
		            boolean firstTime = true;
		            while (firstTime || loop) {
		
		                firstTime = false;
		                byte[] data = new byte[4096];
		
		                if (line != null) {
		
		                    line.start();
		                    int nBytesRead = 0;
		
		                    while (nBytesRead != -1) {
		                        try {
									nBytesRead = din.read(data, 0, data.length);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		                        if (nBytesRead != -1)
		                            line.write(data, 0, nBytesRead);
		                    }
		
		                    line.drain();
		                    line.stop();
		                    line.close();
		                    
		                    reset();
		                }
		            }
        		}
        	}.start();
        } catch(Throwable e) {}
        	/*} catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}

/*@SuppressWarnings("removal")
public class Sound {

	private AudioClip clip;
	
	public static final Sound musicBackground = new Sound("/music.wav");
	public static final Sound hurtEffect = new  Sound("/hurt.wav");
	
	@SuppressWarnings("deprecation")
	private Sound(String name) {
		try {
			clip = Applet.newAudioClip(Sound.class.getResource(name));
		}catch(Throwable e) {}
	}
	
	public void play() {
		try {
			new Thread() {
				@SuppressWarnings("deprecation")
				public void run() {
					clip.play();
				}
			}.start();
		}catch(Throwable e) {}
	}
	
	public void loop() {
		try {
			new Thread() {
				@SuppressWarnings("deprecation")
				public void run() {
					clip.loop();
				}
			}.start();
		}catch(Throwable e) {}
	
	}
	
}*/
