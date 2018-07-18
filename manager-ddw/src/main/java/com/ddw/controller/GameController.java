package com.ddw.controller;

import com.ddw.beans.GamePO;
import com.ddw.beans.RankPO;
import com.ddw.servies.GameManagerService;
import com.ddw.servies.RankService;
import com.gen.common.services.CacheService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Jacky on 2018/5/16.
 */
@Controller
@RequestMapping("/manager/game")
public class GameController {
    private final Logger logger = Logger.getLogger(GameController.class);

    @Autowired
    private GameManagerService gameManagerService;
    @Autowired
    private RankService rankService;
    @Autowired
    private CacheService cacheService;

    @GetMapping("list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {
            model.addAttribute("gamePage", gameManagerService.findPage(pageNo));
        }catch (Exception e){
            logger.error("GameController->list",e);
        }
        return "pages/manager/game/list";
    }

    @GetMapping("to-edit")
    public String toEdit(String id,Model model){
        try {
            if(StringUtils.isNotBlank(id)) {
                GamePO gamePO = gameManagerService.selectById(id);
                model.addAttribute("gamePO", gamePO);
            }
        }catch (Exception e){
            logger.error("GameController->update",e);
        }
        return "pages/manager/game/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEdit(GamePO gamePO){
        try {
            return this.gameManagerService.saveOrUpdate(gamePO);
        }catch (Exception e){
            logger.error("GameController->doEdit",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("delete")
    @ResponseBody
    public ResponseVO delete(String id){
        try {
            return this.gameManagerService.delete(id);
        }catch (Exception e){
            logger.error("GameController->delete",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @RequestMapping("rank/list")
    public String rankList(@RequestParam(defaultValue = "1") Integer pageNo,@RequestParam(defaultValue = "1") Integer gameId, Model model){
        try {
            model.addAttribute("gameList", gameManagerService.findList());
            if(gameId == null){
                gameId = (Integer) cacheService.get("gameId");
            }else{
                cacheService.set("gameId",gameId);
            }
            model.addAttribute("gameId",gameId);
            model.addAttribute("rankList",rankService.findList(pageNo,gameId));
        }catch (Exception e){
            logger.error("GameController->rankList",e);
        }
        return "pages/manager/game/rankList";
    }

    @GetMapping("rank/to-edit")
    public String rankToEdit(String id,Integer gameId,Model model){
        try {
            RankPO rankPO = new RankPO();
            if(StringUtils.isNotBlank(id) && !id.equals("-1")) {
                rankPO = rankService.selectById(id);
            }
            if(gameId == null){
                gameId = (Integer)cacheService.get("gameId");
            }else{
                cacheService.set("gameId",gameId);
            }
            rankPO.setGameId(gameId);
            model.addAttribute("rankPO", rankPO);
        }catch (Exception e){
            logger.error("GameController->rankToEdit",e);
        }
        return "pages/manager/game/rankEdit";
    }

    @PostMapping("rank/do-edit")
    @ResponseBody
    public ResponseVO rankDoEdit(RankPO rankPO){
        try {
            return this.rankService.saveOrUpdate(rankPO);
        }catch (Exception e){
            logger.error("GameController->rankDoEdit",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("rank/delete")
    @ResponseBody
    public ResponseVO rankDelete(String id){
        try {
            return this.rankService.delete(id);
        }catch (Exception e){
            logger.error("GameController->rankDelete",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
}
