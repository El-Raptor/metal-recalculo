package br.com.sankhya.dao;

import java.math.BigDecimal;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.model.Item;

public class ItemDAO {
	/**
	 * Esse m�todo obt�m os dados de uma inst�ncia de registro de pe�a do or�amento
	 * e os insere na inst�ncia da classe Item.
	 * 
	 * @param iteVO inst�ncia de registro de pe�a do or�amento;
	 * @return Inst�ncia da classe Item.
	 * @throws Exception
	 */
	public static Item read(DynamicVO iteVO) throws Exception {
		Item item = new Item();

		item.setQtdneg(iteVO.asBigDecimal("QTDNEG"));
		item.setVlrunit(coalesce(iteVO, "VLRUNIT"));

		return item;
	}
	
	/**
	 * Esse retorna o valor de um campo buscado da inst�ncia de um registro da pe�a
	 * ou, caso este seja nulo, retorna 0 (zero).
	 * 
	 * @param iteVO inst�ncia de um registro da pe�a.
	 * @param field campo a ser buscado.
	 * @return retorna o valor do campo buscado ou 0 (zero).
	 */
	private static BigDecimal coalesce(DynamicVO iteVO, String field) {
		return iteVO.asBigDecimal(field) == null ? BigDecimal.ZERO : iteVO.asBigDecimal(field);
	}
}
