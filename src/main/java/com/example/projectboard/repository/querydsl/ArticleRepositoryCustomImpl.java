package com.example.projectboard.repository.querydsl;

import com.example.projectboard.domain.Article;
import com.example.projectboard.domain.QArticle;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {
    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findByDistinctHashtag() {

        return from(QArticle.article)
                .distinct()
                .select(QArticle.article.hashtag)
                .where(QArticle.article.hashtag.isNotNull()).fetch();
    }
}
