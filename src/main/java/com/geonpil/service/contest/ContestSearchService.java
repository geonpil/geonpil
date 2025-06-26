package com.geonpil.service.contest;

import com.geonpil.domain.ContestPost;
import com.geonpil.dto.boardSearch.SearchResult;
import com.geonpil.elasticsearch.ContestDocument;
import com.geonpil.mapper.board.ContestMapper;
import com.geonpil.repository.search.ContestSearchRepository;
import com.geonpil.util.DdayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestSearchService {

    private final ContestSearchRepository contestSearchRepository;
    private final ContestMapper contestMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 개별 공모전을 검색 인덱스에 추가
     */
    public void index(ContestPost dto) {
        // 검색 인덱스에 공모전 정보 저장 로직
        log.info("공모전 색인: ID={}, 제목={}", dto.getPostId(), dto.getTitle());

        // ContestDocument 객체 생성
        ContestDocument doc = new ContestDocument();

        // 부모 클래스(BoardDocument) 필드 설정
        doc.setPostId(dto.getPostId());
        doc.setTitle(dto.getTitle());
        doc.setContent(dto.getContent());
        doc.setBoardCode(dto.getBoardCode());
        doc.setCategoryId(dto.getCategoryId());

        // ContestDocument 필드 설정
        doc.setSubtitle(dto.getSubtitle());
        doc.setHostName(dto.getHostName()); // 필드명 주의: host_name vs hostName
        doc.setTarget(dto.getTarget());

        // 실제 ElasticSearch 또는 다른 검색 엔진에 저장하는 코드 구현
        contestSearchRepository.save(doc);

        log.info("공모전 색인 완료: {}", doc);
    }

    /**
     * 모든 공모전을 데이터베이스에서 가져와 검색 인덱스에 추가
     */
    public void indexAllFromDatabase() {
        log.info("모든 공모전 색인 시작");

        try {
            // DB에서 모든 공모전을 가져옴
            List<ContestPost> allContests = contestMapper.findAllContestForIndexing();
            log.info("색인할 공모전 ���: {}", allContests.size());

            List<ContestDocument> docs = allContests.stream()
                    .map(contest -> {
                        try {
                            ContestDocument doc = new ContestDocument();

                            // 부모 클래스(BoardDocument) 필드 설정
                            doc.setPostId(contest.getPostId());
                            doc.setTitle(contest.getTitle());
                            doc.setContent(contest.getContent());
                            doc.setBoardCode(contest.getBoardCode());

                            // 카테고리 ID 설정 - categoryIdsRaw가 문자열로 들어있으므로 처리 필요
                            if (contest.getCategoryIdsRaw() != null && !contest.getCategoryIdsRaw().isEmpty()) {
                                // 콤마로 분리된 카테고리 ID 문자열을 처리
                                String[] categoryIds = contest.getCategoryIdsRaw().split(",");
                                if (categoryIds.length > 0) {
                                    try {
                                        doc.setCategoryId(Long.parseLong(categoryIds[0]));
                                    } catch (NumberFormatException e) {
                                        log.warn("카테고리 ID 변환 오류: {}", e.getMessage());
                                    }
                                }
                            }

                            // ContestDocument 필드 설정
                            doc.setSubtitle(contest.getSubtitle());
                            doc.setHostName(contest.getHostName());
                            doc.setTarget(contest.getTarget());

                            // 날짜 설정 (null 체크 포함)
                            doc.setStartDate(contest.getStartDate());
                            doc.setEndDate(contest.getEndDate());

                            return doc;
                        } catch (Exception e) {
                            log.error("공모전 문서 변환 중 오류: postId={}, error={}",
                                    contest.getPostId(), e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(doc -> doc != null)
                    .collect(Collectors.toList());

            log.info("Elasticsearch에 저장할 문서 수: {}", docs.size());

            if (!docs.isEmpty()) {
                // 각 문서를 개별적으로 저장 (일괄 저장 대신)
                for (ContestDocument doc : docs) {
                    try {
                        contestSearchRepository.save(doc);
                        log.debug("문서 저장 성공: id={}", doc.getPostId());
                    } catch (Exception e) {
                        log.error("문서 저장 실패: id={}, 오류={}", doc.getPostId(), e.getMessage(), e);
                    }
                }
                log.info("모든 공모전 색인 완료: {} 건", docs.size());
            } else {
                log.warn("색인할 공모전 문서가 없습니다.");
            }
        } catch (Exception e) {
            log.error("공모전 색인 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 키워드로 공모전 검색
     * @param keyword 검색 키워드
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param boardCode 게시판 코드
     * @param categoryIds 카테고리 ID 목록 (쉼표로 구분된 문자열)
     * @param isClosedIncluded 마감된 공모전 포함 여부
     * @param sort 정렬 기준
     * @param searchType 검색 유형
     * @return 검색 결과
     */
    public SearchResult<ContestPost> searchByKeyword(
            String keyword, int page, int size, Integer boardCode,
            String categoryIds, boolean isClosedIncluded, String sort, String searchType) {

        log.info("공모전 검색: 키워드={}, 페이지={}, ��시판={}, 카테고리={}, 마감포함={}, 정렬={}, 검색유형={}",
                keyword, page, boardCode, categoryIds, isClosedIncluded, sort, searchType);

        try {
            // 항상 필드를 지정하여 Criteria 생성 (boardCode로 시작)
            Criteria criteria = new Criteria("boardCode").is(boardCode);

            // 키워드가 있는 경우에만 키워드 검색 조건 추가
            if (StringUtils.hasText(keyword)) {
                // 검색 유형에 따른 키워드 검색 조건 설정
                Criteria keywordCriteria;

                switch (searchType) {
                    case "all": // 전체
                        // OR 조건으로 모든 필드 검색
                        keywordCriteria = new Criteria("title").contains(keyword)
                                .or(new Criteria("subtitle").contains(keyword))
                                .or(new Criteria("content").contains(keyword))
                                .or(new Criteria("hostName").contains(keyword));
                        break;

                    case "titleContent": // 제목+내용
                        keywordCriteria = new Criteria("title").contains(keyword)
                                .or(new Criteria("content").contains(keyword));
                        break;

                    case "title": // 제목
                        keywordCriteria = new Criteria("title").contains(keyword);
                        break;

                    case "content": // 내용
                        keywordCriteria = new Criteria("content").contains(keyword);
                        break;

                    case "host": // 주최자
                        keywordCriteria = new Criteria("hostName").contains(keyword);
                        break;

                    default: // 기본값은 전체 검색
                        keywordCriteria = new Criteria("title").contains(keyword)
                                .or(new Criteria("subtitle").contains(keyword))
                                .or(new Criteria("content").contains(keyword))
                                .or(new Criteria("hostName").contains(keyword));
                        break;
                }

                // 검색 조건 결합 (기존 조건에 AND로 키워드 조건 추가)
                criteria = criteria.and(keywordCriteria);
                log.debug("키워드 검색 조건 추가: {}", keyword);
            } else {
                log.info("키워드 없이 필터 조건만으로 검색합니다.");
            }

            // 카테고리 필터 적용
            if (StringUtils.hasText(categoryIds) && !categoryIds.equals("0")) {
                String[] ids = categoryIds.split(",");
                if (ids.length > 0) {
                    List<Object> validIds = new java.util.ArrayList<>();
                    for (String id : ids) {
                        try {
                            validIds.add(Long.parseLong(id.trim()));
                        } catch (NumberFormatException e) {
                            log.warn("유효하지 않은 카테고리 ID: {}", id);
                        }
                    }

                    // 유효한 카테고리 ID가 있는 경우에만 필터 적용
                    if (!validIds.isEmpty()) {
                        criteria = criteria.and(new Criteria("categoryId").in(validIds.toArray()));
                        log.debug("카테고리 필터 적용: {}", validIds);
                    }
                }
            }

            // 마감 여부 필터 적용 (isClosedIncluded가 false면 마감되지 않은 공모전만)
            if (!isClosedIncluded) {
                LocalDate today = LocalDate.now();
                // 마감일이 오늘 이후인 공모전만 필터링
                criteria = criteria.and(new Criteria("endDate").greaterThanEqual(today));
                log.debug("마감되지 않은 공모전만 필터링 적용 (날짜: {})", today);
            }

            // 쿼리 생성
            CriteriaQuery query = new CriteriaQuery(criteria);

            // 페이지네이션 설정
            query.setPageable(PageRequest.of(page - 1, size));

            // 정렬 조건 적용
            if ("deadline".equals(sort)) {
                // 마감일 순 (마감일이 가까운 순)
                query.addSort(Sort.by(Sort.Order.asc("endDate").with(Sort.NullHandling.NULLS_LAST)));
                log.debug("정렬: 마감일 순");
            } else {
                // 최신순 (기본)
                query.addSort(Sort.by(Sort.Direction.DESC, "postId"));
                log.debug("정렬: 최신순");
            }

            // 디버깅용 로깅 추가
            log.debug("검색 쿼리: {}", query.getCriteria().toString());

            // 검색 실행
            SearchHits<ContestDocument> searchHits = elasticsearchOperations.search(query, ContestDocument.class);
            log.debug("검색 결과 수: {}", searchHits.getTotalHits());

            // 검색 결과에서 문서 ID 추출
            List<Long> postIds = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(ContestDocument::getPostId)
                    .collect(Collectors.toList());

            // 결과가 없는 경우
            if (postIds.isEmpty()) {
                log.debug("검색 결과 없음");
                return new SearchResult<>(Collections.emptyList(), page, size, 0, 0);
            }

            // ID로 DB에서 실제 공모전 정보 조회
            List<ContestPost> contests = contestMapper.findContestsByPostIds(postIds);
            log.debug("DB에서 조회된 공모전 수: {}", contests != null ? contests.size() : 0);

            //dDay 계산
            if(contests != null && !contests.isEmpty()) {
                for(ContestPost post : contests) {
                    post.setDDay(DdayUtils.calculateDDay(post.getEndDate()));
                }
            }

            // 검색 결과가 없거나 DB 조회 결과가 없는 경우
            if (contests == null || contests.isEmpty()) {
                log.debug("DB 조회 결과 없음");
                return new SearchResult<>(Collections.emptyList(), page, size, 0, 0);
            }

            // 검색 결과 정렬 순서에 맞게 재정렬
            List<ContestPost> sortedContests = postIds.stream()
                    .map(id -> contests.stream()
                            .filter(p -> p.getPostId().equals(id))
                            .findFirst()
                            .orElse(null))
                    .filter(p -> p != null)
                    .collect(Collectors.toList());

            // 검색 결과 반환
            long totalHits = searchHits.getTotalHits();
            int totalPages = calculateTotalPages((int) totalHits, size);
            log.debug("최종 검색 결과: totalHits={}, totalPages={}, 결과 개수={}",
                    totalHits, totalPages, sortedContests.size());

            return new SearchResult<>(
                    sortedContests,       // content: 검색 결과 목록
                    page,                 // page: 현재 페이지 번호
                    size,                 // size: 페이지당 아이템 수
                    totalHits,            // totalHits: 총 검색 결과 수
                    totalPages            // totalPages: 총 페이지 수
            );
        } catch (Exception e) {
            log.error("공모전 검색 중 오류 발생: {}", e.getMessage(), e);
            return new SearchResult<>(Collections.emptyList(), page, size, 0, 0);
        }
    }

    // 총 페이지 수 계산
    private int calculateTotalPages(int totalHits, int size) {
        return totalHits == 0 ? 0 : (int) Math.ceil((double) totalHits / size);
    }
}
