package edu.lgcns.team428.chungbaji_be.bookmark.ctrl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.lgcns.team428.chungbaji_be.bookmark.domain.dto.BookmarkRequestDTO;
import edu.lgcns.team428.chungbaji_be.bookmark.domain.dto.BookmarkResponseDTO;
import edu.lgcns.team428.chungbaji_be.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    // 게시글 북마크
    @PostMapping("/register")
    public ResponseEntity<BookmarkResponseDTO> register(@RequestBody BookmarkRequestDTO request) {
        System.out.println("bookmark controller post call");

        // 인증/인가 
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        BookmarkResponseDTO dto = bookmarkService.register(email,request.getPolicyId());

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // 북마크 해제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody BookmarkRequestDTO request) {
        System.out.println("bookmark controller delete call");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        bookmarkService.delete(email,request.getPolicyId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

    }

    // 북마크한 정책 리스트업
    @GetMapping("/list")
    public ResponseEntity<List<BookmarkResponseDTO>> list(BookmarkRequestDTO request) {
        System.out.println("bookmark controller list call");

        List<BookmarkResponseDTO> list = bookmarkService.listByMember(request.getMemberId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(list);
    }

}
