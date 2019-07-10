package com.example.androidtest2013.video3.hw.video.server;

public class VideoStreamingSenderServiceClient {
	private final VideoStreamingSenderService service;

	public VideoStreamingSenderServiceClient(VideoStreamingSenderService service) {
		this.service = service;
	}

	public String getStatus() {
		return service.getStatus();
	}

	public String getIp() {
		// TODO Auto-generated method stub
		return service.getHostIp();
	}
}
