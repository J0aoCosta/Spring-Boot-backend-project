package springbootbuyitem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.springbootbuyitem.BuyItemApplication;
import com.training.springbootbuyitem.entity.response.user.UserLoginResponseDto;
import com.training.springbootbuyitem.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(classes = BuyItemApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ControllerItemTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private static final String ITEM_NAME = "banana";
    private static final long ID = 1l;

    private String USERNAME = "username1";
    private String PASSWORD = "password1";

    private String authToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public void onSetUp() throws Exception {
        log.info("Before All");
        System.out.println("Before All");
        String loginPersonUrl = "http://localhost:8888/login";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject = new JSONObject();
        personJsonObject.put("username", USERNAME);
        personJsonObject.put("password", PASSWORD);

        HttpEntity<String> request =
                new HttpEntity<String>(personJsonObject.toString(), headers);

        UserLoginResponseDto personResultAsJsonStr = restTemplate.postForObject(loginPersonUrl, request, UserLoginResponseDto.class);
        authToken = personResultAsJsonStr.getJwtToken();
        log.info("JWTTOken --- " + authToken);
        System.out.println("JWTTOken --- " + authToken);
    }


    @Test
    @DisplayName("GET '/' - default msg")
    public void defaultMsgTest() throws Exception {
        this.mockMvc.perform(get("/")
                .header("Authorization", "Bearer " + authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("This is what i was looking for")));
    }

//    @Test
//    @DisplayName("POST '/' - create item")
//    public void createItemTest() throws Exception {
//        CreateItemRequestDto requestItem = CreateItemRequestDto.builder()
//                .name(ITEM_NAME)
//                .priceTag(BigDecimal.TEN.doubleValue())
//                .stock(BigInteger.TEN.intValue())
//                .market("PT")
//                .build();
//
//        Item responseItem = Item.builder()
//                .itemUid(ID)
//                .build();
//
//        when(itemService.save(any())).thenReturn(responseItem);
//
//        this.mockMvc.perform(post("/item")
//                .content(asJsonString(requestItem)))
//                .andExpect(status().isCreated());
//    }
//
//    static String asJsonString(final Object obj) {
//        try {
//            return new ObjectMapper().writeValueAsString(obj);
//        } catch (Exception e) {
//            throw new RuntimeException();
//        }
//    }

}
