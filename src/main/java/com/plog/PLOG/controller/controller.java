package com.plog.PLOG.controller;

import com.plog.PLOG.dto.BoardDTO;
import com.plog.PLOG.dto.LocationDTO;
import com.plog.PLOG.dto.dto;
import com.plog.PLOG.entity.BoardEntity;
import com.plog.PLOG.repository.BoardRepository;
import com.plog.PLOG.repository.repository;
import com.plog.PLOG.service.BoardService;
import com.plog.PLOG.service.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class controller {

    private final service s;
    private final repository u_r;
    private final BoardService b_s;
    private final BoardRepository b_r;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public controller(service s, repository u_r, BoardService b_s, BoardRepository b_r) {
        this.s = s;
        this.u_r = u_r;
        this.b_s = b_s;
        this.b_r = b_r;
    }

    @GetMapping("/")
    public String home(Model model) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String nickname = s.getCurrentName();
        List<BoardDTO> boardList = b_s.readallBoards();

        model.addAttribute("name", name);
        model.addAttribute("nickname", nickname);
        model.addAttribute("BOARDLIST", boardList);
        model.addAttribute("kakaoApiKey", kakaoApiKey);

        return "home";
    }

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "signup";
    }

    @PostMapping("/signupProc")
    public String signupProc(dto d) {
        s.toEntity(d);
        return "redirect:/login";
    }

    @GetMapping("/user/update/{username}")
    public String updatePage(@PathVariable String username, Model model) {
        if (!s.isAccess(username)) return "redirect:/login";

        dto userDto = s.readOneUser(username);
        String nickname = s.getCurrentName();

        model.addAttribute("USER", userDto);
        model.addAttribute("nickname", nickname);

        return "update";
    }

    @PostMapping("/user/update/{username}")
    public String updateProcess(@PathVariable String username, dto updatedDto) {
        if (!s.isAccess(username)) return "redirect:/login";

        s.updateOneUser(updatedDto, username);
        return "redirect:/";
    }

    @GetMapping("/user/my_blog/{username}")
    public String myBlog(@PathVariable String username, Model model) {
        if (!s.isAccess(username)) return "redirect:/";

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = s.getCurrentUserId();
        String nickname = s.getCurrentName();
        List<BoardDTO> boardList = b_s.readOneALLBoard(userId);

        model.addAttribute("name", name);
        model.addAttribute("nickname", nickname);
        model.addAttribute("BOARDLIST", boardList);
        model.addAttribute("kakaoApiKey", kakaoApiKey);

        return "my_blog";
    }

    @GetMapping("/user/my_blog/{username}/write")
    public String writeBlog(@PathVariable String username, Model model) {
        if (!s.isAccess(username)) return "redirect:/";

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String nickname = s.getCurrentName();

        model.addAttribute("name", name);
        model.addAttribute("nickname", nickname);
        model.addAttribute("kakaoApiKey", kakaoApiKey);

        return "my_blog_write";
    }

    @PostMapping("/user/my_blog/{username}/write")
    public String writeBlogProc(@PathVariable String username, @ModelAttribute BoardDTO boardDTO) {
        if (!s.isAccess(username)) return "redirect:/";

        b_s.createOneBoard(boardDTO, boardDTO.getLocations());
        return "redirect:/user/my_blog/" + username;
    }

    @GetMapping("/board/{boardid}")
    public String readBoard(@PathVariable Long boardid, Model model) {
        BoardEntity board = b_r.findById(boardid).orElseThrow();
        List<LocationDTO> locations = b_s.readBoard(boardid);

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        String nickname = s.getCurrentName();

        model.addAttribute("name", name);
        model.addAttribute("nickname", nickname);
        model.addAttribute("locations", locations);
        model.addAttribute("title", board.getTitle());
        model.addAttribute("author", board.getEntity().getName());
        model.addAttribute("authorID", board.getEntity().getUsername());
        model.addAttribute("role", role);
        model.addAttribute("kakaoApiKey", kakaoApiKey);

        return "read_board";
    }

    @GetMapping("/board/{boardid}/update")
    public String boardUpdate(@PathVariable Long boardid, Model model) {
        if (!b_s.isAccess(boardid)) return "redirect:/";

        BoardEntity board = b_r.findById(boardid).orElseThrow();
        List<LocationDTO> locations = b_s.readBoard(boardid);

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        String nickname = s.getCurrentName();

        model.addAttribute("name", name);
        model.addAttribute("nickname", nickname);
        model.addAttribute("locations", locations);
        model.addAttribute("title", board.getTitle());
        model.addAttribute("author", board.getEntity().getName());
        model.addAttribute("authorID", board.getEntity().getUsername());
        model.addAttribute("role", role);
        model.addAttribute("kakaoApiKey", kakaoApiKey);


        return "update_board";
    }

    @PostMapping("/board/{boardid}/update")
    public String boardUpdateProc(@PathVariable Long boardid, @ModelAttribute BoardDTO dtos) {
        if (!b_s.isAccess(boardid)) return "redirect:/";
        String newTitle = dtos.getTitle();
        b_s.updateBoard(boardid, dtos.getLocations(), newTitle);
        return "redirect:/board/" + boardid;
    }

    @PostMapping("/board/{boardid}/delete")
    public String boardDeleteProc(@PathVariable Long boardid) {
        if (!b_s.isAccess(boardid)) return "redirect:/";

        b_s.deleteboard(boardid);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return "redirect:/user/my_blog/" + username;
    }
}
