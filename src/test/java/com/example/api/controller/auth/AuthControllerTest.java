package com.example.api.controller.auth;

import com.example.api.controller.auth.dto.TokenDto;
import com.example.api.controller.auth.request.TokenRequest;
import com.example.api.service.auth.AuthService;
import com.example.config.jwt.JwtTokenProvider;
import com.example.domain.member.MemberRepository;
import com.example.fixture.auth.TokenFixture;
import com.example.util.jwt.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.common.jwt.TokenConstants.TOKEN_HEADER;
import static com.example.fixture.auth.TokenFixture.*;
import static com.example.fixture.member.MemberConstant.TEST_EMAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("이메일과 패스워드로 accessToken을 발급받는다.")
    @Test()
    void getAuthentication() throws Exception {
        //given
        TokenRequest tokenRequest = createDefaultTokenRequest();
        TokenDto tokenMockDto = TokenDto.builder()
                                        .accessToken("accessToken")
                                        .reflashToken(null)
                                        .build();
        given(authService.getToken(any())).willReturn(tokenMockDto);

        //when//then
        mockMvc.perform(post("/api/authentication")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(header().exists(TOKEN_HEADER));
    }


    @DisplayName("유효하지 않은 이메일로 token을 요청할 경우 에러가 발생한다.")
    @Test()
    void getAuthenticationWithInvalidEmail() throws Exception {
        //given
        String invalidEmail = "invalidt";
        String validPassword = "test123!@#";
        TokenRequest tokenRequest = createTokenRequest(invalidEmail,validPassword);

        //when//then
        mockMvc.perform(post("/api/authentication")
                        .content(objectMapper.writeValueAsString(tokenRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("이메일 형식에 맞지 않습니다."));
    }

    @DisplayName("패스워드를 입력하지 않고 token을 요청할 경우 에러가 발생한다.")
    @Test()
    void getAuthenticationWithInvalidPassword() throws Exception {
        //given
        String invalidPassword = "";
        TokenRequest tokenRequest = createTokenRequest(TEST_EMAIL,invalidPassword);

        //when//then
        mockMvc.perform(post("/api/authentication")
                        .content(objectMapper.writeValueAsString(tokenRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("패스워드는 필수 입력 값입니다."));
    }



}