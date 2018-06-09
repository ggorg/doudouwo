import com.ApiApplication;
import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.UserInfoVO;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=ApiApplication.class)
@WebAppConfiguration
public class BidTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        //       mvc = MockMvcBuilders.standaloneSetup(new TestController()).build();
        mvc = MockMvcBuilders.webAppContextSetup(context).build();//建议使用这种
    }
    @Test
    public void testBid()throws Exception{
        System.out.println("请求登录");
        Map map=new HashMap();
        map.put("openid","test123");
        map.put("registerType","1");
        String resString=mvc.perform(MockMvcRequestBuilders.post("/ddwapp/user/save")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSON(map).toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
                /*.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")));*/
        UserInfoVO userInfo=JSONObject.parseObject(resString, UserInfoVO.class);
        System.out.println(resString);
        System.out.println("选择门店");
        map=new HashMap();
        map.put("storeId",1);
        resString=mvc.perform(MockMvcRequestBuilders.post("/ddwapp/stores/choose/"+userInfo.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSON(map).toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
                /*.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")));*/
        System.out.println(resString);
        ResponseApiVO res=JSONObject.parseObject(resString, ResponseApiVO.class);

        /*map=new HashMap();
        map.put("langlat","116.493956,39.960963");
        resString=mvc.perform(MockMvcRequestBuilders.post("/ddwapp/liveradio/queryLiveRadioList/"+userInfo.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSON(map).toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
                *//*.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")));*//*
        System.out.println(resString);
        JSONObject  json= JSONObject.parseObject(resString);
        json.getJSONObject("data").getJSONArray("list");*/

    map=new HashMap();
        map.put("code","10");
        resString=mvc.perform(MockMvcRequestBuilders.post("/ddwapp/liveradio/selectLiveRadioRoom/"+userInfo.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSONObject.toJSON(map).toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

        System.out.println(resString);


    }
}
