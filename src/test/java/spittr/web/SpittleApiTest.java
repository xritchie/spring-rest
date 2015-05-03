package spittr.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import spittr.Spitter;
import spittr.Spittle;
import spittr.api.MockApiController;
import spittr.api.SpittleApiController;
import spittr.config.AmqpConfig;
import spittr.config.RootConfig;
import spittr.config.SpitterWebInitializer;
import spittr.config.SpringDataJpaConfig;
import spittr.data.SpitterRepository;
import spittr.data.SpittleRepository;

import java.util.Date;

import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by shawnritchie on 19/04/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={WebConfig.class, RootConfig.class, AmqpConfig.class})
public class SpittleApiTest {

    private MockMvc mockMvc;

    @Autowired
    SpittleRepository spittleRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void mockTest() throws Exception {
        MvcResult results = mockMvc.perform(get("/mock/")).andReturn();
        Assert.notNull(results.getResponse());
    }

    @Test
    public void mockSpittleCreateTest() throws Exception {
        Spittle spittle = new Spittle(1L, new Spitter(1L, "username", "password", "fullName",
                "test@gmail.com", true), "test", new Date());

        MvcResult results =
            mockMvc.perform(get("/mock/create"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        Assert.notNull(results.getAsyncResult());
        Assert.notNull(results.getAsyncResult().toString());
        System.out.println("Resutls: " + ((Spittle)results.getAsyncResult()).getMessage());
    }

    @Test
    public void localTest() throws Exception {
        MockApiController controller = new MockApiController();
        MockMvc localMvc = standaloneSetup(controller).build();

        MvcResult results = localMvc.perform(get("/mock/")).andReturn();
        Assert.notNull(results.getResponse());
    }

    @Test
    public void localRestApiTest() throws Exception {
        SpittleApiController controller = new SpittleApiController(spittleRepository);
        MockMvc localMvc = standaloneSetup(controller).build();

        MvcResult results = localMvc.perform(get("/spittles/1/")).andReturn();
        Assert.notNull(results.getResponse());
    }

    @Test
    public void mockRestApiTest() throws Exception {
        MvcResult results = mockMvc.perform(get("/spittles/1/")).andReturn();
        Assert.notNull(results.getResponse());
    }

    @Test
    public void restApiTest() throws Exception {
        mockMvc.perform(get("/spittles/1/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));
    }

}
