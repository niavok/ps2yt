package com.niavok;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

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
	
	private static final String OUTPUT_FILE = "out.mkv";
	
	private static final Integer OUTPUT_WIDTH = 1920;
	
	private static final Integer OUTPUT_HEIGHT = 1080;
	
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
			
//			now, create a packet
			IPacket packet = IPacket.make();
			
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
				if (container.writePacket(packet) < 0) {
					throw new RuntimeException("Could not write packet to container.");
				}
			}
			
			
			
			
			
//			positionInMicroseconds += frameRate.getDouble() * 1000 * 1000;
			
//			after all this, increase the timestamp by one frame (in microseconds)
			positionInMicroseconds += (1/frameRate.getDouble() * Math.pow(1000, 2));
		}
		
		//Flush
		IPacket packet = IPacket.make();
		
		while(true) {
		
		int encodeVideoResult = videoStreamCoder.encodeVideo(packet, null, 0);
		System.out.println("encodeVideoResult="+ encodeVideoResult);
		if (encodeVideoResult >= 0) {
			if (packet.isComplete()) {
				wpCount++;
				System.out.println("write flush packet "+ wpCount);
				if (container.writePacket(packet) < 0) {
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
}
