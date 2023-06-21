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

import javax.sound.sampled.Clip;

import entities.Player;

public class Sound {
	
	public static final Sound musicBackground = new Sound("/music.wav", true);
	public static final Sound hurtEffect = new  Sound("/hurt.wav", false);
	public static final Sound shotEffect = new  Sound("/shotgf1b.wav", false);
	public static final Sound enemyEffect = new  Sound("/umph-47201.wav", false);
	public static final Sound ammoEffect = new  Sound("/arme.wav", false);
	
	
	InputStream raw; 
	
    AudioInputStream in;
    AudioFormat decodedFormat;
    AudioInputStream din;
    AudioFormat baseFormat;
    SourceDataLine line;
    private boolean loop;
    private BufferedInputStream stream;
    private final int BUFFER_SIZE = 128000;
    // private ByteArrayInputStream stream;
    private boolean running = false;
    private BufferedInputStream myStream;
    private Clip clip;

    /**
     * recreate the stream
     * 
     */
    /*public void reset() {
        try {
        	//System.out.println("RAW:");
        	//System.out.println(AudioSystem.getAudioFileFormat(raw).getByteLength());
        	System.out.println("STREAM:");
        	System.out.println(AudioSystem.getAudioFileFormat(stream).getByteLength());
        	
        	raw.mark(-1); 
        	//raw.reset();
        	//stream.reset();
            //in = AudioSystem.getAudioInputStream(stream);
            in = AudioSystem.getAudioInputStream(raw);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            line = getLine(decodedFormat);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

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
    	myStream = new BufferedInputStream(getClass().getResourceAsStream(filename));
    	
    	try {
			in = AudioSystem.getAudioInputStream(myStream);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	din = null;
        
        baseFormat = in.getFormat();

        decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);

        din = AudioSystem.getAudioInputStream(decodedFormat, in);
        
        try {
			clip = AudioSystem.getClip();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
			clip.open(din);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    /*Sound(String filename) {
        //this.loop = false;
        try {
            raw = Sound.class.getResourceAsStream(filename);
            
            //raw.mark(raw.available()+1); 
            
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
    }*/

    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }

    public synchronized void play_() {

        try {
        	new Thread() {
        		public void run() {
        			running = true;
		            //boolean firstTime = true;
		            //while (firstTime || loop) {		            	
        			while (running) {
		            	
		            	//din.mark(1);
		                //firstTime = false;
		                byte[] data = new byte[BUFFER_SIZE];
		
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
		                        //if (nBytesRead != -1)
		                        if (nBytesRead >= 0)
		                            line.write(data, 0, nBytesRead);
		                    }
		                    
		                    //if (firstTime) {
		                    if (running) {
								//stream.mark(1);
								//stream.reset();
								//Sound.musicBackground.play();
								//play();
								//loop = false;
								//firstTime = false;
								//play();
								/*try {
									System.out.print(din.read());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/
								//reset();
		                    }
	                	}
	                }
		
                    line.drain();
                    //line.stop();
                    line.close();
		                    
		                /*    reset();
		                }*/
		            //}
        		}
        	}.start();
        } catch(Throwable e) {}
        	/*} catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    
    
    public synchronized void AudioPlayer() {
        new Thread() {
          public void run() {
            try {

              clip = AudioSystem.getClip();
              
              ////BufferedInputStream myStream = new BufferedInputStream(getClass().getResourceAsStream("/music.wav"));  
              ////BufferedInputStream myStream = new BufferedInputStream(Sound.class.getResourceAsStream("/music.wav"));
              //AudioInputStream audio2 = AudioSystem.getAudioInputStream(myStream);
              
              ////AudioInputStream inputStream = AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream("/music.wav"));         
              
              
              //audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
              //BufferedInputStream bufferedInputStream = new BufferedInputStream(audioInputStream);
              //AudioInputStream audioInputStream = new AudioInputStream(audio2, audio2.getFormat(), audio2.getFrameLength());
              
              //audioInputStream = convertToPCM(audioInputStream);
              
              //clip.open(audio2);
              clip.open(din);
              //clip.open(audioInputStream);
              
              
            } catch (Exception e) {
              System.err.println(e.getMessage());
              e.printStackTrace();
            }
          }
        }.start();  
      }     
    
    public void play(){
        
    	clip.setFramePosition(0);  // Must always rewind!
        clip.start();
    }
    public void loop(){
    	
    	clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop(){
    		
            clip.stop();
        }
    
    
    private static AudioInputStream convertToPCM(AudioInputStream audioInputStream)
    {
        AudioFormat m_format = audioInputStream.getFormat();

        if ((m_format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) &&
            (m_format.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED))
        {
            AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                m_format.getSampleRate(), 16,
                m_format.getChannels(), m_format.getChannels() * 2,
                m_format.getSampleRate(), m_format.isBigEndian());
            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
    }

    return audioInputStream;
}
    
    /*public void stopSound() {
        this.nBytesRead = false;
        if (line != null) {
        	line.stop();
        	line.drain();
        	line.close();
        }
    }*/

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
