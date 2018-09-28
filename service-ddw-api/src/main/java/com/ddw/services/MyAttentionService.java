package com.ddw.services;

import com.ddw.beans.MyAttentionDTO;
import com.ddw.beans.MyAttentionPO;
import com.ddw.beans.MyAttentionVO;
import com.ddw.beans.UserInfoVO;
import com.ddw.dao.UserInfoMapper;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 我的关注
 * Created by Jacky on 2018/4/16.
 */
@Service
@Transactional(readOnly = true)
public class MyAttentionService extends CommonService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(int userId,MyAttentionDTO myAttentionDTO)throws Exception{
        MyAttentionPO myAttentionPO = new MyAttentionPO();
        PropertyUtils.copyProperties(myAttentionPO,myAttentionDTO);
        myAttentionPO.setUserId(userId);
        myAttentionPO.setCreateTime(new Date());
        myAttentionPO.setUpdateTime(new Date());
        return this.commonInsert("ddw_my_attention",myAttentionPO);
    }

    public MyAttentionPO query(int userId,MyAttentionDTO myAttentionDTO)throws Exception {
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        if(myAttentionDTO.getGoddessId() != 0 ){
            searchCondition.put("goddessId",myAttentionDTO.getGoddessId());
        }else if(myAttentionDTO.getPracticeId() != 0){
            searchCondition.put("practiceId",myAttentionDTO.getPracticeId());
        }
        return  this.commonObjectBySearchCondition("ddw_my_attention",searchCondition,new MyAttentionPO().getClass());
    }

    public ResponseVO queryGoddessByUserId(int userId,Integer pageNum,Integer pageSize)throws Exception{
        if(pageNum == null || pageSize == null){
            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
        }
        MyAttentionVO myAttentionVO = new MyAttentionVO();
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        searchCondition.put("practiceId",0);
        List<Map> list = this.commonList("ddw_my_attention",null,pageNum,pageSize,searchCondition);
        Long count = this.commonCountBySearchCondition("ddw_my_attention",searchCondition);
        List<Integer>userIdList = new ArrayList<Integer>();
        for(Map map:list){
            userIdList.add((Integer)map.get("goddessId"));
        }
        if(!list.isEmpty()){
            List<UserInfoVO> userInfoList = userInfoMapper.getUserInfoList(userIdList);
            ListIterator<UserInfoVO> userInfoListIterator = userInfoList.listIterator();
            while (userInfoListIterator.hasNext()){
                userInfoListIterator.next().setFocus(true);
            }
            myAttentionVO.setUserInfoList(userInfoList);
        }
        myAttentionVO.setUserId(userId);
        myAttentionVO.setGoddessCount(count.intValue());
        return new ResponseVO(1,"成功",myAttentionVO);
    }

    public ResponseVO queryPracticeByUserId(int userId,Integer pageNum,Integer pageSize)throws Exception{
        if(pageNum == null || pageSize == null){
            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
        }
        MyAttentionVO myAttentionVO = new MyAttentionVO();
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        searchCondition.put("goddessId",0);
        List<Map> list = this.commonList("ddw_my_attention",null,pageNum,pageSize,searchCondition);
        Long count = this.commonCountBySearchCondition("ddw_my_attention",searchCondition);
        List<Integer>userIdList = new ArrayList<Integer>();
        for(Map map:list){
            userIdList.add((Integer) map.get("practiceId"));
        }
        if(!list.isEmpty()){
            List<UserInfoVO> userInfoList = userInfoMapper.getUserInfoList(userIdList);
            ListIterator<UserInfoVO> userInfoListIterator = userInfoList.listIterator();
            while (userInfoListIterator.hasNext()){
                userInfoListIterator.next().setFocus(true);
            }
            myAttentionVO.setUserInfoList(userInfoList);
        }
        myAttentionVO.setUserId(userId);
        myAttentionVO.setPracticeCount(count.intValue());
        return new ResponseVO(1,"成功",myAttentionVO);
    }

    public ResponseVO queryGoddessFansByUserId(int userId,Integer pageNo)throws Exception{
        Integer pageSize = 10;
        if(pageNo == null || pageSize == null){
            return new ResponseVO(-2,"提交失败,pageNo不能为空",null);
        }
        MyAttentionVO myAttentionVO = new MyAttentionVO();
        Map searchCondition = new HashMap<>();
        searchCondition.put("goddessId",userId);
        List<Map> list = this.commonList("ddw_my_attention",null,pageNo,pageSize,searchCondition);
        Long count = this.commonCountBySearchCondition("ddw_my_attention",searchCondition);
        List<Integer>userIdList = new ArrayList<Integer>();
        for(Map map:list){
            userIdList.add((Integer)map.get("userId"));
        }
        if(!list.isEmpty()){
            List<UserInfoVO> userInfoList = userInfoMapper.getUserInfoList(userIdList);
            ListIterator<UserInfoVO> userInfoListIterator = userInfoList.listIterator();
            while (userInfoListIterator.hasNext()){
                userInfoListIterator.next().setFocus(true);
            }
            myAttentionVO.setUserInfoList(userInfoList);
        }
        myAttentionVO.setUserId(userId);
        myAttentionVO.setGoddessCount(count.intValue());
        return new ResponseVO(1,"成功",myAttentionVO);
    }

    public ResponseVO queryPracticeFansByUserId(int userId,Integer pageNo)throws Exception{
        Integer pageSize = 10;
        if(pageNo == null || pageSize == null){
            return new ResponseVO(-2,"提交失败,pageNo不能为空",null);
        }
        MyAttentionVO myAttentionVO = new MyAttentionVO();
        Map searchCondition = new HashMap<>();
        searchCondition.put("practiceId",userId);
        List<Map> list = this.commonList("ddw_my_attention",null,pageNo,pageSize,searchCondition);
        Long count = this.commonCountBySearchCondition("ddw_my_attention",searchCondition);
        List<Integer>userIdList = new ArrayList<Integer>();
        for(Map map:list){
            userIdList.add((Integer)map.get("userId"));
        }
        if(!list.isEmpty()){
            List<UserInfoVO> userInfoList = userInfoMapper.getUserInfoList(userIdList);
            ListIterator<UserInfoVO> userInfoListIterator = userInfoList.listIterator();
            while (userInfoListIterator.hasNext()){
                userInfoListIterator.next().setFocus(true);
            }
            myAttentionVO.setUserInfoList(userInfoList);
        }
        myAttentionVO.setUserId(userId);
        myAttentionVO.setPracticeCount(count.intValue());
        return new ResponseVO(1,"成功",myAttentionVO);
    }

    public Long queryPracticeFansCountByUserId(Integer userId)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("practiceId",userId);
        return this.commonCountBySearchCondition("ddw_my_attention",searchCondition);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO delete(int userId, MyAttentionDTO myAttentionDTO)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        if(myAttentionDTO.getGoddessId() != 0 ){
            searchCondition.put("goddessId",myAttentionDTO.getGoddessId());
        }else if(myAttentionDTO.getPracticeId() != 0){
            searchCondition.put("practiceId",myAttentionDTO.getPracticeId());
        }
        return this.commonDeleteByParams("ddw_my_attention",searchCondition);
    }

    /**
     * 判断是否关注代练
     * @param userId
     * @param practiceId
     * @return
     * @throws Exception
     */
    public boolean isFocusPractice(Integer userId,Integer practiceId)throws Exception{
        Map<String,Object> searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        searchCondition.put("practiceId",practiceId);
        return super.commonCountBySearchCondition("ddw_my_attention", searchCondition) > 0;
    }

    /**
     * 判断是否关注女神
     * @param userId
     * @param goddessId
     * @return
     * @throws Exception
     */
    public boolean isFocusGoddess(Integer userId,Integer goddessId)throws Exception{
        Map<String,Object> searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        searchCondition.put("goddessId",goddessId);
        return super.commonCountBySearchCondition("ddw_my_attention", searchCondition) > 0;
    }

    /**
     * 查询粉丝数
     * @param userId 用户编号
     * @return
     */
    public Long getFans(Integer userId){
        Map<String,Object> searchCondition = new HashedMap();
        searchCondition.put("(goddessId="+userId+" or practiceId="+userId+")",null);
        return this.commonCountBySearchCondition("ddw_my_attention",searchCondition);
    }
}
