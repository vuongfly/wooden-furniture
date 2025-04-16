package com.woodenfurniture.authentication.controller;

import com.nimbusds.jose.JOSEException;
import com.woodenfurniture.authentication.dto.AuthenticationRequest;
import com.woodenfurniture.dto.request.IntrospectRequest;
import com.woodenfurniture.dto.request.LogoutRequest;
import com.woodenfurniture.dto.request.RefreshRequest;
import com.woodenfurniture.dto.response.ApiResponse;
import com.woodenfurniture.authentication.dto.AuthenticationResponse;
import com.woodenfurniture.dto.response.IntrospectResponse;
import com.woodenfurniture.authentication.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/token-info")
    ApiResponse<AuthenticationResponse> getTokenInfo() {
        // Build a response containing token details
        AuthenticationResponse tokenInfo = authenticationService.getTokenInfo();
        return ApiResponse.<AuthenticationResponse>builder()
                .result(tokenInfo)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
} 