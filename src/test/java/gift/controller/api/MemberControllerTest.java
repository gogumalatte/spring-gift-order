package gift.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.MemberLoginRequest;
import gift.dto.MemberRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerSuccess() throws Exception {
        MemberRegisterRequest request = new MemberRegisterRequest("test@example.com", "password123");
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("로그인 성공 및 실패 테스트")
    void loginSuccessAndFail() throws Exception {
        MemberRegisterRequest registerRequest = new MemberRegisterRequest("login@example.com", "password123");
        mockMvc.perform(post("/api/members/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)));

        MemberLoginRequest loginSuccessRequest = new MemberLoginRequest("login@example.com", "password123");
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginSuccessRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists());

        MemberLoginRequest loginFailRequest = new MemberLoginRequest("login@example.com", "wrongpassword");
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginFailRequest)))
            .andExpect(status().isForbidden());
    }
}