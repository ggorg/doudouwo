package weixin.services;

import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.dao.PubweixinMapper;
import com.ty.entity.Pubweixin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 微信公众号相关操作服务
 */
@Service
public class PubWeixinService{
    @Autowired
    private PubweixinMapper pubweixinMapper;

    /**
     * 添加公众号
     * @param pubweixin
     * @return
     */
    @Transactional(readOnly = false)
	@CacheEvict(value="commonCache",key = "#pubweixin.appid")
	public ResponseVO saveOrUpdate(Pubweixin pubweixin){
		ResponseVO vo=new ResponseVO();
    	int ret = 0;
    	Pubweixin pw = pubweixinMapper.selectByAppid(pubweixin.getAppid());
    	if(pw!=null){
    		pubweixin.setId(pw.getId());
    		pubweixin.setUpdate_date(new Date());
    		ret = pubweixinMapper.update(pubweixin);
			vo.setReMsg("修改成功");
		}else{
			pubweixin.setCreate_date(new Date());
			pubweixin.setUpdate_date(new Date());
			ret = pubweixinMapper.insert(pubweixin);
			vo.setReMsg("创建公众号成功");
		}
		vo.setReCode(1);
    	return vo;
    }
    /**
     * 删除公众号
     * @param pubweixin
     * @return
     */
    @Transactional(readOnly = false)
    public ResponseVO delete(Pubweixin pubweixin){
		ResponseVO vo=new ResponseVO();
    	int res = pubweixinMapper.delete(pubweixin);
		if(res>0){
			vo.setReCode(1);
			vo.setReMsg("删除成功");
		}else{
			vo.setReCode(-2);
			vo.setReMsg("删除失败");
		}
    	return vo;
    }
    
    /**
     * 分页查询微信公众号列表
     * @param pageNum
     * @return
     */
    public Page findPubweixin(Integer pageNum) throws Exception{
//		return this.commonPage("weixin_public","update_date desc",pageNum,10,new HashMap<>());
		Page<Pubweixin> page = new Page<Pubweixin>(pageNum,10);
		List<Pubweixin> list = pubweixinMapper.findList(page,new Pubweixin());
		int total = pubweixinMapper.findListCount(new Pubweixin());
		page.setResult(list);
		page.setTotal(total);
		return page;
	}

    /**
     * 无分页查询微信公众号列表
     *
     * @return
     */
    public List<Pubweixin> findPubweixinAll() {
        List<Pubweixin> list = pubweixinMapper.findListAll();
        return list;
    }
    
    /**
     * 根据APPID返回公众号
     * @param appid
     * @return
     */
	@Cacheable(value="commonCache",key = "#appid")
    public Pubweixin selectByAppid(String appid){
    	return pubweixinMapper.selectByAppid(appid);
    }

}
