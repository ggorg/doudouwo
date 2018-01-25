package weixin.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.dao.TagsMapper;
import com.ty.entity.Tags;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * 用户标签管理
 */
@Service
public class TagsService {

    private static final Logger logger = Logger.getLogger(TagsService.class);
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private TagsMapper tagsMapper;


    /**
     * 分页查询微信列表
     * 
     * @param pageNum 分页信息
     * @return 微信标签列表
     */
    public Page<Tags> findList(Integer pageNum,Tags tags) {
        Page<Tags> page = new Page<Tags>(pageNum,10);
        // 设置分页参数
        List<Tags> tagsList = tagsMapper.findList(page,tags);
        int total =  tagsMapper.findListCount(tags);
        // 执行分页查询
        page.setResult(tagsList);
        page.setTotal(total);
        return page;
    }

    /**
     * 创建标签
     * @param tags
     * @return
     */
    @Transactional(readOnly = false)
    public ResponseVO save(Tags tags) {
        ResponseVO vo = new ResponseVO();
        JSONObject json = weixinInterfaceService.tagsCreate(tags.getAppid(), tags.getName());
        if(json.containsKey("errcode")){
            vo.setReCode(-1);
            vo.setReMsg(json.getString("errmsg"));
        }else{
            tags.setId(json.getJSONObject("tag").getInteger("id"));
            int res = tagsMapper.insert(tags);
            if(res>0){
                vo.setReCode(1);
                vo.setReMsg("成功");
                vo.setData(tags);
            }else{
                vo.setReCode(-2);
                vo.setReMsg("入库失败");
            }
        }
    	return vo;
    }

    public ResponseVO update(Tags tags) {
        ResponseVO vo = new ResponseVO();
        JSONObject json = weixinInterfaceService.tagsUpdate(tags.getAppid(), tags.getId(),tags.getName());
        if(json.containsKey("errcode")){
            vo.setReCode(-1);
            vo.setReMsg(json.getString("errcode")+json.getString("errmsg"));
        }else{
            tags.setId(json.getJSONObject("tag").getInteger("id"));
            int res = tagsMapper.insert(tags);
            if(res>0){
                vo.setReCode(1);
                vo.setReMsg("成功");
                vo.setData(tags);
            }else{
                vo.setReCode(-2);
                vo.setReMsg("入库失败");
            }
        }
        return vo;
    }
    
    /**
     * 根据appid和id查询标签
     * 
     * @param entity
     * @return
     */
    public Tags selectById(Tags entity) {
        return tagsMapper.selectById(entity);
    }

    /**
     * 不分页查询标签列表
     * @param entity
     * @return
     */
    public List<Tags>findListAll(Tags entity){return tagsMapper.findListAll(entity);}

    /**
     * 根据appid和id删除标签
     * @param tags
     * @return
     */
    public ResponseVO delete(Tags tags) {
        ResponseVO vo = new ResponseVO();
        JSONObject json = weixinInterfaceService.tagsDelete(tags.getAppid(), tags.getId());
        if(json.containsKey("errcode") && json.getInteger("errcode") != 0){
            vo.setReCode(-1);
            vo.setReMsg(json.getString("errmsg"));
        }else{
            int res = tagsMapper.delete(tags);
            if(res>0){
                vo.setReCode(1);
                vo.setReMsg("成功");
                vo.setData(tags);
            }else{
                vo.setReCode(-2);
                vo.setReMsg("失败");
            }
        }
        return vo;
    }

    /**
     * 更新公众号下微信用户标签
     * @param appid
     * @return
     */
    public JSONObject getAllTags(String appid){
        //{   "tags":[{       "id":1,       "name":"每天一罐可乐星人",       "count":0 //此标签下粉丝数 },{   "id":2,   "name":"星标组",   "count":0 },{   "id":127,   "name":"广东",   "count":5 }   ] }
        JSONObject result = new JSONObject();
        JSONObject jsonObject = weixinInterfaceService.tagsGet(appid);
        if (jsonObject.containsKey("tags")) {
            tagsMapper.deleteByAppid(appid);
            JSONArray jsonArray = jsonObject.getJSONArray("tags");
            Iterator<Object> it = jsonArray.iterator();
            while (it.hasNext()) {
                JSONObject ob = (JSONObject) it.next();
                Tags tags = new Tags();
                tags.setAppid(appid);
                tags.setId(ob.getInteger("id"));
                tags.setName(ob.getString("name"));
                tags.setCount(ob.getInteger("count"));
                tagsMapper.insert(tags);
            }
            result.put("retCode", 1);
            result.put("retMsg", "更新完毕");
        } else {
            result.put("retCode", -2);
            result.put("retMsg", jsonObject.getString("errmsg"));
        }
        return result;
    }
}