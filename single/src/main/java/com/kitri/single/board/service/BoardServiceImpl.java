package com.kitri.single.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kitri.single.board.dao.BoardDao;
import com.kitri.single.board.model.BoardDto;
import com.kitri.single.common.dao.CommonDao;
import com.kitri.single.hashtag.dao.HashtagDao;
import com.kitri.single.hashtag.model.HashtagDto;

@Service
public class BoardServiceImpl implements BoardService {
	
	//로그
	//private static final Logger logger = LoggerFactory.getLogger(BoardServiceImpl.class);
		
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	@Transactional
	public int writeArticle(BoardDto boardDto) {
		
		// boardNum로 글번호 증가( 시퀀스 글번호를 증가시키고 그 글번호를 가져옴 )
		// select 값을 저절로 반환 됨.
		int boardNum = sqlSession.getMapper(CommonDao.class).getNextSeq();
		
		// 글번호 증가한것을 dto에 추가해주고 글 작성
		boardDto.setBoardNum(boardNum);
		int cnt = sqlSession.getMapper(BoardDao.class).writeArticle(boardDto);
		
		// 해쉬태그 DB Insert
		int cnthashtag = 0;
		List<String> hashtagList = boardDto.getHashtagList();
		for (int i = 0; i < hashtagList.size(); i++) {
			HashtagDto hashtagDto = new HashtagDto();
			hashtagDto.setHashtagTypeNum(1);
			hashtagDto.setHashtagContent(hashtagList.get(i));
			hashtagDto.setBoardNum(boardDto.getBoardNum());
			sqlSession.getMapper(HashtagDao.class).insertHashtag(hashtagDto);
		}
		
		return cnt != 0? boardDto.getBoardNum() : 0;
		
	}
	
	@Override
//	@Transactional //알아서 트랜잭션해줌 root에서 관리.
	public BoardDto viewArticle(int boardNum) {
//		sqlSession.getMapper(CommonDao.class).updateHit(seq); // mapper_common 마이바티스 설정해주기
		
		// 글번호의 글들을 dto에 담음
		BoardDto boardDto = sqlSession.getMapper(BoardDao.class).viewArticle(boardNum);
		
		// map에 type번호랑 글번호 put하기
		Map<String, Integer> parameter = new HashMap<String, Integer>();
		parameter.put("tagType", 1);
		parameter.put("boardNum", boardNum);
		
		// map을 dao로 보내주고 해쉬태그 리스트를 가져옴.
		List<String> hashtagList = sqlSession.getMapper(HashtagDao.class).getHashtagList(parameter);
		boardDto.setHashtagList(hashtagList);
		
//		// 엔터키 적용해서 보내기.
//		reboardDto.setContent(reboardDto.getContent().replace("\n", "<br>"));
		return boardDto;
	}

	
	
	
	

}



















