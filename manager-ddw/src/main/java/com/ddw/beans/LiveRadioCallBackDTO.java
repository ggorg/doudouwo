package com.ddw.beans;

public class LiveRadioCallBackDTO {
    private String t;//有效时间
    private String sign; //安全签名
    private Integer event_type;//事件类型
    private String stream_id;//直播码
    private String channel_id	;//直播码

    /**
     * event_type = 0 代表断流，event_type = 1 代表推流，同时消息体会额外包含如下信息
     */
    private String appname; //推流路径	string		Y
    private String app; //推流域名	string		Y
    private Integer event_time	;//消息产生的时间	int	单位 s	Y
    private String sequence	;//消息序列号，标识一次推流活动，一次推流活动会产生相同序列号的推流和断流消息	string		Y
    private String node;	//Upload接入点的 IP	String		Y
    private String  user_ip; //用户推流 IP	String	Client_ip	Y
    private Integer errcode;	//断流错误码	Int		N
    private String  errmsg;	//断流错误信息	String		N
    private String  push_duration; //推流时长	 单位ms

    /**
     * event_type = 100 代表有新的录制文件生成
     */
    private String  video_id;//	vid	string	点播用 vid，在点播平台可以唯一定位一个点播视频文件	Y
    private String  video_url;//	下载地址	string	点播视频的下载地址	Y
    private String  file_size;//	文件大小	string		Y
    private Integer start_time;//	分片开始时间戳	int	开始时间（unix 时间戳，由于 I 帧干扰，暂时不能精确到秒级）	Y
    private Integer end_time;//	分片结束时间戳	int	结束时间（unix 时间戳，由于 I 帧干扰，暂时不能精确到秒级）	Y
    private String  file_id;//	file_id	string		Y
    private String   file_format;//	文件格式	string	flv, hls, mp4	Y
    private Integer vod2Flag;//	是否开启点播 2.0	Int	0 表示未开启，1 表示开启	N
    private String  record_file_id;//	录制文件 ID	String	点播 2.0 开启时，才会有这个字段	N
    private Integer duration;//	推流时长	Int		Y
    private String  stream_param ;//推流 url 参数

    /**
     * event_type = 200 代表有新的截图图片生成，
     */
    private String pic_url;//	图片地址	string	不带域名的路径	Y
    private Integer  create_time;//	截图时间戳	int	截图时间戳（unix 时间戳，由于 I 帧干扰，暂时不能精确到秒级）	Y
    private String  pic_full_url;//	截图全路径	String	需要带上域名

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Integer getEvent_time() {
        return event_time;
    }

    public void setEvent_time(Integer event_time) {
        this.event_time = event_time;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getUser_ip() {
        return user_ip;
    }

    public void setUser_ip(String user_ip) {
        this.user_ip = user_ip;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getPush_duration() {
        return push_duration;
    }

    public void setPush_duration(String push_duration) {
        this.push_duration = push_duration;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public Integer getStart_time() {
        return start_time;
    }

    public void setStart_time(Integer start_time) {
        this.start_time = start_time;
    }

    public Integer getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Integer end_time) {
        this.end_time = end_time;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getFile_format() {
        return file_format;
    }

    public void setFile_format(String file_format) {
        this.file_format = file_format;
    }

    public Integer getVod2Flag() {
        return vod2Flag;
    }

    public void setVod2Flag(Integer vod2Flag) {
        this.vod2Flag = vod2Flag;
    }

    public String getRecord_file_id() {
        return record_file_id;
    }

    public void setRecord_file_id(String record_file_id) {
        this.record_file_id = record_file_id;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getStream_param() {
        return stream_param;
    }

    public void setStream_param(String stream_param) {
        this.stream_param = stream_param;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public Integer getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Integer create_time) {
        this.create_time = create_time;
    }

    public String getPic_full_url() {
        return pic_full_url;
    }

    public void setPic_full_url(String pic_full_url) {
        this.pic_full_url = pic_full_url;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getEvent_type() {
        return event_type;
    }

    public void setEvent_type(Integer event_type) {
        this.event_type = event_type;
    }

    public String getStream_id() {
        return stream_id;
    }

    public void setStream_id(String stream_id) {
        this.stream_id = stream_id;
    }

    @Override
    public String toString() {
        return "LiveRadioCallBackDTO{" +
                "t='" + t + '\'' +
                ", sign='" + sign + '\'' +
                ", event_type=" + event_type +
                ", stream_id='" + stream_id + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", appname='" + appname + '\'' +
                ", app='" + app + '\'' +
                ", event_time=" + event_time +
                ", sequence='" + sequence + '\'' +
                ", node='" + node + '\'' +
                ", user_ip='" + user_ip + '\'' +
                ", errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", push_duration='" + push_duration + '\'' +
                ", video_id='" + video_id + '\'' +
                ", video_url='" + video_url + '\'' +
                ", file_size='" + file_size + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", file_id='" + file_id + '\'' +
                ", file_format='" + file_format + '\'' +
                ", vod2Flag=" + vod2Flag +
                ", record_file_id='" + record_file_id + '\'' +
                ", duration=" + duration +
                ", stream_param='" + stream_param + '\'' +
                ", pic_url='" + pic_url + '\'' +
                ", create_time=" + create_time +
                ", pic_full_url='" + pic_full_url + '\'' +
                '}';
    }
}
