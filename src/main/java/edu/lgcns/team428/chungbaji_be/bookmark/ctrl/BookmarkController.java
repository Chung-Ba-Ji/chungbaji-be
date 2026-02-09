package edu.lgcns.team428.chungbaji_be.bookmark.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.lgcns.team428.chungbaji_be.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService ;
}
