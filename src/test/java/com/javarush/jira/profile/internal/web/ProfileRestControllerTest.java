package com.javarush.jira.profile.internal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.profile.ContactTo;
import com.javarush.jira.profile.ProfileTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.Set;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = ProfileRestController.REST_URL;
    private static final String TEST_USER_EMAIL = "user@gmail.com";

    @Autowired
    private ObjectMapper objectMapper;

    private ProfileTo validProfile;
    private ProfileTo invalidProfile;

    @BeforeEach
    void setUp() {
        validProfile = createProfile(
                1L,
                Set.of("TASK_CREATED", "TASK_UPDATED"),
                Set.of(new ContactTo("telegram", "@testuser"))
        );

        invalidProfile = createProfile(1L, null, null);
    }

    private ProfileTo createProfile(Long id, Set<String> notifications, Set<ContactTo> contacts) {
        return new ProfileTo(id, notifications, contacts);
    }

    @Test
    @WithUserDetails(value = TEST_USER_EMAIL)
    void getProfile_success() throws Exception {
        perform(get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mailNotifications").isArray())
                .andExpect(jsonPath("$.contacts").isArray())
                .andExpect(jsonPath("$.contacts.length()").value(3))
                .andExpect(jsonPath("$.contacts[*].code").value(hasItems("skype", "mobile", "website")));
    }

    @Test
    void getProfile_unauthorized() throws Exception {
        perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = TEST_USER_EMAIL)
    void updateProfile_success() throws Exception {

        ProfileTo profileToUpdate = createProfile(
                1L,
                Set.of("assigned", "deadline"),
                Set.of(new ContactTo("tg", "@testuser"), new ContactTo("mobile", "+380123456789"))
        );

        String json = objectMapper.writeValueAsString(profileToUpdate);

        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateProfile_unauthorized() throws Exception {
        String json = objectMapper.writeValueAsString(validProfile);

        perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }
}