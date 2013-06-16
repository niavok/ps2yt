package com.niavok;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.TestAudioSamplesGenerator;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class VideoEncoder {
	
//	private static final Logger logger = LoggerFactory.getLogger(VideoEncoder.class);
	
	private static final String OUTPUT_FILE = "out.mkv";
	
	private static final Integer OUTPUT_WIDTH = 1920;
	
	private static final Integer OUTPUT_HEIGHT = 1080;

	private IContainer container;

	private IStreamCoder videoStreamCoder;

	private int generatedFrameCount;

	private int writeFrameCount;

	private long positionInMicroseconds;

	private IRational frameRate;

	private IStream audioStream;
	
	public static void main(String[] arguments) {
		System.out.println("Compiling an empty stream to "+ OUTPUT_FILE);
		
		File outputFile = new File(OUTPUT_FILE);
		
		if (outputFile.exists())
			outputFile.delete();
		
//		open a container
		IContainer container = IContainer.make();
		container.open(OUTPUT_FILE, IContainer.Type.WRITE, null);
		
//		create the video stream and get its coder
		ICodec videoCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
		IStream videoStream = container.addNewStream(videoCodec);
		IStreamCoder videoStreamCoder = videoStream.getStreamCoder();
		
		
		ICodec audioCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_MP3);
		
		IStream audioStream = container.addNewStream(audioCodec);
		IStreamCoder audioStreamCoder = audioStream.getStreamCoder();
		
		audioStreamCoder.setCodec(audioCodec);
		audioStreamCoder.open(null, null);
		
//		setup the stream coder
		IRational frameRate = IRational.make(25, 1);
		
		videoStreamCoder.setWidth(OUTPUT_WIDTH);
		videoStreamCoder.setHeight(OUTPUT_HEIGHT);
		videoStreamCoder.setFrameRate(frameRate);
		videoStreamCoder.setTimeBase(IRational.make(frameRate.getDenominator(),
				frameRate.getNumerator()));
		videoStreamCoder.setBitRate(350000);
		videoStreamCoder.setNumPicturesInGroupOfPictures(30);
		videoStreamCoder.setPixelType(IPixelFormat.Type.YUV444P);
		videoStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
		videoStreamCoder.setGlobalQuality(0);
		
//		open the coder first
		videoStreamCoder.open(null, null);
		
//		write the header
		container.writeHeader();
		
//		let us begin
		long positionInMicroseconds = 0;
		
//              encode 30 frames, right?
		//* frameRate.getDouble()
		
		int wpCount = 0;
		
//		IPacket initPacket = IPacket.make();
//		videoStreamCoder.encodeVideo(initPacket, null, 0);
		
		//10s

//		now, create a packet
		IPacket packet = IPacket.make();

		
		for (int i = 0; i < frameRate.getDouble() * 10 ; i++) {
			
			System.out.println("write frame "+i+" at "+positionInMicroseconds+" microseconds" );
			
//			create a green box with a 50 pixel border for the frame image
			BufferedImage outputImage = new BufferedImage(videoStreamCoder.getWidth(),
					videoStreamCoder.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			Graphics graphics = outputImage.getGraphics();
			graphics.setColor(Color.GREEN);
			graphics.drawRect(0 +i, 0 +i, 
					videoStreamCoder.getWidth() - 00 -i *2, 
					videoStreamCoder.getHeight() - 00 -i *2 );
			
			outputImage = convert(outputImage, BufferedImage.TYPE_3BYTE_BGR);
			
			
			IConverter converter = ConverterFactory.createConverter(outputImage, 
					videoStreamCoder.getPixelType());
			
			 
			
			IVideoPicture frame = converter.toPicture(outputImage, positionInMicroseconds);
			frame.setQuality(0);
			
			if (videoStreamCoder.encodeVideo(packet, frame, 0) < 0) {
				throw new RuntimeException("Unable to encode video.");
			}
			
			if (packet.isComplete()) {
				System.out.println("write packet "+ wpCount);
				wpCount++;
				if (container.writePacket(packet,true) < 0) {
					throw new RuntimeException("Could not write packet to container.");
				}
			}
			
			
			// Encode audio
			IAudioSamples audioSamples = IAudioSamples.make(1, 2);
			
			TestAudioSamplesGenerator testAudioSamplesGenerator = new TestAudioSamplesGenerator();
			testAudioSamplesGenerator.setDesiredNoteFrequency(440);
			testAudioSamplesGenerator.prepare(2, 44100);
			testAudioSamplesGenerator.fillNextSamples(audioSamples, 100);
			
			audioStreamCoder.encodeAudio(packet, audioSamples, 100);
			if (packet.isComplete()) {
				System.out.println("write audio packet "+ wpCount);
				wpCount++;
				if (container.writePacket(packet,true) < 0) {
					throw new RuntimeException("Could not write packet to container.");
				}
			}
			
//			positionInMicroseconds += frameRate.getDouble() * 1000 * 1000;
			
//			after all this, increase the timestamp by one frame (in microseconds)
			positionInMicroseconds += (1/frameRate.getDouble() * Math.pow(1000, 2));
		}
		
		//Flush
//		IPacket packet = IPacket.make();
		
		while(true) {
		
		int encodeVideoResult = videoStreamCoder.encodeVideo(packet, null, 0);
		System.out.println("encodeVideoResult="+ encodeVideoResult);
		if (encodeVideoResult >= 0) {
			if (packet.isComplete()) {
				wpCount++;
				System.out.println("write flush packet "+ wpCount);
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
		
		System.out.println("diff="+(500-wpCount));
		
		
//		done, so now let's wrap this up.		
		container.writeTrailer();
		
		videoStreamCoder.close();
//		container.flushPackets();
		container.close();
		
//		DecodeAndPlayVideo.main(new String[]{OUTPUT_FILE});
	}
	
	private static BufferedImage convert(BufferedImage value, int type) {
		if (value.getType() == type)
			return value;
		
		BufferedImage result = new BufferedImage(value.getWidth(), value.getHeight(),
				type);
		
		result.getGraphics().drawImage(value, 0, 0, null);
		
		return result;
	}

	public VideoEncoder() {
System.out.println("Compiling an empty stream to "+ OUTPUT_FILE);
		
		File outputFile = new File(OUTPUT_FILE);
		
		if (outputFile.exists())
			outputFile.delete();
		
		container = IContainer.make();
		container.open(OUTPUT_FILE, IContainer.Type.WRITE, null);
	
		initAudioStream();
		
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

		
	}
	
	

	private void initVideoStream() {
		ICodec videoCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
		IStream videoStream = container.addNewStream(videoCodec);
		videoStreamCoder = videoStream.getStreamCoder();
		//1 frame for 10 s
		frameRate = IRational.make(1, 2);
		
		videoStreamCoder.setWidth(OUTPUT_WIDTH);
		videoStreamCoder.setHeight(OUTPUT_HEIGHT);
		videoStreamCoder.setFrameRate(frameRate);
		videoStreamCoder.setTimeBase(IRational.make(frameRate.getDenominator(),
				frameRate.getNumerator()));
		videoStreamCoder.setBitRate(350000);
		videoStreamCoder.setNumPicturesInGroupOfPictures(30);
		videoStreamCoder.setPixelType(IPixelFormat.Type.YUV444P);
		videoStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
		videoStreamCoder.setGlobalQuality(0);
		
		videoStreamCoder.open(null, null);
	}

	private void initAudioStream() {
		ICodec audioCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_MP3);
		
		audioStream = container.addNewStream(audioCodec);
		
		IStreamCoder audioStreamCoder = audioStream.getStreamCoder();
		
		audioStreamCoder.setCodec(audioCodec);
		audioStreamCoder.setSampleRate(48000);
		audioStreamCoder.setChannels(2);
		audioStreamCoder.open(null, null);
	}

	int plop = 0;
	
	public void writeAudioPacket(IPacket packet) {
		System.out.println("Write audio packet duration="+packet.getDuration());
		System.out.println("Packet time stamp="+packet.getTimeStamp());
		System.out.println("Packet base time="+packet.getTimeBase());
		
		
		
		
		if(positionInMicroseconds <= 1000000 * packet.getTimeStamp() * packet.getTimeBase().getDouble()) {
			writeFrame(generatedFrameCount);
		}
		container.writePacket(packet, true);
		
		
		
		plop++;
	}
	
	private void writeFrame(int frameId) {
		System.out.println("writeFrame "+frameId);
		IPacket packet = IPacket.make();
		
		BufferedImage outputImage = new BufferedImage(videoStreamCoder.getWidth(),
				videoStreamCoder.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics graphics = outputImage.getGraphics();
		graphics.setColor(Color.GREEN);
		graphics.drawRect(frameId, frameId, 
				videoStreamCoder.getWidth() - 2*frameId, 
				videoStreamCoder.getHeight() - 2*frameId );
		
		outputImage = convert(outputImage, BufferedImage.TYPE_3BYTE_BGR);
		
		
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
		
		IPacket packet = IPacket.make();
		while(writeFrameCount < generatedFrameCount) {
			
			int encodeVideoResult = videoStreamCoder.encodeVideo(packet, null, 0);
			System.out.println("encodeVideoResult="+ encodeVideoResult);
			if (encodeVideoResult >= 0) {
				if (packet.isComplete()) {
					writeFrameCount++;
					System.out.println("write flush packet "+ writeFrameCount);
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
	
}