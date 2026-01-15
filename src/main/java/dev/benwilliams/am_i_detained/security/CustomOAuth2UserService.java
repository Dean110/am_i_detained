package dev.benwilliams.am_i_detained.security;

import dev.benwilliams.am_i_detained.entity.User;
import dev.benwilliams.am_i_detained.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String oauthId = oauth2User.getAttribute("sub") != null ? 
            oauth2User.getAttribute("sub") : oauth2User.getAttribute("id");
        String email = oauth2User.getAttribute("email");
        
        User user = userRepository.findByOauthProviderAndOauthId(provider, oauthId)
            .orElseGet(() -> {
                User newUser = User.builder()
                    .oauthProvider(provider)
                    .oauthId(oauthId)
                    .email(email)
                    .build();
                return userRepository.save(newUser);
            });
        
        return new CustomOAuth2User(oauth2User, user);
    }
}
