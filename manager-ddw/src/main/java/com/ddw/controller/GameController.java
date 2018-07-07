package com.ddw.controller;

import com.ddw.services.GameService;
import com.ddw.servies.RankService;
import com.gen.common.beans.GamePO;
import com.gen.common.beans.RankPO;
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
    private GameService gameService;
    @Autowired
    private RankService rankService;
    @Autowired
    private CacheService cacheService;

    @GetMapping("list")
    public String list(Model model){
        try {
            model.addAttribute("gameList",gameService.findList());
        }catch (Exception e){
            logger.error("GameController->list",e);
        }
        return "pages/manager/game/list";
    }

    @GetMapping("to-edit")
    public String toEdit(String id,Model model){
        try {
            if(StringUtils.isNotBlank(id)) {
                GamePO gamePO = gameService.selectById(id);
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
            return this.gameService.saveOrUpdate(gamePO);
        }catch (Exception e){
            logger.error("GameController->doEdit",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("delete")
    @ResponseBody
    public ResponseVO delete(String id){
        try {
            return this.gameService.delete(id);
        }catch (Exception e){
            logger.error("GameController->delete",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @RequestMapping("rank/list")
    public String rankList(@RequestParam(defaultValue = "1") String gameId, Model model){
        try {
            model.addAttribute("gameList",gameService.findList());
            if(gameId == null){
                gameId = (String) cacheService.get("gameId");
            }else{
                cacheService.set("gameId",gameId);
            }
            model.addAttribute("gameId",Integer.valueOf(gameId));
            model.addAttribute("rankList",rankService.findList(gameId));
        }catch (Exception e){
            logger.error("GameController->rankList",e);
        }
        return "pages/manager/game/rankList";
    }

    @GetMapping("rank/to-edit")
    public String rankToEdit(String id,Model model){
        try {
            if(StringUtils.isNotBlank(id)) {
                RankPO rankPO = rankService.selectById(id);
                model.addAttribute("rankPO", rankPO);
            }
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
