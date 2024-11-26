package com.ezen.boot_JPA.repository;

import com.ezen.boot_JPA.entity.Board;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.ezen.boot_JPA.entity.QBoard.board;

public class BoardCustomRepositoryImpl implements BoardCustomRepository{

    private final JPAQueryFactory queryFactory;

    public BoardCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 실제 구현
    @Override
    public Page<Board> searchBoard(String type, String keyword, Pageable pageable) {
        // 조건이 많을 경우
        // select * from board where
        // isDel = 'n' and title like '% aaa %';
        // BooleanExpression condition = board.isDel.eq('N')
        // condition = condition.and(board.title.containsIgnoreCase(keyword));
        BooleanExpression condition = null;

        // 동적 검색 조건 추가
        if(type != null && keyword != null){
            // 타입이 여러 개일 경우 추가되는 작업
            String[] typearr = type.split("");
            BooleanExpression dynamicCondition = null;
            for (String t : typearr){
                switch (type) {
                    case "t":
                        condition = (condition == null) ?
                                board.title.containsIgnoreCase(keyword)
                                : condition.or(board.title.containsIgnoreCase(keyword));
                        break;
                    case "w":
                        condition = (condition == null) ?
                                board.writer.containsIgnoreCase(keyword)
                                : condition.or(board.writer.containsIgnoreCase(keyword));
                        break;
                    case "c":
                        condition = (condition == null) ?
                                board.content.containsIgnoreCase(keyword)
                                : condition.or(board.content.containsIgnoreCase(keyword));
                        break;
                }
                if(dynamicCondition != null){
//                  condition = condition.and(dynamicCondition);
                    condition = dynamicCondition;
                }
            }
//            switch (type){
//                case "t" :
//                    condition = board.title.containsIgnoreCase(keyword);
//                    break;
//                case "w" :
//                    condition = board.writer.containsIgnoreCase(keyword);
//                    break;
//                case "c" :
//                    condition = board.content.containsIgnoreCase(keyword);
//                    break;
//                case "tw":
//                    condition = (board.content.containsIgnoreCase(keyword)).or(board.writer.containsIgnoreCase(keyword));
//                    break;
//            }
        }

        // 쿼리 작성 및 페이징 적용
        List<Board> result = queryFactory
                .selectFrom(board)
                .where(condition)
                .orderBy(board.bno.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        // 검색된 데이터의 전체 개수 조회
        int total = queryFactory
                .selectFrom(board)
                .where(condition)
                .fetch().size();
        // .fetchCount(); => .fetch().size();
        return new PageImpl<>(result, pageable, total);
    }
}
