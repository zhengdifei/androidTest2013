package com.example.androidtest2013.video3.hw.video.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.Deflater;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.androidtest2013.R;
import com.example.androidtest2013.video3.hw.video.server.util.Constants;

//import com.smartcam.webcam.video.JpegHandler;
//import com.spore.jni.ImageUtilEngine;

public class VideoStreamingSenderService extends Service {
	private static final String TAG = VideoStreamingSenderService.class.getCanonicalName();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition suspended = lock.newCondition();
	private boolean isPaused = false;
	private boolean isExit = false;
	private int sent;

	private Camera camera;
	protected int frames;
	private long bt, startTime, endTime;
	private Deflater compresser;
	
//	private JpegHandler jpegHandler = new JpegHandler();
// ImageUtilEngine imageEngine= new ImageUtilEngine();
	boolean tag= true;
	int  count=0;
	
	private final class PreviewCallbackImplementation implements
			Camera.PreviewCallback {
		public void onPreviewFrame(byte[] previewFrameBytes, Camera camera) {
			Log.e("zdf", "camera preview");
	/*	
	  //用来检测压缩效率
	  if(count++<100){
			MyLog.w(TAG, "previewFrameBytes:"+previewFrameBytes.length);
			
//				MyLog.w(TAG, "previewFrameBytes:"+ArUtil.Bytes2HexString(previewFrameBytes));
//				byte[] bitmap2Bytes =  jpegHandler.encodeYUV420SP(previewFrameBytes);
//				MyLog.w(TAG, "jpegHandler:"+ArUtil.Bytes2HexString(bitmap2Bytes));
//				
//				Camera.Parameters parameters = camera.getParameters();
//				MyLog.w(TAG, "w:"+parameters.getPreviewSize().width+" h:"+parameters.getPreviewSize().height);
				
				long start = System.currentTimeMillis();
//				int[] buf = imageEngine.decodeYUV420SP(previewFrameBytes, width, height);
//				 Log.w(TAG, "------"+previewFrameBytes.length+" "+buf.length+"  time:"+(System.currentTimeMillis()-start));
				
//				byte[] bitmap2Bytes =  jpegHandler.encodeYUV420SP(previewFrameBytes);
//				 Log.w(TAG, "------"+previewFrameBytes.length+" "+bitmap2Bytes.length+"  time:"+(System.currentTimeMillis()-start));
				 
//				 byte[] data  =  new byte[COMPRESSED_FRAME_SIZE];
//				 compresser.reset();
//					compresser.setInput(previewFrameBytes);
//					compresser.finish();
//					int deflate = compresser.deflate(data);
//					 Log.w(TAG, "------"+previewFrameBytes.length+" "+deflate+"  time:"+(System.currentTimeMillis()-start));
				 
				tag = false;
			}
			else{
				return;
			}*/
			
			try {
				if (camera == null) {
					return;
				}
				if(!freeFrames.isEmpty()) {
					Log.e("zdf", "开始时间：" + sdf.format(new Date()));
					startTime = System.currentTimeMillis();
					Frame frame;
					frame = freeFrames.take();

					//将原始camera数据转换成YuvImage对象，再转换成byte[]传输，否则在接收端需要进行先解析。
					YuvImage image = new YuvImage(previewFrameBytes, ImageFormat.NV21, Constants.defWidth, Constants.defHeight, null);
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 70, outputStream);
					byte[] jpegData = outputStream.toByteArray();
					try {
						outputStream.flush();
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if(Constants.isCompress){
						//用zip压缩数据（576k->460k）
						compresser.reset();
						compresser.setInput(jpegData);
						compresser.finish();
						frame.compressedSize = compresser.deflate(frame.data);
						frame.uncompressedSize = jpegData.length;
						filledFrames.put(frame);
						frames++;
					}
					else{
					///// 不压缩
						frame.compressedSize = jpegData.length;
						frame.uncompressedSize = jpegData.length;
						frame.data=jpegData;
						filledFrames.put(frame);
						frames++;
					////
					}
					endTime = System.currentTimeMillis();
					Log.e("zdf", "耗时：" + (endTime - startTime));
					Log.w(TAG, "------"+frame.uncompressedSize+" "+frame.compressedSize);
					
					////  先转成RGB,再不压缩传输 --压缩效果好，当时耗时太长 
					/* int[] mByteArray = new int[800*480 * 3];// 设置空的图像RGB数组，用于接收转化后的数据   
					 //YUV编码数据转化为RGB                  
					 decodeYUV420SP(mByteArray,previewFrameBytes,width, height);                  
					 					 Bitmap mBitmap=null;                  
					 mBitmap = Bitmap.createBitmap(mByteArray, width, height, Bitmap.Config.ARGB_8888) ;
					 byte[] bitmap2Bytes = ImageUtil.Bitmap2Bytes(mBitmap); 
					Log.w(TAG, "------"+bitmap2Bytes.length+" "+frame.uncompressedSize+" "+frame.compressedSize);

					frame.compressedSize=bitmap2Bytes.length;
					frame.uncompressedSize=bitmap2Bytes.length;
					frame.data=bitmap2Bytes;
					
					 filledFrames.put(frame);
					 frames++;*/
					////
					 
					 ////使用jpegHandler来压缩，压缩效率极高（576k->24k），但耗时长（90ms），另外在接收端没找到解压缩办法
//					byte[] bitmap2Bytes =  jpegHandler.encodeYUV420SP(previewFrameBytes);
//					frame.compressedSize=bitmap2Bytes.length;
//					frame.uncompressedSize=bitmap2Bytes.length;
//					frame.data=bitmap2Bytes;
//					Log.w(TAG, "jpegHandler:"+bitmap2Bytes);
//					Camera.Parameters parameters = camera.getParameters();
//					
//					 filledFrames.put(frame);
//					 frames++;
					 ////
					
					 //// 使用NV21采集图像，然后压缩成jpeg，耗时长，最后只有6fps
			/*		YuvImage image = new YuvImage(previewFrameBytes, ImageFormat.NV21, width, height, null);   
		            if(image!=null){ 
		                    ByteArrayOutputStream outstream = new ByteArrayOutputStream(); 
		                   image.compressToJpeg(new Rect(0, 0, width, height), 100, outstream);  
		                  try {
							outstream.flush();
						} catch (IOException e) {
							e.printStackTrace();
						} 
		                //启用线程将图像数据发送出去 
		                  byte[] bitmap2Bytes = outstream.toByteArray();
		                  
//							compresser.reset();
//							compresser.setLevel(Deflater.BEST_COMPRESSION);  //将当前压缩级别设置为指定值。
//							compresser.setInput(bitmap2Bytes);
//							compresser.finish();
//							frame.compressedSize = compresser.deflate(frame.data);
//							frame.uncompressedSize = bitmap2Bytes.length;
		                  
		              	Log.w(TAG, "------"+bitmap2Bytes.length+" "+frame.uncompressedSize+" "+frame.compressedSize);

						frame.compressedSize=bitmap2Bytes.length;
						frame.uncompressedSize=bitmap2Bytes.length;
						frame.data=bitmap2Bytes;
						 filledFrames.put(frame);
						 frames++;
		            }   */
					 ////
					
					////////通过jni调用直接转换成RGB，耗时高（90ms）,压缩效率也不高（576k->384k）
//					  int[] buf = imageEngine.decodeYUV420SP(previewFrameBytes, width, height);
//					  byte[] data = new byte[buf.length];
//					  for (int i = 0; i < buf.length; i++) {
//						  data[i] = (char)buf[i];
//					  }
//					  compresser.reset();
//						compresser.s.setInput(bitmap2Bytes);
//						compresser.finish();
//						frame.compressedSize = compresser.deflate(frame.data);
//						frame.uncompressedSize = bitmap2Bytes.length;
	                  
//	              	Log.w(TAG, "------"+bitmap2Bytes.length+" "+frame.uncompressedSize+" "+frame.compressedSize);
					///////
					
				} else {
					dropped++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}

	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
			int height) {
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}    
	
	private final class serverThreadRunnable implements Runnable {
		@Override
		public void run() {
			ServerSocket serverSocket;
			try {
				serverSocket = new ServerSocket();
				String localIpAddress = getHostIp();//getLocalIpAddress();
				Log.d(TAG, " ------------------localIpAddress:"+localIpAddress);
				SocketAddress address = new InetSocketAddress(localIpAddress, 2014);
				serverSocket.bind(address);
			} catch (IOException e) {
				Log.e(TAG, "Could not create server socket", e);
				return;
			}
			while (!isExit) {
				Socket socket;
				try {
					Log.d(TAG, "Waiting for connection!");
					socket = serverSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "Error getting client socket", e);
					return;
				}
				DataOutputStream dataOutputStream;
				try {
					OutputStream outputStream = socket.getOutputStream();
					dataOutputStream = new DataOutputStream(outputStream);
				} catch (IOException e) {
					Log.e(TAG, "Error getting outputstream", e);
					return;
				}
				Log.d(TAG, "accepted new socket connection and created outputstream");
				while (socket.isConnected()) {
					lock.lock();
					try {
						if(isPaused) {
							suspended.await();
							continue;
						}
						if(isExit) {
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						continue;
					} finally {
						lock.unlock();
					}
					Frame frame;
					try {
						frame = filledFrames.take();
					} catch (InterruptedException e) {
						Log.e(TAG, "Error getting filled frame", e);
						continue;
					}
					try {
						Log.i(TAG, "Writing out uncompressed " + frame.uncompressedSize + " compressed " + frame.compressedSize + " of width " + preW + " and height " + preH);
						dataOutputStream.writeInt(frame.uncompressedSize);
						dataOutputStream.writeInt(frame.compressedSize);
						//dataOutputStream.writeInt(preW);
						//dataOutputStream.writeInt(preH);
						dataOutputStream.writeBoolean(Constants.isCompress);
						dataOutputStream.write(frame.data, 0, frame.compressedSize);
						
//						InputStream sbs = new ByteArrayInputStream(frame.data); 
//						byte[] buff = new byte[1024];  
//				        int rc = 0;  
//				        while ((rc = sbs.read(buff, 0, 1024)) > 0) {  
//				        	dataOutputStream.write(buff, 0, rc);  
//				        }  
						
						sent++;
					} catch (IOException e) {
						Log.e(TAG, "Error writing to output stream", e);
						break;
					} finally {
						freeFrames.add(frame);
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
					Log.d(TAG, "Error closing socket");
				}
			}
		}
	}
	private SurfaceView mDummySurfaceView;
	private class Frame {
		byte[] data;
		int uncompressedSize;
		int compressedSize;
		public Frame() {
			uncompressedSize = 0;
			compressedSize = 0;
		}
	}
	private LinkedBlockingQueue<Frame> freeFrames;
	private LinkedBlockingQueue<Frame> filledFrames = new LinkedBlockingQueue<Frame>();
	private int preH;
	private int preW;
	private Thread serverThread;
	private static final int NUMBER_OF_BUFFERS = 5;
	private static final int COMPRESSED_FRAME_SIZE = 500000;
	byte[][] buffers;
	private int bytesPerPixel;
	private int dropped;

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		lock.lock();
		try {
			isExit = true;
			suspended.signal();
		} finally {
			lock.unlock();
		}
		closeCamera();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	public class LocalBinder extends Binder {
		public VideoStreamingSenderService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return VideoStreamingSenderService.this;
		}
	}
	private final IBinder binder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		changePauseState(false);
		return binder;
	}

	private void changePauseState(boolean flag) {
		lock.lock();
		try {
			isPaused = flag;
			suspended.signal();
		} finally {
			lock.unlock();
		}
	}

	Camera.PreviewCallback previewCallback = new PreviewCallbackImplementation();
	private int minFps;
	private int maxFps;
	private int currentFps;

	/** Called when the activity is first created. */

	@Override
	public void onCreate() {
		super.onCreate();
		
		/// 用jpegHandler来压缩图片
//		PixelFormat pixelFmt = new PixelFormat();
//		PixelFormat.getPixelFormatInfo(PixelFormat.YCbCr_420_SP, pixelFmt);
//		int frameBufferSize = width * height * pixelFmt.bitsPerPixel / 8;
//		jpegHandler.initYuv(frameBufferSize, width, height);
		///
		
		changePauseState(false);
		Log.d(TAG, " ------------------compresser");
		compresser = new Deflater();
		compresser.setStrategy(Deflater.HUFFMAN_ONLY);
		compresser.setLevel(Deflater.BEST_COMPRESSION);  //将当前压缩级别设置为指定值。
		Log.d(TAG, " ------------------freeFrames");
		if(freeFrames == null) {
			freeFrames = new LinkedBlockingQueue<Frame>();
			for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
				Frame frame = new Frame();
				frame.data = new byte[COMPRESSED_FRAME_SIZE];
				freeFrames.add(frame);
			}
		}
		Log.d(TAG, " ------------------freeFrames");
		if(serverThread == null) {
			serverThread = new Thread(new serverThreadRunnable());
			serverThread.start();
		}
		Log.d(TAG, " ------------------openCamera");
		makeAndAddSurfaceView();

	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	private void openCamera() {
		Log.e("zdf", "open camera");
		lock.lock();
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(mDummySurfaceView.getHolder());
			Camera.Parameters parameters = camera.getParameters();

			List<int[]> supportedPreviewFpsRange = parameters.getSupportedPreviewFpsRange();
			minFps = supportedPreviewFpsRange.get(Camera.Parameters.PREVIEW_FPS_MIN_INDEX)[0];
			maxFps = supportedPreviewFpsRange.get(Camera.Parameters.PREVIEW_FPS_MIN_INDEX)[1];
			currentFps = Constants.defFpsMax;
			//系统可支持的尺寸
			List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
			for (Size size : supportedPreviewSizes) {
				Log.w(TAG, "supported preview size is:  " + size.width+" "+size.height);
			}
			//算法，取一个合理的尺寸
			/*Size selectedSize = supportedPreviewSizes.get(0);
			for(Size size : supportedPreviewSizes) {
				if(size.width < selectedSize.width)
					selectedSize = size;
			}
			parameters.setPreviewSize(selectedSize.width, selectedSize.height);*/
			//直接选择系统配置好的尺寸
			parameters.setPreviewSize(Constants.defWidth, Constants.defHeight);
			parameters.setPictureSize(Constants.defWidth, Constants.defHeight);
			//预览编码格式
			//parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
			// 设置图片格式
//			parameters.setPictureFormat(PixelFormat.JPEG);
			parameters.setPictureFormat(ImageFormat.NV21);
//			parameters.setPreviewFpsRange(minFps, maxFps);
			//预览fps范围
			//parameters.setPreviewFpsRange(Constants.defFpsMin, Constants.defFpsMax);
			//设置固定的fps值,设置则报错
			//parameters.setPreviewFrameRate(5);
			preH = parameters.getPreviewSize().height;
			preW = parameters.getPreviewSize().width;
			camera.setParameters(parameters);
			camera.setDisplayOrientation(90);
			bytesPerPixel = 4;
			if (buffers == null) {
				int frameSize = preH * preW * bytesPerPixel;
				Log.d(TAG, "Frame size is " + frameSize);
				// buffers = new byte[NUMBER_OF_BUFFERS][frameSize];
			}

			//camera.setPreviewCallbackWithBuffer(previewCallback);
			camera.setPreviewCallback(previewCallback);
			camera.startPreview();
			bt = System.currentTimeMillis();
			sent = 0;
			dropped = 0;
			frames = 0;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	private void closeCamera() {
		lock.lock();
		try {
			if (camera != null) {
				camera.stopPreview();
				camera.setPreviewCallback(null);
				camera.release();
				camera = null;
			}
		} finally {
			lock.unlock();
		}
	}

	private void makeAndAddSurfaceView() {
		SurfaceView dummyView = new SurfaceView(this.getApplication());
		SurfaceHolder holder = dummyView.getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder surfaceHolder) {
				Log.e("zdf", "sv created");
				openCamera();
			}

			@Override
			public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

			}

			@Override
			public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

			}
		});
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		WindowManager wm = (WindowManager)this.getApplication().getSystemService(Context.WINDOW_SERVICE);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(1, 1,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
						WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSPARENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		params.alpha = PixelFormat.TRANSPARENT;
		//params.x = params.y = this.getApplication().getResources().getDimensionPixelOffset(R.dimen.preview_surface_offset);

		wm.addView(dummyView, params);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			dummyView.setAlpha(PixelFormat.TRANSPARENT);
//		}

		//dummyView.getBackground().setAlpha(PixelFormat.TRANSPARENT);
		mDummySurfaceView = dummyView;
	}
   public static String getLocalIpAddress(){
		try{
			 for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				 NetworkInterface intf = en.nextElement();  
	                for (Enumeration<InetAddress> enumIpAddr = intf  
	                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
	                    InetAddress inetAddress = enumIpAddr.nextElement();  
	                    if (!inetAddress.isLoopbackAddress() /*&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())*/) {
	                    	return inetAddress.getHostAddress().toString();  
	                    }  
	                }  
			 }
		}catch (SocketException e) {
			Log.e(TAG, "Could not get local IP", e);
		}
		
		return null; 
	}

	public static String getHostIp(){
		String hostIp = null;
		try {
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia = null;
			while (nis.hasMoreElements()){
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements()){
					ia = ias.nextElement();
					if(ia instanceof Inet6Address){
						continue;
					}

					String ip = ia.getHostAddress();
					if(!"127.0.0.1".equals(ip) && !ip.startsWith("10.0")){
						hostIp = ia.getHostAddress();
						//Log.e("zdf", "IP:" + hostIp);
						break;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return hostIp;
	}

	public String getStatus() {
		return  " Dropped " + dropped  //"Frame rate: " + frames * 1000/ (System.currentTimeMillis() - bt)
				+ " sent " + sent
				+ "\nminfps " + minFps
				+ " maxfps " + maxFps
				+ " currentfps " + currentFps
				+ "\nwidth " + preW 
				+ " height " + preH
				+ "\niscompress " + Constants.isCompress ;
	}
}