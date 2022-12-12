package br.com.sankhya.dao;

import java.math.BigDecimal;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.model.Item;

public class ItemDAO {
	/**
	 * Esse método obtém os dados de uma instância de registro de peça do orçamento
	 * e os insere na instância da classe Item.
	 * 
	 * @param iteVO instância de registro de peça do orçamento;
	 * @return Instância da classe Item.
	 * @throws Exception
	 */
	public static Item read(DynamicVO iteVO) throws Exception {
		Item item = new Item();

		item.setQtdneg(iteVO.asBigDecimal("QTDNEG"));
		item.setVlrunit(coalesce(iteVO, "VLRUNIT"));

		return item;
	}
	
	/**
	 * Esse retorna o valor de um campo buscado da instância de um registro da peça
	 * ou, caso este seja nulo, retorna 0 (zero).
	 * 
	 * @param iteVO instância de um registro da peça.
	 * @param field campo a ser buscado.
	 * @return retorna o valor do campo buscado ou 0 (zero).
	 */
	private static BigDecimal coalesce(DynamicVO iteVO, String field) {
		return iteVO.asBigDecimal(field) == null ? BigDecimal.ZERO : iteVO.asBigDecimal(field);
	}
}
