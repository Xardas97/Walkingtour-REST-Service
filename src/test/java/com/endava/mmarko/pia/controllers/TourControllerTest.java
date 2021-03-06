package com.endava.mmarko.pia.controllers;

import com.endava.mmarko.pia.config.TestConfig;
import com.endava.mmarko.pia.config.WebConfig;
import com.endava.mmarko.pia.models.Guide;
import com.endava.mmarko.pia.models.Tour;
import com.endava.mmarko.pia.models.User;
import com.endava.mmarko.pia.services.GuideService;
import com.endava.mmarko.pia.services.TourService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.endava.mmarko.pia.controllers.ControllerTestUtil.JSON_CONTENT_TYPE;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebConfig.class, TestConfig.class})
@WebAppConfiguration
public class TourControllerTest {
    private static final int ID = 5;
    
    private MockMvc mockMvc;
    @Autowired
    private TourService tourService;
    @Autowired
    private GuideService guideService;
    @Autowired
    private WebApplicationContext context;

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete("/tours/{id}", ID ))
                .andExpect(status().isOk());

        verify(tourService, times(1)).delete(ID);
    }

    @Test
    public void updateTest() throws Exception {
        Tour tour = new Tour("name", "description", "point", 1);

        byte[] unsavedJsonBytes = new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsBytes(tour);

        when(tourService.update(any())).thenAnswer(i -> i.getArguments()[0]);

        mockMvc.perform(put("/tours/{id}", ID )
                .contentType(JSON_CONTENT_TYPE)
                .content(unsavedJsonBytes))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.meetingPoint", is("point")))
                .andExpect(jsonPath("$.minPeople", is(1)));
    }

    @Test
    public void saveTest() throws Exception {
        Tour tour = new Tour("name", "description", "point", 1);

        byte[] unsavedJsonBytes = new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsBytes(tour);

        tour.setId(ID);
        when(tourService.save(any())).thenReturn(tour);

        mockMvc.perform(post("/tours" )
                .contentType(JSON_CONTENT_TYPE)
                .content(unsavedJsonBytes))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.meetingPoint", is("point")))
                .andExpect(jsonPath("$.minPeople", is(1)));
    }

    @Test
    public void saveConflictTest() throws Exception {
        Tour tour = new Tour("name", "description", "point", 1);

        byte[] unsavedJsonBytes = new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsBytes(tour);

        when(tourService.save(any())).thenReturn(null);

        mockMvc.perform(post("/tours" )
                .contentType(JSON_CONTENT_TYPE)
                .content(unsavedJsonBytes))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath("$.code", is(4)));
    }

    @Test
    public void findTest() throws Exception {
        Tour tour = new Tour("name", "description", "point", 1);
        when(tourService.find(1)).thenReturn(tour);

        mockMvc.perform(get("/tours/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.meetingPoint", is("point")))
                .andExpect(jsonPath("$.minPeople", is(1)));
    }

    @Test
    public void findNotFoundTest() throws Exception {
        when(tourService.find(1)).thenReturn(null);

        mockMvc.perform(get("/tours/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath("$.code", is(3)));
    }

    @Test
    public void findAlTest() throws Exception {
        List<Tour> tours = new LinkedList<>();
        Tour tour1 = new Tour("name1", "description1", "point1", 1);
        Tour tour2 = new Tour("name2", "description2", "point2", 2);
        tours.add(tour1); tours.add(tour2);

        when(tourService.findAll()).thenReturn(tours);

        mockMvc.perform(get("/tours"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[0].meetingPoint", is("point1")))
                .andExpect(jsonPath("$[0].minPeople", is(1)))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].description", is("description2")))
                .andExpect(jsonPath("$[1].meetingPoint", is("point2")))
                .andExpect(jsonPath("$[1].minPeople", is(2)));
    }

    @Test
    public void guidesByTourTest() throws Exception {
        List<Guide> guides = Arrays.asList(
                new Guide(new User("username1")),
                new Guide(new User("username2")));

        when(guideService.findByTour(ID)).thenReturn(guides);
        mockMvc.perform(get("/tours/{ID}/guides", ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].user.username", is("username1")))
                .andExpect(jsonPath("$[1].user.username", is("username2")));
    }

    @Before
    public void init(){
        reset(tourService);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
}
