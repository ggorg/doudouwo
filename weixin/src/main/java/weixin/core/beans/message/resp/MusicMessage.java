package weixin.core.beans.message.resp;

/**
 * 音乐消息
 */
public class MusicMessage extends BaseMessage {
	// 音乐
	private weixin.core.beans.message.resp.Music Music;

	public weixin.core.beans.message.resp.Music getMusic() {
		return Music;
	}

	public void setMusic(weixin.core.beans.message.resp.Music music) {
		Music = music;
	}
}
