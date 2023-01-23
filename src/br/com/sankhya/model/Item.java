package br.com.sankhya.model;

import java.math.BigDecimal;

import br.com.sankhya.dao.ItemDAO;
import br.com.sankhya.jape.vo.DynamicVO;

public class Item {
	private BigDecimal vlrunit;
	private BigDecimal sequencia;
	private BigDecimal nunota;
	private BigDecimal peso;
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
	public BigDecimal getSequencia() {
		return sequencia;
	}
	public void setSequencia(BigDecimal sequencia) {
		this.sequencia = sequencia;
	}

	public BigDecimal getNunota() {
		return nunota;
	}

	public void setNunota(BigDecimal nunota) {
		this.nunota = nunota;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public BigDecimal getQtdneg() {
		return qtdneg;
	}

	public void setQtdneg(BigDecimal qtdneg) {
		this.qtdneg = qtdneg;
	}
	
	
	
}
