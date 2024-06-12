package banzzac.dto;

import org.springframework.stereotype.Component;


import lombok.Data;
@Data
@Component
public class PageDTO {
	private Integer listCnt = 20; //기본 갯수 30개
	private Integer currentPage = 1;
	//전체 페이지 수
	private Integer block = 10;
	private Integer currentBlock = 1;
	private Integer searchNo = 0;
	private Integer totalPage;
	private Integer minPage = 1;
	private Integer maxPage;
	private boolean isNextBtn;
	private Integer prevBlock;
	private Integer nextBlock;
	
	
	public void setCurrentPage(Integer currentPage) {
		if(currentPage != null) {			
			this.currentPage = currentPage;
			if(this.currentPage <= 0) {
				this.currentPage = 1;
			}
			this.searchNo = (this.currentPage-1) * listCnt;
		}
	}
	
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage % listCnt == 0 ?
						totalPage/listCnt :
						totalPage /listCnt + 1;
		this.minPage = (this.currentPage - 1) / block * block + 1;
		this.maxPage = this.minPage + block -1;
		this.isNextBtn = true;
		
		this.prevBlock = this.minPage - 1;
		this.nextBlock = this.maxPage + 1;
		
		if(this.maxPage > this.totalPage) {
			this.maxPage = this.totalPage;
			this.isNextBtn = false;
		}
		if(this.currentPage > this.maxPage) {
			this.currentPage = this.maxPage;
			this.minPage = (this.currentPage - 1) / block * block + 1;
			this.searchNo = (this.currentPage-1) * listCnt;
		}
		
		if(this.totalPage < nextBlock) {
			this.nextBlock = this.totalPage;
		}
		if(this.currentPage > this.maxPage) {
			this.currentPage = this.maxPage;
		}
		
	}
	
}
