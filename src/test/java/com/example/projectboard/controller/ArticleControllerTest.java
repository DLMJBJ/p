package com.example.projectboard.controller;

import com.example.projectboard.config.SecurityConfig;
import com.example.projectboard.domain.type.SearchType;
import com.example.projectboard.dto.ArticleWithCommentsDto;
import com.example.projectboard.dto.UserAccountDto;
import com.example.projectboard.service.ArticleService;
import com.example.projectboard.service.PaginationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("View Controller")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    private final MockMvc mvc;
    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;
    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }
    @DisplayName("View, GET 게시판 정상 호출")
    @Test
    public void givenNoting_whenRequestArticlesView_thenArticlesView() throws Exception {
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());
        // When & Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));
        then(articleService).should().getArticle(articleId);
    }
    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesPage_thenReturnsArticlesPage() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(
                        get("/articles")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attributeExists("searchType"));


        then(articleService).should().searchArticles(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("View, GET 단일 게시판 정상 호출")
    @Test
    public void givenNoting_whenRequestArticleView_thenArticleView() throws Exception {
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());
        mvc.perform(get("/articles/"+articleId)).
                andExpect(status().isOk()).
                andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).
                andExpect(view().name("articles/detail")).
                andExpect(model().attributeExists("article")).
                andExpect(model().attributeExists("articleComments"));
        then(articleService).should().getArticle(articleId);
    }
    @DisplayName("View, GET 단일 게시판 검색어 포함 호출")
    @Test
    public void givenSearchKeyword_whenSearchArticleView_thenArticleView() throws Exception {
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        given(articleService.searchArticles(eq(searchType),eq(searchValue), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(),anyInt())).willReturn(List.of(0,1,2,3,4));

        mvc.perform(get("/articles").
                queryParam("searchType", searchType.toString()).
                queryParam("searchValue", searchValue)
                ).
                andExpect(status().isOk()).
                andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).
                andExpect(view().name("articles/index")).
                andExpect(model().attributeExists("articles")).
                andExpect(model().attributeExists("searchType"));
        then(articleService).should().searchArticles(eq(searchType),eq(searchValue), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());

    }
    @Disabled("그냥 다 안해")
    @DisplayName("View, GET 게시판 검색 정상 호출")
    @Test
    public void givenNoting_whenRequestArticleSearchView_thenArticleSearchView() throws Exception {
        mvc.perform(get("/articles/search")).
                andExpect(status().isOk()).
                andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).
                andExpect(view().name("article/search"));
    }

    @DisplayName("View, GET 게시판 해시태그 검색 정상 호출")
    @Test
    public void givenNoting_whenRequestArticleHashcodeSearchView_thenArticleHashtagSearchView() throws Exception {
        given(articleService.searchArticlesViaHashtag(eq(null),any(Pageable.class))).willReturn(Page.empty());

        mvc.perform(get("/articles/search-hashtag")).
                andExpect(status().isOk()).
                andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).
                andExpect(view().name("article/search-hashtag")).
                andExpect(model().attribute("articles",Page.empty())).
                andExpect(model().attributeExists("hashtag")).
                andExpect(model().attributeExists("paginationBarNumbers"));
        then(articleService).should().searchArticlesViaHashtag(eq(null),any(Pageable.class));
    }

    @DisplayName("View, GET 게시판 해시태그 검색 정상 반환")
    @Test
    public void givenHashtag_whenRequestArticleHashcodeSearchView_thenArticleHashtagSearchView() throws Exception {
        String searchValue = "#java";
        given(articleService.searchArticlesViaHashtag(eq(searchValue),any(Pageable.class))).willReturn(Page.empty());

        mvc.perform(get("/articles/search-hashtag").
                queryParam("searchValue",searchValue)

                ).
                andExpect(status().isOk()).
                andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).
                andExpect(view().name("article/search-hashtag")).
                andExpect(model().attribute("articles",Page.empty())).
                andExpect(model().attributeExists("hashtag")).
                andExpect(model().attributeExists("paginationBarNumbers"));
        then(articleService).should().searchArticlesViaHashtag(eq(searchValue),any(Pageable.class));
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto(){
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }
    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(1L,
                "uno",
                "pw",
                "uno@mail.com",
                "Uno",
                "memo",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }
}