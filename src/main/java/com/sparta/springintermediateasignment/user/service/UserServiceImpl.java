package com.sparta.springintermediateasignment.user.service;

import com.sparta.springintermediateasignment.exceptoins.InvalidIdException;
import com.sparta.springintermediateasignment.exceptoins.PasswordMissmatchException;
import com.sparta.springintermediateasignment.schedule.entity.Schedule;
import com.sparta.springintermediateasignment.schedule.repository.ScheduleRepository;
import com.sparta.springintermediateasignment.user.dto.JwtTokenResponseDto;
import com.sparta.springintermediateasignment.user.dto.LoginRequestDto;
import com.sparta.springintermediateasignment.user.dto.ManagerAddRequestDto;
import com.sparta.springintermediateasignment.user.dto.SignupRequestDto;
import com.sparta.springintermediateasignment.user.dto.UserDto;
import com.sparta.springintermediateasignment.user.entity.ScheduleManager;
import com.sparta.springintermediateasignment.user.entity.User;
import com.sparta.springintermediateasignment.user.enums.UserRole;
import com.sparta.springintermediateasignment.user.repository.ScheduleManagerRepository;
import com.sparta.springintermediateasignment.user.repository.UserRepository;
import com.sparta.springintermediateasignment.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleManagerRepository scheduleManagerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Override
    @Transactional(readOnly = false)
    public Long save(UserDto userDto) {
        User user = User.of(userDto);
        userRepository.save(user);
        return user.getId();
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Long id) {
        User user = getUser(id);

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = false)
    public UserDto update(Long id, UserDto userDto) {
        User user = getUser(id);

        // 수정
        userRepository.save(user);
        return userDto;
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
            .stream()
            .map(UserDto::of)
            .toList();
    }

    @Override
    public UserDto findById(Long id) {
        return UserDto.of(getUser(id));
    }

    @Override
    @Transactional(readOnly = false)
    public void addManager(ManagerAddRequestDto userDto) {
        User user = getUser(userDto.getManagerId());
        Schedule schedule = scheduleRepository.findById(userDto.getScheduleId())
            .orElseThrow(() -> new InvalidIdException("일정"));

        ScheduleManager scheduleManager = ScheduleManager.builder()
            .user(user)
            .schedule(schedule)
            .build();

        scheduleManagerRepository.save(scheduleManager);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteManager(ManagerAddRequestDto userDto) {
        ScheduleManager del = scheduleManagerRepository.findByUserId(userDto.getManagerId())
            .orElseThrow(() -> new InvalidIdException("일정 담당자"));
        scheduleManagerRepository.delete(del);
    }

    @Transactional(readOnly = false)
    public JwtTokenResponseDto signup(SignupRequestDto requestDto, HttpServletResponse res) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByName(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 이메일 중복 확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 권한 확인
        UserRole role = UserRole.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRole.ADMIN;
        }

        // 사용자 등록 후 jwt 생성 반환
        User user = User.builder()
            .name(requestDto.getUsername())
            .email(requestDto.getEmail())
            .password(password)
            .role(role)
            .build();
        String token = jwtUtil.createToken(username, role);
        userRepository.save(user);

        return new JwtTokenResponseDto(token);
    }

    @Override
    public JwtTokenResponseDto login(LoginRequestDto requestDto, HttpServletResponse res) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원있는지 확인
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        // 비밀번호 확인
        System.out.println("user.getPassword() = " + user.getPassword());
        System.out.println("password = " + password);

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new PasswordMissmatchException();
        }

        // 사용자 권한 확인
        UserRole role = user.getRole()
            .equals(UserRole.USER) ? UserRole.USER : UserRole.ADMIN;

        // 사용자 등록 후 jwt 생성 반환
        String token = jwtUtil.createToken(user.getName(), role);
        userRepository.save(user);
        return new JwtTokenResponseDto(token);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
    }

}
