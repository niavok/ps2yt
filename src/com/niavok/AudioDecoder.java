/* 
 * This file is part of PS2YT
 *
 * Copyright (C) 2013 Frédéric Bertolus (Niavok)
 * 
 * PS2YT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.niavok;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class AudioDecoder {
	
	
	
	
	private IContainer container;
	private int audioStreamId;
	private IStreamCoder audioCoder;
	private String filename;




	public AudioDecoder(String filename) {
	
	    
	    this.filename = filename;
		container = IContainer.make();
	    
	    // Open up the container
	    if (container.open(filename, IContainer.Type.READ, null) < 0)
	      throw new IllegalArgumentException("could not open file: " + filename);
	    
	    // query how many streams the call to open found
	    int numStreams = container.getNumStreams();
	    
	    System.out.println("numStreams="+numStreams);
	    System.out.println("duration="+container.getDuration());
	    
	    
	    
	    
	    audioStreamId = -1;
	    audioCoder = null;
	    IStream audioStream = null;
	    for(int i = 0; i < numStreams; i++)
	    {
	      // Find the stream object
	      IStream stream = container.getStream(i);
	      // Get the pre-configured decoder that can decode this stream;
	      IStreamCoder coder = stream.getStreamCoder();
	    
	      System.out.println("audio frame size="+coder.getAudioFrameSize());
	      System.out.println("channels="+coder.getChannels());
	      System.out.println("sampleRate="+coder.getSampleRate());
	      System.out.println("timeBase="+coder.getTimeBase().toString());
	      
	      
	      if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
	      {
	        audioStreamId = i;
	        audioCoder = coder;
	        audioStream = stream;
	        break;
	      }
	    }
	    if (audioStreamId == -1)
	      throw new RuntimeException("could not find audio stream in container: "+filename);
	    
	    /*
	     * Now we have found the audio stream in this file.  Let's open up our decoder so it can
	     * do work.
	     */
	    if (audioCoder.open() < 0)
	      throw new RuntimeException("could not open audio decoder for container: "+filename);
	    
	    /*
	     * And once we have that, we ask the Java Sound System to get itself ready.
	     */
//	    openJavaSound(audioCoder);
	    
	    
	    
	    /*
	     * Now, we start walking through the container looking at each packet.
	     */
	    
	    
	     
//	    System.out.println("ret="+ret);
//	    System.out.println("getReadRetryCount() ="+container.getReadRetryCount() );
	    
	    
	    
	}
	
	public void decodeTo(VideoEncoder videoEncoder) {
		int packetCount = 0;
	    long timeCount = 0;
	    
	    IPacket packet = IPacket.make();
	    
	    while(true)
	    {
	    	int ret = container.readNextPacket(packet);
	    	if(ret < 0) {
	    		System.out.println("Error:" + IError.make(ret).getDescription());
//	    		break;
	    		
//	    		if(ret==-5) {
	    			break;
//	    		}
//	    		continue;
	    	}
	    	
	      /*
	       * Now we have a packet, let's see if it belongs to our audio stream
	       */
	      if (packet.getStreamIndex() == audioStreamId)
	      {
	    	  packetCount++;
	    	  
	    	  
//	    	  System.out.println("#### ------------");
//	    	  System.out.println("#### Packet timestamp="+packet.getTimeStamp());
//	    	  System.out.println("####        timeCount="+timeCount);
	    	  
	    	  
	    	  
	    	  packet.setTimeStamp(timeCount);
	    	  
//	    	  videoEncoder.writeAudioPacket(packet);
	    	  
	    	  
	    	  
//	    	  System.out.println("#### Packet formated timestamp="+packet.getFormattedTimeStamp());
//			  System.out.println("#### packet.getPosition()="+packet.getPosition());
	    			  
//	  		
	    	  
	    	  long tsOffset = 0;
	       
	    	  
	    	  
	        /*
	         * We allocate a set of samples with the same number of channels as the
	         * coder tells us is in this buffer.
	         * 
	         * We also pass in a buffer size (1024 in our example), although Xuggler
	         * will probably allocate more space than just the 1024 (it's not important why).
	         */
	        IAudioSamples inSamples = IAudioSamples.make(1024, audioCoder.getChannels());
	        
	        /*
	         * A packet can actually contain multiple sets of samples (or frames of samples
	         * in audio-decoding speak).  So, we may need to call decode audio multiple
	         * times at different offsets in the packet's data.  We capture that here.
	         */
	        int offset = 0;
	        int retval = 0;
	        /*
	         * Keep going until we've processed all data
	         */
	        while(offset < packet.getSize())
	        {
	          int bytesDecoded = audioCoder.decodeAudio(inSamples, packet, offset);
	          if (bytesDecoded < 0)
	            throw new RuntimeException("got error decoding audio in: " + filename);
	          offset += bytesDecoded;
	          /*
	           * Some decoder will consume data in a packet, but will not be able to construct
	           * a full set of samples yet.  Therefore you should always check if you
	           * got a complete set of samples from the decoder
	           */
	          
	          
	          if (inSamples.getTimeStamp() != Global.NO_PTS) {
	        	  inSamples.setTimeStamp(inSamples.getTimeStamp() - tsOffset);
	          }
	          tsOffset = inSamples.getTimeBase().rescale(timeCount, packet.getTimeBase());
	          inSamples.setTimeStamp(tsOffset);
	        
//	          samples.setTimeStamp((long) (timeCount * (packet.getTimeBase().getDouble() / samples.getTimeBase().getDouble() )) );
	          	          
//	          System.out.println("#### samples timebase="+samples.getTimeBase());
//	          System.out.println("#### samples timestamp="+samples.getTimeStamp());
//	          System.out.println("#### samples formated timestamp="+inSamples.getFormattedTimeStamp());
	          
	          
	          if (inSamples.isComplete())
	          {
	        	videoEncoder.writeAudioSamples(inSamples);  
//	            playJavaSound(samples);
	          }
	        }
	      }
	      else
	      {
	        /*
	         * This packet isn't part of our audio stream, so we just silently drop it.
	         */
	        ;
	      }
	      timeCount+=packet.getDuration();
	      
//	      ret = container.readNextPacket(packet); 
//	      System.out.println("ret="+ret);
	    }
	    
	    System.out.println("#### packetCount="+packetCount);
	    System.out.println("#### timeCount="+timeCount);
	  
	  videoEncoder.close();
	    /*
	     * Technically since we're exiting anyway, these will be cleaned up by 
	     * the garbage collector... but because we're nice people and want
	     * to be invited places for Christmas, we're going to show how to clean up.
	     */
	    
	    if (audioCoder != null)
	    {
	      audioCoder.close();
	      audioCoder = null;
	    }
	    if (container !=null)
	    {
	      container.close();
	      container = null;
	    }
	  }

	 
	 

	public int getSampleRate() {
		
		return audioCoder.getSampleRate();
	}
	
	public int getChannels() {
		return audioCoder.getChannels();
	}
	
	
}
