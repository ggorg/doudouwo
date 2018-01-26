package com.weixin.core.beans.message.resp;

/**
 * 音乐消息
 */
public class MusicMessage extends BaseMessage {
	// 音乐
	private com.weixin.core.beans.message.resp.Music Music;

	public com.weixin.core.beans.message.resp.Music getMusic() {
		return Music;
	}

	public void setMusic(com.weixin.core.beans.message.resp.Music music) {
		Music = music;
	}
}
