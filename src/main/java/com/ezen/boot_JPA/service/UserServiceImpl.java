package com.ezen.boot_JPA.service;

import com.ezen.boot_JPA.dto.UserDTO;
import com.ezen.boot_JPA.entity.AuthUser;
import com.ezen.boot_JPA.entity.User;
import com.ezen.boot_JPA.repository.AuthUserRepository;
import com.ezen.boot_JPA.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final AuthUserRepository authUserRepository;

    @Override
    public String register(UserDTO userDTO) {
        String email = userRepository.save(convertDtoToEntity(userDTO)).getEmail();
        if(email != null){
            authUserRepository.save(convertDtoToAuthEntity(userDTO));
        }
        return email;
    }

    @Transactional
    @Override
    public UserDTO selectEmail(String username) {
        // findById : ID(PK)를 조건으로 해당 객체를 검색 후 리턴(Optional)
        // 내가 검색하고자 하는 조건이 ID가 아닐 경우. Repository 등록 후 사용
        // optional.isPresent() : 값이 있는지 확인 true / false
        // optional.isEmpty() : 값이 비었는지 확인 true / false
        Optional<User> optional = userRepository.findById(username);
        List<AuthUser> authUserList = authUserRepository.findByEmail(username);
        if(optional.isPresent()){
            UserDTO userDTO = convertEntityToDto(optional.get(),
                    authUserList
                            .stream()
                            .map(u -> convertEntityToAuthDto(u))
                            .toList()
            );
            log.info(">>> userDTO >>> {}", userDTO);
            return userDTO;
        }
        return null;
    }

    @Override
    public boolean updateLastLogin(String authEmail) {
        Optional<User> optional = userRepository.findById(authEmail);
        if(optional.isPresent()){
            User user = optional.get();
            // LocalDateTime.now() => 현재 날짜 시간
            user.setLastLogin(LocalDateTime.now());
            String email = userRepository.save(user).getEmail();
            return email == null ? false : true;
        }
        return false;
    }

    @Override
    public List<UserDTO> getList() {
    // findAll : select * from user;
        List<User> userList = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();
        for(User user : userList){
            List<AuthUser> authUserList = authUserRepository.findByEmail(user.getEmail());
            UserDTO userDTO = convertEntityToDto(user,
                    authUserList.stream()
                            .map(u -> convertEntityToAuthDto(u))
                            .toList());
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    @Override
    public String modify(UserDTO userDTO) {
        Optional<User> optional = userRepository.findById(userDTO.getEmail());
        if(optional.isPresent()){
            User user = optional.get();
            if(userDTO.getPwd().length() > 0){
                user.setPwd(userDTO.getPwd());
            }
            user.setNickName(userDTO.getNickName());
            return userRepository.save(user).getEmail();
        }
        return null;
    }

    @Override
    public void remove(String email) {
        Optional <User> optional = userRepository.findById(email);
        if(optional.isPresent()){
            User user = optional.get();
            List<AuthUser> authUserList = authUserRepository.findByEmail(user.getEmail());
            for(AuthUser authUser : authUserList){
                authUserRepository.deleteById(authUser.getId());
            }
        }
        userRepository.deleteById(email);
    }

}
