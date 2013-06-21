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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class VideoEncoder {
	
//	private static final Logger logger = LoggerFactory.getLogger(VideoEncoder.class);
	
	private static final Integer OUTPUT_WIDTH = 720;
	
	private static final Integer OUTPUT_HEIGHT = 480;

	private IContainer container;

	private IStreamCoder videoStreamCoder;

	private int generatedFrameCount;

	private int writeFrameCount;

	private long positionInMicroseconds;

	private IRational frameRate;

	private IStream audioStream;
	
//	public static void main(String[] arguments) {
//		System.out.println("Compiling an empty stream to "+ outputPath);
//		
//		File outputFile = new File(outputPath);
//		
//		if (outputFile.exists())
//			outputFile.delete();
//		
////		open a container
//		IContainer container = IContainer.make();
//		container.open(outputPath, IContainer.Type.WRITE, null);
//		
////		create the video stream and get its coder
//		ICodec videoCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
//		IStream videoStream = container.addNewStream(videoCodec);
//		IStreamCoder videoStreamCoder = videoStream.getStreamCoder();
//		
//		
//		ICodec audioCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_MP3);
//		
//		IStream audioStream = container.addNewStream(audioCodec);
//		IStreamCoder audioStreamCoder = audioStream.getStreamCoder();
//		
//		audioStreamCoder.setCodec(audioCodec);
//		audioStreamCoder.open(null, null);
//		
////		setup the stream coder
//		IRational frameRate = IRational.make(25, 1);
//		
//		videoStreamCoder.setWidth(OUTPUT_WIDTH);
//		videoStreamCoder.setHeight(OUTPUT_HEIGHT);
//		videoStreamCoder.setFrameRate(frameRate);
//		videoStreamCoder.setTimeBase(IRational.make(frameRate.getDenominator(),
//				frameRate.getNumerator()));
//		videoStreamCoder.setBitRate(350000);
//		videoStreamCoder.setNumPicturesInGroupOfPictures(30);
//		videoStreamCoder.setPixelType(IPixelFormat.Type.YUV444P);
//		videoStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
//		videoStreamCoder.setGlobalQuality(0);
//		
////		open the coder first
//		videoStreamCoder.open(null, null);
//		
////		write the header
//		container.writeHeader();
//		
////		let us begin
//		long positionInMicroseconds = 0;
//		
////              encode 30 frames, right?
//		//* frameRate.getDouble()
//		
//		int wpCount = 0;
//		
////		IPacket initPacket = IPacket.make();
////		videoStreamCoder.encodeVideo(initPacket, null, 0);
//		
//		//10s
//
////		now, create a packet
//		IPacket packet = IPacket.make();
//
//		
//		for (int i = 0; i < frameRate.getDouble() * 10 ; i++) {
//			
//			System.out.println("write frame "+i+" at "+positionInMicroseconds+" microseconds" );
//			
////			create a green box with a 50 pixel border for the frame image
//			BufferedImage outputImage = new BufferedImage(videoStreamCoder.getWidth(),
//					videoStreamCoder.getHeight(), BufferedImage.TYPE_INT_ARGB);
//			
//			Graphics graphics = outputImage.getGraphics();
//			graphics.setColor(Color.GREEN);
//			graphics.drawRect(0 +i, 0 +i, 
//					videoStreamCoder.getWidth() - 00 -i *2, 
//					videoStreamCoder.getHeight() - 00 -i *2 );
//			
//			outputImage = convert(outputImage, BufferedImage.TYPE_3BYTE_BGR);
//			
//			
//			IConverter converter = ConverterFactory.createConverter(outputImage, 
//					videoStreamCoder.getPixelType());
//			
//			 
//			
//			IVideoPicture frame = converter.toPicture(outputImage, positionInMicroseconds);
//			frame.setQuality(0);
//			
//			if (videoStreamCoder.encodeVideo(packet, frame, 0) < 0) {
//				throw new RuntimeException("Unable to encode video.");
//			}
//			
//			if (packet.isComplete()) {
//				System.out.println("write packet "+ wpCount);
//				wpCount++;
//				if (container.writePacket(packet,true) < 0) {
//					throw new RuntimeException("Could not write packet to container.");
//				}
//			}
//			
//			
//			// Encode audio
//			IAudioSamples audioSamples = IAudioSamples.make(1, 2);
//			
//			TestAudioSamplesGenerator testAudioSamplesGenerator = new TestAudioSamplesGenerator();
//			testAudioSamplesGenerator.setDesiredNoteFrequency(440);
//			testAudioSamplesGenerator.prepare(2, 44100);
//			testAudioSamplesGenerator.fillNextSamples(audioSamples, 100);
//			
//			audioStreamCoder.encodeAudio(packet, audioSamples, 100);
//			if (packet.isComplete()) {
//				System.out.println("write audio packet "+ wpCount);
//				wpCount++;
//				if (container.writePacket(packet,true) < 0) {
//					throw new RuntimeException("Could not write packet to container.");
//				}
//			}
//			
////			positionInMicroseconds += frameRate.getDouble() * 1000 * 1000;
//			
////			after all this, increase the timestamp by one frame (in microseconds)
//			positionInMicroseconds += (1/frameRate.getDouble() * Math.pow(1000, 2));
//		}
//		
//		//Flush
////		IPacket packet = IPacket.make();
//		
//		while(true) {
//		
//		int encodeVideoResult = videoStreamCoder.encodeVideo(packet, null, 0);
//		System.out.println("encodeVideoResult="+ encodeVideoResult);
//		if (encodeVideoResult >= 0) {
//			if (packet.isComplete()) {
//				wpCount++;
//				System.out.println("write flush packet "+ wpCount);
//				if (container.writePacket(packet,true) < 0) {
//					throw new RuntimeException("Could not write packet to container.");
//				}
//			} else {
//				break;
//			}
//		} else {
//			break;
//		}
//		
//		}
//		
//		System.out.println("diff="+(500-wpCount));
//		
//		
////		done, so now let's wrap this up.		
//		container.writeTrailer();
//		
//		videoStreamCoder.close();
////		container.flushPackets();
//		container.close();
//		
////		DecodeAndPlayVideo.main(new String[]{OUTPUT_FILE});
//	}
	
	private static BufferedImage convert(BufferedImage value, int type) {
		if (value.getType() == type)
			return value;
		
		BufferedImage result = new BufferedImage(value.getWidth(), value.getHeight(),
				type);
		
		result.getGraphics().drawImage(value, 0, 0, null);
		
		return result;
	}

	public VideoEncoder(String outputPath, int sampleRate, int channels) {
		this.outputPath = outputPath;
		System.out.println("Compiling an empty stream to "+ outputPath);
				
		File outputFile = new File(outputPath);
		
		if (outputFile.exists())
			outputFile.delete();
		
		container = IContainer.make();
		container.open(outputPath, IContainer.Type.WRITE, null);
	
		initAudioStream(sampleRate, channels);
		
		initVideoStream();
		
//		write the header
		container.writeHeader();
		
		positionInMicroseconds = 0;
		
//              encode 30 frames, right?
		//* frameRate.getDouble()
		
		generatedFrameCount = 0;
		writeFrameCount = 0;
		
//		IPacket initPacket = IPacket.make();
//		videoStreamCoder.encodeVideo(initPacket, null, 0);
		
		//10s

//		now, create a packet
//		IPacket packet = IPacket.make();

		packet = IPacket.make();
	}
	
	

	private void initVideoStream() {
		ICodec videoCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
		IStream videoStream = container.addNewStream(videoCodec);
		videoStreamCoder = videoStream.getStreamCoder();
		//1 frame for 10 s
		frameRate = IRational.make(1, 1);
		
		videoStreamCoder.setWidth(OUTPUT_WIDTH);
		videoStreamCoder.setHeight(OUTPUT_HEIGHT);
		videoStreamCoder.setFrameRate(frameRate);
		videoStreamCoder.setTimeBase(IRational.make(frameRate.getDenominator(),
				frameRate.getNumerator()));
		videoStreamCoder.setBitRate(3500000);
		videoStreamCoder.setNumPicturesInGroupOfPictures(30);
		videoStreamCoder.setPixelType(IPixelFormat.Type.YUV420P);
		videoStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
		videoStreamCoder.setGlobalQuality(0);
		
		videoStreamCoder.open(null, null);
	}

	private void initAudioStream(int sampleRate, int channels) {
		ICodec audioCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_MP3);
		
		audioStream = container.addNewStream(audioCodec);
		
		audioStreamCoder = audioStream.getStreamCoder();
		
		audioStreamCoder.setCodec(audioCodec);
		audioStreamCoder.setSampleRate(sampleRate);
		audioStreamCoder.setBitRate(128000);
		audioStreamCoder.setChannels(channels);
		audioStreamCoder.open(null, null);
	}


	private IPacket packet;

	private IStreamCoder audioStreamCoder;
	
	public void writeAudioPacket(IPacket packet) {
//		System.out.println("Write audio packet duration="+packet.getDuration());
//		System.out.println("Packet time stamp="+packet.getTimeStamp());
//		System.out.println("Packet base time="+packet.getTimeBase());
		
		
		
		
		if(positionInMicroseconds + (1/frameRate.getDouble() * Math.pow(1000, 2)) <= 1000000 * packet.getTimeStamp() * packet.getTimeBase().getDouble()) {
			writeFrame(generatedFrameCount);
		}
		container.writePacket(packet, true);
		
		
		
	}
	
	Random rand = new Random();

	private Color color;

	private BufferedImage image;

	private String outputPath;
	
	
	private void writeFrame(int frameId) {
		System.out.println("writeFrame "+frameId);
		
		int offset=frameId % (Math.min(OUTPUT_WIDTH/2, OUTPUT_HEIGHT/2));
//		if(offset == 0) {
//			color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
//		}
		
		
//		BufferedImage outputImage = new BufferedImage(videoStreamCoder.getWidth(),
//				videoStreamCoder.getHeight(), BufferedImage.TYPE_INT_ARGB);
//		
//		Graphics graphics = outputImage.getGraphics();
		
		
//		graphics.setColor(color);
//		graphics.drawRect(offset, offset, 
//				videoStreamCoder.getWidth() - 2*offset, 
//				videoStreamCoder.getHeight() - 2*offset );
//		
//		outputImage = convert(outputImage, BufferedImage.TYPE_3BYTE_BGR);
		
		BufferedImage outputImage = image;
		
		IConverter converter = ConverterFactory.createConverter(outputImage, 
				videoStreamCoder.getPixelType());
		
		 
		
		IVideoPicture frame = converter.toPicture(outputImage, positionInMicroseconds);
		frame.setQuality(0);
		
		if (videoStreamCoder.encodeVideo(packet, frame, 0) < 0) {
			throw new RuntimeException("Unable to encode video.");
		}
		
		if (packet.isComplete()) {
			System.out.println("write video packet");
			if (container.writePacket(packet,true) < 0) {
				writeFrameCount++;
				throw new RuntimeException("Could not write packet to container.");
			}
		}
		positionInMicroseconds += (1/frameRate.getDouble() * Math.pow(1000, 2));
		
		generatedFrameCount++;
	}
	
	
	public void close(){
		System.out.println("close");
		
		System.out.println("generatedFrameCount="+generatedFrameCount);
		System.out.println("writeFrameCount="+writeFrameCount);
		
		
		while(writeFrameCount < generatedFrameCount) {
			
			int encodeVideoResult = videoStreamCoder.encodeVideo(packet, null, 0);
//			System.out.println("encodeVideoResult="+ encodeVideoResult);
			if (encodeVideoResult >= 0) {
				if (packet.isComplete()) {
					writeFrameCount++;
//					System.out.println("write flush packet "+ writeFrameCount);
					if (container.writePacket(packet,true) < 0) {
						throw new RuntimeException("Could not write packet to container.");
					}
				} else {
					break;
				}
			} else {
				break;
			}
			
		}
		
		while(true) {
			
			int encodeVideoResult = audioStreamCoder.encodeVideo(packet, null, 0);
//			System.out.println("encodeVideoResult="+ encodeVideoResult);
			if (encodeVideoResult >= 0) {
				if (packet.isComplete()) {
					writeFrameCount++;
//					System.out.println("write flush packet "+ writeFrameCount);
					if (container.writePacket(packet,true) < 0) {
						throw new RuntimeException("Could not write packet to container.");
					}
				} else {
					break;
				}
			} else {
				break;
			}
			
		}
		
		
		container.writeTrailer();
		container.close();
		
		
		
		
		
		
		
	}

	public void writeAudioSamples(IAudioSamples samples) {
		int numSamplesConsumed = 0;
		 int retval = 0;
	
//			if(positionInMicroseconds + (1/frameRate.getDouble() * Math.pow(1000, 2)) <= 1000000 * samples.getTimeStamp() * samples.getTimeBase().getDouble()) {
		if(positionInMicroseconds <= 1000000 * samples.getTimeStamp() * samples.getTimeBase().getDouble()) {
			System.out.println("writeFrame at="+samples.getFormattedTimeStamp());
			writeFrame(generatedFrameCount);
		}
		 
		while (numSamplesConsumed < samples.getNumSamples()) {
            retval = audioStreamCoder.encodeAudio(packet, samples, numSamplesConsumed);
            if (retval <= 0)
              throw new RuntimeException("Could not encode any audio: " + retval);
            /**
             * Increment the number of samples consumed, so that the next time through this
             * loop we encode new audio 
             */
            numSamplesConsumed += retval;
            if (packet.isComplete())
            {
              /**
               * If we got a complete packet out of the encoder, then go ahead and write it
               * to the container.
               */
              retval = container.writePacket(packet, true);
              if (retval < 0)
                throw new RuntimeException("could not write output packet");
            }
          }
		
		
		

		
	}

	public void setImage(BufferedImage inputImage) {
		image = new BufferedImage(videoStreamCoder.getWidth(),
				videoStreamCoder.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();

		float widthRatio = (float)inputImage.getWidth() / (float)videoStreamCoder.getWidth();
		float heightRatio = (float)inputImage.getHeight() / (float)videoStreamCoder.getHeight();
			
		float ratio = Math.max(widthRatio, heightRatio);
		
		int newWidth = (int) (inputImage.getWidth()/ ratio);
		int newHeight = (int) (inputImage.getHeight() / ratio);
			
		int xOffset = (videoStreamCoder.getWidth() - newWidth)/2;
		int yOffset = (videoStreamCoder.getHeight() - newHeight)/2;
			
		graphics.drawImage(inputImage, xOffset, yOffset, newWidth, newHeight, null);	
		
				
		
		
		
		image = convert(image, BufferedImage.TYPE_3BYTE_BGR);
		
	}

	
}
