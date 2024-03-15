package com.sofiatech.kdg.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofiatech.kdg.dto.*;
import com.sofiatech.kdg.entities.Token;
import com.sofiatech.kdg.entities.User;
import com.sofiatech.kdg.enumerations.Role;
import com.sofiatech.kdg.enumerations.TokenType;
import com.sofiatech.kdg.repositories.TokenRepository;
import com.sofiatech.kdg.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public RegisterResponse register(RegisterRequest request) {
        var tmpUser = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (tmpUser!=null)
        {
            return RegisterResponse.builder()
                    .message("user with email: "+request.getEmail()+" already exists.")
                    .build();
        }
        String password = UUID.randomUUID().toString();
        log.info(password);
        String content = buildEmail(request.getFirstName(),password,"");

        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .isActive(true)
                .build();
        var savedUser = userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        emailService.send(request.getEmail(),content);
        return RegisterResponse.builder()
                .message("Account created successfully.")
                .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e){
            return AuthenticationResponse.builder()
                    .message("Invalid email or password")
                    .build();
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        if (!user.isActive()){
            return AuthenticationResponse.builder()
                    .message("Account is disabled")
                    .build();
        }
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .message("Authentication is successful")
                .build();
    }
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType("BEARER")
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    public HttpResponse forgetPassword(String email){
        //todo develop function
        return new HttpResponse();
    }
    public User currentlyAuthenticatedUser()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);

    }
    private String buildEmail(String name,  String password,String link) {
        return "<div style=\"background-color:#faf5ff\"><div style=\"background:#fff;background-color:#fff;margin:0 auto;max-width:600px\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background:#fff;background-color:#fff;width:100%\"><tbody><tr><td style=\"direction:ltr;font-size:0;padding:0;padding-bottom:0;padding-left:0;padding-right:0;padding-top:0;text-align:center\"><div style=\"background:#fff;background-color:#fff;margin:0 auto;max-width:600px\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background:#fff;background-color:#fff;width:100%\"><tbody><tr><td style=\"border:undefined solid undefined;direction:ltr;font-size:0;padding:20px 0;padding-bottom:30px;padding-left:20px;padding-right:20px;padding-top:20px;text-align:center\"><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr><td style=\"background-color:transparent;border:0 solid undefined;vertical-align:top;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0;padding:0;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0;word-break:break-word\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0\"><tbody><tr><td style=\"width:44px\"><amp-img alt=\"Alternate image text\" height=\"256\" layout=\"responsive\" src=\"logo.svg\" style=\"border: 0px solid transparent; border-radius: 0px; display: block; outline: 0px; text-decoration: none; width: 100%; font-size: 13px; --loader-delay-offset: 129ms !important;\" width=\"256\" class=\"i-amphtml-element i-amphtml-layout-responsive i-amphtml-layout-size-defined i-amphtml-built i-amphtml-layout\" i-amphtml-layout=\"responsive\"><i-amphtml-sizer slot=\"i-amphtml-svc\" style=\"height: 50px;\"></i-amphtml-sizer></amp-img></td></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table></div></td></tr></tbody></table></div><div style=\"background:#477eeb;background-color:#477eeb;margin:0 auto;max-width:600px\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background:#477eeb;background-color:#477eeb;width:100%\"><tbody><tr><td style=\"border:0 solid #1e293b;direction:ltr;font-size:0;padding:20px 0;padding-bottom:10px;padding-left:10px;padding-right:10px;padding-top:10px;text-align:center\"><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr><td style=\"background-color:transparent;border:0 solid transparent;vertical-align:top;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0;padding:0;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0;word-break:break-word\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0\"><tbody><tr><td style=\"width:87px\"><amp-img alt=\"Image\" height=\"50\" layout=\"responsive\" src=\"logo.svg\" style=\"border: 0px solid rgb(30, 41, 59); border-radius: 0px; display: block; outline: 0px; text-decoration: none; width: 100%; font-size: 13px; --loader-delay-offset: 129ms !important;\" width=\"500\" class=\"i-amphtml-element i-amphtml-layout-responsive i-amphtml-layout-size-defined i-amphtml-built i-amphtml-layout\" i-amphtml-layout=\"responsive\"><i-amphtml-sizer slot=\"i-amphtml-svc\" style=\"padding-top: 100%;\"></i-amphtml-sizer></amp-img></td></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table></div></td></tr></tbody></table></div><div style=\"background:#fff;background-color:#fff;margin:0 auto;max-width:600px\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background:#fff;background-color:#fff;width:100%\"><tbody><tr><td style=\"border:0 solid #1e293b;direction:ltr;font-size:0;padding:20px 0;padding-bottom:40px;padding-left:30px;padding-right:30px;padding-top:20px;text-align:center\"><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background-color:transparent;border:0 solid transparent;vertical-align:top\" width=\"100%\"><tbody><tr><td align=\"left\" style=\"font-size:0;padding:10px 25px;padding-top:0;padding-right:0;padding-bottom:24px;padding-left:0;word-break:break-word\"><div style=\"font-family:Helvetica;font-size:16px;font-weight:400;letter-spacing:0;line-height:1.5;text-align:left;color:#1e293b\"><p style=\"text-align:justify\"><span style=\"font-size:36px\">Email verification</span></p><p style=\"text-align:justify\">&nbsp;</p><p style=\"text-align:justify\"><span style=\"font-size:16px\">Hi "+name+",</span></p><p style=\"text-align:justify\"><span style=\"font-size:16px\">You're almost set to start using Kubernetes Deployment Generator .You'll find your account password below. Simply click the link to access your account.</span>\n" +
                "</p>\n" +
                "<p>"+password+"</p><p>&nbsp;</p></div></td></tr><tr><td align=\"center\" style=\"font-size:0;padding:0;word-break:break-word\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse:separate;width:auto;line-height:100%\"><tbody><tr><td align=\"center\" bgcolor=\"#EB4747\" role=\"presentation\" style=\"border-radius:4px;border:0 solid none;height:auto;background:#eb4747;padding:0\"><a href=\""+link+"\" style=\"display:inline-block;background:#477eeb;color:#fff;font-family:Helvetica;font-size:16px;font-weight:400;line-height:1;letter-spacing:1px;margin:0;text-decoration:none;text-transform:none;padding:12px 24px 12px 24px;border-radius:4px\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://t.mmtrkr.com/clicks/amp/f28870a9-ec3f-416c-aad7-d6dfdf6b1cb5/354dfab3-5609-4d3d-8f7f-64eae167948f/5899f07a-304d-55d9-86b6-ee22788187e2&amp;source=gmail&amp;ust=1709906986464000&amp;usg=AOvVaw01JQQmI7Odb6PUvX75L8Gz\" rel=\"noreferrer noopener\"><strong>login</strong></a></td></tr></tbody></table></td></tr></tbody></table></div></td></tr></tbody></table></div><div style=\"background:#fff;background-color:#fff;margin:0 auto;max-width:600px\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background:#fff;background-color:#fff;width:100%\"><tbody><tr><td style=\"border:0 solid #1e293b;direction:ltr;font-size:0;padding:20px 0;padding-bottom:0;padding-left:50px;padding-right:50px;padding-top:0;text-align:center\"><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr><td style=\"background-color:transparent;border:0 solid transparent;vertical-align:top;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr><td style=\"font-size:0;padding:10px 25px;padding-top:10px;padding-right:0;padding-bottom:10px;padding-left:0;word-break:break-word\"><p style=\"border-top:solid 1px #477eeb;font-size:1px;margin:0 auto;width:100%\"></p></td></tr></tbody></table></td></tr></tbody></table></div></td></tr></tbody></table></div><div style=\"background:0 0;background-color:transparent;margin:0 auto;max-width:600px\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background:0 0;background-color:transparent;width:100%\"><tbody><tr><td style=\"border:0 solid #1e293b;direction:ltr;font-size:0;padding:20px 0;padding-bottom:30px;padding-left:20px;padding-right:20px;padding-top:20px;text-align:center\"><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background-color:transparent;border:0 solid transparent;vertical-align:top\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0;padding:10px 25px;padding-top:12px;padding-right:0;padding-bottom:12px;padding-left:0;word-break:break-word\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"float:none;display:inline-table\"><tbody><tr><td style=\"padding:0 5px 0 5px;vertical-align:middle\"></td></tr></tbody></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"float:none;display:inline-table\"><tbody><tr><td style=\"padding:0 5px 0 5px;vertical-align:middle\"></td></tr></tbody></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"float:none;display:inline-table\"><tbody><tr><td style=\"padding:0 5px 0 5px;vertical-align:middle\"></td></tr></tbody></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"float:none;display:inline-table\"><tbody><tr><td style=\"padding:0 5px 0 5px;vertical-align:middle\"></td></tr></tbody></table></td></tr><tr><td align=\"left\" style=\"font-size:0;padding:10px 25px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:5px;word-break:break-word\"><div style=\"font-family:Helvetica;font-size:14px;font-weight:400;letter-spacing:.4px;line-height:1.4;text-align:left;color:#444\"><p style=\"text-align:center\">Sofia-technologies</p></div></td></tr><tr><td style=\"background:0 0;font-size:0;word-break:break-word\"><div style=\"height:24px\">&nbsp;</div></td></tr><tr><td align=\"left\" style=\"font-size:0;padding:10px 25px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:5px;word-break:break-word\"><div style=\"font-family:Helvetica;font-size:12px;font-weight:400;letter-spacing:.4px;line-height:1.4;text-align:left;color:#444\"><p style=\"text-align:center\"><strong>| Privacy Policy | Contact Details | &nbsp;&nbsp;</strong></p></div></td></tr></tbody></table></div></td></tr></tbody></table></div></td></tr></tbody></table></div></div>";
    }
}
