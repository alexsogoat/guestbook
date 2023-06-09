package kr.ac.kopo.guestbook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import kr.ac.kopo.guestbook.entity.QGuestbook;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import kr.ac.kopo.guestbook.dto.GuestbookDTO;
import kr.ac.kopo.guestbook.dto.PageRequestDTO;
import kr.ac.kopo.guestbook.dto.PageResultDTO;
import kr.ac.kopo.guestbook.entity.Guestbook;
import kr.ac.kopo.guestbook.repository.GuestbookRepository;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService{
    private final GuestbookRepository repository;
    @Override
    public Long register(GuestbookDTO dto){

        log.info("DTO--------------");
        log.info(dto);

        Guestbook entity = dtoToEntity(dto);

        log.info(entity);

        repository.save(entity);

        return entity.getGno();
    }

    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO){
        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());

        BooleanBuilder booleanBuilder = getSearch(requestDTO); // 검색 조건 처리

        Page<Guestbook> result = repository.findAll(booleanBuilder, pageable); // Querydsl 사용

        Function<Guestbook, GuestbookDTO> fn = (entity ->
                entityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public GuestbookDTO read(Long gno){
        Optional<Guestbook> result = repository.findById(gno);
        return result.isPresent()? entityToDto(result.get()):null;
    }
    @Override
    public void remove(Long gno){
        repository.deleteById(gno);
    }
    @Override
    public void modify(GuestbookDTO dto){
        // 업데이트 하는 항목은 '제목', '내용'
        Optional<Guestbook> result = repository.findById(dto.getGno());
        if(result.isPresent()){
            Guestbook entity = result.get();
            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());
            repository.save(entity);
        }
    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO){
        String type = requestDTO.getType();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = requestDTO.getKeyword();
        BooleanExpression expression = qGuestbook.gno.gt(0L);
        booleanBuilder.and(expression);
        if(type==null || type.trim().length()==0){
            return booleanBuilder;
        }
        BooleanBuilder conditionBuilder = new BooleanBuilder();
        if(type.contains("t")){
            conditionBuilder.or(qGuestbook.title.contains(keyword));
        }if(type.contains("c")){
            conditionBuilder.or(qGuestbook.title.contains(keyword));
        }if(type.contains("w")){
            conditionBuilder.or(qGuestbook.title.contains(keyword));
        }

        // 모든 조건 통합
        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }
}
