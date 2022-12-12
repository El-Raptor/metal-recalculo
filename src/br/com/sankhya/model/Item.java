package br.com.sankhya.model;

import java.math.BigDecimal;

import br.com.sankhya.dao.ItemDAO;
import br.com.sankhya.jape.vo.DynamicVO;

public class Item {
	private BigDecimal vlrunit;
	private BigDecimal qtdneg;
	
	public static Item builder(DynamicVO iteVO) throws Exception {
		return ItemDAO.read(iteVO);
	}
	
	public BigDecimal getVlrunit() {
		return vlrunit;
	}
	public void setVlrunit(BigDecimal vlrunit) {
		this.vlrunit = vlrunit;
	}
	public BigDecimal getQtdneg() {
		return qtdneg;
	}
	public void setQtdneg(BigDecimal qtdNeg) {
		this.qtdneg = qtdNeg;
	}
	
	
}
