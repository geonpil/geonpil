package com.geonpil.service.board;


import com.geonpil.domain.board.BoardType;
import com.geonpil.mapper.board.BoardTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardTypeService {

    private final BoardTypeMapper boardTypeMapper;


    public List<BoardType> getAllBoardTypes() {
        return boardTypeMapper.getAllBoardTypes();
    }

}